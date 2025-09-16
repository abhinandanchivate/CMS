package com.securebank.account;

import com.securebank.account.dto.AccountListResponse;
import com.securebank.account.dto.AccountTransactionPage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public AccountListResponse listAccounts() {
        return accountService.listAccounts();
    }

    @GetMapping("/{accountId}/transactions")
    public AccountTransactionPage transactions(@PathVariable String accountId,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                               @RequestParam(defaultValue = "0") @Min(0) int page,
                                               @RequestParam(defaultValue = "25") @Min(1) @Max(100) int size) {
        return accountService.transactions(accountId, from, to, page, size);
    }
}
