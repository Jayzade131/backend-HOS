package com.org.hosply360.util.Others;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.properties.TextAlignment;

public class DuplicateWatermarkUtil {

    public static class DuplicateWatermarkHandler implements IEventHandler {

        private final PdfFont font;

        public DuplicateWatermarkHandler() {
            try {
                this.font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create font", e);
            }
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            Rectangle pageSize = page.getPageSize();

            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDoc);
            pdfCanvas.saveState();
            pdfCanvas.setFillColorRgb(173 / 255f, 216 / 255f, 230 / 255f); // Light Blue

            PdfExtGState gs1 = new PdfExtGState();
            gs1.setFillOpacity(0.5f);
            pdfCanvas.setExtGState(gs1);


            Canvas canvas = new Canvas(pdfCanvas, pageSize, false);
            canvas.setFont(font);
            canvas.setFontSize(60);
            canvas.showTextAligned(
                    "DUPLICATE",
                    pageSize.getWidth() / 2,
                    pageSize.getHeight() / 2,
                    TextAlignment.CENTER,
                    (float) Math.toRadians(45)
            );
            canvas.close();

            pdfCanvas.restoreState();
        }
    }
}