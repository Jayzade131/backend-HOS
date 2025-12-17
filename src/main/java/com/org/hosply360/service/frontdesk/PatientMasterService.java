package com.org.hosply360.service.frontdesk;


import com.org.hosply360.dto.frontDeskDTO.PatientDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientInfoDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientReqDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientResponseDTO;

import java.util.List;

public interface PatientMasterService {

    String createOrUpdatePatient(PatientReqDTO patientReqDTO);

    List<PatientResponseDTO> getAllPatients(String organizationId);
    PatientDTO getPatient(String id);

    List<PatientInfoDTO> fetchAllPatient(String organizationId);

    void deletePatient(String id);

}
