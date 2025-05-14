package com.isaacwallace.transaction_service.DataAccess;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Payment {
    private Method method;
    private Currency currency;
    private Double amount;
}
