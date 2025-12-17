package com.org.hosply360.repository.IPD;


import com.org.hosply360.dto.IPDDTO.BedDataDTO;
import com.org.hosply360.dto.IPDDTO.BedResponseDTO;

import java.util.List;

public interface IPDBedCustomRepo {
    BedResponseDTO getBedsByWardId(String orgId, String wardId);
}
