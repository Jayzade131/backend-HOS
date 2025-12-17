package com.org.hosply360.repository.OPDRepo;

import com.org.hosply360.dao.OPD.OPDInvoice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OPDInvoiceRepository extends MongoRepository<OPDInvoice, String> {

    @Query("{'id': ?0, 'defunct': ?1, 'org.$id': ObjectId(?2)}")
    Optional<OPDInvoice> findByIdAndDefunctAndOrg(String id, boolean defunct, String orgId);

    @Query("{'appointment.$id': { $in: ?0 }, 'defunct': false }")
    List<OPDInvoice> findByAppointmentIdIn(List<String> appointmentIds);

    @Query(value = "{ 'appointment.$id': ?0, 'defunct': false }", fields = "{ 'payment_status': 1, '_id': 0 }")
    org.bson.Document findPaymentStatusByAppointmentId(String appointmentId);





    @Query("{'invoiceNumber': ?0, 'defunct': ?1}")
    Optional<OPDInvoice> findByInvoiceNumberAndDefunct(String invoiceNumber, boolean defunct);

    @Query("{'appointment_id': ?0, 'defunct': false}")
    Optional<OPDInvoice> findByAppointmentIdAndDefunctFalse(String appointmentId);

    @Query("{'org.id': ?0, 'appointment': { $ne: null }, 'defunct': false}")
    List<OPDInvoice> findByOrgIdAndAppointmentNotNullAndDefunctFalse(String orgId);

    @Query("{'org.id': ?0, 'appointment': { $ne: null }, 'invoiceDate': { $gte: ?1, $lte: ?2 }, 'defunct': false}")
    List<OPDInvoice> findByOrgIdAndAppointmentNotNullAndInvoiceDateBetweenAndDefunctFalse(String orgId, LocalDateTime from, LocalDateTime to);

    List<OPDInvoice> findByOrgIdAndDefunct(String orgId, boolean defunct);

    List<OPDInvoice> findByOrgIdAndInvoiceDateBetweenAndDefunct(String orgId, LocalDateTime from, LocalDateTime to, boolean defunct);

    @Aggregation(pipeline = {
            "{ $match: { 'org.$id': ObjectId(?0), 'defunct': false } }",
            "{ $lookup: { from: 'patients', localField: 'patient.$id', foreignField: '_id', as: 'patientDoc' } }",
            "{ $unwind: '$patientDoc' }",
            "{ $match: { 'patientDoc.pId': ?1 } }"
    })
    List<OPDInvoice> findInvoicesByOrgIdAndPatientPId(String orgId, String pId);

    @Aggregation(pipeline = {
            "{ $match: { 'org.$id': ObjectId(?0), 'defunct': false } }",
            "{ $lookup: { from: 'patients', localField: 'patient.$id', foreignField: '_id', as: 'patientDoc' } }",
            "{ $unwind: '$patientDoc' }",
            "{ $match: { 'patientDoc.pId': ?1 } }",
            "{ $match: { 'invoiceDate': { $gte: ?2, $lte: ?3 } } }"
    })
    List<OPDInvoice> findInvoicesByOrgIdAndPatientPIdAndDateRange(
            String orgId,
            String pId,
            LocalDateTime from,
            LocalDateTime to
    );

}
