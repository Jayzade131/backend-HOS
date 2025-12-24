package com.org.hosply360.util.Others;

import com.org.hosply360.dao.IPD.IPDBillingSequence;
import com.org.hosply360.dao.IPD.IPDNoSequence;
import com.org.hosply360.dao.OPD.AppointmentSequence;
import com.org.hosply360.dao.OPD.AppointmentTokenCounter;
import com.org.hosply360.dao.frontDeskDao.PatientSequence;
import com.org.hosply360.dao.OPD.InvoiceSequence;
import com.org.hosply360.dao.OPD.ReceiptSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {
    private final MongoOperations mongoOperations;
    private final MongoTemplate mongoTemplate;

    public long generatePatientSequence(String seqName) {
        PatientSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                PatientSequence.class);

        return counter != null ? counter.getSeq() : 1;
    }

    public long generateAppointmentSequence(String seqName) {

        AppointmentSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                AppointmentSequence.class);

        return counter != null ? counter.getSeq() : 1;
    }

    public long generateInvoiceSequence(String seqName) {
        InvoiceSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                InvoiceSequence.class
        );

        return counter != null ? counter.getSeq() : 1;
    }

    public long generateReceiptSequence(String seqName) {
        ReceiptSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                ReceiptSequence.class
        );
        return counter != null ? counter.getSeq() : 1;
    }

    public String generateReceiptNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = generateReceiptSequence("receipt_sequence");
        return "RCPT-" + datePart + "-" + String.format("%04d", seq);
    }


    public long generateBillingSequence(String seqName) {
        IPDBillingSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                IPDBillingSequence.class
        );
        return counter != null ? counter.getSeq() : 1;
    }

    public String generateIPDBillingNumber() {
        String yearPart = String.valueOf(LocalDate.now().getYear());
        long seq = generateBillingSequence("ipd_billing_sequence");
        return "IPDBILL-" + yearPart + "-" + String.format("%05d", seq);
    }



    public long generateIPDNoSequence(String seqName) {
        IPDNoSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                IPDNoSequence.class
        );

        return counter != null ? counter.getSeq() : 1;
    }

    public String generateIPDNumber() {
        String year = String.valueOf(LocalDate.now().getYear()); // e.g., 2025
        long seq = generateIPDNoSequence("ipd_sequence"); // Your sequence generator
        return year + String.format("%04d", seq);
    }






    public long getNextToken(String orgId, String day, boolean isWalkIn) {
        if (!isWalkIn) {
            throw new IllegalArgumentException("Token numbers are only generated for walk-in appointments");
        }

        Query query = new Query(Criteria.where("orgId").is(orgId)
                .and("appointmentDay").is(day)
                .and("isWalkIn").is(true));

        Update update = new Update()
                .inc("seq", 1)
                .setOnInsert("orgId", orgId)
                .setOnInsert("appointmentDay", day)
                .setOnInsert("isWalkIn", true);

        FindAndModifyOptions options = FindAndModifyOptions.options().upsert(true).returnNew(true);

        AppointmentTokenCounter counter = mongoTemplate.findAndModify(query, update, options, AppointmentTokenCounter.class);
        return counter.getSeq();
    }


    public String generatePathologyReceiptNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long seq = generateReceiptSequence("PATHOLOGY_RECEIPT_SEQ");
        return "PATH-" + datePart + "-" + String.format("%04d", seq);
    }


}
