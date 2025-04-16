package com.isaacwallace.api_gateway.Membership.Business;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Phone {
    private String number;

    private PhoneType type;
}
