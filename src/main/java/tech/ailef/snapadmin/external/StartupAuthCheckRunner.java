package tech.ailef.snapadmin.external;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupAuthCheckRunner implements CommandLineRunner {
	@Autowired
	private SnapAdmin snapAdmin;
	
    @Override
    public void run(String...args) throws Exception {
    	HttpRequest request = HttpRequest.newBuilder()
		        .uri(URI.create("http://localhost:8080/admin"))
		        .build();
		
    	HttpResponse<String> response = HttpClient
			  .newBuilder()
			  .build()
			  .send(request, BodyHandlers.ofString());
    	
    	String body = response.body();
    }
}