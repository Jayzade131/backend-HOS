package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.Test;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.globalMasterDTO.TestDTO;
import com.org.hosply360.dto.globalMasterDTO.TestReqDTO;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.TestRepository;
import com.org.hosply360.service.globalMaster.TestService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private static final Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);
    private final TestRepository testRepository;
    private final OrganizationMasterRepository organizationMasterRepository;
    private final EntityFetcherUtil entityFetcherUtil;

    // create test
    @Override
    public TestDTO createTest(TestReqDTO testReqDTO) {
        ValidatorHelper.validateObject(testReqDTO); // validate the request object
        if (testRepository.findByTestNameAndDefunct(testReqDTO.getName(), false).isPresent()) { // check if test already exists
            throw new GlobalMasterException(ErrorConstant.ITEM_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }
        Test test = ObjectMapperUtil.copyObject(testReqDTO, Test.class); // map dto to entity
        test.setOrganization(entityFetcherUtil.getOrganizationOrThrow(testReqDTO.getOrganizationId()));
        test.setDefunct(false);
        Test saved = testRepository.save(test); // save the test
        logger.info("Test created successfully");
        TestDTO dto = ObjectMapperUtil.copyObject(saved, TestDTO.class); // map entity to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(saved.getOrganization(), OrganizationDTO.class));
        return dto; // return the dto
    }

    // update test
    @Override
    public TestDTO updateTest(String id, TestReqDTO testReqDTO) {
        ValidatorHelper.ValidateAllObject(id, testReqDTO); // validate the request object
        Test existingTest = testRepository.findByIdAndDefunct(id, false) // find the test by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        ObjectMapperUtil.safeCopyObjectAndIgnore(testReqDTO, existingTest, List.of("id", "defunct", "organizationId")); // update the test
        existingTest.setOrganization(entityFetcherUtil.getOrganizationOrThrow(testReqDTO.getOrganizationId()));
        existingTest.setDefunct(false);
        Test updatedTest = testRepository.save(existingTest); // save the test
        logger.info("Test updated successfully");
        TestDTO dto = ObjectMapperUtil.copyObject(updatedTest, TestDTO.class); // map entity to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(updatedTest.getOrganization(), OrganizationDTO.class));
        return dto; // return the dto
    }

    // get test by id
    @Override
    public TestDTO getTestById(String id) {
        ValidatorHelper.validateObject(id); // validate the request object
        Test test = testRepository.findByIdAndDefunct(id, false) // find the test by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        logger.info("Test fetched successfully");
        TestDTO dto = ObjectMapperUtil.copyObject(test, TestDTO.class); // map entity to dto
        dto.setOrganizationDTO(ObjectMapperUtil.copyObject(test.getOrganization(), OrganizationDTO.class));
        return dto; // return the dto
    }

    // get all tests
    @Override
    public List<TestDTO> getAllTests(String organizationId) {
        logger.info("Fetching all tests for organization ID: {}", organizationId);
        Organization organization = organizationMasterRepository.findByIdAndDefunct(organizationId, false) // find the organization by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.NOT_FOUND));
        List<Test> tests = testRepository.findByAllDefunct(organization.getId(), false); // find all tests by organization id and defunct
        return tests.stream() // stream the tests
                .map(test -> { // map the tests to dto
                    TestDTO dto = ObjectMapperUtil.copyObject(test, TestDTO.class); // map entity to dto
                    dto.setOrganizationDTO(ObjectMapperUtil.copyObject(test.getOrganization(), OrganizationDTO.class));
                    return dto; // return the dto
                })
                .toList(); // convert the stream to list
    }

    // delete test
    @Override
    public void deleteTest(String id) {
        ValidatorHelper.ValidateAllObject(id); // validate the request object
        Test test = testRepository.findByIdAndDefunct(id, false) // find the test by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
        test.setDefunct(true); // soft delete the test
        testRepository.save(test); // save the test
        logger.info("Test deleted successfully with ID: {}", id);
    }
}