# Very Basic Performance Testing with Red Hat JBoss A-MQ

These instructions are for doing very basic performance test using single threaded producers and consumers to
push messages onto a queue and to pull a message off of a queue.

First, start the broker:
`mvn -P performance-test`

Second, start the consumer:
`mvn -P timed-consumer`

Last, start the producer:
`mvn -P timed-producer`

Onces this completes, it will display the number of messages per second. Remember for the consumer, the time starts
before it gets its first message. So depending on how long you take to start the producer, it can skew the time a
little.
