package com.org.hosply360.dto.IPDDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IPDPatientListDTO {

    private String id;
    private String ipdNo;
    private String invoiceNo;
    private String wardId;
    private String wardName;
    private String bedId;
    private String bedNo;
    private String consultId;
    private String consultName;
    private String secondaryConsultId;
    private String secondaryConsultName;
    private String patientId;
    private String patientFirstName;
    private String patientLastName;
    private String patientMobileNo;
    private String dob;
    private String patientAge;
    private String patientGender;
    private String admissionDate;
    private String amount;
    private String paidAmount;
    private String dueAmount;
    private String ipdStatus;
}
