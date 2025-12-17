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
public class ConfigurationReqDTO {
    private String id;
    private String organizationId;
    private OPD_Vital opdVital;
    private String billingItemId;
    private String ipdAdmissionPrintFormat;
    private String ipdNoFormat;
    private String medicineMaster;
    private double surgeon;
    private double anasthesist;
    private double otCharges;
    private double otConsumable;
    private boolean defunct;
}
