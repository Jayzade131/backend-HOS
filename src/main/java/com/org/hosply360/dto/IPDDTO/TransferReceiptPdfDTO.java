package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.dto.utils.PdfHeaderFooterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferReceiptPdfDTO {
    private String patientName;
    private String gender;
    private String age;
    private String mobileNumber;
    private String admissionNumber;
    private String consultant;
    private String admissionDate;
    private String transferDateTime;
    private String remark;

    private String fromWard;
    private String fromBed;
    private String toWard;
    private String toBed;

    private PdfHeaderFooterDTO headerFooter;

}
