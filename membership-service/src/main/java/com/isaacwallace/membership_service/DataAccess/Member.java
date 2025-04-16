package com.isaacwallace.membership_service.DataAccess;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private MemberIdentifier memberIdentifier;

    private String first_name;
    private String last_name;
    private String email;

    @Embedded
    private Address address;

    @Embedded
    private Phone phone;
}
