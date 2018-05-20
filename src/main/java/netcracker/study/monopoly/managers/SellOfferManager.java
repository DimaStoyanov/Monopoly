package netcracker.study.monopoly.managers;

import lombok.Data;
import lombok.NonNull;
import netcracker.study.monopoly.exceptions.NotAllowedOperationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SellOfferManager {

    private final AtomicInteger lastId = new AtomicInteger();
    private final Map<Integer, Offer> offers = new ConcurrentHashMap<>();
    private final Map<UUID, List<Integer>> offerInGame = new ConcurrentHashMap<>();


    public int createOffer(UUID gameId, UUID sellerId, UUID buyerId, Integer cost) {
        Offer offer = new Offer(sellerId, buyerId, cost);
        offers.put(offer.rqId, offer);
        offerInGame.putIfAbsent(gameId, new ArrayList<>());
        offerInGame.get(gameId).add(offer.rqId);
        return offer.rqId;
    }

    public Offer getOffer(@NonNull Integer rqId) {
        return offers.get(rqId);
    }

    public Offer declineOffer(@NonNull Integer rqId, UUID playerId) {
        Offer offer = offers.get(rqId);
        if (!Objects.equals(offer.getBuyerId(), playerId)) {
            throw new NotAllowedOperationException();
        }
        offers.remove(rqId);
        return offer;
    }

    public void removeAllOfferInGame(@NonNull UUID gameId) {
        offerInGame.getOrDefault(gameId, Collections.emptyList()).forEach(offers::remove);
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