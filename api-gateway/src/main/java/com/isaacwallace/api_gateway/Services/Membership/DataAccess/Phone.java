package com.isaacwallace.api_gateway.Services.Membership.DataAccess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phone {
    private String number;
    private PhoneType type;
}
