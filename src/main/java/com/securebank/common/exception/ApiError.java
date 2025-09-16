package com.securebank.common.exception;

import java.util.Map;

public record ApiError(String error, String message, Map<String, String> fields, String traceId) {
}
