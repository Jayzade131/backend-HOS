package com.org.hosply360.dao.auth;

import com.org.hosply360.dao.other.BaseModel;
import com.org.hosply360.dao.globalMaster.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class Users extends BaseModel {

    @Id
    private String id;

    @Field("user_name")
    @NotNull
    private String username;

    @NotNull
    @Field("password")
    private String password;

    @Field("email")
    private String email;

    @Field("name")
    private String name;

    @Field("mobile_no")
    private String mobileNo;

    @Field("defunct")
    @NotNull
    private boolean defunct;

    @DBRef
    private List<Organization> organizations;

    @DBRef
    private List<Roles> roles;

    @Field("default_password_flag")
    private boolean isDefaultPassword;

}
