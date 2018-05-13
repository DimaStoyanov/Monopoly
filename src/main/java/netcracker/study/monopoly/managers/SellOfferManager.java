package netcracker.study.monopoly.managers;

import lombok.Data;
import lombok.NonNull;
import netcracker.study.monopoly.exceptions.NotAllowedOperationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SellOfferManager {

    public static final int OFFER_TIMEOUT_SECONDS = 60;
    private final AtomicInteger lastId = new AtomicInteger();
    private final Map<Integer, Offer> offers = new ConcurrentHashMap<>();

    public int createOffer(UUID sellerId, UUID buyerId, Integer cost) {
        Offer offer = new Offer(sellerId, buyerId, cost);
        offers.put(offer.rqId, offer);
        return offer.rqId;
    }

    public Offer getOffer(@NonNull Integer rqId) {
        LocalDate now = LocalDate.now();
        Offer offer = offers.get(rqId);
        Duration duration = Duration.between(offer.createdAt, now);
        if (duration.getSeconds() > OFFER_TIMEOUT_SECONDS) {
            offers.remove(rqId);
        }
        return offers.get(rqId);
    }

    public void removeOffer(@NonNull Integer rqId, UUID buyerId) {
        Offer offer = offers.get(rqId);
        if (!Objects.equals(offer.getBuyerId(), buyerId)) {
            throw new NotAllowedOperationException();
        }
        offers.remove(rqId);
    }


    @Data
    public class Offer {
        int rqId;
        UUID sellerId;
        UUID buyerId;
        Integer cost;
        LocalDate createdAt;

        Offer(UUID sellerId, UUID buyerId, Integer cost) {
            rqId = lastId.incrementAndGet();
            this.sellerId = sellerId;
            this.buyerId = buyerId;
            this.cost = cost;
            createdAt = LocalDate.now();
        }
    }
}