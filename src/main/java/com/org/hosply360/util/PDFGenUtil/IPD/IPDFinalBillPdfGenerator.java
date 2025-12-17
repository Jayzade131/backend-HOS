package com.org.hosply360.util.PDFGenUtil.IPD;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.org.hosply360.dto.IPDDTO.IPDBillingDTO;
import com.org.hosply360.dto.IPDDTO.IPDBillingItemDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillFullResDTO;
import com.org.hosply360.dto.IPDDTO.IPDFinalBillSummaryDTO;
import com.org.hosply360.dto.IPDDTO.IPDSurgeryBillingDTO;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class IPDFinalBillPdfGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");


    public byte[] generate(IPDFinalBillSummaryDTO dto) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(40, 36, 36, 36);

            PdfFont bold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

            // 🧾 Title
            Paragraph title = new Paragraph("FINAL BILL SUMMARY")
                    .setFont(bold)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15);
            document.add(title);

            // 📋 Bill Info Table
            Table billTable = new Table(UnitValue.createPercentArray(new float[]{2, 3}));
            billTable.setWidth(UnitValue.createPercentValue(100));

            addRow(billTable, "Final Bill No:", safeString(dto.getFinalBillNo()), bold, normal);
            addRow(billTable, "Final Bill Date:", dto.getFinalBillDate() != null
                    ? dto.getFinalBillDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    : "-", bold, normal);
            addRow(billTable, "Has Settled:", dto.getHasSettled() != null && dto.getHasSettled() ? "Yes" : "No", bold, normal);

            document.add(new Paragraph("Bill Information").setFont(bold).setFontSize(12).setMarginTop(10));
            document.add(billTable);

            // 👤 Patient Info Section
            document.add(new Paragraph("\nPatient Information").setFont(bold).setFontSize(12));
            Table patientTable = new Table(UnitValue.createPercentArray(new float[]{2, 3}));
            patientTable.setWidth(UnitValue.createPercentValue(100));

            addRow(patientTable, "Patient Name:", safeString(dto.getPatientName()), bold, normal);
            addRow(patientTable, "Patient ID:", safeString(dto.getPatientId()), bold, normal);
            addRow(patientTable, "Gender:", safeString(dto.getGender()), bold, normal);
            addRow(patientTable, "Mobile:", safeString(dto.getMobileNumber()), bold, normal);
            addRow(patientTable, "IPD No:", safeString(dto.getIpdNo()), bold, normal);
            document.add(patientTable);

            // 💰 Bill Summary Section
            document.add(new Paragraph("\nBill Summary").setFont(bold).setFontSize(12));
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{2, 3}));
            summaryTable.setWidth(UnitValue.createPercentValue(100));

            addRow(summaryTable, "Total Bill Amount:", safeAmount(dto.getTotalBillAmount()), bold, normal);
            addRow(summaryTable, "Total Surgery Charges:", safeAmount(dto.getTotalSurgeryCharges()), bold, normal);
            addRow(summaryTable, "Advance Amount:", safeAmount(dto.getTotalAdvanceAmount()), bold, normal);
            addRow(summaryTable, "Total Paid Amount:", safeAmount(dto.getTotalPaidAmount()), bold, normal);
            addRow(summaryTable, "Total Discount:", safeAmount(dto.getTotalDiscountAmount()), bold, normal);
            addRow(summaryTable, "Refund Amount:", safeAmount(dto.getTotalRefundAmount()), bold, normal);
            addRow(summaryTable, "Net Payable:", safeAmount(dto.getNetPayableAmount()), bold, normal);
            addRow(summaryTable, "Balance Amount:", safeAmount(dto.getBalanceAmount()), bold, normal);

            document.add(summaryTable);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error while generating Final Bill Summary PDF", e);
        }
    }

    private void addRow(Table table, String label, String value, PdfFont bold, PdfFont normal) {
        table.addCell(createCell(label, bold, true));
        table.addCell(createCell(value, normal, false));
    }

    private Cell createCell(String text, PdfFont font, boolean isHeader) {
        Cell cell = new Cell()
                .add(new Paragraph(text != null ? text : "-").setFont(font).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
        if (isHeader) {
            cell.setFontColor(ColorConstants.BLACK);
        }
        return cell;
    }

    private String safeString(String s) {
        return s != null ? s : "-";
    }

    private String safeAmount(BigDecimal b) {
        return b != null ? b.toPlainString() : "-";
    }

    public static byte[] generateFinalBillPdf(IPDFinalBillFullResDTO bill) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.setFont(PdfFontFactory.createFont());
        document.add(new Paragraph("IPD FINAL BILL SUMMARY")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15));

        // ========== PATIENT DETAILS ==========
        document.add(sectionTitle("Patient Details"));
        Table patientTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        patientTable.setWidth(UnitValue.createPercentValue(100));
        addRow(patientTable, "Patient ID", bill.getPId());
        addRow(patientTable, "Patient Name", bill.getPatientName());
        addRow(patientTable, "Gender", bill.getGender());
        addRow(patientTable, "Age", bill.getAge());
        addRow(patientTable, "Mobile No", bill.getMobileNumber());
        addRow(patientTable, "IPD No", bill.getIpdNo());
        document.add(patientTable);

        // ========== ADMISSION DETAILS ==========
        document.add(sectionTitle("Admission Details"));
        Table admissionTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        admissionTable.setWidth(UnitValue.createPercentValue(100));
        addRow(admissionTable, "Admission ID", bill.getAdmissionId());
        addRow(admissionTable, "Consultant", bill.getConsultantName());
        addRow(admissionTable, "Second Consultant", bill.getSecondConsultantName());
        addRow(admissionTable, "Admission Date", formatDate(bill.getAdmissionDate()));
        addRow(admissionTable, "Ward", bill.getWardName());
        addRow(admissionTable, "Bed", bill.getBedName());
        document.add(admissionTable);

        // ========== FINAL BILL DETAILS ==========
        document.add(sectionTitle("Final Bill Summary"));
        Table billTable = new Table(UnitValue.createPercentArray(new float[]{2, 2}));
        billTable.setWidth(UnitValue.createPercentValue(100));
        addRow(billTable, "Final Bill No", bill.getFinalBillNo());
        addRow(billTable, "Final Bill Date", formatDate(bill.getFinalBillDate()));
        addRow(billTable, "Total Bill Amount", formatCurrency(bill.getTotalBillAmount()));
        addRow(billTable, "Total Surgery Charges", formatCurrency(bill.getTotalSurgeryCharges()));
        addRow(billTable, "Total Advance Amount", formatCurrency(bill.getTotalAdvanceAmount()));
        addRow(billTable, "Total Paid Amount", formatCurrency(bill.getTotalPaidAmount()));
        addRow(billTable, "Total Discount Amount", formatCurrency(bill.getTotalDiscountAmount()));
        addRow(billTable, "Total Refund Amount", formatCurrency(bill.getTotalRefundAmount()));
        addRow(billTable, "Net Payable Amount", formatCurrency(bill.getNetPayableAmount()));
        addRow(billTable, "Balance Amount", formatCurrency(bill.getBalanceAmount()));
        addRow(billTable, "Refund Balance", formatCurrency(bill.getRefundBalance()));
        addRow(billTable, "Settled", String.valueOf(bill.getHasSettled()));
        addRow(billTable, "Settled Date", bill.getSettledDate() != null ? bill.getSettledDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "-");
        addRow(billTable, "Remarks", bill.getRemarks());
        document.add(billTable);

        // ========== BILLING SUMMARY ==========
        if (bill.getBillingSummary() != null && !bill.getBillingSummary().isEmpty()) {
            document.add(sectionTitle("Expense Summary"));
            for (IPDBillingDTO billing : bill.getBillingSummary()) {
                document.add(new Paragraph("Billing No: " + billing.getBillingNo())
                        .setBold().setFontSize(12).setMarginTop(5));

                Table itemTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 1, 1}));
                itemTable.setWidth(UnitValue.createPercentValue(100));
                addTableHeader(itemTable, "Item Name", "Qty", "Rate", "Discount", "Amount");

                if (billing.getBillingItems() != null) {
                    for (IPDBillingItemDTO item : billing.getBillingItems()) {
                        itemTable.addCell(item.getBillingItemName());
                        itemTable.addCell(String.valueOf(item.getQuantity()));
                        itemTable.addCell(formatCurrency(item.getRate()));
                        itemTable.addCell(formatCurrency(item.getDiscountAmount()));
                        itemTable.addCell(formatCurrency(item.getAmount()));
                    }
                }
                document.add(itemTable);
            }
        }

        // ========== SURGERY DETAILS ==========
        if (bill.getSurgeries() != null && !bill.getSurgeries().isEmpty()) {
            document.add(sectionTitle("Surgery Summary"));
            for (IPDSurgeryBillingDTO surgery : bill.getSurgeries()) {
                document.add(new Paragraph("Surgery ID: " + surgery.getSurgeryId())
                        .setBold().setFontSize(12).setMarginTop(5));
                Table surgeryTable = new Table(UnitValue.createPercentArray(new float[]{2, 2}));
                surgeryTable.setWidth(UnitValue.createPercentValue(100));
                addRow(surgeryTable, "Billing Date", surgery.getBillingDateTime() != null ? surgery.getBillingDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) : "-");
                addRow(surgeryTable, "Total Surgery Charges", formatCurrency(surgery.getTotalSurgeryAmount()));
                addRow(surgeryTable, "Paid Amount", formatCurrency(surgery.getPaidAmount()));
                addRow(surgeryTable, "Balance Amount", formatCurrency(surgery.getBalanceAmount()));
                document.add(surgeryTable);
            }
        }

        document.add(new Paragraph("\nGenerated by Hosply360 System")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontColor(ColorConstants.GRAY));

        document.close();
        return baos.toByteArray();
    }

    // ---------- Helper Methods ----------
    private static Paragraph sectionTitle(String title) {
        return new Paragraph(title)
                .setFontSize(14)
                .setBold()
                .setMarginTop(10)
                .setUnderline();
    }

    private static void addRow(Table table, String key, String value) {
        table.addCell(new Cell().add(new Paragraph(key).setBold()));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "-")));
    }

    private static void addTableHeader(Table table, String... headers) {
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }
    }

    private static String formatCurrency(Object value) {
        if (value == null) return "-";
        return String.format("₹ %.2f", ((Number) value).doubleValue());
    }

    private static String formatDate(LocalDate date) {
        return date != null ? DATE_FORMAT.format(date) : "-";
    }
}
