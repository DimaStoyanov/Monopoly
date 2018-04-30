package netcracker.study.monopoly.api.controllers.rest.admin;

import com.google.gson.Gson;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import netcracker.study.monopoly.api.dto.game.cells.Street;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Log4j2
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

    @PutMapping(value = "/field")
    public String saveField(@RequestBody List<Street> body) {
        log.info("Saving field: " + body);
        File fieldDir = new File("src/main/resources/static/game/field/");
        Gson gson = new Gson();
        for (Street cell : body) {
            File cellFile = new File(fieldDir, cell.getPosition().toString() + ".json");
            try {
                cell.setType("STREET");
                cell.setCost(0);
                cell.setName("NAME");

                @Cleanup
                FileWriter writer = new FileWriter(cellFile);
                gson.toJson(cell, writer);
            } catch (IOException e) {
                e.printStackTrace();
                return "Can't write to file";
            }
        }
        return "Success";
    }
}
