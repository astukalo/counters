# Simple Spring boot application to manage counters
 
Create counter:
```
curl -v -H "Content-Type: text/plain" -X POST --data "counter_name" http://localhost:8082/counters
```

Increment counter:
```
curl -v -X POST http://localhost:8082/counters/name/inc
```

Get counter's value:
```
curl -v http://localhost:8082/counters/name
```

Remove counter:
```
curl -v -X DELETE http://localhost:8082/counters/name
```

Get sum of all counters
```
curl -v http://localhost:8082/counters?view=sum
```

Get names of all counters
```
curl -v http://localhost:8082/counters?view=names
```