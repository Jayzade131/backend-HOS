package com.org.hosply360.dao.globalMaster;

import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "insurance_provider")
public class InsuranceProvider extends BaseModel {
    @Id
    private String id;

    @DBRef
    private Organization organization;

    @NotBlank(message = "Provider name is required")
    @Field("name")
    private String name;

    @NotBlank(message = "Code is required")
    @Indexed(unique = true)
    @Field("code")
    private String code;

    @NotBlank(message = "Provider type is required")
    @Field("provider_type")
    private String providerType;

    @Field("registration_number")
    private String registrationNumber;

    @Field("contact_person_name")
    private String contactPersonName;

    @Field("contact_number")
    private String contactNumber;

    @Field("email")
    private String email;

    @Field("address")
    private String address;

    @Field("website")
    private String website;

    @Field("defunct")
    private Boolean defunct = false;
}
