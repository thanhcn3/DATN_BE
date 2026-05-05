package com.datn.datn_be.service;

import com.datn.datn_be.dto.OrderItemResponse;
import com.datn.datn_be.entity.Order;
import com.datn.datn_be.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@fpoly.edu.vn}")
    private String fromAddress;

    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    @Async
    public void sendOrderConfirmationEmail(User user, Order order,
                                           List<OrderItemResponse> items,
                                           User approver, Instant approvedAt) {
        if (user == null || user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Bo qua gui email don hang {} vi nguoi dung khong co email.", order.getId());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(user.getEmail());
            helper.setSubject("Don hang #" + shortId(order.getId().toString()).toUpperCase() + " da duoc xac nhan - FPOLY Store");
            helper.setText(buildHtml(user, order, items, approver, approvedAt != null ? approvedAt : Instant.now()), true);

            mailSender.send(message);
            log.info("Da gui email xac nhan don hang {} den {} (nguoi duyet: {})",
                    order.getId(), user.getEmail(),
                    approver != null ? displayName(approver) : "tu dong");
        } catch (Exception e) {
            log.error("Loi khi gui email xac nhan don hang {}: {}", order.getId(), e.getMessage(), e);
        }
    }

    private String buildHtml(User user, Order order, List<OrderItemResponse> items,
                             User approver, Instant approvedAt) {

        String customerName  = displayName(user);
        String orderDate     = order.getOrderDate() != null ? DISPLAY_FMT.format(order.getOrderDate()) : "--";
        String approvedAtStr = DISPLAY_FMT.format(approvedAt);
        String paymentLabel  = toPaymentLabel(order.getPaymentMethod());
        String shortOrderId  = shortId(order.getId().toString()).toUpperCase();

        StringBuilder productRows = new StringBuilder();
        for (OrderItemResponse item : items) {
            String name     = escapeHtml(item.getProductName() != null ? item.getProductName() : "San pham");
            int    qty      = item.getQuantity();
            BigDecimal unit = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            BigDecimal sub  = unit.multiply(BigDecimal.valueOf(qty));

            productRows.append("<tr>")
                .append("<td style='padding:10px 0;border-bottom:1px solid #f3f4f6;font-size:14px;color:#111827;'>")
                .append(name).append("</td>")
                .append("<td style='padding:10px 0;border-bottom:1px solid #f3f4f6;text-align:center;font-size:13px;color:#6b7280;white-space:nowrap;'>")
                .append("x").append(qty).append("</td>")
                .append("<td style='padding:10px 0;border-bottom:1px solid #f3f4f6;text-align:right;font-size:14px;font-weight:600;color:#111827;white-space:nowrap;'>")
                .append(formatVnd(sub)).append("</td>")
                .append("</tr>");
        }

        String shippingAddress = escapeHtml(order.getShippingAddress() != null ? order.getShippingAddress() : "--");
        String shippingPhone   = escapeHtml(order.getShippingPhone()   != null ? order.getShippingPhone()   : "--");
        String shippingName    = escapeHtml(order.getShippingName()    != null ? order.getShippingName()    : "--");

        return "<!DOCTYPE html>"
            + "<html lang='vi'><head><meta charset='UTF-8'>"
            + "<meta name='viewport' content='width=device-width,initial-scale=1'></head>"
            + "<body style='margin:0;padding:0;background:#f3f4f6;font-family:Arial,sans-serif;'>"
            + "<table width='100%' cellpadding='0' cellspacing='0' style='padding:32px 0;'>"
            + "<tr><td align='center'>"
            + "<table width='560' cellpadding='0' cellspacing='0'"
            + " style='background:#ffffff;border-radius:8px;border:1px solid #e5e7eb;overflow:hidden;'>"

            // Header
            + "<tr><td style='padding:20px 28px;border-bottom:3px solid #e11d48;'>"
            + "<span style='font-size:15px;font-weight:700;color:#e11d48;letter-spacing:0.5px;'>FPOLY Store</span>"
            + "</td></tr>"

            // Title + greeting
            + "<tr><td style='padding:24px 28px 16px;'>"
            + "<h2 style='margin:0 0 8px;font-size:18px;color:#111827;font-weight:700;'>Don hang da duoc xac nhan</h2>"
            + "<p style='margin:0;font-size:14px;color:#6b7280;line-height:1.6;'>"
            + "Xin chao <strong style='color:#111827;'>" + escapeHtml(customerName) + "</strong>, "
            + "don hang <strong>#" + shortOrderId + "</strong> cua ban da duoc xac nhan. "
            + "Chung toi se giao hang trong thoi gian som nhat."
            + "</p></td></tr>"

            // Order info
            + "<tr><td style='padding:0 28px 16px;'>"
            + "<table width='100%' cellpadding='0' cellspacing='0' style='border-top:1px solid #f3f4f6;'>"
            + row2col("Ma don hang",   "#" + shortOrderId, true)
            + row2col("Ngay dat hang", orderDate,          false)
            + row2col("Xac nhan luc",  approvedAtStr,      false)
            + row2col("Thanh toan",    paymentLabel,       false)
            + row2col("Giao cho",      shippingName,       false)
            + row2col("So dien thoai", shippingPhone,      false)
            + row2col("Dia chi",       shippingAddress,    false)
            + "</table></td></tr>"

            // Products
            + "<tr><td style='padding:0 28px 8px;'>"
            + "<p style='margin:0 0 8px;font-size:11px;font-weight:700;color:#9ca3af;text-transform:uppercase;letter-spacing:0.6px;'>San pham</p>"
            + "<table width='100%' cellpadding='0' cellspacing='0'>"
            + "<tr style='border-bottom:2px solid #e5e7eb;'>"
            + "<th style='padding:6px 0;font-size:11px;color:#9ca3af;font-weight:600;text-align:left;'>Ten san pham</th>"
            + "<th style='padding:6px 0;font-size:11px;color:#9ca3af;font-weight:600;text-align:center;'>SL</th>"
            + "<th style='padding:6px 0;font-size:11px;color:#9ca3af;font-weight:600;text-align:right;'>Thanh tien</th>"
            + "</tr>"
            + productRows
            + "</table></td></tr>"

            // Total
            + "<tr><td style='padding:12px 28px 24px;text-align:right;border-top:2px solid #f3f4f6;'>"
            + "<span style='font-size:13px;color:#6b7280;margin-right:12px;'>Tong cong</span>"
            + "<strong style='font-size:18px;color:#e11d48;'>" + formatVnd(order.getTotalAmount()) + "</strong>"
            + "</td></tr>"

            // Footer
            + "<tr><td style='padding:16px 28px;background:#f9fafb;border-top:1px solid #e5e7eb;text-align:center;'>"
            + "<p style='margin:0;font-size:11px;color:#9ca3af;'>Co thac mac? Lien he ho tro qua chat truc tiep tren website.</p>"
            + "<p style='margin:4px 0 0;font-size:11px;color:#d1d5db;'>© 2025 FPOLY Store</p>"
            + "</td></tr>"

            + "</table></td></tr></table>"
            + "</body></html>";
    }

    private String row2col(String label, String value, boolean bold) {
        String valStyle = bold
                ? "font-size:13px;color:#111827;font-weight:700;text-align:right;"
                : "font-size:13px;color:#111827;text-align:right;";
        return "<tr>"
             + "<td style='font-size:13px;color:#6b7280;padding:5px 0;'>" + escapeHtml(label) + "</td>"
             + "<td style='" + valStyle + "padding:5px 0;'>" + value + "</td>"
             + "</tr>";
    }

    private String toPaymentLabel(String method) {
        if (method == null) return "COD";
        return switch (method.toUpperCase()) {
            case "VNPAY" -> "VNPay (thanh toan online)";
            case "COD"   -> "COD (tien mat khi nhan hang)";
            default      -> escapeHtml(method);
        };
    }

    private String displayName(User user) {
        if (user == null) return "Khong ro";
        if (user.getFullName() != null && !user.getFullName().isBlank()) return user.getFullName();
        return user.getUsername();
    }

    private String shortId(String uuid) {
        return uuid.substring(0, Math.min(8, uuid.length()));
    }

    private String formatVnd(BigDecimal amount) {
        if (amount == null) return "0 VND";
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return nf.format(amount) + " VND";
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}