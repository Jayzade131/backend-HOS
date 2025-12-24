package com.org.hosply360.cacheService;

import com.org.hosply360.constant.Enums.DoctorType;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dto.OPDDTO.AppointmentDocInfoDTO;
import com.org.hosply360.repository.frontDeskRepo.DoctorMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "doctor-cache", cacheManager = "appCacheManager")
public class DoctorCacheService {

    private final DoctorMasterRepository doctorMasterRepository;

    @Cacheable(value = "doctors-speciality-response", key = "#speId + ':'+ #defunct + ':' + #orgId")
    public List<AppointmentDocInfoDTO> getDoctorsBySpeciality(String speId, Boolean defunct, String orgId) {
        return doctorMasterRepository.findBySpecialtityIdAndDefunct(speId, defunct, orgId);
    }

    @Cacheable(value = "doctors-org-response", key = "#defunct + ':' + #orgId")
    public List<AppointmentDocInfoDTO> getDoctorsByOrg(Boolean defunct, String orgId) {
        return doctorMasterRepository.findAllByDefuncts(defunct, orgId);
    }

    @Cacheable(value = "doctors-type-response", key = "#defunct + ':' + #orgId + ':' + #doctorType")
    public List<AppointmentDocInfoDTO> getDoctorsByType(Boolean defunct, String orgId, DoctorType doctorType) {
        return doctorMasterRepository.findByDoctorTypeAndDefunct(false, orgId, doctorType);
    }

    @Caching(evict = {
            @CacheEvict(value = "doctors-speciality-response", allEntries = true),
            @CacheEvict(value = "doctors-org-response", allEntries = true),
            @CacheEvict(value = "doctors-type-response", allEntries = true)
    })
    public Doctor saveDoctor(Doctor doctor) {
        return doctorMasterRepository.save(doctor);
    }


}
