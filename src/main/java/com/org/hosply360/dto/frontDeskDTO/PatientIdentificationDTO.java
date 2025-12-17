package com.org.hosply360.dto.frontDeskDTO;

import com.org.hosply360.dto.globalMasterDTO.IdentificationDocumentDTO;
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
public class PatientIdentificationDTO {

    private IdentificationDocumentDTO identificationDocumentId;
    private String documentNumber;

}

