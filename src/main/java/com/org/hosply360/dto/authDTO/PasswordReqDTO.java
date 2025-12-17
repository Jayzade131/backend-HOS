package com.org.hosply360.dto.authDTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordReqDTO {

    private String UserId;

    private String defaultPassword;

    private String newPassword;

    private String confirmPassword;

}
