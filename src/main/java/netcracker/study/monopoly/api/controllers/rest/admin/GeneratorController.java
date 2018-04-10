package netcracker.study.monopoly.api.controllers.rest.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class GeneratorController {

    @GetMapping("/images")
    public List<String> getImages() {
        File file = new File("src/main/resources/static/game/images");
        return Arrays
                .stream(Objects.requireNonNull(file.listFiles()))
                .map(f -> "game/images/" + f.getName())
                .filter(f -> f.endsWith(".jpg"))
                .collect(Collectors.toList());
    }
}
