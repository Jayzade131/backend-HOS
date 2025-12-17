package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.Currency;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.globalMasterDTO.CurrencyDTO;
import com.org.hosply360.dto.globalMasterDTO.CurrencyReqDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.CurrencyRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.globalMaster.CurrencyService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyServiceImpl.class);
    private final CurrencyRepository currencyRepository;
    private final OrganizationMasterRepository organizationMasterRepository;

    // create currency
    @Override
    @Transactional
    public CurrencyDTO createCurrency(CurrencyReqDTO currencyDto) {
        ValidatorHelper.validateObject(currencyDto); // validate the currency dto
        Organization organization = organizationMasterRepository.findByIdAndDefunct(currencyDto.getOrganizationDTO(), false) // validate the organization dto
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (currencyRepository.findByCodeandDefunct(currencyDto.getCode(), false).isPresent()) { // validate the currency code
            logger.info("Language code {} already exists", currencyDto.getCode());
            throw new GlobalMasterException(ErrorConstant.CURRENCY_CODE_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        Currency currency = ObjectMapperUtil.copyObject(currencyDto, Currency.class); // copy the currency dto to the currency object
        currency.setOrganization(organization);
        currency.setDefunct(false);
        Currency saved = currencyRepository.save(currency); // save the currency object
        logger.info("Currency created successfully");
        CurrencyDTO currencyDTO = ObjectMapperUtil.copyObject(saved, CurrencyDTO.class); // convert the currency object to dto
        currencyDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getOrganization(), OrganizationDTO.class));
        return currencyDTO;  // return the currency dto
    }

    // update currency
    @Override
    @Transactional
    public CurrencyDTO updateCurrency(String id, CurrencyReqDTO currencyDto) {
        ValidatorHelper.ValidateAllObject(id, currencyDto); // validate the currency dto
        Currency existing = currencyRepository.findByIdandDefunct(currencyDto.getId(), false) // validate the currency id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.CURRENCY_NOT_FOUND, HttpStatus.NOT_FOUND));
        Organization organization = organizationMasterRepository.findByIdAndDefunct(currencyDto.getOrganizationDTO(), false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Currency currency = ObjectMapperUtil.copyObject(currencyDto, Currency.class); // copy the currency dto to the currency object
        currency.setOrganization(organization);
        currency.setDefunct(false);
        ObjectMapperUtil.safeCopyObjectAndIgnore(currencyDto, existing, List.of("id", "defunct", "organizationId")); // update the currency object
        Currency updatedCurrency = currencyRepository.save(currency); // save the currency object
        logger.info("Currency updated successfully");
        CurrencyDTO currencyDTO = ObjectMapperUtil.copyObject(updatedCurrency, CurrencyDTO.class); // convert the currency object to dto
        currencyDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(updatedCurrency.getOrganization(), OrganizationDTO.class));
        return currencyDTO; // return the currency dto
    }

    // get currency by id
    @Override
    public CurrencyDTO getCurrencyById(String id) {
        Currency currency = currencyRepository.findByIdandDefunct(id, false) // validate the currency id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.CURRENCY_NOT_FOUND, HttpStatus.NOT_FOUND));
        CurrencyDTO currencyDTO = ObjectMapperUtil.copyObject(currency, CurrencyDTO.class); // convert the currency object to dto
        currencyDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(currency.getOrganization(), OrganizationDTO.class));
        return currencyDTO; // return the currency dto
    }

    // get all currencies
    @Override
    public List<CurrencyDTO> getAllCurrencies(String organizationId) {
        logger.info("Fetching all currency for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<Currency> currencies = currencyRepository.findAllByDefunct(organization.getId(), false); // get all currencies
        return currencies.stream() // convert the currency objects to dtos
                .map(currency -> { // map each currency object to dto
                    CurrencyDTO dto = ObjectMapperUtil.copyObject(currency, CurrencyDTO.class); // convert the currency object to dto
                    dto.setOrganizationDTO(ObjectMapperUtil.copyObject(currency.getOrganization(), OrganizationDTO.class));
                    return dto; // return the dto
                })
                .collect(Collectors.toList()); // collect the dtos
    }

    // delete currency by id
    @Override
    public void deleteCurrencyById(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the currency id
        Currency currency = currencyRepository.findByIdandDefunct(id, false) // validate the currency id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.CURRENCY_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Deleting currency with ID: {}", id);
        currency.setDefunct(true); // soft delete the currency
        currencyRepository.save(currency); // save the currency object
        logger.info("deleted currency with ID: {}", id);
    }
}
