package com.securebank.transfer.dto;

public record TransferStatusResponse(String transferId, String status, String reason) {
}
