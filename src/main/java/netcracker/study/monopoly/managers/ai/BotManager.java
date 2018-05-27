package netcracker.study.monopoly.managers.ai;

import netcracker.study.monopoly.api.dto.Offer;
import netcracker.study.monopoly.models.entities.Game;

public interface BotManager {
    void processOffer(Offer offer, Game game);

    void makeStep(Game game);

    void triggerAcceptOffer(Offer offer, Game game);

    void triggerDeclaimedOffer(Offer offer, Game game);
}
