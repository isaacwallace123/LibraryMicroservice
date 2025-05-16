package com.isaacwallace.api_gateway.Services.Transaction.DataAccess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Method method;
    private Currency currency;
    private Double amount;
}
