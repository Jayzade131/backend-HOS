package com.org.hosply360.service.pathology.impl;

import com.org.hosply360.constant.Enums.PackageTestStatus;
import com.org.hosply360.constant.Enums.TestSource;
import com.org.hosply360.constant.Enums.TestStatus;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.PackageE;
import com.org.hosply360.dao.globalMaster.Test;
import com.org.hosply360.dao.globalMaster.TestParameterMaster;
import com.org.hosply360.dao.pathology.PackageTestReport;
import com.org.hosply360.dao.pathology.TestManager;
import com.org.hosply360.dao.pathology.TestReport;
import com.org.hosply360.dao.pathology.TestReportParameter;
import com.org.hosply360.dto.frontDeskDTO.PatientDTO;
import com.org.hosply360.dto.pathologyDTO.GetResPacTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.GetResTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportItem;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportItemDTO;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportPdfResponseDTO;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportReqDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;
import com.org.hosply360.dto.pathologyDTO.PatientBillInfoDTO;
import com.org.hosply360.dto.pathologyDTO.TestDataReqDTO;
import com.org.hosply360.dto.pathologyDTO.TestManagerDTO;
import com.org.hosply360.dto.pathologyDTO.TestReportDTO;
import com.org.hosply360.dto.pathologyDTO.TestReportParameterDTO;
import com.org.hosply360.dto.pathologyDTO.TestReportPdfResponseDTO;
import com.org.hosply360.dto.pathologyDTO.TestReportReqDTO;
import com.org.hosply360.exception.pathologyException;
import com.org.hosply360.repository.PathologyRepo.PackageTestReportRepository;
import com.org.hosply360.repository.PathologyRepo.TestReportRepository;
import com.org.hosply360.service.pathology.TestReportService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.Others.UserUtilis;
import com.org.hosply360.util.encryptionUtil.EncryptionUtil;
import com.org.hosply360.util.mapper.HeaderFooterMapperUtil;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.org.hosply360.util.mapper.PatientMapperUtil.buildPatientBillInfo;

@Service
@RequiredArgsConstructor
public class TestReportServiceImpl implements TestReportService {

    private static final Logger logger = LoggerFactory.getLogger(TestReportServiceImpl.class);
    private final TestReportRepository testReportRepository;
    private final PackageTestReportRepository packageTestReportRepository;
    private final EntityFetcherUtil entityFetcherUtil;

    // decrypt the patient dto for test report
    private void decryptPatientDTOForTestReport(GetResTestReportDTO dto) {
        dto.setFirstName(EncryptionUtil.decrypt(dto.getFirstName()));
        dto.setLastName(EncryptionUtil.decrypt(dto.getLastName()));
        dto.setPhoneNo(EncryptionUtil.decrypt(dto.getPhoneNo()));
    }

    // decrypt the patient dto for package test report
    private void decryptPatientDTOForPackageTestReport(GetResPacTestReportDTO dto) {
        dto.setFirstName(EncryptionUtil.decrypt(dto.getFirstName()));
        dto.setLastName(EncryptionUtil.decrypt(dto.getLastName()));
        dto.setPhoneNo(EncryptionUtil.decrypt(dto.getPhoneNo()));
    }

    // decrypt the patient dto for package test report
    @Override
    public String createTestReport(TestReportReqDTO reqDTO) {

        ValidatorHelper.validateObject(reqDTO);

        TestManager testManager = entityFetcherUtil.getTestMangerOrThrow(reqDTO.getTestManagerId());

        // business rules
        if (TestStatus.PENDING.equals(testManager.getStatus())) {
            throw new pathologyException(ErrorConstant.CHANGE_THE_STATUS, HttpStatus.BAD_REQUEST);
        }

        if (TestSource.PACKAGE.equals(testManager.getSource())) {
            throw new pathologyException(ErrorConstant.INVALID_TEST_SOURCE, HttpStatus.BAD_REQUEST);
        }

        // prevent duplicate
        boolean exists = testReportRepository.findByTestManagerIdAndTestIdAndDefunctFalse(reqDTO.getTestManagerId(), reqDTO.getTestDataReqDTOS().getTestId()).isPresent();

        if (exists) {
            throw new pathologyException(ErrorConstant.TEST_REPORT_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        TestReport testReport = TestReport.builder().defunct(false).build();

        buildOrUpdateTestReport(testReport, reqDTO, testManager);

        return testReportRepository.save(testReport).getId();
    }


    // map test values to parameters
    private List<TestReportParameter> mapTestValuesToParameters(Test test, List<TestReportParameter> inputParameters) {
        List<TestParameterMaster> masters = test.getTestParameterMasters(); //get the test parameter masters
        List<TestReportParameter> reportParams = new ArrayList<>(); //create a list to store the report parameters
        if (Objects.isNull(masters) || Objects.isNull(inputParameters))
            return reportParams; //check the masters and input parameters are null or not
        for (TestParameterMaster master : masters) { //iterate over the masters
            TestReportParameter matchedInput = inputParameters.stream() //iterate over the input parameters
                    .filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(master.getName())) //filter the input parameters
                    .findFirst() //get the first matched input parameter
                    .orElseThrow(() -> new pathologyException(ErrorConstant.MISSING_PARAMETER + master.getName(), HttpStatus.BAD_REQUEST));
            reportParams.add(TestReportParameter.builder() //build the report parameters
                    .name(master.getName()).unit(master.getUnit()).referenceRange(master.getReferenceRange()).value(matchedInput.getValue()).build());
        }
        return reportParams; //return the report parameters
    }

    private TestReport buildOrUpdateTestReport(TestReport testReport, TestReportReqDTO reqDTO, TestManager testManager) {

        TestDataReqDTO testData = Optional.ofNullable(reqDTO.getTestDataReqDTOS()).filter(d -> d.getTestId() != null).orElseThrow(() -> new pathologyException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST));

        validateParameterValues(testData.getParametersValues());

        Test test = entityFetcherUtil.getTestOrThrow(testData.getTestId());

        List<TestReportParameter> parameters = mapTestValuesToParameters(test, testData.getParametersValues());

        testReport.setTestManager(testManager);
        testReport.setOrganization(entityFetcherUtil.getOrganizationOrThrow(reqDTO.getOrganizationId()));
        testReport.setPatient(entityFetcherUtil.getPatientOrThrow(reqDTO.getPatientId()));
        testReport.setDoctor(entityFetcherUtil.getDoctorOrThrow(reqDTO.getDoctorId()));
        testReport.setTest(test);
        testReport.setReportDate(LocalDateTime.now());
        testReport.setParameters(parameters);

        return testReport;
    }


    private void validateParameterValues(List<TestReportParameter> parameters) {

        if (parameters == null || parameters.isEmpty()) {
            throw new pathologyException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }

        for (TestReportParameter param : parameters) {

            if (param.getName() == null) {
                throw new pathologyException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
            }

            if (param.getValue() == null || param.getValue().trim().isEmpty()) {
                throw new pathologyException(ErrorConstant.TEST_PARAMETER_VALUE_EMPTY + param.getName(), HttpStatus.BAD_REQUEST);
            }
        }
    }


    // update test report
    @Override
    public String updateTestReport(TestReportReqDTO reqDTO) {

        ValidatorHelper.validateObject(reqDTO);

        TestReport testReport = entityFetcherUtil.getTestReportOrThrow(reqDTO.getTestReportId());

        TestManager testManager = entityFetcherUtil.getTestMangerOrThrow(reqDTO.getTestManagerId());

        buildOrUpdateTestReport(testReport, reqDTO, testManager);

        return testReportRepository.save(testReport).getId();
    }


    // create package test report
    @Override
    public String createPackageTestReport(PackageTestReportReqDTO dto) {
        ValidatorHelper.validateObject(dto); //validate the request dto
        TestManager testManager = entityFetcherUtil.getTestMangerOrThrow(dto.getTestManagerId()); //get the test manager
        if (TestStatus.PENDING.equals(testManager.getStatus())) { //check the test manager status
            throw new pathologyException(ErrorConstant.CHANGE_THE_STATUS, HttpStatus.BAD_REQUEST);
        }
        if (TestSource.INDIVIDUAL.equals(testManager.getSource())) { //check the test source
            throw new pathologyException(ErrorConstant.INVALID_TEST_SOURCE, HttpStatus.BAD_REQUEST);
        }
        List<TestDataReqDTO> testDataInputs = Optional.ofNullable(dto.getTestDataReqDTOS()) //get the test data
                .filter(list -> !list.isEmpty()) //filter the test data
                .orElseThrow(() -> new pathologyException(ErrorConstant.TEST_DATA_IS_REQUIRED, HttpStatus.BAD_REQUEST));
        List<Test> packageTests = Optional.ofNullable(testManager.getPackageE()) //get the package tests
                .map(PackageE::getTestName) //get the test name
                .filter(list -> !list.isEmpty()) //filter the test name
                .orElseThrow(() -> new pathologyException(ErrorConstant.NO_TEST_FOUND, HttpStatus.BAD_REQUEST));
        List<PackageTestReportItem> reportItems = packageTests.stream() //stream the package tests
                .map(packageTest -> { //map the package tests
                    String testId = Optional.ofNullable(packageTest) //get the test id
                            .map(Test::getId) //get the test id
                            .orElseThrow(() -> new pathologyException(ErrorConstant.INVALID_TEST_PACKAGE, HttpStatus.INTERNAL_SERVER_ERROR));
                    TestDataReqDTO input = testDataInputs.stream() //get the test data
                            .filter(data -> testId.equals(data.getTestId())) //filter the test data
                            .findFirst() //get the first matched input parameter
                            .orElseThrow(() -> new pathologyException(ErrorConstant.MISSING_INPUT_FOR_TEST_ID + testId, HttpStatus.INTERNAL_SERVER_ERROR));
                    List<TestReportParameter> parameters = mapTestValuesToParameters(packageTest, input.getParametersValues()); //map the test values to parameters
                    return PackageTestReportItem.builder() //build the package test report item
                            .test(packageTest).reportDate(LocalDateTime.now()).parameters(parameters).build();
                }).collect(Collectors.toList()); //collect the package tests

        PackageTestReport packageTestReport = PackageTestReport.builder() //build the package test report
                .organization(entityFetcherUtil.getOrganizationOrThrow(dto.getOrganizationId())).testManager(testManager).doctor(entityFetcherUtil.getDoctorOrThrow(dto.getDoctorId())).patient(entityFetcherUtil.getPatientOrThrow(dto.getPatientId())).packageTestReportItem(reportItems).status(dto.getStatus()).defunct(false).reportDate(LocalDateTime.now()).build();
        String reportId = packageTestReportRepository.save(packageTestReport).getId(); //save the package test report
        logger.info("Package test report [{}] saved successfully", reportId);
        return reportId; //return the report id
    }

    // update package test report
    @Override
    public String updatePackageTestReport(PackageTestReportReqDTO dto) {
        ValidatorHelper.validateObject(dto); //validate the request dto
        PackageTestReport packageTestReport = entityFetcherUtil.getPackageTestReportOrThrow(dto.getPackageTestReportId()); //get the package test report
        TestManager testManager = entityFetcherUtil.getTestMangerOrThrow(dto.getTestManagerId()); //get the test manager
        if (TestStatus.PENDING.equals(testManager.getStatus())) { //check the test manager status
            throw new pathologyException(ErrorConstant.CHANGE_THE_STATUS, HttpStatus.BAD_REQUEST);
        }
        if (TestSource.INDIVIDUAL.equals(testManager.getSource())) { //check the test source
            throw new pathologyException(ErrorConstant.INVALID_TEST_SOURCE, HttpStatus.BAD_REQUEST);
        }
        if (PackageTestStatus.COMPLETED.equals(packageTestReport.getStatus())) { //check the package test status
            throw new pathologyException(ErrorConstant.DO_NOT_CHANGE_THE_STATUS, HttpStatus.BAD_REQUEST);
        }
        List<TestDataReqDTO> testDataReqDTOList = Optional.ofNullable(dto.getTestDataReqDTOS()) //get the test data
                .filter(list -> !list.isEmpty()) //filter the test data
                .orElseThrow(() -> new pathologyException(ErrorConstant.TEST_DATA_IS_REQUIRED, HttpStatus.BAD_REQUEST));
        List<Test> packageTests = Optional.ofNullable(testManager.getPackageE().getTestName()) //get the package tests
                .filter(list -> !list.isEmpty()) //filter the package tests
                .orElseThrow(() -> new pathologyException(ErrorConstant.NO_TEST_FOUND_IN_PACKAGE, HttpStatus.BAD_REQUEST));
        List<PackageTestReportItem> updatedItems = packageTests.stream() //stream the package tests
                .filter(Objects::nonNull) //filter the package tests
                .map(packageTest -> {
                    TestDataReqDTO matchingInput = testDataReqDTOList.stream() //get the test data
                            .filter(input -> input.getTestId() != null && //filter the test data
                                    input.getTestId().equals(packageTest.getId())) //filter the test data
                            .findFirst() //get the first matched input parameter
                            .orElse(null);
                    List<TestReportParameter> parameters = (matchingInput != null) //check the test data
                            ? mapTestValuesToParameters(packageTest, matchingInput.getParametersValues()) //map the test values to parameters
                            : new ArrayList<>(); //return empty list
                    PackageTestReportItem item = new PackageTestReportItem(); //build the package test report item
                    item.setTest(packageTest);
                    item.setReportDate(LocalDateTime.now());
                    item.setParameters(parameters);
                    return item; //return the package test report item
                }).collect(Collectors.toList()); //collect the package tests
        packageTestReport.setOrganization(entityFetcherUtil.getOrganizationOrThrow(dto.getOrganizationId()));
        packageTestReport.setTestManager(testManager);
        packageTestReport.setPatient(entityFetcherUtil.getPatientOrThrow(dto.getPatientId()));
        packageTestReport.setDoctor(entityFetcherUtil.getDoctorOrThrow(dto.getDoctorId()));
        packageTestReport.setStatus(dto.getStatus());
        packageTestReport.setReportDate(LocalDateTime.now());
        packageTestReport.setPackageTestReportItem(updatedItems);
        PackageTestReport updated = packageTestReportRepository.save(packageTestReport); //save the package test report
        logger.info("Package test report updated successfully");
        return updated.getId(); //return the updated package test report id
    }

    // get test report by id
    @Override
    public TestReportDTO getTestReportById(String reportId) {
        logger.info("Fetching test report with ID: {}", reportId);
        if (!StringUtils.hasText(reportId)) { //check the report id
            throw new pathologyException(ErrorConstant.REPORT_ID_IS_REQUIRED, HttpStatus.BAD_REQUEST);
        }
        TestReport report = testReportRepository.findByIdAndDefunct(reportId, false) //get the test report
                .orElseThrow(() -> new pathologyException(ErrorConstant.TEST_REPORT_NOT_FOUND, HttpStatus.NOT_FOUND));
        TestReportDTO dto = ObjectMapperUtil.copyObject(report, TestReportDTO.class); //copy the test report to dto
        dto.setOrganizationId(report.getOrganization().getId()); //set the organization id
        PatientDTO patientDTO = ObjectMapperUtil.copyObject(report.getPatient(), PatientDTO.class); //copy the patient to dto
        dto.setPatId(patientDTO.getId());
        dto.setPId(patientDTO.getPId());
        dto.setDoctorId(report.getDoctor().getId());
        dto.setTestManagerDTO(ObjectMapperUtil.copyObject(report.getTestManager(), TestManagerDTO.class)); //set the test manager dto
        dto.setReportDate(report.getReportDate());
        dto.setParameters(report.getParameters());
        return dto; //return the test report dto
    }

    // get package test report by id
    @Override
    public PackageTestReportDTO getPackageTestReportById(String packageReportId) {
        logger.info("Fetching package test report with ID: {}", packageReportId);
        if (!StringUtils.hasText(packageReportId)) { //check the package report id
            throw new pathologyException(ErrorConstant.REPORT_ID_IS_REQUIRED, HttpStatus.BAD_REQUEST);
        }
        PackageTestReport report = packageTestReportRepository.findByIdAndDefunct(packageReportId, false) //get the package test report
                .orElseThrow(() -> new pathologyException(ErrorConstant.PACKAGE_TEST_REPORT_NOT_FOUND, HttpStatus.NOT_FOUND));
        PackageTestReportDTO dto = ObjectMapperUtil.copyObject(report, PackageTestReportDTO.class); //copy the package test report to dto
        dto.setOrganizationId(report.getOrganization().getId());
        PatientDTO patientDTO = ObjectMapperUtil.copyObject(report.getPatient(), PatientDTO.class); //copy the patient to dto
        dto.setPatId(patientDTO.getId());
        dto.setPId(patientDTO.getPId());
        dto.setDoctorId(report.getDoctor().getId());
        dto.setTestManagerDTO(ObjectMapperUtil.copyObject(report.getTestManager(), TestManagerDTO.class)); //set the test manager dto
        dto.setReportDate(report.getReportDate());
        dto.setPackageTestReportItem(report.getPackageTestReportItem());
        dto.setStatus(report.getStatus());
        return dto; //return the package test report dto
    }

    // get test reports
    @Override
    public PagedResultForTest<GetResTestReportDTO> getTestReports(String orgId, LocalDate fromDate, LocalDate toDate, String pId, String mobileNo, int page, int size) {
        if ((Objects.isNull(fromDate) || Objects.isNull(toDate) || fromDate.isAfter(toDate)) //check the date range
                && !StringUtils.hasText(pId) //check the patient id
                && !StringUtils.hasText(mobileNo)) { //check the mobile number
            throw new pathologyException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }
        LocalDateTime start = Objects.nonNull(fromDate) ? fromDate.atStartOfDay() : null; //get the start date
        LocalDateTime end = Objects.nonNull(toDate) ? toDate.atTime(LocalTime.MAX) : null; //get the end date
        String safeMobile = StringUtils.hasText(mobileNo) ? EncryptionUtil.encrypt(mobileNo) : null; //encrypt the mobile number
        PagedResultForTest<GetResTestReportDTO> reports = testReportRepository.findCustomTestReports(orgId, start, end, pId, safeMobile, page, size); //get the test reports
        reports.getData().forEach(this::decryptPatientDTOForTestReport); //decrypt the patient dto for test report
        if (StringUtils.hasText(mobileNo)) { //check the mobile number
            List<GetResTestReportDTO> filtered = reports.getData().stream() //filter the test reports
                    .filter(r -> mobileNo.equals(r.getPhoneNo())) //filter the test reports by mobile number
                    .toList(); //get the filtered test reports
            return new PagedResultForTest<>(filtered, reports.getTotal(), page, size); //return the filtered test reports
        }
        return reports; //return the test reports
    }

    // get package test reports
    @Override
    public PagedResultForTest<GetResPacTestReportDTO> getPackageTestReports(String orgId, LocalDate fromDate, LocalDate toDate, String pId, String mobileNo, int page, int size) {
        if ((Objects.isNull(fromDate) || Objects.isNull(toDate) || fromDate.isAfter(toDate)) //check the date range
                && !StringUtils.hasText(pId) //check the patient id
                && !StringUtils.hasText(mobileNo)) { //check the mobile number
            throw new pathologyException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST);
        }
        LocalDateTime startDateTime = Objects.nonNull(fromDate) ? fromDate.atStartOfDay() : null; //get the start date
        LocalDateTime endDateTime = Objects.nonNull(toDate) ? toDate.atTime(LocalTime.MAX) : null; //get the end date
        String encryptedMobile = StringUtils.hasText(mobileNo) ? EncryptionUtil.encrypt(mobileNo) : null; //encrypt the mobile number
        PagedResultForTest<GetResPacTestReportDTO> reports = packageTestReportRepository.findCustomPackageTestReports(orgId, startDateTime, endDateTime, pId, encryptedMobile, page, size); //get the package test reports
        reports.getData().forEach(this::decryptPatientDTOForPackageTestReport); //decrypt the patient dto for package test report
        if (StringUtils.hasText(mobileNo)) { //check the mobile number
            List<GetResPacTestReportDTO> filtered = reports.getData().stream() //filter the package test reports
                    .filter(r -> mobileNo.equals(r.getPhoneNo())) //filter the package test reports by mobile number
                    .toList(); //get the filtered package test reports
            return new PagedResultForTest<>(filtered, reports.getTotal(), page, size);
        }
        return reports; //return the package test reports
    }

    @Override
    public TestReportPdfResponseDTO getTestReportPdfResponseDTO(String testReportId) {
        TestReport testReport = entityFetcherUtil.getTestReportOrThrow(testReportId); //get the test report
        PatientBillInfoDTO patientDto = buildPatientBillInfo(testReport.getPatient());
        List<TestReportParameterDTO> parameterDTOs = testReport.getParameters().stream().filter(Objects::nonNull).map(param -> TestReportParameterDTO.builder().name(param.getName()).value(param.getValue()).unit(param.getUnit()).referenceRange(param.getReferenceRange()).build()).toList();

        return TestReportPdfResponseDTO.builder().headerFooter(HeaderFooterMapperUtil.buildHeaderFooter(testReport.getOrganization())).reportDateTime(testReport.getReportDate().toString()).patient(patientDto).parameters(parameterDTOs).generatedBy(UserUtilis.getLoggedInUsername()).build();
    }

    @Override
    public PackageTestReportPdfResponseDTO getPackageTestReportPdfResponseDTO(String packageTestReportId) {
        PackageTestReport packageTestReport = entityFetcherUtil.getPackageTestReportOrThrow(packageTestReportId); //get the package test report
        PatientBillInfoDTO patientDto = buildPatientBillInfo(packageTestReport.getPatient());
        List<PackageTestReportItemDTO> packageTestReportItemDTOs = packageTestReport.getPackageTestReportItem().stream().filter(Objects::nonNull).map(item -> PackageTestReportItemDTO.builder().testName(item.getTest().getName()).testId(item.getTest().getId()).reportDate(item.getReportDate()).parameters(item.getParameters().stream().filter(Objects::nonNull).map(param -> TestReportParameterDTO.builder().name(param.getName()).value(param.getValue()).unit(param.getUnit()).referenceRange(param.getReferenceRange()).build()).toList()).build()).toList();

        return PackageTestReportPdfResponseDTO.builder().headerFooter(HeaderFooterMapperUtil.buildHeaderFooter(packageTestReport.getOrganization())).reportDateTime(packageTestReport.getReportDate().toString()).patient(patientDto).packageTestReportItem(packageTestReportItemDTOs).generatedBy(UserUtilis.getLoggedInUsername()).build();
    }
}
