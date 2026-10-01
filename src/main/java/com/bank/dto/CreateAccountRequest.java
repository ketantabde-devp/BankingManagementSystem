package com.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank private String accountHolderName;
    @NotBlank @Pattern(regexp = "\\d{10,20}", message = "Account number must contain 10 to 20 digits")
    private String accountNumber;
}
