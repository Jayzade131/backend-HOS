package com.org.hosply360.dao.IPD;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "ipd_billing_sequences")
public class IPDBillingSequence {
    @Id
    private String id;
    private long seq;
}
