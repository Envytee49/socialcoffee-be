package com.example.socialcoffee.dto.request;

import com.example.socialcoffee.dto.common.AddressDTO;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDTO {
    @Size(max = 100)
    private String displayName;

    @Size(max = 500)
    private String bio;

    @Size(max = 100)
    private String coffeePreference;

    @Pattern(regexp = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$")
    private String phone;

    private LocalDate dob;

    @Size(max = 50)
    private String gender;

    private AddressDTO address;
}
