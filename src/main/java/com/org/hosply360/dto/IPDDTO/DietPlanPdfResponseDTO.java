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
public class DietPlanPdfResponseDTO {
    private PdfHeaderFooterDTO headerFooter;
    private String ipdNo;
    private String admDate;
    private String consultant;
    private String patientName;
    private String ageGender;
    private String mobileNo;
    private String dietRemark;
    private List<DietInfoDTO> dites;
}
