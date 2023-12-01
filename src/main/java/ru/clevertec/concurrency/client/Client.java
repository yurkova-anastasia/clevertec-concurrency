package ru.clevertec.concurrency.client;

import lombok.Data;
import ru.clevertec.concurrency.model.Request;
import ru.clevertec.concurrency.model.Response;
import ru.clevertec.concurrency.server.Server;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class Client {

    private List<Integer> data;
    private final ExecutorService executorService;
    private final AtomicInteger accumulator;
    private final Lock lock;

    public Client(int size) {
        this.data = generateData(size);
        this.executorService = Executors.newFixedThreadPool(size);
        this.accumulator = new AtomicInteger();
        this.lock = new ReentrantLock();
    }


    public void sendRequest() throws ExecutionException, InterruptedException {
        final Random random = new Random();
        Server server = new Server();

        List<CompletableFuture<Response>> futures = IntStream.range(0, data.size())
                .mapToObj(this::createRequest)
                .map(request -> CompletableFuture.supplyAsync(() -> {
                            sleep();
                            Response response = server.process(request);
                            removeDataItem(request.getData());
                            accumulator.addAndGet(response.listSize());
                            return response;
                        }, executorService
                ))
                .toList();

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        combinedFuture.get();

//        Метод allOf предоставляет способ ждать завершения всех переданных CompletableFuture в массиве.
//        Затем, вызывая allOf.get(), вы блокируете текущий поток до тех пор, пока все эти CompletableFuture не завершатся.

        executorService.shutdown();
    }

    private void removeDataItem(Integer value) {
        lock.lock();
        try {
            data.remove(value);
        } finally {
            lock.unlock();
        }
    }

    private void sleep() {
        int delay = ThreadLocalRandom.current().nextInt(100, 501);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int getAccumulator() {
        return accumulator.get();
    }

    public int getDataSize() {
        return data.size();
    }

    private List<Integer> generateData(int dataSize) {
        return IntStream.rangeClosed(1, dataSize)
                .boxed()
                .collect(Collectors.toList());
    }

    private Request createRequest(int index) {
        Integer value = data.get(index);
        return Request.builder()
                .data(value)
                .build();
    }

}
