package com.securebank.account;

import com.securebank.account.dto.AccountBalanceDto;
import com.securebank.account.dto.AccountListResponse;
import com.securebank.account.dto.AccountSummaryDto;
import com.securebank.account.dto.AccountTransactionDto;
import com.securebank.account.dto.AccountTransactionPage;
import com.securebank.common.security.CurrentUserService;
import com.securebank.domain.account.Account;
import com.securebank.domain.account.AccountRepository;
import com.securebank.domain.account.AccountTransaction;
import com.securebank.domain.account.AccountTransactionRepository;
import com.securebank.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public AccountService(AccountRepository accountRepository,
                          AccountTransactionRepository transactionRepository,
                          CurrentUserService currentUserService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public AccountListResponse listAccounts() {
        User user = currentUserService.requireCurrentUser();
        List<AccountSummaryDto> accounts = accountRepository.findByUser(user).stream()
                .map(account -> new AccountSummaryDto(
                        account.getId().toString(),
                        account.getType().name(),
                        account.getNumberMask(),
                        account.getIfsc(),
                        account.getBranch(),
                        new AccountBalanceDto(account.getCurrency(), account.getBalance(), account.getUpdatedAt())
                ))
                .collect(Collectors.toList());
        return new AccountListResponse(accounts);
    }

    @Transactional(readOnly = true)
    public AccountTransactionPage transactions(String accountId, LocalDate from, LocalDate to, int page, int size) {
        User user = currentUserService.requireCurrentUser();
        UUID accountUuid = UUID.fromString(accountId);
        Account account = accountRepository.findByIdAndUser(accountUuid, user)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountTransaction> result;
        Instant fromInstant = from != null ? from.atStartOfDay().toInstant(ZoneOffset.UTC) : Instant.EPOCH;
        Instant toInstant = to != null ? to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusSeconds(1) : Instant.now();
        result = transactionRepository.findByAccountAndPostedAtBetweenOrderByPostedAtDesc(account, fromInstant, toInstant, pageable);
        List<AccountTransactionDto> content = result.getContent().stream()
                .map(txn -> new AccountTransactionDto(
                        txn.getId().toString(),
                        txn.getType().name(),
                        txn.getNarration(),
                        txn.getAmount(),
                        account.getCurrency(),
                        txn.getPostedAt(),
                        txn.getBalanceAfter()
                ))
                .collect(Collectors.toList());
        return new AccountTransactionPage(content, result.getNumber(), result.getSize(), result.getTotalElements());
    }
}
