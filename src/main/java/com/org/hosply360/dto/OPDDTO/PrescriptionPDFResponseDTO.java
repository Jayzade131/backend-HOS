package com.org.hosply360.dto.OPDDTO;

import com.org.hosply360.constant.Enums.TestWhen;
import com.org.hosply360.dto.globalMasterDTO.TestDTO;
import com.org.hosply360.dto.pathologyDTO.PrescribedMedPdfResponseDTO;
import com.org.hosply360.dto.utils.PdfHeaderFooterDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrescriptionPDFResponseDTO {

    private PdfHeaderFooterDTO pdfHeaderFooterDTO;

    private LocalDate prescriptionDate;

    private String patientFirstName;

    private String patientLastName;

    private String patientMidName;

    private String doctorName;

    private String complaints;

    private String patientRecord;

    private String generalExamination;

    private String history;

    private ObstetricDTO obstetric;

    private VitalsDTO vitals;

    private List<String> test;

    private TestWhen testWhen;

    private String diagnosis;

    private List<PrescribedMedPdfResponseDTO> prescribedMeds;

    private String advice;

    private String nextVisit;
}
