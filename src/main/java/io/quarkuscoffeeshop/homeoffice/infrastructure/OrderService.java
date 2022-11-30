package io.quarkuscoffeeshop.homeoffice.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkuscoffeeshop.homeoffice.domain.EventType;
import io.quarkuscoffeeshop.homeoffice.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import javax.persistence.PersistenceException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import java.io.IOException;

import static javax.transaction.Transactional.TxType;


@ApplicationScoped
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @PersistenceContext // or even @Autowired
    private EntityManager entityManager;

    @Transactional
    public void onEventReceived(EventType eventType, Order order) {
        LOGGER.debug("processing EventType {} for Order {}", eventType, order);
        switch (eventType) {
            case OrderCreated:
                LOGGER.debug("onOrderCreated being called: {}", order);
                onOrderCreated(order);
                break;
            case OrderUpdated:
                onOrderUpdated(order);
                break;
            case LoyaltyMemberPurchase:
                onLoyaltyMemberPurchase(order);
                break;
            default:
                LOGGER.error("Cannot determine appropriate action for {}", eventType);
                //order.persist();
        }
    }

    void onOrderCreated(final Order order) {
        LOGGER.debug("onOrderCreated being persisted: {}", order);
        //order.persist();
        //  entityManager.saveOrUpdate(order);
		try {
		  order.id="";
          order.persist();
		} catch (PersistenceException e) {
          LOGGER.debug("Order exception: {}", e);
		} 
        LOGGER.debug("Order persisted: {}", order);
    }

    void onOrderUpdated(final Order order){
        order.persist();
        LOGGER.debug("Order persisted: {}", order);
    }

    void onLoyaltyMemberPurchase(final Order order) {
        LOGGER.debug("Persisted and sent {}", order);
    }

    @Transactional
    public void orderCreated(JsonNode event) {


//        Order order = new Order();
//        order.orderId = event.get("orderId").asText();
//        order.orderSource = event.get("orderSource").asText();
//        order.timestamp = LocalDateTime.parse(event.get("timestamp").asText());
//        order.loyaltyMemberId = event.get("loyaltyMemberId").asText();
//        LOGGER.info("orderId: {}", order.orderId);
//        entityManager.persist(order);
//
        LOGGER.info("Processed 'OrderCreated' event: {}", event);
    }
}
