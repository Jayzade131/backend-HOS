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
public class TransferReceiptDto {

    private String patientName;
    private String gender;
    private String age;
    private String mobileNumber;
    private String admissionNumber;
    private String consultant;
    private String transferDateTime;
    private String admissionDate;
    private String remark;
    private String fromWard;
    private String fromBed;
    private String toWard;
    private String toBed;
    private String createdBy;

}

