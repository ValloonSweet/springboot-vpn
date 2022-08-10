package com.orbvpn.api.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.orbvpn.api.domain.entity.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Component
@Slf4j
public class PDFUtils {

    private BaseFont bfBold;
    private BaseFont bf;
    private int pageNumber = 0;

    public String createPDF(Invoice invoice, String directoryPath) {

        String path = directoryPath + "/Invoice_" + invoice.getId() + "_" + Timestamp.valueOf(LocalDateTime.now()).getTime() + ".pdf";

        Document doc = new Document();
        PdfWriter docWriter = null;
        initializeFonts();

        try {
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.addAuthor("ORB GLOBAL LTD");
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator("orbvpn.com");
            doc.addTitle("Invoice");
            doc.setPageSize(PageSize.LETTER);

            doc.open();
            PdfContentByte cb = docWriter.getDirectContent();

            boolean beginPage = true;
            int y = 0;

            for (int i = 0; i < 2; i++) {
                if (beginPage) {
                    beginPage = false;
                    generateLayout(cb);
                    generateHeader(cb);
                    y = 615;
                }
                generateDetail(cb, i, y);
                y = y - 15;
            }
            printPageNumber(cb);

        } catch (Exception dex) {
            dex.printStackTrace();
        } finally {
            doc.close();
            if (docWriter != null)
                docWriter.close();
        }
        return path;
    }


    private void generateLayout(PdfContentByte cb) {

        try {
            cb.setLineWidth(1f);

            // Invoice Detail box layout
            cb.rectangle(20, 50, 550, 600);
            cb.moveTo(20, 630);
            cb.lineTo(570, 630);
            cb.moveTo(50, 50);
            cb.lineTo(50, 650);
            cb.moveTo(150, 50);
            cb.lineTo(150, 650);
            cb.moveTo(430, 50);
            cb.lineTo(430, 650);
            cb.moveTo(500, 50);
            cb.lineTo(500, 650);
            cb.stroke();

            // Invoice Detail box Text Headings
            createHeadings(cb, 22, 633, "Qty");
            createHeadings(cb, 52, 633, "Item Number");
            createHeadings(cb, 152, 633, "Item Description");
            createHeadings(cb, 432, 633, "Price");
            createHeadings(cb, 502, 633, "Ext Price");

        } catch (Exception dex) {
            dex.printStackTrace();
        }

    }

    private void generateHeader(PdfContentByte cb) {

        try {

            createHeadings(cb, 20, 720, "INVOICE" , 32);
            createHeadings(cb, 350, 750, "+1 (725) 223 0569");
            createHeadings(cb, 350, 735, "info@orbvpn.com");
            createHeadings(cb, 350, 720, "orbvpn.com");

            createHeadings(cb, 460, 750, "ORB GLOBAL LTD");
            createHeadings(cb, 460, 735, "London, England");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void generateDetail(PdfContentByte cb, int index, int y) {
        DecimalFormat df = new DecimalFormat("0.00");

        try {
            createContent(cb, 48, y, String.valueOf(index + 1), PdfContentByte.ALIGN_RIGHT);
            createContent(cb, 52, y, "ITEM" + (index + 1), PdfContentByte.ALIGN_LEFT);
            createContent(cb, 152, y, "Product Description - SIZE " + (index + 1), PdfContentByte.ALIGN_LEFT);

            double price = Double.parseDouble(df.format(Math.random() * 10));
            double extPrice = price * (index + 1);
            createContent(cb, 498, y, df.format(price), PdfContentByte.ALIGN_RIGHT);
            createContent(cb, 568, y, df.format(extPrice), PdfContentByte.ALIGN_RIGHT);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void createHeadings(PdfContentByte cb, float x, float y, String text, int... fontsize) {
        cb.beginText();
        if(fontsize.length > 0)
            cb.setFontAndSize(bf, fontsize[0]);
        else
            cb.setFontAndSize(bfBold, 10);
        cb.setTextMatrix(x, y);
        cb.showText(text.trim());
        cb.endText();
    }

    private void printPageNumber(PdfContentByte cb) {
        cb.beginText();
        cb.setFontAndSize(bfBold, 8);
        cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, "Page No. " + (pageNumber + 1), 570, 25, 0);
        cb.endText();
        pageNumber++;
    }

    private void createContent(PdfContentByte cb, float x, float y, String text, int align) {
        cb.beginText();
        cb.setFontAndSize(bf, 8);
        cb.showTextAligned(align, text.trim(), x, y, 0);
        cb.endText();
    }

    private void initializeFonts() {

        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}
