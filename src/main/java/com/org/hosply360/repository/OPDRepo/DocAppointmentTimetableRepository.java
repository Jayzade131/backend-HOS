package com.org.hosply360.repository.OPDRepo;

import com.org.hosply360.dao.OPD.DocAppointmentTimetable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DocAppointmentTimetableRepository extends MongoRepository<DocAppointmentTimetable, String> {

    @Query("{'doctorId': ?0, 'defunct': ?1, 'organizationId': ?2}")
    DocAppointmentTimetable findByDoctorIdAndDefunctAndOrg(String doctorId, boolean defunct, String orgId);

    @Query("{'defunct': ?0,'organizationId': ?1}")
    List<DocAppointmentTimetable> findAllByDefunctAndOrgId(boolean b, String orgId);

    @Query("{ 'doctorId': ?0, 'defunct': ?1 }")
    DocAppointmentTimetable findByDoctorIdAndDefunct(String doctorId, boolean defunct);

}

