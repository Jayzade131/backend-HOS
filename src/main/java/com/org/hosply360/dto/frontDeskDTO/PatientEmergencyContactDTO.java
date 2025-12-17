package com.org.hosply360.dto.frontDeskDTO;

import com.org.hosply360.util.encryptionUtil.EncryptedField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientEmergencyContactDTO {

    @EncryptedField
    private String name;

    @EncryptedField
    private String relationship;

    @EncryptedField
    private String phone;

    private boolean isPrimary;
}
