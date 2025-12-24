package com.org.hosply360.cacheService;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.OPD.DocAppointmentTimetable;
import com.org.hosply360.dto.OPDDTO.OpTimetableDTO;
import com.org.hosply360.dto.OPDDTO.WeeklyScheduleDTO;
import com.org.hosply360.exception.OPDException;
import com.org.hosply360.repository.OPDRepo.DocAppointmentTimetableRepository;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "doc-timetable-cache", cacheManager = "appCacheManager")
public class DocTimeTableCacheService {

    private final DocAppointmentTimetableRepository timetableRepository;

    private OpTimetableDTO convertToDTO(DocAppointmentTimetable timetable) {
        OpTimetableDTO dto = ObjectMapperUtil.copyObject(timetable, OpTimetableDTO.class);
        List<String> daysOrder = List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");

        Map<String, WeeklyScheduleDTO> scheduleMap = daysOrder.stream()
                .filter(day -> timetable.getWeeklySchedule().containsKey(day))
                .collect(Collectors.toMap(
                        day -> day,
                        day -> ObjectMapperUtil.copyObject(timetable.getWeeklySchedule().get(day), WeeklyScheduleDTO.class),
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));

        dto.setWeeklySchedule(scheduleMap);
        dto.setDoctorId(timetable.getDoctorId() != null ? timetable.getDoctorId().getId() : null);
        dto.setDoctorName(timetable.getDoctorId() != null ? timetable.getDoctorId().getFirstName() : null);
        dto.setSpecialtyId(timetable.getSpecialtyId() != null ? timetable.getSpecialtyId().getId() : null);
        dto.setSpecialtyName(timetable.getSpecialtyId() != null ? timetable.getSpecialtyId().getDepartment() : null);
        dto.setOrganizationId(timetable.getOrganizationId() != null ? timetable.getOrganizationId().getId() : null);
        return dto;
    }


    @Cacheable(value = "doc-timetable-cache", key = "#doctorId + ':' + #defunct + ':' + #orgId")
    public OpTimetableDTO getOpTimetableByDoctor(String doctorId,Boolean defunct, String orgId)
    {
        DocAppointmentTimetable dto = timetableRepository.findByDoctorIdAndDefunctAndOrg(doctorId, defunct, orgId);
        if (dto == null) {
            throw new OPDException(ErrorConstant.OP_TIMETABLE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
       return convertToDTO(dto);
    }


    @Caching(evict = {@CacheEvict(value = "doc-timetable-cache", allEntries = true)})
    public DocAppointmentTimetable saveDocAppointmentTimetable(DocAppointmentTimetable docAppointmentTimetable)
    {
        return timetableRepository.save(docAppointmentTimetable);
    }

}
