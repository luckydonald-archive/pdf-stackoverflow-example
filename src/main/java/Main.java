import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test for flattening a fun formatted field
 */
public class Main {
    public static void main(String[] args) throws IOException {
        String inputFileName = "test.pdf";

        String val = "<?xml version=\"1.0\"?>"
                + "<body xmlns=\"http://www.w3.org/1999/xhtml\">"
                +   "<p style=\"color:#FF0000;font-size:8pt;\">"
                +       "<i>Small</i> <b>Red</b>&#13;"
                +   "</p>"
                +   "<p style=\"color:#00FF00;font-size:20pt;\">"
                +       "<i>Big</i> <b>Green</b>&#13;"
                +   "</p>"
                + "</body>";
        String valNoFormat = "Small Red\rBig Green\r";

        PDDocument pdf_document = PDDocument.load(new File(inputFileName));
        PDAcroForm acroForm = pdf_document.getDocumentCatalog().getAcroForm();
        PDTextField acroField = (PDTextField)acroForm.getField("example_field_number_one");

        acroField.setValue(valNoFormat);
        acroField.setRichTextValue(val);

        acroForm.setNeedAppearances(true);
        pdf_document.save(new File("output01.pdf"));

        List<PDField> the_fields = new ArrayList<PDField>();
        for (PDField field: pdf_document.getDocumentCatalog().getAcroForm().getFieldTree()) {
            the_fields.add(field);
        }
        System.out.println("Flattening fields: " + Arrays.stream(the_fields.toArray()).map(field -> ((PDField)field).getFullyQualifiedName()).collect(Collectors.joining(", ","[","]")));
        acroForm.setNeedAppearances(true);
        pdf_document.getDocumentCatalog().getAcroForm().flatten(the_fields, true);
        pdf_document.save(new File("output02.pdf"));
    }
}
