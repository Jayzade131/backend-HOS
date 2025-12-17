package com.org.hosply360.dto.frontDeskDTO;
import com.org.hosply360.dto.globalMasterDTO.LanguageDTO;
import com.org.hosply360.dto.globalMasterDTO.ReligionDTO;
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
public class PatientDemographicInformationDTO {

    private LanguageDTO languagePreference;

    private ReligionDTO religion;
}

