package com.insurance.config;


import com.insurance.domain.*;
import com.insurance.repo.CustomerRepository;
import com.insurance.repo.PolicyRepository;
import com.insurance.repo.RiderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
@Order(1)
public class DataSeeder implements CommandLineRunner {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final RiderRepository riderRepository;


    public DataSeeder(PolicyRepository policyRepository, CustomerRepository customerRepository, RiderRepository riderRepository) {
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository;
        this.riderRepository = riderRepository;
    }

    @Transactional
    public void run(String... args) throws Exception {

        if (policyRepository.count() == 0) {
            //added customers to the database
            Customer anita = customerRepository.save(new Customer("Anita Sharma", "anita.sharma@life.example"));
            Customer rahul = customerRepository.save(new Customer("Rahul Mehta", "rahul.mehta@life.example"));
            Customer priya = customerRepository.save(new Customer("Priya Nair", "priya.nair@life.example"));
            Customer vikram = customerRepository.save(new Customer("Vikram Singh", "vikram.singh@hdfclife.example"));
            Customer sneha = customerRepository.save(new Customer("Sneha Patel", "sneha.patel@life.example"));

            //added policies to the database
            policyRepository.save(new Policy("HDFC-LIFE-1001", ProductType.TERM, 18500, PolicyStatus.ACTIVE, anita));
            policyRepository.save(new Policy("HDFC-LIFE-1002", ProductType.ULIP, 42000, PolicyStatus.ACTIVE, rahul));
            policyRepository.save(new Policy("HDFC-LIFE-1003", ProductType.ENDOWMENT, 27000, PolicyStatus.LAPSED, priya));
            policyRepository.save(new Policy("HDFC-LIFE-1004", ProductType.TERM, 15200, PolicyStatus.ACTIVE, vikram));
            policyRepository.save(new Policy("HDFC-LIFE-1005", ProductType.ULIP, 36000, PolicyStatus.ACTIVE, sneha));
            policyRepository.save(new Policy("HDFC-LIFE-1006", ProductType.ENDOWMENT, 22000, PolicyStatus.PENDING, anita));

            Policy policy1001 = policyRepository.findByPolicyNo("HDFC-LIFE-1001").orElseThrow();

            Rider accidentCover = riderRepository.findByCode("ACCIDENT_COVER").orElseThrow();

            Rider waiverOfPremium = riderRepository.findByCode("WAIVER_OF_PREMIUM").orElseThrow();

            policy1001.getRiders().add(accidentCover);
            policy1001.getRiders().add(waiverOfPremium);

            policyRepository.save(policy1001);

        }

    }

}
