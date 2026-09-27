package com.insurance.config;

import com.insurance.domain.Policy;
import com.insurance.domain.PolicyStatus;
import com.insurance.domain.ProductType;
import com.insurance.repo.ClaimRepository;
import com.insurance.repo.CustomerRepository;
import com.insurance.repo.PolicyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
@Order(2)
public class StartupSummaryRunner implements CommandLineRunner {
    private final Environment environment;
    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final ClaimRepository claimRepository;

    public StartupSummaryRunner(
            Environment environment,
            PolicyRepository policyRepository,
            CustomerRepository customerRepository,
            ClaimRepository claimRepository
    ) {
        this.environment = environment;
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
        this.claimRepository = claimRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void run(String... args) {
        String[] profiles = environment.getActiveProfiles();
        String profile = profiles.length > 0
                ? profiles[0]
                : environment.getDefaultProfiles()[0];

        System.out.println("Active profile → " + profile);
        System.out.println("Policy count → " + policyRepository.count());
        System.out.println("Customer count → " + customerRepository.count());

        Policy policy1004 = policyRepository.findByPolicyNo("HDFC-LIFE-1004").orElseThrow();
        System.out.println("Lookup HDFC-LIFE-1004 customer → " + policy1004.getCustomer().getFullName());
        System.out.println("Active count → "
                + policyRepository.findByStatusOrderByPolicyNoAsc(PolicyStatus.ACTIVE).size());
        System.out.println("TERM count → "
                + policyRepository.findByProductTypeOrderByPolicyNoAsc(ProductType.TERM).size());
        System.out.println("Anita Sharma policy count → "
                + policyRepository.findByCustomer_FullNameOrderByPolicyNoAsc("Anita Sharma").size());

        String premiumPolicyNumbers = policyRepository.findWithPremiumAtLeast(20000).stream()
                .map(Policy::getPolicyNo)
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
        System.out.println("minPremium=20000 policy numbers → " + premiumPolicyNumbers);

        Policy policy1001 = policyRepository.findByPolicyNo("HDFC-LIFE-1001").orElseThrow();
        String riderCodes = policy1001.getRiders().stream()
                .map(rider -> rider.getCode())
                .sorted()
                .reduce((first, second) -> first + ", " + second)
                .orElse("");
        System.out.println("HDFC-LIFE-1001 rider codes → " + riderCodes);
    }
}
