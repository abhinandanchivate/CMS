package com.securebank.beneficiary;

import com.securebank.beneficiary.dto.BeneficiaryListResponse;
import com.securebank.beneficiary.dto.CreateBeneficiaryRequest;
import com.securebank.beneficiary.dto.CreateBeneficiaryResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping
    public BeneficiaryListResponse listBeneficiaries() {
        return beneficiaryService.listBeneficiaries();
    }

    @PostMapping
    public ResponseEntity<CreateBeneficiaryResponse> addBeneficiary(@Valid @RequestBody CreateBeneficiaryRequest request) {
        CreateBeneficiaryResponse response = beneficiaryService.addBeneficiary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
