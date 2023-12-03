package ru.clevertec.concurrency.server;

import lombok.Data;
import lombok.Getter;
import ru.clevertec.concurrency.model.Request;
import ru.clevertec.concurrency.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Server {

    @Getter
    private List<Integer> resources;
    private final Lock lock;

    public Server() {
        this.resources = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    public Response process(Request request) {
        Integer data = request.getData();

        sleep();

        lock.lock();
        try {
            resources.add(data);
            int size = resources.size();
            return createResponse(size);
        } finally {
            lock.unlock();
        }
    }

    public int getResourcesSize() {
        return resources.size();
    }

    private void sleep() {
        int delay = ThreadLocalRandom.current().nextInt(100, 1001);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Response createResponse(int size) {
        return Response.builder()
                .listSize(size)
                .build();
    }

}
