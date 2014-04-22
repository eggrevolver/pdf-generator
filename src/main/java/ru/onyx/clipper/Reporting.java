package ru.onyx.clipper;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import org.xml.sax.SAXException;
import ru.onyx.clipper.data.PropertyGetter;
import ru.onyx.clipper.model.Report;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

/**
 * User: MasterSPB
 * Date: 15.03.12
 * Time: 22:20
 */
public class Reporting {

     public Reporting() {

     }

    public static byte[] CreateDocumentEx(String markup, HashMap<String, byte[]> fonts, PropertyGetter dataSource) throws IOException, DocumentException, SAXException, XPathExpressionException, ParserConfigurationException, ParseException {

        Report rep = new Report();

        rep.LoadMarkup(markup, fonts, dataSource);
        return rep.GetDocument();
    }

    public static byte[] CreateDocument(String markup, String fontNames, PropertyGetter dataSource) {
        try {
            HashMap<String, byte[]> fonts = FontHelper.GetFonts(fontNames);
            return CreateDocumentEx(markup, fonts, dataSource);
        } catch (Exception e) {
            throw new RuntimeException("Can't Create Document ", e);
        }
    }



    public byte[] ConcatenateDocuments(String pageName,String propertyName,PropertyGetter propGetter) throws IOException, DocumentException {
        Document concatDoc = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // step 2
        PdfCopy copy = new PdfCopy(concatDoc, out);

        // step 3
        concatDoc.open();
        // step 4
        PdfReader reader;
        int n;
        int pageSize =  propGetter.GetPageCount(pageName);

        // loop over the documents you want to concatenate
        for (int i = 0; i < pageSize; i++) {
            byte[] doc =   propGetter.GetProperty(propGetter.GetProperty(String.format("%s(%s).%s", pageName, i + 1, propertyName))).getBytes();
            reader = new PdfReader(doc);
            // loop over the pages in that document
            n = reader.getNumberOfPages();
            for (int page = 0; page < n; ) {
                copy.addPage(copy.getImportedPage(reader, ++page));
            }
            copy.freeReader(reader);
        }

        return out.toByteArray();
    }
}