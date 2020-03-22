package xyz.a5s7.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import xyz.a5s7.services.CounterService;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CountersControllerTest {
    private static final String BASE_PATH = "/counters";
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CounterService service;

    @Test
    public void shouldReturn201WhenCreated() throws Exception {
        String name = "counter";
        when(service.create(name)).thenReturn(true);

        MockHttpServletResponse response = mvc.perform(
                post(BASE_PATH)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(name))
                .andReturn().getResponse()
                ;

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void shouldReturn409WhenCounterAlreadyCreated() throws Exception {
        String name = "counter";
        when(service.create(name)).thenReturn(false);

        MockHttpServletResponse response = mvc.perform(
                post(BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(name))
                .andReturn().getResponse()
                ;

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    public void shouldReturn404IfNotExistWhenInc() throws Exception {
        String name = "sum";
        doThrow(IllegalArgumentException.class).when(service).inc(name);

        mvc.perform(
                post(BASE_PATH + "/{name}/inc", name))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""))
        ;
    }

    @Test
    public void shouldIncCounter() throws Exception {
        String name = "счетчик";
        mvc.perform(
                post(BASE_PATH + "/{name}/inc", name))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
        ;
        verify(service).inc(name);
    }

    @Test
    public void shouldGetValueOfCounter() throws Exception {
        String name = "333";
        long expected = 12312L;
        when(service.get(name)).thenReturn(expected);

        mvc.perform(
                get(BASE_PATH + "/{name}", name))
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content().string(Long.toString(expected)))
        ;
    }

    @Test
    public void shouldReturn404IfNotExistWhenGet() throws Exception {
        String name = "names";
        when(service.get(name)).thenReturn(null);

        mvc.perform(
                get(BASE_PATH + "/{name}", name))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""))
        ;
    }

    @Test
    public void shouldReturnSumOfCounters() throws Exception {
        long expected = 123123123L;
        when(service.sumAllCounters()).thenReturn(expected);

        mvc.perform(
                get(BASE_PATH)
                    .param(CountersController.VIEW_PARAM, "Sum")
        )
            .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
            .andExpect(status().isOk())
            .andExpect(content().string(Long.toString(expected)))
        ;
    }

    @Test
    public void shouldReturnNamesOfCounters() throws Exception {
        Set<String> expectedValues = new HashSet<>();
        expectedValues.add("sum");
        expectedValues.add("names");
        expectedValues.add("счетчик");
        expectedValues.add("123");
        expectedValues.add("dslk,sdo");
        when(service.getNames()).thenReturn(expectedValues);

        String result = mvc.perform(
                get(BASE_PATH)
                        .param(CountersController.VIEW_PARAM, "nAmeS")
        )
                .andDo(r -> System.out.println(r.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        assertThat(result).contains(expectedValues);
    }

    @Test
    public void shouldReturnOkWhenViewUnknown() throws Exception {
        mvc.perform(
                get(BASE_PATH)
                    .param(CountersController.VIEW_PARAM, "avg")
        )
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(CountersController.UNKNOWN_VIEW_MSG)))
        ;
    }

    @Test
    public void shouldRemoveCounter() throws Exception {
        String name = "names";
        mvc.perform(
                delete(BASE_PATH + "/{name}", name))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
        ;
        verify(service).remove(name);
    }
}