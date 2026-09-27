package com.insurance.repo;

import com.insurance.domain.Policy;
import com.insurance.domain.PolicyStatus;
import com.insurance.domain.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByPolicyNo(String policyNo);

    boolean existsByPolicyNo(String policyNo);

    List<Policy> findByStatusOrderByPolicyNoAsc(PolicyStatus status);

    List<Policy> findByProductTypeOrderByPolicyNoAsc(ProductType productType);

    List<Policy> findByCustomer_FullNameOrderByPolicyNoAsc(String fullName);

    List<Policy> findAllByOrderByPolicyNoAsc();

    @Query("""
        SELECT p
        FROM Policy p
        WHERE p.basePremium >= :minPremium
        ORDER BY p.basePremium DESC
        """)
    List<Policy> findWithPremiumAtLeast(
            @Param("minPremium") int minPremium
    );
}