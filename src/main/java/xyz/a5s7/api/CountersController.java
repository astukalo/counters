package xyz.a5s7.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.a5s7.services.CounterService;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/counters")
public class CountersController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CountersController.class);

    static final String SUM_VIEW = "sum";
    static final String NAMES_VIEW = "names";
    static final String VIEW_PARAM = "view";
    static final String UNKNOWN_VIEW_MSG = "Unknown value for parameter \"" + VIEW_PARAM + "\": ";

    private final CounterService counterService;

    public CountersController(CounterService counterService) {
        this.counterService = counterService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody String name) {
        if (!counterService.create(name)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/{name}/inc")
    public void inc(@PathVariable("name") String name) {
        counterService.inc(name);
    }

    @GetMapping(value = "/{name}")
    public ResponseEntity<Long> get(@PathVariable String name) {
        Long value = counterService.get(name);
        if (value == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(value);
    }

    @GetMapping
    public ResponseEntity<String> view(@RequestParam(VIEW_PARAM) String view) {
        if (SUM_VIEW.equalsIgnoreCase(view)) {
            Long sum = counterService.sumAllCounters();
            return ResponseEntity.ok(Long.toString(sum));
        } else if (NAMES_VIEW.equalsIgnoreCase(view)) {
            String names = counterService.getNames()
                    .stream()
                    .collect(
                            Collectors.joining(System.lineSeparator())
                    );
            return ResponseEntity.ok(names);
        } else if (StringUtils.hasText(view)) {
            return new ResponseEntity<>(UNKNOWN_VIEW_MSG + view, HttpStatus.OK);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{name}")
    public void remove(@PathVariable String name) {
        counterService.remove(name);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleCounterNotFound(HttpServletRequest req, Exception ex) {
        LOGGER.error(ex.getMessage(), ex);
    }
}
