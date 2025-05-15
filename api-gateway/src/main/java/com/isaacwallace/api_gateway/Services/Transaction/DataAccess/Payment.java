package com.isaacwallace.api_gateway.Services.Transaction.DataAccess;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Payment {
    private Method method;
    private Currency currency;
    private Double amount;
}
