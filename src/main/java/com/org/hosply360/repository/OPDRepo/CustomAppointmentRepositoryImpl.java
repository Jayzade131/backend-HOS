package com.org.hosply360.repository.OPDRepo;

import com.org.hosply360.dto.OPDDTO.AppointmentSummaryDTO;
import com.org.hosply360.util.encryptionUtil.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomAppointmentRepositoryImpl implements CustomAppointmentRepository {


    private final MongoTemplate mongoTemplate;

    @Override
    public List<AppointmentSummaryDTO> findAppointmentSummary(String orgId, LocalDateTime fromDate, LocalDateTime toDate) {
        MatchOperation match = Aggregation.match(
                Criteria.where("org_id.$id").is(new ObjectId(orgId))
                        .and("appointment_date").gte(fromDate).lte(toDate)
                        .and("defunct").is(false)
        );

        LookupOperation lookupPatient = LookupOperation.newLookup()
                .from("patients")
                .localField("patient_id.$id")
                .foreignField("_id")
                .as("patient");

        UnwindOperation unwindPatient = Aggregation.unwind("patient", true);


        LookupOperation lookupDoc = LookupOperation.newLookup()
                .from("doctor_master")
                .localField("doctor_id.$id")
                .foreignField("_id")
                .as("doctor_master");

        UnwindOperation unwindDoc = Aggregation.unwind("doctor_master", true);


        LookupOperation lookupLastVisit = LookupOperation.newLookup()
                .from("Appointment")
                .localField("patient_id.$id")
                .foreignField("patient_id.$id")
                .as("patientAppointments");

        UnwindOperation unwindLastVisit = Aggregation.unwind("patientAppointments", true);

        AggregationOperation groupMaxVisit = context -> new Document("$group",
                new Document("_id", "$_id")
                        .append("appointmentId", new Document("$first", "$_id"))
                        .append("patientId", new Document("$first", "$patient._id"))
                        .append("appointmentDate", new Document("$first", "$appointment_date"))
                        .append("lastVisit", new Document("$max", "$patientAppointments.appointment_date"))
                        .append("startTime", new Document("$first", "$start_time"))
                        .append("endTime", new Document("$first", "$end_time"))
                        .append("patient", new Document("$first", "$patient"))
                        .append("doctor_master", new Document("$first", "$doctor_master"))
                        .append("status", new Document("$first", "$status"))
                        .append("totalVisit", new Document("$sum", 1))
                        .append("doc_id", new Document("$first", "$doctor_master._id"))
                        .append("appointment_type", new Document("$first", "$appointment_type"))
        );

        ProjectionOperation project = Aggregation.project()
                .and("appointmentId").as("appointmentId")
                .and("patientId").as("patientId")
                .and("appointmentDate").as("appointmentDate")
                .and("patient.personal_info.first_name").as("name")
                .and("patient.personal_info.dob").as("dob")
                .and("patient.personal_info.gender").as("gender")
                .and("status").as("status")
                .and("startTime").as("startTime")
                .and("endTime").as("endTime")
                .and("doctor_master.firstName").as("consultantName")
                .and("lastVisit").as("lastVisit")
                .and("totalVisit").as("totalVisit")
                .and("doc_id").as("doc_id")
                .and("appointment_type").as("visitType");

        Aggregation aggregation = Aggregation.newAggregation(
                match,
                lookupPatient,
                unwindPatient,
                lookupDoc,
                unwindDoc,
                lookupLastVisit,
                unwindLastVisit,
                groupMaxVisit,
                project
        );


        List<AppointmentSummaryDTO> results = mongoTemplate.aggregate(aggregation, "Appointment", AppointmentSummaryDTO.class).getMappedResults();

        return results.stream()
                .map(this::mapAndDecryptAndCalculateAge)
                .toList();
    }




    private AppointmentSummaryDTO mapAndDecryptAndCalculateAge(AppointmentSummaryDTO dto) {
        dto.setName(EncryptionUtil.decrypt(dto.getName()));
        dto.setGender(EncryptionUtil.decrypt(dto.getGender()));

        if (dto.getDob() != null && !dto.getDob().isEmpty()) {
            try {
                String decryptedDob = EncryptionUtil.decrypt(dto.getDob());
                LocalDate dobDate = LocalDate.parse(decryptedDob);
                dto.setAge(calculateAge(dobDate));

            } catch (Exception e) {
                dto.setAge(null);
            }
        }
        return dto;
    }


    private String calculateAge(LocalDate dob) {
        Period period = Period.between(dob, LocalDate.now());
        int years = period.getYears();
        int months = period.getMonths();

        StringBuilder ageBuilder = new StringBuilder();
        if (years > 0) {
            ageBuilder.append(years).append(" y");
        }
        if (months > 0) {
            if (ageBuilder.length() > 0) {
                ageBuilder.append(" ");
            }
            ageBuilder.append(months).append(" m");
        }
        if (years == 0 && months == 0) {
            ageBuilder.append("0 m"); // newborn case
        }

        return ageBuilder.toString();
    }



}

