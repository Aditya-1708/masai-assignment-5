package com.insurance.service;

import com.insurance.domain.Customer;
import com.insurance.domain.Policy;
import com.insurance.domain.PolicyStatus;
import com.insurance.domain.ProductType;
import com.insurance.dto.CreatePolicyRequest;
import com.insurance.dto.PolicyResponse;
import com.insurance.exception.DuplicatePolicyException;
import com.insurance.exception.InvalidRequestException;
import com.insurance.exception.PolicyNotFoundException;
import com.insurance.repo.CustomerRepository;
import com.insurance.repo.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;

    public PolicyService(PolicyRepository policyRepository, CustomerRepository customerRepository) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public List<PolicyResponse> getAllPolicies() {
        return policyRepository.findAllByOrderByPolicyNoAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PolicyResponse getPolicy(String policyNo) {
        return toResponse(findPolicy(policyNo));
    }

    @Transactional
    public List<PolicyResponse> getPolicies(String status, String type, String customer) {
        int providedFilters = (status == null ? 0 : 1)
                + (type == null ? 0 : 1)
                + (customer == null ? 0 : 1);
        if (providedFilters > 1) {
            throw new InvalidRequestException("Only one policy filter may be provided at a time");
        }
        if (status != null) {
            PolicyStatus policyStatus = switch (status) {
                case "Active" -> PolicyStatus.ACTIVE;
                case "Lapsed" -> PolicyStatus.LAPSED;
                case "Pending" -> PolicyStatus.PENDING;
                default -> throw new InvalidRequestException("Invalid policy status: " + status);
            };
            return policyRepository.findByStatusOrderByPolicyNoAsc(policyStatus).stream()
                    .map(this::toResponse)
                    .toList();
        }
        if (type != null) {
            ProductType productType;
            try {
                productType = ProductType.valueOf(type);
            } catch (IllegalArgumentException exception) {
                throw new InvalidRequestException("Invalid product type: " + type);
            }
            return policyRepository.findByProductTypeOrderByPolicyNoAsc(productType).stream()
                    .map(this::toResponse)
                    .toList();
        }
        if (customer != null) {
            return policyRepository.findByCustomer_FullNameOrderByPolicyNoAsc(customer).stream()
                    .map(this::toResponse)
                    .toList();
        }
        return getAllPolicies();
    }

    @Transactional
    public List<PolicyResponse> searchByMinimumPremium(Integer minPremium) {
        if (minPremium == null || minPremium < 0) {
            throw new InvalidRequestException("minPremium is required and must not be negative");
        }
        return policyRepository.findWithPremiumAtLeast(minPremium).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PolicyResponse createPolicy(CreatePolicyRequest request) {
        if (policyRepository.existsByPolicyNo(request.policyNo())) {
            throw new DuplicatePolicyException(request.policyNo());
        }

        ProductType productType = ProductType.valueOf(request.type());
        PolicyStatus status = switch (request.status()) {
            case "Active" -> PolicyStatus.ACTIVE;
            case "Lapsed" -> PolicyStatus.LAPSED;
            case "Pending" -> PolicyStatus.PENDING;
            default -> throw new InvalidRequestException("Invalid policy status: " + request.status());
        };

        Customer customer = customerRepository.findByEmail(request.email())
                .orElseGet(() -> customerRepository.save(
                        new Customer(request.customer(), request.email())
                ));
        Policy policy = new Policy(
                request.policyNo(),
                productType,
                request.basePremium(),
                status,
                customer
        );
        return toResponse(policyRepository.save(policy));
    }

    @Transactional
    public void deletePolicy(String policyNo) {
        Policy policy = findPolicy(policyNo);
        policyRepository.delete(policy);
    }

    private Policy findPolicy(String policyNo) {
        return policyRepository.findByPolicyNo(policyNo)
                .orElseThrow(() -> new PolicyNotFoundException(policyNo));
    }

    private PolicyResponse toResponse(Policy policy) {
        List<String> riderCodes = policy.getRiders().stream()
                .map(rider -> rider.getCode())
                .sorted()
                .toList();
        return new PolicyResponse(
                policy.getPolicyNo(),
                policy.getCustomer().getFullName(),
                policy.getCustomer().getEmail(),
                policy.getProductType().name(),
                policy.getBasePremium(),
                switch (policy.getStatus()) {
                    case ACTIVE -> "Active";
                    case LAPSED -> "Lapsed";
                    case PENDING -> "Pending";
                },
                riderCodes
        );
    }
}
