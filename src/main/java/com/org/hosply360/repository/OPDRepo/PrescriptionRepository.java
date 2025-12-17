package com.org.hosply360.repository.OPDRepo;

import com.org.hosply360.dao.OPD.Prescription;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    @Query(value = "{ 'id': ?0, 'defunct': ?1, 'organization.id': ?2 }")
    Optional<Prescription> finByPrescriptionAndDefunctAndOrg(String prescriptionId, boolean defunct, String orgId);


    @Query(value = "{ 'appointment.id': ?0, 'defunct': ?1, 'organization.id': ?2 }")
    Optional<Prescription> findByAppointmentIdAndDefunctAndOrg(String appointmentId, boolean defunct, String orgId);


    @Query("{'patient.$id': ?0, 'defunct': ?1, 'organization.$id': ?2}")
    List<Prescription> getPrescriptionHistoryByPatient(ObjectId patientId, boolean defunct, ObjectId orgId);



    @Query(value = "{ 'patient.id': ?0, 'defunct': ?1, 'organization.id': ?2 }")
    List<Prescription> findAllByPatientIdAndDefunctAndOrg(String patientId, boolean defunct, String orgId);

    @Query(value = "{ '_id': ?0, 'organization.id': ?1, 'defunct': false }")
    Optional<Prescription> findByIdAndOrganizationId(String prescriptionId, String organizationId);


}
