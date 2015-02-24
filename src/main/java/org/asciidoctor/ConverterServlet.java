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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        PrintWriter writer = resp.getWriter();
        Boolean htmlOK = false;
        Boolean pdfOK = false;
		try {
			final Path pathHtml  = asciidoctor.convertToHTML("sample.adoc");
            htmlOK = (pathHtml != null && Files.exists(pathHtml));

	        final Path path = asciidoctor.convertToPDF("sample.adoc");
            pdfOK =  (path != null && Files.exists(path));
		} catch (Exception e) {
			writer.println(e.getMessage());
		}

        writer.println("HTML : " + htmlOK +  " / PDF : " + pdfOK);
    }
}
