package com.org.hosply360.util.PDFGenUtil.IPD;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.org.hosply360.dao.IPD.IPDAdmission;
import com.org.hosply360.util.Others.DuplicateWatermarkUtil;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ReceiptPdfGenerator {

    public byte[] generateReceiptPdf(
            String receiptNo,
            Object receiptDate,
            IPDAdmission admission,
            String receivedFrom,
            String fatherOrHusbandName,
            BigDecimal amount,
            String through,
            String remark,
            String otherRemark,
            String preparedBy,
            boolean isDuplicate
    ) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            if (isDuplicate) {
                pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new DuplicateWatermarkUtil.DuplicateWatermarkHandler());
            }
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(36, 36, 36, 36);
            PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            Table header = new Table(UnitValue.createPercentArray(new float[]{2, 3}))
                    .setWidth(UnitValue.createPercentValue(100));
            header.addCell(new Cell().add(new Paragraph("SARAL HMS")
                            .setFont(bold)
                            .setFontSize(14)
                            .setFontColor(ColorConstants.BLUE))
                    .setBorder(null));
            Cell hospitalCell = new Cell().setBorder(null);
            hospitalCell.add(new Paragraph("DEMO HOSPITAL").setFont(bold).setFontSize(12));
            hospitalCell.add(new Paragraph("Raipur, (C.G.) 492007").setFont(regular));
            hospitalCell.add(new Paragraph("Phone no.: -").setFont(regular));
            hospitalCell.setTextAlignment(TextAlignment.RIGHT);
            header.addCell(hospitalCell);
            document.add(header);
            document.add(new Paragraph("\n"));
            Paragraph title = new Paragraph("MONEY RECEIPT")
                    .setFont(bold)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(11);
            document.add(title);
            document.add(new Paragraph("\n"));
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{2, 3, 2, 3}))
                    .setWidth(UnitValue.createPercentValue(100));
            String patientId = admission.getPatient() != null ? admission.getPatient().getPId() : "N/A";
            String ipdNo = admission.getIpdNo() != null ? admission.getIpdNo() : "N/A";
            String formattedDate = formatReceiptDate(receiptDate);
            addRow(infoTable, "Patient ID", patientId, "Receipt No", receiptNo, bold, regular);
            addRow(infoTable, "IPD No.", ipdNo, "Receipt Date", formattedDate, bold, regular);
            document.add(infoTable);
            document.add(new Paragraph("\n"));
            Table body = new Table(UnitValue.createPercentArray(new float[]{3, 7}))
                    .setWidth(UnitValue.createPercentValue(100));
            addSingleRow(body, "Received With thanks from", receivedFrom, bold, regular);
            addSingleRow(body, "Father/Husband Name", fatherOrHusbandName, bold, regular);
            addSingleRow(body, "The Sum of amount",
                    "Rs. " + formatAmount(amount) + "   In Words : " + convertNumberToWords(amount.longValue()) + " Only", bold, regular);
            addSingleRow(body, "Amount for the receipt no.", receiptNo, bold, regular);
            addSingleRow(body, "Through", through, bold, regular);
            addSingleRow(body, "Remark", remark, bold, regular);
            addSingleRow(body, "Other Remark", otherRemark, bold, regular);
            document.add(body);
            document.add(new Paragraph("\n"));
            Table total = new Table(UnitValue.createPercentArray(new float[]{2, 8}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBorder(new SolidBorder(1));
            total.addCell(new Cell().add(new Paragraph("Rs. " + formatAmount(amount)))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(bold)
                    .setFontSize(10)
                    .setBorder(new SolidBorder(1)));
            total.addCell(new Cell().add(new Paragraph("In Words : " + convertNumberToWords(amount.longValue()) + " Only"))
                    .setFont(regular)
                    .setFontSize(10)
                    .setBorder(new SolidBorder(1)));
            document.add(total);
            document.add(new Paragraph("\n"));
            Table footer = new Table(UnitValue.createPercentArray(new float[]{5, 5}))
                    .setWidth(UnitValue.createPercentValue(100));
            footer.addCell(new Cell().add(new Paragraph("Prepared By : " + preparedBy))
                    .setFont(regular)
                    .setBorder(null));
            footer.addCell(new Cell().add(new Paragraph("Authorised Signatory")
                            .setTextAlignment(TextAlignment.RIGHT))
                    .setFont(regular)
                    .setBorder(null));
            document.add(footer);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Receipt PDF", e);
        }
    }

    private String formatReceiptDate(Object receiptDate) {
        if (receiptDate == null) return "N/A";
        try {
            if (receiptDate instanceof LocalDateTime) {
                return ((LocalDateTime) receiptDate).format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a"));
            } else if (receiptDate instanceof LocalDate) {
                return ((LocalDate) receiptDate).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            } else {
                return receiptDate.toString();
            }
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%.2f", amount);
    }

    private void addRow(Table table, String key1, String val1, String key2, String val2,
                        PdfFont bold, PdfFont regular) {
        table.addCell(new Cell().add(new Paragraph(key1).setFont(bold)).setBorder(null));
        table.addCell(new Cell().add(new Paragraph(val1 != null ? val1 : "").setFont(regular)).setBorder(null));
        table.addCell(new Cell().add(new Paragraph(key2).setFont(bold)).setBorder(null));
        table.addCell(new Cell().add(new Paragraph(val2 != null ? val2 : "").setFont(regular)).setBorder(null));
    }

    private void addSingleRow(Table table, String label, String value, PdfFont bold, PdfFont regular) {
        table.addCell(new Cell().add(new Paragraph(label).setFont(bold)).setBorder(null));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "").setFont(regular)).setBorder(null));
    }

    private static final String[] tensNames = {"", " Ten", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty", " Ninety"};
    private static final String[] numNames = {"", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine", " Ten", " Eleven", " Twelve",
            " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen", " Eighteen", " Nineteen"};

    private String convertNumberToWords(long number) {
        if (number == 0) return "Zero";
        StringBuilder words = new StringBuilder();
        if (number / 10000000 > 0) {
            words.append(convertNumberToWords(number / 10000000)).append(" Crore");
            number %= 10000000;
        }
        if (number / 100000 > 0) {
            words.append(convertNumberToWords(number / 100000)).append(" Lakh");
            number %= 100000;
        }
        if (number / 1000 > 0) {
            words.append(convertNumberToWords(number / 1000)).append(" Thousand");
            number %= 1000;
        }
        if (number / 100 > 0) {
            words.append(convertNumberToWords(number / 100)).append(" Hundred");
            number %= 100;
        }
        if (number > 0) {
            if (words.length() > 0) words.append(" and");
            if (number < 20) words.append(numNames[(int) number]);
            else {
                words.append(tensNames[(int) (number / 10)]);
                if ((number % 10) > 0) words.append(numNames[(int) (number % 10)]);
            }
        }
        return words.toString().trim();
    }
}
