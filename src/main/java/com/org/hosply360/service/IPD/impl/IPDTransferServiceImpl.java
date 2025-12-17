package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.constant.Enums.AdmitStatus;
import com.org.hosply360.constant.Enums.IpdStatus;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IpdTransfer;
import com.org.hosply360.dao.auth.Users;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.WardBedMaster;
import com.org.hosply360.dao.globalMaster.WardMaster;
import com.org.hosply360.dto.IPDDTO.IpdTransferDTO;
import com.org.hosply360.dto.IPDDTO.IpdTransferHistoryDto;
import com.org.hosply360.dto.IPDDTO.TransferReceiptDto;
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.repository.IPD.IPDAdmissionRepository;
import com.org.hosply360.repository.IPD.IPDTransferRepo;
import com.org.hosply360.repository.authRepo.UsersRepository;
import com.org.hosply360.repository.globalMasterRepo.WardBedMasterRepository;
import com.org.hosply360.service.IPD.IPDTransferService;
import com.org.hosply360.util.Others.AgeUtil;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.PDFGenUtil.TransferReceiptPdfGenerator;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IPDTransferServiceImpl implements IPDTransferService {

    private final EntityFetcherUtil entityFetcherUtil;
    private final IPDTransferRepo ipdTransferRepo;
    private final IPDAdmissionRepository ipdAdmissionRepository;
    private final WardBedMasterRepository wardBedMasterRepo;
    private final UsersRepository userRepository;
    private final TransferReceiptPdfGenerator transferReceiptPdfGenerator;

    private static final Logger logger = LoggerFactory.getLogger(IPDTransferServiceImpl.class);


    private IpdTransferHistoryDto mapToDtoWithUserCache(IpdTransfer transfer, Map<String, String> userCache) {
        String userId = transfer.getCreatedBy();
        String userName = userCache.computeIfAbsent(userId, this::fetchUserNameById);
        return IpdTransferHistoryDto.builder()
                .transferId(transfer.getId())
                .dateTime(formatDate(transfer.getDateTime()))
                .fromWardName(getSafeName(transfer.getCurrentWard()))
                .fromBedName(getSafeBedNumber(transfer.getCurrentBed()))
                .toWardName(getSafeName(transfer.getTransferWard()))
                .toBedName(getSafeBedNumber(transfer.getTransferBed()))
                .remark(transfer.getRemark())
                .createdByName(userName)
                .build();
    }

    @Override
    @Transactional
    public String createIpdTransfer(IpdTransferDTO requestDTO) {
        logger.info("Creating IPD transfer for Admission ID: {}", requestDTO.getIpdAdmissionId());
        validateRequest(requestDTO);
        IPDAdmission admission = entityFetcherUtil.getIPDAdmissionOrThrow(requestDTO.getIpdAdmissionId());
        WardMaster currentWard = entityFetcherUtil.getWardMasterOrThrow(requestDTO.getCurrentWardId());
        WardMaster transferWard = entityFetcherUtil.getWardMasterOrThrow(requestDTO.getTransferWardId());
        WardBedMaster currentBed = entityFetcherUtil.getWardBedMasterOrThrow(requestDTO.getCurrentBedId());
        WardBedMaster transferBed = entityFetcherUtil.getWardBedMasterOrThrow(requestDTO.getTransferBedId());

        EnumSet<IpdStatus> BLOCKED_STATUSES = EnumSet.of(IpdStatus.DISCHARGED, IpdStatus.CANCELLED, IpdStatus.EXPIRED);
        if (BLOCKED_STATUSES.contains(admission.getIpdStatus())) {
            throw new IPDException(ErrorConstant.TRANSFER_NOT_ALLOWED, HttpStatus.BAD_REQUEST);
        }

        if (AdmitStatus.AVAILABLE.equals(transferBed.getStatus())) {
            IpdTransfer transfer = ObjectMapperUtil.copyObject(requestDTO, IpdTransfer.class);
            transfer.setIpdAdmission(admission);
            transfer.setCurrentWard(currentWard);
            transfer.setTransferWard(transferWard);
            transfer.setCurrentBed(currentBed);
            transfer.setTransferBed(transferBed);
            transfer.setDateTime(Optional.ofNullable(requestDTO.getDateTime()).orElse(LocalDateTime.now()));
            transfer.setDefunct(false);

            IpdTransfer savedTransfer = ipdTransferRepo.save(transfer);
            logger.info("IPD transfer created successfully with ID: {}", savedTransfer.getId());

            updateBedStatuses(currentBed, transferBed);
            updateIpdAdmission(admission, transferWard, transferBed);
            return savedTransfer.getId();
        } else {
            logger.warn("Transfer not allowed: Transfer bed {} in ward {} is not available",
                    transferBed.getId(), transferWard.getWardName());
            throw new IPDException(
                    String.format(ErrorConstant.BED_NOT_AVAILABLE, transferBed.getBedNo(), transferWard.getWardName()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void updateBedStatuses(WardBedMaster currentBed, WardBedMaster transferBed) {
        currentBed.setStatus(AdmitStatus.AVAILABLE);
        transferBed.setStatus(AdmitStatus.BOOKED);
        wardBedMasterRepo.saveAll(Arrays.asList(currentBed, transferBed));
        logger.debug("Updated bed statuses: currentBed={} → AVAILABLE, transferBed={} → BOOKED",
                currentBed.getBedNo(), transferBed.getBedNo());
    }

    private void updateIpdAdmission(IPDAdmission admission, WardMaster newWard, WardBedMaster newBed) {
        logger.info("Updating IPD Admission ID: {} with new Ward: {} and Bed: {}",
                admission.getId(), newWard.getWardName(), newBed.getBedNo());
        try {
            admission.setWardMaster(newWard);
            admission.setWardName(newWard.getWardName());
            admission.setBedMaster(newBed);
            admission.setBedNo(newBed.getBedNo());
            ipdAdmissionRepository.save(admission);

            logger.info("IPD Admission updated successfully after transfer for Admission ID: {}", admission.getId());
        } catch (Exception e) {
            logger.error("Error updating IPD Admission after transfer: {}", e.getMessage(), e);
            throw new IPDException(ErrorConstant.IPD_ADMISSION_UPDATE_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateRequest(IpdTransferDTO requestDTO) {
        if (ObjectUtils.isEmpty(requestDTO.getIpdAdmissionId()) ||
                ObjectUtils.isEmpty(requestDTO.getCurrentWardId()) ||
                ObjectUtils.isEmpty(requestDTO.getTransferWardId()) ||
                ObjectUtils.isEmpty(requestDTO.getCurrentBedId()) ||
                ObjectUtils.isEmpty(requestDTO.getTransferBedId())) {
            throw new GlobalMasterException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<IpdTransferHistoryDto> getTransfersByIpdAdmission(String admissionId) {
        List<IpdTransfer> transfers = ipdTransferRepo.findByIpdAdmission_IdAndDefunctFalse(admissionId);

        Map<String, String> userNameCache = new HashMap<>();
        return transfers.stream()
                .map(transfer -> mapToDtoWithUserCache(transfer, userNameCache))
                .toList();
    }

    private String fetchUserNameById(String userId) {
        return userRepository.findById(userId)
                .map(Users::getName)
                .orElse("Unknown User");
    }

    private String getSafeName(WardMaster ward) {
        return ward != null ? ward.getWardName() : "-";
    }

    private String getSafeBedNumber(WardBedMaster bed) {
        return bed != null ? bed.getBedNo() : "-";
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toString() : "-";
    }


    @Override
    public PdfResponseDTO generateTransferReceiptPdf(String transferId) {
        IpdTransfer transfer = ipdTransferRepo.findById(transferId)
                .orElseThrow(() -> new IPDException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        IPDAdmission admission = transfer.getIpdAdmission();
        Patient patient = admission.getPatient();

        String createdByName = userRepository.findById(transfer.getCreatedBy())
                .map(Users::getName)
                .orElse("Unknown");

        String age = AgeUtil.getAge(patient.getPatientPersonalInformation());

        TransferReceiptDto dto = TransferReceiptDto.builder()
                .patientName(patient.getPatientPersonalInformation().getFirstName() + " " + patient.getPatientPersonalInformation().getLastName())
                .gender(patient.getPatientPersonalInformation().getGender())
                .age(age)
                .mobileNumber(patient.getPatientContactInformation().getPrimaryPhone())
                .admissionNumber(admission.getIpdNo())
                .consultant(admission.getPrimaryConsultant().getFirstName())
                .admissionDate(formatDate(admission.getAdmitDateTime()))
                .transferDateTime(formatDate(transfer.getDateTime()))
                .remark(transfer.getRemark())
                .fromWard(getSafeName(transfer.getCurrentWard()))
                .fromBed(getSafeBedNumber(transfer.getCurrentBed()))
                .toWard(getSafeName(transfer.getTransferWard()))
                .toBed(getSafeBedNumber(transfer.getTransferBed()))
                .createdBy(createdByName)
                .build();

        byte[] pdfBytes = transferReceiptPdfGenerator.generateTransferReceiptPDF(dto);
        String fileName = "IPD_Transfer_Receipt_" + admission.getIpdNo() + "_" + LocalDate.now() + ".pdf";
        return new PdfResponseDTO(pdfBytes, fileName);
    }

}


