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
import com.org.hosply360.dto.OPDDTO.PdfResponseDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientDTO;
import com.org.hosply360.dto.pathologyDTO.GetResPacTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.GetResTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportDTO;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportItem;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportReqDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;
import com.org.hosply360.dto.pathologyDTO.TestDataReqDTO;
import com.org.hosply360.dto.pathologyDTO.TestManagerDTO;
import com.org.hosply360.dto.pathologyDTO.TestReportDTO;
import com.org.hosply360.dto.pathologyDTO.TestReportReqDTO;
import com.org.hosply360.exception.pathologyException;
import com.org.hosply360.repository.PathologyRepo.PackageTestReportRepository;
import com.org.hosply360.repository.PathologyRepo.TestReportRepository;
import com.org.hosply360.service.pathology.TestReportService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.PDFGenUtil.PackageTestReportPdfGenerator;
import com.org.hosply360.util.PDFGenUtil.ReportPdfGenerator;
import com.org.hosply360.util.encryptionUtil.EncryptionUtil;
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

@Service
@RequiredArgsConstructor
public class TestReportServiceImpl implements TestReportService {

    private static final Logger logger = LoggerFactory.getLogger(TestReportServiceImpl.class);
    private final TestReportRepository testReportRepository;
    private final PackageTestReportRepository packageTestReportRepository;
    private final ReportPdfGenerator reportPdfGenerator;
    private final PackageTestReportPdfGenerator packageTestReportPdfGenerator;
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
        ValidatorHelper.validateObject(reqDTO); //validate the request dto
        logger.info("Starting creation of test report for organizationId: {}, testManagerId: {}, patientId: {}",
                reqDTO.getOrganizationId(), reqDTO.getTestManagerId(), reqDTO.getPatientId());
        TestManager testManager = entityFetcherUtil.getTestMangerOrThrow(reqDTO.getTestManagerId()); //get the test manager
        if (TestStatus.PENDING.equals(testManager.getStatus())) { //check the test manager status
            throw new pathologyException(ErrorConstant.CHANGE_THE_STATUS, HttpStatus.BAD_REQUEST);
        }
        if (TestSource.PACKAGE.equals(testManager.getSource())) { //check the test source
            throw new pathologyException(ErrorConstant.INVALID_TEST_SOURCE, HttpStatus.BAD_REQUEST);
        }
        TestDataReqDTO testData = Optional.ofNullable(reqDTO.getTestDataReqDTOS()) //get the test data
                .filter(d -> d.getTestId() != null) //check the test id
                .orElseThrow(() -> new pathologyException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST));
        Test test = entityFetcherUtil.getTestOrThrow(testData.getTestId()); //get the test
        List<TestReportParameter> parameters = mapTestValuesToParameters(test, testData.getParametersValues()); //map the test values to parameters
        TestReport testReport = TestReport.builder() //build the test report
                .reportDate(LocalDateTime.now())
                .testManager(testManager)
                .defunct(false)
                .patient(entityFetcherUtil.getPatientOrThrow(reqDTO.getPatientId()))
                .test(test)
                .doctor(entityFetcherUtil.getDoctorOrThrow(reqDTO.getDoctorId()))
                .organization(entityFetcherUtil.getOrganizationOrThrow(reqDTO.getOrganizationId()))
                .parameters(parameters)
                .build();
        String reportId = testReportRepository.save(testReport).getId(); //save the test report
        logger.info("Test report {} saved successfully", reportId);
        return reportId; //return the report id
    }

    // map test values to parameters
    private List<TestReportParameter> mapTestValuesToParameters(Test test, List<TestReportParameter> inputParameters) {
        List<TestParameterMaster> masters = test.getTestParameterMasters(); //get the test parameter masters
        List<TestReportParameter> reportParams = new ArrayList<>(); //create a list to store the report parameters
        if (Objects.isNull(masters) || Objects.isNull(inputParameters)) return reportParams; //check the masters and input parameters are null or not
        for (TestParameterMaster master : masters) { //iterate over the masters
            TestReportParameter matchedInput = inputParameters.stream() //iterate over the input parameters
                    .filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(master.getName())) //filter the input parameters
                    .findFirst() //get the first matched input parameter
                    .orElseThrow(() -> new pathologyException(ErrorConstant.MISSING_PARAMETER + master.getName(), HttpStatus.BAD_REQUEST));
            reportParams.add(TestReportParameter.builder() //build the report parameters
                    .name(master.getName())
                    .unit(master.getUnit())
                    .referenceRange(master.getReferenceRange())
                    .value(matchedInput.getValue())
                    .build());
        }
        return reportParams; //return the report parameters
    }

    // update test report
    @Override
    public String updateTestReport(TestReportReqDTO reqDTO) {
        ValidatorHelper.validateObject(reqDTO); //validate the request dto
        String reportId = reqDTO.getTestReportId(); //get the report id
        logger.info("Updating test report with ID: {}", reportId);
        TestReport testReport = entityFetcherUtil.getTestReportOrThrow(reportId); //get the test report
        TestDataReqDTO testData = Optional.ofNullable(reqDTO.getTestDataReqDTOS()) //get the test data
                .filter(d -> d.getTestId() != null) //filter the test data
                .orElseThrow(() -> new pathologyException(ErrorConstant.INVALID_REQUEST_DATA, HttpStatus.BAD_REQUEST));
        Test test = entityFetcherUtil.getTestOrThrow(testData.getTestId()); //get the test
        List<TestReportParameter> updatedParameters = mapTestValuesToParameters(test, testData.getParametersValues()); //map the test values to parameters
        testReport.setTestManager(entityFetcherUtil.getTestMangerOrThrow(reqDTO.getTestManagerId()));
        testReport.setOrganization(entityFetcherUtil.getOrganizationOrThrow(reqDTO.getOrganizationId()));
        testReport.setPatient(entityFetcherUtil.getPatientOrThrow(reqDTO.getPatientId()));
        testReport.setTest(test);
        testReport.setDoctor(entityFetcherUtil.getDoctorOrThrow(reqDTO.getDoctorId()));
        testReport.setReportDate(LocalDateTime.now());
        testReport.setParameters(updatedParameters);
        String updatedId = testReportRepository.save(testReport).getId(); //save the test report
        logger.info("Test report [{}] updated successfully", updatedId);
        return updatedId; //return the updated id
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
                            .orElseThrow(() -> new pathologyException(
                                    ErrorConstant.MISSING_INPUT_FOR_TEST_ID + testId, HttpStatus.INTERNAL_SERVER_ERROR));
                    List<TestReportParameter> parameters = mapTestValuesToParameters(packageTest, input.getParametersValues()); //map the test values to parameters
                    return PackageTestReportItem.builder() //build the package test report item
                            .test(packageTest)
                            .reportDate(LocalDateTime.now())
                            .parameters(parameters)
                            .build();
                })
                .collect(Collectors.toList()); //collect the package tests

        PackageTestReport packageTestReport = PackageTestReport.builder() //build the package test report
                .organization(entityFetcherUtil.getOrganizationOrThrow(dto.getOrganizationId()))
                .testManager(testManager)
                .doctor(entityFetcherUtil.getDoctorOrThrow(dto.getDoctorId()))
                .patient(entityFetcherUtil.getPatientOrThrow(dto.getPatientId()))
                .packageTestReportItem(reportItems)
                .status(dto.getStatus())
                .defunct(false)
                .reportDate(LocalDateTime.now())
                .build();
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
        if(PackageTestStatus.COMPLETED.equals(packageTestReport.getStatus())){ //check the package test status
            throw new pathologyException(ErrorConstant.DO_NOT_CHANGE_THE_STATUS,HttpStatus.BAD_REQUEST);
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
                })
                .collect(Collectors.toList()); //collect the package tests
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
        dto.setTestManagerDTO(ObjectMapperUtil.copyObject(report.getTestManager(),TestManagerDTO.class)); //set the test manager dto
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
        LocalDateTime start = Objects.nonNull(fromDate)  ? fromDate.atStartOfDay() : null; //get the start date
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
        LocalDateTime startDateTime = Objects.nonNull(fromDate)? fromDate.atStartOfDay() : null; //get the start date
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

    // generate test report pdf
    @Override
    public PdfResponseDTO generateTestReportPdf(String testReportId) {
        TestReport testReport = entityFetcherUtil.getTestReportOrThrow(testReportId); //get the test report
        byte[] pdfBytes = reportPdfGenerator.generateTestReportPDF(testReport); //generate the test report pdf
        String fileName = "TestReport_" + testReport.getPatient().getPId() + "_" + LocalDate.now() + ".pdf"; //get the file name
        PdfResponseDTO pdfResponseDTO = new PdfResponseDTO(); //create the pdf response dto
        pdfResponseDTO.setBody(pdfBytes);
        pdfResponseDTO.setFileName(fileName);
        return pdfResponseDTO; //return the pdf response dto
    }

    // generate package test report pdf
    @Override
    public PdfResponseDTO generatePackageTestReportPdf(String packageTestReportId) {
        PackageTestReport packageTestReport = entityFetcherUtil.getPackageTestReportOrThrow(packageTestReportId); //get the package test report
        byte[] pdfBytes = packageTestReportPdfGenerator.generatePackageTestReportPDF(packageTestReport); //generate the package test report pdf
        String fileName = "PackageTestReport_" + packageTestReport.getPatient().getPId() + "_" + LocalDate.now() + ".pdf"; //get the file name
        PdfResponseDTO pdfResponseDTO = new PdfResponseDTO(); //create the pdf response dto
        pdfResponseDTO.setBody(pdfBytes);
        pdfResponseDTO.setFileName(fileName);
        return pdfResponseDTO; //return the pdf response dto
    }
}
