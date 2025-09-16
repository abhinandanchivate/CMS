package com.securebank.transfer;

import com.securebank.transfer.dto.CreateTransferRequest;
import com.securebank.transfer.dto.CreateTransferResponse;
import com.securebank.transfer.dto.TransferStatusResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<CreateTransferResponse> initiateTransfer(@Valid @RequestBody CreateTransferRequest request) {
        CreateTransferResponse response = transferService.initiateTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{transferId}")
    public TransferStatusResponse getStatus(@PathVariable String transferId) {
        return transferService.getStatus(transferId);
    }
}
