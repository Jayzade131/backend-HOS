package com.org.hosply360.util.Others;


import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.dao.IPD.IPDFinancialSummary;
import com.org.hosply360.dao.IPD.IPDDiet;
import com.org.hosply360.dao.IPD.IPDFinalBill;
import com.org.hosply360.dao.IPD.IPDReceipt;
import com.org.hosply360.dao.OPD.Appointment;
import com.org.hosply360.dao.frontDeskDao.Doctor;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.BillingItem;
import com.org.hosply360.dao.globalMaster.BillingItemGroup;
import com.org.hosply360.dao.globalMaster.CompanyMaster;
import com.org.hosply360.dao.globalMaster.InsuranceProvider;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dao.globalMaster.PackageE;
import com.org.hosply360.dao.globalMaster.PatientCategory;
import com.org.hosply360.dao.globalMaster.Speciality;
import com.org.hosply360.dao.globalMaster.Template;
import com.org.hosply360.dao.globalMaster.Test;
import com.org.hosply360.dao.globalMaster.WardBedMaster;
import com.org.hosply360.dao.globalMaster.WardMaster;
import com.org.hosply360.dao.pathology.PackageTestReport;
import com.org.hosply360.dao.pathology.TestManager;
import com.org.hosply360.dao.pathology.TestReport;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.exception.OPDException;
import com.org.hosply360.exception.pathologyException;
import com.org.hosply360.repository.IPD.IPDAdmissionRepository;
import com.org.hosply360.repository.IPD.IPDFinancialSummaryRepository;
import com.org.hosply360.repository.IPD.IPDDietRepository;
import com.org.hosply360.repository.IPD.IPDFinalBillRepository;
import com.org.hosply360.repository.IPD.IPDReceiptRepository;
import com.org.hosply360.repository.OPDRepo.AppointmentRepository;
import com.org.hosply360.repository.PathologyRepo.PackageTestReportRepository;
import com.org.hosply360.repository.PathologyRepo.TestManagerRepository;
import com.org.hosply360.repository.PathologyRepo.TestReportRepository;
import com.org.hosply360.repository.frontDeskRepo.DoctorMasterRepository;
import com.org.hosply360.repository.frontDeskRepo.PatientRepository;
import com.org.hosply360.repository.globalMasterRepo.BillingItemGroupRepository;
import com.org.hosply360.repository.globalMasterRepo.BillingItemRepository;
import com.org.hosply360.repository.globalMasterRepo.CompanyMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.InsuranceProviderRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.PackageERepository;
import com.org.hosply360.repository.globalMasterRepo.PatientCategoryRepository;
import com.org.hosply360.repository.globalMasterRepo.SpecialityMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.TemplateRepository;
import com.org.hosply360.repository.globalMasterRepo.TestRepository;
import com.org.hosply360.repository.globalMasterRepo.WardBedMasterRepository;
import com.org.hosply360.repository.globalMasterRepo.WardMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityFetcherUtil {

    private final DoctorMasterRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final OrganizationMasterRepository organizationRepository;
    private final AppointmentRepository appointmentRepository;
    private final PackageERepository packageERepository;
    private final TestManagerRepository testManagerRepository;
    private final TestRepository testRepository;
    private final TestReportRepository testReportRepository;
    private final PackageTestReportRepository packageTestReportRepository;
    private final BillingItemGroupRepository billingItemGroupRepository;
    private final BillingItemRepository billingItemRepository;

    private final WardMasterRepository wardMasterRepository;
    private final WardBedMasterRepository wardBedMasterRepository;
    private final InsuranceProviderRepository insuranceProviderRepository;
    private final CompanyMasterRepository companyMasterRepository;
    private final PatientCategoryRepository patientCategoryRepository;
    private final IPDAdmissionRepository ipdAdmissionRepository;
    private final IPDDietRepository ipdDietRepository;
    private final SpecialityMasterRepository specialityMasterRepository;
    private final TemplateRepository templateRepository;
    private final IPDFinalBillRepository ipdFinalBillRepository;
    private final IPDFinancialSummaryRepository financialSummaryRepository;
    private final IPDReceiptRepository ipdReceiptRepository;


    public Doctor getDoctorOrThrow(String doctorId) {
        return doctorRepository.findByIdAndDefunct(doctorId, false)
                .orElseThrow(() -> new OPDException(ErrorConstant.DOCTOR_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }


    public Patient getPatientOrThrow(String patientId) {
        return patientRepository.findByIdAndDefunct(patientId, false)
                .orElseThrow(() -> new OPDException(ErrorConstant.PATIENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    public Organization getOrganizationOrThrow(String orgId) {
        return organizationRepository.findByIdAndDefunct(orgId, false)
                .orElseThrow(() -> new OPDException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }


    public Appointment getAppointmentOrThrow(String id, String orgId) {
        return appointmentRepository.findByIdAndDefunct(id, orgId, false)
                .orElseThrow(() -> new OPDException(ErrorConstant.APPOINTMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public PackageE getPackageEOrThrow(String packageEId) {
        return packageERepository.findByIdAndDefunct(packageEId, false)
                .orElseThrow(() -> new pathologyException(ErrorConstant.PACKAGE_NOT_FOUND, HttpStatus.CONFLICT));
    }

    public TestManager getTestMangerOrThrow(String testMId) {
        return testManagerRepository
                .findByIdAndDefunct(testMId, false)
                .orElseThrow(() -> new pathologyException(ErrorConstant.TEST_MANAGER_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    public Test getTestOrThrow(String testId) {
        return testRepository.findByIdAndDefunct(testId, false)
                .orElseThrow(() -> new pathologyException(ErrorConstant.TEST_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public TestReport getTestReportOrThrow(String testReportId) {
        return testReportRepository.findByIdAndDefunct(testReportId, false)
                .orElseThrow(() -> new pathologyException(ErrorConstant.TEST_REPORT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public PackageTestReport getPackageTestReportOrThrow(String packageTestReportId) {
        return packageTestReportRepository.findByIdAndDefunct(packageTestReportId, false)
                .orElseThrow(() -> new pathologyException(ErrorConstant.PACKAGE_TEST_REPORT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public BillingItemGroup getBillingItemGroupOrThrow(String billId) {
        return billingItemGroupRepository.findByIdAndDefunct(billId, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.BILLING_ITEM_GROUP_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public BillingItem getBillingItemOrThrow(String billId) {
        return billingItemRepository.findByIdAndDefunct(billId, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public WardMaster getWardMasterOrThrow(String wardId) {
        return wardMasterRepository.findByIdAndDefunct(wardId, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.WARD_MASTER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public WardBedMaster getWardBedMasterOrThrow(String wardbedId) {
        return wardBedMasterRepository.findByIdAndDefunctFalse(wardbedId)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.WARD_BED_MASTER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }


    public InsuranceProvider getInsuranceProviderOrThrow(String providerId) {
        return insuranceProviderRepository.findByIdAndDefunct(providerId, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.INSURANCE_PROVIDER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public CompanyMaster getCompanyOrThrow(String companyId) {
        return companyMasterRepository.findByIdAndDefunct(companyId, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.COMPANY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public PatientCategory getPatientCategoryOrThrow(String patientCategoryId) {
        return patientCategoryRepository.findByIdAndDefunct(patientCategoryId, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.PATIENT_CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public IPDAdmission getIPDAdmissionOrThrow(String admissionId) {
        return ipdAdmissionRepository.findByIdAndDefunct(admissionId,false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.IPD_ADMISSION_NOT_FOUND, HttpStatus.NOT_FOUND));
    }



    public IPDDiet getIPDDietOrThrow(String ipdDietId) {
        return ipdDietRepository.findByIdAndDefunct(ipdDietId, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.IPD_DIET_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public Speciality getSpecialityOrThrow(String specialityId) {
        return specialityMasterRepository.findByIdAndDefunct(specialityId, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.SPECIALITY_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public Template getTemplateOrThrow(String templateId) {
        return templateRepository.findByIdAndDefunct(templateId, false)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.TEMPLATE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public IPDFinalBill getIPDFinalBillOrThrow(String finalBillId) {
        return ipdFinalBillRepository.findById(finalBillId)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.IPD_FINAL_BILL_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public IPDFinancialSummary getFinancialSummaryOrThrow(String advanceId) {
        return financialSummaryRepository.findById(advanceId)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.IPD_ADVANCE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

   public IPDReceipt getIPDReceiptOrThrow(String receiptId) {
        return ipdReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.IPD_RECEIPT_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
