package com.org.hosply360.dto.pathologyDTO;

import com.org.hosply360.dto.utils.PdfHeaderFooterDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestReportPdfResponseDTO {
    private PdfHeaderFooterDTO headerFooter;
    private String reportDateTime;
    private PatientBillInfoDTO patient;
    private List<TestReportParameterDTO> parameters;
    private String generatedBy;

}
