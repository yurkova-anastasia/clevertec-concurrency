package ru.clevertec.concurrency.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

class ClientTest {

    private Client client;
    private final int size = 100;


    @BeforeEach
    void setUp() {
        client = new Client(size);
    }

    @Test
    void test_checkAccumulator() throws ExecutionException, InterruptedException {
        //given
        int expected = 5050;

        //when
        client.sendRequest();
        int actual = client.getAccumulator();

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_checkDataSize_whenRequestsSend() throws ExecutionException, InterruptedException {
        //given
        int expected = 0;

        //when
        client.sendRequest();
        int actual = client.getDataSize();

        //then
        Assertions.assertEquals(expected, actual);
    }

}