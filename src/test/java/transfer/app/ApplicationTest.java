package transfer.app;

import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.junit.Assert.*;
import org.asynchttpclient.*;
import transfer.model.AccountDto;
import transfer.model.MoneyTransferDto;

public class ApplicationTest {

    private static AsyncHttpClient asyncHttpClient;
    private final UUID accountId1 = UUID.randomUUID();
    private final UUID accountId2 = UUID.randomUUID();
    private final BigDecimal initBalance1 = BigDecimal.valueOf(100);
    private final BigDecimal initBalance2 = BigDecimal.valueOf(100);
    private AccountDto acc1 = AccountDto.builder().id(accountId1).balance(initBalance1).build();
    private AccountDto acc2 = AccountDto.builder().id(accountId2).balance(initBalance2).build();
    private Gson gson = new Gson();

    @Before
    public void init() {
        Application.main(null);
        asyncHttpClient = asyncHttpClient();
    }

    @AfterClass
    public static void destroy() throws IOException {
        asyncHttpClient.close();
    }

    @Test
    public void main() throws IOException, InterruptedException {
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/api/accounts"))
                .build();
        final HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

       System.out.println(response.body());
    }

    @Test
    public void usingAsyncClient() throws InterruptedException, ExecutionException {
        final Future<Response> whenResponse = asyncHttpClient.prepareGet("http://localhost:4567/api/accounts").execute();
        final Response response = whenResponse.get();
        assertEquals("[]", response.getResponseBody());
    }

    @Test
    public void createAccounts() throws InterruptedException, ExecutionException {
        final String acc1String = gson.toJson(acc1);
        final String acc2String = gson.toJson(acc2);
        final Future<Response> account1CreateResponse = asyncHttpClient.preparePost("http://localhost:4567/api/accounts/create")
                .setBody(acc1String)
                .execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(final Response response) throws Exception {
                        return response;
                    }
                });
        final Future<Response> account2CreateResponse = asyncHttpClient.preparePost("http://localhost:4567/api/accounts/create")
                .setBody(acc2String)
                .execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(final Response response) throws Exception {
                        return response;
                    }
                });
        final Response response = account1CreateResponse.get();
        final Response response2 = account2CreateResponse.get();
        assertEquals(acc1String, response.getResponseBody());
        assertEquals(acc2String, response2.getResponseBody());
    }

    @Test
    public void testMultipleTransfers() throws ExecutionException, InterruptedException, IOException {
        createAccounts();
        Future<Response> whenResponse = null;
        Future<Response> whenResponse2 = null;
        for (int i = 0; i < 100; i++) {
            whenResponse = asyncHttpClient.preparePost("http://localhost:4567/api/transactions/create")
                    .setBody(gson.toJson(MoneyTransferDto.builder().transactionId(UUID.randomUUID()).amount(BigDecimal.ONE).senderId(accountId1).receiverId(accountId2).build()))
                    .execute(
                            new AsyncCompletionHandler<Response>() {
                        @Override
                        public Response onCompleted(Response response) throws Exception {
                            return response;
                        }
                    }
                    );
            whenResponse2 = asyncHttpClient.preparePost("http://localhost:4567/api/transactions/create")
                    .setBody(gson.toJson(MoneyTransferDto.builder().transactionId(UUID.randomUUID()).amount(BigDecimal.ONE).senderId(accountId2).receiverId(accountId1).build()))
                    .execute(
                            new AsyncCompletionHandler<Response>() {
                        @Override
                        public Response onCompleted(Response response) throws Exception {
                            return response;
                        }
                    }
                    );
        }

        final Response response = whenResponse.get();
        assertTrue(response.getStatusCode() == HttpStatus.CREATED_201);
        final Response response2 = whenResponse2.get();
        assertTrue(response2.getStatusCode() == HttpStatus.CREATED_201);
        Thread.sleep(1000);

        final AccountDto acc1StatePostRequests = getAccount(accountId1);
        final AccountDto acc2StatePostRequests = getAccount(accountId2);
        assertTrue((acc1StatePostRequests.getBalance().add(acc2StatePostRequests.getBalance())).equals(initBalance1
                .add(initBalance2)));
        asyncHttpClient.close();
    }

    private AccountDto getAccount (final UUID id) throws InterruptedException, ExecutionException {
        Future<Response> whenResponse = null;
        whenResponse = asyncHttpClient.prepareGet("http://localhost:4567/api/accounts/" + id)
                .execute(
                        new AsyncCompletionHandler<Response>() {
                            @Override
                            public Response onCompleted(Response response) throws Exception {
                                return response;
                            }
                        }
                );
        final Response response = whenResponse.get();
        return (gson.fromJson(response.getResponseBody(), AccountDto.class));
    }
}