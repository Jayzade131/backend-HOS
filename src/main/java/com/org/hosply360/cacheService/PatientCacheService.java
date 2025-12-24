package com.org.hosply360.cacheService;


import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dto.frontDeskDTO.PatientInfoDTO;
import com.org.hosply360.repository.frontDeskRepo.PatientRepository;
import com.org.hosply360.util.encryptionUtil.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "patient-cache", cacheManager = "appCacheManager")
public class PatientCacheService {

    private final PatientRepository patientRepository;

    private void decryptPatientDTO(PatientInfoDTO dto) {
        dto.setFirstname(EncryptionUtil.decrypt(dto.getFirstname()));
        dto.setLastname(EncryptionUtil.decrypt(dto.getLastname()));
        dto.setPatientNumber(EncryptionUtil.decrypt(dto.getPatientNumber()));
    }


    @Caching(evict = {@CacheEvict(value = "patients-defunct-org", allEntries = true)})
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @Cacheable(value = "patients-defunct-org", key = "#defunct + ':' + #orgId")
    public List<PatientInfoDTO> fetchPatients(Boolean defunct, String orgId) {
        List<PatientInfoDTO> dto = patientRepository.findAllByDefuncts(defunct, orgId);
        dto.forEach(this::decryptPatientDTO);
        return dto;
    }
}
