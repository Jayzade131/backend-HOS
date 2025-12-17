package com.org.hosply360.service.OPD.impl;

import com.org.hosply360.constant.Enums.AppointmentStatus;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.OPD.Appointment;
import com.org.hosply360.dao.OPD.DocAppointmentTimetable;
import com.org.hosply360.dao.OPD.WeeklySchedule;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.OPDDTO.OpTimetableDTO;
import com.org.hosply360.dto.OPDDTO.WeeklyScheduleDTO;
import com.org.hosply360.exception.OPDException;
import com.org.hosply360.repository.OPDRepo.AppointmentRepository;
import com.org.hosply360.repository.OPDRepo.DocAppointmentTimetableRepository;
import com.org.hosply360.repository.frontDeskRepo.DoctorMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.OPD.DocAppointmentTimetableService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocAppointmentTimetableServiceImpl implements DocAppointmentTimetableService {

    private static final Logger logger = LoggerFactory.getLogger(DocAppointmentTimetableServiceImpl.class);

    private final DocAppointmentTimetableRepository timetableRepository;
    private final DoctorMasterRepository doctorRepository;
    private final OrganizationMasterRepository organizationRepository;
    private final AppointmentRepository appointmentRepository;

    private boolean isValidSession(String from, String to) {
        return from != null && !from.isBlank() && !"--".equals(from)
                && to != null && !to.isBlank() && !"--".equals(to);
    }

    private boolean isValidTimeRange(String from, String to) {
        if (!isValidSession(from, to)) {
            return false;
        }

        LocalTime start = LocalTime.of(
                Integer.parseInt(from.substring(0, 2)),
                Integer.parseInt(from.substring(2))
        );
        LocalTime end = LocalTime.of(
                Integer.parseInt(to.substring(0, 2)),
                Integer.parseInt(to.substring(2))
        );

        long durationMinutes = end.isAfter(start)
                ? Duration.between(start, end).toMinutes()
                : Duration.between(start.atDate(LocalDate.now()), end.atDate(LocalDate.now().plusDays(1))).toMinutes();

        return durationMinutes > 0 && durationMinutes <= 720;
    }

    private OpTimetableDTO convertToDTO(DocAppointmentTimetable timetable) {
        OpTimetableDTO dto = ObjectMapperUtil.copyObject(timetable, OpTimetableDTO.class);
        List<String> daysOrder = List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");

        Map<String, WeeklyScheduleDTO> scheduleMap = daysOrder.stream()
                .filter(day -> timetable.getWeeklySchedule().containsKey(day))
                .collect(Collectors.toMap(
                        day -> day,
                        day -> ObjectMapperUtil.copyObject(timetable.getWeeklySchedule().get(day), WeeklyScheduleDTO.class),
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));

        dto.setWeeklySchedule(scheduleMap);
        dto.setDoctorId(timetable.getDoctorId() != null ? timetable.getDoctorId().getId() : null);
        dto.setDoctorName(timetable.getDoctorId() != null ? timetable.getDoctorId().getFirstName() : null);
        dto.setSpecialtyId(timetable.getSpecialtyId() != null ? timetable.getSpecialtyId().getId() : null);
        dto.setSpecialtyName(timetable.getSpecialtyId() != null ? timetable.getSpecialtyId().getDepartment() : null);
        dto.setOrganizationId(timetable.getOrganizationId() != null ? timetable.getOrganizationId().getId() : null);
        return dto;
    }

    @Override
    @Transactional
    public OpTimetableDTO createOpTimetable(OpTimetableDTO dto) {
        if (dto == null || dto.getDoctorId() == null || dto.getSpecialtyId() == null) {
            throw new OPDException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        Organization organization = organizationRepository.findByIdAndDefunct(dto.getOrganizationId(), false)
                .orElseThrow(() -> new OPDException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new OPDException(ErrorConstant.DOCTOR_NOT_FOUND, HttpStatus.NOT_FOUND));

        DocAppointmentTimetable existing = timetableRepository.findByDoctorIdAndDefunctAndOrg(dto.getDoctorId(), false, dto.getOrganizationId());
        if (existing != null) {
            throw new OPDException(ErrorConstant.DUPLICATE_DOCTOR_SCHEDULE, HttpStatus.CONFLICT);
        }

        Map<String, WeeklyScheduleDTO> weeklySchedule = dto.getWeeklySchedule();
        if (weeklySchedule == null || weeklySchedule.isEmpty()) {
            throw new OPDException(ErrorConstant.WEEKLY_SCHEDULE_MUST_NOT_BE_EMPTY, HttpStatus.BAD_REQUEST);
        }

        Map<String, WeeklySchedule> validScheduleMap = weeklySchedule.entrySet().stream()
                .peek(entry -> {
                    WeeklyScheduleDTO schedule = entry.getValue();
                    if (!isValidTimeRange(schedule.getMorningSessionFrom(), schedule.getMorningSessionTo())) {
                        throw new OPDException(ErrorConstant.SESSION_MORNING_START_BEFORE_END + " " + entry.getKey(),
                                HttpStatus.BAD_REQUEST);
                    }
                    if (!isValidTimeRange(schedule.getAfternoonSessionFrom(), schedule.getAfternoonSessionTo())) {
                        throw new OPDException(ErrorConstant.SESSION_AFTERNOON_START_BEFORE_END + " " + entry.getKey(),
                                HttpStatus.BAD_REQUEST);
                    }
                    if (!isValidTimeRange(schedule.getEveningSessionFrom(), schedule.getEveningSessionTo())) {
                        throw new OPDException(ErrorConstant.SESSION_EVENING_START_BEFORE_END + " " + entry.getKey(),
                                HttpStatus.BAD_REQUEST);
                    }
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ObjectMapperUtil.copyObject(entry.getValue(), WeeklySchedule.class)
                ));

        DocAppointmentTimetable entity = ObjectMapperUtil.copyObject(dto, DocAppointmentTimetable.class);
        entity.setDoctorId(doctor);
        entity.setSpecialtyId(doctor.getSpecialty());
        entity.setOrganizationId(organization);
        entity.setDefunct(false);
        entity.setWeeklySchedule(validScheduleMap);

        DocAppointmentTimetable saved = timetableRepository.save(entity);
        logger.info("OP Timetable created successfully with ID {}", saved.getId());

        return convertToDTO(saved);
    }

    @Override
    public List<OpTimetableDTO> getAllOpTimetables(String organizationId) {
        logger.info("Fetching all OP Timetables for org={}", organizationId);
        return timetableRepository.findAllByDefunctAndOrgId(false, organizationId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional
    public OpTimetableDTO updateOpTimetable(OpTimetableDTO dto) {
        if (dto == null || dto.getId() == null || dto.getDoctorId() == null || dto.getSpecialtyId() == null) {
            throw new OPDException(ErrorConstant.DOCTOR_AND_SPECIALITY_REQUIRED, HttpStatus.BAD_REQUEST);
        }

        DocAppointmentTimetable existing = timetableRepository.findById(dto.getId())
                .orElseThrow(() -> new OPDException(ErrorConstant.OP_TIMETABLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new OPDException(ErrorConstant.DOCTOR_NOT_FOUND, HttpStatus.NOT_FOUND));

        Map<String, WeeklyScheduleDTO> newScheduleDTO = dto.getWeeklySchedule();
        if (newScheduleDTO == null || newScheduleDTO.isEmpty()) {
            throw new OPDException(ErrorConstant.WEEKLY_SCHEDULE_MUST_NOT_BE_EMPTY, HttpStatus.BAD_REQUEST);
        }

        Map<String, WeeklySchedule> updatedSchedule = newScheduleDTO.entrySet().stream()
                .peek(entry -> {
                    WeeklyScheduleDTO schedule = entry.getValue();
                    if (!isValidTimeRange(schedule.getMorningSessionFrom(), schedule.getMorningSessionTo())) {
                        throw new OPDException(ErrorConstant.SESSION_MORNING_START_BEFORE_END + " " + entry.getKey(),
                                HttpStatus.BAD_REQUEST);
                    }
                    if (!isValidTimeRange(schedule.getAfternoonSessionFrom(), schedule.getAfternoonSessionTo())) {
                        throw new OPDException(ErrorConstant.SESSION_AFTERNOON_START_BEFORE_END + " " + entry.getKey(),
                                HttpStatus.BAD_REQUEST);
                    }
                    if (!isValidTimeRange(schedule.getEveningSessionFrom(), schedule.getEveningSessionTo())) {
                        throw new OPDException(ErrorConstant.SESSION_EVENING_START_BEFORE_END + " " + entry.getKey(),
                                HttpStatus.BAD_REQUEST);
                    }
                })
                .filter(entry -> {
                    WeeklyScheduleDTO schedule = entry.getValue();
                    return isValidSession(schedule.getMorningSessionFrom(), schedule.getMorningSessionTo())
                            || isValidSession(schedule.getAfternoonSessionFrom(), schedule.getAfternoonSessionTo())
                            || isValidSession(schedule.getEveningSessionFrom(), schedule.getEveningSessionTo());
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ObjectMapperUtil.copyObject(entry.getValue(), WeeklySchedule.class)
                ));

        if (updatedSchedule.isEmpty()) {
            throw new OPDException(ErrorConstant.AT_LEAST_ONE_VALID_SCHEDULE_IS_REQUIRED, HttpStatus.BAD_REQUEST);
        }

        existing.setDoctorId(doctor);
        existing.setSpecialtyId(doctor.getSpecialty());
        existing.setWeeklySchedule(updatedSchedule);

        DocAppointmentTimetable updated = timetableRepository.save(existing);
        logger.info("OP Timetable updated successfully with ID {}", updated.getId());

        return convertToDTO(updated);
    }

    @Override
    public void deleteOpTimetable(String id) {
        logger.info("Deleting OP Timetable with ID {}", id);
        DocAppointmentTimetable timetable = timetableRepository.findById(id)
                .orElseThrow(() -> new OPDException(ErrorConstant.OP_TIMETABLE_NOT_FOUND, HttpStatus.NOT_FOUND));

        timetable.setDefunct(true);
        timetableRepository.save(timetable);
        logger.info("OP Timetable deleted successfully with ID {}", id);

        this.updateFutureAppointmentsToPending(timetable.getDoctorId().getId());
    }

    @Async("taskExecutor")
    public void updateFutureAppointmentsToPending(String doctorId) {
        List<Appointment> futureAppointments = appointmentRepository
                .findByDoctor_IdAndAppointmentDateAfterAndDefunctFalse(
                        doctorId, LocalDateTime.now());

        List<Appointment> toUpdate = futureAppointments.stream()
                .filter(appt -> appt.getStatus() == AppointmentStatus.SCHEDULED
                        || appt.getStatus() == AppointmentStatus.RESCHEDULED)
                .peek(appt -> appt.setStatus(AppointmentStatus.PENDING))
                .toList();

        if (!toUpdate.isEmpty()) {
            appointmentRepository.saveAll(toUpdate);
            logger.info("Updated {} future appointments to PENDING for doctorId={}", toUpdate.size(), doctorId);
        }
    }

    @Override
    public OpTimetableDTO getOpTimetableByDoctorId(String doctorId, String organizationId) {
        DocAppointmentTimetable timetable = timetableRepository.findByDoctorIdAndDefunctAndOrg(doctorId, false, organizationId);
        if (timetable == null) {
            throw new OPDException(ErrorConstant.OP_TIMETABLE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return convertToDTO(timetable);
    }
}
