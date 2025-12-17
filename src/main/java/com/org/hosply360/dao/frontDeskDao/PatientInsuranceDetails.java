package com.org.hosply360.dao.frontDeskDao;

import com.org.hosply360.dao.globalMaster.InsuranceProvider;
import com.org.hosply360.util.encryptionUtil.EncryptedField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientInsuranceDetails {

    @Field("provider")
    @DBRef
    private InsuranceProvider provider;

    @Field("policy_number")
    @EncryptedField
    private String policyNumber;

    @Field("group_number")
    @EncryptedField
    private String groupNumber;

    @Field("valid_from")
    private LocalDate validFrom;

    @Field("valid_to")
    private LocalDate validTo;

    @Field("is_primary")
    private boolean isPrimary;

}
