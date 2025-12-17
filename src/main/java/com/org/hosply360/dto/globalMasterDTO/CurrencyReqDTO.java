package com.org.hosply360.dto.globalMasterDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyReqDTO {
    private String id;
    private String organizationDTO;
    private String code;
    private List<String> symbol;
    private String name;
    private long decimalPlaces;
    private boolean defunct;
}
