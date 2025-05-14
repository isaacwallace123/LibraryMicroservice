package com.isaacwallace.transaction_service.DomainClient.Membership.Models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Phone {
    private String number;
    private PhoneType type;

    public Phone(@NotNull String number, @NotNull PhoneType type) {
        this.number = number;
        this.type = type;
    }
}
