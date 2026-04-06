package com.datn.datn_be.service;

import com.datn.datn_be.dto.ChatMessageResponse;
import com.datn.datn_be.dto.ChatRoomResponse;
import com.datn.datn_be.dto.ChatHandoffRequest;
import com.datn.datn_be.dto.PaginationResponse;
import com.datn.datn_be.dto.SendChatMessageRequest;
import com.datn.datn_be.entity.ChatMessage;
import com.datn.datn_be.entity.ChatRoom;
import com.datn.datn_be.entity.User;
import com.datn.datn_be.entity.UserRole;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.repository.ChatMessageRepository;
import com.datn.datn_be.repository.ChatRoomRepository;
import com.datn.datn_be.repository.RoleRepository;
import com.datn.datn_be.repository.UserRepository;
import com.datn.datn_be.repository.UserRoleRepository;
import com.datn.datn_be.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int ROLE_ADMIN_ID = 1;
    private static final String ROLE_USER = "USER";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String MESSAGE_TYPE_USER = "USER";
    private static final String MESSAGE_TYPE_ADMIN = "ADMIN";
    private static final String MESSAGE_TYPE_BOT = "BOT";
    private static final String MESSAGE_TYPE_SYSTEM = "SYSTEM";
    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_CLOSED = "CLOSED";
    private static final UUID BOT_SENDER_ID = new UUID(0L, 1L);
    private static final UUID SYSTEM_SENDER_ID = new UUID(0L, 2L);

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final AdminNotificationService adminNotificationService;

    @Transactional
    public ChatRoomResponse createOrGetMyRoom(String authHeader) {
        UUID userId = getUserIdFromHeader(authHeader);
        ChatRoom room = chatRoomRepository.findFirstByUserIdAndStatusOrderByUpdatedAtDesc(userId, STATUS_OPEN)
                .orElseGet(() -> createRoomForUser(userId));
        return mapRoom(room);
    }

    public ChatRoomResponse getMyRoom(String authHeader) {
        UUID userId = getUserIdFromHeader(authHeader);
        ChatRoom room = chatRoomRepository.findFirstByUserIdAndStatusOrderByUpdatedAtDesc(userId, STATUS_OPEN)
                .orElseThrow(() -> new ClientSideException(404, "Bạn chưa có phòng chat"));
        return mapRoom(room);
    }

    public List<ChatMessageResponse> getMyRoomMessages(String authHeader, int limit) {
        UUID userId = getUserIdFromHeader(authHeader);
        ChatRoom room = chatRoomRepository.findFirstByUserIdAndStatusOrderByUpdatedAtDesc(userId, STATUS_OPEN)
                .orElseThrow(() -> new ClientSideException(404, "Bạn chưa có phòng chat"));
        return getMessagesByRoom(room.getId(), limit);
    }

    @Transactional
    public ChatMessageResponse sendMyMessage(String authHeader, SendChatMessageRequest request) {
        UUID userId = getUserIdFromHeader(authHeader);
        String content = normalizeContent(request.getContent());

        ChatRoom room = chatRoomRepository.findFirstByUserIdAndStatusOrderByUpdatedAtDesc(userId, STATUS_OPEN)
                .orElseGet(() -> createRoomForUser(userId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy người dùng"));

        if (room.getAdminId() == null) {
            room.setAdminId(pickDefaultAdminId().orElse(null));
        }

        ChatMessage msg = new ChatMessage();
        msg.setRoomId(room.getId());
        msg.setSenderId(userId);
        msg.setSenderRole(ROLE_USER);
        msg.setMessageType(MESSAGE_TYPE_USER);
        msg.setSenderName(displayName(user));
        msg.setContent(content);
        msg.setRead(false);
        msg.setCreatedAt(Instant.now());
        ChatMessage saved = chatMessageRepository.save(msg);

        room.setLastMessage(content);
        room.setLastMessageAt(saved.getCreatedAt());
        room.setUpdatedAt(Instant.now());
        room.setUnreadAdminCount((room.getUnreadAdminCount() == null ? 0 : room.getUnreadAdminCount()) + 1);
        chatRoomRepository.save(room);

        adminNotificationService.notifyChatMessageToAdmin(
                room.getId().toString(),
                displayName(user),
                content
        );

        if (!room.isHandoffRequested()) {
            String faqReply = findFaqReply(content);
            if (faqReply != null) {
                ChatMessage botMessage = new ChatMessage();
                botMessage.setRoomId(room.getId());
                botMessage.setSenderId(BOT_SENDER_ID);
                botMessage.setSenderRole(ROLE_ADMIN);
                botMessage.setMessageType(MESSAGE_TYPE_BOT);
                botMessage.setSenderName("FPOLY Bot");
                botMessage.setContent(faqReply);
                botMessage.setRead(false);
                botMessage.setCreatedAt(Instant.now());
                ChatMessage botSaved = chatMessageRepository.save(botMessage);

                room.setLastMessage(faqReply);
                room.setLastMessageAt(botSaved.getCreatedAt());
                room.setUpdatedAt(Instant.now());
                room.setUnreadUserCount((room.getUnreadUserCount() == null ? 0 : room.getUnreadUserCount()) + 1);
                chatRoomRepository.save(room);
            }
        }

        return mapMessage(saved);
    }

    @Transactional
    public ChatRoomResponse requestHandoff(String authHeader, ChatHandoffRequest request) {
        UUID userId = getUserIdFromHeader(authHeader);
        ChatRoom room = chatRoomRepository.findFirstByUserIdAndStatusOrderByUpdatedAtDesc(userId, STATUS_OPEN)
                .orElseGet(() -> createRoomForUser(userId));

        if (room.isHandoffRequested()) {
            return mapRoom(room);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy người dùng"));

        if (room.getAdminId() == null) {
            room.setAdminId(pickDefaultAdminId().orElse(null));
        }

        String reason = request != null ? normalizeHandoffReason(request.getReason()) : "Muốn gặp nhân viên";
        Instant handoffTime = Instant.now();

        room.setHandoffRequested(true);
        room.setHandoffAt(handoffTime);

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRoomId(room.getId());
        systemMessage.setSenderId(SYSTEM_SENDER_ID);
        systemMessage.setSenderRole(ROLE_ADMIN);
        systemMessage.setMessageType(MESSAGE_TYPE_SYSTEM);
        systemMessage.setSenderName("Hệ thống");
        systemMessage.setContent("Người dùng đã yêu cầu hỗ trợ nhân viên. Lý do: " + reason);
        systemMessage.setRead(false);
        systemMessage.setCreatedAt(handoffTime);
        ChatMessage savedSystemMessage = chatMessageRepository.save(systemMessage);

        room.setLastMessage(savedSystemMessage.getContent());
        room.setLastMessageAt(savedSystemMessage.getCreatedAt());
        room.setUpdatedAt(Instant.now());
        room.setUnreadAdminCount((room.getUnreadAdminCount() == null ? 0 : room.getUnreadAdminCount()) + 1);
        chatRoomRepository.save(room);

        adminNotificationService.notifyChatHandoffToAdmin(
                room.getId().toString(),
                displayName(user),
                reason
        );

        return mapRoom(room);
    }

    @Transactional
    public void markMyRoomAsRead(String authHeader) {
        UUID userId = getUserIdFromHeader(authHeader);
        ChatRoom room = chatRoomRepository.findFirstByUserIdAndStatusOrderByUpdatedAtDesc(userId, STATUS_OPEN)
                .orElseThrow(() -> new ClientSideException(404, "Bạn chưa có phòng chat"));

        room.setUnreadUserCount(0);
        room.setUpdatedAt(Instant.now());
        chatRoomRepository.save(room);

        List<ChatMessage> all = getRawMessages(room.getId(), 200);
        List<ChatMessage> needUpdate = new ArrayList<>();
        for (ChatMessage msg : all) {
            if (ROLE_ADMIN.equals(msg.getSenderRole()) && !msg.isRead()) {
                msg.setRead(true);
                needUpdate.add(msg);
            }
        }
        if (!needUpdate.isEmpty()) {
            chatMessageRepository.saveAll(needUpdate);
        }
    }

    public long getMyUnreadCount(String authHeader) {
        UUID userId = getUserIdFromHeader(authHeader);
        return chatRoomRepository.countByUserIdAndStatusAndUnreadUserCountGreaterThan(userId, STATUS_OPEN, 0);
    }

    public PaginationResponse<ChatRoomResponse> adminGetRooms(String authHeader, int page, int pageSize, String status) {
        UUID adminId = getAdminIdFromHeader(authHeader);

        Page<ChatRoom> roomPage;
        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            roomPage = chatRoomRepository.findAllByOrderByUpdatedAtDesc(PageRequest.of(page, pageSize));
        } else {
            roomPage = chatRoomRepository.findByStatusOrderByUpdatedAtDesc(status.toUpperCase(), PageRequest.of(page, pageSize));
        }

        List<ChatRoomResponse> content = roomPage.getContent().stream().map(this::mapRoom).toList();
        return new PaginationResponse<>(
                page,
                pageSize,
                roomPage.getTotalElements(),
                roomPage.getTotalPages(),
                content,
                roomPage.hasNext(),
                roomPage.hasPrevious()
        );
    }

    public List<ChatMessageResponse> adminGetRoomMessages(String authHeader, String roomId, int limit) {
        getAdminIdFromHeader(authHeader);
        UUID roomUUID = parseUuid(roomId);
        chatRoomRepository.findById(roomUUID)
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy phòng chat"));
        return getMessagesByRoom(roomUUID, limit);
    }

    @Transactional
    public ChatMessageResponse adminSendMessage(String authHeader, String roomId, SendChatMessageRequest request) {
        UUID adminId = getAdminIdFromHeader(authHeader);
        String content = normalizeContent(request.getContent());

        UUID roomUUID = parseUuid(roomId);
        ChatRoom room = chatRoomRepository.findById(roomUUID)
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy phòng chat"));

        if (STATUS_CLOSED.equalsIgnoreCase(room.getStatus())) {
            room.setStatus(STATUS_OPEN);
        }

        User adminUser = userRepository.findById(adminId)
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy admin"));

        if (room.getAdminId() == null) {
            room.setAdminId(adminId);
        }

        ChatMessage msg = new ChatMessage();
        msg.setRoomId(roomUUID);
        msg.setSenderId(adminId);
        msg.setSenderRole(ROLE_ADMIN);
        msg.setMessageType(MESSAGE_TYPE_ADMIN);
        msg.setSenderName(displayName(adminUser));
        msg.setContent(content);
        msg.setRead(false);
        msg.setCreatedAt(Instant.now());
        ChatMessage saved = chatMessageRepository.save(msg);

        room.setLastMessage(content);
        room.setLastMessageAt(saved.getCreatedAt());
        room.setUpdatedAt(Instant.now());
        room.setUnreadUserCount((room.getUnreadUserCount() == null ? 0 : room.getUnreadUserCount()) + 1);
        chatRoomRepository.save(room);

        User roomUser = userRepository.findById(room.getUserId())
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy user trong phòng chat"));

        adminNotificationService.notifyChatMessageToUser(
                roomUser.getId().toString(),
                displayName(adminUser),
                content
        );

        return mapMessage(saved);
    }

    @Transactional
    public void adminMarkRoomAsRead(String authHeader, String roomId) {
        getAdminIdFromHeader(authHeader);
        UUID roomUUID = parseUuid(roomId);
        ChatRoom room = chatRoomRepository.findById(roomUUID)
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy phòng chat"));

        room.setUnreadAdminCount(0);
        room.setUpdatedAt(Instant.now());
        chatRoomRepository.save(room);

        List<ChatMessage> all = getRawMessages(roomUUID, 200);
        List<ChatMessage> needUpdate = new ArrayList<>();
        for (ChatMessage msg : all) {
            if (ROLE_USER.equals(msg.getSenderRole()) && !msg.isRead()) {
                msg.setRead(true);
                needUpdate.add(msg);
            }
        }
        if (!needUpdate.isEmpty()) {
            chatMessageRepository.saveAll(needUpdate);
        }
    }

    @Transactional
    public void adminCloseRoom(String authHeader, String roomId) {
        getAdminIdFromHeader(authHeader);
        ChatRoom room = chatRoomRepository.findById(parseUuid(roomId))
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy phòng chat"));
        room.setStatus(STATUS_CLOSED);
        room.setUnreadUserCount(0);
        room.setUnreadAdminCount(0);
        room.setUpdatedAt(Instant.now());
        chatRoomRepository.save(room);
    }

    @Transactional
    public void adminReopenRoom(String authHeader, String roomId) {
        getAdminIdFromHeader(authHeader);
        ChatRoom room = chatRoomRepository.findById(parseUuid(roomId))
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy phòng chat"));
        room.setStatus(STATUS_OPEN);
        room.setUpdatedAt(Instant.now());
        chatRoomRepository.save(room);
    }

    public long adminUnreadRoomsCount(String authHeader) {
        getAdminIdFromHeader(authHeader);
        return chatRoomRepository.countByUnreadAdminCountGreaterThan(0);
    }

    private ChatRoom createRoomForUser(UUID userId) {
        ChatRoom room = new ChatRoom();
        room.setUserId(userId);
        room.setAdminId(pickDefaultAdminId().orElse(null));
        room.setStatus(STATUS_OPEN);
        room.setLastMessage(null);
        room.setLastMessageAt(null);
        room.setUnreadUserCount(0);
        room.setUnreadAdminCount(0);
        room.setHandoffRequested(false);
        room.setHandoffAt(null);
        room.setCreatedAt(Instant.now());
        room.setUpdatedAt(Instant.now());
        return chatRoomRepository.save(room);
    }

    private List<ChatMessageResponse> getMessagesByRoom(UUID roomId, int limit) {
        int safeLimit = limit <= 0 ? 50 : Math.min(limit, 200);
        Page<ChatMessage> page = chatMessageRepository.findByRoomId(
                roomId,
                PageRequest.of(0, safeLimit, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        List<ChatMessage> list = new ArrayList<>(page.getContent());
        list.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        return list.stream().map(this::mapMessage).toList();
    }

    private List<ChatMessage> getRawMessages(UUID roomId, int limit) {
        int safeLimit = limit <= 0 ? 100 : Math.min(limit, 500);
        Page<ChatMessage> page = chatMessageRepository.findByRoomId(
                roomId,
                PageRequest.of(0, safeLimit, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return page.getContent();
    }

    private String normalizeContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new ClientSideException(400, "Nội dung tin nhắn không được để trống");
        }
        String trimmed = content.trim();
        if (trimmed.length() > 2000) {
            throw new ClientSideException(400, "Tin nhắn tối đa 2000 ký tự");
        }
        return trimmed;
    }

    private UUID getUserIdFromHeader(String authHeader) {
        String token = authHeader != null && authHeader.startsWith("Bearer ")
                ? authHeader.substring(7)
                : authHeader;
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null || userId.isBlank()) {
            throw new ClientSideException(401, "Token không hợp lệ");
        }
        return parseUuid(userId);
    }

    private UUID getAdminIdFromHeader(String authHeader) {
        UUID userId = getUserIdFromHeader(authHeader);
        List<UserRole> roles = userRoleRepository.findByUserId(userId);
        boolean isAdmin = roles.stream().anyMatch(this::isAdminRole);
        if (!isAdmin) {
            throw new ClientSideException(403, "Bạn không có quyền truy cập chức năng chat admin");
        }
        return userId;
    }

    private Optional<UUID> pickDefaultAdminId() {
        return userRoleRepository.findAll()
                .stream()
                .filter(this::isAdminRole)
                .map(UserRole::getUserId)
                .findFirst();
    }

    private boolean isAdminRole(UserRole userRole) {
        if (userRole == null || userRole.getRoleId() == null) {
            return false;
        }

        if (ROLE_ADMIN_ID == userRole.getRoleId()) {
            return true;
        }

        return roleRepository.findById(userRole.getRoleId())
                .map(role -> role.getName() != null && role.getName().toUpperCase().contains(ROLE_ADMIN))
                .orElse(false);
    }

    private ChatRoomResponse mapRoom(ChatRoom room) {
        User user = userRepository.findById(room.getUserId()).orElse(null);
        User admin = room.getAdminId() != null ? userRepository.findById(room.getAdminId()).orElse(null) : null;

        return new ChatRoomResponse(
                room.getId().toString(),
                room.getUserId() != null ? room.getUserId().toString() : null,
                user != null ? user.getUsername() : null,
                user != null ? user.getFullName() : null,
                room.getAdminId() != null ? room.getAdminId().toString() : null,
                admin != null ? displayName(admin) : null,
                room.getStatus(),
                room.isHandoffRequested(),
                room.getHandoffAt(),
                room.getLastMessage(),
                room.getLastMessageAt(),
                room.getUnreadUserCount() == null ? 0 : room.getUnreadUserCount(),
                room.getUnreadAdminCount() == null ? 0 : room.getUnreadAdminCount(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }

    private ChatMessageResponse mapMessage(ChatMessage msg) {
        return new ChatMessageResponse(
                msg.getId().toString(),
                msg.getRoomId().toString(),
                msg.getSenderId().toString(),
                msg.getSenderRole(),
                msg.getMessageType(),
                msg.getSenderName(),
                msg.getContent(),
                msg.isRead(),
                msg.getCreatedAt()
        );
    }

    private String normalizeHandoffReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return "Muốn gặp nhân viên";
        }
        String trimmed = reason.trim();
        if (trimmed.length() > 500) {
            return trimmed.substring(0, 500);
        }
        return trimmed;
    }

    private String findFaqReply(String userContent) {
        if (userContent == null || userContent.isBlank()) {
            return null;
        }

        String text = userContent.toLowerCase(Locale.ROOT);

        if (containsAny(text, "ship", "vận chuyển", "giao hàng", "bao lâu", "mấy ngày")) {
            return "Shop giao hàng nội thành từ 1-2 ngày và liên tỉnh từ 3-5 ngày làm việc. Bạn có thể theo dõi trạng thái trong mục Đơn hàng.";
        }
        if (containsAny(text, "đổi trả", "hoàn tiền", "trả hàng", "bảo hành")) {
            return "Bạn có thể yêu cầu đổi trả trong 7 ngày nếu sản phẩm còn đủ điều kiện. Vào Đơn hàng > Chi tiết đơn > Yêu cầu hỗ trợ để được xử lý nhanh.";
        }
        if (containsAny(text, "thanh toán", "vnpay", "cod", "trả góp")) {
            return "Hiện tại shop hỗ trợ COD và VNPay. Khi checkout, bạn chọn phương thức thanh toán phù hợp trước khi đặt hàng.";
        }
        if (containsAny(text, "đơn hàng", "kiểm tra đơn", "tra cứu", "order")) {
            return "Bạn vào mục Đơn hàng để xem trạng thái chi tiết. Nếu cần hỗ trợ nhanh hơn, bấm Gặp nhân viên để được tiếp nhận.";
        }
        if (containsAny(text, "xin chào", "chào", "hello", "hi", "shop")) {
            return "Chào bạn, mình là trợ lý tự động của FPOLY. Bạn có thể hỏi về giao hàng, thanh toán, đổi trả, hoặc bấm Gặp nhân viên để được hỗ trợ trực tiếp.";
        }

        return null;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String displayName(User user) {
        if (user == null) return "Unknown";
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName();
        }
        return user.getUsername();
    }

    private UUID parseUuid(String raw) {
        try {
            return UUID.fromString(raw);
        } catch (Exception e) {
            throw new ClientSideException(400, "UUID không hợp lệ");
        }
    }
}
