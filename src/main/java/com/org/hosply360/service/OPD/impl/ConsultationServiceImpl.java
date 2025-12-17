package com.org.hosply360.service.OPD.impl;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.OPD.Appointment;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.OPDDTO.AppointmentDocInfoDTO;
import com.org.hosply360.dto.OPDDTO.AppointmentSummaryDTO;
import com.org.hosply360.dto.OPDDTO.ConsultationAppointmentDTO;
import com.org.hosply360.exception.OPDException;
import com.org.hosply360.repository.OPDRepo.AppointmentRepository;
import com.org.hosply360.repository.OPDRepo.CustomAppointmentRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.OPD.ConsultationService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final AppointmentRepository appointmentRepository;
    private final OrganizationMasterRepository organizationMasterRepository;
    private final CustomAppointmentRepository customAppointmentRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private static final Logger logger = LoggerFactory.getLogger(ConsultationServiceImpl.class);

    public Organization validateOrganization(String orgId) {
        if (orgId == null || orgId.isEmpty()) {
            logger.info("Organization ID is missing or empty. Returning null organization.");
            return null;
        } else {
            logger.info("Validating organization with ID: {}", orgId);

            return organizationMasterRepository.findByIdAndDefunct(orgId, false)
                    .orElseThrow(() -> new OPDException(
                            ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        }
    }
    private ConsultationAppointmentDTO mapToDTO(Appointment appointment, String lastVisit, long totalVisits) {
        ConsultationAppointmentDTO dto = ObjectMapperUtil.copyObject(appointment, ConsultationAppointmentDTO.class);

        dto.setId(appointment.getId());
        dto.setAId(appointment.getAId());


        if (appointment.getDoctor() != null) {
            dto.setDoctorId(appointment.getDoctor().getId());

            AppointmentDocInfoDTO docInfo = ObjectMapperUtil.copyObject(appointment.getDoctor(), AppointmentDocInfoDTO.class);
            if (appointment.getSpecialty() != null) {
                docInfo.setSpecialtyName(appointment.getSpecialty().getDescription());
            } else {
                docInfo.setSpecialtyName(ApplicationConstant.NA);
            }
            dto.setConsultant(docInfo.getFirstName() + " - " + docInfo.getSpecialtyName());
        } else {
            dto.setDoctorId(null);
            dto.setConsultant(ApplicationConstant.NA);
        }


        if (appointment.getPatient() != null && appointment.getPatient().getPatientPersonalInformation() != null) {
            String firstName = appointment.getPatient().getPatientPersonalInformation().getFirstName();
            String lastName = appointment.getPatient().getPatientPersonalInformation().getLastName();
            dto.setPatientName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));

            String dob = appointment.getPatient().getPatientPersonalInformation().getDateOfBirth();
            String age = calculateAge(dob);
            String gender = appointment.getPatient().getPatientPersonalInformation().getGender();
            dto.setAgeGender((age != null ? age : ApplicationConstant.NA) + "/" + (gender != null ? gender :ApplicationConstant.NA));
        } else {
            dto.setPatientName(ApplicationConstant.NA);
            dto.setAgeGender(ApplicationConstant.NA);
        }


        if (appointment.getPatient() != null && appointment.getPatient().getPatientContactInformation() != null) {
            dto.setMobile(appointment.getPatient().getPatientContactInformation().getPrimaryPhone());
        } else {
            dto.setMobile(ApplicationConstant.NA);
        }

        dto.setPId(appointment.getPatient() != null ? appointment.getPatient().getPId() : null);
        dto.setPatientId(appointment.getPatient() != null ? appointment.getPatient().getId() : null);
        dto.setAppointmentType(appointment.getAppointmentType());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStatus(appointment.getStatus());
        dto.setTotalVisit((int) totalVisits);
        dto.setLastVisit(lastVisit);

        return dto;
    }

    private String calculateAge(String dobString) {
        if (dobString == null || dobString.isEmpty()) return ApplicationConstant.NA;
        try {
            LocalDate dob = LocalDate.parse(dobString);
            return String.valueOf(Period.between(dob, LocalDate.now()).getYears());
        } catch (Exception e) {
            logger.warn("Invalid DOB format: {}", dobString);
            return ApplicationConstant.NA;
        }
    }

    @Override
    public ConsultationAppointmentDTO getConsultationDetails(String appointmentId, String orgId) {
        logger.info("Fetching consultation appointment for ID: {} and org: {}", appointmentId, orgId);
        validateOrganization(orgId);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .filter(app -> app.getOrg().getId().equals(orgId))
                .orElseThrow(() -> new OPDException(
                        ErrorConstant.APPOINTMENT_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<Appointment> previousVisits = appointmentRepository.findLastVisitExcludingCurrent(
                appointment.getPatient().getId(), orgId, appointment.getId()
        );

        String lastVisit = previousVisits.stream()
                .findFirst()
                .map(a -> a.getAppointmentDate().format(DATE_FORMATTER))
                .orElse(ApplicationConstant.NA);

        long totalVisits = appointmentRepository.countByPatientIdAndOrgId(
                appointment.getPatient().getId(), orgId
        );

        ConsultationAppointmentDTO dto = mapToDTO(appointment, lastVisit, totalVisits);

        logger.info("Consultation details successfully fetched for appointment ID {}", appointmentId);
        return dto;
    }


    @Override
    public List<AppointmentSummaryDTO> getAllConsultationDetailsForOrg(String orgId, LocalDateTime fromDate, LocalDateTime toDate) {
        logger.info("Fetching consultation appointment summary via aggregation for org: {}", orgId);
        validateOrganization(orgId);
        return customAppointmentRepository.findAppointmentSummary(orgId, fromDate, toDate);
    }
}
