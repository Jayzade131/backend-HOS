package com.org.hosply360.service.globalMaster.impl;

import com.org.hosply360.constant.ApplicationConstant;
import com.org.hosply360.constant.Enums.AppointmentStatus;
import com.org.hosply360.constant.Enums.ReceiptStatus;
import com.org.hosply360.constant.ErrorConstant;
import com.org.hosply360.controller.OPD.InvoiceItemsResponseDto;
import com.org.hosply360.controller.globalMaster.BillingItemInfoDTO;
import com.org.hosply360.dao.OPD.Appointment;
import com.org.hosply360.dao.OPD.InvoiceItems;
import com.org.hosply360.dao.OPD.OPDInvoice;
import com.org.hosply360.dao.OPD.OPDReceipt;
import com.org.hosply360.dao.frontDeskDao.Patient;
import com.org.hosply360.dao.globalMaster.BillingItem;
import com.org.hosply360.dao.globalMaster.BillingItemGroup;
import com.org.hosply360.dao.globalMaster.Organization;
import com.org.hosply360.dto.OPDDTO.AppointmentDTO;
import com.org.hosply360.dto.OPDDTO.InvoiceItemsDto;
import com.org.hosply360.dto.OPDDTO.InvoiceResponseDTO;
import com.org.hosply360.dto.OPDDTO.OPDInvoiceDTO;
import com.org.hosply360.dto.OPDDTO.OPDInvoicePaymentUpdateDTO;
import com.org.hosply360.dto.OPDDTO.OPDInvoiceReqDTO;
import com.org.hosply360.dto.OPDDTO.OPDPaymentHistoryReqDTO;
import com.org.hosply360.dto.OPDDTO.ReceiptResponseDTO;
import com.org.hosply360.dto.frontDeskDTO.PatientInfoDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemDTO;
import com.org.hosply360.dto.globalMasterDTO.BillingItemGroupDTO;
import com.org.hosply360.dto.globalMasterDTO.OrganizationDTO;
import com.org.hosply360.dto.utils.PdfHeaderFooterDTO;
import com.org.hosply360.exception.FrontDeskException;
import com.org.hosply360.exception.GlobalMasterException;
import com.org.hosply360.exception.OPDException;
import com.org.hosply360.helper.CustomUserDetails;
import com.org.hosply360.repository.OPDRepo.AppointmentRepository;
import com.org.hosply360.repository.OPDRepo.OPDInvoiceRepository;
import com.org.hosply360.repository.OPDRepo.OPDReceiptRepository;
import com.org.hosply360.repository.frontDeskRepo.PatientRepository;
import com.org.hosply360.repository.globalMasterRepo.BillingItemGroupRepository;
import com.org.hosply360.repository.globalMasterRepo.BillingItemRepository;
import com.org.hosply360.repository.globalMasterRepo.OrganizationMasterRepository;
import com.org.hosply360.service.OPD.OPDInvoiceService;
import com.org.hosply360.service.OPD.OPDPaymentHistoryService;
import com.org.hosply360.util.Others.AmountToWordsUtil;
import com.org.hosply360.util.Others.SequenceGeneratorService;
import com.org.hosply360.util.encryptionUtil.EncryptionUtil;
import com.org.hosply360.util.mapper.HeaderFooterMapperUtil;
import com.org.hosply360.util.mapper.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.org.hosply360.service.OPD.impl.AppointmentServiceImpl.getAppointmentDTO;

@Service
@RequiredArgsConstructor
public class OPDInvoiceServiceImpl implements OPDInvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(OPDInvoiceServiceImpl.class);

    private final OPDInvoiceRepository opdInvoiceRepository;
    private final PatientRepository patientRepository;
    private final OrganizationMasterRepository organizationMasterRepository;
    private final BillingItemGroupRepository billingItemGroupRepository;
    private final BillingItemRepository billingItemRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final OPDReceiptRepository opdReceiptRepository;
    private final OPDPaymentHistoryService opdPaymentHistoryService;
    private final AppointmentRepository appointmentRepository;

    // get dto from entity
    private OPDInvoiceDTO getDto(OPDInvoice entity) {
        PatientInfoDTO patientInfoDTO = null; // initialize the patient info dto
        if (entity.getPatient() != null) { // if the patient is not null
            var personalInfo = entity.getPatient().getPatientPersonalInformation(); // get the personal info
            patientInfoDTO = PatientInfoDTO.builder() // build the patient info dto
                    .id(entity.getPatient().getId())
                    .firstname(personalInfo != null ? personalInfo.getFirstName() + " " + personalInfo.getLastName() : null)
                    .patientNumber(entity.getPatient().getPatientContactInformation().getPrimaryPhone())
                    .pid(entity.getPatient().getPId())
                    .build();
        }
        List<InvoiceItemsDto> invoiceItemDTOs = null; // initialize the invoice item dto list
        if (entity.getInvoiceItems() != null) { // if the invoice items are not null
            invoiceItemDTOs = entity.getInvoiceItems().stream().map(item -> { // map each invoice item to dto
                BillingItemGroupDTO groupDTO = ObjectMapperUtil.copyObject(item.getBillingItemGroup(), BillingItemGroupDTO.class); // copy the billing item group to dto
                if (item.getBillingItemGroup() != null && item.getBillingItemGroup().getOrganization() != null) { // if the billing item group is not null
                    groupDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(item.getBillingItemGroup().getOrganization(), OrganizationDTO.class)); // copy the organization to dto
                }
                BillingItemDTO billingItemDTO = ObjectMapperUtil.copyObject(item.getBillingItem(), BillingItemDTO.class); // copy the billing item to dto
                if (item.getBillingItem() != null && item.getBillingItem().getOrganization() != null) { // if the billing item is not null
                    billingItemDTO.setOrganizationDTO(ObjectMapperUtil.copyObject(item.getBillingItem().getOrganization(), OrganizationDTO.class)); // copy the organization to dto
                }
                return InvoiceItemsDto.builder() // build the invoice item dto
                        .billingItemGroup(groupDTO)
                        .billingItem(billingItemDTO)
                        .quantity(item.getQuantity())
                        .rate(item.getRate())
                        .discountPercent(item.getDiscountPercent() == null ? 0 : item.getDiscountPercent()) // set the discount percent
                        .discountAmount(item.getDiscountAmount() == null ? 0 : item.getDiscountAmount()) // set the discount amount
                        .amount(item.getAmount())
                        .build();
            }).toList(); // convert the list to dto
        }
        AppointmentDTO appointmentDTO = null; // initialize the appointment dto
        if (entity.getAppointment() != null) { // if the appointment is not null
            appointmentDTO = getAppointmentDTO(entity.getAppointment());
        }
        return OPDInvoiceDTO.builder() // build the opd invoice dto
                .id(entity.getId())
                .orgId(entity.getOrg() != null ? entity.getOrg().getId() : null)
                .patient(patientInfoDTO)
                .appointment(appointmentDTO)
                .consultant(entity.getConsultant())
                .invoiceNumber(entity.getInvoiceNumber())
                .invoiceDate(entity.getInvoiceDate())
                .invoiceItems(invoiceItemDTOs)
                .totalAmount(entity.getTotalAmount())
                .discountAmount(entity.getDiscountAmount())
                .amountToPay(entity.getAmountToPay())
                .paidAmount(entity.getPaidAmount())
                .lastPaidAmount(entity.getLastPaidAmount())
                .totalPaidAmount((entity.getAmountToPay() != null && entity.getBalanceAmount() != null)
                        ? entity.getAmountToPay() - entity.getBalanceAmount() // calculate the total paid amount
                        : null)
                .balanceAmount(entity.getBalanceAmount())
                .status(entity.getStatus())
                .receiptGiven(entity.getReceiptGiven())
                .remark(entity.getRemark())
                .defunct(entity.isDefunct())
                .build();
    }

    // create invoice
    @Override
    @Transactional
    public String createInvoice(OPDInvoiceReqDTO requestDTO) {
        logger.info("Creating OPD Invoice for patient {}", requestDTO.getPatientId());
        Patient patient = patientRepository.findByIdAndDefunct(requestDTO.getPatientId(), false) // find the patient by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.PATIENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
        Organization org = organizationMasterRepository.findByIdAndDefunct(requestDTO.getOrgId(), false) // find the organization by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.ORGANIZATION_NOT_FOUND, HttpStatus.BAD_REQUEST));
        Appointment appointment = null; // initialize the appointment
        if (StringUtils.hasText(requestDTO.getAppointmentId())) { // if the appointment id is not null
            appointment = appointmentRepository.findByIdAndDefunct(requestDTO.getAppointmentId(), org.getId(), false) // find the appointment by id and defunct
                    .orElseThrow(() -> new GlobalMasterException(ErrorConstant.APPOINTMENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
        }
        OPDInvoice invoice = ObjectMapperUtil.copyObject(requestDTO, OPDInvoice.class); // copy the request dto to opd invoice
        invoice.setPatient(patient);
        invoice.setAppointment(appointment);
        invoice.setOrg(org);
        invoice.setDefunct(false);
        AtomicReference<Double> totalAmount = new AtomicReference<>(0.0); // initialize the total amount
        AtomicReference<Double> totalDiscount = new AtomicReference<>(0.0); // initialize the total discount
        List<InvoiceItems> invoiceItems = requestDTO.getInvoiceItems().stream().map(itemDTO -> { // map each invoice item to entity
            InvoiceItems item = ObjectMapperUtil.copyObject(itemDTO, InvoiceItems.class); // copy the invoice item dto to entity
            BillingItemGroup group = billingItemGroupRepository.findByIdAndDefunct(itemDTO.getBillingItemGroupId(), false) // find the billing item group by id and defunct
                    .orElseThrow(() -> new GlobalMasterException(ErrorConstant.BILLING_ITEM_GROUP_NOT_FOUND, HttpStatus.BAD_REQUEST));
            BillingItem billingItem = billingItemRepository.findByIdAndDefunct(itemDTO.getBillingItemId(), false) // find the billing item by id and defunct
                    .orElseThrow(() -> new GlobalMasterException(ErrorConstant.BILLING_ITEM_NOT_FOUND, HttpStatus.BAD_REQUEST));
            item.setBillingItemGroup(group);
            item.setBillingItem(billingItem);
            double itemTotal = item.getRate() * item.getQuantity(); // calculate the total amount
            double itemDiscount = item.getDiscountAmount(); // calculate the discount amount
            totalAmount.updateAndGet(v -> v + itemTotal); // update the total amount
            totalDiscount.updateAndGet(v -> v + itemDiscount); // update the total discount
            return item; // return the invoice item
        }).toList(); // convert the stream to list
        invoice.setInvoiceItems(invoiceItems); // set the invoice items
        double amountToPay = totalAmount.get() - totalDiscount.get(); // calculate the amount to pay
        double paidAmount = requestDTO.getPaidAmount() != null ? requestDTO.getPaidAmount() : 0.0; // calculate the paid amount
        double balance = totalAmount.get() - totalDiscount.get() - paidAmount; // calculate the balance


        long seqNum = sequenceGeneratorService.generateInvoiceSequence("Invoice_Number"); // generate the invoice number

        String formattedId = String.format("INV-%s-%s-%05d", org.getOrganizationCode(), LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")), seqNum); // format the invoice number


        invoice.setInvoiceNumber(formattedId);
        invoice.setTotalAmount(totalAmount.get());
        invoice.setDiscountAmount(totalDiscount.get());
        invoice.setAmountToPay(amountToPay);
        invoice.setPaidAmount(paidAmount);
        invoice.setBalanceAmount(balance);
        invoice.setStatus(balance == 0.0 ? ApplicationConstant.PAID : (balance > 0.0 ? ApplicationConstant.PROCESSING : ApplicationConstant.UNPAID));
        OPDInvoice saved = opdInvoiceRepository.save(invoice); // save the invoice
        return saved.getId(); // return the saved invoice id
    }

    // update invoice
    @Override
    @Transactional
    public OPDInvoiceDTO updateInvoice(OPDInvoiceReqDTO requestDTO) {
        OPDInvoice existing = opdInvoiceRepository.findByIdAndDefunctAndOrg(requestDTO.getId(), false, requestDTO.getOrgId()) // find the invoice by id and defunct and organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.OPD_INVOICE_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (existing.getStatus().equalsIgnoreCase(ApplicationConstant.PAID)) { // if the invoice is paid
            throw new GlobalMasterException(ErrorConstant.PAID_INVOICE, HttpStatus.BAD_REQUEST);
        }
        Patient patient = patientRepository.findByIdAndDefunct(requestDTO.getPatientId(), false) // find the patient by id and defunct
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.PATIENT_NOT_FOUND, HttpStatus.BAD_REQUEST));
        ObjectMapperUtil.safeCopyObjectAndIgnore(requestDTO, existing, List.of("paidAmount")); // copy the request dto to existing invoice
        existing.setPatient(patient);
        AtomicReference<Double> totalAmount = new AtomicReference<>(0.0); // initialize the total amount
        AtomicReference<Double> totalDiscount = new AtomicReference<>(0.0); // initialize the total discount
        List<InvoiceItems> invoiceItems = null; // initialize the invoice items
        if (requestDTO.getInvoiceItems() != null) { // if the invoice items are not null
            invoiceItems = requestDTO.getInvoiceItems().stream().map(itemDTO -> { // map each invoice item to entity
                InvoiceItems item = ObjectMapperUtil.copyObject(itemDTO, InvoiceItems.class); // copy the invoice item dto to entity
                BillingItemGroup group = null; // initialize the billing item group
                if (itemDTO.getBillingItemGroupId() != null && !itemDTO.getBillingItemGroupId().trim().isEmpty()) { // if the billing item group id is not null or empty
                    group = billingItemGroupRepository.findByIdAndDefunct(itemDTO.getBillingItemGroupId(), false) // find the billing item group by id and defunct
                            .orElseThrow(() -> new GlobalMasterException(ErrorConstant.BILLING_ITEM_GROUP_NOT_FOUND, HttpStatus.BAD_REQUEST)); // throw exception if not found
                }
                BillingItem billingItem = null; // initialize the billing item
                if (itemDTO.getBillingItemId() != null && !itemDTO.getBillingItemId().trim().isEmpty()) { // if the billing item id is not null or empty
                    billingItem = billingItemRepository.findByIdAndDefunct(itemDTO.getBillingItemId(), false) // find the billing item by id and defunct
                            .orElseThrow(() -> new GlobalMasterException(ErrorConstant.BILLING_ITEM_NOT_FOUND, HttpStatus.BAD_REQUEST));
                }
                item.setBillingItemGroup(group);
                item.setBillingItem(billingItem);
                double itemTotal = item.getRate() * item.getQuantity(); // calculate the total amount
                double itemDiscount = item.getDiscountAmount(); // calculate the discount amount
                totalAmount.updateAndGet(v -> v + itemTotal); // update the total amount
                totalDiscount.updateAndGet(v -> v + itemDiscount); // update the total discount
                return item; // return the invoice item
            }).toList(); // convert the stream to list
            existing.setInvoiceItems(invoiceItems);
        }
        double amountToPay = totalAmount.get() - totalDiscount.get(); // calculate the amount to pay
        double paidAmount = requestDTO.getPaidAmount() != null // if the paid amount is not null
                ? requestDTO.getPaidAmount() // return the paid amount
                : (existing.getPaidAmount() != null ? existing.getPaidAmount() : 0.0); // return the existing paid amount
        double balance = amountToPay - paidAmount; // calculate the balance
        boolean hasInvoiceItems = existing.getInvoiceItems() != null && existing.getInvoiceItems().stream() // check if the invoice items are not null and has any invoice items
                .anyMatch(group -> group.getBillingItemGroup() != null && !group.getBillingItemGroup().equals("")); // check if the billing item group is not null and not empty
        existing.setTotalAmount(totalAmount.get());
        existing.setDiscountAmount(totalDiscount.get());
        existing.setAmountToPay(amountToPay);
        existing.setPaidAmount(paidAmount);
        existing.setBalanceAmount(balance);
        existing.setStatus(
                balance == 0.0
                        ? (hasInvoiceItems ? ApplicationConstant.PAID : ApplicationConstant.UNPAID) // if the balance is 0 and has invoice items, set the status to paid
                        : (balance > 0.0 ? ApplicationConstant.PROCESSING : ApplicationConstant.UNPAID) // if the balance is greater than 0, set the status to processing
        );
        OPDInvoice saved = opdInvoiceRepository.save(existing); // save the existing invoice
        return getDto(saved); // return the saved invoice
    }

    // get invoice by id
    @Override
    public OPDInvoiceDTO getInvoiceById(String id, String orgId) {
        logger.info("Fetching OPD Invoice with ID: {}", id);
        OPDInvoice invoice = opdInvoiceRepository.findByIdAndDefunctAndOrg(id, false, orgId).get(); // find the invoice by id and defunct and organization id
        return getDto(invoice); // return the invoice dto
    }

    // get invoices by filters
    @Override
    public List<OPDInvoiceDTO> getInvoicesByFilters(String orgId, String pId, LocalDate fromDate, LocalDate toDate) {
        logger.info("Filtering OPD Invoices | orgId: {}, patientId: {}, from: {}, to: {}", orgId, pId, fromDate, toDate);
        LocalDateTime startDateTime = (fromDate != null) ? fromDate.atStartOfDay() : null; // convert the from date to start of day
        LocalDateTime endDateTime = (toDate != null) ? toDate.atTime(LocalTime.MAX) : null; // convert the to date to end of day
        List<OPDInvoice> invoices; // initialize the invoices list
        if (StringUtils.hasText(pId)) { // if the patient id is not null or empty
            if (startDateTime != null && endDateTime != null) { // if the start and end date are not null
                invoices = opdInvoiceRepository.findInvoicesByOrgIdAndPatientPIdAndDateRange(orgId, pId, startDateTime, endDateTime); // find the invoices by organization id and patient id and date range
            } else {
                invoices = opdInvoiceRepository.findInvoicesByOrgIdAndPatientPId(orgId, pId); // find the invoices by organization id and patient id
            }
        } else {
            if (startDateTime != null && endDateTime != null) { // if the start and end date are not null
                invoices = opdInvoiceRepository.findByOrgIdAndInvoiceDateBetweenAndDefunct(orgId, startDateTime, endDateTime, false); // find the invoices by organization id and invoice date between and defunct
            } else {
                invoices = opdInvoiceRepository.findByOrgIdAndDefunct(orgId, false); // find the invoices by organization id and defunct
            }
        }
        logger.debug("Found {} invoices", invoices.size());
        return invoices.stream().map(this::getDto).toList(); // return the invoices dto list
    }

    // get invoices with appointments
    @Override
    public List<OPDInvoiceDTO> getInvoicesWithAppointments(String orgId, LocalDate fromDate, LocalDate toDate) {
        logger.info("Fetching OPD invoices with non-null appointments | orgId: {}, from: {}, to: {}", orgId, fromDate, toDate);
        List<OPDInvoice> invoices; // initialize the invoices list
        LocalDateTime startDateTime = (fromDate != null) ? fromDate.atStartOfDay() : null; // convert the from date to start of day
        LocalDateTime endDateTime = (toDate != null) ? toDate.atTime(LocalTime.MAX) : null; // convert the to date to end of day
        if (startDateTime != null && endDateTime != null) { // if the start and end date are not null
            invoices = opdInvoiceRepository.findByOrgIdAndAppointmentNotNullAndInvoiceDateBetweenAndDefunctFalse(
                    orgId, startDateTime, endDateTime); // find the invoices by organization id and appointment not null and invoice date between and defunct false
        } else {
            invoices = opdInvoiceRepository.findByOrgIdAndAppointmentNotNullAndDefunctFalse(orgId); // find the invoices by organization id and appointment not null and defunct false
        }
        if (invoices.isEmpty()) {
            throw new GlobalMasterException(ErrorConstant.OPD_INVOICE_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return invoices.stream().map(this::getDto).toList(); // return the invoices dto list
    }

    // delete invoice
    @Override
    public void deleteInvoice(String id, String orgId) {
        logger.info("Deleting OPD Invoice {}", id);
        OPDInvoice invoice = opdInvoiceRepository.findByIdAndDefunctAndOrg(id, false, orgId) // find the invoice by id and defunct and organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.OPD_INVOICE_NOT_FOUND, HttpStatus.NOT_FOUND));
        invoice.setDefunct(true); // set the defunct to true
        opdInvoiceRepository.save(invoice); // save the invoice
        logger.info("OPD Invoice soft deleted successfully with ID: {}", id);
    }


    @Override
    public InvoiceResponseDTO generateInvoiceDetailsByIdentifier(String identifier) {


        OPDInvoice invoice = opdInvoiceRepository.findById(identifier)
                .orElseGet(() -> opdInvoiceRepository.findByAppointmentIdAndDefunctFalse(identifier)
                        .orElseThrow(() -> new FrontDeskException(
                                ErrorConstant.OPD_INVOICE_NOT_FOUND, HttpStatus.NOT_FOUND)));

        // Patient full name
        String fullName = invoice.getPatient().getPatientPersonalInformation().getFirstName() + " "
                + invoice.getPatient().getPatientPersonalInformation().getLastName();

        // Age
        LocalDate dob = LocalDate.parse(invoice.getPatient().getPatientPersonalInformation().getDateOfBirth());
        Period age = Period.between(dob, LocalDate.now());
        String ageStr = age.getYears() + " Years " + age.getMonths() + " Months";

        // Convert invoice items → InvoiceItemsResponseDto
        List<InvoiceItemsResponseDto> itemDTOs = invoice.getInvoiceItems().stream().map(item ->
                InvoiceItemsResponseDto.builder()
                        .billingItemGroupName(item.getBillingItemGroup().getItemGroupName())
                        .billingItem(
                                BillingItemInfoDTO.builder()
                                        .id(item.getBillingItem().getId())
                                        .itemName(item.getBillingItem().getItemName())
                                        .rate(item.getBillingItem().getRate())
                                        .build()
                        )
                        .quantity(item.getQuantity())
                        .rate(item.getRate())
                        .amount(item.getAmount())
                        .build()
        ).toList();

        return InvoiceResponseDTO.builder()
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate().toLocalDate())
                .patientId(invoice.getPatient().getPId())
                .patientName(fullName)
                .age(ageStr)
                .gender(invoice.getPatient().getPatientPersonalInformation().getGender())
                .mobile(invoice.getPatient().getPatientContactInformation().getPrimaryPhone())
                .consultant(invoice.getConsultant())
                .invoiceItems(itemDTOs)
                .totalAmount(invoice.getTotalAmount())
                .discountAmount(invoice.getDiscountAmount())
                .amountToPay(invoice.getAmountToPay())
                .amountInWords(AmountToWordsUtil.convertToWords(invoice.getAmountToPay()))
                .status(invoice.getStatus())
                .headerFooter(HeaderFooterMapperUtil.buildHeaderFooter(invoice.getOrg()))
                .build();
    }


    // update paid amount
    @Transactional
    @Override
    public String updatePaidAmount(OPDInvoicePaymentUpdateDTO dto) {
        logger.info("Updating paid amount and generating receipt for Invoice ID: {}, Org ID: {}", dto.getInvoiceId(), dto.getOrgId());
        Optional.ofNullable(dto.getNewAmount()).filter(amount -> amount > 0) // filter the amount
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.PAID_AMOUNT_INVALID, HttpStatus.BAD_REQUEST));
        OPDInvoice invoice = opdInvoiceRepository.findByIdAndDefunctAndOrg(dto.getInvoiceId(), false, dto.getOrgId()) // find the invoice by id and defunct and organization id
                .orElseThrow(() -> new GlobalMasterException(ErrorConstant.OPD_INVOICE_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (invoice.getBalanceAmount() == 0) { // if the balance amount is 0
            throw new GlobalMasterException(ErrorConstant.PAID_INVOICE, HttpStatus.BAD_REQUEST);
        }
        double updatedPaidAmount = invoice.getPaidAmount() + dto.getNewAmount(); // update the paid amount
        double newBalance = Math.max(invoice.getAmountToPay() - updatedPaidAmount, 0.0); // update the balance amount
        invoice.setPaidAmount(updatedPaidAmount);
        invoice.setBalanceAmount(newBalance);
        invoice.setLastPaidAmount(dto.getNewAmount());
        invoice.setLastPaymentType(dto.getPaymentType());
        invoice.setStatus(newBalance <= 0 ? ApplicationConstant.PAID : updatedPaidAmount > 0 ? ApplicationConstant.PROCESSING : ApplicationConstant.UNPAID);
        OPDInvoice updatedInvoice = opdInvoiceRepository.save(invoice); // save the invoice
        if (updatedInvoice.getAppointment() != null && updatedInvoice.getBalanceAmount() <= 0) { // if the balance amount is 0
            Appointment appointment = appointmentRepository.findByIdAndDefunct(updatedInvoice.getAppointment().getId(), updatedInvoice.getOrg().getId(), false) // find the appointment by id and defunct
                    .orElseThrow(() -> new OPDException(ErrorConstant.APPOINTMENT_NOT_FOUND, HttpStatus.NOT_FOUND));
            appointment.setStatus(AppointmentStatus.PAID);
            appointmentRepository.save(appointment); // save the appointment
        }
        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // get the principal
        String userRole = principal.getUser().getUsername(); // get the user role
        String receiptNumber = sequenceGeneratorService.generateReceiptNumber(); // generate the receipt number
        OPDReceipt savedReceipt = opdReceiptRepository.save(OPDReceipt.builder() // save the receipt
                .invoiceId(updatedInvoice.getInvoiceNumber())
                .receiptNumber(receiptNumber)
                .receiptDate(LocalDateTime.now())
                .generatedBy(userRole)
                .paidAmount(dto.getNewAmount())
                .paymentType(dto.getPaymentType())
                .chequeNumber(dto.getChequeNumber())
                .bankName(dto.getBankName())
                .chequeDate(dto.getChequeDate())
                .defunct(false)
                .receiptGiven(ReceiptStatus.No)
                .build());
        opdPaymentHistoryService.save(OPDPaymentHistoryReqDTO.builder() // save the payment history
                .invoiceId(updatedInvoice.getInvoiceNumber())
                .receiptId(savedReceipt.getId())
                .paymentAmount(dto.getNewAmount())
                .paymentType(dto.getPaymentType())
                .chequeNumber(dto.getChequeNumber())
                .bankName(dto.getBankName())
                .chequeDate(dto.getChequeDate())
                .paymentDate(savedReceipt.getReceiptDate())
                .recievedBy(userRole)
                .build());
        return savedReceipt.getId(); // return the receipt id
    }


    @Override
    public ReceiptResponseDTO getReceiptData(String receiptId) {

        OPDReceipt receipt = opdReceiptRepository.findById(receiptId)
                .orElseThrow(() -> new FrontDeskException(ErrorConstant.RECEIPT_NOT_FOUND, HttpStatus.NOT_FOUND));

        OPDInvoice invoice = opdInvoiceRepository
                .findByInvoiceNumberAndDefunct(receipt.getInvoiceId(), false)
                .orElseThrow(() -> new FrontDeskException(ErrorConstant.INVOICE_NOT_FOUND_FOR_RECEIPT, HttpStatus.NOT_FOUND));

        Organization organization = invoice.getOrg();

        // Patient Full name
        var p = invoice.getPatient().getPatientPersonalInformation();
        String patientName = (EncryptionUtil.decrypt(p.getFirstName()) + " " +
                EncryptionUtil.decrypt(p.getLastName())).trim();

        // Build Header / Footer
        PdfHeaderFooterDTO headerFooter = HeaderFooterMapperUtil.buildHeaderFooter(organization);
        boolean isChequeMode = receipt.getPaymentType() != null &&
                receipt.getPaymentType().equalsIgnoreCase("cheque");

        ReceiptResponseDTO.ReceiptResponseDTOBuilder builder = ReceiptResponseDTO.builder()
                .receiptNumber(receipt.getReceiptNumber())
                .receiptDate(receipt.getCreatedDate())
                .invoiceNumber(invoice.getInvoiceNumber())
                .patientId(invoice.getPatient().getPId())
                .patientName(patientName)
                .amountReceived(invoice.getLastPaidAmount())
                .paymentMode(invoice.getLastPaymentType())
                .amountInWords(AmountToWordsUtil.convertToWords(invoice.getLastPaidAmount()))
                .headerFooter(headerFooter);

        if (isChequeMode) {
            builder.chequeNumber(receipt.getChequeNumber());
            builder.chequeDate(receipt.getChequeDate() != null ? receipt.getChequeDate().toString() : null);
            builder.bankName(receipt.getBankName());
        }

        ReceiptResponseDTO response = builder.build();


        // Mark receipt as given only first time
        if (ReceiptStatus.No.equals(receipt.getReceiptGiven())) {
            receipt.setReceiptGiven(ReceiptStatus.Yes);
            opdReceiptRepository.save(receipt);
        }

        return response;
    }


}
