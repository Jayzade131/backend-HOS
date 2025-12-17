package com.org.hosply360.dto.authDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleAccessDto {

    private String id;

    private String moduleName;

    private boolean defunct = false;

    private List<AccessDTO> accessDTO;

}
