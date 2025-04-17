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

    private String firstName;
    private String lastName;
    private String email;

    @Embedded
    private Address address;

    @Embedded
    private Phone phone;
}
