package com.isaacwallace.membership_service.DataAccess;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class Address {
    private String street;
    private String city;
    private String postal;
    private String province;
}
