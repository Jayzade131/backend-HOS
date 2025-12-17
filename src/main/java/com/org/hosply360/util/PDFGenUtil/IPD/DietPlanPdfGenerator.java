package com.org.hosply360.util.PDFGenUtil.IPD;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.org.hosply360.dto.IPDDTO.DietPlanPdfDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DietPlanPdfGenerator {

    private static final Color PRIMARY_COLOR = new DeviceRgb(0, 51, 153);
    private static final Color BORDER_COLOR = new DeviceRgb(180, 180, 180);

    public byte[] generateDietPlanPdf(List<DietPlanPdfDTO> dtoList) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(20, 20, 50, 20);

            addHeader(document);
            addPatientInfo(document, dtoList.get(0));
            addDietPlanTable(document, dtoList);
            addSignatureSection(document);
            addBottomFooter(document);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Diet Plan PDF", e);
        }
    }

    private void addHeader(Document document) throws IOException {
        Table header = new Table(UnitValue.createPercentArray(new float[]{2f, 6f, 3f}));
        header.setWidth(UnitValue.createPercentValue(100));
        header.setBorder(new SolidBorder(PRIMARY_COLOR, 1));

        Image logo = getLogoImage();
        logo.setWidth(90).setHeight(90);

        Cell logoCell = new Cell().add(logo).setBorder(null).setVerticalAlignment(VerticalAlignment.MIDDLE);

        Div titleDiv = new Div()
                .setBackgroundColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8)
                .add(new Paragraph("DIET PLAN").setBold().setFontColor(ColorConstants.WHITE).setFontSize(14));

        Cell titleCell = new Cell().add(titleDiv).setBorder(null);

        Paragraph hospitalName = new Paragraph("DEMO HOSPITAL")
                .setFontSize(16)
                .setBold()
                .setFontColor(PRIMARY_COLOR);
        Paragraph address = new Paragraph("Raipur, (C.G) 492007")
                .setFontSize(9)
                .setFontColor(ColorConstants.DARK_GRAY);

        Cell infoCell = new Cell()
                .add(hospitalName)
                .add(address)
                .setBorder(null)
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        header.addCell(logoCell);
        header.addCell(titleCell);
        header.addCell(infoCell);
        document.add(header);
        document.add(new Paragraph("\n"));
    }

    private void addPatientInfo(Document document, DietPlanPdfDTO dto) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1.5f, 3f, 1.5f, 3f}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setBorder(new SolidBorder(BORDER_COLOR, 1));

        addRow(table, "MRD No.", dto.getMrdNo(), "Adm. Date", dto.getAdmDate());
        addRow(table, "IPD No.", dto.getIpdNo(), "Consultant", dto.getConsultant());
        addRow(table, "Patient Name", dto.getPatientName(), "Referred By", dto.getReferredBy());
        addRow(table, "Age/Gender", dto.getAgeGender(), "Address", dto.getAddress());
        addRow(table, "Mobile No.", dto.getMobileNo(), "Remark", dto.getRemark());

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addRow(Table table, String label1, String val1, String label2, String val2) {
        table.addCell(createCell(label1, true));
        table.addCell(createCell(val1, false));
        table.addCell(createCell(label2, true));
        table.addCell(createCell(val2, false));
    }

    private Cell createCell(String text, boolean isLabel) {
        Paragraph p = new Paragraph(text != null ? text : "-")
                .setFontSize(9)
                .setBold()
                .setFontColor(isLabel ? PRIMARY_COLOR : ColorConstants.BLACK);

        return new Cell().add(p)
                .setBorderBottom(new SolidBorder(BORDER_COLOR, 0.5f))
                .setBorderLeft(null)
                .setBorderRight(null)
                .setBorderTop(null)
                .setPadding(4);
    }

    private void addDietPlanTable(Document document, List<DietPlanPdfDTO> dtoList) {
        Paragraph sectionTitle = new Paragraph("Diet Details")
                .setFontColor(PRIMARY_COLOR)
                .setBold()
                .setFontSize(11)
                .setMarginBottom(6);
        document.add(sectionTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1f, 2f, 2f, 2f, 2f}));
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"S.No.", "Date", "Diet Time", "Diet", "Remark"};
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(PRIMARY_COLOR)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(5));
        }

        int i = 1;
        for (DietPlanPdfDTO dto : dtoList) {
            table.addCell(String.valueOf(i++));
            table.addCell(dto.getDate());
            table.addCell(dto.getDietTime());
            table.addCell(dto.getDiet());
            table.addCell(dto.getDietRemark());
        }

        document.add(table);
        document.add(new Paragraph("\n\n"));
    }

    private void addSignatureSection(Document document) {
        Table footer = new Table(UnitValue.createPercentArray(new float[]{1f, 1f}));
        footer.setWidth(UnitValue.createPercentValue(100));
        footer.setMarginTop(20);

        Cell left = new Cell().add(new Paragraph("Dietician / Nurse Signature")
                        .setBold().setFontSize(9))
                .setTextAlignment(TextAlignment.LEFT)
                .setBorder(null);

        Cell right = new Cell().add(new Paragraph("Authorised Signatory")
                        .setBold().setFontSize(9))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(null);

        footer.addCell(left);
        footer.addCell(right);
        document.add(footer);
    }

    private void addBottomFooter(Document document) {
        Paragraph footerText = new Paragraph(
                "PRINT DATE & TIME : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")))
                .setFontSize(8)
                .setTextAlignment(TextAlignment.LEFT)
                .setFixedPosition(40, 20, 520);
        document.add(footerText);
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
            byte[] blankPixel = new byte[]{
                    (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                    0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                    0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15,
                    0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54,
                    0x78, 0x63, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00,
                    0x01, 0x0D, 0x0A, 0x2D, 0x00, 0x00, 0x00, 0x00,
                    0x49, 0x45, 0x4E, 0x44, 0x42, 0x60
            };
            ImageData imageData = ImageDataFactory.create(blankPixel);
            Image placeholder = new Image(imageData);
            placeholder.setWidth(40);
            placeholder.setHeight(40);
            return placeholder;
        }
    }
}
