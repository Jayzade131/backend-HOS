package com.org.hosply360.repository.frontDeskRepo;

import com.org.hosply360.constant.Enums.DoctorType;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dto.OPDDTO.AppointmentDocInfoDTO;
import com.org.hosply360.dto.frontDeskDTO.DoctorDTO;
import com.org.hosply360.dto.frontDeskDTO.GetDoctorResponse;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorMasterRepository extends MongoRepository<Doctor, String> {

    @Query("{'registration_no': ?0, 'defunct': ?1}")
    Optional<Doctor> findByRegistrationNoAndDefunct(String registrationNo, Boolean defunct);

    @Aggregation(pipeline = {
            "{ $match: { defunct: ?0, 'organization.id': ?1 } }",

            "{ $lookup: { from: 'speciality_department_master', localField: 'specialty.$id', foreignField: '_id', as: 'specialtyData' } }",
            "{ $unwind: { path: '$specialtyData', preserveNullAndEmptyArrays: true } }",

            "{ $lookup: { from: 'speciality_department_master', localField: 'depart.$id', foreignField: '_id', as: 'departmentData' } }",
            "{ $unwind: { path: '$departmentData', preserveNullAndEmptyArrays: true } }",

            "{ $lookup: { from: 'organization_master', localField: 'organization.$id', foreignField: '_id', as: 'orgData' } }",
            "{ $unwind: { path: '$orgData', preserveNullAndEmptyArrays: true } }",

            "{ $addFields: { " +
                    "specialityId: '$specialtyData._id', " +
                    "specialityName: '$specialtyData.department', " +
                    "departmentName: '$departmentData.department', " +
                    "orgIdStr: '$orgData._id', " +
                    // 👇 Create a numeric sort key: 1 if ACTIVE, else 0
                    "statusOrder: { $cond: { if: { $eq: ['$status', 'ACTIVE'] }, then: 1, else: 0 } } " +
                    "} }",

            "{ $project: { " +
                    "_id: 0, " +
                    "id: '$_id', " +
                    "orgId: [ '$orgIdStr' ], " +
                    "RegNo: '$registration_no', " +
                    "doctorName: '$firstName', " +
                    "specialityId: 1, " +
                    "speciality: '$specialityName', " +
                    "department: '$departmentName', " +
                    "status: 1, " +
                    "firstFee: '$first_visit_rate', " +
                    "secondFee: '$second_visit_rate', " +
                    "doctorType: 1, " +
                    "statusOrder: 1 " + // Keep for sorting
                    "} }",

            // 👇 Sort by ACTIVE first, then by id descending
            "{ $sort: { statusOrder: -1, id: -1 } }"
    })
    List<GetDoctorResponse> findAllByDefunct(Boolean defunct, String orgId);


    @Query("{'id': ?0, 'defunct': ?1}")
    Optional<Doctor> findByIdAndDefunct(String id, Boolean defunct);

    @Aggregation(pipeline = {
            "{ $match: { defunct: ?1, 'organization.id': ?2, 'specialty.id': ?0 } }",
            "{ $lookup: { from: 'speciality_department_master', localField: 'specialty.$id', foreignField: '_id', as: 'specialtyData' } }",
            "{ $unwind: { path: '$specialtyData', preserveNullAndEmptyArrays: true } }",
            "{ $project: { doc_id: '$_id', firstName: '$firstName', specialtyName: '$specialtyData.department' } }"
    })
    List<AppointmentDocInfoDTO> findBySpecialtityIdAndDefunct(String SpecialtyId, Boolean defunct, String orgId);


    @Aggregation(pipeline = {
            "{ $match: { defunct: ?0, 'organization.id': ?1 } }",
            "{ $lookup: { from: 'speciality_department_master', localField: 'specialty.$id', foreignField: '_id', as: 'specialtyData' } }",
            "{ $unwind: { path: '$specialtyData', preserveNullAndEmptyArrays: true } }",
            "{ $project: { " +
                    "doc_id: '$_id', " +
                    "firstName: '$firstName', " +
                    "specialtyName: '$specialtyData.department' } }",
            "{ $sort: { _id: -1 } }"
    })
    List<AppointmentDocInfoDTO> findAllByDefuncts(Boolean defunct, String orgId);

    Optional<Doctor> findByUser_IdAndDefunct(String userId, Boolean defunct);

    @Query(value = "{ 'userId': ?0, 'organizationId': ?1, 'defunct': ?2 }", exists = true)
    boolean existsByUserIdAndOrganizationIdAndDefunct(String userId, String organizationId, boolean defunct);

    @Aggregation(pipeline = {
            "{ $match: { defunct: ?0, 'organization.$id': ObjectId(?1), doctorType: ?2 } }",

            "{ $lookup: { "
                    + "from: 'speciality_department_master', "
                    + "localField: 'specialty.$id', "
                    + "foreignField: '_id', "
                    + "as: 'specialtyData' "
                    + "} }",
            "{ $unwind: { path: '$specialtyData', preserveNullAndEmptyArrays: true } }",
            "{ $unwind: { path: '$doctorTariff', preserveNullAndEmptyArrays: true } }",
            "{ $project: { "
                    + "doc_id: { $toString: '$_id' }, "
                    + "firstName: 1, "
                    + "specialtyName: '$specialtyData.department', "
                    + "specialtyId: { $toString: '$specialty.$id' }, "
                    + "firstRate: '$first_visit_rate', "
                    + "secondRate: '$second_visit_rate', "
                    + "tariffFirstRate: { $toDouble: '$doctorTariff.firstRate' }, "
                    + "tariffSecondRate: { $toDouble: '$doctorTariff.secondRate' } "
                    + "} }",
            "{ $sort: { firstName: 1 } }"
    })
    List<AppointmentDocInfoDTO> findByDoctorTypeAndDefunct(Boolean defunct, String orgId, DoctorType doctorType);
}
