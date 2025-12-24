package com.org.hosply360.service.frontdesk.impl;

import com.org.hosply360.cacheService.DoctorCacheService;
import com.org.hosply360.constant.Enums.DoctorType;
import com.org.hosply360.constant.Enums.RoleEnum;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.auth.Users;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.frontDeskDao.DoctorDocument;
import com.org.hosply360.dao.frontDeskDao.DoctorTariff;
import com.org.hosply360.dao.globalMaster.Address;
import com.org.hosply360.dao.globalMaster.BillingItemGroup;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.Tariff;
import com.org.hosply360.dto.OPDDTO.AppointmentDocInfoDTO;
import com.org.hosply360.dto.frontDeskDTO.DoctorDTO;
import com.org.hosply360.dto.frontDeskDTO.DoctorDocumentDTO;
import com.org.hosply360.dto.frontDeskDTO.DoctorDocumentResDTO;
import com.org.hosply360.dto.frontDeskDTO.DoctorReqDTO;
import com.org.hosply360.dto.frontDeskDTO.DoctorTariffDTO;
import com.org.hosply360.dto.frontDeskDTO.GetDoctorResponse;
import com.org.hosply360.dto.globalMasterDTO.AddressDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemReqDTO;
import com.org.hosply360.dto.globalMasterDTO.LanguageDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.SpecialityDTO;
import com.org.hosply360.exception.FrontDeskException;
import com.org.hosply360.repository.authRepo.UsersRepository;
import com.org.hosply360.repository.frontDeskRepo.DoctorMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.AddressMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.BillingItemGroupRepository;
import com.org.hosply360.repository.globalMasterRepo.LanguageMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.SpecialityMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.TariffMasterRepository;
import com.org.hosply360.service.frontdesk.DoctorMasterService;
import com.org.hosply360.service.globalMaster.BillingItemService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.org.hosply360.constant.Enums.CreditToEnum.HOSPITAL;

@Service
@RequiredArgsConstructor
public class DoctorMasterServiceImpl implements DoctorMasterService {

    private static final Logger logger = LoggerFactory.getLogger(DoctorMasterServiceImpl.class);

    private final DoctorMasterRepository doctorRepository;
    private final AddressMasterRepository addressMasterRepository;
    private final SpecialityMasterRepository specialityMasterRepository;
    private final LanguageMasterRepository languageMasterRepository;
    private final OrganizationMasterRepository organizationMasterRepository;
    private final BillingItemService billingItemService;
    private final BillingItemGroupRepository billingItemGroupRepository;
    private final UsersRepository usersRepository;
    private final TariffMasterRepository tariffRepository;
    private final DoctorCacheService doctorCacheService;


    private static DoctorDTO getDoctorDTO(Doctor doctor) {
        DoctorDTO doctorDto = ObjectMapperUtil.copyObject(doctor, DoctorDTO.class);
        doctorDto.setLanguage(ObjectMapperUtil.copyListObject(doctor.getLanguage(), LanguageDTO.class));
        doctorDto.setDoctorUserId(doctor.getUser().getId());
        doctorDto.setUsername(doctor.getUser().getUsername());
        doctorDto.setDoctorType(doctor.getDoctorType());
        doctorDto.setDoctorDocument(ObjectMapperUtil.copyListObject(doctor.getDoctorDocument(), DoctorDocumentResDTO.class));
        List<DoctorTariffDTO> doctorTariffDTO =null;
        if (doctor.getDoctorTariff() != null) {
           doctorTariffDTO = doctor.getDoctorTariff().stream().map(tariff -> {
                DoctorTariffDTO dto = new DoctorTariffDTO();
                dto.setTariffId(tariff.getTariff() != null ? tariff.getTariff().getId() : null);
                dto.setTariffName(tariff.getTariff() != null ? tariff.getTariff().getName() : null);
                dto.setFirstRate(tariff.getFirstRate());
                dto.setSecondRate(tariff.getSecondRate());
                return dto;
            }).toList();
        }
        doctorDto.setDoctorTariff(doctorTariffDTO);
        doctorDto.setTotalFirstVisitRate(doctor.getTotalFirstVisitRate());
        doctorDto.setTotalSecondVisitRate(doctor.getTotalSecondVisitRate());
        doctorDto.setDepartment(ObjectMapperUtil.copyObject(doctor.getDepart(), SpecialityDTO.class));
        doctorDto.setSpecialty(ObjectMapperUtil.copyObject(doctor.getSpecialty(), SpecialityDTO.class));
        doctorDto.setPermanentAddress(ObjectMapperUtil.copyObject(doctor.getPermanentAddress(), AddressDTO.class));
        doctorDto.setOrganization(ObjectMapperUtil.copyListObject(doctor.getOrganization(), OrganizationDTO.class));
        return doctorDto;
    }


    @Override
    @Transactional
    public String createDoctor(DoctorReqDTO doctorReqDTO) throws IOException {
        ValidatorHelper.validateObject(doctorReqDTO);
        Optional<Doctor> doctorOpt = doctorRepository.findByIdAndDefunct(doctorReqDTO.getId(), false);
        String id;
        if (doctorOpt.isPresent()) {
            logger.info("Updating existing doctor with ID: {}", doctorOpt.get().getId());
            try {
                id = updateDoctor(doctorOpt.get(), doctorReqDTO);
            } catch (IOException e) {
                logger.error("Error updating doctor: {}", e.getMessage());
                throw new FrontDeskException(ErrorConstant.UPDATE_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            logger.info("Creating new doctor");
            id = createNewDoctor(doctorReqDTO);
        }
        return id;

    }

    private String createNewDoctor(DoctorReqDTO doctorReqDTO) {

        if (doctorReqDTO.getRegistrationNo() != null && !doctorReqDTO.getRegistrationNo().isEmpty()) {
            if (doctorRepository.findByRegistrationNoAndDefunct(doctorReqDTO.getRegistrationNo(), false).isPresent()) {
                logger.info("Doctor with registration number {} already exists", doctorReqDTO.getRegistrationNo());
                throw new FrontDeskException(ErrorConstant.DOCTOR_ALREADY_EXISTS + " " + doctorReqDTO.getRegistrationNo(), HttpStatus.BAD_REQUEST);
            }
        }

        List<Organization> organizations = organizationMasterRepository.findAllByIDAndDefunct(doctorReqDTO.getOrgIds(), false).orElseThrow(() -> new FrontDeskException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));

        doctorRepository.findByUser_IdAndDefunct(doctorReqDTO.getDoctorUserId(), false)
                .ifPresent(d -> {
                    throw new FrontDeskException(
                            ErrorConstant.USER_ALREADY_ASSIGNED_AS_DOCTOR, HttpStatus.BAD_REQUEST
                    );
                });


        Users users = usersRepository.findByIdAndDefunct(doctorReqDTO.getDoctorUserId(), false).orElseThrow(() -> new FrontDeskException(ErrorConstant.USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        users.getRoles().stream()
                .filter(role -> RoleEnum.DOCTOR.name().equals(role.getName()))
                .findFirst().orElseThrow(() -> new FrontDeskException(ErrorConstant.USER_SHOULD_BE_HAVE_DOCTOR_ROLE, HttpStatus.BAD_REQUEST));


        Doctor doctor = ObjectMapperUtil.copyObject(doctorReqDTO, Doctor.class);
        doctor.setUser(users);
        if (doctorReqDTO.getRegistrationNo().isEmpty()) {
            doctor.setRegistrationNo(null);
        } else {
            doctor.setRegistrationNo(doctorReqDTO.getRegistrationNo());
        }

        List<DoctorDocument> doctorDocumentList = new ArrayList<>();
        if (doctorReqDTO.getDoctorDocumentDTOS() != null && !doctorReqDTO.getDoctorDocumentDTOS().isEmpty()) {
            doctorReqDTO.getDoctorDocumentDTOS().stream().map(doc ->
            {
                byte[] decodeImage = Base64.getDecoder().decode(doc.getDocFile().replaceAll("\\s+", ""));
                DoctorDocument doctorDocument = new DoctorDocument();
                doctorDocument.setDocFile(decodeImage);
                doctorDocument.setDocName(doc.getDocName());
                doctorDocumentList.add(doctorDocument);
                return doctorDocumentList;
            }).toList();
        }
        doctor.setDoctorDocument(doctorDocumentList);

        doctor.setOrganization(organizations);
        if (doctorReqDTO.getLanguageId() != null && !doctorReqDTO.getLanguageId().isEmpty()) {
            doctor.setLanguage(languageMasterRepository.findAllByIdAndDefunct(doctorReqDTO.getLanguageId(), false).orElseThrow(() -> new FrontDeskException(ErrorConstant.LANGUAGE_NOT_FOUND, HttpStatus.NOT_FOUND)));
        }


        if (doctorReqDTO.getDepartmentId() != null && !doctorReqDTO.getDepartmentId().isEmpty()) {
            doctor.setDepart(specialityMasterRepository.findByIdAndDefunct(doctorReqDTO.getDepartmentId(), false).orElseThrow(() -> new FrontDeskException(ErrorConstant.DEPARTMENT_NOT_FOUND, HttpStatus.NOT_FOUND)));
        }

        if (doctorReqDTO.getSpecialtyId() != null && !doctorReqDTO.getSpecialtyId().isEmpty()) {
            doctor.setSpecialty(specialityMasterRepository.findByIdAndDefunct(doctorReqDTO.getSpecialtyId(), false).orElseThrow(() -> new FrontDeskException(ErrorConstant.SPECIALITY_NOT_FOUND, HttpStatus.NOT_FOUND)));
        }

        Address permanentAddress = null;
        if (doctorReqDTO.getPermanentAddress() != null) {
            permanentAddress = addressMasterRepository.save(ObjectMapperUtil.copyObject(doctorReqDTO.getPermanentAddress(), Address.class));
            doctor.setPermanentAddress(permanentAddress);
        }

        doctor.setDoctorType(doctorReqDTO.getDoctorType());
        doctor.setDefunct(false);
        List<DoctorTariff> doctorTariffs=null;
        if (doctorReqDTO.getDoctorTariffDTOS() != null && !doctorReqDTO.getDoctorTariffDTOS().isEmpty()) {
            doctorTariffs = doctorReqDTO.getDoctorTariffDTOS().stream().map(t -> {
                DoctorTariff doctorTariff = new DoctorTariff();
                Tariff tariff = tariffRepository.findByIdAndDefunct(t.getTariffId(), false)
                        .orElseThrow(() -> new FrontDeskException(ErrorConstant.TARIFF_NOT_FOUND, HttpStatus.NOT_FOUND));
                doctorTariff.setTariff(tariff);
                doctorTariff.setTariffName(tariff.getName());
                doctorTariff.setFirstRate(t.getFirstRate());
                doctorTariff.setSecondRate(t.getSecondRate());
                return doctorTariff;
            }).toList();
        }
        doctor.setDoctorTariff(doctorTariffs);

        double totalTariffFirstRate = parseAndSumRates(doctorTariffs, DoctorTariff::getFirstRate);
        double totalTariffSecondRate = parseAndSumRates(doctorTariffs, DoctorTariff::getSecondRate);
        double finalFirstVisitRate = (doctorReqDTO.getFirstVisitRate() != null ? doctorReqDTO.getFirstVisitRate() : 0.0) + totalTariffFirstRate;
        double finalSecondVisitRate = (doctorReqDTO.getSecondVisitRate() != null ? doctorReqDTO.getSecondVisitRate() : 0.0) + totalTariffSecondRate;

        doctor.setTotalFirstVisitRate(finalFirstVisitRate);
        doctor.setTotalSecondVisitRate(finalSecondVisitRate);




        Doctor savedDoctor = doctorCacheService.saveDoctor(doctor);
        if (Objects.nonNull(savedDoctor)) {
            BillingItemGroup bgEntity = billingItemGroupRepository
                    .findByItemGroupNameAndDefunct("consultant", false)
                    .orElseGet(() -> {
                        BillingItemGroup newGroup = new BillingItemGroup();
                        newGroup.setItemGroupName("consultant");
                        newGroup.setDefunct(false);
                        newGroup.setOrganization(savedDoctor.getOrganization().getFirst());
                        return billingItemGroupRepository.save(newGroup);
                    });

            BillingItemReqDTO billingItemReqDTO = BillingItemReqDTO.builder()
                    .organization(savedDoctor.getOrganization().getFirst().getId())
                    .itemName(savedDoctor.getFirstName())
                    .itemGroupId(bgEntity.getId())
                    .serviceCode(savedDoctor.getId())
                    .departmentId(savedDoctor.getDepart().getId())
                    .rate(savedDoctor.getTotalFirstVisitRate())
                    .creditTo(HOSPITAL)
                    .build();


            billingItemService.createBillingItem(billingItemReqDTO);

        }
        logger.info("Doctor created successfully with Name {}", savedDoctor.getFirstName());

        return savedDoctor.getId();
    }
    private double parseAndSumRates(List<DoctorTariff> tariffs, Function<DoctorTariff, String> rateExtractor) {
        if (tariffs == null || tariffs.isEmpty()) return 0.0;

        return tariffs.stream()
                .map(rateExtractor)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .mapToDouble(s -> {
                    try {
                        return Double.parseDouble(s);
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                })
                .sum();
    }


    @Override
    public List<GetDoctorResponse> getAllDoctors(String orgId) {
        return doctorRepository.findAllByDefunct(false, orgId);
    }

    @Override
    public DoctorDTO getDoctorById(String id) {
        Doctor doctor = doctorRepository.findByIdAndDefunct(id, false).orElseThrow(() -> new FrontDeskException(ErrorConstant.DOCTOR_NOT_FOUND, HttpStatus.NOT_FOUND));

        return getDoctorDTO(doctor);
    }

    @Transactional
    public String updateDoctor(Doctor existingDoctor, DoctorReqDTO doctorReqDTO) throws IOException {
        ValidatorHelper.validateObject(doctorReqDTO);

        ObjectMapperUtil.safeCopyObjectAndIgnore(doctorReqDTO, existingDoctor, List.of("id", "defunct", "registrationNo"));

        if (doctorReqDTO.getLanguageId() != null && !doctorReqDTO.getLanguageId().isEmpty()) {
            existingDoctor.setLanguage(languageMasterRepository.findAllByIdAndDefunct(doctorReqDTO.getLanguageId(), false).orElseThrow(() -> new FrontDeskException(ErrorConstant.LANGUAGE_NOT_FOUND, HttpStatus.NOT_FOUND)));
        }


        if (doctorReqDTO.getDepartmentId() != null && !doctorReqDTO.getDepartmentId().isEmpty() && doctorReqDTO.getDepartmentId() != existingDoctor.getDepart().getId()) {
            existingDoctor.setDepart(specialityMasterRepository.findByIdAndDefunct(doctorReqDTO.getDepartmentId(), false).orElseThrow(() -> new FrontDeskException(ErrorConstant.DEPARTMENT_NOT_FOUND, HttpStatus.NOT_FOUND)));
        }

        if (doctorReqDTO.getSpecialtyId() != null && !doctorReqDTO.getSpecialtyId().isEmpty() && doctorReqDTO.getSpecialtyId() != existingDoctor.getSpecialty().getId()) {
            existingDoctor.setSpecialty(specialityMasterRepository.findByIdAndDefunct(doctorReqDTO.getSpecialtyId(), false).orElseThrow(() -> new FrontDeskException(ErrorConstant.SPECIALITY_NOT_FOUND, HttpStatus.NOT_FOUND)));
        }

        List<DoctorDocument> doctorDocumentList = new ArrayList<>();
        if (doctorReqDTO.getDoctorDocumentDTOS() != null && !doctorReqDTO.getDoctorDocumentDTOS().isEmpty()) {
            doctorReqDTO.getDoctorDocumentDTOS().stream().map(doc ->
            {
                byte[] decodeImage = Base64.getDecoder().decode(doc.getDocFile().replaceAll("\\s+", ""));
                DoctorDocument doctorDocument = new DoctorDocument();
                doctorDocument.setDocFile(decodeImage);
                doctorDocument.setDocName(doc.getDocName());
                doctorDocumentList.add(doctorDocument);
                return doctorDocumentList;
            }).toList();
        }
        existingDoctor.setDoctorDocument(doctorDocumentList);

        if (doctorReqDTO.getPermanentAddress() != null) {
            if (doctorReqDTO.getPermanentAddress().getId() != null && doctorReqDTO.getPermanentAddress().getId().isEmpty()) {
                Address perAddress = addressMasterRepository.findById(doctorReqDTO.getPermanentAddress().getId()).orElseThrow(() -> new FrontDeskException(ErrorConstant.ADDRESS_NOT_FOUND, HttpStatus.NOT_FOUND));
                ObjectMapperUtil.safeCopyObjectAndIgnore(doctorReqDTO.getPermanentAddress(), perAddress, List.of("id"));
                existingDoctor.setPermanentAddress(addressMasterRepository.save(perAddress));
            } else {
                Address permanentAddress = addressMasterRepository.save(ObjectMapperUtil.copyObject(doctorReqDTO.getPermanentAddress(), Address.class));
                existingDoctor.setPermanentAddress(permanentAddress);
            }

        }

        if (doctorReqDTO.getOrgIds() != null && !doctorReqDTO.getOrgIds().isEmpty()) {
            List<Organization> organizations = organizationMasterRepository.findAllByIDAndDefunct(doctorReqDTO.getOrgIds(), false).orElseThrow(() -> new FrontDeskException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
            existingDoctor.setOrganization(organizations);
        }

        if (doctorReqDTO.getRegistrationNo() != null && !doctorReqDTO.getRegistrationNo().isEmpty()) {
            Optional<Doctor> existingDoctorByRegNo = doctorRepository.findByRegistrationNoAndDefunct(doctorReqDTO.getRegistrationNo(), false);
            if (existingDoctorByRegNo.isPresent() && !existingDoctorByRegNo.get().getId().equals(existingDoctor.getId())) {
                logger.info("Doctor with registration number {} already exists", doctorReqDTO.getRegistrationNo());
                throw new FrontDeskException(ErrorConstant.DOCTOR_ALREADY_EXISTS + " " + doctorReqDTO.getRegistrationNo(), HttpStatus.BAD_REQUEST);
            }
            existingDoctor.setRegistrationNo(doctorReqDTO.getRegistrationNo());
        } else {
            existingDoctor.setRegistrationNo(null);
        }
        existingDoctor.setDoctorType(doctorReqDTO.getDoctorType());
        List<DoctorTariff> doctorTariffs=null;
        if (doctorReqDTO.getDoctorTariffDTOS() != null && !doctorReqDTO.getDoctorTariffDTOS().isEmpty()) {
            doctorTariffs = doctorReqDTO.getDoctorTariffDTOS().stream().map(t -> {
                DoctorTariff doctorTariff = new DoctorTariff();
                Tariff tariff = tariffRepository.findByIdAndDefunct(t.getTariffId(), false)
                        .orElseThrow(() -> new FrontDeskException(ErrorConstant.TARIFF_NOT_FOUND, HttpStatus.NOT_FOUND));
                doctorTariff.setTariff(tariff);
                doctorTariff.setTariffName(tariff.getName());
                doctorTariff.setFirstRate(t.getFirstRate());
                doctorTariff.setSecondRate(t.getSecondRate());
                return doctorTariff;
            }).toList();
        }
        existingDoctor.setDoctorTariff(doctorTariffs);
        double totalTariffFirstRate = parseAndSumRates(doctorTariffs, DoctorTariff::getFirstRate);
        double totalTariffSecondRate = parseAndSumRates(doctorTariffs, DoctorTariff::getSecondRate);

        double finalFirstVisitRate = (doctorReqDTO.getFirstVisitRate() != null ? doctorReqDTO.getFirstVisitRate() : 0.0) + totalTariffFirstRate;
        double finalSecondVisitRate = (doctorReqDTO.getSecondVisitRate() != null ? doctorReqDTO.getSecondVisitRate() : 0.0) + totalTariffSecondRate;

        existingDoctor.setTotalFirstVisitRate(finalFirstVisitRate);
        existingDoctor.setTotalSecondVisitRate(finalSecondVisitRate);


        Doctor savedDoctor = doctorCacheService.saveDoctor(existingDoctor);


        logger.info("Doctor with ID {} updated successfully", savedDoctor.getId());

        return savedDoctor.getId();
    }

    @Override
    public void deleteDoctor(String id) {
        Doctor doctor = doctorRepository.findByIdAndDefunct(id, false).orElseThrow(() -> new FrontDeskException(ErrorConstant.DOCTOR_NOT_FOUND, HttpStatus.NOT_FOUND));

        logger.info("Deleting doctor with ID: {}", id);

        doctor.setDefunct(true);
        doctorCacheService.saveDoctor(doctor);

        logger.info("deleted doctor with ID: {}", id);
    }

    @Override
    public List<AppointmentDocInfoDTO> getAllDoctorsBySpeciality(String speId, String orgId) {
        logger.info("Fetching all doctors by specialty ID: {}", speId);
        List<AppointmentDocInfoDTO> bySpecialtityIdAndDefunct = doctorCacheService.getDoctorsBySpeciality(speId, false, orgId);
        return bySpecialtityIdAndDefunct.stream().peek(map -> map.setSpecialtyId(speId)).toList();

    }

    @Override
    public List<AppointmentDocInfoDTO> fetchAllDoctor(String orgId) {
        logger.info("Fetching all doctors By Specialty");
        return doctorCacheService.getDoctorsByOrg(false, orgId);
    }

    @Override
    public List<AppointmentDocInfoDTO> getDoctorByDoctorType(DoctorType doctorType, String orgId) {
       return doctorCacheService.getDoctorsByType(false,orgId,doctorType);
    }
}
