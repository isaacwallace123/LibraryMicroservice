package com.isaacwallace.transaction_service.DomainClient.Membership.Models;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class Phone {
    private String number;

    @Enumerated(EnumType.STRING)
    private PhoneType type;

    public Phone(@NotNull String number, @NotNull PhoneType type) {
        this.number = number;
        this.type = type;
    }
}
