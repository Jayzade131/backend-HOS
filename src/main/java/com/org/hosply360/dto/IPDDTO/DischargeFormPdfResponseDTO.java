package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.dto.utils.PdfHeaderFooterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DischargeFormPdfResponseDTO {

    private PdfHeaderFooterDTO headerFooter;

    private String ipdNo;
    private String admissionDate;
    private String dischargeDate;

    private String patientName;
    private String patientAttendantName;
    private String ageGender;
    private String address;

    private String primaryConsultant;
    private String secondaryConsultant;

    private String status;

    private List<SurgeryInfoDTO> surgeries;

//    private String admittingDiagnosis;
//    private String finalDiagnosis;


}
