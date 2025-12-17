package com.org.hosply360.dto.authDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleDTO {

    private String id;

    private String moduleName;

    private boolean defunct = false;

}
