package com.org.hosply360.util.PDFGenUtil.IPD;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.org.hosply360.dao.IPD.IPDBilling;
import com.org.hosply360.dao.IPD.IPDBillingItem;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.util.Others.AmountToWordsUtil;
import com.org.hosply360.util.Others.DuplicateWatermarkUtil;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class IPDBillingPdfGenerator {

    public byte[] generateIPDBill(IPDBilling billing) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            document.setMargins(20, 20, 30, 20);

            // ========== HEADER ==========
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 4}))
                    .useAllAvailableWidth();

            try {
                Image logo = new Image(ImageDataFactory.create("src/main/resources/static/images/logo.png"))
                        .setWidth(60)
                        .setHeight(60);
                headerTable.addCell(new Cell().add(logo)
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.LEFT));
            } catch (Exception e) {
                headerTable.addCell(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
            }

            Paragraph title = new Paragraph("IPD Billing")
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER);

            headerTable.addCell(new Cell()
                    .add(title)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorder(Border.NO_BORDER));
            document.add(headerTable);

            document.add(new LineSeparator(new SolidLine()));
            document.add(new Paragraph(" "));

            // ========== BILLING INFO SECTION ==========
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{2, 3, 2, 3}))
                    .useAllAvailableWidth();

            infoTable.addCell(infoCell("Billing No:")).addCell(infoCell(billing.getBillingNo()));
            infoTable.addCell(infoCell("Billing Date:"))
                    .addCell(infoCell(billing.getBillingDateTime() != null
                            ? billing.getBillingDateTime().toLocalDate().toString() : "-"));

            IPDAdmission admission = billing.getAdmissionId();
            if (admission != null && admission.getPatient() != null) {
                var patient = admission.getPatient();
                String fullName = patient.getPatientPersonalInformation().getFirstName() + " " +
                        patient.getPatientPersonalInformation().getLastName();

                infoTable.addCell(infoCell("Patient ID:")).addCell(infoCell(patient.getPId()));
                infoTable.addCell(infoCell("Patient Name:")).addCell(infoCell(fullName));
                infoTable.addCell(infoCell("Sex:"))
                        .addCell(infoCell(patient.getPatientPersonalInformation().getGender()));

                try {
                    LocalDate dob = LocalDate.parse(
                            patient.getPatientPersonalInformation().getDateOfBirth(),
                            DateTimeFormatter.ISO_LOCAL_DATE);
                    Period age = Period.between(dob, LocalDate.now());
                    infoTable.addCell(infoCell("Age:"))
                            .addCell(infoCell(String.format("%dy %dm", age.getYears(), age.getMonths())));
                } catch (Exception e) {
                    infoTable.addCell(infoCell("Age:")).addCell(infoCell("-"));
                }

                infoTable.addCell(infoCell("Mobile No:"))
                        .addCell(infoCell(patient.getPatientContactInformation().getPrimaryPhone()));
            }

            infoTable.addCell(infoCell("Remarks:"))
                    .addCell(infoCell(billing.getRemarks() != null ? billing.getRemarks() : "-"));

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // ========== BILLING ITEMS ==========
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 3, 1, 1, 2, 2}))
                    .useAllAvailableWidth();

            table.addHeaderCell(createHeaderCell("Sr."))
                    .addHeaderCell(createHeaderCell("Item Group"))
                    .addHeaderCell(createHeaderCell("Item Name"))
                    .addHeaderCell(createHeaderCell("Qty"))
                    .addHeaderCell(createHeaderCell("Rate"))
                    .addHeaderCell(createHeaderCell("Discount"))
                    .addHeaderCell(createHeaderCell("Amount (Rs)"));

            List<IPDBillingItem> items = billing.getBillingItems();
            if (items != null && !items.isEmpty()) {
                for (int i = 0; i < items.size(); i++) {
                    IPDBillingItem item = items.get(i);
                    table.addCell(normalCell(String.valueOf(i + 1)));
                    table.addCell(normalCell(item.getBillingItemGroup() != null
                            ? item.getBillingItemGroup().getItemGroupName() : "-"));
                    table.addCell(normalCell(item.getBillingItem() != null
                            ? item.getBillingItem().getItemName() : "-"));
                    table.addCell(normalCell(item.getQuantity() != null ? String.valueOf(item.getQuantity()) : "-"));
                    table.addCell(normalCell(String.format("\u20B9%.2f", item.getRate() != null ? item.getRate() : 0.0)));
                    table.addCell(normalCell(item.getDiscountAmount() != null
                            ? String.format("\u20B9%.2f (%s%%)", item.getDiscountAmount(), item.getDiscountPercent())
                            : "0.00 (0%)"));
                    table.addCell(normalCell(String.format("\u20B9%.2f", item.getAmount() != null ? item.getAmount() : 0.0)));
                }
            } else {
                table.addCell(new Cell(1, 7)
                        .add(new Paragraph("No billing items found"))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBorder(Border.NO_BORDER));
            }

            document.add(table);
            document.add(new Paragraph(" "));

            // ========== SUMMARY ==========
            Table summary = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                    .setWidth(UnitValue.createPercentValue(60))
                    .setHorizontalAlignment(HorizontalAlignment.RIGHT);

            summary.addCell(summaryLabel("Total Amount"))
                    .addCell(summaryValue(billing.getTotalAmount()));
            summary.addCell(summaryLabel("Discount"))
                    .addCell(summaryValue(billing.getDiscountAmount()));
            summary.addCell(summaryLabel("Net Amount"))
                    .addCell(summaryValue(billing.getNetAmount()));
            summary.addCell(summaryLabel("Paid Amount"))
                    .addCell(summaryValue(billing.getPaidAmount()));
            summary.addCell(summaryLabel("Balance Amount"))
                    .addCell(summaryValue(billing.getBalanceAmount()));
            summary.addCell(summaryLabel("Refund Amount"))
                    .addCell(summaryValue(billing.getRefundAmount()));

            String amountInWords = AmountToWordsUtil.convertToWords(
                    billing.getNetAmount() != null ? billing.getNetAmount().doubleValue() : 0.0);
            summary.addCell(summaryLabel("Amount in Words:"))
                    .addCell(new Cell().add(new Paragraph(amountInWords))
                            .setBorder(Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.RIGHT));

            summary.addCell(summaryLabel("Settled"))
                    .addCell(new Cell().add(new Paragraph(Boolean.TRUE.equals(billing.getHasSettled()) ? "Yes" : "No"))
                            .setBorder(Border.NO_BORDER)
                            .setTextAlignment(TextAlignment.RIGHT));

            document.add(summary);

            document.add(new Paragraph(" "));
            document.add(new LineSeparator(new SolidLine()));

            // ========== WATERMARK / FOOTER ==========
            if (Boolean.TRUE.equals(billing.getCanceled())) {
                pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new DuplicateWatermarkUtil.DuplicateWatermarkHandler());
            }

            Paragraph footer = new Paragraph("Authorized Signature")
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(20);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    // ---------- Utility Cell Methods ----------
    private Cell infoCell(String text) {
        return new Cell().add(new Paragraph(text)).setBorder(Border.NO_BORDER);
    }

    private Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text).setBold().setFontSize(11))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell normalCell(String text) {
        return new Cell().add(new Paragraph(text)).setTextAlignment(TextAlignment.CENTER);
    }

    private Cell summaryLabel(String label) {
        return new Cell().add(new Paragraph(label)).setBorder(Border.NO_BORDER);
    }

    private Cell summaryValue(BigDecimal value) {
        double val = value != null ? value.doubleValue() : 0.0;
        return new Cell().add(new Paragraph(String.format("\u20B9%.2f", val)))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
    }
}

