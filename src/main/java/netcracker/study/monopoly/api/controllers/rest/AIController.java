package netcracker.study.monopoly.api.controllers.rest;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.exceptions.PlayerNotFoundException;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.entities.Player.PlayerType;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

import static netcracker.study.monopoly.models.entities.Player.PlayerType.PASSIVE_BOT;

@RestController
@RequestMapping("/ai")
@Log4j2
public class AIController {

    public static EnumMap<PlayerType, Set<UUID>> bots;
    private final PlayerRepository playerRepository;

    public AIController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        bots = new EnumMap<>(PlayerType.class);
        createPassiveBot();
    }

    @GetMapping("/types")
    public List<String> getAvailableBotTypes() {
        return Arrays.stream(PlayerType.values())
                .filter(p -> p != PlayerType.PLAYER)
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @GetMapping("/bots")
    public List<String> getAvailableBots() {
        return bots.values().stream()
                .map(ids -> ids.stream()
                        .map(id -> playerRepository.findById(id)
                                .orElseThrow(() -> new PlayerNotFoundException(id)))
                        .map(Player::getNickname)
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void createPassiveBot() {

        saveBot(PASSIVE_BOT, "Kyle (Bot)",
                "https://cdn1.iconfinder.com/data/icons/bots/280/bot-9-2-512.png");

        saveBot(PASSIVE_BOT, "Mountain (Bot)", "https://cdn.iconscout.com/public/images/icon/premium/png-512/" +
                "ai-bot-robot-technology-machine-3cad6573056c2037-512x512.png");

        saveBot(PASSIVE_BOT, "Merlin (Bot)",
                "https://d30y9cdsu7xlg0.cloudfront.net/png/415502-200.png");

        saveBot(PASSIVE_BOT, "Tusk (Bot)",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdk7yCVIfu4AfYfalxPacGL5Kib6Kipak2rzaydVj_zBEIw92vPg");

    }

    private void saveBot(PlayerType botType, String name, String avatarUrl) {
        bots.computeIfAbsent(botType, t -> new HashSet<>());
        Set<UUID> botIds = bots.get(botType);

        Optional<Player> botOpt = playerRepository.findByNickname(name);
        if (!botOpt.isPresent()) {
            log.info(String.format("Create bot %s", name));
            Player bot = new Player(name);
            bot.setPlayerType(PASSIVE_BOT);
            bot.setAvatarUrl(avatarUrl);
            playerRepository.save(bot);
            botIds.add(bot.getId());
        } else {
            botIds.add(botOpt.get().getId());
        }
    }

}
