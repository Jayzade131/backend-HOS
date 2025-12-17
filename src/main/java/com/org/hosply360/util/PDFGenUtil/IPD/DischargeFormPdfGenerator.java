package com.org.hosply360.util.PDFGenUtil.IPD;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
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
import com.org.hosply360.dto.IPDDTO.DischargeFormPdfDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DischargeFormPdfGenerator {

    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(0, 51, 153);
    private static final DeviceRgb BORDER_COLOR = new DeviceRgb(180, 180, 180);

    public byte[] generateDischargeFormPdf(DischargeFormPdfDTO dto) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(20, 20, 40, 20);
            addHeader(document);
            addPatientDetails(document, dto);
            addDischargeSections(document, dto);
            addSignatureSection(document, dto);
            addFooter(document);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Discharge Form PDF", e);
        }
    }

    private void addHeader(Document document) throws IOException {
        Table header = new Table(UnitValue.createPercentArray(new float[]{2f, 6f, 3f}));
        header.setWidth(UnitValue.createPercentValue(100));
        header.setKeepTogether(false);
        Image logo = getLogoImage();
        logo.setWidth(70).setHeight(70);
        header.addCell(new Cell().add(logo).setBorder(null));
        Cell title = new Cell().add(new Paragraph("DISCHARGE SUMMARY")
                        .setBold()
                        .setFontColor(ColorConstants.WHITE)
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(PRIMARY_COLOR)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(null);
        header.addCell(title);
        Paragraph hospital = new Paragraph("DEMO HOSPITAL\nRaipur, (C.G.) 492007\nPhone: 0771-3569770")
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(9)
                .setFontColor(ColorConstants.DARK_GRAY);
        header.addCell(new Cell().add(hospital).setBorder(null));
        document.add(header);
        document.add(new Paragraph("\n"));
    }

    private void addPatientDetails(Document document, DischargeFormPdfDTO dto) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2f, 3f, 2f, 3f}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setBorder(new SolidBorder(BORDER_COLOR, 1));
        table.setKeepTogether(false);
        addRow(table, "IPD No", dto.getIpdNo(), "M.R. No", dto.getMrdNo());
        addRow(table, "Patient Name", dto.getPatientName(), "Admission Date", dto.getAdmissionDate());
        addRow(table, "Father/Husband", dto.getFatherName(), "Discharge Date", dto.getDischargeDate());
        addRow(table, "Address", dto.getAddress(), "Consultant Name", dto.getPrimaryConsultant());
        addRow(table, "Age/Gender", dto.getAgeGender(), "Sec. Consultant", dto.getSecondaryConsultant());
        addRow(table, "Type", dto.getType(), "Third Consultant", dto.getThirdConsultant());
        addRow(table, "Remark", dto.getRemark(), "", "");
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
        return new Cell().add(new Paragraph(text == null ? "-" : text)
                        .setFontSize(9)
                        .setBold()
                        .setFontColor(isLabel ? PRIMARY_COLOR : ColorConstants.BLACK))
                .setBorder(null)
                .setBorderBottom(new SolidBorder(BORDER_COLOR, 0.5f))
                .setPadding(4);
    }

    private void addDischargeSections(Document document, DischargeFormPdfDTO dto) {
        addSection(document, "DEPARTMENT", dto.getDepartment());
        addSection(document, "DIAGNOSIS", dto.getDiagnosis());
        addSection(document, "COMPLAINTS ON ADMISSION", dto.getComplaints());
        addSection(document, "INVESTIGATIONS", dto.getInvestigations());
        addSection(document, "TREATMENT", dto.getTreatment());
        addSection(document, "INDICATION", dto.getIndication());
        addSection(document, "HISTORY", dto.getHistory());
        addSection(document, "EXAMINATION", dto.getExamination());
        addSection(document, "Rx", dto.getRx());
        addSection(document, "Review", dto.getReview());
        document.add(new Paragraph("\n"));
    }

    private void addSection(Document document, String title, String value) {
        document.add(new Paragraph(title + " :-")
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setFontSize(10)
                .setKeepTogether(false));
        document.add(new Paragraph(value == null ? "" : value)
                .setFontSize(9)
                .setMarginBottom(5)
                .setKeepTogether(false));
    }

    private void addSignatureSection(Document document, DischargeFormPdfDTO dto) {
        Paragraph p = new Paragraph("Signature of the Patient / Attendant : ________________________\nName : ________________________")
                .setFontSize(9)
                .setMarginTop(10)
                .setKeepTogether(false);
        document.add(p);
        document.add(new Paragraph("अगली बार आने से पहले फोन द्वारा दिखाने का समय लेकर ही आये.\nफोन: 0771-3569770, 3501700, +91-8717870920")
                .setFontSize(8)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setMarginTop(10)
                .setKeepTogether(false));

        document.add(new Paragraph("-------------------------------------------------------------------------------------------------------------------------------------")
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setKeepTogether(false));
        Table table = new Table(UnitValue.createPercentArray(new float[]{1f, 1f, 1f}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setKeepTogether(false);
        table.addCell(createSignatureCell(dto.getPrimaryConsultant(), "Consultant"));
        table.addCell(createSignatureCell(dto.getSecondaryConsultant(), "Second Consultant"));
        table.addCell(createSignatureCell(dto.getThirdConsultant(), "Third Consultant"));
        document.add(table);
    }

    private Cell createSignatureCell(String name, String label) {
        return new Cell().add(new Paragraph(label + "\n" + (name != null ? name : "-"))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(9)
                        .setBold())
                .setBorder(null);
    }

    private void addFooter(Document document) {
        Paragraph footerText = new Paragraph(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm a")))
                .setFontSize(8)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(10)
                .setKeepTogether(false);
        document.add(footerText);
    }

    private Image getLogoImage() throws IOException {
        try {
            ClassPathResource logoResource = new ClassPathResource("static/images/hospital_logo.png");
            ImageData imageData = ImageDataFactory.create(logoResource.getURL());
            return new Image(imageData);
        } catch (Exception e) {
            byte[] blankPixel = new byte[]{
                    (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
            };
            return new Image(ImageDataFactory.create(blankPixel));
        }
    }
}
