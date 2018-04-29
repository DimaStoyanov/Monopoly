package netcracker.study.monopoly.api.dto;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
public class GithubUser {

    private String login;

    @JsonProperty("followers_url")
    private String followersUrl;

    @JsonProperty("avatar_url")
    private String avatarUrl;
}
