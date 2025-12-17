package com.org.hosply360.repository.OPDRepo;

import com.org.hosply360.dto.OPDDTO.PatientUpcomingVisitResponseDTO;
import com.org.hosply360.util.encryptionUtil.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class PrescriptionCustomRepositoryImpl implements PrescriptionCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<PatientUpcomingVisitResponseDTO> findPatientsByNextVisitRange(LocalDate fromDate, LocalDate toDate, String doctorId) {

        Criteria baseCriteria = Criteria.where("defunct").is(false);

        MatchOperation removeInvalid = Aggregation.match(
                new Criteria().andOperator(
                        Criteria.where("next_visit").ne(null),
                        Criteria.where("next_visit").ne("")
                )
        );

        AddFieldsOperation convertDate = Aggregation.addFields()
                .addFieldWithValue("nextVisitDateParsed",
                        ConvertOperators.ToDate.toDate("$next_visit")).build();

        java.util.Date today = java.util.Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Criteria upcomingCriteria = Criteria.where("nextVisitDateParsed").gte(today);

        boolean hasDateRange = (fromDate != null && toDate != null);
        boolean hasDoctor = (doctorId != null && !doctorId.isEmpty());

        Criteria finalCriteria;

        if (hasDateRange && hasDoctor) {
            java.util.Date from = java.util.Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            java.util.Date to = java.util.Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            Criteria dateCriteria = new Criteria().andOperator(
                    Criteria.where("nextVisitDateParsed").gte(from),
                    Criteria.where("nextVisitDateParsed").lte(to)
            );

            Criteria doctorCriteria = Criteria.where("doctor.$id").is(new ObjectId(doctorId));
            finalCriteria = new Criteria().andOperator(baseCriteria, upcomingCriteria, dateCriteria, doctorCriteria);

        } else if (hasDateRange || hasDoctor) {
            List<Criteria> orFilters = new ArrayList<>();
            if (hasDateRange) {
                java.util.Date from = java.util.Date.from(fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                java.util.Date to = java.util.Date.from(toDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

                orFilters.add(new Criteria().andOperator(
                        Criteria.where("nextVisitDateParsed").gte(from),
                        Criteria.where("nextVisitDateParsed").lte(to)
                ));
            }
            if (hasDoctor) {
                orFilters.add(Criteria.where("doctor.$id").is(new ObjectId(doctorId)));
            }

            finalCriteria = new Criteria().andOperator(baseCriteria, upcomingCriteria,
                    new Criteria().orOperator(orFilters.toArray(new Criteria[0])));

        } else {
            finalCriteria = new Criteria().andOperator(baseCriteria, upcomingCriteria);
        }

        MatchOperation matchStage = Aggregation.match(finalCriteria);

        LookupOperation lookupPatient = Aggregation.lookup("patients", "patient.$id", "_id", "patientDetails");
        LookupOperation lookupDoctor = Aggregation.lookup("doctor_master", "doctor.$id", "_id", "doctorDetails");
        LookupOperation lookupDoctorSpecialty = Aggregation.lookup(
                "speciality_department_master",
                "doctorDetails.specialty.$id",
                "_id",
                "doctorSpecialtyDetails"
        );

        Aggregation aggregation = Aggregation.newAggregation(
                removeInvalid,
                convertDate,
                matchStage,
                lookupPatient,
                lookupDoctor,
                Aggregation.unwind("patientDetails", true),
                Aggregation.unwind("doctorDetails", true),
                lookupDoctorSpecialty,
                Aggregation.unwind("doctorSpecialtyDetails", true),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "nextVisitDateParsed")),
                Aggregation.project()
                        .and("patientDetails._id").as("patientId")
                        .and("patientDetails.pId").as("pid")
                        .and("patientDetails.personal_info.first_name").as("firstName")
                        .and("patientDetails.personal_info.last_name").as("lastName")
                        .and("patientDetails.personal_info.dob").as("dob")
                        .and("doctorDetails._id").as("doctorId")
                        .and("doctorDetails.firstName").as("doctorName")
                        .and("doctorSpecialtyDetails.description").as("doctorSpecialty")
                        .and(ConvertOperators.ToString.toString("$nextVisitDateParsed")).as("nextVisitDate")
                        .and("patientDetails.contact_info.primary_phone").as("mobileNo")
                        .and("patientDetails.personal_info.gender").as("gender")
                        .and("patientDetails.contact_info.email").as("email")
                        .and("prescription_date").as("lastVisitDate")
        );

        AggregationResults<PatientUpcomingVisitResponseDTO> results =
                mongoTemplate.aggregate(aggregation, "prescription", PatientUpcomingVisitResponseDTO.class);

        List<PatientUpcomingVisitResponseDTO> response = results.getMappedResults();

        response.forEach(r -> {
            try {
                if (r.getDob() != null) {
                    String decryptedDob = EncryptionUtil.decrypt(r.getDob());
                    r.setDob(decryptedDob);
                    LocalDate dob = LocalDate.parse(decryptedDob, DateTimeFormatter.ISO_DATE);
                    r.setAge(Period.between(dob, LocalDate.now()).getYears());
                }
                if (r.getFirstName() != null) r.setFirstName(EncryptionUtil.decrypt(r.getFirstName()));
                if (r.getLastName() != null) r.setLastName(EncryptionUtil.decrypt(r.getLastName()));
                if (r.getMobileNo() != null) r.setMobileNo(EncryptionUtil.decrypt(r.getMobileNo()));
                if (r.getEmail() != null) r.setEmail(EncryptionUtil.decrypt(r.getEmail()));
                if (r.getGender() != null) r.setGender(EncryptionUtil.decrypt(r.getGender()));

                if (r.getNextVisitDate() != null && !r.getNextVisitDate().isEmpty()) {
                    LocalDate date = LocalDate.parse(r.getNextVisitDate().substring(0, 10));
                    r.setNextVisitDate(date.format(DateTimeFormatter.ISO_DATE));
                }

                if (r.getLastVisitDate() != null && !r.getLastVisitDate().isEmpty()) {
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                    DateTimeFormatter outputFormatter = DateTimeFormatter.ISO_DATE;
                    java.time.ZonedDateTime zonedDateTime = java.time.ZonedDateTime.parse(r.getLastVisitDate(), inputFormatter);
                    r.setLastVisitDate(zonedDateTime.toLocalDate().format(outputFormatter));
                }

            } catch (Exception ignored) {}
        });

        return response;
    }
}