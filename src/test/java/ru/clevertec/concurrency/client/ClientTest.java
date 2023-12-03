package ru.clevertec.concurrency.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.concurrency.model.Request;
import ru.clevertec.concurrency.model.Response;
import ru.clevertec.concurrency.server.Server;

import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ClientTest {

    private Client client;
    @Mock
    private Server server;
    private final int size = 100;


    @BeforeEach
    void setUp() {
        client = new Client(size);
    }

    @Test
    void test_checkAccumulator() throws ExecutionException, InterruptedException {
        //given
        Response response = Response.builder().listSize(1).build();
        doReturn(response).when(server).process(any(Request.class));
        int expected = size;

        //when
        client.sendRequest(server);
        int actual = client.getAccumulator();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_checkDataSize_whenRequestsSend() throws ExecutionException, InterruptedException {
        //given
        Response response = Response.builder().listSize(1).build();
        doReturn(response).when(server).process(any(Request.class));
        int expected = 0;

        //when
        client.sendRequest(server);
        int actual = client.getDataSize();

        //then
        Assertions.assertEquals(expected, actual);
    }

}