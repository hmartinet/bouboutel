package ch.poudriere.bouboutel.utils;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 *
 * @author Hervé Martinet
 */
public class PDFManager {

    Configuration config = new Configuration(Configuration.VERSION_2_3_31);

    public PDFManager() {
        config.setClassForTemplateLoading(this.getClass(), "/templates");
        // Recommended settings for new projects:
        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(
                TemplateExceptionHandler.RETHROW_HANDLER);
        config.setLogTemplateExceptions(false);
        config.setWrapUncheckedExceptions(true);
        config.setFallbackOnNullLoopVariable(false);
        BeansWrapper bw = new BeansWrapper(Configuration.VERSION_2_3_31);
        config.setSharedVariable("statics", bw.getStaticModels());
        DefaultObjectWrapper ow
                = new DefaultObjectWrapper(Configuration.VERSION_2_3_31);
        ow.setIterableSupport(true);
        config.setObjectWrapper(ow);
    }

    public String renderHtml(String filename, Map params) throws IOException, TemplateException {
        Template template = config.getTemplate(filename);
        Writer writer = new StringWriter();
        template.process(params, writer);
        return writer.toString();
    }

    public ITextRenderer createRenderer(String filename, Map params) throws IOException, TemplateException {
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(this.renderHtml(filename, params));
        renderer.getFontResolver().addFont("C:\\WINDOWS\\FONTS\\CALIBRI.TTF", true);
        renderer.layout();
        return renderer;
    }

    public void createPDF(String filename, Map params, OutputStream out) throws IOException, TemplateException {
        this.createRenderer(filename, params).createPDF(out);
    }
    
    public Document getDocument(String filename, Map params) throws IOException, TemplateException {
        return this.createRenderer(filename, params).getDocument();
    }

    public void save(String filename, Map params, File file) throws IOException, TemplateException {
        createPDF(filename, params, new FileOutputStream(file));
    }

    public void print(String filename, Map params) throws IOException, TemplateException, PrinterException {              
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createPDF(filename, params, out);

        PDDocument document = Loader.loadPDF(out.toByteArray());
        
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
        attrs.add(MediaSizeName.ISO_A4);
        job.print(attrs);
    }

    public void printTicket(Map params) throws IOException, TemplateException, PrinterException {              
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createPDF("ticket.ftlh", params, out);

        PDDocument document = Loader.loadPDF(out.toByteArray());
        document.getPage(0).setRotation(90);
        
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
        attrs.add(MediaSizeName.ISO_A6);
        job.print(attrs);
    }
}
