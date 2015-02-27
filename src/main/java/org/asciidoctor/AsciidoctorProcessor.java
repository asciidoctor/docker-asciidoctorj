package org.asciidoctor;


import static org.asciidoctor.OptionsBuilder.options;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class AsciidoctorProcessor {

	private Asciidoctor asciidoctor;

	@Inject
	private Logger logger;

	@PostConstruct
	public void init() {
		asciidoctor = Asciidoctor.Factory.create();
	}

	public Path convertToHTML(final String inputFilename) throws URISyntaxException, IOException,
			InterruptedException {

		logger.info("converting adoc to html...");

        asciidoctor.convert(getResourceAsString(inputFilename),
                parameters(getOutputDir(), "sample.html", "html5"));
        return FileSystems.getDefault().getPath(getOutputDir() + "sample.html");
	}

	public Path convertToPDF(final String inputFilename) throws URISyntaxException, IOException,
			InterruptedException {
		logger.info("Converting adoc to PDF to" + getOutputDir());

		// sample adoc to pdf
		String outputFilename = "sample.pdf";
		asciidoctor.convert(getResourceAsString(inputFilename),
				parameters(getOutputDir(), outputFilename, "pdf"));
		Path out = FileSystems.getDefault().getPath(getOutputDir() + outputFilename);
		return out;
	}

	private Map<String, Object> parameters(String outputDir,
			String outputFilename, String backend) {
		return options().backend(backend).safe(SafeMode.UNSAFE)
				.headerFooter(true).inPlace(true)
				.toFile(new File(outputDir + outputFilename)).asMap();
	}


    public static final String getOutputDir() {
        return "/opt/jboss/documents/";
    }


	private String getResourceAsString(String path) {
		try {
			URL resource = Thread.currentThread().getContextClassLoader()
					.getResource(path);
			if (resource != null) {
				return readFromStream(Thread.currentThread()
						.getContextClassLoader().getResourceAsStream(path));
			} else {
				throw new RuntimeException(new FileNotFoundException(path));
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private String readFromStream(final InputStream is) {
		if (is == null) {
			return "";
		}
		final char[] buffer = new char[1024];
		final StringBuilder out = new StringBuilder();
		try {
			final Reader in = new InputStreamReader(is, "UTF-8");
			try {
				for (;;) {
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
						break;
					out.append(buffer, 0, rsz);
				}
			} finally {
				in.close();
			}
		} catch (UnsupportedEncodingException ex) {
			/* ... */
		} catch (IOException ex) {
			/* ... */
		}
		return out.toString();
	}


}
