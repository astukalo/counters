package xyz.a5s7.services;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CounterServiceTest {
    CounterService counterService;

    @Before
    public void setUp() {
        counterService = new CounterService();
    }

    @Test
    public void shouldCreateCounterOnce() {
        assertThat(counterService.create("sum")).isTrue();
        assertThat(counterService.create("sum")).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldComplainIfIncNotExistingCounter() {
        counterService.inc("count");
    }

    @Test
    public void shouldReturnNullIfNotFound() {
        assertThat(counterService.get("unknown")).isNull();
    }

    @Test
    public void shouldIncAndGetCounter() {
        String name = "счетчик";
        counterService.create(name);
        assertThat(counterService.get(name)).isEqualTo(0);
        counterService.inc(name);
        counterService.inc(name);
        assertThat(counterService.get(name)).isEqualTo(2);
    }

    @Test
    public void shouldRemoveCounter() {
        String name = "count";
        counterService.remove(name);
        assertThat(counterService.get(name)).isNull();
        counterService.create(name);
        assertThat(counterService.get(name)).isZero();
        counterService.remove(name);
        assertThat(counterService.get(name)).isNull();
        //checking idempotence - no complains
        counterService.remove(name);
        assertThat(counterService.get(name)).isNull();
    }

    @Test
    public void sumAllCounters() {
        assertThat(counterService.sumAllCounters()).isZero();

        String[] names = new String[]{"7", "2", "3"};
        int expectedSum = Arrays.stream(names).mapToInt(Integer::valueOf).sum();

        for (String name : names) {
            counterService.create(name);
        }
        assertThat(counterService.sumAllCounters()).isZero();

        for (String name : names) {
            for (int i = 0; i < Integer.parseInt(name); i++) {
                counterService.inc(name);
            }
        }
        assertThat(counterService.sumAllCounters()).isEqualTo(expectedSum);
    }

    @Test
    public void shouldReturnNames() {
        assertThat(counterService.getNames()).isEmpty();
        counterService.create("name");
        counterService.create("name");
        counterService.create("cc");
        counterService.create("name");
        counterService.create("sum");
        counterService.create("values");
        counterService.create("dd");
        assertThat(counterService.getNames())
                .containsExactlyInAnyOrder("name", "cc", "sum", "values", "dd");
    }
}