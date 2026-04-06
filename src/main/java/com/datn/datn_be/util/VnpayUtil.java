package com.datn.datn_be.util;

import com.datn.datn_be.configuration.VnpayConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class VnpayUtil {

    private static final Logger log = LoggerFactory.getLogger(VnpayUtil.class);
    private final VnpayConfig vnpayConfig;

    private static final DateTimeFormatter VN_DATE_FMT = DateTimeFormatter
            .ofPattern("yyyyMMddHHmmss")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    /**
     * Build redirect URL tới cổng VNPay.
     * Hash được tính trên chuỗi raw (key=value, không encode).
     * URL query string dùng value đã URL-encode UTF-8.
     */
    public String buildPaymentUrl(String txnRef, long amountVnd, String orderInfo, String clientIp) {
        // Validate credentials
        if (vnpayConfig.getTmnCode() == null || vnpayConfig.getTmnCode().isBlank()) {
            throw new RuntimeException("VNPay: tmn-code chưa được cấu hình trong application.yml");
        }
        if (vnpayConfig.getHashSecret() == null || vnpayConfig.getHashSecret().isBlank()) {
            throw new RuntimeException("VNPay: hash-secret chưa được cấu hình trong application.yml");
        }
        log.info("VNPAY buildUrl | tmnCode={} | txnRef={} | amount={} | returnUrl={}",
                vnpayConfig.getTmnCode(), txnRef, amountVnd * 100, vnpayConfig.getReturnUrl());

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", vnpayConfig.getVersion());
        params.put("vnp_Command", vnpayConfig.getCommand());
        params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amountVnd * 100));
        params.put("vnp_CurrCode", vnpayConfig.getCurrency());
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", vnpayConfig.getLocale());
        params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        params.put("vnp_IpAddr", clientIp);
        params.put("vnp_CreateDate", VN_DATE_FMT.format(Instant.now()));
        params.put("vnp_ExpireDate", VN_DATE_FMT.format(Instant.now().plusSeconds(900)));

        // Chuẩn VNPay: hash và URL đều dùng URLEncoder.encode(value, US_ASCII)
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            if (fieldValue != null && !fieldValue.isEmpty()) {
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);
                hashData.append(fieldName).append('=').append(encodedValue).append('&');
                query.append(fieldName).append('=').append(encodedValue).append('&');
            }
        }
        // Xóa dấu & cuối
        if (hashData.length() > 0) hashData.deleteCharAt(hashData.length() - 1);
        if (query.length() > 0) query.deleteCharAt(query.length() - 1);

        String secureHash = hmacSHA512(vnpayConfig.getHashSecret(), hashData.toString());
        String paymentUrl = vnpayConfig.getUrl() + "?" + query + "&vnp_SecureHash=" + secureHash;
        log.info("VNPAY hashData: {}", hashData);
        log.info("VNPAY paymentUrl: {}", paymentUrl);
        return paymentUrl;
    }

    /**
     * Xác minh chữ ký callback từ VNPay.
     * Params từ VNPay về là đã URL-decode (Spring tự decode), nên tính hash từ giá trị raw.
     */
    public boolean verifySignature(Map<String, String> params) {
        String vnpSecureHash = params.get("vnp_SecureHash");
        if (vnpSecureHash == null) return false;

        Map<String, String> sorted = new TreeMap<>(params);
        sorted.remove("vnp_SecureHash");
        sorted.remove("vnp_SecureHashType");

        // Encode lại giống lúc build — cùng logic với buildPaymentUrl
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            String fieldValue = entry.getValue();
            if (fieldValue != null && !fieldValue.isEmpty()) {
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);
                if (hashData.length() > 0) hashData.append('&');
                hashData.append(entry.getKey()).append('=').append(encodedValue);
            }
        }
        String computedHash = hmacSHA512(vnpayConfig.getHashSecret(), hashData.toString());
        log.info("VNPAY verify | received={} | computed={}", vnpSecureHash, computedHash);
        return computedHash.equalsIgnoreCase(vnpSecureHash);
    }

    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo HMAC-SHA512", e);
        }
    }
}

