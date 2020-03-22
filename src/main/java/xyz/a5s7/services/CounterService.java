package xyz.a5s7.services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

@Service
public class CounterService {
    private final Map<String, LongAdder> map = new ConcurrentHashMap<>();

    public boolean create(String name) {
        return map.putIfAbsent(name, new LongAdder()) == null;
    }

    public void inc(String name) {
        LongAdder longAdder = map.get(name);
        if (longAdder == null) {
            throw new IllegalArgumentException("Counter with name " + name + " is not found");
        }
        longAdder.increment();
    }

    public Long get(String name) {
        LongAdder longAdder = map.get(name);
        return longAdder != null ? longAdder.longValue() : null;
    }

    public void remove(String name) {
        map.remove(name);
    }

    public Long sumAllCounters() {
        //potential overflow & eventual consistency (some items may be removed or added while summing)
        //if sum is called frequently, then it may be pre-calculated while incrementing or removing a counter
        return map.values().parallelStream().mapToLong(LongAdder::longValue).sum();
    }

    public Set<String> getNames() {
        return map.keySet();
    }

}
