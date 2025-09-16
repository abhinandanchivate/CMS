package com.securebank.domain.beneficiary;

import com.securebank.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, UUID> {

    List<Beneficiary> findByUser(User user);

    Optional<Beneficiary> findByIdAndUser(UUID id, User user);
}
