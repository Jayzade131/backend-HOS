package com.org.hosply360.customRepositories.impl;

import com.org.hosply360.customRepositories.AppointmentCustomRepository;
import com.org.hosply360.dto.OPDDTO.AppointmentWithInvoiceDTO;
import com.org.hosply360.dto.OPDDTO.PagedResult;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AppointmentCustomRepositoryImpl implements AppointmentCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public PagedResult<AppointmentWithInvoiceDTO> findAppointmentsFilteredAndSorted(
            String id, String pId, Boolean isWalkIn, String doctorId, String orgId,
            LocalDateTime fromDate, LocalDateTime toDate,
            int page, int size) {

        List<AggregationOperation> pipeline = new ArrayList<>();

        // 1) MATCH filter
        Criteria criteria = Criteria.where("org_id.$id").is(new ObjectId(orgId))
                .and("defunct").is(false);

        if (StringUtils.hasText(id)) {
            criteria.and("_id").is(new ObjectId(id));
        }
        if (StringUtils.hasText(pId)) {
            criteria.and("pId").is(pId);
        }
        if (StringUtils.hasText(doctorId)) {
            criteria.and("doctor_id.$id").is(new ObjectId(doctorId));
        }
        if (fromDate != null && toDate != null) {
            criteria.and("appointment_date").gte(fromDate).lte(toDate);
        }
        if (isWalkIn != null) {
            criteria.and("is_walkin").is(isWalkIn);
        }


        pipeline.add(Aggregation.match(criteria));

        // 2) STATUS ORDER
        pipeline.add(ctx -> new Document("$addFields",
                new Document("statusOrder",
                        new Document("$switch",
                                new Document("branches", Arrays.asList(
                                        new Document("case", new Document("$eq", Arrays.asList("$status", "INPROGRESS"))).append("then", 1),
                                        new Document("case", new Document("$eq", Arrays.asList("$status", "SCHEDULED"))).append("then", 2),
                                        new Document("case", new Document("$eq", Arrays.asList("$status", "CHECKEDIN"))).append("then", 3),
                                        new Document("case", new Document("$eq", Arrays.asList("$status", "PENDING"))).append("then", 4),
                                        new Document("case", new Document("$eq", Arrays.asList("$status", "COMPLETED"))).append("then", 5),
                                        new Document("case", new Document("$eq", Arrays.asList("$status", "NOSHOW"))).append("then", 6),
                                        new Document("case", new Document("$eq", Arrays.asList("$status", "CANCELLED"))).append("then", 7),
                                        new Document("case", new Document("$eq", Arrays.asList("$status", "RESCHEDULED"))).append("then", 8)
                                ))
                                        .append("default", 99)
                        )
                )
        ));

        // 3) Date truncation
        pipeline.add(ctx -> new Document("$addFields",
                new Document("appointmentDateOnly",
                        new Document("$dateTrunc",
                                new Document("date", "$appointment_date")
                                        .append("unit", "day")
                        )
                )
        ));

        // 4) Lookup invoice
        pipeline.add(ctx -> new Document("$lookup",
                new Document("from", "opd_invoice")
                        .append("let", new Document("appointmentId", "$_id"))
                        .append("pipeline", Arrays.asList(
                                new Document("$match", new Document("$expr",
                                        new Document("$eq", Arrays.asList("$appointment.$id", "$$appointmentId"))
                                ))
                        ))
                        .append("as", "invoiceData")
        ));

        // 5) Lookup doctor
        pipeline.add(ctx -> new Document("$lookup",
                new Document("from", "doctor_master")
                        .append("let", new Document("doctorId", "$doctor_id.$id"))
                        .append("pipeline", Arrays.asList(
                                new Document("$match", new Document("$expr",
                                        new Document("$eq", Arrays.asList("$_id", "$$doctorId"))
                                ))
                        ))
                        .append("as", "doctorData")
        ));

        // 6) Lookup patient
        pipeline.add(ctx -> new Document("$lookup",
                new Document("from", "patients")
                        .append("let", new Document("patientId", "$patient_id.$id"))
                        .append("pipeline", Arrays.asList(
                                new Document("$match", new Document("$expr",
                                        new Document("$eq", Arrays.asList("$_id", "$$patientId"))
                                ))
                        ))
                        .append("as", "patientData")
        ));
        pipeline.add(ctx -> new Document("$lookup",
                new Document("from", "speciality_department_master")
                        .append("let", new Document("specId", new Document("$arrayElemAt", Arrays.asList("$doctorData.specialty.$id", 0))))
                        .append("pipeline", Arrays.asList(
                                new Document("$match", new Document("$expr",
                                        new Document("$eq", Arrays.asList("$_id", "$$specId"))
                                ))
                        ))
                        .append("as", "specialtyData")
        ));

        // 7) Flatten invoice, doctor, patient fields
        pipeline.add(ctx -> new Document("$addFields",
                new Document("invoiceId", new Document("$arrayElemAt", Arrays.asList("$invoiceData._id", 0)))
                        .append("invoiceNumber", new Document("$arrayElemAt", Arrays.asList("$invoiceData.invoice_number", 0)))
                        .append("totalAmount", new Document("$arrayElemAt", Arrays.asList("$invoiceData.amount_to_pay", 0)))
                        .append("paidAmount", new Document("$arrayElemAt", Arrays.asList("$invoiceData.paid_amount", 0)))
                        .append("balanceAmount", new Document("$arrayElemAt", Arrays.asList("$invoiceData.balance_amount", 0)))
                        .append("doctorName", new Document("$arrayElemAt", Arrays.asList("$doctorData.firstName", 0)))
                        .append("docInfo", new Document()
                                .append("doc_id", new Document("$arrayElemAt", Arrays.asList("$doctorData._id", 0)))
                                .append("firstName", new Document("$arrayElemAt", Arrays.asList("$doctorData.firstName", 0)))
                                .append("specialtyId", new Document("$arrayElemAt", Arrays.asList("$doctorData.specialty.$id", 0)))
                                .append("specialtyName", new Document("$arrayElemAt", Arrays.asList("$specialtyData.department", 0)))
                        )
                        .append("patientId", new Document("$arrayElemAt", Arrays.asList("$patientData._id", 0)))
                        .append("patientName", new Document("$arrayElemAt", Arrays.asList("$patientData.personal_info.first_name", 0)))
                        .append("lastName", new Document("$arrayElemAt", Arrays.asList("$patientData.personal_info.last_name", 0)))
                        .append("patientNumber", new Document("$arrayElemAt", Arrays.asList("$patientData.contact_info.primary_phone", 0)))
                        .append("patientAge", new Document("$arrayElemAt", Arrays.asList("$patientData.personal_info.dob", 0)))
                        .append("patientGender", new Document("$arrayElemAt", Arrays.asList("$patientData.personal_info.gender", 0)))
        ));
// 9) Sort: by date, then status order, then actual appointment time
        pipeline.add(Aggregation.sort(Sort.by(
                Sort.Order.asc("appointmentDateOnly"),
                Sort.Order.asc("statusOrder")

        )));

        // 8) Remove temp arrays (but keep appointmentDateOnly for sorting)
        pipeline.add(ctx -> new Document("$unset", Arrays.asList("invoiceData", "doctorData", "patientData","specialtyData", "statusOrder")));


        // ---- COUNT ----
        long total = mongoTemplate.count(
                org.springframework.data.mongodb.core.query.Query.query(criteria),
                "Appointment"
        );

        // 10) Pagination
        int skip = (page - 1) * size;
        pipeline.add(Aggregation.skip((long) skip));
        pipeline.add(Aggregation.limit(size));



        pipeline.add(ctx -> new Document("$project",
                new Document("appointmentId", "$_id")
                        .append("aId", "$aId")
                        .append("pId", "$pId")
                        .append("tokenNumber", "$token_number")
                        .append("appointmentDate", "$appointment_date")
                        .append("session", "$session")
                        .append("startTime", "$start_time")
                        .append("endTime", "$end_time")
                        .append("appointmentStatus", "$status")
                        .append("appointmentType", "$appointment_type")
                        .append("isWalkIn", "$is_walkin")
                        .append("defunct", "$defunct")
                        .append("invoiceId", "$invoiceId")
                        .append("invoiceNumber", "$invoiceNumber")
                        .append("totalAmount", "$totalAmount")
                        .append("paidAmount", "$paidAmount")
                        .append("balanceAmount", "$balanceAmount")
                        .append("patientId", "$patientId")
                        .append("patientName", "$patientName")
                        .append("lastName", "$lastName")
                        .append("patientMoNumber", "$patientNumber")
                        .append("ageGender", "$ageGender")
                        .append("doctorName", "$doctorName")
                        .append("docInfo", "$docInfo")
                        .append("age", "$patientAge")
                        .append("gender", "$patientGender")
        ));



        Aggregation aggregation = Aggregation.newAggregation(pipeline);

        List<AppointmentWithInvoiceDTO> results = mongoTemplate.aggregate(
                aggregation, "Appointment", AppointmentWithInvoiceDTO.class
        ).getMappedResults();

        return new PagedResult<>(results, total);
    }
}
