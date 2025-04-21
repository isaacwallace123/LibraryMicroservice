package com.isaacwallace.membership_service.Business;

import com.isaacwallace.membership_service.DataAccess.Member;
import com.isaacwallace.membership_service.DataAccess.MemberIdentifier;
import com.isaacwallace.membership_service.DataAccess.MemberRepository;
import com.isaacwallace.membership_service.DataAccess.PhoneType;
import com.isaacwallace.membership_service.Mapper.MemberRequestMapper;
import com.isaacwallace.membership_service.Mapper.MemberResponseMapper;
import com.isaacwallace.membership_service.Presentation.Models.MemberRequestModel;
import com.isaacwallace.membership_service.Presentation.Models.MemberResponseModel;
import com.isaacwallace.membership_service.Utils.Exceptions.DuplicateResourceException;
import com.isaacwallace.membership_service.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.membership_service.Utils.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final MemberResponseMapper memberResponseMapper;
    private final MemberRequestMapper memberRequestMapper;

    public MemberServiceImpl(MemberRepository memberRepository, MemberResponseMapper memberResponseMapper, MemberRequestMapper memberRequestMapper) {
        this.memberRepository = memberRepository;
        this.memberResponseMapper = memberResponseMapper;
        this.memberRequestMapper = memberRequestMapper;
    }

    private void validateMemberRequestModel(MemberRequestModel model) {
        if (model.getFirstName() == null || model.getFirstName().isBlank()) {
            throw new InvalidInputException("Invalid firstName: " + model.getFirstName());
        }
        if (model.getLastName() == null || model.getLastName().isBlank()) {
            throw new InvalidInputException("Invalid lastName: " + model.getLastName());
        }

        if (model.getEmail() == null || model.getEmail().isBlank()) {
            throw new InvalidInputException("Invalid email: " + model.getEmail());
        }

        if (model.getPhone() == null) {
            throw new InvalidInputException("Invalid phone: Input missing.");
        }
        if (model.getAddress() == null) {
            throw new InvalidInputException("Invalid address: Input missing.");
        }

        if (model.getAddress().getStreet() == null || model.getAddress().getStreet().isBlank()) {
            throw new InvalidInputException("Invalid street: " + model.getAddress().getStreet());
        }
        if (model.getAddress().getCity() == null || model.getAddress().getCity().isBlank()) {
            throw new InvalidInputException("Invalid city: " + model.getAddress().getCity());
        }
        if (model.getAddress().getProvince() == null || model.getAddress().getProvince().isBlank()) {
            throw new InvalidInputException("Invalid state: " + model.getAddress().getProvince());
        }
        if (model.getAddress().getPostal() == null || model.getAddress().getPostal().isBlank()) {
            throw new InvalidInputException("Invalid zip: " + model.getAddress().getPostal());
        }

        if (model.getPhone().getNumber() == null || model.getPhone().getNumber().isBlank()) {
            throw new InvalidInputException("Invalid phone number: " + model.getPhone().getNumber());
        }

        try {
            PhoneType.valueOf(model.getPhone().getType().toString());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid title enum: " + model.getPhone().getType());
        }

        if (this.memberRepository.existsByFirstNameIgnoreCaseAndLastNameIgnoreCase(model.getFirstName(), model.getLastName())) {
            throw new DuplicateResourceException("Duplicate member: " + model.getFirstName() + " " + model.getLastName());
        }

        if (this.memberRepository.existsByEmailIgnoreCase(model.getEmail())) {
            throw new DuplicateResourceException("Duplicate email: " + model.getEmail());
        }
    }

    private Member getMemberObjectById(String memberid) {
        try {
            UUID.fromString(memberid);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid memberid: " + memberid);
        }

        Member member = this.memberRepository.findMemberByMemberIdentifier_Memberid(memberid);

        if (member == null) {
            throw new NotFoundException("Unknown memberid: " + memberid);
        }

        return member;
    }

    public List<MemberResponseModel> getAllMembers() {
        return this.memberResponseMapper.entityToResponseModelList(this.memberRepository.findAll());
    }

    public MemberResponseModel getMemberById(String memberid) {
        Member member = this.getMemberObjectById(memberid);

        return this.memberResponseMapper.entityToResponseModel(member);
    }

    public MemberResponseModel addMember(MemberRequestModel memberRequestModel) {
        Member member = this.memberRequestMapper.requestModelToEntity(memberRequestModel, new MemberIdentifier());

        this.validateMemberRequestModel(memberRequestModel);

        return this.memberResponseMapper.entityToResponseModel(this.memberRepository.save(member));
    }

    public MemberResponseModel updateMember(String memberid, MemberRequestModel memberRequestModel) {
        Member member = this.getMemberObjectById(memberid);

        this.validateMemberRequestModel(memberRequestModel);

        this.memberRequestMapper.updateEntityFromRequest(memberRequestModel, member);

        Member updatedMember = this.memberRepository.save(member);

        return this.memberResponseMapper.entityToResponseModel(updatedMember);
    }

    public void deleteMember(String memberid) {
        Member member = this.getMemberObjectById(memberid);

        this.memberRepository.delete(member);
    }
}
