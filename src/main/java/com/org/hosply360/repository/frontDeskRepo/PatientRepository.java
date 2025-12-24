package com.org.hosply360.repository.frontDeskRepo;

import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dto.frontDeskDTO.PatientInfoDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientResponseDTO;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    @Aggregation
    @Query("{'id': ?0, 'defunct': ?1}")
    Optional<Patient> findByIdAndDefunct(String id, boolean defunct);


 @Aggregation(pipeline = {
         "{ '$match': { 'organization.id': ?1, 'defunct': ?0 } }",

         "{ '$addFields': { " +
                 "'pid': '$pId', " +
                 "'firstname': '$personal_info.first_name', " +
                 "'lastname': '$personal_info.last_name', " +
                 "'patientNumber': '$contact_info.primary_phone' " +
                 "} }",

         "{ '$project': { " +
                 "'_id': 1, " +
        //         "'id': '$_id', " +
                 "'pid': 1, " +
                 "'firstname': 1, " +
                 "'lastname': 1, " +
                 "'patientNumber': 1 " +
                 "} }"
 })
 List<PatientInfoDTO> findAllByDefuncts(Boolean defunct, String orgId);

    @Query("{ 'patientPersonalInformation.firstName': ?0, 'patientContactInformation.primaryPhone': ?1 }")
    Optional<Patient> findByEncryptedNameAndEncryptedPhone(String encryptedName, String encryptedPhone);

    @Aggregation(pipeline = {
            "{ '$match': { 'organization.id': ?0, 'defunct': false } }",

            "{ '$addFields': { " +
                    "'pId': '$pId', " +
                    "'firstName': '$personal_info.first_name', " +
                    "'lastName': '$personal_info.last_name', " +
                    "'dob': '$personal_info.dob', " +
                    "'phoneNumber': '$contact_info.primary_phone', " +
                    // 👇 Add computed field for sorting ACTIVE patients first
                    "'statusOrder': { $cond: { if: { $eq: ['$status', 'ACTIVE'] }, then: 1, else: 0 } } " +
                    "} }",

            "{ '$project': { " +
                    "'_id': 1, " +
                    "'pId': 1, " +
                    "'firstName': 1, " +
                    "'lastName': 1, " +
                    "'dob': 1, " +
                    "'phoneNumber': 1, " +
                    "'status': 1, " +
                    "'statusOrder': 1 " +  // Keep for sorting
                    "} }",

            // 👇 Sort ACTIVE first, then by newest createdDate
            "{ '$sort': { 'statusOrder': -1, 'createdDate': -1 } }"
    })
    List<PatientResponseDTO> findPatientsByOrgId(String orgId);


}
