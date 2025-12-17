package com.org.hosply360.dto.IPDDTO;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DischargeFormPdfDTO {
    private String ipdNo;
    private String mrdNo;
    private String patientName;
    private String fatherName;
    private String admissionDate;
    private String dischargeDate;
    private String address;
    private String ageGender;
    private String primaryConsultant;
    private String secondaryConsultant;
    private String thirdConsultant;
    private String type;
    private String remark;
    private String department;
    private String diagnosis;
    private String complaints;
    private String investigations;
    private String treatment;
    private String indication;
    private String history;
    private String examination;
    private String rx;
    private String review;
}
