package com.isaacwallace.membership_service.DataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setup() {
        memberRepository.deleteAll();
    }

    @Test
    void testMemberConstructorAndGetters() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        assertEquals("John", member.getFirstName());
        assertEquals("Doe", member.getLastName());
        assertEquals("l7V0o@example.com", member.getEmail());
        assertEquals("123 Main St", member.getAddress().getStreet());
        assertEquals("Anytown", member.getAddress().getCity());
        assertEquals("12345", member.getAddress().getPostal());
        assertEquals("CA", member.getAddress().getProvince());
        assertEquals("555-555-5555", member.getPhone().getNumber());
        assertEquals(PhoneType.MOBILE, member.getPhone().getType());
    }

    @Test
    void testPhoneConstructorAndGetters() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);

        assertEquals("555-555-5555", phone.getNumber());
        assertEquals(PhoneType.MOBILE, phone.getType());
    }

    @Test
    void testAddressConstructorAndGetters() {
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");

        assertEquals("123 Main St", address.getStreet());
        assertEquals("Anytown", address.getCity());
        assertEquals("12345", address.getPostal());
        assertEquals("CA", address.getProvince());
    }

    @Test
    void testSaveMember() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        memberRepository.save(member);

        assertEquals(1, memberRepository.count());
    }

    @Test
    void toStringContainsAllFields() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        memberRepository.save(member);

        String toString = member.toString();
        assertTrue(toString.contains(member.getFirstName()));
        assertTrue(toString.contains(member.getLastName()));
        assertTrue(toString.contains(member.getEmail()));
        assertTrue(toString.contains(member.getAddress().getStreet()));
        assertTrue(toString.contains(member.getAddress().getCity()));
        assertTrue(toString.contains(member.getAddress().getPostal()));
        assertTrue(toString.contains(member.getAddress().getProvince()));
        assertTrue(toString.contains(member.getPhone().getNumber()));
        assertTrue(toString.contains(member.getPhone().getType().toString()));
    }

    @Test
    void testEqualsSameObject() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        assertEquals(member, member);
    }

    @Test
    void testEqualsDifferentClass() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        assertNotEquals(member, "NotMember");
    }

    @Test
    void testEqualsNull() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        assertNotEquals(member, null);
    }

    @Test
    void testNotEqualsDifferentFields() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        Member member2 = new Member("Jane", "Doe", "l7V0o@example.com", address, phone);

        assertNotEquals(member, member2);
        assertNotEquals(member.hashCode(), member2.hashCode());
    }

    @Test
    void testEqualsDifferentId() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        Member member2 = new Member("Jane", "Doe", "l7V0o@example.com", address, phone);

        member1.setMemberIdentifier(new MemberIdentifier());
        member2.setMemberIdentifier(new MemberIdentifier());

        assertNotEquals(member1, member2);
        assertNotEquals(member1.hashCode(), member2.hashCode());
    }

    @Test
    void testHashCodeDifferentObjects() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        Member member2 = new Member("Jane", "Doe", "l7V0o@example.com", address, phone);
        assertNotEquals(member1.hashCode(), member2.hashCode());
    }

    @Test
    void testHashCodeConsistency() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        member.setMemberIdentifier(new MemberIdentifier("abc-123"));
        member.setId(1);

        int hash = member.hashCode();

        assertEquals(hash, member.hashCode());

        member.setFirstName("Jane");

        assertNotEquals(hash, member.hashCode());
    }

    @Test
    void whenMemberExists_thenReturnAllMembers() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        Member member2 = new Member("Jane", "Doe", "l7V0o@example.com", address, phone);

        memberRepository.save(member);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findAll();

        assertNotNull(members);
        assertNotEquals(0, members.size());
        assertEquals(2, members.size());
        assertEquals(members.size(), this.memberRepository.count());
    }

    @Test
    void testEquals_DifferentFirstName() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        Member member2 = new Member("Jane", "Doe", "l7V0o@example.com", address, phone);
        assertNotEquals(member1, member2);
    }

    @Test
    void testEquals_DifferentLastName() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        Member member2 = new Member("John", "Smith", "l7V0o@example.com", address, phone);
        assertNotEquals(member1, member2);
    }

    @Test
    void testEquals_DifferentEmail() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0@example.com", address, phone);
        Member member2 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        assertNotEquals(member1, member2);
    }

    @Test
    void testEquals_DifferentAddress() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        Member member2 = new Member("John", "Doe", "l7V0o@example.com", new Address("123 Main St", "Anytown", "12345", "NA"), phone);
        assertNotEquals(member1, member2);
    }

    @Test
    void testEquals_DifferentPhone() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        Member member2 = new Member("John", "Doe", "l7V0o@example.com", address, new Phone("555-555-5556", PhoneType.MOBILE));
        assertNotEquals(member1, member2);
    }

    @Test
    void testEquals_DifferentMemberIdentifier() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        Member member2 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        member1.setMemberIdentifier(new MemberIdentifier("abc-123"));
        member2.setMemberIdentifier(new MemberIdentifier("def-456"));
        assertNotEquals(member1, member2);
    }

    @Test
    void testEquals_MixedNullFields() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", null, "l7V0o@example.com", address, phone);
        Member member2 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        assertNotEquals(member1, member2);
    }

    @Test
    void whenMemberExists_thenReturnMemberByMemberId() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        member.setMemberIdentifier(new MemberIdentifier());
        memberRepository.save(member);

        Member foundMember = memberRepository.findMemberByMemberIdentifier_Memberid(member.getMemberIdentifier().getMemberid());

        assertNotNull(foundMember);

        assertEquals(member.getMemberIdentifier().getMemberid(), foundMember.getMemberIdentifier().getMemberid());

        assertEquals(member.getFirstName(), foundMember.getFirstName());
        assertEquals(member.getLastName(), foundMember.getLastName());
        assertEquals(member.getEmail(), foundMember.getEmail());
        assertEquals(member.getAddress(), foundMember.getAddress());
        assertEquals(member.getPhone(), foundMember.getPhone());
    }

    @Test
    void whenMemberDoesNotExist_thenReturnNull() {
        Member member = this.memberRepository.findMemberByMemberIdentifier_Memberid("00000000-0000-0000-0000-000000000000");

        assertNull(member);
    }

    @Test
    void whenValidEntitySaved_thenPersistentAndReturn() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        member.setMemberIdentifier(new MemberIdentifier());

        Member savedMember = this.memberRepository.save(member);

        assertNotNull(savedMember);
        assertNotNull(savedMember.getMemberIdentifier().getMemberid());

        assertEquals(member.getFirstName(), savedMember.getFirstName());
        assertEquals(member.getLastName(), savedMember.getLastName());
        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getAddress(), savedMember.getAddress());
        assertEquals(member.getPhone(), savedMember.getPhone());
    }

    @Test
    void whenValidEntityDeleted_thenPersistentAndReturn() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        member.setMemberIdentifier(new MemberIdentifier());

        this.memberRepository.save(member);
        this.memberRepository.delete(member);

        assertNull(this.memberRepository.findMemberByMemberIdentifier_Memberid(member.getMemberIdentifier().getMemberid()));
    }

    @Test
    void testExistsByFirstNameAndLastNameIgnoreCase() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        member.setMemberIdentifier(new MemberIdentifier());

        this.memberRepository.save(member);

        assertTrue(this.memberRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(member.getFirstName(), member.getLastName()));
        assertFalse(this.memberRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase("johnny", "doe"));
    }

    @Test
    void testExistsByEmailIgnoreCase() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        member.setMemberIdentifier(new MemberIdentifier());

        this.memberRepository.save(member);

        assertTrue(this.memberRepository.existsByEmailIgnoreCase(member.getEmail()));
        assertFalse(this.memberRepository.existsByEmailIgnoreCase("l7@example.com"));
    }

    @Test
    void testSavingDuplicateMembers() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        member1.setMemberIdentifier(new MemberIdentifier());
        Member member2 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        member2.setMemberIdentifier(new MemberIdentifier());

        this.memberRepository.save(member1);
        this.memberRepository.save(member2);

        List<Member> members = this.memberRepository.findAll();

        assertEquals(2, members.size());
    }

    @Test
    void testDeleteAllMembers() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        member1.setMemberIdentifier(new MemberIdentifier());
        Member member2 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        member2.setMemberIdentifier(new MemberIdentifier());

        this.memberRepository.save(member1);
        this.memberRepository.save(member2);

        this.memberRepository.deleteAll();

        List<Member> members = this.memberRepository.findAll();

        assertEquals(0, members.size());
    }

    @Test
    void testDefaultConstructorSetsIdCorrectly() {
        String testId = "testId";
        MemberIdentifier memberIdentifier = new MemberIdentifier(testId);

        assertNotNull(memberIdentifier);
        assertEquals(testId, memberIdentifier.getMemberid());
    }

    @Test
    void testTwoDefaultConstructorGenerateDifferentIds() {
        MemberIdentifier memberIdentifier1 = new MemberIdentifier();
        MemberIdentifier memberIdentifier2 = new MemberIdentifier();

        assertNotEquals(memberIdentifier1.getMemberid(), memberIdentifier2.getMemberid());
    }

    @Test
    void testMemberIdIsNotNullOrEmpty() {
        MemberIdentifier memberIdentifier = new MemberIdentifier();
        assertNotNull(memberIdentifier.getMemberid());
        assertNotEquals("", memberIdentifier.getMemberid());
    }

    @Test
    void testCustomIdCanbeUuidString() {
        String uuid = UUID.randomUUID().toString();
        MemberIdentifier memberIdentifier = new MemberIdentifier(uuid);

        assertDoesNotThrow(() -> UUID.fromString(memberIdentifier.getMemberid()));
        assertEquals(uuid, memberIdentifier.getMemberid());
    }

    @Test
    void testEqualsIdenticalValuesDifferentInstances() {
        MemberIdentifier memberid = new MemberIdentifier();

        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        Member member2 = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        member1.setMemberIdentifier(memberid);
        member2.setMemberIdentifier(memberid);

        assertEquals(member1, member2);
    }

    @Test
    void testEqualsDifferentMemberIdentifier() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        Member member2 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        member1.setMemberIdentifier(new MemberIdentifier("abc-123"));
        member2.setMemberIdentifier(new MemberIdentifier("def-456"));
        assertNotEquals(member1, member2);
    }

    @Test
    void testHashCodeConssistencyForSameState() {
        Phone phone = new Phone("555-555-5555", PhoneType.MOBILE);
        Address address = new Address("123 Main St", "Anytown", "12345", "CA");
        MemberIdentifier memberIdentifier = new MemberIdentifier("abc-123");
        Member member1 = new Member("John", "Doe", "l7V0o@example.com", address, phone);
        Member member2 = new Member("John", "Doe", "l7V0o@example.com", address, phone);

        member1.setMemberIdentifier(memberIdentifier);
        member2.setMemberIdentifier(memberIdentifier);

        assertEquals(member1.hashCode(), member2.hashCode());
    }

    @Test
    void testEqualsWithNullFields() {
        Member member1 = new Member();
        Member member2 = new Member();
        assertEquals(member1, member2);
    }

    @Test
    void testCanEqual() {
        Member member = new Member();

        assertTrue(member.canEqual(new Member()));
        assertFalse(member.canEqual("Not a member"));
    }
}