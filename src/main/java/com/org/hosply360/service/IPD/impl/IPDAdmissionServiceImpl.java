package com.org.hosply360.service.IPD.impl;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.Enums.AdmitStatus;
import com.org.hosply360.constant.Enums.IpdStatus;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.CorporateDetails;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IPDFinancialSummary;
import com.org.hosply360.dao.IPD.InsuranceDetails;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.Address;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.PatientCategory;
import com.org.hosply360.dao.globalMaster.Speciality;
import com.org.hosply360.dao.globalMaster.WardBedMaster;
import com.org.hosply360.dao.globalMaster.WardMaster;
import com.org.hosply360.dto.IPDDTO.AdmissionInfoDTO;
import com.org.hosply360.dto.IPDDTO.AdmissionRecordResponseDTO;
import com.org.hosply360.dto.IPDDTO.BarcodeResDTO;
import com.org.hosply360.dto.IPDDTO.BedResponseDTO;
import com.org.hosply360.dto.IPDDTO.ConsultantInfoDTO;
import com.org.hosply360.dto.IPDDTO.IPDAdmissionDTO;
import com.org.hosply360.dto.IPDDTO.IPDAdmissionReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDAdmissionStatusReqDTO;
import com.org.hosply360.dto.IPDDTO.IPDPatientListDTO;
import com.org.hosply360.dto.IPDDTO.MedicalInfoDTO;
import com.org.hosply360.dto.IPDDTO.PatientInfoDetailDTO;
import com.org.hosply360.dto.IPDDTO.WardBedInfoDTO;
import com.org.hosply360.exception.IPDException;
import com.org.hosply360.repository.IPD.IPDAdmissionRepository;
import com.org.hosply360.repository.IPD.IPDFinancialSummaryRepository;
import com.org.hosply360.repository.IPD.customRepo.IPDAdmissionCustomRepository;
import com.org.hosply360.repository.globalMasterRepo.WardBedMasterRepository;
import com.org.hosply360.service.IPD.IPDAdmissionService;
import com.org.hosply360.util.Others.AgeUtil;
import com.org.hosply360.util.Others.BarcodeGenerator;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.Others.SequenceGeneratorService;
import com.org.hosply360.util.mapper.HeaderFooterMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IPDAdmissionServiceImpl implements IPDAdmissionService {

    private static final Logger logger = LoggerFactory.getLogger(IPDAdmissionServiceImpl.class);

    private final IPDAdmissionRepository ipdAdmissionRepository;
    private final WardBedMasterRepository wardBedMasterRepository;
    private final EntityFetcherUtil entityFetcherUtil;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final IPDAdmissionCustomRepository ipdAdmissionCustomRepository;
    private final IPDFinancialSummaryRepository ipdFinancialSummaryRepository;

    // Creates a new IPD admission record with the provided details
    // Handles patient admission, bed allocation, and consultant assignments
    @Override
    @Transactional
    public String createAdmission(IPDAdmissionReqDTO requestDTO) {
        logger.info("Creating IPD Admission for patientId={}, orgId={}", requestDTO.getPatientId(), requestDTO.getOrgId());

        validateRequest(requestDTO);

        Organization org = entityFetcherUtil.getOrganizationOrThrow(requestDTO.getOrgId());
        Patient patient = entityFetcherUtil.getPatientOrThrow(requestDTO.getPatientId());
        WardMaster ward = entityFetcherUtil.getWardMasterOrThrow(requestDTO.getWardMasterId());
        WardBedMaster bed = entityFetcherUtil.getWardBedMasterOrThrow(requestDTO.getBedMasterId());
        validateBedAvailability(bed);

        Doctor primaryConsultant = entityFetcherUtil.getDoctorOrThrow(requestDTO.getPrimaryConsultantId());

        // Use Optional for nullable fields
        Doctor secondaryConsultant = Optional.ofNullable(requestDTO.getSecondaryConsultantId())
                .filter(id -> !id.isEmpty())
                .map(entityFetcherUtil::getDoctorOrThrow)
                .orElse(null);

        Doctor refBy = Optional.ofNullable(requestDTO.getRefBy())
                .filter(ref -> !ref.isEmpty())
                .map(entityFetcherUtil::getDoctorOrThrow)
                .orElse(null);

        Speciality department = Optional.ofNullable(requestDTO.getDepartment())
                .filter(dept -> !dept.isEmpty())
                .map(entityFetcherUtil::getSpecialityOrThrow)
                .orElse(null);

        String ipdNo = sequenceGeneratorService.generateIPDNumber();

        IPDAdmission admission = IPDAdmission.builder()
                .orgId(org)
                .patient(patient)
                .wardMaster(ward)
                .bedMaster(bed)
                .wardName(ward.getWardName())
                .bedNo(bed.getBedNo())
                .admitDateTime(requestDTO.getAdmitDateTime())
                .primaryConsultant(primaryConsultant)
                .secondaryConsultant(secondaryConsultant)
                .diagnosis(requestDTO.getDiagnosis())
                .isPatient(requestDTO.getIsPatient())
                .ipdNo(ipdNo)
                .regMrdNo(requestDTO.getRegMrdNo())
                .ipdStatus(IpdStatus.ADMITTED)
                .patientType(requestDTO.getPatientType())
                .department(department)
                .refBy(refBy)
                .remarks(requestDTO.getRemarks())
                .defunct(Boolean.FALSE)
                .build();

        setPatientTypeDetails(admission, requestDTO);

        IPDAdmission savedAdmission = saveAdmissionAndUpdateBed(admission, requestDTO.getBedMasterId());

        logger.info("Successfully created IPD Admission with id={}", savedAdmission.getId());
        return savedAdmission.getId();
    }


    private IPDAdmission saveAdmissionAndUpdateBed(IPDAdmission admission, String bedMasterId) {
        IPDAdmission savedAdmission = ipdAdmissionRepository.save(admission);

        WardBedMaster updateBedMaster = entityFetcherUtil.getWardBedMasterOrThrow(bedMasterId);
        updateBedMaster.setStatus(AdmitStatus.BOOKED);
        wardBedMasterRepository.save(updateBedMaster);

        logger.debug("Updated bed status to BOOKED for bedId={}", bedMasterId);
        return savedAdmission;
    }


    // Checks if the requested bed is available for admission
    // Throws exception if bed is already booked
    private void validateBedAvailability(WardBedMaster bed) {
        if (AdmitStatus.BOOKED.equals(bed.getStatus())) {
            logger.warn("Attempt to admit patient into already booked bed. bedId={}, status={}",
                    bed.getId(), bed.getStatus());
            throw new IPDException(ErrorConstant.BED_ALREADY_BOOKED, HttpStatus.BAD_REQUEST);
        }
    }


    // Validates the admission request to ensure all required fields are present
    // Throws IPDException with appropriate error message if validation fails
    private void validateRequest(IPDAdmissionReqDTO requestDTO) {
        if (ObjectUtils.isEmpty(requestDTO.getPatientId()) || ObjectUtils.isEmpty(requestDTO.getOrgId())) {
            throw new IPDException(ErrorConstant.ORGANIZATION_AND_PATIENT_REQUIRED, HttpStatus.BAD_REQUEST);
        }

        if (ObjectUtils.anyNull(
                requestDTO.getAdmitDateTime(),
                requestDTO.getBedMasterId(),
                requestDTO.getWardMasterId(),
                requestDTO.getPatientType(),
                requestDTO.getIsPatient()
        )) {
            if (requestDTO.getAdmitDateTime() == null) {
                throw new IPDException(ErrorConstant.ADMISSION_DATE_REQUIRED, HttpStatus.BAD_REQUEST);
            }
            if (ObjectUtils.isEmpty(requestDTO.getBedMasterId())) {
                throw new IPDException(ErrorConstant.BED_ASSIGNMENT_REQUIRED, HttpStatus.BAD_REQUEST);
            }
            if (ObjectUtils.isEmpty(requestDTO.getWardMasterId())) {
                throw new IPDException(ErrorConstant.WARD_ASSIGNMENT_REQUIRED, HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getPatientType() == null) {
                throw new IPDException(ErrorConstant.PATIENT_TYPE_REQUIRED, HttpStatus.BAD_REQUEST);
            }
            if (requestDTO.getIsPatient() == null) {
                throw new IPDException(ErrorConstant.PATIENT_CASE_TYPE_REQUIRED, HttpStatus.BAD_REQUEST);
            }
        }
    }


    // Sets patient-specific details based on patient type (Regular/Corporate/Insurance)
    // Handles different data requirements for each patient type
    private void setPatientTypeDetails(IPDAdmission admission, IPDAdmissionReqDTO requestDTO) {
        switch (requestDTO.getPatientType()) {
            case REGULAR -> {
                PatientCategory category = entityFetcherUtil.getPatientCategoryOrThrow(requestDTO.getPatientCategoryId());
                admission.setRegular(category);
            }
            case CORPORATE -> Optional.ofNullable(requestDTO.getCorporateDetails())
                    .ifPresent(corporateReq -> admission.setCorporateDetails(
                            CorporateDetails.builder()
                                    .companyId(entityFetcherUtil.getCompanyOrThrow(corporateReq.getCompanyId()))
                                    .approval(corporateReq.getApproval())
                                    .build()
                    ));

            case INSURANCE -> Optional.ofNullable(requestDTO.getInsuranceDetails())
                    .ifPresent(insuranceReq -> admission.setInsuranceDetails(
                            InsuranceDetails.builder()
                                    .insuranceName(entityFetcherUtil.getInsuranceProviderOrThrow(insuranceReq.getInsuranceProviderId()))
                                    .insuranceNo(insuranceReq.getInsuranceNo())
                                    .approval(insuranceReq.getApproval())
                                    .build()
                    ));

            default -> throw new IPDException(ErrorConstant.UNSUPORTED_PATIENT_TYPE, HttpStatus.BAD_REQUEST);
        }
    }


    // Updates an existing admission record with new information
    // Validates patient ID consistency and updates only allowed fields
    @Override
    @Transactional
    public String updateAdmission(IPDAdmissionReqDTO requestDTO) {
        logger.info("Updating IPD Admission for id={}, orgId={}", requestDTO.getId(), requestDTO.getOrgId());

        IPDAdmission existing = ipdAdmissionRepository.findByIdAndDefunct(requestDTO.getId(), false)
                .orElseThrow(() -> new IPDException(ErrorConstant.IPD_ADMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (!Objects.equals(existing.getPatient().getId(), requestDTO.getPatientId())) {
            throw new IPDException(ErrorConstant.PATIENT_CHANGE_NOT_ALLOWED, HttpStatus.BAD_REQUEST);
        }

        Optional.ofNullable(requestDTO.getPrimaryConsultantId())
                .map(entityFetcherUtil::getDoctorOrThrow)
                .ifPresent(existing::setPrimaryConsultant);

        Optional.ofNullable(requestDTO.getSecondaryConsultantId())
                .map(entityFetcherUtil::getDoctorOrThrow)
                .ifPresent(existing::setSecondaryConsultant);

        Optional.ofNullable(requestDTO.getAdmitDateTime()).ifPresent(existing::setAdmitDateTime);
        Optional.ofNullable(requestDTO.getDiagnosis()).ifPresent(existing::setDiagnosis);
        Optional.ofNullable(requestDTO.getIsPatient()).ifPresent(existing::setIsPatient);
        Optional.ofNullable(requestDTO.getRegMrdNo()).ifPresent(existing::setRegMrdNo);
        Optional.ofNullable(requestDTO.getIpdStatus()).ifPresent(existing::setIpdStatus);

        if (requestDTO.getPatientType() != null) {
            existing.setPatientType(requestDTO.getPatientType());
            setPatientTypeDetails(existing, requestDTO);
        }

        IPDAdmission updatedAdmission = ipdAdmissionRepository.save(existing);

        logger.info("Successfully updated IPD Admission with id={}", updatedAdmission.getId());
        return updatedAdmission.getId();
    }


    // Retrieves list of available beds for a specific ward
    // Used for bed assignment during admission
    @Override
    public BedResponseDTO getBedsByWardId(String orgId, String wardId) {
        return ipdAdmissionRepository.getBedsByWardId(orgId, wardId);
    }

    // Retrieves paginated list of IPD admissions with filtering options
    // Supports filtering by organization, ward, status, and date range
    @Override
    public Page<IPDAdmissionDTO> getAdmissions(
            String orgId,           // Filter by organization ID
            String id,              // Filter by admission ID
            String wardId,          // Filter by ward ID
            String ipdStatus,       // Filter by admission status
            LocalDate fromDate,     // Start date for date range filter
            LocalDate toDate,       // End date for date range filter
            int page,               // Page number (0-based)
            int size               // Number of items per page
    ) {
        return ipdAdmissionCustomRepository.findAdmissions(orgId, id, wardId, ipdStatus, fromDate, toDate, page, size);
    }

    @Override
    public Page<IPDPatientListDTO> getPatientList(
            String orgId,
            String id,
            String wardId,
            String ipdStatus,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    ) {
        return ipdAdmissionCustomRepository.findPatientList(orgId, id, wardId, ipdStatus, fromDate, toDate, page, size);
    }

    // Cancels an existing admission and updates its status
    // Adds cancellation remarks and maintains audit trail
    @Override
    public String cancelAdmission(IPDAdmissionStatusReqDTO requestDTO) {
        IPDAdmission existing = ipdAdmissionRepository.findByIdAndDefunct(requestDTO.getId(), false)
                .orElseThrow(() -> new IPDException(ErrorConstant.IPD_ADMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Optional<IPDFinancialSummary> byIpdAdmissionId = ipdFinancialSummaryRepository.findByIpdAdmissionId(existing.getId());
        if (byIpdAdmissionId.isPresent()) {
            throw new IPDException(ErrorConstant.CANNOT_CANCEL_IPD, HttpStatus.CONFLICT);
        }
        existing.setIpdStatus(IpdStatus.CANCELLED);
        existing.setRemarks(requestDTO.getRemarks());
        ipdAdmissionRepository.save(existing);
        return existing.getId();
    }

    @Override
    public BarcodeResDTO getIpdBarcode(String IpdId) {
        IPDAdmission ipdAdmission = ipdAdmissionRepository.findByIdAndDefunct(IpdId, false).orElseThrow(()
                -> new IPDException(ErrorConstant.IPD_ADMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));

        String barcodeText = ipdAdmission.getIpdNo();
        String barcodeImage = BarcodeGenerator.generateCode128BarcodeBase64(barcodeText);
        return BarcodeResDTO.builder()
                .barcodeImage(barcodeImage)
                .barcodeText(barcodeText)
                .build();
    }

    @Override
    public AdmissionRecordResponseDTO getAdmissionRecord(String ipdId) {

        // ===== Validation =====
        ValidatorHelper.validateObject(ipdId);
        logger.info("Fetching IPD admission record for IPD ID: {}", ipdId);

        // ===== Fetch IPD Admission =====
        IPDAdmission ipdAdmission = ipdAdmissionRepository
                .findByIdAndDefunct(ipdId, false)
                .orElseThrow(() -> {
                    logger.error("IPD Admission not found for ID: {}", ipdId);
                    return new IPDException(
                            ErrorConstant.IPD_ADMISSION_NOT_FOUND,
                            HttpStatus.NOT_FOUND
                    );
                });

        // ===== Patient Mapping =====
        Patient patient = ipdAdmission.getPatient();
        PatientInfoDetailDTO patientInfo = buildPatientInfo(patient);

        // ===== Admission Info =====
        AdmissionInfoDTO admissionInfo = AdmissionInfoDTO.builder()
                .ipdId(ipdAdmission.getId())
                .ipdNo(ipdAdmission.getIpdNo())
                .regMrdNo(ipdAdmission.getRegMrdNo())
                .admitDateTime(ipdAdmission.getAdmitDateTime().toString())
                .dischargeDateTime(ipdAdmission.getDischargeDateTime().toString())
                .department(ipdAdmission.getDepartment().getDepartment())
                .patientType(ipdAdmission.getPatientType().toString())
                .referredBy(ipdAdmission.getRefBy().getFirstName())
                .ipdStatus(ipdAdmission.getIpdStatus().toString())
                .build();

        // ===== Ward & Bed Info =====
        WardBedInfoDTO wardBedInfo = WardBedInfoDTO.builder()
                .wardName(
                        ipdAdmission.getWardMaster() != null
                                ? ipdAdmission.getWardMaster().getWardName()
                                : null
                )
                .bedNo(
                        ipdAdmission.getBedMaster() != null
                                ? ipdAdmission.getBedMaster().getBedNo()
                                : null
                )
                .build();

        // ===== Consultant Info =====
        ConsultantInfoDTO consultantInfo = ConsultantInfoDTO.builder()
                .consultant(
                        ipdAdmission.getPrimaryConsultant() != null
                                ? ipdAdmission.getPrimaryConsultant().getFirstName()
                                : null
                )
                .secondaryConsultant(
                        ipdAdmission.getSecondaryConsultant() != null
                                ? ipdAdmission.getSecondaryConsultant().getFirstName()
                                : null
                )
                .referredBy(ipdAdmission.getRefBy().getFirstName())
                .build();

        // ===== Medical Info =====
        MedicalInfoDTO medicalInfo = MedicalInfoDTO.builder()
                .diagnosis(ipdAdmission.getDiagnosis())
                .isMLC(ApplicationConstant.MLC.equalsIgnoreCase(ipdAdmission.getIsPatient().toString()))
                .build();

        // ===== Final Response =====
        AdmissionRecordResponseDTO response = AdmissionRecordResponseDTO.builder()
                .admissionInfo(admissionInfo)
                .patientInfo(patientInfo)
                .wardBedInfo(wardBedInfo)
                .consultantInfo(consultantInfo)
                .medicalInfo(medicalInfo)
                .remarks(ipdAdmission.getRemarks())
                .headerFooter(
                        HeaderFooterMapperUtil.buildHeaderFooter(
                                ipdAdmission.getOrgId()
                        )
                )
                .build();

        logger.info("Successfully built AdmissionRecordResponseDTO for IPD ID: {}", ipdId);
        return response;
    }

    // =========================================================
    // ================= PRIVATE MAPPER METHODS =================
    // =========================================================

    private PatientInfoDetailDTO buildPatientInfo(Patient patient) {

        if (patient == null) {
            return null;
        }

        var personal = patient.getPatientPersonalInformation();
        var contact = patient.getPatientContactInformation();

        String firstName = null;
        String lastName = null;
        String gender = null;
        String bloodGroup = null;
        String maritalStatus = null;
        String dob = null;

        if (personal != null) {
            firstName = personal.getFirstName();
            lastName = personal.getLastName();
            gender = personal.getGender();
            bloodGroup = personal.getBloodType();
            maritalStatus = personal.getMaritalStatus();
            dob = personal.getDateOfBirth();
        }

        String primaryPhone = null;
        String secondaryPhone = null;
        String email = null;
        String formattedAddress = null;

        if (contact != null) {
            primaryPhone = contact.getPrimaryPhone();
            secondaryPhone = contact.getSecondaryPhone();
            email = contact.getEmail();

            Address addr = contact.getAddress();
            if (addr != null) {
                formattedAddress = String.join(", ",
                        addr.getBuildingFlat(),
                        addr.getStreet(),
                        addr.getCityName(),
                        addr.getStateName(),
                        addr.getCountryName()
                ) + " - " + addr.getPinCode();
            }
        }

        return PatientInfoDetailDTO.builder()
                .id(patient.getId())
                .pid(patient.getPId())
                .firstname(firstName)
                .lastname(lastName)
                .patientNumber(primaryPhone)
                .dateOfBirth(dob)
                .age(AgeUtil.getAge(dob))
                .gender(gender)
                .bloodGroup(bloodGroup)
                .maritalStatus(maritalStatus)
                .alternateNo(secondaryPhone)
                .email(email)
                .address(formattedAddress)
                .build();
    }


}
