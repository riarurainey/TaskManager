package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private HttpRequest request;
    private final String API_TOKEN;
    HttpResponse<String> response;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder().uri(URI.create(url + "/register")).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        API_TOKEN = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI url = URI.create(String.format("http://localhost:8078/save/%s?API_TOKEN=%s", key, API_TOKEN));
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Код ответа: " + response.statusCode());

    }

    public String load(String key) throws IOException, InterruptedException {
        URI url = URI.create(String.format("http://localhost:8078/load/%s?API_TOKEN=%s", key, API_TOKEN));
        request = HttpRequest.newBuilder()
                .GET()
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Код ответа: " + response.statusCode());
        System.out.println("Тело ответа: " + response.body());
        return response.body();

    }
}
