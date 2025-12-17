package com.org.hosply360.dao.OPD;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "invoice_sequence")
public class InvoiceSequence {
    @Id
    private String id;
    private long seq;
}