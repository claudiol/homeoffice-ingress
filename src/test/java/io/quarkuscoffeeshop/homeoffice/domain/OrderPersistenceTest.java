package io.quarkuscoffeeshop.homeoffice.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import java.math.BigDecimal;
import static io.smallrye.common.constraint.Assert.assertNotNull;

@QuarkusTest
public class OrderPersistenceTest {

    Logger logger = LoggerFactory.getLogger(OrderPersistenceTest.class);

    @Test @Transactional
    public void testOrderPersistence() {
        Order order = new Order(
                "ja280ea4-1b92-4f26-8df4-3b11d38e38dd",
                "WEB",
                Instant.now(),
                Optional.empty(),
                Optional.of(new ArrayList<LineItem>(){{ add(new LineItem(Item.CAKEPOP, BigDecimal.valueOf(3.15), "Goofy")); }}),
                Optional.empty());
        order.persist();

        Order updatedOrder = Order.findById("fa280ea4-1b92-4f26-8df4-3b11d38e38dd");
        logger.info("UpdatedOrder: {}", updatedOrder);
        assertNotNull(updatedOrder);

    }

}
