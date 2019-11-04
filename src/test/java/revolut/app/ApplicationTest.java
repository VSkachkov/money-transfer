package revolut.app;

import com.google.gson.Gson;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.ConsoleHandler;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.junit.Assert.*;
import org.asynchttpclient.*;

public class ApplicationTest {

    private static AsyncHttpClient asyncHttpClient;
    final String newAccounts = "[\n" +
            "\t{\n" +
            "\t\t\"id\": \"0a93076c-f18c-46cf-8735-3cf742245d80\", \n" +
            "\t\t\"balance\": \"100\"\n" +
            "\t}, \n" +
            "\t{\n" +
            "\t\t\"id\": \"a9ef647c-5d40-4423-b5ee-cf08d6300117\", \n" +
            "\t\t\"balance\": \"200\"\n" +
            "\t}\n" +
            "]";
    final String oneTransfer = "{\n" +
            "    \"receiverId\": \"a9ef647c-5d40-4423-b5ee-cf08d6300117\",\n" +
            "    \"senderId\": \"0a93076c-f18c-46cf-8735-3cf742245d80\",\n" +
            "    \"amount\": \"1\"\n" +
            "}";
    final String secondTransfer = "{\n" +
            "    \"senderId\": \"a9ef647c-5d40-4423-b5ee-cf08d6300117\",\n" +
            "    \"receiverId\": \"0a93076c-f18c-46cf-8735-3cf742245d80\",\n" +
            "    \"amount\": \"1\"\n" +
            "}";

    @Before
    public void init()  throws IOException, InterruptedException, ExecutionException {
        Application.main(null);
        asyncHttpClient = asyncHttpClient();
    }

    @AfterClass
    public static void destroy() throws IOException {
        asyncHttpClient.close();
    }

    @Test
    public void main() throws IOException, InterruptedException {
//        Application.main(null);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/api/accounts"))
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }

    @Test
    public void usingAsyncClient() throws InterruptedException, ExecutionException {
        Future<Response> whenResponse = asyncHttpClient.prepareGet("http://localhost:4567/api/accounts").execute();
        Response response = whenResponse.get();
        assertEquals("[]", response.getResponseBody());
    }

    @Test
    public void createAccounts() throws InterruptedException, ExecutionException {

        Future<Response> whenResponse = asyncHttpClient.preparePost("http://localhost:4567/api/accounts/create")
                .setBody(newAccounts)
                .setHeader("Content-Type", "application/json")
                .execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(Response response) throws Exception {
                        return response;
                    }
                });
        Response response = whenResponse.get();
        assertEquals("{\"code\":0,\"success\":true}", response.getResponseBody());
    }

    @Test
    public void testMultipleTransfers() throws ExecutionException, InterruptedException, IOException {
        createAccounts();
        Future<Response> whenResponse = null;
        Future<Response> whenResponse2 = null;
        for (int i = 0; i < 100; i++) {
            whenResponse = asyncHttpClient.preparePost("http://localhost:4567/api/transactions/create")
                    .setBody(oneTransfer)
                    .setHeader("Content-Type", "application/json")
                    .execute(
                            new AsyncCompletionHandler<Response>() {
                        @Override
                        public Response onCompleted(Response response) throws Exception {
                            return response;
                        }
                    }
                    );
            whenResponse2 = asyncHttpClient.preparePost("http://localhost:4567/api/transactions/create")
                    .setBody(secondTransfer)
                    .setHeader("Content-Type", "application/json")
                    .execute(
                            new AsyncCompletionHandler<Response>() {
                        @Override
                        public Response onCompleted(Response response) throws Exception {
                            return response;
                        }
                    }
                    );
        }

        Response response = whenResponse.get();
        Response response2 = whenResponse2.get();
        System.out.println(response.getResponseBody());
        System.out.println(response2.getResponseBody());
        Thread.sleep(1000);

        Future<Response> allAccountsResponseFuture = asyncHttpClient.prepareGet("http://localhost:4567/api/accounts").execute();

        Response response3 = allAccountsResponseFuture.get();
        System.out.println("Program Result: " + response3.getResponseBody());
        asyncHttpClient.close();

    }
}