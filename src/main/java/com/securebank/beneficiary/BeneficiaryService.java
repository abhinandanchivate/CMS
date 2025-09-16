package com.securebank.beneficiary;

import com.securebank.beneficiary.dto.BeneficiaryDto;
import com.securebank.beneficiary.dto.BeneficiaryListResponse;
import com.securebank.beneficiary.dto.CreateBeneficiaryRequest;
import com.securebank.beneficiary.dto.CreateBeneficiaryResponse;
import com.securebank.common.security.CurrentUserService;
import com.securebank.domain.beneficiary.Beneficiary;
import com.securebank.domain.beneficiary.BeneficiaryRepository;
import com.securebank.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final CurrentUserService currentUserService;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository, CurrentUserService currentUserService) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public BeneficiaryListResponse listBeneficiaries() {
        User user = currentUserService.requireCurrentUser();
        List<BeneficiaryDto> beneficiaries = beneficiaryRepository.findByUser(user).stream()
                .map(beneficiary -> new BeneficiaryDto(
                        beneficiary.getId().toString(),
                        beneficiary.getName(),
                        maskAccountNumber(beneficiary.getAccountNumber()),
                        beneficiary.getIfsc(),
                        beneficiary.getBankName(),
                        beneficiary.isVerified()
                ))
                .collect(Collectors.toList());
        return new BeneficiaryListResponse(beneficiaries);
    }

    @Transactional
    public CreateBeneficiaryResponse addBeneficiary(CreateBeneficiaryRequest request) {
        if (!request.accountNumber().equals(request.confirmAccountNumber())) {
            throw new IllegalArgumentException("Account numbers do not match");
        }
        User user = currentUserService.requireCurrentUser();
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUser(user);
        beneficiary.setName(request.name());
        beneficiary.setAccountNumber(request.accountNumber());
        beneficiary.setIfsc(request.ifsc());
        beneficiary.setBankName(request.bankName());
        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return CreateBeneficiaryResponse.pending(saved.getId().toString());
    }

    private String maskAccountNumber(String number) {
        if (number == null || number.length() < 4) {
            return "****";
        }
        String lastFour = number.substring(number.length() - 4);
        return "XXXXXXXX" + lastFour;
    }
}
