package com.org.hosply360.dto.authDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleModuleAccessDto {

    private String id;

    private String name;

    private String description;

    private boolean defunct = false;

    private List<ModuleAccessDto> moduleAccessDto;
}
