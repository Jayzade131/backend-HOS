package com.org.hosply360.util.PDFGenUtil;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.org.hosply360.dto.IPDDTO.TransferReceiptDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TransferReceiptPdfGenerator {

    private static final Color PRIMARY_COLOR = new DeviceRgb(0, 102, 204); // Professional blue
    private static final Color SECONDARY_COLOR = new DeviceRgb(240, 245, 255); // Light blue background
    private static final Color ACCENT_COLOR = new DeviceRgb(0, 82, 164); // Darker blue
    private static final Color TEXT_COLOR = new DeviceRgb(51, 51, 51); // Dark gray for text

    public byte[] generateTransferReceiptPDF(TransferReceiptDto dto) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(20, 20, 30, 20);

            addCompactHeader(document);

            addCompactContent(document, dto);

            addCompactSignatureSection(document);

            addCompactFooter(document);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating Transfer Receipt PDF", e);
        }
    }

    private void addCompactHeader(Document document) throws IOException {
        Div headerContainer = new Div()
                .setBackgroundColor(SECONDARY_COLOR)
                .setPadding(12)
                .setMarginBottom(12)
                .setBorder(new SolidBorder(PRIMARY_COLOR, 1));

        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{0.8f, 3f, 1f}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        Image logo = getLogoImage();
        logo.setWidth(60);
        logo.setHeight(60);
        Cell logoCell = new Cell()
                .add(logo)
                .setBorder(null)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        Paragraph hospitalInfo = new Paragraph()
                .add(new Paragraph("HOSPLY360 HOSPITAL")
                        .setBold()
                        .setFontSize(14)
                        .setFontColor(PRIMARY_COLOR)
                        .setMarginBottom(1))
                .add(new Paragraph("Multi-Specialty Healthcare Center")
                        .setFontSize(9)
                        .setFontColor(TEXT_COLOR)
                        .setMarginBottom(2))
                .add(new Paragraph("123 Health Street, Medical District")
                        .setFontSize(8)
                        .setFontColor(ColorConstants.DARK_GRAY))
                .add(new Paragraph("Phone: +91-9999999999 | Email: info@hosply360.com")
                        .setFontSize(8)
                        .setFontColor(ColorConstants.DARK_GRAY));

        Cell infoCell = new Cell()
                .add(hospitalInfo)
                .setBorder(null)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setTextAlignment(TextAlignment.CENTER);

        Div documentBadge = new Div()
                .setBackgroundColor(PRIMARY_COLOR)
                .setPadding(6)
                .setMargin(3)
                .setTextAlignment(TextAlignment.CENTER)
                .add(new Paragraph("TRANSFER RECEIPT")
                        .setBold()
                        .setFontColor(ColorConstants.WHITE)
                        .setFontSize(10));

        Cell badgeCell = new Cell()
                .add(documentBadge)
                .setBorder(null)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        headerTable.addCell(logoCell);
        headerTable.addCell(infoCell);
        headerTable.addCell(badgeCell);

        headerContainer.add(headerTable);
        document.add(headerContainer);
    }

    private void addCompactContent(Document document, TransferReceiptDto dto) {
        Paragraph title = new Paragraph("INPATIENT TRANSFER ACKNOWLEDGEMENT")
                .setFontSize(12)
                .setBold()
                .setFontColor(ACCENT_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(12)
                .setMarginTop(5);
        document.add(title);

        addCombinedTable(document, dto);
    }

    private void addCombinedTable(Document document, TransferReceiptDto dto) {
        Table mainTable = new Table(UnitValue.createPercentArray(new float[]{100f}));
        mainTable.setWidth(UnitValue.createPercentValue(100));
        mainTable.setMarginBottom(10);

        Cell patientCell = new Cell()
                .setBackgroundColor(new DeviceRgb(250, 250, 250))
                .setPadding(10)
                .setBorder(new SolidBorder(new DeviceRgb(200, 200, 200), 1));

        Paragraph patientHeader = new Paragraph("PATIENT INFORMATION")
                .setBold()
                .setFontSize(11)
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(8);

        Table patientTable = new Table(UnitValue.createPercentArray(new float[]{25f, 35f, 20f, 20f}));
        patientTable.setWidth(UnitValue.createPercentValue(100));
        patientTable.setMarginBottom(5);

        addUniformPatientCell(patientTable, "Patient Name", true);
        addUniformPatientCell(patientTable, dto.getPatientName(), false);
        addUniformPatientCell(patientTable, "Gender/Age", true);
        addUniformPatientCell(patientTable, dto.getGender() + " / " + dto.getAge(), false);

        addUniformPatientCell(patientTable, "Mobile Number", true);
        addUniformPatientCell(patientTable, dto.getMobileNumber(), false);
        addUniformPatientCell(patientTable, "Admission No.", true);
        addUniformPatientCell(patientTable, dto.getAdmissionNumber(), false);

        addUniformPatientCell(patientTable, "Consultant", true);
        addUniformPatientCell(patientTable, dto.getConsultant(), false);
        addUniformPatientCell(patientTable, "Admission Date", true);
        addUniformPatientCell(patientTable, dto.getAdmissionDate(), false);

        patientCell.add(patientHeader);
        patientCell.add(patientTable);
        mainTable.addCell(patientCell);

        Cell transferCell = new Cell()
                .setPadding(5)
                .setBorder(null);

        Paragraph transferHeader = new Paragraph("TRANSFER DETAILS")
                .setBold()
                .setFontSize(11)
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(8);

        Table transferTable = new Table(UnitValue.createPercentArray(new float[]{40f, 60f}));
        transferTable.setWidth(UnitValue.createPercentValue(100));

        Cell headerCell1 = new Cell().add(new Paragraph("FIELD").setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(PRIMARY_COLOR)
                .setPadding(8)
                .setTextAlignment(TextAlignment.LEFT);
        Cell headerCell2 = new Cell().add(new Paragraph("DETAILS").setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(PRIMARY_COLOR)
                .setPadding(8)
                .setTextAlignment(TextAlignment.LEFT);
        transferTable.addHeaderCell(headerCell1);
        transferTable.addHeaderCell(headerCell2);

        addCompactTableRow(transferTable, "Transfer Date & Time", dto.getTransferDateTime(), true);
        addCompactTableRow(transferTable, "From Ward / Bed", dto.getFromWard() + " / " + dto.getFromBed(), false);
        addCompactTableRow(transferTable, "To Ward / Bed", dto.getToWard() + " / " + dto.getToBed(), true);
        addCompactTableRow(transferTable, "Transfer Remarks", dto.getRemark(), false);
        addCompactTableRow(transferTable, "Processed By", dto.getCreatedBy(), true);

        transferCell.add(transferHeader);
        transferCell.add(transferTable);
        mainTable.addCell(transferCell);

        document.add(mainTable);
    }

    private void addUniformPatientCell(Table table, String text, boolean isLabel) {
        String displayText = text != null ? text : "-";

        Cell cell = new Cell()
                .setPadding(6)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setTextAlignment(TextAlignment.LEFT);

        if (isLabel) {
            cell.add(new Paragraph(displayText).setBold().setFontSize(9));
        } else {
            cell.add(new Paragraph(displayText).setFontSize(9));
        }

        table.addCell(cell);
    }

    private void addCompactTableRow(Table table, String key, String value, boolean isEven) {
        Color bgColor = isEven ? new DeviceRgb(250, 250, 250) : ColorConstants.WHITE;
        String displayValue = value != null ? value : "Not specified";

        Cell keyCell = new Cell().add(new Paragraph(key).setBold().setFontSize(9))
                .setBackgroundColor(bgColor)
                .setPadding(8)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));

        Cell valCell = new Cell().add(new Paragraph(displayValue).setFontSize(9))
                .setBackgroundColor(bgColor)
                .setPadding(8)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));

        table.addCell(keyCell);
        table.addCell(valCell);
    }

    private void addCompactSignatureSection(Document document) {
        Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1f, 1f}));
        signatureTable.setWidth(UnitValue.createPercentValue(100));
        signatureTable.setMarginTop(15);

        Div authSection = new Div()
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        authSection.add(new Paragraph("Authorized Signature")
                .setBold()
                .setFontSize(9)
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10));
        authSection.add(new LineSeparator(new DottedLine(1)).setMarginBottom(8));
        authSection.add(new Paragraph("Relieving Ward Incharge")
                .setFontSize(8)
                .setFontColor(ColorConstants.DARK_GRAY));

        Div receiveSection = new Div()
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        receiveSection.add(new Paragraph("Authorized Signature")
                .setBold()
                .setFontSize(9)
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10));
        receiveSection.add(new LineSeparator(new DottedLine(1)).setMarginBottom(8));
        receiveSection.add(new Paragraph("Receiving Ward Incharge")
                .setFontSize(8)
                .setFontColor(ColorConstants.DARK_GRAY));

        Cell leftCell = new Cell().add(authSection).setBorder(null);
        Cell rightCell = new Cell().add(receiveSection).setBorder(null);

        signatureTable.addCell(leftCell);
        signatureTable.addCell(rightCell);

        document.add(signatureTable);
    }

    private void addCompactFooter(Document document) {
        Div footerContainer = new Div()
                .setBackgroundColor(SECONDARY_COLOR)
                .setPadding(10)
                .setMarginTop(15)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderTop(new SolidBorder(PRIMARY_COLOR, 1));

        Paragraph footerText = new Paragraph()
                .add(new Paragraph("HOSPLY360 Hospital - Committed to Quality Healthcare")
                        .setFontSize(8)
                        .setFontColor(PRIMARY_COLOR)
                        .setMarginBottom(2))
                .add(new Paragraph("Generated on: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm")))
                        .setFontSize(7)
                        .setFontColor(ColorConstants.DARK_GRAY))
                .add(new Paragraph("Electronically generated document")
                        .setItalic()
                        .setFontSize(7)
                        .setFontColor(ColorConstants.GRAY));

        footerContainer.add(footerText);
        document.add(footerContainer);
    }

    private Image getLogoImage() throws IOException {
        try {
            ClassPathResource logoResource = new ClassPathResource("static/images/hospital_logo.png");
            ImageData imageData = ImageDataFactory.create(logoResource.getURL());
            Image logo = new Image(imageData);
            logo.setWidth(40);
            logo.setHeight(40);
            return logo;
        } catch (Exception e) {
            byte[] blankPixel = new byte[] {
                    (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                    0x00,0x00,0x00,0x0D,0x49,0x48,0x44,0x52,0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x01,0x08,0x06,0x00,0x00,0x00,0x1F,0x15,
                    0x00,0x00,0x00,0x0A,0x49,0x44,0x41,0x54,0x78,0x63,0x00,0x01,0x00,0x00,0x05,0x00,0x01,0x0D,0x0A,0x2D,
                    0x00,0x00,0x00,0x00,0x49,0x45,0x4E,0x44,0x42,0x60
            };
            ImageData imageData = ImageDataFactory.create(blankPixel);
            Image placeholder = new Image(imageData);
            placeholder.setWidth(40);
            placeholder.setHeight(40);
            return placeholder;
        }
    }
}