package com.org.hosply360.service.frontdesk.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.frontDeskDao.PatientAllergy;
import com.org.hosply360.dao.frontDeskDao.PatientConsents;
import com.org.hosply360.dao.frontDeskDao.PatientContactInformation;
import com.org.hosply360.dao.frontDeskDao.PatientDemographicInformation;
import com.org.hosply360.dao.frontDeskDao.PatientEmergencyContact;
import com.org.hosply360.dao.frontDeskDao.PatientIdentification;
import com.org.hosply360.dao.frontDeskDao.PatientInsuranceDetails;
import com.org.hosply360.dao.frontDeskDao.PatientMedicalInformation;
import com.org.hosply360.dao.frontDeskDao.PatientMedication;
import com.org.hosply360.dao.frontDeskDao.PatientMiscellaneous;
import com.org.hosply360.dao.frontDeskDao.PatientPersonalInformation;
import com.org.hosply360.dao.globalMaster.Address;
import com.org.hosply360.dao.globalMaster.IdentificationDocument;
import com.org.hosply360.dao.globalMaster.InsuranceProvider;
import com.org.hosply360.dao.globalMaster.Language;
import com.org.hosply360.dao.globalMaster.Occupation;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.Religion;
import com.org.hosply360.dto.frontDeskDTO.DoctorDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientAllergyDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientConsentsDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientContactInformationDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientDemographicInformationDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientEmergencyContactDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientIdentificationDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientIdentificationReqDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientInfoDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientInsuranceDetailsDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientMedicalInformationDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientMedicationDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientMiscellaneousDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientPersonalInformationDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientReqDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.AddressDTO;
import com.org.hosply360.dto.globalMasterDTO.IdentificationDocumentDTO;
import com.org.hosply360.dto.globalMasterDTO.IdentificationResDTO;
import com.org.hosply360.dto.globalMasterDTO.InsuranceProviderDTO;
import com.org.hosply360.dto.globalMasterDTO.LanguageDTO;
import com.org.hosply360.dto.globalMasterDTO.OccupationDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.ReligionDTO;
import com.org.hosply360.dto.globalMasterDTO.SpecialityDTO;
import com.org.hosply360.exception.FrontDeskException;
import com.org.hosply360.repository.frontDeskRepo.DoctorMasterRepository;
import com.org.hosply360.repository.frontDeskRepo.PatientRepository;
import com.org.hosply360.repository.globalMasterRepo.AddressMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.IdentificationDocumentRepository;
import com.org.hosply360.repository.globalMasterRepo.InsuranceProviderRepository;
import com.org.hosply360.repository.globalMasterRepo.LanguageMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.OccupationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.ReligionRepository;
import com.org.hosply360.service.frontdesk.PatientMasterService;
import com.org.hosply360.util.Others.SequenceGeneratorService;
import com.org.hosply360.util.encryptionUtil.EncryptionUtil;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientMasterService {

    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
    private final PatientRepository patientRepository;
    private final OccupationMasterRepository occupationRepository;
    private final AddressMasterRepository addressMasterRepository;
    private final IdentificationDocumentRepository identificationDocumentRepository;
    private final LanguageMasterRepository languageMasterRepository;
    private final ReligionRepository religionMasterRepository;
    private final DoctorMasterRepository doctorMasterRepository;
    private final InsuranceProviderRepository insuranceProviderRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final OrganizationMasterRepository organizationMasterRepository;

    private void decryptPatientDTO(PatientResponseDTO dto) {
        dto.setFirstName(EncryptionUtil.decrypt(dto.getFirstName()));
        dto.setLastName(EncryptionUtil.decrypt(dto.getLastName()));
        dto.setPhoneNumber(EncryptionUtil.decrypt(dto.getPhoneNumber()));
        dto.setDob(EncryptionUtil.decrypt(dto.getDob()));
    }
    public PatientDTO getPatientDTO(Patient patient) {
        PatientDTO patientDTO = ObjectMapperUtil.copyObject(patient, PatientDTO.class);
        patientDTO.setPId(patient.getPId());
        patientDTO.setOrganization(ObjectMapperUtil.copyObject(patient.getOrganization(), OrganizationDTO.class));
        OrganizationDTO organizationDTO = ObjectMapperUtil.copyObject(patient.getOrganization(), OrganizationDTO.class);
        if (patient.getOrganization() != null && patient.getOrganization().getAddress() != null) {
            AddressDTO addressDTO = ObjectMapperUtil.copyObject(patient.getOrganization().getAddress(), AddressDTO.class);
            organizationDTO.setAddress(addressDTO);
        }
        patientDTO.setOrganization(organizationDTO);
        PatientPersonalInformationDTO patientPersonalInformationDTO = ObjectMapperUtil.copyObject(patient.getPatientPersonalInformation(), PatientPersonalInformationDTO.class);
        patientPersonalInformationDTO.setOccupation(ObjectMapperUtil.copyObject(patient.getPatientPersonalInformation().getOccupation(), OccupationDTO.class));
        patientDTO.setPersonalInformation(patientPersonalInformationDTO);

        PatientContactInformationDTO patientContactInformationDTO = null;
        if (patient.getPatientContactInformation() != null) {
            patientContactInformationDTO = ObjectMapperUtil.copyObject(patient.getPatientContactInformation(), PatientContactInformationDTO.class);
            patientContactInformationDTO.setAddress(
                    ObjectMapperUtil.copyObject(patient.getPatientContactInformation().getAddress(), AddressDTO.class)
            );
        }
        patientDTO.setContactInformation(patientContactInformationDTO);

        List<PatientIdentificationDTO> patientIdentificationDTOList = Optional.ofNullable(patient.getPatientIdentification())
                .orElse(Collections.emptyList())
                .stream()
                .map(identification -> {
                    PatientIdentificationDTO identificationDTO = ObjectMapperUtil.copyObject(identification, PatientIdentificationDTO.class);
                    identificationDTO.setIdentificationDocumentId(ObjectMapperUtil.copyObject(identification.getIdentificationDocument(), IdentificationDocumentDTO.class));
                    return identificationDTO;
                })
                .toList();
        patientDTO.setIdentification(patientIdentificationDTOList);

        PatientMedicalInformationDTO patientMedicalInformation = null;
        if (patient.getPatientMedicalInformation() != null) {
            patientMedicalInformation = ObjectMapperUtil.copyObject(patient.getPatientMedicalInformation(), PatientMedicalInformationDTO.class);
            patientMedicalInformation.setKnownAllergies(
                    ObjectMapperUtil.copyListObject(patient.getPatientMedicalInformation().getKnownAllergies(), PatientAllergyDTO.class)
            );
            patientMedicalInformation.setCurrentPatientMedications(
                    ObjectMapperUtil.copyListObject(patient.getPatientMedicalInformation().getCurrentPatientMedications(), PatientMedicationDTO.class)
            );
        }
        patientDTO.setMedicalInformation(patientMedicalInformation);
        List<PatientEmergencyContactDTO> patientEmergencyContacts = new ArrayList<>();
        if (patient.getPatientEmergencyContacts() != null) {
            if (patient.getPatientEmergencyContacts() instanceof PatientEmergencyContactDTO) {
                patientEmergencyContacts.add(ObjectMapperUtil.copyObject(patient.getPatientEmergencyContacts(), PatientEmergencyContactDTO.class));
            } else {
                patientEmergencyContacts = ObjectMapperUtil.copyListObject(patient.getPatientEmergencyContacts(), PatientEmergencyContactDTO.class);
            }
        }
        patientDTO.setEmergencyContacts(patientEmergencyContacts);
        PatientDemographicInformationDTO patientDemographicInformationDTO = null;
        if (patient.getPatientDemographicInformation() != null) {
            patientDemographicInformationDTO = ObjectMapperUtil.copyObject(patient.getPatientDemographicInformation(), PatientDemographicInformationDTO.class);
            patientDemographicInformationDTO.setLanguagePreference(
                    ObjectMapperUtil.copyObject(patient.getPatientDemographicInformation().getLanguagePreference(), LanguageDTO.class)
            );
            patientDemographicInformationDTO.setReligion(
                    ObjectMapperUtil.copyObject(patient.getPatientDemographicInformation().getReligion(), ReligionDTO.class)
            );
        }
        patientDTO.setDemographicInformation(patientDemographicInformationDTO);
        PatientConsentsDTO patientConsentsDTO = null;
        if (patient.getPatientConsents() != null) {
            patientConsentsDTO = ObjectMapperUtil.copyObject(patient.getPatientConsents(), PatientConsentsDTO.class);
        }
        patientDTO.setConsents(patientConsentsDTO);
        List<PatientInsuranceDetailsDTO> insuranceDetailsDTOList = Optional.ofNullable(patient.getPatientInsuranceDetails()).orElse(Collections.emptyList()).stream().map(insurance -> {
            PatientInsuranceDetailsDTO insuranceDTO = ObjectMapperUtil.copyObject(insurance, PatientInsuranceDetailsDTO.class);
            insuranceDTO.setProvider(ObjectMapperUtil.copyObject(insurance.getProvider(), InsuranceProviderDTO.class));
            return insuranceDTO;
        }).toList();
        patientDTO.setInsuranceDetails(insuranceDetailsDTOList);
        PatientMiscellaneousDTO patientMiscellaneousDTO = ObjectMapperUtil.copyObject(patient.getPatientMiscellaneous(), PatientMiscellaneousDTO.class);

        if (patient.getPatientMiscellaneous() != null && patient.getPatientMiscellaneous().getReferredBy() != null) {
            Doctor doctor = patient.getPatientMiscellaneous().getReferredBy();
            DoctorDTO doctorDTO = ObjectMapperUtil.copyObject(doctor, DoctorDTO.class);
            doctorDTO.setOrganization(ObjectMapperUtil.copyListObject(doctor.getOrganization(), OrganizationDTO.class));
            doctorDTO.setLanguage(ObjectMapperUtil.copyListObject(doctor.getLanguage(), LanguageDTO.class));
            doctorDTO.setDepartment(ObjectMapperUtil.copyObject(doctor.getDepart(), SpecialityDTO.class));
            doctorDTO.setSpecialty(ObjectMapperUtil.copyObject(doctor.getSpecialty(), SpecialityDTO.class));
            doctorDTO.setPermanentAddress(ObjectMapperUtil.copyObject(doctor.getPermanentAddress(), AddressDTO.class));
            patientMiscellaneousDTO.setDoctor(doctorDTO);
        }
        patientDTO.setMiscellaneous(patientMiscellaneousDTO);
        return patientDTO;

    }

    public Occupation validateOccupation(PatientReqDTO patientDTO) {

        String occupationId = patientDTO.getPersonalInformation().getOccupationId();

        if (occupationId != null && !occupationId.trim().isEmpty()) {
            logger.info("Validating occupation with ID: {}", occupationId.trim());
            return occupationRepository.findByIdAndDefunct(occupationId.trim(), false)
                    .orElseThrow(() -> new FrontDeskException(ErrorConstant.OCCUPATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        }
        return null;
    }

    public Organization validateOrganization(PatientReqDTO patientDTO) {
        if (patientDTO == null || patientDTO.getOrganizationId() == null || patientDTO.getOrganizationId().trim().isEmpty()) {
            logger.info("Organization ID is missing or empty. Returning null organization.");
            return null;
        } else {
            String organizationId = patientDTO.getOrganizationId().trim();
            logger.info("Validating organization with ID: {}", organizationId);

            return organizationMasterRepository.findByIdAndDefunct(organizationId, false)
                    .orElseThrow(() -> new FrontDeskException(
                            ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        }
    }

    public Doctor validateDoctor(PatientReqDTO patientDTO) {
        if (patientDTO.getMiscellaneous() != null) {
            String doctorId = patientDTO.getMiscellaneous().getDoctorId();
            if (doctorId != null && !doctorId.trim().isEmpty()) {
                return doctorMasterRepository.findByIdAndDefunct( doctorId,false)
                        .orElseThrow(() -> new FrontDeskException(ErrorConstant.DOCTOR_NOT_FOUND, HttpStatus.NOT_FOUND));
            }
        }
        return null;
    }

    @Override
    @Transactional
    public String  createOrUpdatePatient(PatientReqDTO patientReqDTO) {
        Patient patient;
        boolean isUpdate = false;

        if (patientReqDTO.getId() != null && !patientReqDTO.getId().isEmpty()) {
            patient = patientRepository.findByIdAndDefunct(patientReqDTO.getId(), false)
                    .orElseThrow(() -> new FrontDeskException(ErrorConstant.PATIENT_NOT_FOUND, HttpStatus.NOT_FOUND));
            isUpdate = true;
        } else {
            patient = new Patient();
        }
        if (!isUpdate) {
            String name = patientReqDTO.getPersonalInformation().getFirstName();
            String primaryPhone = patientReqDTO.getContactInformation().getPrimaryPhone();

            if (name != null && !name.trim().isEmpty() &&
                    primaryPhone != null && !primaryPhone.trim().isEmpty()) {

                String normalizedName = name.trim();
                String normalizedPhone = primaryPhone.trim();
                String encryptedName = EncryptionUtil.encrypt(normalizedName);
                String encryptedPhone = EncryptionUtil.encrypt(normalizedPhone);
                Optional<Patient> existingPatient = patientRepository
                        .findByEncryptedNameAndEncryptedPhone(encryptedName, encryptedPhone);
                if (existingPatient.isPresent()) {
                    throw new FrontDeskException(
                            "Patient with same name and primary contact already exists",
                            HttpStatus.CONFLICT
                    );
                }
            }
        }

        Occupation occupation = validateOccupation(patientReqDTO);
        PatientMiscellaneous patientMiscellaneous = new PatientMiscellaneous();
        Doctor doctor = validateDoctor(patientReqDTO);
        if (doctor != null) {
            patientMiscellaneous.setReferredBy(doctor);
        }
        Organization organization = validateOrganization(patientReqDTO);

        Address savedAddress = null;
        if (patientReqDTO.getContactInformation() != null && patientReqDTO.getContactInformation().getAddress() != null) {
            Address address = ObjectMapperUtil.copyObject(patientReqDTO.getContactInformation().getAddress(), Address.class);
            savedAddress = addressMasterRepository.save(address);
        }

        List<PatientIdentification> patientIdentificationList = new ArrayList<>();

        if (patientReqDTO.getIdentification() != null && !patientReqDTO.getIdentification().isEmpty()
                && patientReqDTO.getIdentification().get(0).getIdentificationDocumentId() != null
                && !patientReqDTO.getIdentification().get(0).getIdentificationDocumentId().isEmpty()) {

            List<String> documentIdList = patientReqDTO.getIdentification().stream()
                    .map(PatientIdentificationReqDTO::getIdentificationDocumentId)
                    .toList();

            List<IdentificationResDTO> fetchIdAndLimit = identificationDocumentRepository
                    .findByIdAndDefunct(documentIdList, false);
            Set<String> dbIds = fetchIdAndLimit.stream()
                    .map(IdentificationResDTO::getId)
                    .collect(Collectors.toSet());

            if (!dbIds.containsAll(documentIdList)) {
                throw new FrontDeskException(ErrorConstant.INVALID_IDENTIFICATION_ID, HttpStatus.BAD_REQUEST);
            }

            Map<String, Long> dbIdToLimit = fetchIdAndLimit.stream()
                    .collect(Collectors.toMap(IdentificationResDTO::getId, IdentificationResDTO::getLimit));

            for (PatientIdentificationReqDTO req : patientReqDTO.getIdentification()) {
                Long expectedLimit = dbIdToLimit.get(req.getIdentificationDocumentId());
                if (expectedLimit == null) {
                    throw new FrontDeskException(ErrorConstant.DOCUMENT_REQUIRED, HttpStatus.NOT_FOUND);
                }
                if (req.getDocumentNumber().length() != expectedLimit) {
                    throw new FrontDeskException(ErrorConstant.DOCUMENT_NUMBER_LENGTH_MISMATCH + req.getIdentificationDocumentId(), HttpStatus.BAD_REQUEST);
                }
            }

            patientIdentificationList = patientReqDTO.getIdentification().stream()
                    .map(req -> {
                        IdentificationDocument identificationDocument = identificationDocumentRepository
                                .findByIdandDefunct(req.getIdentificationDocumentId(), false)
                                .orElseThrow(() -> new FrontDeskException(
                                        ErrorConstant.ID_DOCUMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
                        return new PatientIdentification(identificationDocument, req.getDocumentNumber());
                    })
                    .toList();
        }


        List<PatientInsuranceDetails> insuranceDetailsList;
        if (patientReqDTO.getInsuranceDetails() != null &&
                !patientReqDTO.getInsuranceDetails().isEmpty() &&
                patientReqDTO.getInsuranceDetails().getFirst().getProviderId() != null &&
                !patientReqDTO.getInsuranceDetails().getFirst().getProviderId().isEmpty()) {

            insuranceDetailsList = Optional.ofNullable(patientReqDTO.getInsuranceDetails())
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(dto -> dto.getProviderId() != null)
                    .map(dto -> {
                        InsuranceProvider insuranceProvider = insuranceProviderRepository.findByIdAndDefunct(dto.getProviderId(), false)
                                .orElseThrow(() -> new FrontDeskException(ErrorConstant.INSURANCE_PROVIDER_NOT_FOUND, HttpStatus.NOT_FOUND));
                        PatientInsuranceDetails details = ObjectMapperUtil.copyObject(dto, PatientInsuranceDetails.class);
                        details.setProvider(insuranceProvider);
                        return details;
                    }).toList();
        } else {
            insuranceDetailsList = Collections.emptyList();
        }

        List<PatientEmergencyContact> emergencyContacts;
        if (patientReqDTO.getEmergencyContacts() != null &&
                !patientReqDTO.getEmergencyContacts().isEmpty() &&
                patientReqDTO.getEmergencyContacts().getFirst().getName() != null &&
                !patientReqDTO.getEmergencyContacts().getFirst().getName().isEmpty()) {

            emergencyContacts = patientReqDTO.getEmergencyContacts()
                    .stream()
                    .map(dto -> {
                        return ObjectMapperUtil.copyObject(dto, PatientEmergencyContact.class);
                    })
                    .toList();
        } else {
            emergencyContacts = Collections.emptyList();
        }
        Language language = null;
        if (patientReqDTO.getDemographicInformation() != null && patientReqDTO.getDemographicInformation().getLanguagePreferenceId() != null) {
            String languageId = patientReqDTO.getDemographicInformation().getLanguagePreferenceId();
            if (!languageId.trim().isEmpty()) {
                language = languageMasterRepository.findById(languageId)
                        .orElseThrow(() -> new FrontDeskException(ErrorConstant.LANGUAGE_NOT_FOUND, HttpStatus.NOT_FOUND));
            }
        }
        Religion religion = null;
        if (patientReqDTO.getDemographicInformation() != null && patientReqDTO.getDemographicInformation().getReligionId() != null) {
            String religionId = patientReqDTO.getDemographicInformation().getReligionId();
            if (!religionId.trim().isEmpty()) {
                religion = religionMasterRepository.findById(religionId)
                        .orElseThrow(() -> new FrontDeskException(ErrorConstant.RELIGION_NOT_FOUND, HttpStatus.NOT_FOUND));
            }
        }
        PatientPersonalInformation personalInfo = ObjectMapperUtil.copyObject(patientReqDTO.getPersonalInformation(), PatientPersonalInformation.class);
        personalInfo.setOccupation(occupation);
        PatientContactInformation contactInfo = null;
        if (patientReqDTO.getContactInformation() != null && !patientReqDTO.getContactInformation().equals("")) {
            contactInfo = ObjectMapperUtil.copyObject(patientReqDTO.getContactInformation(), PatientContactInformation.class);
            contactInfo.setAddress(savedAddress);
        }
        PatientMedicalInformation medicalInfo = null;
        if (patientReqDTO.getMedicalInformation() != null) {
            medicalInfo = ObjectMapperUtil.copyObject(patientReqDTO.getMedicalInformation(), PatientMedicalInformation.class);
            medicalInfo.setKnownAllergies(ObjectMapperUtil.copyListObject(patientReqDTO.getMedicalInformation().getKnownAllergies(), PatientAllergy.class));
            medicalInfo.setCurrentPatientMedications(ObjectMapperUtil.copyListObject(patientReqDTO.getMedicalInformation().getCurrentPatientMedications(), PatientMedication.class));
        }

        PatientDemographicInformation demographicInfo = null;
        if (patientReqDTO.getDemographicInformation() != null) {
            demographicInfo = ObjectMapperUtil.copyObject(patientReqDTO.getDemographicInformation(), PatientDemographicInformation.class);
            demographicInfo.setLanguagePreference(language);
            demographicInfo.setReligion(religion);
        }

        PatientConsents consents = null;
        if (patientReqDTO.getConsents() != null) {
            consents = ObjectMapperUtil.copyObject(patientReqDTO.getConsents(), PatientConsents.class);
        }

        String pidToUse;
        if (!isUpdate) {
            if (patientReqDTO.getId() == null || patientReqDTO.getId().isEmpty()) {
                long seqNum = sequenceGeneratorService.generatePatientSequence("patient_sequence");
                pidToUse = String.format("PID%03d", seqNum);
                patientReqDTO.setPId(pidToUse);
            } else {
                pidToUse = patientReqDTO.getPId();
            }
            patient.setPId(pidToUse);
        }
        patient.setOrganization(organization);
        patient.setPatientPersonalInformation(personalInfo);
        patient.setPatientContactInformation(contactInfo);
        patient.setPatientIdentification(patientIdentificationList);
        patient.setPatientMedicalInformation(medicalInfo);
        patient.setPatientEmergencyContacts(emergencyContacts);
        patient.setPatientDemographicInformation(demographicInfo);
        patient.setPatientConsents(consents);
        patient.setPatientMiscellaneous(patientMiscellaneous);
        patient.setPatientInsuranceDetails(insuranceDetailsList);
        patient.setStatus(patientReqDTO.getStatus());
        patient.setDefunct(false);
        if (patientReqDTO.getImage() != null && !patientReqDTO.getImage().isEmpty()) {
            String base64Clean = patientReqDTO.getImage().replaceAll("\\s+", "");
            byte[] decodedImage = Base64.getDecoder().decode(base64Clean);
            patient.setImage(decodedImage);
        }else{
            patient.setImage(null);
        }
        patientRepository.save(patient);
        return patient.getId();
    }

    @Override
    public List<PatientResponseDTO> getAllPatients(String organizationId) {
        List<PatientResponseDTO> dtos = patientRepository.findPatientsByOrgId(organizationId);
        dtos.forEach(this::decryptPatientDTO);
        return dtos;
    }

    @Override
    public PatientDTO getPatient(String id) {
        logger.info("Fetching patient with ID {}", id);
        Patient patient = patientRepository.findByIdAndDefunct(id, false).orElseThrow(() -> new FrontDeskException(ErrorConstant.PATIENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        return getPatientDTO(patient);
    }

        @Override
        public List<PatientInfoDTO> fetchAllPatient(String organizationId) {
            logger.info("Fetching all patients");
            List<Patient> allPatient = patientRepository.findAllByDefuncts(false, organizationId);
            return allPatient.stream().map(patient -> {
                PatientInfoDTO patientInfoDTO = ObjectMapperUtil.copyObject(patient, PatientInfoDTO.class);
                patientInfoDTO.setFirstname(patient.getPatientPersonalInformation().getFirstName());
                patientInfoDTO.setPid(patient.getPId());
                patientInfoDTO.setLastname(patient.getPatientPersonalInformation().getLastName());
                patientInfoDTO.setPatientNumber(patient.getPatientContactInformation().getPrimaryPhone());
                return patientInfoDTO;
            }).toList();
        }

    @Override
    public void deletePatient(String id) {
        logger.info("Deleting patient with ID {}", id);
        Patient patient = patientRepository.findByIdAndDefunct(id, false).orElseThrow(() -> new FrontDeskException(ErrorConstant.PATIENT_NOT_FOUND, HttpStatus.NOT_FOUND));
        patient.setDefunct(true);
        patientRepository.save(patient);
        logger.info("Patient deleted successfully with ID {}", id);
    }
}
