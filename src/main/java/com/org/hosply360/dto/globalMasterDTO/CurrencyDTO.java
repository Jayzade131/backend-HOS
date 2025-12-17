package com.org.hosply360.dto.globalMasterDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDTO
{
    private String id;
    private OrganizationDTO organizationDTO;
    private String code;
    private List<String> symbol;
    private String name;
    private long decimalPlaces;
    private boolean defunct;
}