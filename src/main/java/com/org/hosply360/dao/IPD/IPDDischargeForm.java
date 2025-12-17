package com.org.hosply360.dao.IPD;

import com.org.hosply360.constant.Enums.type;
import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "ipd_discharge_form")
public class IPDDischargeForm extends BaseModel {
    @Id
    private String id;
    private String organizationId;
    private String ipdAdmissionId;
    private String primaryConsultant;
    private String secondaryConsultant;
    private String thirdConsultant;
    private String templateId;
    private String remarks;
    private LocalDateTime dateTime;
    private type type;
    private String dischargeSummary;
    private Boolean defunct;
}
