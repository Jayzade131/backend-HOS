package com.org.hosply360.constant;

/**
 * Centralized error messages for the application.
 * Organized by functional areas for better maintainability.
 */
public class ErrorConstant {
    // ============================== AUTHENTICATION & AUTHORIZATION ==============================
    public static final String INVALID_TOKEN = "Invalid token";
    public static final String INVALID_AUTHENTICATION = "Invalid authentication";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_WITH_USERNAME_ALREADY_EXISTS = "User with this username already exists";
    public static final String USER_WITH_EMAIL_ALREADY_EXISTS = "User with this email already exists";
    public static final String USER_WITH_MOBILE_NO_ALREADY_EXISTS = "User with this phone number already exists";
    public static final String PASSWORD_NOT_MATCH = "New password and confirm password do not match";
    public static final String INVALID_DEFAULT_PASSWORD = "Default password is invalid. Please enter a valid default password.";
    public static final String CANNOT_DELETE_OWN_ACCOUNT = "Request denied: self-deletion is restricted by policy.";

    // ==================================== USER MANAGEMENT =====================================
    public static final String USER_ALREADY_ASSIGNED_AS_DOCTOR = "User is already assigned as doctor";
    public static final String USER_SHOULD_BE_HAVE_DOCTOR_ROLE = "User should have DOCTOR role to be associated as a doctor.";

    // ================================== ACCESS CONTROL =======================================
    public static final String ACCESS_NAME_ALREADY_EXISTS = "Access name already exists";
    public static final String ACCESS_NOT_FOUND = "Access not found";
    public static final String MODULE_NOT_FOUND = "Module not found";
    public static final String MODULE_NAME_ALREADY_EXISTS = "Module name already exists";
    public static final String ROLE_NOT_FOUND = "Role not found";
    public static final String ROLE_NAME_ALREADY_EXISTS = "Role name already exists";
    public static final String ROLE_MODULE_MAPPING_NOT_FOUND = "Role module mapping not found";
    public static final String MODULE_ACCESS_MAPPING_NOT_FOUND = "Module access mapping not found";

    // ================================== ORGANIZATION =========================================
    public static final String ORGANIZATION_ALREADY_EXISTS = "Organization already exists with id ";
    public static final String ORGANIZATION_CODE_ALREADY_EXISTS = "Organization Code already exists";
    public static final String ORGANIZATION_NOT_FOUND = "Organization not found";
    public static final String ORGANIZATION_REQUIRED = "Organization is required";
    public static final String ORGANIZATION_CANNOT_BE_ITS_OWN_PARENT = "Organization cannot be its own parent";
    public static final String ORGANIZATION_AND_PATIENT_REQUIRED = "Organization and patient are required";
    public static final String ORGANIZATION_ID_MISMATCH_DURING_UPDATE = "Organization ID mismatch during update";

    // =================================== MASTER DATA =========================================
    public static final String LANGUAGE_CODE_ALREADY_EXISTS = "Language code already exists";
    public static final String LANGUAGE_NOT_FOUND = "Language not found";
    public static final String OCCUPATION_CODE_ALREADY_EXISTS = "Occupation code already exists";
    public static final String OCCUPATION_NOT_FOUND = "Occupation not found";
    public static final String SPECIALITY_NOT_FOUND = "Speciality not found";
    public static final String RELIGION_NOT_FOUND = "Religion not found";
    public static final String CURRENCY_NOT_FOUND = "Currency not found";
    public static final String CURRENCY_CODE_ALREADY_EXISTS = "Currency code already exists";
    public static final String ID_DOCUMENT_NOT_FOUND = "Document not found";
    public static final String TEMPLATE_NOT_FOUND = "Template not found";
    public static final String CONFIG_NOT_FOUND = "Configuration not found";
    public static final String COMPANY_ALREADY_EXISTS = "Company already exists";
    public static final String COMPANY_NOT_FOUND = "Company not found";
    public static final String PATIENT_CATEGORY_ALREADY_EXISTS = "Patient category already exists";
    public static final String PATIENT_CATEGORY_NOT_FOUND = "Patient category not found";

    // ==================================== DOCTOR ============================================
    public static final String DOCTOR_ALREADY_EXISTS = "Doctor already exists with registration number";
    public static final String DOCTOR_NOT_FOUND = "Doctor not found";
    public static final String DOCTOR_AND_SPECIALITY_REQUIRED = "Doctor and speciality are mandatory";
    public static final String SCHEDULE_NOT_FOUND = "Schedule not found for the doctor";
    public static final String DUPLICATE_DOCTOR_SCHEDULE = "Schedule already exists for this doctor";
    public static final String WEEKLY_SCHEDULE_MUST_NOT_BE_EMPTY = "Weekly schedule must not be empty";
    public static final String AT_LEAST_ONE_VALID_SCHEDULE_IS_REQUIRED = "At least one valid schedule is required";

    // ==================================== APPOINTMENT =======================================
    public static final String APPOINTMENT_NOT_FOUND = "Appointment not found";
    public static final String APPOINTMENT_CONFLICT = "Appointment conflict detected. Doctor is already booked for the selected time slot";
    public static final String APPOINTMENT_DATE_CANNOT_BE_BEFORE_TODAY = "Appointment date cannot be in the past";
    public static final String TIME_CONFLICT_END_TIME = "End time must be after start time";
    public static final String TIME_BOUND = "Time is outside session bounds";
    public static final String OP_TIMETABLE_NOT_FOUND = "OP Timetable not found";
    public static final String SESSION_MORNING_START_BEFORE_END = "Morning session start must be before end";
    public static final String SESSION_EVENING_START_BEFORE_END = "Evening session start must be before end";
    public static final String SESSION_AFTERNOON_START_BEFORE_END = "Afternoon session start must be before end";
    public static final String OPD_INVOICE_NOT_FOUND = "OPD Invoice not found";
    public static final String DONT_EDIT_THE_PRESCRIPTION_WHEN_APPOINTMENT_STATUS_IS_PAID = "Don't edit the prescription when appointment status is paid";
    public static final String ERROR_IN_GENERATING_RECEIPT=  "Failed to generate Money Receipt PDF";

    // ==================================== PATIENT ===========================================
    public static final String PATIENT_NOT_FOUND = "Patient not found";
    public static final String ADDRESS_NOT_FOUND = "Address not found";
    public static final String INVALID_IDENTIFICATION_ID = "Invalid identification document ID in request";
    public static final String DOCUMENT_NUMBER_LENGTH_MISMATCH = "Document number length does not match limit for document ID: ";

    // ==================================== BILLING & PAYMENT =================================
    public static final String BILLING_ITEM_NOT_FOUND = "Billing item not found";
    public static final String BILLING_ITEM_GROUP_NOT_FOUND = "Billing item group not found";
    public static final String BILLING_ALREADY_CANCELLED = "Billing already cancelled";
    public static final String BILL_NOT_FOUND = "Bill not found";
    public static final String BILL_ALREADY_SETTLED = "Bill already settled";
    public static final String PAYMENT_EXCEEDS_REMAINING_BALANCE = "Payment exceeds remaining balance";
    public static final String PAID_AMOUNT_INVALID = "Paid amount must be greater than 0";
    public static final String RECEIPT_NOT_FOUND = "Receipt not found with the given ID";
    public static final String INVOICE_NOT_FOUND_FOR_RECEIPT = "Invoice not found for the given receipt ID";
    public static final String TARIFF_ALREADY_EXISTS = "Tariff already exists";
    public static final String TARIFF_NOT_FOUND = "Tariff not found";
    public static final String LEDGER_NOT_FOUND = "Ledger not found";
    public static final String GST_NUMBER_REQUIRED = "GST number is required for registered ledgers";

    // ==================================== TEST & PATHOLOGY ==================================
    public static final String TEST_NOT_FOUND = "Test not found";
    public static final String TEST_REPORT_NOT_FOUND = "Test report not found";
    public static final String TEST_MANAGER_NOT_FOUND = "Test manager not found";
    public static final String TEST_MANAGER_ALREADY_PAID = "Test manager already paid";
    public static final String TEST_NOT_FOUND_IN_PACKAGE = "Test not found in package";
    public static final String INVALID_TEST_SOURCE = "Invalid test source";
    public static final String INVALID_TEST_PACKAGE = "Invalid test in package";
    public static final String PACKAGE_TEST_REPORT_NOT_FOUND = "Package test report not found";
    public static final String NO_TEST_FOUND = "No tests found in the package";
    public static final String PLEASE_PROVIDE_EITHER_TEST_ID_OR_PACKAGE_ID = "Please provide either testId or packageId.";
    public static final String TEST_DATA_IS_REQUIRED = "Test input data is required";
    public static final String REPORT_ID_IS_REQUIRED = "Report ID is required";
    public static final String MISSING_INPUT_FOR_TEST_ID = "Missing input for test ID: ";
    public static final String PAID_INVOICE = "OPD Invoice already paid and hence cannot be updated or deleted";
    public static final String MISSING_PARAMETER = "Missing parameter: ";
    public static final String NO_TEST_FOUND_IN_PACKAGE = "No tests found in the package";
    public static final String CHANGE_THE_STATUS = "First change the test manager status to Accepted";
    public static final String ONLY_PENDING_OR_ACCEPTED_UPDATES_ALLOWED = "Only PENDING or ACCEPTED status updates are allowed";
    public static final String DO_NOT_CHANGE_THE_STATUS = "Cannot change the status when it is completed";
    public static final String INVALID_PAYMENT_AMOUNT = "Invalid Payment Amount";
    public static final String AMOUNT_IS_GREATER_THAN_BALANCE = "Amount is greater than balance";
    public static final String TEST_REPORT_ALREADY_EXISTS = "Test report already exists";
    public static final String TEST_PARAMETER_VALUE_EMPTY = "Test parameter value is missing for parameter: ";


    // ==================================== IPD ===============================================
    public static final String IPD_ADMISSION_NOT_FOUND = "Admission not found";
    public static final String IPD_ADMISSION_ID_NOT_FOUND = "IPD admission ID not found";
    public static final String IPD_ADMISSION_REQUIRED = "IPD admission is required";
    public static final String IPD_ADMISSION_UPDATE_FAILED = "Failed to update IPD admission";
    public static final String IPD_DISCHARGE_NOT_FOUND = "IPD discharge not found";
    public static final String IPD_DISCHARGE_ALREADY_EXISTS = "IPD discharge already exists";
    public static final String CANNOT_MAKE_PAYMENT_FOR_A_CANCELED_BILL = "Cannot make payment for a canceled bill";
    public static final String IPD_DIET_NOT_FOUND = "IPD diet not found";
    public static final String ADMISSION_ID_MISMATCH_DURING_UPDATE = "Admission ID mismatch during update";
    public static final String IPD_ADVANCE_NOT_FOUND = "IPD advance not found";
    public static final String IPD_FINAL_BILL_NOT_FOUND = "IPD final bill not found";
    public static final String IPD_RECEIPT_NOT_FOUND = "IPD receipt not found";
    public static final String IPD_ADVANCE_ALREADY_REFUNDED = "IPD advance already refunded";
    public static final String CANNOT_CANCEL_IPD = "Cannot cancel IPD admission";
    public static final String ADMISSION_NOT_FOUND = "Admission not found";
    public static final String ADMISSION_DATE_REQUIRED = "Admission date is required";
    public static final String BED_ASSIGNMENT_REQUIRED = "Bed assignment is required for IPD admission";
    public static final String WARD_ASSIGNMENT_REQUIRED = "Ward assignment is required for IPD admission";
    public static final String PATIENT_TYPE_REQUIRED = "Patient type is required for IPD admission";
    public static final String PATIENT_CASE_TYPE_REQUIRED = "Patient case type (New/Revisit) is required";
    public static final String PATIENT_CHANGE_NOT_ALLOWED = "Patient change not allowed";
    public static final String BED_NOT_AVAILABLE = "Bed '%s' in ward '%s' is not available";
    public static final String BED_ALREADY_BOOKED = "The bed is already booked";
    public static final String WARD_MASTER_NOT_FOUND = "Ward master not found";
    public static final String WARD_BED_MASTER_NOT_FOUND = "Bed master not found";
    public static final String SURGERY_NOT_FOUND = "Surgery not found";
    public static final String FINANCIAL_SUMMARY_NOT_FOUND = "Financial summary not found";
    public static final String TRANSFER_NOT_ALLOWED = "Transfer not allowed. Patient is already discharged/cancelled/expired";
    public static final String DIET_CREATION_FAILED = "Diet creation failed";
    public static final String DIET_UPDATE_FAILED = "Diet update failed";
    public static final String DIET_NOT_FOUND = "Diet not found";
    public static final String DIET_DELETION_FAILED = "Diet deletion failed";
    public static final String DOCUMENT_HEAD_REQUIRED = "Document head is required";
    public static final String DOCUMENT_REQUIRED = "Document is required";
    public static final String CONSULTANT_ID_REQUIRED = "Consultant ID is required";
    public static final String IPD_RECEIPT_TOTAL_PAID_AMOUNT_MUST_BE_GREATER_THAN_ZERO = "IPD Receipt total paid amount must be greater than zero";
    public static final String CANNOT_UPDATE_DISCHARGE_IPD_DOCUMENT = "Cannot update discharged IPD document";
    public static final String IPD_RECEIPT_PAYMENT_MODE_IS_REQUIRED = "IPD Receipt payment mode is required";
    public static final String IPD_RECEIPT_CHEQUE_NUMBER_IS_REQUIRED = "IPD Receipt cheque number is required";
    public static final String IPD_RECEIPT_BANK_NAME_IS_REQUIRED = "IPD Receipt bank name is required";
    public static final String BILLING_NOT_FOUND = "Billing not found";
    public static final String UNSUPORTED_PATIENT_TYPE = "Unsupported patient type";

    // ==================================== GENERAL ===========================================
    public static final String INVALID_REQUEST_DATA = "Please provide valid data";
    public static final String INVALID_MASTER_TYPE = "Invalid master type";
    public static final String DUPLICATE_ENTRY = "Duplicate entry";
    public static final String DATA_NOT_FOUND = "Data not found for the given ID";
    public static final String INVALID_LOGO_FILE_FORMAT = "Invalid logo file format";
    public static final String UPDATE_FAILED = "Update failed. Please try again";
    public static final String INVALID_STATUS = "This status change is not allowed";
    public static final String INVALID_DATE_RANGE = "Invalid date range";
    public static final String GRP_ID_NOT_FOUND = "Group ID not found";
    public static final String ITEM_NOT_FOUND = "Item not found";
    public static final String ITEM_ALREADY_EXISTS = "Item already exists with this name";
    public static final String ITEM_GROUP_NOT_FOUND = "Item group not found";
    public static final String ITEM_GROUP_ALREADY_EXISTS = "Item group already exists with this name";
    public static final String DEPARTMENT_NOT_FOUND = "Department not found";
    public static final String PACKAGE_NOT_FOUND = "Package not found";
    public static final String PACKAGE_ALREADY_EXISTS = "Package already exists with this name";
    public static final String PRESCRIPTION_NOT_FOUND = "Prescription not found";
    public static final String MEDICINE_NOT_FOUND = "Medicine not found";
    public static final String INSURANCE_PROVIDER_NOT_FOUND = "Insurance provider not found";
    public static final String INSURANCE_PROVIDER_CODE_ALREADY_EXISTS = "Insurance provider code already exists";
}
