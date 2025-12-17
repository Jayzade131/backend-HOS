package com.org.hosply360.constant;

/**
 * Centralized constants for all API endpoints in the application.
 * Endpoints are grouped by their functional domains for better organization.
 */
public class EndpointConstants {

    // ======================== AUTHENTICATION & USER MANAGEMENT ========================
    public static final String AUTH_URL = "/api/auth";
    public static final String REG_USER = "/user";
    public static final String LOGIN_USER = "/loginUser";
    public static final String USERS_BY_ORG_ID = "/users/{organizationId}";
    public static final String DOCTOR_USERS_BY_ORG_ID = "/docUsers/{organizationId}";
    public static final String CHANGE_DEFAULT_PASSWORD = "/checkUserDefaultPassword";
    public static final String DELETE_USER_BY_ID = "/users/{userId}";
    public static final String CHECK_USERNAME_EXISTS = "/checkUsernameExists/{username}";
    public static final String CHECK_EMAIL_EXISTS = "/checkEmailExists/{email}";
    public static final String CHECK_MOBILE_NO_EXISTS = "/checkMobileNoExists/{mobileNo}";

    // ======================== ORGANIZATION & CONFIGURATION ===========================
    public static final String ORGANIZATION_API = "/organization";
    public static final String ORGANIZATION_API_BY_ID = "/organization/{id}";
    public static final String FETCH_ALL_ORGANIZATION = "/fetchAllOrganization";

    public static final String CONFIGURATION = "/configuration";
    public static final String CONFIGURATION_BY_ID = "/configurations/{id}";
    public static final String GET_ALL_CONFIGURATION = "/config/{organizationId}";

    public static final String GLOBAL_MASTER_API = "/api/globalMaster";

    // ============================== MASTER DATA ======================================
    // Company
    public static final String COMPANY = "/companyMaster";
    public static final String COMPANY_BY_ID = "/companies/{id}";
    public static final String GET_ALL_COMPANY = "/company/{organizationId}";

    // Patient Category
    public static final String PATIENT_CATEGORY = "/patientCategory";
    public static final String PATIENT_CATEGORY_BY_ID = "/patients/{id}";
    public static final String GET_ALL_PATIENT_CATEGORIES = "/patient/{organizationId}";

    // Language
    public static final String LANGUAGE_API = "/language";
    public static final String LANGUAGE_API_BY_ID = "/language/{id}";
    public static final String GET_LANGUAGES_API = "/languages/{organizationId}";

    // Occupation
    public static final String OCCUPATION_API = "/occupation";
    public static final String GET_OCCUPATIONS_API = "/occupations/{organizationId}";
    public static final String OCCUPATION_API_BY_ID = "/occupation/{id}";

    // Religion
    public static final String RELIGION = "/religion";
    public static final String RELIGION_BY_ID = "/religion/{id}";
    public static final String GET_ALL_RELIGION = "/religions/{organizationId}";

    // ID Document
    public static final String ID_DOCUMENT = "/identificationDocument";
    public static final String GET_ALL_ID_DOCUMENTS = "/identificationDocuments/{organizationId}";
    public static final String DOCUMENT_BY_ID = "/document/{id}";

    // Currency
    public static final String CURRENCY = "/currency";
    public static final String CURRENCY_BY_ID = "/currency/{id}";
    public static final String GET_ALL_CURRENCIES = "/currencies/{organizationId}";

    //Speciality
    public static final String SPECIALITY_API = "/speciality";
    public static final String GET_SPECIALITIES_API = "/specialities/{organizationId}/{masterType}";
    public static final String SPECIALITY_API_BY_ID = "/speciality/{id}";

    //Doctor
    public static final String CREATE_MODIFY_DOCTOR_API = "/doctor";
    public static final String GET_DOCTORS_API = "/doctors";
    public static final String GET_DOCTORS_BY_DOCTOR_TYPE_API = "/doctors/{doctorType}/{organizationId}";
    public static final String DOCTOR_API_BY_ID = "/doctor/{id}";
    public static final String DOCTOR_WITH_SPECIALITY = "/doctorWithSpeciality";
    public static final String FETCH_ALL_DOCTOR = "/fetchAllDoctor/{organizationId}";

    //Ward
    public static final String CREATE_WARD_API = "/ward";
    public static final String GET_WARDS_API = "/wards";
    public static final String GET_WARD_API = "/ward/{orgId}/{id}";
    public static final String UPDATE_WARD_API = "/ward";
    public static final String DELETE_WARD_API = "/ward/{id}";

    //Bed
    public static final String CREATE_WARD_BED_API = "/ward-bed/create";
    public static final String UPDATE_WARD_BED_API = "/ward-bed/update";
    public static final String GET_WARD_BEDS_API = "/ward-bed/list";
    public static final String GET_WARD_BED_API = "/ward-bed/{id}";
    public static final String DELETE_WARD_BED_API = "/ward-bed/delete/{id}";
    public static final String GET_ALL_AVAILABLE_BEDS_API = "/ward-bed/availableBeds";

    // patient management
    public static final String FRONTDESK_API = "/api/frontDesk";
    public static final String CREATE_PATIENT_API = "/patient";
    public static final String GET_PATIENT_API = "/patient/{id}";
    public static final String GET_PATIENTS_API = "/patients";
    public static final String DELETE_PATIENT_API = "/patient/{id}";
    public static final String FETCH_ALL_PATIENT = "/fetchAllPatient/{organizationId}";
    public static final String UPCOMING_PATIENT_BY_VISIT_DATE = "/patientsByNextVisit";

    // Ledger
    public static final String LEDGER_API = "/ledger";
    public static final String LEDGER_BY_ID = "/ledger/{id}";
    public static final String GET_ALL_LEDGERS = "/ledgers/{organizationId}";

    //Tariff
    public static final String TARIFF_API = "/tariff";
    public static final String TARIFFS_API_BY_ID = "/tariffs/{id}";
    public static final String GET_TARIFF_API = "/tariff/{orgId}";

    // Package
    public static final String PACKAGE_API = "/packageE";
    public static final String PACKAGE_API_BY_ID = "/packageE/{id}";
    public static final String GET_ALL_PACKAGE_API = "/packageEs/{organizationId}";

    // Insurance
    public static final String INSURANCE_PROVIDER = "/insuranceProvider";
    public static final String INSURANCE_PROVIDER_BY_ID = "/insuranceProvider/{id}";
    public static final String GET_ALL_INSURANCE_PROVIDERS = "/insuranceProviders/{organizationId}";

    // Medicine
    public static final String UPLOAD_MEDICINE_CSV = "/uploadMedicineCsv";
    public static final String GET_ALL_MEDICINES_API = "/getAllMedicines";

    // Template
    public static final String TEMPLATE = "/template";
    public static final String TEMPLATE_BY_ID = "/template/{id}";
    public static final String GET_ALL_TEMPLATE = "/templates/{organizationId}";

    // =============================== APPOINTMENTS ===================================
    public static final String CREATE_APPOINTMENT_API = "/appointment";
    public static final String UPDATE_APPOINTMENT_API = "/appointment";
    public static final String GET_APPOINTMENTS_BY_PATIENT_AND_ORG_EXCLUDING_COMPLETED_API = "/appointmentsByPatientAndOrgExcludingCompleted/{patientId}/{orgId}";
    public static final String GET_APPOINTMENTS = "/appointments/org/{orgId}";
    public static final String DELETE_APPOINTMENT_API = "/appointment/{id}/{orgId}";
    public static final String UPDATE_STATUS_API = "/updateStatus/{id}/{orgId}/{status}";
    public static final String APPOINTMENTS_FILTERS = "/appointments/filters";
    public static final String GET_APPOINTMENTS_BY_ORG_ID_AND_DATE_RANGE_API = "/appointments/org/{orgId}/between-dates";

    // ============================ DOCTOR SCHEDULE ===================================
    public static final String DOC_SCHEDULE_API = "/docSchedule";
    public static final String GET_DOC_SCHEDULE_BY_DOCTOR_ID_API = "/docSchedule/{doctorId}/{orgId}";
    public static final String GET_DOC_SCHEDULES_API = "/docSchedules";
    public static final String DELETE_DOC_SCHEDULE_API = "/docSchedule/{id}";

    // ============================== CONSULTATION ====================================
    public static final String GET_CONSULTATION_DETAILS_API = "/consultation/details/{appointmentId}/{orgId}";
    public static final String GET_ALL_CONSULTATION_DETAILS_API = "/consultation";

    // ============================== PRESCRIPTION ====================================
    public static final String PRESCRIPTION_API = "/prescription";
    public static final String DOWNLOAD_PRESCRIPTION = "/prescription/download";
    public static final String PRESCRIPTION_HISTORY_BY_PATIENT = "/prescriptionHistory/patient";

    // ============================= BILLING & PAYMENT ================================
    public static final String BILLING_ITEM_API = "/api/billingItem";
    public static final String BILLING_ITEM_BY_ID = "/billingItem/{id}";
    public static final String GET_ALL_BILLING_ITEMS = "/billingItems/{organizationId}";
    public static final String BILLING_ITEM = "/billingItem";
    public static final String GET_ALL_BILLING_ITEMS_BY_ITEM_GRP = "/billingItems/{organizationId}/{itemGrpId}";

    public static final String BILLING_ITEM_GROUP = "/billingItemGroup";
    public static final String BILLING_ITEM_GROUP_BY_ID = "/billingItemGroup/{id}";
    public static final String GET_ALL_BILLING_ITEM_GROUPS = "/billingItemGroups/{organizationId}";


    // ================================ TEST & PATHOLOGY =============================
    public static final String TEST = "/test";
    public static final String GET_ALL_TESTS = "/tests/{organizationId}";
    public static final String TEST_BY_ID = "/test/{id}";

    public static final String TEST_MANAGER = "/testManager";
    public static final String GET_TEST_MANAGER = "/testManagers/byFilter";
    public static final String TEST_MANAGER_PAYMENT = "/testManagerPayment";

    public static final String TEST_PATHOLOGY_API = "/api/pathology";
    public static final String TEST_REPORT = "/testReport";
    public static final String TEST_REPORT_BY_ID = "/testReport/{testReportId}";
    public static final String DOWNLOAD_TEST_REPORT = "/testReport/download";
    public static final String DOWNLOAD_TEST_MANAGER_BILL = "/testMangerBill/download";
    public static final String GET_TEST_REPORT = "/testReport/byFilter";

    public static final String PACKAGE_TEST_REPORT = "/packageTestReport";
    public static final String PACKAGE_TEST_REPORT_ID = "/packageTestReport/{packageTestReportId}";
    public static final String DOWNLOAD_PACKAGE_TEST_REPORT = "/packageTestReport/download";
    public static final String GET_PACKAGE_TEST_REPORT = "/packageTestReport/byFilter";

    // ================================== OPD INVOICE ================================
    public static final String CREATE_OPD_INVOICE_API = "/opdInvoice/create";
    public static final String UPDATE_OPD_INVOICE_API = "/opdInvoice/update";
    public static final String GET_OPD_INVOICE_BY_ID_API = "/opdInvoice/{id}/{orgId}";
    public static final String GET_OPD_INVOICES_BY_FILTERS = "/opdInvoices/filter";
    public static final String DELETE_OPD_INVOICE_API = "/opdInvoice/delete/{id}/{orgId}";
    public static final String DOWNLOAD_OPD_BILL_API = "/opdInvoice/download/{id}/{orgId}";
    public static final String DOWNLOAD_OPD_RECEIPT_API = "/receipt/print/{receiptId}";
    public static final String GET_OPD_RECEIPT_API = "/receipt/data/{receiptId}";

    public static final String INVOICE_PAYMENT = "/opdInvoice/payment/rDownload";
    public static final String GET_OPD_PAYMENT_HISTORY_API = "/{invoiceId}";
    public static final String GET_OPD_INVOICE_DETAILS_API = "/opd/invoice/details/{identifier}";


    // ================================ SECURITY =====================================
    public static final String ACCESS = "/access";
    public static final String ACCESS_BY_ID = "/access/{id}";
    public static final String GET_ALL_ACCESS = "/accesses";

    public static final String MODULE = "/module";
    public static final String GET_ALL_MODULE = "/modules";
    public static final String MODULE_BY_ID = "/module/{id}";

    public static final String ROLE = "/role";
    public static final String ROLE_BY_ID = "/role/{id}";
    public static final String GET_ALL_ROLES = "/roles";

    public static final String ROLE_MODULE_MAPPING = "/roleModuleMapping";
    public static final String GET_ROLE_MODULE_MAPPING = "/roleModuleMapping";
    public static final String ROLE_MODULE_MAPPING_BY_ID = "/roleModuleMapping/{id}";

    public static final String MODULE_ACCESS_MAPPING = "/moduleAccessMapping";
    public static final String GET_MODULE_ACCESS_MAPPING = "/moduleAccessMapping";
    public static final String MODULE_ACCESS_MAPPING_BY_ID = "/moduleAccessMapping/{id}";


    // ========================= INPATIENT DEPARTMENT (IPD) =========================
    // Base IPD API
    public static final String IPD_API = "/api/ipd";

    // IPD Admission
    public static final String IPD_ADMISSION = "/admission";
    public static final String IPD_ADMISSION_CANCEL = "/admission/cancel";
    public static final String IPD_BEDS_BY_WARD = "/admission/beds";
    public static final String IPD_BARCODE = "/barcode/{ipdAdmissionId}";
    public static final String IPD_ADMISSION_FILTERS = "/admission/filters";
    public static final String IPD_PATIENT_LIST = "/admission/patientList";

    // IPD Diet Management
    public static final String IPD_DIET = "/ipd/diet";
    public static final String IPD_DIET_BY_ID = "/ipd/diet/{id}";
    public static final String IPD_DIET_PLAN = "/ipd/dietPlan";
    public static final String IPD_DIET_BY_IPD_ADMISSION = "/ipd/diets/{ipdAdmissionId}";

    // IPD Discharge
    public static final String IPD_DISCHARGE_FORM = "/ipd/dischargeForm";
    public static final String IPD_DISCHARGE_RECEIPT = "/dischargeReceipt";

    // IPD Document Management
    public static final String IPD_DOCUMENT = "/ipd-document";
    public static final String IPD_DOCUMENT_ID = "/ipd-document/{id}";
    public static final String IPD_DOC_ID = "/ipd-doc/{docId}";
    public static final String IPD_ADMISSION_ID_DOCUMENT_ID = "/ipd-admission-document/{ipdAdmissionId}";

    // IPD Transfer
    public static final String IPD_TRANSFER = "/transfer";
    public static final String IPD_TRANSFER_BY_ADMISSION = "/transfer/{ipdAdmissionId}";
    public static final String IPD_TRANSFER_RECEIPT = "/transfer/receipt";

    // IPD Surgery
    public static final String IPD_SURGERY_FORM = "/ipd-surgery-form";
    public static final String IPD_SURGERY_FORM_BY_ID = "/ipd-surgery-form/{id}";
    public static final String IPD_SURGERIES = "/ipd-surgeries";
    public static final String IPD_SURGERY_BILL = "/surgery-billing/{billingId}";
    public static final String IPD_SURGERY_CANCEL = "/surgery/{surgeryId}/cancel";
    public static final String GET_IPD_SURGERY_BILLINGS = "/surgery-billings";

    // IPD Financials
    public static final String IPD_FINANCIAL_SUMMARY = "/ipd-financial-summary";
    public static final String IPD_FINANCIAL_SUMMARY_REFUND = "/ipd-financial-summary/refund";
    public static final String IPD_FINANCIAL_SUMMARIES = "/ipd-financial-summaries";

    // IPD Billing
    public static final String IPD_BILLING_CREATE = "/billing";
    public static final String IPD_BILLING_UPDATE = "/billing";
    public static final String GET_IPD_BILLINGS = "/billings/{organizationId}/{admissionId}/{id}";
    public static final String IPD_BILLING_CANCEL = "/billing/{billingId}/cancel";
    public static final String IPD_BILLING_ITEM_CANCEL = "/billing/items/cancel";
    public static final String DOWNLOAD_IPD_BILL_API = "/download-ipd-bill/{id}/{orgId}";
    public static final String BILLING_PAYMENT = "/payment/{billingId}";

    // IPD Receipts
    public static final String IPD_RECEIPT_DOWNLOAD = "/download-ipd-receipt";
    public static final String IPD_RECEIPT = "/ipd-receipt";

    // Final Billing
    public static final String CREATE_FINAL_BILL = "/create";
    public static final String GET_FINAL_BILL = "/get/{admissionId}";
    public static final String FINAL_BILL_PAYMENT = "/finalBillPayment";
    public static final String FINAL_BILL_SUMMARY = "/summary/{admissionId}";
    public static final String FINAL_BILL_SUMMARY_PDF = "/summary/pdf/{admissionId}/{orgId}";
    public static final String FINAL_BILL_PDF = "/pdf/{admissionId}/{orgId}";
    public static final String UPDATE_FINAL_BILL_DISCOUNT = "/discount";
    public static final String REFRESH_FINAL_BILL = "/refresh/{admissionId}";

    private EndpointConstants() {
        // Private constructor to prevent instantiation
    }
}



