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
import com.org.hosply360.constant.Enums.TestSource;
import com.org.hosply360.dao.OPD.ReceiptNumberGenerator;
import com.org.hosply360.dao.globalMaster.Test;
import com.org.hosply360.dao.pathology.TestManager;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.List;

@Component
public class TestReportBillPdfGenerator {

    private final ReceiptNumberGenerator receiptNumberGenerator;

    public TestReportBillPdfGenerator(ReceiptNumberGenerator receiptNumberGenerator) {
        this.receiptNumberGenerator = receiptNumberGenerator;
    }

    public byte[] generateTestMangerPDF(TestManager testManager) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // === HEADER: Hospital info top-left ===
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                    .setWidth(UnitValue.createPercentValue(100));

            Cell hospitalInfo = new Cell().setBorder(null).setTextAlignment(TextAlignment.LEFT);
            hospitalInfo.add(new Paragraph("SARAL VIMS").setFont(bold).setFontSize(14));
            hospitalInfo.add(new Paragraph("DEMO HOSPITAL").setFont(bold).setFontSize(12));
            hospitalInfo.add(new Paragraph("Raipur, (C.G.) 492007").setFont(normal).setFontSize(10));
            hospitalInfo.add(new Paragraph("Phone: 9657487657").setFont(normal).setFontSize(10));
            headerTable.addCell(hospitalInfo);

            // Right cell empty (could hold logo later)
            headerTable.addCell(new Cell().setBorder(null));
            document.add(headerTable);

            document.add(new Paragraph("\n"));

            // === RECEIPT BLOCK CENTERED ===
            String receiptNo = receiptNumberGenerator.generateReceiptNo();
            String pid = safeString(() -> testManager.getPatient().getPId());

            Table receiptTable = new Table(1).setWidth(UnitValue.createPercentValue(100));
            receiptTable.addCell(new Cell()
                    .add(new Paragraph("RECEIPT").setFont(bold).setFontSize(14))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(null));
            receiptTable.addCell(new Cell()
                    .add(new Paragraph("Receipt No: " + receiptNo).setFont(normal).setFontSize(10))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(null));
            receiptTable.addCell(new Cell()
                    .add(new Paragraph("PID: " + pid).setFont(normal).setFontSize(10))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(null));
            document.add(receiptTable);

            document.add(new Paragraph("\n"));

            // === PATIENT DETAILS TABLE ===
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{20, 30, 20, 30}))
                    .setWidth(UnitValue.createPercentValue(100));

            String firstName = safeString(() -> testManager.getPatient().getPatientPersonalInformation().getFirstName());
            String lastName = safeString(() -> testManager.getPatient().getPatientPersonalInformation().getLastName());
            String dob = safeString(() -> testManager.getPatient().getPatientPersonalInformation().getDateOfBirth());
            String gender = safeString(() -> testManager.getPatient().getPatientPersonalInformation().getGender());
            String phone = safeString(() -> testManager.getPatient().getPatientContactInformation().getPrimaryPhone(), "N/A");
            String address = formatAddress(testManager.getPatient().getPatientContactInformation().getAddress());
            String reportDate = new SimpleDateFormat("dd-MM-yyyy hh:mm a")
                    .format(Timestamp.valueOf(testManager.getTestDateTime()));
            String age = calculateAge(dob);

            addInfoRow(infoTable, bold, "PATIENT NAME:", firstName + " " + lastName, "DATE OF BIRTH:", dob);
            addInfoRow(infoTable, bold, "GENDER:", gender, "AGE:", age);
            addInfoRow(infoTable, bold, "MOBILE NO:", phone, "REPORT DATE:", reportDate);
            infoTable.addCell(new Cell().add(new Paragraph("ADDRESS:").setFont(bold)).setBorder(null).setNeutralRole());
            infoTable.addCell(new Cell(1, 3).add(new Paragraph(address)).setBorder(null));

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // === TESTS TABLE ===
            Table testTable = new Table(UnitValue.createPercentArray(new float[]{8, 60, 16, 16}))
                    .setWidth(UnitValue.createPercentValue(100));
            addTableHeader(testTable, bold, "Sr. No", "Test/Package Name", "Rate", "Amount");

            double totalAmount = 0.0;
            int srNo = 1;

            if (testManager.getSource() == TestSource.INDIVIDUAL) {
                List<Test> tests = testManager.getTest();
                for (Test test : tests) {
                    double rate = test.getAmount();
                    totalAmount += rate;
                    testTable.addCell(new Cell().add(new Paragraph(String.valueOf(srNo++)).setTextAlignment(TextAlignment.CENTER)));
                    testTable.addCell(new Cell().add(new Paragraph(test.getName())));
                    testTable.addCell(new Cell().add(new Paragraph(formatAmount(rate))).setTextAlignment(TextAlignment.RIGHT));
                    testTable.addCell(new Cell().add(new Paragraph(formatAmount(rate))).setTextAlignment(TextAlignment.RIGHT));
                }
            } else {
                double pkgAmount = testManager.getPackageE().getTotalAmount();
                totalAmount += pkgAmount;
                testTable.addCell(new Cell().add(new Paragraph(String.valueOf(srNo++)).setTextAlignment(TextAlignment.CENTER)));
                testTable.addCell(new Cell().add(new Paragraph(testManager.getPackageE().getPackageName())));
                testTable.addCell(new Cell().add(new Paragraph(formatAmount(pkgAmount))).setTextAlignment(TextAlignment.RIGHT));
                testTable.addCell(new Cell().add(new Paragraph(formatAmount(pkgAmount))).setTextAlignment(TextAlignment.RIGHT));
            }

            double paidAmount = testManager.getPaidAmount() != null ? testManager.getPaidAmount() : 0.0;
            double balanceAmount = totalAmount - paidAmount;

            addTotalRow(testTable, bold, "Total Amount:", totalAmount);
            addTotalRow(testTable, bold, "Paid Amount:", paidAmount);
            addTotalRow(testTable, bold, "Balance Amount:", balanceAmount);

            document.add(testTable);

            // === FOOTER ===
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("CHECKED BY").setFont(bold).setFontSize(10));
            document.add(new Paragraph("Pathology (Pathologist)"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("PRINT DATE & TIME : " +
                    new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(new Date())));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    // === Helper Methods ===
    private void addInfoRow(Table table, PdfFont bold, String k1, String v1, String k2, String v2) {
        table.addCell(new Cell().add(new Paragraph(k1).setFont(bold)).setBorder(null).setNeutralRole());
        table.addCell(new Cell().add(new Paragraph(v1)).setBorder(null));
        table.addCell(new Cell().add(new Paragraph(k2).setFont(bold)).setBorder(null).setNeutralRole());
        table.addCell(new Cell().add(new Paragraph(v2)).setBorder(null));
    }

    private void addTableHeader(Table table, PdfFont bold, String... headers) {
        for (String header : headers) {
            table.addCell(new Cell()
                    .add(new Paragraph(header).setFont(bold).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }
    }

    private void addTotalRow(Table table, PdfFont bold, String label, double amount) {
        table.addCell(new Cell(1, 3).add(new Paragraph(label).setFont(bold)));
        table.addCell(new Cell().add(new Paragraph(formatAmount(amount))).setTextAlignment(TextAlignment.RIGHT));
    }

    private String formatAmount(double amount) {
        return String.format("%.2f", amount);
    }

    private String safeString(SupplierWithException<String> supplier) {
        return safeString(supplier, "");
    }

    private String safeString(SupplierWithException<String> supplier, String defaultValue) {
        try {
            String value = supplier.get();
            return value != null ? value : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String calculateAge(String dob) {
        try {
            LocalDate birthDate = LocalDate.parse(dob);
            return String.valueOf(Period.between(birthDate, LocalDate.now()).getYears());
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String formatAddress(Object addressObj) {
        if (addressObj == null) return "N/A";

        String s = addressObj.toString();
        if (s != null && !s.contains("@")) return s; // use if toString is already good

        StringBuilder sb = new StringBuilder();
        tryAppend(sb, addressObj, "getAddressLine1");
        tryAppend(sb, addressObj, "getAddressLine2");
        tryAppend(sb, addressObj, "getCity");
        tryAppend(sb, addressObj, "getState");
        tryAppend(sb, addressObj, "getPincode");

        return sb.length() > 0 ? sb.toString() : "N/A";
    }

    private void tryAppend(StringBuilder sb, Object obj, String methodName) {
        try {
            Method m = obj.getClass().getMethod(methodName);
            Object val = m.invoke(obj);
            if (val != null && !val.toString().isBlank()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(val.toString().trim());
            }
        } catch (Exception ignored) {}
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
