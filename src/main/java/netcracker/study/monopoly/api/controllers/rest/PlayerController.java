package netcracker.study.monopoly.api.controllers.rest;

import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.GithubUser;
import netcracker.study.monopoly.api.dto.PlayerInfo;
import netcracker.study.monopoly.converters.PlayerInfoConverter;
import netcracker.study.monopoly.exceptions.GameNotFoundException;
import netcracker.study.monopoly.exceptions.NotAllowedOperationException;
import netcracker.study.monopoly.exceptions.PlayerNotFoundException;
import netcracker.study.monopoly.models.entities.Game;
import netcracker.study.monopoly.models.entities.Player;
import netcracker.study.monopoly.models.entities.PlayerState;
import netcracker.study.monopoly.models.repositories.GameRepository;
import netcracker.study.monopoly.models.repositories.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

import static java.lang.String.format;
import static netcracker.study.monopoly.models.entities.Game.GameState.NOT_STARTED;

@RestController
@RequestMapping("/player")
@Log4j2
public class PlayerController {

    public static final String ROOM_ID_KEY = "roomId";
    public static final String GAME_ID_KEY = "gameId";
    public static final String PROFILE_ID_KEY = "id";
    public static final String PLAYER_STATE_ID_KEY = "psId";


    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final PlayerInfoConverter playerInfoConverter;

    public PlayerController(PlayerRepository playerRepository,
                            GameRepository gameRepository, PlayerInfoConverter playerInfoConverter) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.playerInfoConverter = playerInfoConverter;
    }


    @GetMapping("/friends")
    public List<PlayerInfo> getFriends(HttpSession session) {
        Player player = getPlayer(session);
        return playerInfoConverter.toDtoAll(player.getFriends());
    }

    private Player getPlayer(HttpSession session) {
        return playerRepository.findById((UUID) session.getAttribute("id"))
                .orElseThrow(PlayerNotFoundException::new);
    }

    @GetMapping("/info")
    public PlayerInfo getInfo(HttpSession session) {
        Player player = getPlayer(session);
        return playerInfoConverter.toDto(player);
    }

    @GetMapping("/room")
    public Long getRoomId(HttpSession session) {
        return (Long) session.getAttribute(ROOM_ID_KEY);
    }

    @PutMapping("/room")
    public void setRoomId(@RequestParam(name = ROOM_ID_KEY) Long roomId,
                          HttpSession session, Principal principal) {
        log.info(format("Player %s join in room %s", principal.getName(), roomId));
        session.setAttribute(ROOM_ID_KEY, roomId);
    }

    @GetMapping("/game")
    public UUID getGameId(HttpSession session) {
        return (UUID) session.getAttribute(GAME_ID_KEY);
    }

    @PutMapping("/game")
    public boolean setGameId(@RequestParam(name = GAME_ID_KEY) UUID gameId,
                             HttpSession session, Principal principal) {
        log.info(format("Player %s join in game {%s}", principal.getName(), gameId));
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));

        if (game.getCurrentState() != NOT_STARTED) {
            throw new NotAllowedOperationException("Can't join in the game. Game already started.");
        }

        for (PlayerState playerState : game.getPlayerStates()) {
            if (Objects.equals(playerState.getPlayer().getNickname(), principal.getName())) {
                session.setAttribute(PLAYER_STATE_ID_KEY, playerState.getId());
                session.setAttribute(GAME_ID_KEY, gameId);
                session.setAttribute(ROOM_ID_KEY, null);
                return true;
            }
        }
        return false;
    }

    @PutMapping("/add_friend")
    public ResponseEntity<String> addFriend(@RequestParam(name = "nickname") String nickname,
                                            Principal principal) {
        Player playerFrom = playerRepository.findByNickname(principal.getName())
                .orElseThrow(PlayerNotFoundException::new);
        Player playerTo = playerRepository.findByNickname(nickname)
                .orElseThrow(PlayerNotFoundException::new);

        if (playerFrom.getFriends().contains(playerTo)) {
            return new ResponseEntity<>("Already friends", HttpStatus.BAD_REQUEST);
        }
        if (playerFrom.equals(playerTo)) {
            return new ResponseEntity<>("Can't be friend with yourself", HttpStatus.BAD_REQUEST);
        }

        playerFrom.addFriend(playerTo);
        playerRepository.save(playerFrom);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @PutMapping("/remove_friend")
    public ResponseEntity<String> removeFriend(@RequestParam(name = "nickname") String nickname,
                                               Principal principal) {
        String name = principal.getName();
        Player playerFrom = playerRepository.findByNickname(name)
                .orElseThrow(PlayerNotFoundException::new);
        Player playerTo = playerRepository.findByNickname(nickname)
                .orElseThrow(PlayerNotFoundException::new);
        if (playerFrom.getFriends().contains(playerTo)) {
            playerFrom.removeFriend(playerTo);
            playerRepository.save(playerFrom);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Can't find friend " + nickname, HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/add_followers")
    @Transactional
    public String addFollowers() {
        OAuth2Authentication authentication = (OAuth2Authentication)
                SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getUserAuthentication().getName();
        log.info(format("%s add followers to friends", name));


        Player player = playerRepository.findByNickname(name)
                .orElseThrow(PlayerNotFoundException::new);
        Map details = (Map) authentication.getUserAuthentication().getDetails();

        RestTemplate restTemplate = new RestTemplate();
        String followersUrl = (String) details.get("followers_url");
        GithubUser[] githubFollowers = restTemplate.getForObject(followersUrl, GithubUser[].class, new HashMap<>());

        for (GithubUser githubUser : githubFollowers) {
            // TODO: not add followers, that user   already removes from friends
            playerRepository.findByNickname(githubUser.getLogin())
                    .ifPresent(player::addFriend);
        }
        playerRepository.save(player);

        return "Success";
    }

    @PutMapping("/remove_friends")
    public String removeAllFriends(Principal principal) {
        log.info(format("%s remove all friends", principal.getName()));

        Player player = playerRepository.findByNickname(principal.getName())
                .orElseThrow(PlayerNotFoundException::new);
        player.removeAllFriends();
        playerRepository.save(player);
        return "Success";
    }
}
