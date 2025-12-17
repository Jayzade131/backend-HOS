package com.org.hosply360.service.globalMaster;

import com.org.hosply360.dto.globalMasterDTO.CurrencyDTO;
import com.org.hosply360.dto.globalMasterDTO.CurrencyReqDTO;

import java.util.List;

public interface CurrencyService {
    CurrencyDTO createCurrency(CurrencyReqDTO currencyDto);
    CurrencyDTO updateCurrency(String id,CurrencyReqDTO currencyDto);
    CurrencyDTO getCurrencyById(String id);
    List<CurrencyDTO> getAllCurrencies(String organizationId);
    void deleteCurrencyById(String id);

}
