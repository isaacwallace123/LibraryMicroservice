package com.isaacwallace.membership_service.DataAccess;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Integer> {
    Member findMemberByMemberIdentifier_Memberid(String memberid);

    boolean existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName, String lastName);
    boolean existsByEmailIgnoreCase(String email);
}
