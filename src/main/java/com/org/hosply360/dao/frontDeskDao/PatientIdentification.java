package com.org.hosply360.dao.frontDeskDao;


import com.org.hosply360.dao.globalMaster.IdentificationDocument;
import com.org.hosply360.util.encryptionUtil.EncryptedField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientIdentification {

    @Indexed
    @Field("identification_document_id")
    @DBRef
    private IdentificationDocument identificationDocument;

    @EncryptedField
    @Field("document_number")
    private String documentNumber;


}