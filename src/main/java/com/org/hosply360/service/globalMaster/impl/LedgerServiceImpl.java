package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.Address;
import com.org.hosply360.dao.globalMaster.Ledger;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.globalMasterDTO.AddressDTO;
import com.org.hosply360.dto.globalMasterDTO.LedgerDTO;
import com.org.hosply360.dto.globalMasterDTO.LedgerResponseDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.AddressMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.LedgerMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.globalMaster.LedgerService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private static final Logger logger = LoggerFactory.getLogger(LedgerServiceImpl.class);
    private final OrganizationMasterRepository organizationMasterRepository;
    private final LedgerMasterRepository ledgerMasterRepository;
    private final AddressMasterRepository addressMasterRepository;

    // create ledger
    @Override
    @Transactional
    public LedgerResponseDTO createLedger(LedgerDTO ledgerDTO) {
        logger.info("Creating new ledger with name: {}", ledgerDTO.getLedgerName());
        ValidatorHelper.validateObject(ledgerDTO); // validate the request object
        Organization organizations = organizationMasterRepository.findByIdAndDefunct(ledgerDTO.getOrganizationId(), false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Ledger ledger = ObjectMapperUtil.copyObject(ledgerDTO, Ledger.class); // convert the request object to ledger object
        Address address = null; // initialize the address object
        if (ledgerDTO.getAddress() != null) { // if the address is not null
            address = addressMasterRepository.save(ObjectMapperUtil.copyObject(ledgerDTO.getAddress(), Address.class)); // convert the address object to address entity
            ledger.setAddress(address);
        }
        if (ledgerDTO.getRegisteredWithGst()) { // if the ledger is registered with gst
            if (ledgerDTO.getGstNumber() == null || ledgerDTO.getGstNumber().isEmpty()) { // if the gst number is null or empty
                throw new GlobalMasterException(ErrorConstant.GST_NUMBER_REQUIRED, HttpStatus.BAD_REQUEST);
            }
            ledger.setGstNumber(ledgerDTO.getGstNumber()); // set the gst number
        } else {
            ledger.setGstNumber(null);
        }
        ledger.setOrganization(organizations);
        ledger.setDefunct(false);
        ledger.setGroup(ledgerDTO.getGroup());
        Ledger savedLedger = ledgerMasterRepository.save(ledger); // save the ledger
        logger.info("Ledger created successfully with ID: {}", savedLedger.getId());
        LedgerResponseDTO responseDto = ObjectMapperUtil.copyObject(savedLedger, LedgerResponseDTO.class); // convert the ledger object to ledger response object
        responseDto.setOrganization(ObjectMapperUtil.copyObject(savedLedger.getOrganization(), OrganizationDTO.class));
        responseDto.setAddress(ObjectMapperUtil.copyObject(savedLedger.getAddress(), AddressDTO.class));
        return responseDto; // return the response object
    }

    // update ledger
    @Override
    @Transactional
    public LedgerResponseDTO updateLedger(LedgerDTO ledgerDTO) {
        ValidatorHelper.validateObject(ledgerDTO); // validate the request object
        Ledger existingLedger = ledgerMasterRepository.findById(ledgerDTO.getId()) // find the ledger by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.LEDGER_NOT_FOUND, HttpStatus.NOT_FOUND));
        existingLedger.setLedgerName(ledgerDTO.getLedgerName());
        existingLedger.setMobile(ledgerDTO.getMobile());
        existingLedger.setEmail(ledgerDTO.getEmail());
        existingLedger.setPan(ledgerDTO.getPan());
        existingLedger.setContractPerson(ledgerDTO.getContractPerson());
        existingLedger.setBankAccountName(ledgerDTO.getBankAccountName());
        existingLedger.setBranch(ledgerDTO.getBranch());
        existingLedger.setBankName(ledgerDTO.getBankName());
        existingLedger.setBankAccountNumber(ledgerDTO.getBankAccountNumber());
        existingLedger.setIfscCode(ledgerDTO.getIfscCode());
        existingLedger.setRegisteredWithGst(ledgerDTO.getRegisteredWithGst());
        existingLedger.setGroup(ledgerDTO.getGroup());
        if (ledgerDTO.getRegisteredWithGst()) { // if the ledger is registered with gst
            if (ledgerDTO.getGstNumber() == null || ledgerDTO.getGstNumber().isEmpty()) { // validate the gst number
                throw new GlobalMasterException(ErrorConstant.GST_NUMBER_REQUIRED, HttpStatus.BAD_REQUEST);
            }
            existingLedger.setGstNumber(ledgerDTO.getGstNumber());
        } else {
            existingLedger.setGstNumber(null);
        }
        // ===== Address handling similar to Doctor permanent address =====
        if (ledgerDTO.getAddress() != null) { // if the address is not null
            if (ledgerDTO.getAddress().getId() != null && !ledgerDTO.getAddress().getId().isEmpty()) { // if the address id is not null
                // update existing address
                Address existingAddress = addressMasterRepository.findById(ledgerDTO.getAddress().getId()) // find the address by id
                        .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ADDRESS_NOT_FOUND, HttpStatus.NOT_FOUND));
                ObjectMapperUtil.safeCopyObjectAndIgnore(ledgerDTO.getAddress(), existingAddress, List.of("id")); // update the address
                existingLedger.setAddress(addressMasterRepository.save(existingAddress)); // save the address
            } else {
                // new address
                Address newAddress = addressMasterRepository.save(ObjectMapperUtil.copyObject(ledgerDTO.getAddress(), Address.class)); // save the new address
                existingLedger.setAddress(newAddress);
            }
        }
        Ledger updatedLedger = ledgerMasterRepository.save(existingLedger); // save the updated ledger
        logger.info("Ledger updated successfully with ID: {}", updatedLedger.getId());
        LedgerResponseDTO responseDto = ObjectMapperUtil.copyObject(updatedLedger, LedgerResponseDTO.class); // convert the ledger object to ledger response object
        responseDto.setOrganization(ObjectMapperUtil.copyObject(updatedLedger.getOrganization(), OrganizationDTO.class));
        responseDto.setAddress(ObjectMapperUtil.copyObject(updatedLedger.getAddress(), AddressDTO.class));
        return responseDto; // return the response object
    }

    // get ledger by id
    @Override
    public LedgerResponseDTO getLedgerById(String id) {
        logger.info("Fetching ledger with ID: {}", id);
        Ledger ledger = ledgerMasterRepository.findById(id) // find the ledger by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.LEDGER_NOT_FOUND, HttpStatus.NOT_FOUND));
        LedgerResponseDTO dto = ObjectMapperUtil.copyObject(ledger, LedgerResponseDTO.class); // convert the ledger object to ledger response object
        dto.setOrganization(ObjectMapperUtil.copyObject(ledger.getOrganization(), OrganizationDTO.class));
        dto.setAddress(ObjectMapperUtil.copyObject(ledger.getAddress(), AddressDTO.class));
        logger.info("Fetched ledger successfully with ID: {}", id);
        return dto; // return the response object
    }

    // delete ledger by id
    @Override
    public void deleteLedger(String id) {
        logger.info("Deleting (soft delete) ledger with ID: {}", id);
        Ledger ledger = ledgerMasterRepository.findById(id) // find the ledger by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.LEDGER_NOT_FOUND, HttpStatus.NOT_FOUND));
        ledger.setDefunct(true); // mark the ledger as defunct
        ledgerMasterRepository.save(ledger); // save the ledger
        logger.info("Ledger marked as defunct with ID: {}", id);
    }

    // get all ledgers by organization id
    @Override
    public List<LedgerResponseDTO> getAllLedger(String organizationId) {
        logger.info("Fetching all ledgers for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // find the organization by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<Ledger> ledgers = ledgerMasterRepository.findByOrganizationIdAndDefunct(organization.getId(), false); // find all ledgers by organization id
        logger.info("Found {} ledgers for organization ID: {}", ledgers.size(), organizationId);
        return ledgers.stream() // convert the list of ledger objects to list of ledger response objects
                .map(led -> { // map each ledger object to ledger response object
                    LedgerResponseDTO dto = ObjectMapperUtil.copyObject(led, LedgerResponseDTO.class); // convert the ledger object to ledger response object
                    dto.setOrganization(ObjectMapperUtil.copyObject(led.getOrganization(), OrganizationDTO.class));
                    dto.setAddress(ObjectMapperUtil.copyObject(led.getAddress(), AddressDTO.class));
                    return dto;
                })
                .collect(Collectors.toList()); // collect the list of ledger response objects
    }
}
