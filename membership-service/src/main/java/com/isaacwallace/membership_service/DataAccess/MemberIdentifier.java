package com.isaacwallace.membership_service.DataAccess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class MemberIdentifier {
    @Column(name = "memberid")
    private String memberid;

    public MemberIdentifier() {
        this.memberid = UUID.randomUUID().toString();
    }

    public MemberIdentifier(String memberid) {
        this.memberid = memberid;
    }
}
