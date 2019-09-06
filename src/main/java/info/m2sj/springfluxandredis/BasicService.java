package info.m2sj.springfluxandredis;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
public class BasicService {
    private final ReactiveRedisConnectionFactory factory;
    private final ReactiveRedisOperations<String, String> reactiveRedisOperations;
    private static final AtomicInteger count = new AtomicInteger(0);

    public BasicService(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, String> reactiveRedisOperations) {
        this.factory = factory;
        this.reactiveRedisOperations = reactiveRedisOperations;
    }


    public void loadData() {
        List<String> data = new ArrayList<>();
        IntStream.range(0, 100).forEach(i -> {
            System.out.println(count.getAndAdd(1));
            data.add(UUID.randomUUID().toString());
        });

        Flux<String> stringFlux = Flux.fromIterable(data);
        factory.getReactiveConnection()
                .serverCommands()
                .flushAll()
                .thenMany(stringFlux.flatMap(uid -> reactiveRedisOperations
                        .opsForValue()
                        .set(uid, uid)));
    }
}
