package ru.clevertec.concurrency.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.clevertec.concurrency.model.Request;
import ru.clevertec.concurrency.model.Response;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ServerTest {

    private Server server;
    private final int size = 10;
    private List<Integer> expectedResources;

    @BeforeEach
    void setUp() {
        server = new Server();
        expectedResources = generateData(size);
    }

    @Test
    void test_checkResources_whenRequestsSend() {
        //given
        int expectedSize = size;
        List<Integer> expectedResources = generateData(expectedSize);


        //when
        List<Integer> actualResources = IntStream.range(0, expectedResources.size())
                .mapToObj(this::createRequest)
                .map(request -> server.process(request))
                .map(Response::listSize)
                .toList();
        int actualSize = server.getResourcesSize();

        //then
        Assertions.assertEquals(expectedSize, actualSize);
        Assertions.assertEquals(expectedResources, actualResources);
    }

    private List<Integer> generateData(int dataSize) {
        return IntStream.rangeClosed(1, dataSize)
                .boxed()
                .collect(Collectors.toList());
    }

    private Request createRequest(int index) {
        Integer value = expectedResources.get(index);
        return Request.builder()
                .data(value)
                .build();
    }

}