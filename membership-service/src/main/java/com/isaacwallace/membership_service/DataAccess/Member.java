package com.isaacwallace.membership_service.DataAccess;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    public Member(@NotNull String firstName, @NotNull String lastName, @NotNull String email, @NotNull Address address, @NotNull Phone phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phone = phone;
    }
}
