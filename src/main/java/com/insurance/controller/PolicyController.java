package com.insurance.controller;

import com.insurance.dto.CreatePolicyRequest;
import com.insurance.dto.ClaimResponse;
import com.insurance.dto.PolicyResponse;
import com.insurance.service.ClaimService;
import com.insurance.service.PolicyService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
@Tag(name = "Policies", description = "Insurance policy operations")
public class PolicyController {
    private final PolicyService policyService;
    private final ClaimService claimService;

    public PolicyController(PolicyService policyService, ClaimService claimService) {
        this.policyService = policyService;
        this.claimService = claimService;
    }

    @GetMapping
    @Operation(summary = "List policies", description = "List all policies or filter by status, type, or customer.")
    @ApiResponse(responseCode = "200", description = "Policies returned")
    public List<PolicyResponse> getPolicies(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String customer
    ) {
        return policyService.getPolicies(status, type, customer);
    }

    @GetMapping("/search")
    @Operation(summary = "Search policies by minimum premium")
    @ApiResponse(responseCode = "200", description = "Matching policies returned")
    @ApiResponse(responseCode = "400", description = "Missing or invalid minimum premium")
    public List<PolicyResponse> searchPolicies(
            @RequestParam(required = false) Integer minPremium
    ) {
        return policyService.searchByMinimumPremium(minPremium);
    }

    @GetMapping("/{policyNo}")
    @Operation(summary = "Get a policy by policy number")
    @ApiResponse(responseCode = "200", description = "Policy returned")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public PolicyResponse getPolicy(@PathVariable String policyNo) {
        return policyService.getPolicy(policyNo);
    }

    @GetMapping("/{policyNo}/claims")
    @Operation(summary = "List claims for a policy")
    @ApiResponse(responseCode = "200", description = "Claims returned")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public List<ClaimResponse> getClaims(@PathVariable String policyNo) {
        return claimService.getClaimsForPolicy(policyNo);
    }

    @PostMapping
    @Operation(summary = "Create a policy")
    @ApiResponse(responseCode = "201", description = "Policy created")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "409", description = "Policy number already exists")
    public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody CreatePolicyRequest request) {
        PolicyResponse response = policyService.createPolicy(request);
        return ResponseEntity.created(URI.create("/api/policies/" + response.policyNo())).body(response);
    }

    @DeleteMapping("/{policyNo}")
    @Operation(summary = "Delete a policy")
    @ApiResponse(responseCode = "204", description = "Policy deleted")
    @ApiResponse(responseCode = "404", description = "Policy not found")
    public ResponseEntity<Void> deletePolicy(@PathVariable String policyNo) {
        policyService.deletePolicy(policyNo);
        return ResponseEntity.noContent().build();
    }
}
