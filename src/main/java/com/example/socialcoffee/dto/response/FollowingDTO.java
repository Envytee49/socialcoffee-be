package com.example.socialcoffee.dto.response;


import com.example.socialcoffee.domain.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
public class FollowingDTO extends UserDTO {
    private boolean isFollowing;

    public FollowingDTO(final User user) {
        super(user);
    }

    public FollowingDTO(final User user,
                        boolean isFollowing) {
        super(user);
        this.isFollowing = isFollowing;
    }

}
