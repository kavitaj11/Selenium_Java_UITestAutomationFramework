package org.k11techlab.framework.selenium.coreframework.commonUtil.fileHandler;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;

public class PDFTextParser {
    static PDFParser parser;
    static String parsedText;
    static PDFTextStripper pdfStripper;
    static PDDocument pdDoc;
    static COSDocument cosDoc;

    PDDocumentInformation pdDocInfo;

    public PDFTextParser() {
    }

    public static String pdftoText(String filename) {
        System.out.println("Parsing from PDF file" + filename + ":");

        File f = new File(filename);
        if (!f.isFile()) {
            System.out.println("file" + filename + "does return null");
        }
        try {
            parser = new PDFParser((RandomAccessRead) new FileInputStream(f));
        } catch (Exception e) {
            System.out.println("Unable to open PDF parser");
            return null;
        }

        try {
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
        } catch (Exception e) {
            System.out.println("An error occurred in parsing the PDF Doc");
            e.printStackTrace();
            try {
                if (cosDoc != null) cosDoc.close();
                if (pdDoc != null) pdDoc.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Done");
        }
        return parsedText;
    }





}
