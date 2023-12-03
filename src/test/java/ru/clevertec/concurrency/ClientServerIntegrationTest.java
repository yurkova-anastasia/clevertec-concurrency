package ru.clevertec.concurrency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.concurrency.client.Client;
import ru.clevertec.concurrency.server.Server;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClientServerIntegrationTest {

    private Server server;
    private Client client;
    private final int size = 100;

    @BeforeEach
    void setUp() {
        server = new Server();
        client = new Client(size);
    }

    @Test
    public void testSendRequest() throws InterruptedException, ExecutionException {
        //given
        int expectedAccumulator = (1 + size) * (size / 2);
        int expectedDataSize = 0;
        int expectedResourceSize = size;
        List<Integer> expectedResources = generateData(expectedResourceSize);

        //when
        client.sendRequest(server);
        int actualAccumulator = client.getAccumulator();
        int actualDataSize = client.getDataSize();
        int actualResourcesSize = server.getResourcesSize();
        List<Integer> actualResources =
                server.getResources().stream()
                        .sorted()
                        .toList();

        //then
        Assertions.assertEquals(expectedAccumulator, actualAccumulator);
        Assertions.assertEquals(expectedDataSize, actualDataSize);
        Assertions.assertEquals(expectedResourceSize, actualResourcesSize);
        Assertions.assertEquals(expectedResources, actualResources);
    }

    private List<Integer> generateData(int dataSize) {
        return IntStream.rangeClosed(1, dataSize)
                .boxed()
                .collect(Collectors.toList());
    }

}
