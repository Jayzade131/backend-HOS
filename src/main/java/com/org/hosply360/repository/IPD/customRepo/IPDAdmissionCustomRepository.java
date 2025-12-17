package com.org.hosply360.repository.IPD.customRepo;

import com.org.hosply360.dao.IPD.CorporateDetails;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.InsuranceDetails;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dto.IPDDTO.CorporateDetailsDTO;
import com.org.hosply360.dto.IPDDTO.IPDAdmissionDTO;
import com.org.hosply360.dto.IPDDTO.IPDPatientListDTO;
import com.org.hosply360.dto.IPDDTO.InsuranceDetailsDTO;
import com.org.hosply360.dto.frontDeskDTO.DoctorInfoDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientInfoDTO;
import com.org.hosply360.util.encryptionUtil.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class IPDAdmissionCustomRepository {

    private final MongoTemplate mongoTemplate;


    public Page<IPDAdmissionDTO> findAdmissions(
            String orgId,
            String id,
            String wardId,
            String ipdStatus,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    ) {

        Query query = buildQuery(orgId, id, wardId, ipdStatus, fromDate, toDate);

        long total = mongoTemplate.count(query, IPDAdmission.class);

        applyPagination(query, page, size);

        List<IPDAdmission> admissions = mongoTemplate.find(query, IPDAdmission.class);
        List<IPDAdmissionDTO> dtoList = admissions.stream()
                .map(this::toDTO)
                .toList();

        return new PageImpl<>(dtoList, PageRequest.of(page, size), total);
    }


    private Query buildQuery(
            String orgId,
            String id,
            String wardId,
            String ipdStatus,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("defunct").is(false));

        if (isValid(orgId)) {
            criteriaList.add(Criteria.where("orgId.$id").is(new ObjectId(orgId)));
        }
        if (isValid(id)) {
            criteriaList.add(Criteria.where("_id").is(new ObjectId(id)));
        }
        if (isValid(ipdStatus)) {
            criteriaList.add(Criteria.where("ipdStatus").is(ipdStatus));
        }
        if (isValid(wardId)) {
            criteriaList.add(Criteria.where("wardMaster.$id").is(new ObjectId(wardId)));
        }
        if (fromDate != null && toDate != null) {
            criteriaList.add(Criteria.where("admitDateTime")
                    .gte(fromDate.atStartOfDay())
                    .lte(toDate.atTime(23, 59, 59))
            );
        }

        Criteria finalCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        return new Query(finalCriteria);
    }


    private void applyPagination(Query query, int page, int size) {
        query.skip((long) page * size);
        query.limit(size);
    }


    private IPDAdmissionDTO toDTO(IPDAdmission admission) {
        return IPDAdmissionDTO.builder()
                .id(admission.getId())
                .orgId(admission.getOrgId() != null ? admission.getOrgId().getId() : null)
                .patient(toPatientDTO(admission.getPatient()))
                .wardMaster(admission.getWardMaster() != null ? admission.getWardMaster().getId() : null)
                .bedMaster(admission.getBedMaster() != null ? admission.getBedMaster().getId() : null)
                .wardName(admission.getWardName())
                .bedNo(admission.getBedNo())
                .admitDateTime(admission.getAdmitDateTime())
                .primaryConsultant(toDoctorDTO(admission.getPrimaryConsultant()))
                .secondaryConsultant(toDoctorDTO(admission.getSecondaryConsultant()))
                .dischargeDateTime(admission.getDischargeDateTime())
                .patientType(admission.getPatientType())
                .regular(admission.getRegular() != null ? admission.getRegular().getId() : null)
                .corporateDetails(toCorporateDTO(admission.getCorporateDetails()))
                .insuranceDetails(toInsuranceDTO(admission.getInsuranceDetails()))
                .diagnosis(admission.getDiagnosis())
                .isPatient(admission.getIsPatient())
                .ipdNo(admission.getIpdNo())
                .departmentId(admission.getDepartment() != null ? admission.getDepartment().getId() : null)
                .refBy(admission.getRefBy() != null ? admission.getRefBy().getId() : null)
                .regMrdNo(admission.getRegMrdNo())
                .ipdStatus(admission.getIpdStatus())
                .defunct(admission.getDefunct())
                .build();
    }


    private PatientInfoDTO toPatientDTO(Patient patient) {
        if (patient == null) return null;

        return PatientInfoDTO.builder()
                .id(patient.getId())
                .firstname(patient.getPatientPersonalInformation().getFirstName())
                .lastname(patient.getPatientPersonalInformation().getLastName())
                .pid(patient.getPId())
                .patientNumber(patient.getPatientContactInformation().getPrimaryPhone())
                .build();
    }

    private DoctorInfoDTO toDoctorDTO(Doctor doctor) {
        if (doctor == null) return null;

        return DoctorInfoDTO.builder()
                .id(doctor.getId())
                .doctorName(doctor.getFirstName())
                .doctorSpeciality(
                        doctor.getSpecialty() != null ? doctor.getSpecialty().getDepartment() : null
                )
                .build();
    }

    private CorporateDetailsDTO toCorporateDTO(CorporateDetails corp) {
        if (corp == null) return null;

        return CorporateDetailsDTO.builder()
                .companyId(corp.getCompanyId() != null ? corp.getCompanyId().getId() : null)
                .companyName(corp.getCompanyId() != null ? corp.getCompanyId().getCompanyName() : null)
                .approval(corp.getApproval())
                .build();
    }

    private InsuranceDetailsDTO toInsuranceDTO(InsuranceDetails ins) {
        if (ins == null) return null;

        return InsuranceDetailsDTO.builder()
                .insuranceProviderId(
                        ins.getInsuranceName() != null ? ins.getInsuranceName().getId() : null
                )
                .insuranceName(
                        ins.getInsuranceName() != null ? ins.getInsuranceName().getName() : null
                )
                .insuranceNo(ins.getInsuranceNo())
                .approval(ins.getApproval())
                .build();
    }

    private boolean isValid(String s) {
        return s != null && !s.isBlank();
    }


    public Page<IPDPatientListDTO> findPatientList(
            String orgId,
            String id,
            String wardId,
            String ipdStatus,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    ) {
        List<AggregationOperation> operations = new ArrayList<>();

        // -----------------------------------------------
        // 1️⃣ Build Dynamic Filter Criteria
        // -----------------------------------------------
        Criteria criteria = new Criteria();
        List<Criteria> filters = new ArrayList<>();

        filters.add(Criteria.where("defunct").is(false));

        if (orgId != null && !orgId.isEmpty()) {
            filters.add(Criteria.where("orgId.$id").is(new ObjectId(orgId)));
        }
        if (id != null && !id.isEmpty()) {
            filters.add(Criteria.where("_id").is(new ObjectId(id)));
        }
        if (wardId != null && !wardId.isEmpty()) {
            filters.add(Criteria.where("wardMaster.$id").is(new ObjectId(wardId)));
        }
        if (ipdStatus != null && !ipdStatus.isEmpty()) {
            filters.add(Criteria.where("ipdStatus").is(ipdStatus));
        }
        if (fromDate != null && toDate != null) {
            filters.add(Criteria.where("admitDateTime")
                    .gte(fromDate.atStartOfDay())
                    .lte(toDate.plusDays(1).atStartOfDay()));
        }

        if (!filters.isEmpty()) {
            criteria.andOperator(filters.toArray(new Criteria[0]));
        }

        operations.add(Aggregation.match(criteria));

        // -----------------------------------------------
        //  Lookups: Patient, Doctors, Ward, Bed
        // -----------------------------------------------
        operations.add(Aggregation.lookup("patients", "patient.$id", "_id", "patient"));
        operations.add(Aggregation.lookup("doctor_master", "primaryConsultant.$id", "_id", "primaryDoctor"));
        operations.add(Aggregation.lookup("doctor_master", "secondaryConsultant.$id", "_id", "secondaryDoctor"));
        operations.add(Aggregation.lookup("wardMaster", "wardMaster.$id", "_id", "ward"));
        operations.add(Aggregation.lookup("wardBedMaster", "bedMaster.$id", "_id", "bed"));
        operations.add(Aggregation.lookup("ipd_financial_summary", "_id", "ipdAdmission.$id", "financialSummary"));
        operations.add(Aggregation.lookup("ipd_final_bill", "_id", "admission.$id", "finalBill"));


        // -----------------------------------------------
        //  Unwind all lookup results
        // -----------------------------------------------
        operations.add(Aggregation.unwind("patient", true));
        operations.add(Aggregation.unwind("primaryDoctor", true));
        operations.add(Aggregation.unwind("secondaryDoctor", true));
        operations.add(Aggregation.unwind("ward", true));
        operations.add(Aggregation.unwind("bed", true));
        operations.add(Aggregation.unwind("financialSummary", true));
        operations.add(Aggregation.unwind("finalBill", true));

        // -----------------------------------------------
        //  Sort
        // -----------------------------------------------
        operations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "admitDateTime")));

        // -----------------------------------------------
        // Projection: All DTO Fields + Financial Summary Fields
        // -----------------------------------------------
        ProjectionOperation projection = Aggregation.project()
                .and("_id").as("id")
                .and("ipdNo").as("ipdNo")
                .and(ConditionalOperators.ifNull("finalBill.final_bill_no").then("-"))
                .as("invoiceNo")
                .and("ward._id").as("wardId")
                .and("ward.wardName").as("wardName")
                .and("bed._id").as("bedId")
                .and("bed.bedNo").as("bedNo")
                .and("primaryDoctor._id").as("consultId")
                .and("primaryDoctor.firstName").as("consultName")
                .and("secondaryDoctor._id").as("secondaryConsultId")
                .and("secondaryDoctor.firstName").as("secondaryConsultName")
                .and("patient._id").as("patientId")
                .and("patient.personal_info.first_name").as("patientFirstName")
                .and("patient.personal_info.last_name").as("patientLastName")
                .and("patient.contact_info.primary_phone").as("patientMobileNo")
                .and("patient.personal_info.dob").as("dob")
                .and("patient.personal_info.gender").as("patientGender")
                .andExpression("dateToString('%d-%m-%Y', admitDateTime)").as("admissionDate")
                .and("ipdStatus").as("ipdStatus")

                .and(ConditionalOperators.ifNull("financialSummary.totalNetAmount").then("0"))
                .as("amount")
                .and(ConditionalOperators.ifNull("financialSummary.totalPaidAmount").then("0"))
                .as("paidAmount")
                .and(ConditionalOperators.ifNull("financialSummary.pendingAmount").then("0"))
                .as("dueAmount");

        operations.add(projection);

        // -----------------------------------------------
        //  Pagination
        // -----------------------------------------------
        long skip = (long) page * size;
        operations.add(Aggregation.skip(skip));
        operations.add(Aggregation.limit(size));

        // -----------------------------------------------
        // Execute Aggregation
        // -----------------------------------------------
        Aggregation aggregation = Aggregation.newAggregation(operations);

        List<IPDPatientListDTO> results = mongoTemplate.aggregate(
                aggregation,
                "ipd_admissions",
                IPDPatientListDTO.class
        ).getMappedResults();

        // -----------------------------------------------
        // Decrypt Phone + Calculate Age
        // -----------------------------------------------
        List<IPDPatientListDTO> finalResults = results.stream()
                .map(this::decryptAndCalculateAge)
                .toList();

        // -----------------------------------------------
        //Total Count
        // -----------------------------------------------
        long total = mongoTemplate.count(new Query(criteria), "ipd_admissions");

        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(finalResults, pageable, total);
    }


    private IPDPatientListDTO decryptAndCalculateAge(IPDPatientListDTO dto) {
        try {
            if (dto.getPatientFirstName() != null) {
                dto.setPatientFirstName(EncryptionUtil.decrypt(dto.getPatientFirstName()));
            }
            if (dto.getPatientLastName() != null) {
                dto.setPatientLastName(EncryptionUtil.decrypt(dto.getPatientLastName()));
            }
            if (dto.getPatientGender() != null) {
                dto.setPatientGender(EncryptionUtil.decrypt(dto.getPatientGender()));
            }
            if (dto.getPatientMobileNo() != null) {
                dto.setPatientMobileNo(EncryptionUtil.decrypt(dto.getPatientMobileNo()));
            }
            if (dto.getDob() != null && !dto.getDob().isEmpty()) {
                String decryptedDob = EncryptionUtil.decrypt(dto.getDob());
                LocalDate dobDate = LocalDate.parse(decryptedDob);
                dto.setPatientAge(calculateAge(dobDate));
            }
        } catch (Exception e) {
            dto.setPatientAge(null);
        }
        return dto;
    }


    private String calculateAge(LocalDate dob) {
        Period period = Period.between(dob, LocalDate.now());
        int years = period.getYears();

        StringBuilder ageBuilder = new StringBuilder();
        if (years > 0) ageBuilder.append(years).append(" Years");

        if (years == 0) {
            ageBuilder.append("0 m");
        }

        return ageBuilder.toString();
    }
}
