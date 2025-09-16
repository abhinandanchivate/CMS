package com.securebank.transfer;

import com.securebank.common.model.TransferStatus;
import com.securebank.common.security.CurrentUserService;
import com.securebank.domain.account.Account;
import com.securebank.domain.account.AccountRepository;
import com.securebank.domain.beneficiary.Beneficiary;
import com.securebank.domain.beneficiary.BeneficiaryRepository;
import com.securebank.domain.transfer.Transfer;
import com.securebank.domain.transfer.TransferRepository;
import com.securebank.domain.user.User;
import com.securebank.transfer.dto.CreateTransferRequest;
import com.securebank.transfer.dto.CreateTransferResponse;
import com.securebank.transfer.dto.TransferStatusResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    public TransferService(TransferRepository transferRepository,
                           AccountRepository accountRepository,
                           BeneficiaryRepository beneficiaryRepository,
                           CurrentUserService currentUserService,
                           PasswordEncoder passwordEncoder) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.currentUserService = currentUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CreateTransferResponse initiateTransfer(CreateTransferRequest request) {
        User user = currentUserService.requireCurrentUser();
        Account fromAccount = accountRepository.findByIdAndUser(UUID.fromString(request.fromAccountId()), user)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndUser(UUID.fromString(request.to().beneficiaryId()), user)
                .orElseThrow(() -> new IllegalArgumentException("Beneficiary not found"));
        validatePin(user, request.userPin());
        Transfer transfer = new Transfer();
        transfer.setUser(user);
        transfer.setFromAccount(fromAccount);
        transfer.setBeneficiary(beneficiary);
        transfer.setAmount(request.amount());
        transfer.setCurrency(request.currency());
        transfer.setPurpose(request.purpose());
        transfer.setStatus(TransferStatus.PROCESSING);
        Transfer saved = transferRepository.save(transfer);
        return new CreateTransferResponse(
                saved.getId().toString(),
                saved.getStatus().name().toLowerCase(),
                saved.getAmount(),
                new CreateTransferResponse.Debit(fromAccount.getId().toString()),
                new CreateTransferResponse.Credit(beneficiary.getId().toString())
        );
    }

    @Transactional(readOnly = true)
    public TransferStatusResponse getStatus(String transferId) {
        User user = currentUserService.requireCurrentUser();
        Transfer transfer = transferRepository.findByIdAndUser(UUID.fromString(transferId), user)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found"));
        return new TransferStatusResponse(transfer.getId().toString(), transfer.getStatus().name().toLowerCase(), transfer.getReason());
    }

    private void validatePin(User user, String pin) {
        if (user.getTransactionPinHash() == null) {
            return;
        }
        if (!passwordEncoder.matches(pin, user.getTransactionPinHash())) {
            throw new IllegalArgumentException("pin_invalid");
        }
    }
}
