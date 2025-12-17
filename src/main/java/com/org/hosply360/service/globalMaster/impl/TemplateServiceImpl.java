package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.Enums.TemplateStatus;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.InsuranceProvider;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.Template;
import com.org.hosply360.dto.globalMasterDTO.TemplateDto;
import com.org.hosply360.dto.globalMasterDTO.TemplateReqDto;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.TemplateRepository;
import com.org.hosply360.service.globalMaster.TemplateService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {
    private static final Logger logger = LoggerFactory.getLogger(TemplateServiceImpl.class);
    private final EntityFetcherUtil entityFetcherUtil;
    private final TemplateRepository templateRepository;

    // create template
    @Override
    public TemplateDto createTemplate(TemplateReqDto templateReqDto) {
        ValidatorHelper.validateObject(templateReqDto); // validate the request object
        Organization organization = entityFetcherUtil.getOrganizationOrThrow(templateReqDto.getOrganizationId()); // find organization by id
        Template template = Template.builder() // build the template object
                .organization(organization)
                .templateName(templateReqDto.getTemplateName())
                .design(templateReqDto.getDesign())
                .templateStatus(templateReqDto.getTemplateStatus())
                .defunct(false)
                .build();
        Template saved = templateRepository.save(template); // save the template
        logger.info("Template Created Successfully");
        TemplateDto templateDto = ObjectMapperUtil.copyObject(saved, TemplateDto.class); // map entity to dto
        templateDto.setOrganizationId(saved.getOrganization().getId());
        return templateDto; // return the dto
    }

    // update template
    @Override
    public TemplateDto updateTemplate(TemplateReqDto templateReqDto) {
        ValidatorHelper.ValidateAllObject(templateReqDto); // validate the request object
        Template template = templateRepository.findByIdAndDefunct(templateReqDto.getId(), false) // find template by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.TEMPLATE_NOT_FOUND, HttpStatus.NOT_FOUND));
        template.setTemplateName(templateReqDto.getTemplateName());
        template.setDesign(templateReqDto.getDesign());
        template.setTemplateStatus(templateReqDto.getTemplateStatus());
        template.setDefunct(false);
        Template updated = templateRepository.save(template); // save the template
        logger.info("Template Updated Successfully");
        TemplateDto templateDto = ObjectMapperUtil.copyObject(updated, TemplateDto.class); // map entity to dto
        templateDto.setOrganizationId(updated.getOrganization().getId());
        return templateDto; // return the dto
    }

    // get template by id
    @Override
    public TemplateDto getTemplaterById(String id) {
        Template template = templateRepository.findByIdAndDefunct(id, false) // find template by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.TEMPLATE_NOT_FOUND, HttpStatus.NOT_FOUND));
        TemplateDto templateDto = ObjectMapperUtil.copyObject(template, TemplateDto.class); // map entity to dto
        templateDto.setOrganizationId(template.getOrganization().getId());
        return templateDto; // return the dto
    }

    // get all templates
    @Override
    public List<TemplateDto> getAllTemplate(String organizationId, TemplateStatus templateStatus) {
        Organization organization = entityFetcherUtil.getOrganizationOrThrow(organizationId); // find organization by id
        List<Template> templates; // list of templates
        if (templateStatus != null) { // if template status is not null
            templates = templateRepository.findAllByDefunctAndTemplateStatus(organization.getId(), false, templateStatus); // find all templates by organization id and defunct and template status
        } else {
            templates = templateRepository.findAllByDefunct(organization.getId(), false); // find all templates by organization id and defunct
        }
        return templates.stream() // stream the templates
                .map(template -> { // map the templates to dtos
                    TemplateDto templateDto = ObjectMapperUtil.copyObject(template, TemplateDto.class); // map entity to dto
                    templateDto.setOrganizationId(template.getOrganization().getId());
                    return templateDto; // return the dto
                })
                .collect(Collectors.toList()); // collect the dtos
    }

    // delete template by id
    @Override
    public void deleteTemplateById(String id) {
        ValidatorHelper.validateObject(id); // validate the id
        Template template = templateRepository.findByIdAndDefunct(id, false) // find template by id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.TEMPLATE_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Deleting template with ID: {}", id);
        template.setDefunct(true); // soft delete the template
        templateRepository.save(template); // save the template
        logger.info("Deleted template with ID: {}", id);
    }
}
