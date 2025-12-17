package com.org.hosply360.service.globalMaster.impl;


import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.Language;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.globalMasterDTO.LanguageDTO;
import com.org.hosply360.dto.globalMasterDTO.LanguageReqDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.LanguageMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.globalMaster.LanguageMasterService;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LanguageMasterServiceImpl implements LanguageMasterService {

    private static final Logger logger = LoggerFactory.getLogger(LanguageMasterServiceImpl.class);
    private final OrganizationMasterRepository organizationMasterRepository;
    private final LanguageMasterRepository languageMasterRepository;

    // create language
    @Override
    @Transactional
    public LanguageDTO createLanguage(LanguageReqDTO languageDTO) {
        ValidatorHelper.validateObject(languageDTO); // validate the request object
        Organization organization = organizationMasterRepository.findByIdAndDefunct(languageDTO.getOrganization(), false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (languageMasterRepository.findByCodeAndDefunct(languageDTO.getCode(), false).isPresent()) { // validate the language code
            logger.info("Language code {} already exists", languageDTO.getCode());
            throw new GlobalMasterException(ErrorConstant.LANGUAGE_CODE_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }
        Language lang = ObjectMapperUtil.copyObject(languageDTO, Language.class); // copy the language dto to the language object
        lang.setOrganization(organization);
        lang.setDefunct(false);
        Language language = languageMasterRepository.save(lang); // save the language object
        logger.info("Language created successfully");
        LanguageDTO languagedto = ObjectMapperUtil.copyObject(language, LanguageDTO.class); // convert the language object to dto
        languagedto.setOrganizationDTO(ObjectMapperUtil.copyObject(language.getOrganization(), OrganizationDTO.class));
        return languagedto; // return the language dto
    }

    // get all languages
    @Override
    public List<LanguageDTO> getAllLanguages(String organizationId) {
        logger.info("Fetching all language for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<Language> languages = languageMasterRepository.findAllByDefunct(organization.getId(), false); // get all languages
        return languages.stream() // convert the language object to dto
                .map(language -> { // map each language object to dto
                    LanguageDTO dto = ObjectMapperUtil.copyObject(language, LanguageDTO.class); // convert the language object to dto
                    dto.setOrganizationDTO(ObjectMapperUtil.copyObject(language.getOrganization(), OrganizationDTO.class));
                    return dto; // return the dto
                })
                .toList(); // collect the dtos
    }

    // get language by id
    @Override
    public LanguageDTO getLanguageById(String id) {
        Language lang = languageMasterRepository.findByIdAndDefunct(id, false) // validate the language id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.LANGUAGE_NOT_FOUND, HttpStatus.NOT_FOUND));
        LanguageDTO languagedto = ObjectMapperUtil.copyObject(lang, LanguageDTO.class); // convert the language object to dto
        languagedto.setOrganizationDTO(ObjectMapperUtil.copyObject(lang.getOrganization(), OrganizationDTO.class));
        return languagedto; // return the dto
    }

    // update language
    @Override
    @Transactional
    public LanguageDTO updateLanguage(String id, LanguageReqDTO dto) {
        ValidatorHelper.ValidateAllObject(id, dto); // validate the request object
        Organization organization = organizationMasterRepository.findByIdAndDefunct(dto.getOrganization(), false) // validate the organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        Language existingLanguage = languageMasterRepository.findByIdAndDefunct(dto.getId(), false) // validate the language id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.LANGUAGE_NOT_FOUND, HttpStatus.NOT_FOUND));
        Language lang = ObjectMapperUtil.copyObject(dto, Language.class); // copy the language dto to the language object
        lang.setOrganization(organization);
        lang.setDefunct(false);
        ObjectMapperUtil.safeCopyObjectAndIgnore(dto, existingLanguage, List.of("id", "defunct", "registrationNo", "organizationId")); // update the language object
        Language updated = languageMasterRepository.save(lang); // save the language object
        logger.info("Language with ID {} updated successfully", dto.getId());
        LanguageDTO languagedto = ObjectMapperUtil.copyObject(updated, LanguageDTO.class); // convert the language object to dto
        languagedto.setOrganizationDTO(ObjectMapperUtil.copyObject(updated.getOrganization(), OrganizationDTO.class));
        return languagedto; // return the dto
    }

    // delete language
    @Override
    public void deleteLanguageById(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        Language language = languageMasterRepository.findByIdAndDefunct(id, false) // validate the language id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.LANGUAGE_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Deleting language with ID: {}", id);
        language.setDefunct(true); // soft delete the language
        languageMasterRepository.save(language); // save the language object
        logger.info("deleted language with ID: {}", id);
    }
}
