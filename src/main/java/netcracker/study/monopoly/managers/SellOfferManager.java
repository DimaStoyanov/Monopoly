package netcracker.study.monopoly.managers;

import lombok.NonNull;
import netcracker.study.monopoly.api.dto.Offer;
import netcracker.study.monopoly.exceptions.NotAllowedOperationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SellOfferManager {

    private static final AtomicInteger lastId = new AtomicInteger();
    private final Map<Integer, Offer> offers = new ConcurrentHashMap<>();
    private final Map<UUID, List<Integer>> offerInGame = new ConcurrentHashMap<>();


    public Offer saveOffer(Offer.OfferBuilder offerBuilder, UUID gameId) {
        offerBuilder.rqId(lastId.incrementAndGet());
        offerBuilder.createdAt(LocalDate.now());
        Offer offer = offerBuilder.build();
        offers.put(offer.getRqId(), offer);
        offerInGame.putIfAbsent(gameId, new ArrayList<>());
        offerInGame.get(gameId).add(offer.getRqId());
        return offer;
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

    public List<Integer> removeAllOfferInPosition(@NonNull UUID gameId, Integer position) {
        List<Integer> ids = offerInGame.getOrDefault(gameId, Collections.emptyList())
                .stream()
                .filter(id -> offers.get(id) != null
                        && Objects.equals(offers.get(id).getStreetPosition(), position))
                .collect(Collectors.toList());
        ids.forEach(offers::remove);
        return ids;
    }


}