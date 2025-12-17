package com.org.hosply360.dto.authDTO;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class JwtResponseDTO {

    private String token;
    private String Message;
    private UserResponseDTO userResponseDto;
}
