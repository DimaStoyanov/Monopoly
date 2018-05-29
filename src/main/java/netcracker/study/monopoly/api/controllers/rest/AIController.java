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

import static netcracker.study.monopoly.models.entities.Player.PlayerType.ACTIVE_BOT;
import static netcracker.study.monopoly.models.entities.Player.PlayerType.PASSIVE_BOT;

@RestController
@RequestMapping("/ai")
@Log4j2
public class AIController {

    public final static Map<PlayerType, Set<UUID>> bots = new EnumMap<>(PlayerType.class);
    private final PlayerRepository playerRepository;

    public AIController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        createPassiveBots();
        createActiveBots();
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

    private void createPassiveBots() {

        saveBot(PASSIVE_BOT, "Kyle (Bot)",
                "https://cdn1.iconfinder.com/data/icons/bots/280/bot-9-2-512.png");

        saveBot(PASSIVE_BOT, "Mountain (Bot)", "https://cdn.iconscout.com/public/images/icon/premium/png-512/" +
                "ai-bot-robot-technology-machine-3cad6573056c2037-512x512.png");

        saveBot(PASSIVE_BOT, "Merlin (Bot)",
                "https://d30y9cdsu7xlg0.cloudfront.net/png/415502-200.png");

        saveBot(PASSIVE_BOT, "Tusk (Bot)",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdk7yCVIfu4AfYfalxPacGL5Kib6Kipak2rzaydVj_zBEIw92vPg");

    }

    private void createActiveBots() {
        saveBot(ACTIVE_BOT, "Poncho (Bot)",
                "https://i.pinimg.com/originals/bb/c1/2b/bbc12bff3a544b88c3d408669231073a.png");

        saveBot(ACTIVE_BOT, "Armstrong (Bot)",
                "https://www.voicebot.net/ImagesCommon/Icons/256x256/VoiceBot.png");

        saveBot(ACTIVE_BOT, "Viper (Bot)",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRXtJOZh9ZMSwgtMIvUFnXXyOXENY6wCDF9xK5biQQ-1RBwWvNTNg");

        saveBot(ACTIVE_BOT, "Imp (Bot)",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRV3-H9POQTftpCKSkhpCCmGG5QFxERkT7YzzsHvBhPlMe-UuIBoA");
    }

    private void saveBot(PlayerType botType, String name, String avatarUrl) {
        bots.computeIfAbsent(botType, t -> new HashSet<>());
        Set<UUID> botIds = bots.get(botType);

        Optional<Player> botOpt = playerRepository.findByNickname(name);
        if (!botOpt.isPresent()) {
            log.info(String.format("Create bot %s", name));
            Player bot = new Player(name);
            bot.setPlayerType(botType);
            bot.setAvatarUrl(avatarUrl);
            playerRepository.save(bot);
            botIds.add(bot.getId());
        } else {
            botIds.add(botOpt.get().getId());
        }
    }

}
