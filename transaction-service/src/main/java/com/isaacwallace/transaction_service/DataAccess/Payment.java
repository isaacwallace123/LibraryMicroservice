package com.isaacwallace.transaction_service.DataAccess;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class Payment {
    @Enumerated(EnumType.STRING)
    private Method method;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private Double amount;
}
