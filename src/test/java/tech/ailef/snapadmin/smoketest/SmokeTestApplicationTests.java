package tech.ailef.snapadmin.smoketest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SmokeTestApplicationTests {

	@LocalServerPort
	private int port;

	@Test
	void contextLoadsAndAdminPanelIsServed() throws Exception {
		HttpClient client = HttpClient.newHttpClient();

		HttpResponse<String> index = get(client, "/admin");
		assertThat(index.statusCode()).isEqualTo(200);

		HttpResponse<String> model = get(client, "/admin/model/tech.ailef.snapadmin.smoketest.SmokeTestEntity");
		assertThat(model.statusCode()).isEqualTo(200);
	}

	private HttpResponse<String> get(HttpClient client, String path) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:" + port + path))
				.GET()
				.build();
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}
}
