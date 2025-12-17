package com.org.hosply360.dto.authDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolesResDto {
    private String id;

    private String name;

    private String description;


    private boolean defunct = false;
}
