    package com.org.hosply360.repository.OPDRepo;


    import com.org.hosply360.dao.OPD.OPDReceipt;
    import org.springframework.data.mongodb.repository.MongoRepository;

    import java.util.List;

    public interface OPDReceiptRepository extends MongoRepository<OPDReceipt, String> {
        List<OPDReceipt> findByInvoiceId(String invoiceId);
    }
