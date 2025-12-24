package com.org.hosply360.service.pathology.impl;

import com.org.hosply360.constant.Enums.PaymentMode;
import com.org.hosply360.constant.Enums.TestSource;
import com.org.hosply360.constant.Enums.TestStatus;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.globalMaster.PackageE;
import com.org.hosply360.dao.globalMaster.Test;
import com.org.hosply360.dao.pathology.PathologyReceipt;
import com.org.hosply360.dao.pathology.TestManager;
import com.org.hosply360.dto.pathologyDTO.BillSummaryDTO;
import com.org.hosply360.dto.pathologyDTO.GetReqTestMangerDTO;
import com.org.hosply360.dto.pathologyDTO.GetResTestManagerDTO;
import com.org.hosply360.dto.pathologyDTO.PagedResultForTest;
import com.org.hosply360.dto.pathologyDTO.PathologyPaymentUpdateDTO;
import com.org.hosply360.dto.pathologyDTO.PatientBillInfoDTO;
import com.org.hosply360.dto.pathologyDTO.TestManagerBillResponseDTO;
import com.org.hosply360.dto.pathologyDTO.TestManagerReceiptDTO;
import com.org.hosply360.dto.pathologyDTO.TestManagerReqDTO;
import com.org.hosply360.dto.pathologyDTO.TestTableDTO;
import com.org.hosply360.dto.pathologyDTO.updateTestManagerReqDTO;
import com.org.hosply360.exception.pathologyException;
import com.org.hosply360.repository.PathologyRepo.PathologyReceiptRepository;
import com.org.hosply360.repository.PathologyRepo.TestManagerRepository;
import com.org.hosply360.repository.globalMasterRepo.PackageERepository;
import com.org.hosply360.repository.globalMasterRepo.TestRepository;
import com.org.hosply360.service.pathology.TestManagerService;
import com.org.hosply360.util.Others.EntityFetcherUtil;
import com.org.hosply360.util.Others.SequenceGeneratorService;
import com.org.hosply360.util.Others.UserUtilis;
import com.org.hosply360.util.PDFGenUtil.TestReportBillPdfGenerator;
import com.org.hosply360.util.encryptionUtil.EncryptionUtil;
import com.org.hosply360.util.mapper.HeaderFooterMapperUtil;
import com.org.hosply360.util.validator.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.org.hosply360.util.mapper.PatientMapperUtil.buildPatientBillInfo;

@Service
@RequiredArgsConstructor
public class TestManagerServiceImpl implements TestManagerService {

    private static final Logger logger = LoggerFactory.getLogger(TestManagerServiceImpl.class);
    private final TestManagerRepository testManagerRepository;
    private final PackageERepository packageERepository;
    private final TestRepository testRepository;
    private final TestReportBillPdfGenerator testReportBillPdfGenerator;
    private final EntityFetcherUtil entityFetcherUtil;
    private final SequenceGeneratorService sequenceGenerator;
    private final PathologyReceiptRepository pathologyReceiptRepository;

    // decrypt the patient dto
    private void decryptPatientDTO(GetResTestManagerDTO dto) {
        dto.setFirstName(EncryptionUtil.decrypt(dto.getFirstName()));
        dto.setLastName(EncryptionUtil.decrypt(dto.getLastName()));
        dto.setPId(EncryptionUtil.decrypt(dto.getPId()));
        dto.setPhoneNo(EncryptionUtil.decrypt(dto.getPhoneNo()));
    }

    // create test manager
    @Override
    public String createTestManager(TestManagerReqDTO reqDTO) {
        ValidatorHelper.validateObject(reqDTO); //check the request object is valid or not
        if (CollectionUtils.isEmpty(reqDTO.getTestId()) && !StringUtils.hasText(reqDTO.getPackageId())) {
            throw new pathologyException(ErrorConstant.PLEASE_PROVIDE_EITHER_TEST_ID_OR_PACKAGE_ID, HttpStatus.BAD_REQUEST);
        }
        TestManager.TestManagerBuilder builder = TestManager.builder() //create the test manager builder
                .organization(entityFetcherUtil.getOrganizationOrThrow(reqDTO.getOrganizationId()))
                .patient(entityFetcherUtil.getPatientOrThrow(reqDTO.getPatientId()))
                .defunct(false)
                .status(TestStatus.PENDING)
                .testDateTime(reqDTO.getTestDateTime())
                .hasPaid(false)
                .paidAmount(0.0);

        if (StringUtils.hasText(reqDTO.getPackageId())) { //check the package id is provided or not
            getPackageE(reqDTO, builder);
        } else {
            getTests(reqDTO, builder);
        }
        TestManager testManager = builder.build(); //build the test manager
        TestManager saved = testManagerRepository.save(testManager); //save the test manager
        logger.info("TestManager created successfully");
        return saved.getId(); //return the test manager id
    }

    // get tests
    private void getTests(TestManagerReqDTO reqDTO, TestManager.TestManagerBuilder builder) {
        List<Test> tests = testRepository.findAllByIdInTestAndDefunct(false, reqDTO.getTestId()); //get the tests
        if (CollectionUtils.isEmpty(tests)) { //check the tests are available or not
            throw new pathologyException(ErrorConstant.TEST_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        builder.test(tests) //set the tests
                .totalAmount(tests.stream().mapToDouble(Test::getAmount).sum()) //set the total amount
                .source(TestSource.INDIVIDUAL); //set the source
    }

    // get package
    private void getPackageE(TestManagerReqDTO reqDTO, TestManager.TestManagerBuilder builder) {
        PackageE packageE = entityFetcherUtil.getPackageEOrThrow(reqDTO.getPackageId()); //get the package
        if (CollectionUtils.isEmpty(packageE.getTestName())) { //check the tests are available or not
            throw new pathologyException(ErrorConstant.TEST_NOT_FOUND_IN_PACKAGE, HttpStatus.NOT_FOUND);
        }
        builder.packageE(packageE) //set the package
                .test(packageE.getTestName())
                .totalAmount(packageE.getTotalAmount())
                .source(TestSource.PACKAGE);
    }

    // update test manager
    @Override
    public String updateTestManager(String id, TestStatus status) {
        ValidatorHelper.validateObject(id); //validate the id
        TestManager testManager = entityFetcherUtil.getTestMangerOrThrow(id); //get the test manager
        if (Objects.nonNull(status)) { //check the status is provided or not
            if (status == TestStatus.PENDING || status == TestStatus.ACCEPTED) { //check the status is pending or accepted
                testManager.setStatus(status);
            } else {
                throw new pathologyException(ErrorConstant.ONLY_PENDING_OR_ACCEPTED_UPDATES_ALLOWED, HttpStatus.BAD_REQUEST);
            }
        }
        TestManager updated = testManagerRepository.save(testManager); //save the test manager
        return updated.getId(); //return the test manager id
    }

    // get test managers
    @Override
    public PagedResultForTest<GetResTestManagerDTO> getTestManagers(GetReqTestMangerDTO dto) {
        LocalDateTime start = dto.getFromDate().atStartOfDay(); //get the start date
        LocalDateTime end = dto.getToDate().atTime(LocalTime.MAX); //get the end date
        String safePid = StringUtils.hasText(dto.getPId()) ? dto.getPId() : null; //get the patient id
        String safeMobile = StringUtils.hasText(dto.getMobileNo()) ? EncryptionUtil.encrypt(dto.getMobileNo()) : null; //get the mobile number and encrypt it
        List<TestStatus> safeStatuses = (Objects.isNull(dto.getTestStatuses()) || dto.getTestStatuses().isEmpty()) ? null : dto.getTestStatuses(); //get the test statuses

        PagedResultForTest<GetResTestManagerDTO> pageResult = testManagerRepository
                .findCustomTestManagersDynamic(dto.getOrgId(), start, end, safePid, safeMobile, safeStatuses, dto.getPage(), dto.getSize()); //get the test managers

        if (Objects.nonNull(pageResult) && Objects.nonNull(pageResult.getData())) { //check the page result is not null and data is not null
            pageResult.getData().forEach(this::decryptPatientDTO); //decrypt the patient dto
        }
        return pageResult; //return the page result
    }

    // test manager payment
    @Override
    public String testManagerPayment(PathologyPaymentUpdateDTO dto) {

        // ===== Validations =====
        ValidatorHelper.validateObject(dto.getTestManagerId());

        TestManager testManager = entityFetcherUtil.getTestMangerOrThrow(dto.getTestManagerId());

        if (Boolean.TRUE.equals(testManager.getHasPaid())) {
            throw new pathologyException(ErrorConstant.TEST_MANAGER_ALREADY_PAID, HttpStatus.BAD_REQUEST);
        }

        Double newAmount = dto.getNewAmount();
        if (Objects.isNull(newAmount) || newAmount <= 0) {
            throw new pathologyException(ErrorConstant.INVALID_PAYMENT_AMOUNT, HttpStatus.BAD_REQUEST);
        }

        double balance = testManager.getTotalAmount() - testManager.getPaidAmount();
        if (newAmount > balance) {
            throw new pathologyException(ErrorConstant.AMOUNT_IS_GREATER_THAN_BALANCE, HttpStatus.BAD_REQUEST);
        }

        // ===== Payment Update =====
        double updatedPaidAmount = testManager.getPaidAmount() + newAmount;
        testManager.setPaidAmount(updatedPaidAmount);
        testManager.setHasPaid(Double.compare(testManager.getTotalAmount(), updatedPaidAmount) == 0);

        testManagerRepository.save(testManager);

        // ===== Receipt Creation =====
        PaymentMode paymentMode = dto.getPaymentType();

        PathologyReceipt.PathologyReceiptBuilder builder = PathologyReceipt.builder()
                .testManager(testManager.getId())
                .receiptNumber(sequenceGenerator.generatePathologyReceiptNumber())
                .receiptDate(LocalDateTime.now())
                .generatedBy(UserUtilis.getLoggedInUsername())
                .paidAmount(newAmount)
                .paymentType(paymentMode)
                .defunct(false);

        if (PaymentMode.CHEQUE.equals(paymentMode)) {
            builder.chequeNumber(dto.getChequeNumber())
                    .bankName(dto.getBankName())
                    .chequeDate(dto.getChequeDate());
        }

        PathologyReceipt savedReceipt = pathologyReceiptRepository.save(builder.build());
        return savedReceipt.getId();
    }


    @Override
    public TestManagerBillResponseDTO getTestManagerBill(String testManagerId) {

        logger.info("Fetching Test Bill | TestManagerId: {}", testManagerId);

        TestManager testManager = testManagerRepository.findByIdAndDefunct(testManagerId, false)
                .orElseThrow(() -> new pathologyException(ErrorConstant.TEST_MANAGER_NOT_FOUND, HttpStatus.NOT_FOUND));

        validateTestManager(testManager);

        PatientBillInfoDTO patientDto = buildPatientBillInfo(testManager.getPatient());
        TestManagerReceiptDTO receiptDto = buildReceiptInfo(testManager);
        List<TestTableDTO> testTableList = buildTestTable(testManager);
        BillSummaryDTO summaryDto = buildSummary(testManager);

        return TestManagerBillResponseDTO.builder()
                .headerFooter(HeaderFooterMapperUtil.buildHeaderFooter(testManager.getOrganization()))
                .receipt(receiptDto)
                .patient(patientDto)
                .tests(testTableList)
                .summary(summaryDto)
                .build();
    }

    private void validateTestManager(TestManager tm) {
        if (tm.getPatient() == null) {
            throw new pathologyException("Patient details missing!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (tm.getTest() == null || tm.getTest().isEmpty()) {
            throw new pathologyException("No tests found for this bill!", HttpStatus.BAD_REQUEST);
        }
    }

    private TestManagerReceiptDTO buildReceiptInfo(TestManager tm) {
        return TestManagerReceiptDTO.builder()
                .reportDateTime(tm.getTestDateTime() != null ? tm.getTestDateTime().toString() : null)
                .source(tm.getSource() != null ? tm.getSource().toString() : null)
                .build();
    }

    @Override
    public String updateTestManagerById(updateTestManagerReqDTO dto) {
        ValidatorHelper.validateObject(dto.getId());
        TestManager testManager = testManagerRepository.findByIdAndDefunct(dto.getId(), false)
                .orElseThrow(() -> new pathologyException(ErrorConstant.TEST_MANAGER_NOT_FOUND, HttpStatus.NOT_FOUND));

        if (dto.getPackageId() != null) {
            PackageE packageNew = packageERepository.findByIdAndDefunct(dto.getPackageId(), false).orElseThrow(() ->
                    new pathologyException(ErrorConstant.PACKAGE_NOT_FOUND, HttpStatus.NOT_FOUND));
            testManager.setPackageE(packageNew);
        }
        if (dto.getTestId() != null) {
            List<Test> testsNew = testRepository.findAllByIdInTestAndDefunct(false, dto.getTestId());
            testManager.setTest(testsNew);
        }
        if (dto.getTestDateTime() != null) {
            testManager.setTestDateTime(dto.getTestDateTime());
        }
        TestManager save = testManagerRepository.save(testManager);
        return save.getId();
    }

    private List<TestTableDTO> buildTestTable(TestManager tm) {

        AtomicInteger sr = new AtomicInteger(1);

        return tm.getTest().stream()
                .map(ref -> testRepository.findByIdAndDefunct(ref.getId(), false)
                        .orElseThrow(() ->
                                new pathologyException(ErrorConstant.TEST_NOT_FOUND, HttpStatus.NOT_FOUND)))
                .map(test -> TestTableDTO.builder()
                        .srNo(sr.getAndIncrement())
                        .name(test.getName())
                        .rate(test.getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    private BillSummaryDTO buildSummary(TestManager tm) {

        double total = Objects.requireNonNullElse(tm.getTotalAmount(), 0.0);
        double paid = Objects.requireNonNullElse(tm.getPaidAmount(), 0.0);

        return BillSummaryDTO.builder()
                .total(total)
                .paid(paid)
                .balance(total - paid)
                .build();
    }


}