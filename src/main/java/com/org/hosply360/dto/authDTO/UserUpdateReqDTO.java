package com.org.hosply360.dto.authDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserUpdateReqDTO {

    private String id;

    private String username;

    private String email;

    private String mobileNo;

    private String name;

    private List<String> organizationId;

    private List<String> roleId;
}
