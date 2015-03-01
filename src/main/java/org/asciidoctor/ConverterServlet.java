package org.asciidoctor;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/convert")
public class ConverterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    @Inject
    private AsciidoctorProcessor asciidoctor;

    private static final String CONVERTER_PARAMETER = "converter";

    private static final String FILENAME_PARAMETER = "filename";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        PrintWriter writer = resp.getWriter();

        String asciidocFile = "sample.adoc";
        if (req.getParameter(FILENAME_PARAMETER) != null){
            asciidocFile = req.getParameter(FILENAME_PARAMETER);
        }

        if (req.getParameter(CONVERTER_PARAMETER) != null &&  "pdf".equals(req.getParameter(CONVERTER_PARAMETER))){
            writer.println(convertToPDF(asciidocFile));
        }
        else {
            writer.println(convertToHTML(asciidocFile));
        }
    }

    private String convertToPDF(String asciidocFile){
        String result = "PDF : ";
        Boolean pdfOK ;

        try {
            final Path path = asciidoctor.convertToPDF(asciidocFile);
            pdfOK =  (path != null && Files.exists(path));

        } catch (Exception e) {
            return result + (e.getMessage());
        }

        return result + pdfOK;
    }

    private String convertToHTML(String asciidocFile){
        String result = "HTML : ";
        Boolean htmlOK ;
        try {
            final Path pathHtml  = asciidoctor.convertToHTML(asciidocFile);
            htmlOK = (pathHtml != null && Files.exists(pathHtml));

        } catch (Exception e) {
           return result + (e.getMessage());
        }

        return result + htmlOK;
    }
}
