package com.org.hosply360.service.OPD;


import com.org.hosply360.dto.OPDDTO.OpTimetableDTO;

import java.util.List;

public interface DocAppointmentTimetableService {


    OpTimetableDTO createOpTimetable(OpTimetableDTO dto);

    List<OpTimetableDTO> getAllOpTimetables(String organizationId);

    OpTimetableDTO updateOpTimetable(OpTimetableDTO dto);

    void deleteOpTimetable(String id);

    OpTimetableDTO getOpTimetableByDoctorId(String doctorId, String organizationId);
}
