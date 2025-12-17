package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.PackageEDTO;
import com.org.hosply360.dto.globalMasterDTO.PackageEReqDTO;

import java.util.List;

public interface PackageEService {
    PackageEDTO createPackage(PackageEReqDTO packageReqDTO);
    PackageEDTO updatePackage(String id, PackageEReqDTO packageReqDTO);
    PackageEDTO getPackageById(String id);
    List<PackageEDTO> getAllPackage(String organizationId);
    void deletePackage(String id);
}
