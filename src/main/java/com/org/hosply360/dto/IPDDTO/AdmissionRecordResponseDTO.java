package com.org.hosply360.dto.IPDDTO;

import com.org.hosply360.dto.utils.PdfHeaderFooterDTO;
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
public class AdmissionRecordResponseDTO {
    private AdmissionInfoDTO admissionInfo;
    private PatientInfoDetailDTO patientInfo;
    private WardBedInfoDTO wardBedInfo;
    private ConsultantInfoDTO consultantInfo;
    private MedicalInfoDTO medicalInfo;
    private String remarks;
    private PdfHeaderFooterDTO headerFooter;

}
