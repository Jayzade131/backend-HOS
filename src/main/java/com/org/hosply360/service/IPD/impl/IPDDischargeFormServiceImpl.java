package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.constant.Enums.IpdStatus;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IPDDischargeForm;
import com.org.hosply360.dao.IPD.IPDSurgery;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.Template;
import com.org.hosply360.dto.IPDDTO.DischargeFormPdfResponseDTO;
import com.org.hosply360.dto.IPDDTO.IPDDischargeFormReqDTO;
import com.org.hosply360.dto.IPDDTO.SurgeryInfoDTO;
import com.org.hosply360.dto.utils.PdfHeaderFooterDTO;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.repository.IPD.IPDAdmissionRepository;
import com.org.hosply360.repository.IPD.IPDDischargeFormRepository;
import com.org.hosply360.repository.IPD.IPDSurgeryFormRepository;
import com.org.hosply360.repository.frontDeskRepo.DoctorMasterRepository;
import com.org.hosply360.service.IPD.IPDDischargeFormService;
import com.org.hosply360.util.Others.AgeUtil;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.Others.TimeUtil;
import com.org.hosply360.util.mapper.HeaderFooterMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class IPDDischargeFormServiceImpl implements IPDDischargeFormService {

    private static final Logger logger = LoggerFactory.getLogger(IPDDischargeFormServiceImpl.class);

    private final EntityFetcherUtil entityFetcherUtil;
    private final IPDDischargeFormRepository ipdDischargeFormRepository;
    private final IPDAdmissionRepository ipdAdmissionRepository;
    private final DoctorMasterRepository doctorRepository;
    private final IPDSurgeryFormRepository ipdSurgeryFormRepository;


    private Doctor fetchDoctorIfPresent(String doctorId) {
        return (doctorId != null && !doctorId.isEmpty())
                ? entityFetcherUtil.getDoctorOrThrow(doctorId)
                : null;
    }

    private String getDoctorId(Doctor doctor) {
        return (doctor != null) ? doctor.getId() : null;
    }

    private String getConsultantName(String consultantId) {
        if (consultantId == null || consultantId.isEmpty()) return "-";
        return doctorRepository.findById(consultantId)
                .map(doc -> "Dr. " + doc.getFirstName())
                .orElse("-");
    }

    private String getPatientFullName(Patient patient) {
        var p = patient.getPatientPersonalInformation();
        return p.getFirstName() + " " + p.getLastName();
    }


    @Override
    public String createDischargeFrom(IPDDischargeFormReqDTO reqDTO) {

        ValidatorHelper.validateObject(reqDTO);

        IPDAdmission ipdAdmission = entityFetcherUtil.getIPDAdmissionOrThrow(reqDTO.getIpdAdmissionId());

        if (IpdStatus.DISCHARGED.equals(ipdAdmission.getIpdStatus())) {
            throw new IPDException(ErrorConstant.IPD_DISCHARGE_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        Organization organization = entityFetcherUtil.getOrganizationOrThrow(reqDTO.getOrganizationId());
        Template template = entityFetcherUtil.getTemplateOrThrow(reqDTO.getTemplateId());

        Doctor secondDoctor = fetchDoctorIfPresent(reqDTO.getSecondaryConsultant());
        Doctor thirdDoctor = fetchDoctorIfPresent(reqDTO.getThirdConsultant());

        IPDDischargeForm dischargeForm = IPDDischargeForm.builder()
                .organizationId(organization.getId())
                .ipdAdmissionId(ipdAdmission.getId())
                .primaryConsultant(ipdAdmission.getPrimaryConsultant().getId())
                .secondaryConsultant(getDoctorId(secondDoctor))
                .thirdConsultant(getDoctorId(thirdDoctor))
                .templateId(template.getId())
                .remarks(reqDTO.getRemarks())
                .dateTime(LocalDateTime.now())
                .type(reqDTO.getType())
                .dischargeSummary(reqDTO.getDischargeSummary())
                .defunct(false)
                .build();

        IPDDischargeForm saved = ipdDischargeFormRepository.save(dischargeForm);
        ipdAdmission.setIpdStatus(IpdStatus.DISCHARGED);
        ipdAdmission.setDischargeDateTime(LocalDateTime.now());
        ipdAdmission.setRemarks(reqDTO.getRemarks());
        ipdAdmissionRepository.save(ipdAdmission);
        return saved.getId();
    }


    @Override
    public DischargeFormPdfResponseDTO getDischargeFormPdf(String dischargeFormId) {
        IPDDischargeForm dischargeForm = ipdDischargeFormRepository.findById(dischargeFormId)
                .orElseThrow(() -> new IPDException(ErrorConstant.IPD_DISCHARGE_NOT_FOUND, HttpStatus.NOT_FOUND));

        IPDAdmission admission = ipdAdmissionRepository.findById(dischargeForm.getIpdAdmissionId())
                .orElseThrow(() -> new IPDException(ErrorConstant.IPD_ADMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        Patient patient = admission.getPatient();
        var personal = patient.getPatientPersonalInformation();

        List<IPDSurgery> surgeries = ipdSurgeryFormRepository.findByOrgIdAndIpdAdmissionIdAndDefunctFalse (
                admission.getOrgId().getId(), admission.getId());

        List<SurgeryInfoDTO> surgeryInfoDTOList = surgeries.stream()
                .map(surgery -> {
                    SurgeryInfoDTO dto = new SurgeryInfoDTO();
                    dto.setSurgeryType(surgery.getTypeOfSurgery());
                    dto.setSurgeonName(surgery.getSurgeons().getFirst().getName());
                    dto.setSurgeryDate(surgery.getDate().toString());
                    dto.setSurgeryTime(surgery.getStartTime());
                    return dto;
                })
                .toList();


        Organization organization = entityFetcherUtil.getOrganizationOrThrow(dischargeForm.getOrganizationId());
        PdfHeaderFooterDTO headerFooter = HeaderFooterMapperUtil.buildHeaderFooter(organization);

        return DischargeFormPdfResponseDTO.builder()
                .headerFooter(headerFooter)
                .ipdNo(admission.getIpdNo())
                .admissionDate(TimeUtil.formatDate(admission.getAdmitDateTime()))
                .dischargeDate(TimeUtil.formatDate(dischargeForm.getDateTime()))
                .patientName(getPatientFullName(patient))
                .patientAttendantName(personal.getMiddleName())
                .ageGender(AgeUtil.getAge(personal.getDateOfBirth()) + " / " + personal.getGender())
                .address(patient.getPatientContactInformation().getAddress().getCityName() + " " + patient.getPatientContactInformation().getAddress().getStateName())
                .primaryConsultant(getConsultantName(dischargeForm.getPrimaryConsultant()))
                .secondaryConsultant(getConsultantName(dischargeForm.getSecondaryConsultant()))
                .status(dischargeForm.getType() != null ? dischargeForm.getType().toString() : "")
                .surgeries(surgeryInfoDTOList)
                .build();
    }
}
