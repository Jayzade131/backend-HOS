package com.org.hosply360.util.PDFGenUtil;

import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.colors.ColorConstants;

public class PageBorderHandler implements IEventHandler {
    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfPage page = docEvent.getPage();
        PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), docEvent.getDocument());

        Rectangle pageSize = page.getPageSize();
        canvas.setStrokeColor(ColorConstants.BLACK);
        canvas.setLineWidth(1);
        canvas.rectangle(
                pageSize.getLeft() + 15,
                pageSize.getBottom() + 15,
                pageSize.getWidth() - 30,
                pageSize.getHeight() - 30
        );
        canvas.stroke();
        canvas.release();
    }
}
