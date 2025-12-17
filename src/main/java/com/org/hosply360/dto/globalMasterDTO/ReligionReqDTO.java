package com.org.hosply360.dto.globalMasterDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReligionReqDTO {
    private String id;

    private String organization;

    private String code;

    private String description;

    private Boolean defunct;
}
