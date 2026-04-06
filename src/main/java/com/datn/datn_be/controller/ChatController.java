package com.datn.datn_be.controller;

import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.dto.ChatHandoffRequest;
import com.datn.datn_be.dto.ChatMessageResponse;
import com.datn.datn_be.dto.ChatRoomResponse;
import com.datn.datn_be.dto.ChatUnreadCountResponse;
import com.datn.datn_be.dto.PaginationResponse;
import com.datn.datn_be.dto.SendChatMessageRequest;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    private <T> ResponseEntity<ApiResponse<T>> toErrorResponse(Exception e) {
        if (e instanceof ClientSideException clientSideException) {
            HttpStatus status = HttpStatus.resolve(clientSideException.getCode());
            if (status == null) {
                status = HttpStatus.BAD_REQUEST;
            }
            return ResponseEntity.status(status)
                    .body(new ApiResponse<>(clientSideException.getCode(), clientSideException.getMessage(), null));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, e.getMessage(), null));
    }

    @PostMapping("/my-room/create")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> createOrGetMyRoom(
            @RequestHeader("Authorization") String authHeader) {
        try {
            ChatRoomResponse response = chatService.createOrGetMyRoom(authHeader);
            return ResponseEntity.ok(new ApiResponse<>(0, "Tạo phòng chat thành công", response));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @GetMapping("/my-room")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> getMyRoom(
            @RequestHeader("Authorization") String authHeader) {
        try {
            ChatRoomResponse response = chatService.getMyRoom(authHeader);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy phòng chat thành công", response));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @GetMapping("/my-room/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMyRoomMessages(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "50") int limit) {
        try {
            List<ChatMessageResponse> response = chatService.getMyRoomMessages(authHeader, limit);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy tin nhắn thành công", response));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @PostMapping("/my-room/messages")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMyMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SendChatMessageRequest request) {
        try {
            ChatMessageResponse response = chatService.sendMyMessage(authHeader, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(0, "Gửi tin nhắn thành công", response));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @PostMapping("/my-room/handoff")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> requestHandoff(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) ChatHandoffRequest request) {
        try {
            ChatRoomResponse response = chatService.requestHandoff(authHeader, request);
            return ResponseEntity.ok(new ApiResponse<>(0, "Đã chuyển yêu cầu đến nhân viên", response));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @PutMapping("/my-room/read")
    public ResponseEntity<ApiResponse<Void>> markMyRoomAsRead(
            @RequestHeader("Authorization") String authHeader) {
        try {
            chatService.markMyRoomAsRead(authHeader);
            return ResponseEntity.ok(new ApiResponse<>(0, "Đã đánh dấu đã đọc", null));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @GetMapping("/my-room/unread-count")
    public ResponseEntity<ApiResponse<ChatUnreadCountResponse>> myUnreadCount(
            @RequestHeader("Authorization") String authHeader) {
        try {
            long unread = chatService.getMyUnreadCount(authHeader);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy số tin chưa đọc thành công", new ChatUnreadCountResponse(unread)));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @GetMapping("/admin/rooms")
    public ResponseEntity<ApiResponse<PaginationResponse<ChatRoomResponse>>> adminGetRooms(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "ALL") String status) {
        try {
            PaginationResponse<ChatRoomResponse> response = chatService.adminGetRooms(authHeader, page, pageSize, status);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy danh sách phòng chat thành công", response));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @GetMapping("/admin/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> adminGetRoomMessages(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String roomId,
            @RequestParam(defaultValue = "100") int limit) {
        try {
            List<ChatMessageResponse> response = chatService.adminGetRoomMessages(authHeader, roomId, limit);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy tin nhắn phòng chat thành công", response));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @PostMapping("/admin/rooms/{roomId}/messages")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> adminSendMessage(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String roomId,
            @RequestBody SendChatMessageRequest request) {
        try {
            ChatMessageResponse response = chatService.adminSendMessage(authHeader, roomId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(0, "Admin gửi tin nhắn thành công", response));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @PutMapping("/admin/rooms/{roomId}/read")
    public ResponseEntity<ApiResponse<Void>> adminMarkRoomAsRead(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String roomId) {
        try {
            chatService.adminMarkRoomAsRead(authHeader, roomId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Admin đã đánh dấu đã đọc", null));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @PutMapping("/admin/rooms/{roomId}/close")
    public ResponseEntity<ApiResponse<Void>> adminCloseRoom(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String roomId) {
        try {
            chatService.adminCloseRoom(authHeader, roomId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Đã đóng phòng chat", null));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @PutMapping("/admin/rooms/{roomId}/reopen")
    public ResponseEntity<ApiResponse<Void>> adminReopenRoom(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String roomId) {
        try {
            chatService.adminReopenRoom(authHeader, roomId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Đã mở lại phòng chat", null));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }

    @GetMapping("/admin/unread-rooms-count")
    public ResponseEntity<ApiResponse<ChatUnreadCountResponse>> adminUnreadRoomsCount(
            @RequestHeader("Authorization") String authHeader) {
        try {
            long unread = chatService.adminUnreadRoomsCount(authHeader);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy số phòng chưa đọc thành công", new ChatUnreadCountResponse(unread)));
        } catch (Exception e) {
            return toErrorResponse(e);
        }
    }
}
