package com.example.socialcoffee.dto.response;

import lombok.Data;

@Data
public class ContributorDTO {
    private UserDTO user;

    private Long contributionCount;
}
