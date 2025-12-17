package com.org.hosply360.service.OPD.impl;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.Enums.AppointmentStatus;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.customRepositories.AppointmentCustomRepository;
import com.org.hosply360.dao.OPD.Appointment;
import com.org.hosply360.dao.OPD.DocAppointmentTimetable;
import com.org.hosply360.dao.OPD.InvoiceItems;
import com.org.hosply360.dao.OPD.OPDInvoice;
import com.org.hosply360.dao.OPD.WeeklySchedule;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.frontDeskDao.PatientPersonalInformation;
import com.org.hosply360.dao.globalMaster.BillingItem;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.OPDDTO.AppointmentDTO;
import com.org.hosply360.dto.OPDDTO.AppointmentDocInfoDTO;
import com.org.hosply360.dto.OPDDTO.AppointmentReqDTO;
import com.org.hosply360.dto.OPDDTO.AppointmentWithInvoiceDTO;
import com.org.hosply360.dto.OPDDTO.PagedResult;
import com.org.hosply360.dto.authDTO.AppResponseDTO;
import com.org.hosply360.exception.OPDException;
import com.org.hosply360.repository.OPDRepo.AppointmentRepository;
import com.org.hosply360.repository.OPDRepo.AppointmentTimeSlot;
import com.org.hosply360.repository.OPDRepo.DocAppointmentTimetableRepository;
import com.org.hosply360.repository.OPDRepo.OPDInvoiceRepository;
import com.org.hosply360.repository.globalMasterRepo.BillingItemRepository;
import com.org.hosply360.service.OPD.AppointmentService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.Others.SequenceGeneratorService;
import com.org.hosply360.util.Others.TimeUtil;
import com.org.hosply360.util.encryptionUtil.EncryptionUtil;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;
    private final DocAppointmentTimetableRepository docAppointmentTimetableRepo;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final OPDInvoiceRepository opdInvoiceRepository;
    private final BillingItemRepository billingItemRepository;
    private final AppointmentCustomRepository appointmentCustomRepository;
    private final EntityFetcherUtil entityFetcherUtil;

    private String getAge(PatientPersonalInformation personalInfo) {
        if (personalInfo == null) {
            return "";
        }

        String dobStr = personalInfo.getDateOfBirth();
        if (!StringUtils.hasText(dobStr)) {
            return "0";
        }

        try {
            LocalDate dob = LocalDate.parse(dobStr);
            return String.valueOf(Period.between(dob, LocalDate.now()).getYears());
        } catch (DateTimeParseException e) {
            logger.warn("Invalid DOB format: {}", dobStr);
            return "0";
        }
    }


    private void validateAppointmentSession(String sessionLabel, String fromTimeStr, String toTimeStr, String startTimeStr, String endTimeStr) {


        LocalTime startTime = TimeUtil.parseTime(startTimeStr);
        LocalTime endTime = TimeUtil.parseTime(endTimeStr);
        LocalTime sessionFrom = TimeUtil.parseTime(fromTimeStr);
        LocalTime sessionTo = TimeUtil.parseTime(toTimeStr);


        if (endTime.isBefore(startTime) || endTime.equals(startTime)) {
            throw new OPDException(ErrorConstant.TIME_CONFLICT_END_TIME, HttpStatus.CONFLICT);
        }

        if (startTime.isBefore(sessionFrom) || endTime.isAfter(sessionTo)) {
            logger.info("{} session: Time is outside session bounds.", sessionLabel);
            throw new OPDException(ErrorConstant.TIME_BOUND, HttpStatus.BAD_REQUEST);
        }
        logger.info("{} session: Time is within session bounds.", sessionLabel);
    }

    private void checkAppointmentConflict(String doctorId, LocalDate appointmentDate,
                                          String startTimeStr, String endTimeStr, String excludeAppointmentId) {

        ObjectId doctorObjectId = new ObjectId(doctorId);
        ObjectId excludeId = (excludeAppointmentId != null) ? new ObjectId(excludeAppointmentId) : null;

        Date startOfDay = Date.from(appointmentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endOfDay = Date.from(appointmentDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<AppointmentTimeSlot> existingSlots = (excludeId != null)
                ? appointmentRepository.findTimeSlotsForConflictExcludingId(doctorObjectId, startOfDay, endOfDay, excludeId)
                : appointmentRepository.findTimeSlotsForConflict(doctorObjectId, startOfDay, endOfDay);

        LocalTime reqStart = TimeUtil.parseTime(startTimeStr);
        LocalTime reqEnd = TimeUtil.parseTime(endTimeStr);

        boolean conflictExists = existingSlots.stream().anyMatch(slot -> {
            LocalTime existStart = TimeUtil.parseTime(slot.getStartTime());
            LocalTime existEnd = TimeUtil.parseTime(slot.getEndTime());
            return !reqEnd.isBefore(existStart) && !reqStart.isAfter(existEnd);

        });

        if (conflictExists) {
            logger.warn("Appointment conflict detected for Doctor={}, Date={}, {}-{}", doctorId, appointmentDate, startTimeStr, endTimeStr);
            throw new OPDException(ErrorConstant.APPOINTMENT_CONFLICT, HttpStatus.CONFLICT);
        }
    }


    public static AppointmentDTO getAppointmentDTO(Appointment appointment) {
        if (appointment == null) return null;

        AppointmentDTO dto = ObjectMapperUtil.copyObject(appointment, AppointmentDTO.class);

        dto.setStatus(appointment.getStatus() != null ? appointment.getStatus().getName() : "");

        if (Objects.nonNull(appointment.getDoctor())) {
            dto.setDoctorId(appointment.getDoctor().getId());
        }

        if (Objects.nonNull(appointment.getSpecialty())) {
            dto.setSpecialtyId(appointment.getSpecialty().getId());
        }

        if (Objects.nonNull(appointment.getPatient())) {
            dto.setPatientId(appointment.getPatient().getId());
            dto.setPId(appointment.getPatient().getPId());
            String firstname = appointment.getPatient().getPatientPersonalInformation().getFirstName();
            String lastname = appointment.getPatient().getPatientPersonalInformation().getLastName();
            dto.setPatientNumber(appointment.getPatient().getPatientContactInformation().getPrimaryPhone());
            dto.setPatientName("%s %s".formatted(firstname, lastname));
        }

        if (Objects.nonNull(appointment.getOrg())) {
            dto.setOrgId(appointment.getOrg().getId());
        }

        if (Objects.nonNull(appointment.getDoctor()) && Objects.nonNull(appointment.getSpecialty())) {
            AppointmentDocInfoDTO docInfo = ObjectMapperUtil.copyObject(appointment.getDoctor(), AppointmentDocInfoDTO.class);
            docInfo.setSpecialtyName(appointment.getSpecialty().getDescription());
            dto.setDocInfo(docInfo);
        }

        return dto;
    }


    @Override
    @Transactional
    public String createAppointment(AppointmentReqDTO request) {

        if (request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new OPDException(ErrorConstant.APPOINTMENT_DATE_CANNOT_BE_BEFORE_TODAY, HttpStatus.BAD_REQUEST);
        }
        logger.info("Creating Appointment for Patient={}, Doctor={}", request.getPatientId(), request.getDoctorId());

        if (!StringUtils.hasText(request.getOrgId()) || !StringUtils.hasText(request.getPatientId())) {
            throw new OPDException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        Patient patient = entityFetcherUtil.getPatientOrThrow(request.getPatientId());
        Organization org = entityFetcherUtil.getOrganizationOrThrow(request.getOrgId());
        Doctor doctor = entityFetcherUtil.getDoctorOrThrow(request.getDoctorId());

        Appointment appointment = request.isWalkIn()
                ? offlineAppointments(request, doctor, patient, org, null)
                : onlineAppointment(request, doctor, patient, org, null);

        long seqNum = sequenceGeneratorService.generateAppointmentSequence(ApplicationConstant.APPOINTMENT_SEQ);
        appointment.setAId(String.format(ApplicationConstant.AID_FORMAT, seqNum));

        Appointment saved = appointmentRepository.save(appointment);
        return saved.getId();
    }



    private Appointment onlineAppointment(AppointmentReqDTO request,
                                          Doctor doctor,
                                          Patient patient,
                                          Organization org,
                                          Appointment excludeAppointment) {

        if (!StringUtils.hasText(request.getDoctorId())
                || !StringUtils.hasText(request.getSpecialtyId())
                || request.getAppointmentDate() == null
                || !StringUtils.hasText(request.getStartTime())
                || !StringUtils.hasText(request.getEndTime())
                || !StringUtils.hasText(request.getSession())) {
            throw new OPDException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        LocalDate appointmentDate = request.getAppointmentDate();

        LocalTime startTimeCheck = TimeUtil.parseTime(request.getStartTime());
        LocalDateTime appointmentStart = TimeUtil.toDateTime(appointmentDate, request.getStartTime());


        if (!appointmentStart.isAfter(LocalDateTime.now())) {
            throw new OPDException(ErrorConstant.APPOINTMENT_DATE_CANNOT_BE_BEFORE_TODAY, HttpStatus.BAD_REQUEST);
        }

        String day = appointmentDate.getDayOfWeek().name();
        DocAppointmentTimetable timetable = docAppointmentTimetableRepo.findByDoctorIdAndDefunct(request.getDoctorId(), false);
        if (timetable == null) {
            throw new OPDException(ErrorConstant.SCHEDULE_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        WeeklySchedule matchedSchedule = timetable.getWeeklySchedule().get(day);
        if (Objects.isNull(matchedSchedule)) {
            throw new OPDException(ErrorConstant.SCHEDULE_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }

        logger.info("Matched Schedule for {}: {}", day, matchedSchedule);

        if (ApplicationConstant.MORNING.equalsIgnoreCase(request.getSession())) {
            validateAppointmentSession(ApplicationConstant.MORNING, matchedSchedule.getMorningSessionFrom(),
                    matchedSchedule.getMorningSessionTo(), request.getStartTime(), request.getEndTime());
        } else if (ApplicationConstant.AFTERNOON.equalsIgnoreCase(request.getSession())) {
            validateAppointmentSession(ApplicationConstant.AFTERNOON, matchedSchedule.getAfternoonSessionFrom(),
                    matchedSchedule.getAfternoonSessionTo(), request.getStartTime(), request.getEndTime());
        } else if (ApplicationConstant.EVENING.equalsIgnoreCase(request.getSession())) {
            validateAppointmentSession(ApplicationConstant.EVENING, matchedSchedule.getEveningSessionFrom(),
                    matchedSchedule.getEveningSessionTo(), request.getStartTime(), request.getEndTime());
        }

        String excludeId = (excludeAppointment != null) ? excludeAppointment.getId() : null;
        checkAppointmentConflict(request.getDoctorId(), appointmentDate,
                request.getStartTime(), request.getEndTime(), excludeId);


        AppointmentStatus status = setAppointmentStatus(request, excludeAppointment);
        return Appointment.builder()
                .appointmentDate(appointmentDate.atTime(startTimeCheck))
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isWalkIn(false)
                .session(request.getSession())
                .doctor(doctor)
                .specialty(doctor.getSpecialty())
                .patient(patient)
                .pId(patient.getPId())
                .org(org)
                .defunct(false)
                .appointmentType(request.getAppointmentType())
                .status(status)
                .appointmentDay(request.getAppointmentDate().toString())
                .tokenNumber(null)
                .build();
    }

    private static AppointmentStatus setAppointmentStatus(AppointmentReqDTO request, Appointment excludeAppointment) {
        AppointmentStatus status;
        if (excludeAppointment != null) {
            boolean startTimeChanged = !Objects.equals(request.getStartTime(), excludeAppointment.getStartTime());
            boolean endTimeChanged = !Objects.equals(request.getEndTime(), excludeAppointment.getEndTime());
            boolean dateChanged = !Objects.equals(request.getAppointmentDate(), excludeAppointment.getAppointmentDate().toLocalDate());

            if (startTimeChanged || endTimeChanged || dateChanged) {
                status = AppointmentStatus.RESCHEDULED;
            } else {
                status = (request.getStatus() != null ? request.getStatus() : excludeAppointment.getStatus());
            }
        } else {
            status = AppointmentStatus.PENDING;
        }
        return status;
    }


    private Appointment offlineAppointments(AppointmentReqDTO request, Doctor doctor, Patient patient, Organization org, Long existingToken) {
        LocalTime startTime = LocalTime.now();
        LocalTime endTime = startTime.plusMinutes(15);
        String appointmentDay = request.getAppointmentDate().toString();

        // 🔹 Generate new token only if not passed (during creation)
        long token = existingToken != null
                ? existingToken
                : sequenceGeneratorService.getNextToken(request.getOrgId(), appointmentDay, request.isWalkIn());

        return Appointment.builder()
                .appointmentDate(request.getAppointmentDate().atTime(LocalTime.NOON))
                .startTime(TimeUtil.formatTime2(startTime))
                .endTime(TimeUtil.formatTime2(endTime))
                .isWalkIn(true)
                .session(request.getSession())
                .doctor(doctor)
                .specialty(doctor.getSpecialty())
                .patient(patient)
                .pId(patient.getPId())
                .org(org)
                .defunct(false)
                .appointmentType(request.getAppointmentType())
                .appointmentDay(appointmentDay)
                .tokenNumber((int) token)
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }


    @Override
    @Transactional
    public String updateAppointment(AppointmentReqDTO request) {
        if (!StringUtils.hasText(request.getId())) {
            throw new OPDException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        List<AppointmentStatus> invalidStatuses = Arrays.asList(
                AppointmentStatus.PAID,
                AppointmentStatus.COMPLETED
        );

        if (invalidStatuses.contains(request.getStatus())) {
            throw new OPDException(ErrorConstant.INVALID_STATUS, HttpStatus.BAD_REQUEST);
        }


        if (request.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new OPDException(ErrorConstant.APPOINTMENT_DATE_CANNOT_BE_BEFORE_TODAY, HttpStatus.BAD_REQUEST);
        }

        Appointment existing = entityFetcherUtil.getAppointmentOrThrow(request.getId(), request.getOrgId());
        logger.info("Updating Appointment ID={}, CurrentStatus={}", existing.getId(), existing.getStatus());

        Document doc = opdInvoiceRepository.findPaymentStatusByAppointmentId(request.getId());
        String isPaid = doc != null ? doc.getString(ApplicationConstant.PAYMENT_KEY) : null;

        if (AppointmentStatus.PAID.equals(existing.getStatus()) && StringUtils.hasText(isPaid)) {
            throw new OPDException(ErrorConstant.PAID_INVOICE, HttpStatus.BAD_REQUEST);
        }

        Doctor doctor = existing.getDoctor();
        if (!Objects.equals(existing.getDoctor().getId(), request.getDoctorId())) {
            doctor = entityFetcherUtil.getDoctorOrThrow(request.getDoctorId());
        }

        Patient patient = existing.getPatient();
        Organization org = existing.getOrg();
        if (!Objects.equals(existing.getOrg().getId(), request.getOrgId())) {
            org = entityFetcherUtil.getOrganizationOrThrow(request.getOrgId());
        }

        Appointment updated;

        if (request.isWalkIn()) {
            updated = offlineAppointments(request, doctor, patient, org, existing.getTokenNumber().longValue())
                    .toBuilder()
                    .id(existing.getId())
                    .status(request.getStatus() != null ? request.getStatus() : AppointmentStatus.SCHEDULED)
                    .build();

            logger.info("Walk-in appointment updated with same token: {}", existing.getTokenNumber());
        }

        else {
            Appointment fresh = onlineAppointment(request, doctor, patient, org, existing);
            Appointment.AppointmentBuilder builder = fresh.toBuilder().id(existing.getId());

            boolean startTimeChanged = !Objects.equals(request.getStartTime(), existing.getStartTime());
            boolean endTimeChanged = !Objects.equals(request.getEndTime(), existing.getEndTime());
            boolean dateChanged = !Objects.equals(request.getAppointmentDate(), existing.getAppointmentDate().toLocalDate());

            if (startTimeChanged || endTimeChanged || dateChanged) {
                builder.status(AppointmentStatus.RESCHEDULED);
            } else {
                builder.status(request.getStatus() != null ? request.getStatus() : existing.getStatus());
            }

            updated = builder.build();
            logger.info("Online appointment updated; reschedule status={}", updated.getStatus());
        }

        updated = appointmentRepository.save(updated);
        logger.info("Appointment updated successfully. ID={}, Status={}", updated.getId(), updated.getStatus());

        return updated.getId();
    }



    @Deprecated
    @Override
    public List<AppointmentDTO> getAppointmentsByPatientAndOrgExcludingCompleted(String patientId, String orgId) {
        logger.info("Fetching appointments for patient {} and org {} excluding 'completed' ones", patientId, orgId);

        List<Appointment> appointments = appointmentRepository
                .findAppointmentsByPatientIdAndOrgIdExcludingCompleted(patientId, orgId);

        if (appointments.isEmpty()) {
            throw new OPDException(ErrorConstant.APPOINTMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return appointments.stream()
                .map(AppointmentServiceImpl::getAppointmentDTO)
                .toList();
    }

    @Deprecated
    @Override
    public List<AppointmentDTO> getAppointments(String id, String doctorId, String orgId, LocalDate appointmentDate) {
        logger.info("Fetching appointments with params - id: {}, doctorId: {}, orgId: {}, date: {}", id, doctorId, orgId, appointmentDate);

        if (!StringUtils.hasText(orgId)) {
            throw new OPDException("Organization ID is required", HttpStatus.BAD_REQUEST);
        }

        List<Appointment> appointments;

        if (StringUtils.hasText(id)) {
            Appointment appointment = appointmentRepository.findByIdAndDefunct(id, orgId, false)
                    .orElseThrow(() -> new OPDException(ErrorConstant.APPOINTMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
            return List.of(getAppointmentDTO(appointment));
        }

        if (StringUtils.hasText(doctorId) && appointmentDate != null) {
            LocalDateTime startOfDay = appointmentDate.atStartOfDay();
            LocalDateTime endOfDay = appointmentDate.atTime(LocalTime.MAX);
            appointments = appointmentRepository.findByDoctorIdAndOrgIdAndAppointmentDateBetweenAndDefunct(
                    doctorId, orgId, startOfDay, endOfDay, false);
        } else if (!StringUtils.hasText(doctorId) && appointmentDate != null) {
            LocalDateTime startOfDay = appointmentDate.atStartOfDay();
            LocalDateTime endOfDay = appointmentDate.atTime(LocalTime.MAX);
            appointments = appointmentRepository.findByOrgIdAndAppointmentDateBetweenAndDefunct(
                    orgId, startOfDay, endOfDay, false);
        } else if (StringUtils.hasText(doctorId)) {
            appointments = appointmentRepository.findByDoctorIdAndOrgIdAndDefunct(doctorId, orgId, false);
        } else {
            appointments = appointmentRepository.findByOrg(orgId, false, Pageable.unpaged()).getContent();
        }

        if (appointments.isEmpty()) {
            throw new OPDException(ErrorConstant.APPOINTMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return appointments.stream()
                .map(AppointmentServiceImpl::getAppointmentDTO)
                .toList();
    }


    @Deprecated
    @Override
    public List<AppointmentDTO> getAppointmentsByOrgIdAndDateRange(String orgId, LocalDate fromDate, LocalDate toDate) {
        logger.info("Fetching appointments for org {}, from {} to {}", orgId, fromDate, toDate);

        if (!StringUtils.hasText(orgId)) {
            throw new OPDException("Organization ID is required", HttpStatus.BAD_REQUEST);
        }

        if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
            throw new OPDException("Invalid date range", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime startDateTime = fromDate.atStartOfDay();
        LocalDateTime endDateTime = toDate.atTime(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository.findByOrgIdAndAppointmentDateBetweenAndDefunct(
                orgId, startDateTime, endDateTime, false
        );

        if (appointments.isEmpty()) {
            throw new OPDException(ErrorConstant.APPOINTMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        return appointments.stream()
                .map(AppointmentServiceImpl::getAppointmentDTO)
                .toList();
    }


    @Override
    public void deleteAppointment(String id, String orgId) {
        logger.info("Deleting Appointment {}", id);
        Appointment appointment = entityFetcherUtil.getAppointmentOrThrow(id, orgId);
        appointment.setDefunct(true);
        appointmentRepository.save(appointment);
        logger.info("Appointment {} deleted successfully", id);
    }

    @Override
    @Transactional
    public void updateStatus(String id, String orgId, AppointmentStatus status) {
        logger.info("Updating status of Appointment {}", id);

        if (status.equals(AppointmentStatus.PAID)) {
            throw new OPDException(ErrorConstant.PAID_INVOICE, HttpStatus.BAD_REQUEST);
        }

        Appointment appointment = entityFetcherUtil.getAppointmentOrThrow(id, orgId);
        appointment.setStatus(status);
        if (status == AppointmentStatus.INPROGRESS) {
            appointment.setSessionStartTime(TimeUtil.formatTime(LocalTime.now()));
        } else if (status == AppointmentStatus.COMPLETED) {
            appointment.setSessionEndTime(TimeUtil.formatTime(LocalTime.now()));


            opdInvoiceRepository.findByAppointmentIdAndDefunctFalse(appointment.getId())
                    .ifPresentOrElse(invoice -> {
                        logger.info("Invoice already exists for appointment {}. Updating consultation charges if needed...", appointment.getId());
                        addConsultationChargesIfNotPresent(invoice, appointment);
                        opdInvoiceRepository.save(invoice);
                    }, () -> {
                        logger.info("No invoice found for appointment {}. Generating new invoice...", appointment.getId());
                        generateConsultationInvoiceFromAppointment(appointment);
                    });


        }

        appointmentRepository.save(appointment);
        logger.info("Appointment {} status updated to {}", id, status);
    }


    private void generateConsultationInvoiceFromAppointment(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();
        Organization org = appointment.getOrg();


        BillingItem billingItem = billingItemRepository
                .findByServiceCode(doctor.getId())
                .orElseThrow(() -> new OPDException(ErrorConstant.BILLING_ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));

        InvoiceItems consultationItem = InvoiceItems.builder()
                .billingItem(billingItem)
                .billingItemGroup(billingItem.getBillingItemGroup())
                .rate(billingItem.getRate())
                .quantity(1L)
                .amount(billingItem.getRate())
                .build();

        double rate = ApplicationConstant.IS_FOLLOWUP.equalsIgnoreCase(appointment.getAppointmentType())
                ? doctor.getSecondVisitRate()
                : billingItem.getRate();

        OPDInvoice invoice = OPDInvoice.builder()
                .patient(patient)
                .appointment(appointment)
                .consultant(appointment.getDoctor().getShortName())
                .org(org)
                .invoiceItems(List.of(consultationItem))
                .totalAmount(rate)
                .discountAmount(0.0)
                .amountToPay(rate)
                .paidAmount(0.0)
                .balanceAmount(rate)
                .status(ApplicationConstant.UNPAID)
                .invoiceDate(LocalDateTime.now())
                .build();

        long seqNum = sequenceGeneratorService.generateInvoiceSequence(ApplicationConstant.INVOICE_SEQ);
        String formattedId = String.format(ApplicationConstant.INVOICE_FORMAT, org.getOrganizationCode(), LocalDate.now().format(DateTimeFormatter.ofPattern(ApplicationConstant.INVOICE_DATE_FORMAT)), seqNum);
        invoice.setInvoiceNumber(formattedId);

        opdInvoiceRepository.save(invoice);
        logger.info("Auto-generated consultation invoice for appointment {}", appointment.getId());
    }

    @Override
    public AppResponseDTO getAppointmentFilters(String id, String pId, Boolean isWalkIn,
                                                String doctorId, String orgId,
                                                LocalDate fromDate, LocalDate toDate,
                                                int page, int size) {

        logger.info("Fetching appointments with invoices - id: {}, pId: {}, doctorId: {}, orgId: {}, from: {}, to: {}, page: {}, size: {}",
                id, pId, doctorId, orgId, fromDate, toDate, page, size);

        if (!StringUtils.hasText(orgId)) {
            throw new OPDException(ErrorConstant.ORGANIZATION_REQUIRED, HttpStatus.BAD_REQUEST);
        }

        if (fromDate != null && toDate != null) {
            ValidatorHelper.validateDateRange(fromDate, toDate,
                    new OPDException(ErrorConstant.INVALID_DATE_RANGE, HttpStatus.BAD_REQUEST));
        }

        LocalDateTime startDateTime = (fromDate != null) ? fromDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (toDate != null) ? toDate.atTime(LocalTime.MAX) : null;

        PagedResult<AppointmentWithInvoiceDTO> pagedAppointments =
                appointmentCustomRepository.findAppointmentsFilteredAndSorted(
                        id, pId, isWalkIn, doctorId, orgId, startDateTime, endDateTime, page, size
                );

        List<AppointmentWithInvoiceDTO> dtoList = pagedAppointments.getData()
                .stream()
                .map(this::decryptAndMapAppointmentDTO)
                .toList();

        long total = pagedAppointments.getTotal();
        long totalPages = (long) Math.ceil((double) total / size);
        return AppResponseDTO.getOk(dtoList, total, totalPages, page);
    }

    private AppointmentWithInvoiceDTO decryptAndMapAppointmentDTO(AppointmentWithInvoiceDTO dto) {
        try {
            if (StringUtils.hasText(dto.getPatientId())) {
                dto.setPatientId(EncryptionUtil.decrypt(dto.getPatientId()));
            }
            if (StringUtils.hasText(dto.getPatientName())) {
                dto.setPatientName(EncryptionUtil.decrypt(dto.getPatientName()));
            }
            if (StringUtils.hasText(dto.getLastName())) {
                dto.setLastName(EncryptionUtil.decrypt(dto.getLastName()));
            }
            if (StringUtils.hasText(dto.getPatientMoNumber())) {
                dto.setPatientMoNumber(EncryptionUtil.decrypt(dto.getPatientMoNumber()));
            }
            if (StringUtils.hasText(dto.getAge())) {
                String decryptedDob = EncryptionUtil.decrypt(dto.getAge());
                PatientPersonalInformation tempInfo = new PatientPersonalInformation();
                tempInfo.setDateOfBirth(decryptedDob);
                dto.setAge(getAge(tempInfo));
            }
            if (StringUtils.hasText(dto.getGender())) {
                dto.setGender(EncryptionUtil.decrypt(dto.getGender()));
            }
        } catch (Exception ex) {
            logger.error("Failed to decrypt patient data for appointmentId: {}", dto.getAppointmentId(), ex);
        }
        return dto;
    }

    private void addConsultationChargesIfNotPresent(OPDInvoice invoice, Appointment appointment) {
        boolean alreadyAdded = invoice.getInvoiceItems().stream()
                .filter(Objects::nonNull)
                .anyMatch(item -> item.getBillingItem().getServiceCode()
                        .equals(appointment.getDoctor().getId()));

        if (alreadyAdded) {
            logger.info("Consultation charges already present in invoice {}. Skipping duplicate addition.", invoice.getInvoiceNumber());
        } else {
            Doctor doctor = appointment.getDoctor();
            BillingItem billingItem = billingItemRepository
                    .findByServiceCode(doctor.getId())
                    .orElseThrow(() -> new OPDException("Consultation Billing Item not found for doctor.", HttpStatus.NOT_FOUND));

            double rate = "followup".equalsIgnoreCase(appointment.getAppointmentType()) ? doctor.getTotalSecondVisitRate() : billingItem.getRate();

            InvoiceItems consultationItem = InvoiceItems.builder()
                    .billingItem(billingItem)
                    .billingItemGroup(billingItem.getBillingItemGroup())
                    .rate(rate)
                    .quantity(1L)
                    .amount(rate)
                    .build();

            invoice.getInvoiceItems().add(consultationItem);

            invoice.setTotalAmount(invoice.getTotalAmount() + rate);
            invoice.setBalanceAmount(invoice.getBalanceAmount() + rate);
            invoice.setAmountToPay(invoice.getAmountToPay() + rate);

            logger.info("Consultation charges added to existing invoice {} for appointment {}", invoice.getInvoiceNumber(), appointment.getId());
        }
    }


}







