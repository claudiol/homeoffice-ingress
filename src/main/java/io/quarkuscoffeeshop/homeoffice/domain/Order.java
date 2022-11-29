package io.quarkuscoffeeshop.homeoffice.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@Entity
@Table(name = "Orders")
public class Order extends PanacheEntityBase {

    @Transient
    static Logger logger = LoggerFactory.getLogger(Order.class);

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(nullable = false, name = "orderId")
    @Column(nullable = false, name = "id")
    //public String orderId;
    public String id;

    public String orderSource;

    public String loyaltyMemberId;

    public String location;

    public Instant timestamp;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = CascadeType.ALL)
    private List<LineItem> baristaLineItems;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order", cascade = CascadeType.ALL)
    private List<LineItem> kitchenLineItems;

    public Order(final String orderId, final String orderSource, final Instant instant, final Optional<String> loyaltyMemberId, Optional<List<LineItem>> baristaLineItems, Optional<List<LineItem>> kitchenLineItems) {
        this.id = orderId;
        this.orderSource = orderSource;
		this.location = "Atlanta";
        this.timestamp = instant;
        if (loyaltyMemberId.isPresent()) {
            this.loyaltyMemberId = loyaltyMemberId.get();
        }else{
            this.loyaltyMemberId = null;
        }
        if (baristaLineItems.isPresent()) {
            baristaLineItems.get().forEach(baristaLineItem -> {
                addBaristaLineItem(new LineItem(baristaLineItem.getItem(), baristaLineItem.getPrice(), baristaLineItem.getName(), this));
            });
        }
        if (kitchenLineItems.isPresent()) {
            baristaLineItems.get().forEach(kitchenLineItem -> {
                addKitchenLineItem(new LineItem(kitchenLineItem.getItem(), kitchenLineItem.getPrice(), kitchenLineItem.getName(), this));
            });
        }
    }

    public Order() {
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Order.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("orderSource='" + orderSource + "'")
                .add("location='" + location + "'")
                .add("loyaltyMemberId='" + loyaltyMemberId + "'")
                .add("timestamp=" + timestamp)
                .add("baristaLineItems=" + baristaLineItems)
                .add("kitchenLineItems=" + kitchenLineItems)
                .toString();
    }

    /**
     * Convenience method to prevent Null Pointer Exceptions
     * @param lineItem
     */
    public void addBaristaLineItem(final LineItem lineItem) {
        if (this.baristaLineItems == null) {
            this.baristaLineItems = new ArrayList<>();
        }
        this.baristaLineItems.add(new LineItem(lineItem.getItem(), lineItem.getPrice(), lineItem.getName(), this));
    }

    /**
     * Convenience method to prevent Null Pointer Exceptions
     * @param lineItem
     */
    public void addKitchenLineItem(final LineItem lineItem) {
        if (this.kitchenLineItems == null) {
            this.kitchenLineItems = new ArrayList<>();
        }
        this.kitchenLineItems.add(new LineItem(lineItem.getItem(), lineItem.getPrice(), lineItem.getName(), this));
    }

    public Optional<List<LineItem>> getBaristaLineItems() {
        return Optional.ofNullable(baristaLineItems);
    }

    public Optional<List<LineItem>> getKitchenLineItems() {
        return Optional.ofNullable(kitchenLineItems);
    }

    public Optional<String> getLoyaltyMemberId() {
        return Optional.ofNullable(this.loyaltyMemberId);
    }

    public String getOrderId() {
        return id;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public String getLocation() {
        return location;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setBaristaLineItems(List<LineItem> baristaLineItems) {
        baristaLineItems.forEach(baristaLineItem -> {
            addBaristaLineItem(baristaLineItem);
        });
    }

    public void setKitchenLineItems(List<LineItem> kitchenLineItems) {
        kitchenLineItems.forEach(kitchenLineItem -> {
            addKitchenLineItem(kitchenLineItem);
        });
    }
}
