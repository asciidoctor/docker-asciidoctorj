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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;

@Named
public class AsciidoctorProcessor {

	private Asciidoctor asciidoctor;

	@PostConstruct
	public void init() {
		asciidoctor = Asciidoctor.Factory.create();
	}

	public Boolean convertToHTML() throws URISyntaxException, IOException,
			InterruptedException {

		// logger.info("convert adoc to html...");
		Map<String, Object> parameters;
		parameters = options()
				.backend("html5")
				.safe(SafeMode.UNSAFE)
				.headerFooter(true)
				.eruby("erubis")
				.attributes(
						AttributesBuilder.attributes().attribute("icons!", "")
								.attribute("allow-uri-read")
								.attribute("copycss!", "").asMap()).asMap();

		final String result = asciidoctor.render(dataConvertToPDF, parameters);
		return result.contains("<h1>Document Title");

	}

	public Boolean convertToPDF() throws URISyntaxException, IOException,
			InterruptedException {

		final String outputDir = getPDFDir();
		// Files.createDirectory(FileSystems.getDefault().getPath(outputDir));
		// logger.info("output dir : " + outputDir);

		// sample adoc to pdf
		String inputFilename = "adoc/sample.adoc";
		String outputFilename = "sample.pdf";
		asciidoctor.convert(getResourceAsString(inputFilename),
				parameters(outputDir, outputFilename, "pdf"));
		Path out = FileSystems.getDefault().getPath(outputDir + outputFilename);
		return Files.exists(out);
	}

	private Map<String, Object> parameters(String outputDir,
			String outputFilename, String backend) {
		return options().backend(backend).safe(SafeMode.UNSAFE)
				.headerFooter(true).inPlace(true)
				.toFile(new File(outputDir + outputFilename)).asMap();
	}


	private String dataConvertToPDF = "= Document Title\\n Maxime GREAU <greaumaxime@gmail.com>\\n\\nContent : {asciidoctor-version}.\\n[source,java]\\n----\\nSystem.out.println(Hello, World!);\\n----\"";

	/**
	 * Gets a resourse in a similar way as {@link File#File(String)}
	 */
	public File getResource(String pathname) {
		try {
			URL resource = Thread.currentThread().getContextClassLoader()
					.getResource(pathname);
			if (resource != null) {
				return new File(Thread.currentThread().getContextClassLoader()
						.getResource(pathname).toURI());
			} else {
				throw new RuntimeException(new FileNotFoundException(pathname));
			}
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String getResourceAsString(String path) {
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

	public String readFromStream(final InputStream is) {
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

	public static final String getPDFDir() {
		return "/opt/jboss/wildfly/standalone/data/";
	}

}
