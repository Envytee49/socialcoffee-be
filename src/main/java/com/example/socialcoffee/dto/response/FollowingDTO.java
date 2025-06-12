package com.example.socialcoffee.dto.response;


import com.example.socialcoffee.domain.neo4j.NUser;
import com.example.socialcoffee.domain.postgres.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
public class FollowingDTO extends UserDTO {
    private boolean isFollowing;

    public FollowingDTO(final NUser user,
                        boolean isFollowing) {
        super(user);
        this.isFollowing = isFollowing;
    }

}
