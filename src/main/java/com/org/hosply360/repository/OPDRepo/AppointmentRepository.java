package com.org.hosply360.repository.OPDRepo;

import com.org.hosply360.dao.OPD.Appointment;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {


    @Query("{'id': ?0, 'org.id': ?1, 'defunct': ?2}")
    Optional<Appointment> findByIdAndDefunct(String id, String orgId, boolean defunct);

    @Query(value = "{ 'org.id': ?0, 'defunct': ?1 }")
    Page<Appointment> findByOrg(String orgId, boolean defunct, Pageable pageable);

    @Query(value = "{ 'doctor_id.$id': ?0, 'appointment_date': { $gte: ?1, $lt: ?2 }, 'defunct': false }",
            fields = "{ 'start_time': 1, 'end_time': 1 }")
    List<AppointmentTimeSlot> findTimeSlotsForConflict(ObjectId doctorId, Date startOfDay, Date endOfDay);


    @Query(value = "{ 'doctor.$id': ?0, 'appointmentDate': { $gte: ?1, $lt: ?2 }, 'defunct': false, '_id': { $ne: ?3 } }",
            fields = "{ 'startTime': 1, 'endTime': 1 }")
    List<AppointmentTimeSlot> findTimeSlotsForConflictExcludingId(ObjectId doctorId, Date startOfDay, Date endOfDay, ObjectId excludeId);


    @Query(value = "{ 'patient.id': ?0, 'org.id': ?1, 'defunct': false, 'status': { $ne: 'COMPLETED' } }")
    List<Appointment> findAppointmentsByPatientIdAndOrgIdExcludingCompleted(String patientId, String orgId);

    @Query("{ 'doctor.id': ?0, 'org.id': ?1, 'appointment_date': { $gte: ?2, $lte: ?3 }, 'defunct': ?4 }")
    List<Appointment> findByDoctorIdAndOrgIdAndAppointmentDateBetweenAndDefunct(
            String doctorId,
            String orgId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean defunct
    );

    @Query("{ 'org.id': ?0,'appointment_date': { $gte: ?1, $lte: ?2 }, 'defunct': ?3 }")
    List<Appointment> findByOrgIdAndAppointmentDateBetweenAndDefunct(
            String orgId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            boolean defunct
    );

    @Query("{ 'doctorId': ?0, 'org.id': ?1, 'defunct': ?2 }")
    List<Appointment> findByDoctorIdAndOrgIdAndDefunct(String doctorId, String orgId, boolean defunct);

    @Query("{'defunct': ?0}")
    Page<Appointment> findAllByDefunct(boolean defunct, Pageable pageable);

    @Query(value = "{ 'patientId': ?0, 'orgId': ?1, 'defunct': false }", count = true)
    long countByPatientIdAndOrgId(String patientId, String orgId);

    @Query(value = "{ 'patientId': ?0, 'orgId': ?1, 'id': { $ne: ?2 }, 'defunct': false }", sort = "{ 'appointmentDate': -1 }")
    List<Appointment> findLastVisitExcludingCurrent(String patientId, String orgId, String currentAppointmentId);


//    @Query("{ 'pId': ?0, 'org.id': ?1, 'appointmentDate': { $gte: ?2, $lte: ?3 }, 'defunct': ?4 }")
//    List<Appointment> findByPIdAndOrgIdAndAppointmentDateBetweenAndDefunct(
//            String pId, String orgId, LocalDateTime from, LocalDateTime to, boolean defunct);
//
//
//    @Query("{ 'pId': ?0,'doctor.id': ?1, 'org.id': ?2, 'appointment_date': { $gte: ?3, $lte: ?4 }, 'defunct': ?5 }")
//    List<Appointment> findByPIdAndDoctorIdAndOrgIdAndAppointmentDateBetweenAndDefunct(
//            String pId,
//            String doctorId,
//            String orgId,
//            LocalDateTime startDate,
//            LocalDateTime endDate,
//            boolean defunct
//    );
//
//
//    @Query("{ 'pId': ?0, 'org.id': ?1, 'defunct': ?2 }")
//    List<Appointment> findByPIdAndOrgIdAndDefunct(String pId, String orgId, boolean defunct);

    List<Appointment> findByDoctor_IdAndAppointmentDateAfterAndDefunctFalse(
            String doctorId, LocalDateTime now);




}
