package com.org.hosply360.dao.frontDeskDao;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.dao.globalMaster.Occupation;
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
public class PatientPersonalInformation {

    @EncryptedField
    @Field("title")
    private String title;

    @EncryptedField
    @Indexed
    @Field("first_name")
    private String firstName;

    @EncryptedField
    @Field("middle_name")
    private String middleName;

    @EncryptedField
    @Indexed
    @Field("last_name")
    private String lastName;

    @EncryptedField
    @Field("dob")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ApplicationConstant.DATE_FORMAT)
    private String dateOfBirth;

    @EncryptedField
    @Field("gender")
    private String gender;

    @EncryptedField
    @Field("preferred_name")
    private String preferredName;

    @EncryptedField
    @Field("marital_status")
    private String maritalStatus;

    @EncryptedField
    @Field("blood_type")
    private String bloodType;

    @Field("occupation")
    @DBRef
    private Occupation occupation;
}
