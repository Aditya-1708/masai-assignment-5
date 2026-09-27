package com.insurance.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(force = true)

@Entity
@Table(name = "policies")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_no", nullable = false, unique = true)
    private final String policyNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private final ProductType productType;


    @Column(name = "base_premium", nullable = false)
    private final int basePremium;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private final PolicyStatus status;


    @ManyToMany
    @JoinTable(
        name = "policy_riders",
        joinColumns = @JoinColumn(name = "policy_id"),
        inverseJoinColumns = @JoinColumn(name = "rider_id")
    )
    private List<Rider> riders = new ArrayList<>();


    @OneToMany(mappedBy = "policy")
    private List<Claim> claims;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private final Customer customer;
}
