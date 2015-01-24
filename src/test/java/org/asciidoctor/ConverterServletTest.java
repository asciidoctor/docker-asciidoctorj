package org.asciidoctor;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
						"classes/adoc/sample.adoc")
				.addPackage(VersionServlet.class.getPackage());
	}

	@Test
	public void should_print_asciidoctor_version(@ArquillianResource URL base)
			throws IOException {
		URL url = new URL(base, "version");
		assertThat(getResponse(url), is("Asciidoctor version : 1.5.2"));
	}

	@Test
	public void should_convert(@ArquillianResource URL base) throws IOException {
		URL url = new URL(base, "convert");
		assertThat(getResponse(url),
				is("HTML : true / PDF : true"));
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.toString();
	}

}
