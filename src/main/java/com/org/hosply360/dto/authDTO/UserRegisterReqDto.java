package com.org.hosply360.dto.authDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class UserRegisterReqDto {

    private String username;

    private String name;

    private String mobileNo;

    private String email;

    private List<String> roleId;

    private List<String> organizationId;

}
