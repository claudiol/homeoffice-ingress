package io.quarkuscoffeeshop.homeoffice.infrastructure;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkuscoffeeshop.homeoffice.domain.EventType;
import io.quarkuscoffeeshop.homeoffice.domain.Order;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.apache.kafka.common.header.Header;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


@ApplicationScoped
public class KafkaEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaEventConsumer.class);

    @Inject
    OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Incoming("orders-in")
    @Transactional
    public CompletionStage<Void> onMessage(KafkaRecord<String, String> message) throws IOException {

            //LOG.debug("Kafka message with key = {} arrived", message.getKey());
            LOG.debug("message received: {}", message.getPayload());

            String eventId = getHeaderAsString(message, "id");
            //EventType eventType = EventType.valueOf(getHeaderAsString(message, "eventType"));
            EventType eventType = EventType.valueOf("OrderCreated");
			// LRC - Hardcoding all events
			//eventType=EventType.OrderCreated;

            LOG.debug("EventType is: {}", eventType);
            LOG.info("EventType is: {}", eventType);

            LOG.debug("Payload: {}", message.getPayload());

            return CompletableFuture.runAsync(()->{

                try {
                    Order order = objectMapper.readValue(message.getPayload(), Order.class);
                    LOG.debug("order: {}", order);
                    orderService.onEventReceived(eventType, order);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }).thenRun(
                    message::ack
            );
    }

//    public CompletionStage<Void> onMessage(KafkaRecord<String, String> message) throws IOException {
//        return CompletableFuture.runAsync(() -> {
//
//            //LOG.debug("Kafka message with key = {} arrived", message.getKey());
//            LOG.debug("message received: {}", message.getPayload());
//
//            String eventId = getHeaderAsString(message, "id");
//            String eventType = getHeaderAsString(message, "eventType");
//
//            LOG.debug("EventType is: {}",eventType);
//
//
//            orderEventHandler.onOrderEvent(
//                    UUID.fromString(eventId),
//                    eventType,
//                    message.getKey(),
//                    message.getPayload(),
//                    message.getTimestamp()
//            );
//
//
//        }).thenRun(message::ack);
//
//    }


    private String getHeaderAsString(KafkaRecord<?, ?> record, String name) {
        Header header = record.getHeaders().lastHeader(name);
        if (header == null) {
            throw new IllegalArgumentException("Expected record header '" + name + "' not present");
        }

        return new String(header.value(), StandardCharsets.UTF_8);
    }


}


