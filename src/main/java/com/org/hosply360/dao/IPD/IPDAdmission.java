package com.org.hosply360.dao.IPD;

import com.org.hosply360.constant.Enums.IpdStatus;
import com.org.hosply360.constant.Enums.PatientCaseType;
import com.org.hosply360.constant.Enums.PatientType;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.PatientCategory;
import com.org.hosply360.dao.globalMaster.Speciality;
import com.org.hosply360.dao.globalMaster.WardBedMaster;
import com.org.hosply360.dao.globalMaster.WardMaster;
import com.org.hosply360.dao.other.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "ipd_admissions")
public class IPDAdmission extends BaseModel {
    @Id
    private String id;

    @DBRef
    private Organization orgId;

    @DBRef
    private Patient patient;

    @DBRef
    private WardMaster wardMaster;

    @DBRef
    private WardBedMaster bedMaster;

    private String wardName;

    private String bedNo;

    private LocalDateTime admitDateTime;

    @DBRef
    private Doctor primaryConsultant;

    @DBRef
    private Doctor secondaryConsultant;

    private LocalDateTime dischargeDateTime;  // (Optional)


    private PatientType patientType;  // Regular, Corporate, Insurance (Required)

    @DBRef
    private PatientCategory regular;

    private CorporateDetails corporateDetails;  // If Corporate

    private InsuranceDetails insuranceDetails;  // If Insurance

    private String diagnosis;

    private PatientCaseType isPatient;  // MLC, NON_MLC (Required)

    @DBRef
    private Speciality department;

    @DBRef
    private Doctor refBy;

    private String ipdNo;

    private String regMrdNo;

    private IpdStatus ipdStatus;

    private String remarks;

    private Boolean defunct = false;
}
