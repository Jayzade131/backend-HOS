package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.dao.globalMaster.MedicineMaster;
import com.org.hosply360.dto.globalMasterDTO.MedicineCsvRowDto;
import com.org.hosply360.repository.globalMasterRepo.MedicineMasterRepository;
import com.org.hosply360.service.globalMaster.MedicineMasterService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineMasterServiceImpl implements MedicineMasterService {

    private final MedicineMasterRepository repository;

    //  save the medicine from csv
    @Override
    public void saveFromCsv(List<MedicineCsvRowDto> csvData) {
        List<MedicineMaster> list = csvData.stream() // convert the list of csv data to list of medicine master objects
                .filter(dto -> !repository.existsByName(dto.name())) // filter out the medicine that already exists
                .map(dto -> { // map each csv row to medicine master object
                    MedicineMaster med = ObjectMapperUtil.copyObject(dto, MedicineMaster.class); // convert the csv row to medicine master object
                    if (med.getIsDefunct() == null) { // if the isDefunct is null, set it to false
                        med.setIsDefunct(false);
                    }
                    return med;
                })
                .toList(); // convert the stream to list
        repository.saveAll(list); // save all the medicine master objects
    }

    // get all the medicine master objects
    public List<MedicineMaster> findAll() {
        return repository.findAll();
    }
}
