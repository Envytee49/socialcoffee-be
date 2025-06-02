package com.example.socialcoffee.dto.request;

import com.example.socialcoffee.dto.common.AddressDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {
    private Long userId;

    @NotBlank(message = "display name must not be blank")
    private String displayName;

    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$")
    private String phone;

    private String dob;

    private String gender;

    private AddressDTO address;
}
