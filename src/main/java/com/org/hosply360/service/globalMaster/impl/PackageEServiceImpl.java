package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.BillingItemGroup;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.PackageE;
import com.org.hosply360.dao.globalMaster.Test;
import com.org.hosply360.dto.globalMasterDTO.BillingItemGroupDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.PackageEDTO;
import com.org.hosply360.dto.globalMasterDTO.PackageEReqDTO;
import com.org.hosply360.dto.globalMasterDTO.TestDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.exception.pathologyException;
import com.org.hosply360.repository.globalMasterRepo.PackageERepository;
import com.org.hosply360.repository.globalMasterRepo.TestRepository;
import com.org.hosply360.service.globalMaster.PackageEService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageEServiceImpl implements PackageEService {

    private static final Logger logger = LoggerFactory.getLogger(PackageEServiceImpl.class);
    private final PackageERepository packageRepository;
    private final TestRepository testRepository;
    private final EntityFetcherUtil entityFetcherUtil;

    // create package
    @Override
    public PackageEDTO createPackage(PackageEReqDTO packageReqDTO) {
        ValidatorHelper.validateObject(packageReqDTO); // validate the package request dto
        BillingItemGroup billingItemGroup = entityFetcherUtil.getBillingItemGroupOrThrow(packageReqDTO.getBillingGrpId()); // get billing item group by id
        List<Test> testList = testRepository.findAllByIdInTestAndDefunct(false, packageReqDTO.getTestId()); // find all tests by id in test and defunct
        if (CollectionUtils.isEmpty(testList)) { // check if test list is empty
            throw new pathologyException(ErrorConstant.TEST_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        if (packageRepository.findByPackageNameAndDefunct(packageReqDTO.getPackageName(), false).isPresent()) { // check if package name already exists
            throw new GlobalMasterException(ErrorConstant.PACKAGE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        PackageE packageE = ObjectMapperUtil.copyObject(packageReqDTO, PackageE.class); // copy package request dto to package entity
        packageE.setOrganization(entityFetcherUtil.getOrganizationOrThrow(packageReqDTO.getOrganization()));
        packageE.setBillingItemGroup(billingItemGroup);
        packageE.setTestName(testList);
        packageE.setDefunct(false);
        packageE.setTotalAmount(testList.stream().mapToDouble(Test::getAmount).sum());
        PackageE saved = packageRepository.save(packageE); // save package entity
        logger.info("Package created successfully");
        PackageEDTO packageEDTO = ObjectMapperUtil.copyObject(saved, PackageEDTO.class); // copy package entity to package dto
        packageEDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getOrganization(), OrganizationDTO.class));
        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(saved.getBillingItemGroup(), BillingItemGroupDTO.class); // copy billing item group entity to billing item group dto
        billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItemGroup.getOrganization(), OrganizationDTO.class));
        packageEDTO.setBillingItemGroupDTO(billingItemGroupDTO);
        packageEDTO.setTestName(ObjectMapperUtil.copyListObject(saved.getTestName(), TestDTO.class)); // copy test entity to test dto
        return packageEDTO; // return package dto
    }

    // update package
    @Override
    public PackageEDTO updatePackage(String id, PackageEReqDTO packageReqDTO) {
        ValidatorHelper.ValidateAllObject(id, packageReqDTO); // validate the request object
        PackageE existing = entityFetcherUtil.getPackageEOrThrow(id); // get package entity by id
        BillingItemGroup billingItemGroup = entityFetcherUtil.getBillingItemGroupOrThrow(packageReqDTO.getBillingGrpId()); // get billing item group entity by id
        List<Test> testList = testRepository.findAllByIdInTestAndDefunct(false, packageReqDTO.getTestId()); // get test entity by id
        if (CollectionUtils.isEmpty(testList)) { // check if test list is empty
            throw new pathologyException(ErrorConstant.TEST_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        ObjectMapperUtil.safeCopyObjectAndIgnore(packageReqDTO, existing,
                List.of("id", "defunct", "organization", "billingItemGroup", "testName")); // copy package request dto to package entity
        existing.setOrganization(entityFetcherUtil.getOrganizationOrThrow(packageReqDTO.getOrganization()));
        existing.setBillingItemGroup(billingItemGroup);
        existing.setTestName(testList);
        existing.setDefunct(false);
        existing.setTotalAmount(testList.stream().mapToDouble(Test::getAmount).sum());
        PackageE updated = packageRepository.save(existing); // save package entity
        logger.info("Package updated successfully");
        PackageEDTO packageEDTO = ObjectMapperUtil.copyObject(updated, PackageEDTO.class); // copy package entity to package dto
        packageEDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(updated.getOrganization(), OrganizationDTO.class));
        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(updated.getBillingItemGroup(), BillingItemGroupDTO.class); // copy billing item group entity to billing item group dto
        billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(billingItemGroup.getOrganization(), OrganizationDTO.class));
        packageEDTO.setBillingItemGroupDTO(billingItemGroupDTO);
        packageEDTO.setTestName(ObjectMapperUtil.copyListObject(updated.getTestName(), TestDTO.class));
        return packageEDTO; // return package dto
    }

    // get package by id
    @Override
    public PackageEDTO getPackageById(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        PackageE packageE = entityFetcherUtil.getPackageEOrThrow(id); // get package entity by id
        logger.info("Package fetched successfully");
        PackageEDTO packageEDTO = ObjectMapperUtil.copyObject(packageE, PackageEDTO.class); // copy package entity to package dto
        packageEDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(packageE.getOrganization(), OrganizationDTO.class));
        BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(packageE.getBillingItemGroup(), BillingItemGroupDTO.class); // copy billing item group entity to billing item group dto
        billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(packageE.getBillingItemGroup().getOrganization(), OrganizationDTO.class));
        packageEDTO.setBillingItemGroupDTO(billingItemGroupDTO);
        packageEDTO.setTestName(ObjectMapperUtil.copyListObject(packageE.getTestName(), TestDTO.class));
        packageEDTO.setTotalAmount(packageE.getTotalAmount());
        return packageEDTO; // return package dto
    }

    // get all packages
    @Override
    public List<PackageEDTO> getAllPackage(String organizationId) {
        logger.info("Fetching all packages for organization ID: {}", organizationId);
        Organization organization = entityFetcherUtil.getOrganizationOrThrow(organizationId); // get organization entity by id
        List<PackageE> packages = packageRepository.findByOrganizationIdAndDefunct(organization.getId(), false); // get all packages by organization id and defunct
        return packages.stream() // stream the packages
                .map(packageE -> { // map the packages to package dto
                    PackageEDTO packageEDTO = ObjectMapperUtil.copyObject(packageE, PackageEDTO.class); // copy package entity to package dto
                    packageEDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(packageE.getOrganization(), OrganizationDTO.class));
                    BillingItemGroupDTO billingItemGroupDTO = ObjectMapperUtil.copyObject(packageE.getBillingItemGroup(), BillingItemGroupDTO.class); // copy billing item group entity to billing item group dto
                    billingItemGroupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(packageE.getBillingItemGroup().getOrganization(), OrganizationDTO.class));
                    packageEDTO.setBillingItemGroupDTO(billingItemGroupDTO);
                    packageEDTO.setTestName(ObjectMapperUtil.copyListObject(packageE.getTestName(), TestDTO.class));
                    packageEDTO.setTotalAmount(packageE.getTotalAmount());
                    return packageEDTO; // return package dto
                })
                .collect(Collectors.toList()); // collect the packages to list
    }

    // delete package
    @Override
    public void deletePackage(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        PackageE packageE = entityFetcherUtil.getPackageEOrThrow(id); // get package entity by id
        packageE.setDefunct(true); // soft delete the package
        packageRepository.save(packageE); // save the package entity
        logger.info("Package deleted successfully with ID: {}", id);
    }
}
