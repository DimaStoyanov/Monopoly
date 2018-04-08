package netcracker.study.monopoly.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.UUID;

@Data
@NoArgsConstructor
public class PlayerInfo {
    @NonNull
    private UUID id;

    @NonNull
    private String nickname;

    @NonNull
    private String avatarUrl;

    private String status;
}
