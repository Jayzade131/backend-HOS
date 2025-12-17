package com.org.hosply360.util.PDFGenUtil;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.org.hosply360.dao.OPD.ReceiptNumberGenerator;
import com.org.hosply360.dao.globalMaster.Address;
import com.org.hosply360.dao.pathology.PackageTestReport;
import com.org.hosply360.dao.pathology.TestReportParameter;
import com.org.hosply360.dto.pathologyDTO.PackageTestReportItem;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class PackageTestReportPdfGenerator {

  private final ReceiptNumberGenerator receiptNumberGenerator;
    public PackageTestReportPdfGenerator(ReceiptNumberGenerator receiptNumberGenerator) {
        this.receiptNumberGenerator = receiptNumberGenerator;
    }
    public byte[] generatePackageTestReportPDF(PackageTestReport dto) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc,PageSize.A4);
            document.setMargins(36, 36, 36, 36);
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            Table header = new Table(UnitValue.createPercentArray(new float[]{2, 3}));
            header.setWidth(UnitValue.createPercentValue(100));
            header.addCell(new Cell().add(new Paragraph("SARAL VIMS").setFont(bold).setFontSize(14).setFontColor(ColorConstants.BLUE)).setBorder(null));
            Cell hospitalCell = new Cell().setBorder(null);
            hospitalCell.add(new Paragraph("DEMO HOSPITAL").setFont(bold));
            hospitalCell.add(new Paragraph("Raipur, (C.G.) 492007").setFont(normal));
            hospitalCell.add(new Paragraph("Phone no : 9657487657").setFont(normal));
            hospitalCell.setTextAlignment(TextAlignment.RIGHT);
            header.addCell(hospitalCell);
            document.add(header);
            document.add(new Paragraph("\n"));
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{2, 3, 2, 3}))
                    .setWidth(UnitValue.createPercentValue(100));
            String receiptNo = receiptNumberGenerator.generateReceiptNo();
            String receivedOn = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(new Date());
            String reportedOn = receivedOn;
            String address = "N/A";
            String phone = "N/A";
            if (dto.getPatient().getPatientContactInformation() != null) {
                Address address1 = dto.getPatient().getPatientContactInformation().getAddress();
                if (address1 != null) {
                    address = address1.toString();
                }
                if (dto.getPatient().getPatientContactInformation().getPrimaryPhone() != null) {
                    phone = dto.getPatient().getPatientContactInformation().getPrimaryPhone();
                }
            }
            addInfoRow(infoTable, "RECEIPT NO :", receiptNo, "CONSULTANT :", dto.getPatient() != null ? dto.getDoctor().getFirstName() : "N/A");
            addInfoRow(infoTable, "PATIENT NAME :", dto.getPatient().getPatientPersonalInformation().getFirstName() + " " + dto.getPatient().getPatientPersonalInformation().getLastName(),
                    "DATE OF BIRTH :", dto.getPatient().getPatientPersonalInformation().getDateOfBirth());
            addInfoRow(infoTable, "GENDER :", dto.getPatient().getPatientPersonalInformation().getGender(), "RECEIVED ON :", receivedOn);
            addInfoRow(infoTable, "ADDRESS :", address, "REPORTED ON :", reportedOn);
            addInfoRow(infoTable, "MOBILE NO :", phone, "", "");
            document.add(infoTable);
            for (PackageTestReportItem item : dto.getPackageTestReportItem()) {
                String testName = item.getTest().getName(); // Test name
                List<TestReportParameter> parameters = item.getParameters(); // Parameters list
                document.add(new Paragraph("\n"));
                document.add(new Paragraph(testName)
                        .setFont(bold)
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER));
                document.add(new Paragraph("\n"));
                Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 2}))
                        .setWidth(UnitValue.createPercentValue(100));
                table.addHeaderCell(createHeaderCell("Parameters"));
                table.addHeaderCell(createHeaderCell("Values"));
                table.addHeaderCell(createHeaderCell("Unit"));
                table.addHeaderCell(createHeaderCell("Normal Range"));
                for (TestReportParameter param : parameters) {
                    table.addCell(new Cell().add(new Paragraph(param.getName())));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(param.getValue()))));
                    table.addCell(new Cell().add(new Paragraph(param.getUnit())));
                    table.addCell(new Cell().add(new Paragraph(param.getReferenceRange())));
                }
                document.add(table);
            }
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("CHECKED BY").setFont(bold).setFontSize(10));
            document.add(new Paragraph("Pathology (Pathologist)"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("PRINT DATE & TIME : " + receivedOn));
            document.add(new Paragraph("This document is not for medico-legal purpose & must be co-related clinically.").setFontSize(8));
            document.add(new Paragraph("USER ID : ADMIN"));
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
    private void addInfoRow(Table table, String key1, String val1, String key2, String val2) throws IOException {
        table.addCell(new Cell().add(new Paragraph(key1).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))).setBorder(null));
        table.addCell(new Cell().add(new Paragraph(val1)).setBorder(null));
        table.addCell(new Cell().add(new Paragraph(key2).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))).setBorder(null));
        table.addCell(new Cell().add(new Paragraph(val2)).setBorder(null));
    }
    private Cell createHeaderCell(String text) throws IOException {
        return new Cell()
                .add(new Paragraph(text).setFontColor(ColorConstants.WHITE).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)))
                .setBackgroundColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
    }
}