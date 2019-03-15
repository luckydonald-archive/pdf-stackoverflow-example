import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.fdf.FDFDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.tools.ImportFDF;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        merge();
        setData();
        flatten();
    }

    /**
     * PART 1, merge .pdf files with form fields
     */
    private static void merge() throws IOException {

        String destinationFileName = "output01.pdf";

        PDFMergerUtility mpdf = new PDFMergerUtility();
        mpdf.addSource("sample5.pdf");
        mpdf.addSource("test.pdf");
        mpdf.setDestinationFileName(destinationFileName);
        mpdf.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
    }

    /**
     * PART 2, apply input data from .xfdf file
     */
    private static void setData() throws IOException {
        String inputFileName = "output01.pdf"; //from step before, merged pdf files.
        String destinationFileName = "output02.pdf";

        // the file containing the data to fill in the form.
        String xfdfFileName = "test.xfdf";

        PDDocument pdf_document = PDDocument.load(new File(inputFileName));
        FDFDocument fdf_document = FDFDocument.loadXFDF(new File(xfdfFileName));

        /*
         * import XFDF to PDF
         */
        ImportFDF importer = new ImportFDF();
        importer.importFDF(pdf_document, fdf_document);

        pdf_document.save(new File(destinationFileName));
    }

    /**
     * PART 3, flatten away the formfields
     */
    private static void flatten() throws IOException {
        String inputFileName = "output02.pdf"; //from step before, merged pdf files.
        String destinationFileName = "output03.pdf"; //from step before, merged pdf files.
        PDDocument pdf_document = PDDocument.load(new File(inputFileName));

        List<PDField> the_fields = new ArrayList<PDField>();
        for (PDField field: pdf_document.getDocumentCatalog().getAcroForm().getFieldTree()) {
            the_fields.add(field);
        }
        System.out.println("Flattening fields: " + Arrays.stream(the_fields.toArray()).map(field -> ((PDField)field).getFullyQualifiedName()).collect(Collectors.joining(", ","[","]")));
        pdf_document.getDocumentCatalog().getAcroForm().flatten(the_fields, true);
        pdf_document.save(new File(destinationFileName));
    }
}
