package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class KVServerTest {
    private KVServer kvServer;
    private HttpClient client;
    private HttpRequest request;
    private HttpResponse<String> response;

    private final static int CODE200_OK = 200;
    private final static int CODE400_BAD_REQUEST = 400;
    private final static int CODE403_FORBIDDEN = 403;
    private final static int CODE404_NOT_FOUND = 404;
    private final static int CODE405_METHOD_NOT_ALLOWED = 405;

    private final static String TEST_API_TOKEN = "DEBUG";


    @BeforeEach
    void start() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void stop() {
        kvServer.stop();
    }

    @Test
    void endPoint_Register_Should_Return_ApiToken_And_CODE200_When_Used_GET_Method()
            throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE200_OK, response.statusCode());
        assertNotNull(response.body(), "Тело запроса пустое, apiToken не пришел");
    }

    @Test
    void endPoint_Register_Should_Return_CODE405_When_Used_NOT_GET_Method() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/register");
        request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Неверный возврат статус кода при неверном методе");
    }

    @Test
    void endPoint_Save_Should_Return_CODE403_When_Api_Token_Is_Wrong() throws IOException, InterruptedException {
        String key = "1";
        String wrong_Api_Token = "HelloWorld";
        URI url = URI.create(String.format("http://localhost:8078/save/%s?API_TOKEN=%s", key, wrong_Api_Token));
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("value"))
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE403_FORBIDDEN, response.statusCode(),
                "Неверный возврат статус кода при неверном API_TOKEN");
    }

    @Test
    void endPoint_Save_Should_Return_CODE200_When_Api_Token_Is_Correct() throws IOException, InterruptedException {
        String key = "1";
        URI url = URI.create(String.format("http://localhost:8078/save/%s?API_TOKEN=%s", key, TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("value"))
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE200_OK, response.statusCode(), "Неверный возврат статус кода при верном API_TOKEN");
    }

    @Test
    void endPoint_Save_Should_Return_CODE400_When_Key_Is_Empty() throws IOException, InterruptedException {
        URI url = URI.create(String.format("http://localhost:8078/save/?API_TOKEN=%s", TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("world"))
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE400_BAD_REQUEST, response.statusCode(),
                "Неверный возврат статус кода при использовании пустого ключа");
    }

    @Test
    void endPoint_Save_Should_Return_CODE400_When_Value_Is_Empty() throws IOException, InterruptedException {
        String key = "1";
        URI url = URI.create(String.format("http://localhost:8078/save/%s?API_TOKEN=%s", key, TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE400_BAD_REQUEST, response.statusCode(),
                "Неверный возврат статус кода при использовании пустого значения");
    }

    @Test
    void endPoint_Save_Should_Return_CODE200_When_Key_And_Value_Not_Empty() throws IOException, InterruptedException {
        String key = "1";
        String value = "value";
        URI url = URI.create(String.format("http://localhost:8078/save/%s?API_TOKEN=%s", key, TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(value))
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE200_OK, response.statusCode(),
                "Неверный возврат статус кода при использовании непустого ключа и значения");
    }

    @Test
    void endPoint_Save_Should_Return_CODE405_When_Used_NOT_POST_Method() throws IOException, InterruptedException {
        String key = "1";
        URI url = URI.create(String.format("http://localhost:8078/save/%s?API_TOKEN=%s", key, TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .GET()
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Неверный возврат статус кода при неверном методе");
    }

    @Test
    void endPoint_Load_Should_Return_CODE403_When_Api_Token_Is_Wrong() throws IOException, InterruptedException {
        String key = "1";
        String wrong_Api_Token = "HelloWorld";
        URI url = URI.create(String.format("http://localhost:8078/load/%s?API_TOKEN=%s", key, wrong_Api_Token));
        request = HttpRequest.newBuilder()
                .GET()
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE403_FORBIDDEN, response.statusCode(),
                "Неверный возврат статус кода при неверном API_TOKEN");
    }

    @Test
    void endPoint_Load_Should_Return_CODE200_When_Api_Token_Is_Correct() throws IOException, InterruptedException {
        String key = "1";
        String value = "value";
        URI url1 = URI.create(String.format("http://localhost:8078/save/%s?API_TOKEN=%s", key, TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(value))
                .header("content-type", "application/json")
                .uri(url1)
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI url = URI.create(String.format("http://localhost:8078/load/%s?API_TOKEN=%s", key, TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .GET()
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE200_OK, response.statusCode(), "Неверный возврат статус кода при верном API_TOKEN");
    }

    @Test
    void endPoint_Load_Should_Return_CODE400_When_Key_Is_Empty() throws IOException, InterruptedException {
        URI url = URI.create(String.format("http://localhost:8078/load/?API_TOKEN=%s", TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .GET()
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE400_BAD_REQUEST, response.statusCode(),
                "Неверный возврат статус кода при использовании пустого ключа");
    }

    @Test
    void endPoint_Load_Should_Return_CODE404_When_Key_Not_Exist() throws IOException, InterruptedException {
        String key = "1";
        URI url = URI.create(String.format("http://localhost:8078/load/%s?API_TOKEN=%s", key, TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .GET()
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE404_NOT_FOUND, response.statusCode(),
                "Неверный возврат статус кода при использовании ключа, которого не существует");
    }

    @Test
    void endPoint_Load_Should_Return_CODE200_When_Key_Is_Exist() throws IOException, InterruptedException {
        String key = "1";
        String value = "value";
        URI url1 = URI.create(String.format("http://localhost:8078/save/%s?API_TOKEN=%s", key, TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(value))
                .header("content-type", "application/json")
                .uri(url1)
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI url = URI.create(String.format("http://localhost:8078/load/%s?API_TOKEN=%s", key, TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .GET()
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE200_OK, response.statusCode(), "Неверный возврат статус кода при верном ключе");
    }

    @Test
    void endPoint_Load_Should_Return_CODE405_When_Used_NOT_GET_Method() throws IOException, InterruptedException {
        String key = "1";
        URI url = URI.create(String.format("http://localhost:8078/load/%s?API_TOKEN=%s", key, TEST_API_TOKEN));
        request = HttpRequest.newBuilder()
                .DELETE()
                .header("content-type", "application/json")
                .uri(url)
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(CODE405_METHOD_NOT_ALLOWED, response.statusCode(),
                "Неверный возврат статус кода при неверном методе");
    }
}
