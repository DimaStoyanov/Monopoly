package netcracker.study.monopoly.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class Offer {
    int rqId;
    UUID sellerId;
    UUID buyerId;
    Integer cost;
    Integer streetPosition;
    LocalDate createdAt;

}
