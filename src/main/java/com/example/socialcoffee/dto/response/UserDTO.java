package com.example.socialcoffee.dto.response;

import com.example.socialcoffee.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String displayName;
    private String username;
    private String profilePhoto;
    public UserDTO(User user) {
        this.id = user.getId();
        this.displayName = user.getDisplayName();
        this.username = user.getUsername();
        this.profilePhoto = user.getProfilePhoto();
    }
}
