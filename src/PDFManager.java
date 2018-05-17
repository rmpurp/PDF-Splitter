import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.IntConsumer;

/**
 * Wrapper for a PDF document containing utility methods for extracting pages
 * and rendering
 */
public class PDFManager {
    private PDDocument pd;
    private PDFRenderer renderer;

    /**
     * Constructs a new PDFManager given the filepath of a PDF
     * @param filename the filepath of the PDF
     * @throws IOException if the PDF cannot be read or found
     */
    public PDFManager(String filename) throws IOException {
        pd = PDDocument.load(new File(filename));
        renderer = new PDFRenderer(pd);
    }

    /**
     * Renders the specified page at the specified DPI
     * @param page the page index
     * @param dpi the dots per inch desired
     * @return a BufferedImage containing the rendered page
     * @throws IOException if PDF cannot be read
     */
    public BufferedImage renderPage(int page, int dpi) throws IOException {
        float scale = ((float) dpi) / 72;
        return renderer.renderImage(page, scale);
    }

    public void close() throws IOException {
        pd.close();
    }

    /**
     * Destructive method that removes the specified number of pages and places
     * them into a new PDDocument, which is returned. The function to be passed
     * in will be called just before the page is deleted and gets passed the
     * index of the page currently being processed. Note that the actual index
     * of each page being processed is 0 since the pages are removed
     * immediately after being added.
     * @param numPages the number of pages for the new PDF
     * @param f the function callback
     * @return the prefix PDF
     * @throws IOException if an IO error occurs
     */
    public PDDocument makePrefixPDF(int numPages, IntConsumer f)
            throws IOException {
        PDDocument prefix = new PDDocument();
        for (int i = 0; i < numPages; i++) {
            prefix.importPage(pd.getPage(0));
            f.accept(i);
            deletePage(0);
        }
        return prefix;
    }

    /**
     * Deletes the page at the given index
     * @param pageNumber the page number
     * @throws IOException if an IO error occurs
     */
    public void deletePage(int pageNumber) throws IOException {
        pd.removePage(pageNumber);
    }

    /**
     * Utility method that saves the given PDDocument to the specified path
     * @param pd the document
     * @param path the filepath
     * @throws IOException if an IO error occurs
     */
    public static void savePDF(PDDocument pd, String path) throws IOException {
        pd.save(path);
        pd.close();
    }

    public static void main(String[] args) throws IOException {
        PDFManager a = new PDFManager("ch5.pdf");
        savePDF(a.makePrefixPDF(3, (i) -> Math.random()), "out.pdf");
        savePDF(a.makePrefixPDF(2, (i) -> Math.random()), "out1.pdf");
        a.close();
    }
}
