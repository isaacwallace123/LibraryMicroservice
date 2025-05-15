package com.isaacwallace.api_gateway.Services.Membership.Presentation;

import com.isaacwallace.api_gateway.Services.Membership.Business.MembershipService;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.Models.MembershipRequestModel;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.Models.MembershipResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/v1/members")
public class MembershipController {
    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MembershipResponseModel>> getMembers() {
        return ResponseEntity.status(HttpStatus.OK).body(this.membershipService.getAllMembers());
    }

    @GetMapping(value = "{memberid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MembershipResponseModel> getMemberById(@PathVariable String memberid) {
        return ResponseEntity.status(HttpStatus.OK).body(this.membershipService.getMemberById(memberid));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MembershipResponseModel> addMember(@RequestBody MembershipRequestModel membershipRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.membershipService.addMember(membershipRequestModel));
    }

    @PutMapping(value = "{memberid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MembershipResponseModel> updateMember(@PathVariable String memberid, @RequestBody MembershipRequestModel membershipRequestModel) {
        return ResponseEntity.status(HttpStatus.OK).body(this.membershipService.updateMember(memberid, membershipRequestModel));
    }

    @DeleteMapping(value = "{memberid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MembershipResponseModel> deleteMember(@PathVariable String memberid) {
        this.membershipService.deleteMember(memberid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
