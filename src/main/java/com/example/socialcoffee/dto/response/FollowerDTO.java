package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.neo4j.NUser;
import com.example.socialcoffee.domain.postgres.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class FollowerDTO extends UserDTO {
    @JsonProperty(value = "is_following")
    private boolean isFollowing;

    public FollowerDTO(final User user) {
        super(user);
    }

    public FollowerDTO(final NUser user, boolean isFollowing) {
        super(user);
        this.isFollowing = isFollowing;
    }

}
