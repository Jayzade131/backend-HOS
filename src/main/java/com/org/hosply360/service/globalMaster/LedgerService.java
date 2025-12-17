package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.LedgerDTO;
import com.org.hosply360.dto.globalMasterDTO.LedgerResponseDTO;

import java.util.List;

public interface LedgerService {

    LedgerResponseDTO createLedger(LedgerDTO ledgerDTO);
    LedgerResponseDTO updateLedger(LedgerDTO ledgerDTO);

    LedgerResponseDTO getLedgerById(String id);

    void deleteLedger(String id);

    List<LedgerResponseDTO> getAllLedger(String organizationId);

}
