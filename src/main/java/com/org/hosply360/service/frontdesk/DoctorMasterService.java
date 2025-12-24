package com.org.hosply360.service.frontdesk;

import com.org.hosply360.constant.Enums.DoctorType;
import com.org.hosply360.dto.OPDDTO.AppointmentDocInfoDTO;
import com.org.hosply360.dto.frontDeskDTO.DoctorDTO;
import com.org.hosply360.dto.frontDeskDTO.DoctorReqDTO;
import com.org.hosply360.dto.frontDeskDTO.GetDoctorResponse;

import java.io.IOException;
import java.util.List;

public interface DoctorMasterService {

    String  createDoctor(DoctorReqDTO doctorReqDTO) throws IOException;

    List<GetDoctorResponse> getAllDoctors(String orgId);

    DoctorDTO getDoctorById(String id);

    void deleteDoctor(String id);

    List<AppointmentDocInfoDTO> getAllDoctorsBySpeciality(String speId, String orgId);

    List<AppointmentDocInfoDTO> fetchAllDoctor(String orgId);

    List<AppointmentDocInfoDTO> getDoctorByDoctorType(DoctorType doctorType, String orgId);
}
