package com.example.demo.controller;

import com.example.demo.service.MessageCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/message-center")
@CrossOrigin(origins = "*")
public class MessageCenterController {
    @Autowired
    private MessageCenterService messageCenterService;

    @GetMapping("/feed")
    public ResponseEntity<Map<String, Object>> getFeed(
            @RequestParam String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer limit
    ) {
        return ResponseEntity.ok(messageCenterService.getFeed(username, role, limit));
    }

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, String> payload) {
        String sender = payload.get("sender");
        String scope = payload.get("scope");
        String receiver = payload.get("receiver");
        String title = payload.get("title");
        String content = payload.get("content");
        String level = payload.get("level");
        return ResponseEntity.ok(messageCenterService.sendMessage(sender, scope, receiver, title, content, level));
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> listUsers() {
        return ResponseEntity.ok(messageCenterService.listRecipients());
    }
}
