package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dao.globalMaster.MedicineMaster;
import com.org.hosply360.dto.globalMasterDTO.MedicineCsvRowDto;

import java.util.List;

public interface MedicineMasterService {
    void saveFromCsv(List<MedicineCsvRowDto> csvData);
    List<MedicineMaster> findAll();

}
