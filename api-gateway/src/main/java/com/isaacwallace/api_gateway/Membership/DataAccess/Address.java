package com.isaacwallace.api_gateway.Membership.DataAccess;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Address {
    private String street;
    private String city;
    private String postal;
    private String province;
}
