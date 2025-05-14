package com.isaacwallace.transaction_service.DomainClient.Membership.Models;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Address {
    private String street;
    private String city;
    private String postal;
    private String province;

    public Address(@NotNull String street, @NotNull String city, @NotNull String postal, @NotNull String province) {
        this.street = street;
        this.city = city;
        this.postal = postal;
        this.province = province;
    }
}
