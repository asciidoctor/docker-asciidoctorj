package org.asciidoctor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConverterServletTest {


	@Deployment(testable = false)
	public static WebArchive create() {
		return ShrinkWrap
				.create(WebArchive.class, "asciidoctorj-it.war")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsManifestResource("wildfly/MANIFEST.MF", "MANIFEST.MF")
				.addAsWebInfResource(
						new File("src/main/resources/adoc/sample.adoc"),
						"classes/sample.adoc")
				.addPackage(ConverterServlet.class.getPackage());
	}

	@Test
	public void should_print_asciidoctor_version(@ArquillianResource URL base)
			throws IOException {
		URL url = new URL(base, "version");
		assertThat(getResponse(url), is("Asciidoctor version : 1.5.2"));
	}

	@Test
	public void should_convert_to_html(@ArquillianResource URL base) throws IOException {
        URL url = createURL(base, "convert", "html", "sample.adoc");
		assertThat(getResponse(url),
				is("HTML : true"));
	}

    @Test
    public void should_convert_to_pdf(@ArquillianResource URL base) throws IOException {
        URL url = createURL(base, "convert", "pdf", "sample.adoc");
        assertThat(getResponse(url),
                is("PDF : true"));
    }

    private URL createURL(URL base, String path, String converter, String filename) throws IOException{
        final String query =  "converter=" + converter + "&"
                 + "filename=" + filename;
        return new URL(base, path + "?" + query );
    }

	private String getResponse(URL url) {
		StringBuffer response = new StringBuffer();
		HttpURLConnection con;

		try {
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response.toString();
	}

}
