package com.org.hosply360.dto.globalMasterDTO;

import com.org.hosply360.constant.Enums.OPD_Vital;
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
public class ConfigurationDTO {
    private String id;
    private OrganizationDTO organizationDto;
    private OPD_Vital opdVital;
    private BillingItemDTO billingItemDto;
    private String ipdAdmissionPrintFormat;
    private String ipdNoFormat;
    private String medicineMaster;
    private Double surgeon;
    private Double anaesthetist;
    private Double otCharges;
    private Double otConsumable;
    private Boolean defunct;
}
