package com.insurance.service;

import com.insurance.domain.Claim;
import com.insurance.domain.ClaimStatus;
import com.insurance.domain.Policy;
import com.insurance.domain.Urgency;
import com.insurance.dto.ClaimResponse;
import com.insurance.dto.CreateClaimRequest;
import com.insurance.exception.InvalidRequestException;
import com.insurance.exception.PolicyNotFoundException;
import com.insurance.repo.ClaimRepository;
import com.insurance.repo.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class ClaimService {
    private final ClaimRepository claimRepository;
    private final PolicyRepository policyRepository;

    public ClaimService(ClaimRepository claimRepository, PolicyRepository policyRepository) {
        this.claimRepository = claimRepository;
        this.policyRepository = policyRepository;
    }

    @Transactional
    public List<ClaimResponse> getClaimsForPolicy(String policyNo) {
        if (!policyRepository.existsByPolicyNo(policyNo)) {
            throw new PolicyNotFoundException(policyNo);
        }
        return claimRepository.findByPolicy_PolicyNoOrderByClaimNoAsc(policyNo).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ClaimResponse createClaim(CreateClaimRequest request) {
        Policy policy = policyRepository.findByPolicyNo(request.policyNo())
                .orElseThrow(() -> new PolicyNotFoundException(request.policyNo()));
        Urgency urgency;
        try {
            urgency = Urgency.valueOf(request.urgency().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException("Invalid claim urgency: " + request.urgency());
        }

        Claim claim = new Claim();
        claim.setClaimNo(String.format(Locale.ROOT, "CLM-%02d", claimRepository.count() + 1));
        claim.setAmount(request.claimAmount());
        claim.setUrgency(urgency);
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setPolicy(policy);
        return toResponse(claimRepository.save(claim));
    }

    private ClaimResponse toResponse(Claim claim) {
        return new ClaimResponse(
                claim.getClaimNo(),
                claim.getPolicy().getPolicyNo(),
                claim.getAmount(),
                claim.getUrgency().name(),
                claim.getStatus().name()
        );
    }
}
