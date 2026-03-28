package com.example.demo.service;

import com.example.demo.dao.MessageCenterDao;
import com.example.demo.entity.MessageCenterMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageCenterService {
    @Autowired
    private MessageCenterDao messageCenterDao;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void init() {
        messageCenterDao.ensureMessageCenterTable();
    }

    public Map<String, Object> getFeed(String username, String role, Integer limit) {
        Map<String, Object> result = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "用户名不能为空");
            return result;
        }
        int fetchLimit = limit == null || limit < 1 ? 200 : Math.min(limit, 500);

        List<Map<String, Object>> items = new ArrayList<>();

        try {
            List<MessageCenterMessage> userMessages = messageCenterDao.listMessagesForUser(username, fetchLimit);
            for (MessageCenterMessage msg : userMessages) {
                items.add(buildMessageItem(msg));
            }

            if (isManager(role)) {
                List<Map<String, Object>> logs = messageCenterDao.listSystemLogs(fetchLimit);
                for (Map<String, Object> log : logs) {
                    items.add(buildLogItem(log));
                }

                List<Map<String, Object>> violations = messageCenterDao.listViolations(fetchLimit);
                for (Map<String, Object> violation : violations) {
                    items.add(buildViolationItem(violation));
                }
            }
        } catch (Exception e) {
            System.err.println("消息中心加载失败: " + e.getMessage());
        }

        items.sort(Comparator.comparingLong((Map<String, Object> o) -> {
            Object value = o.get("sortTime");
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return 0L;
        }).reversed());
        for (Map<String, Object> item : items) {
            item.remove("sortTime");
        }

        result.put("success", true);
        Map<String, Object> data = new HashMap<>();
        data.put("list", items);
        result.put("data", data);
        return result;
    }

    public Map<String, Object> sendMessage(String sender, String scope, String receiver, String title, String content, String level) {
        Map<String, Object> result = new HashMap<>();
        if (sender == null || sender.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "发送人不能为空");
            return result;
        }
        if (title == null || title.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "标题不能为空");
            return result;
        }
        if (content == null || content.trim().isEmpty()) {
            result.put("success", false);
            result.put("message", "内容不能为空");
            return result;
        }

        String normalizedScope = "all".equalsIgnoreCase(scope) ? "all" : "user";
        String normalizedLevel = normalizeLevel(level);

        if ("user".equals(normalizedScope)) {
            if (receiver == null || receiver.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "请选择接收用户");
                return result;
            }
        } else {
            receiver = null;
        }

        MessageCenterMessage message = new MessageCenterMessage();
        message.setSender(sender.trim());
        message.setReceiver(receiver == null ? null : receiver.trim());
        message.setScope(normalizedScope);
        message.setTitle(title.trim());
        message.setContent(content.trim());
        message.setLevel(normalizedLevel);

        try {
            int rows = messageCenterDao.insertMessage(message);
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "消息已发送");
            } else {
                result.put("success", false);
                result.put("message", "消息发送失败");
            }
        } catch (Exception e) {
            System.err.println("消息发送异常: " + e.getMessage());
            result.put("success", false);
            result.put("message", "消息发送异常: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Object> listRecipients() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> users = messageCenterDao.listActiveUsers();
        result.put("success", true);
        Map<String, Object> data = new HashMap<>();
        data.put("list", users);
        result.put("data", data);
        return result;
    }

    private Map<String, Object> buildMessageItem(MessageCenterMessage msg) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "msg-" + msg.getId());
        map.put("title", msg.getTitle());
        map.put("content", msg.getContent());
        map.put("time", formatTime(msg.getCreatedAt()));
        map.put("type", mapLevelToType(msg.getLevel()));
        map.put("level", msg.getLevel());
        map.put("source", "message");
        map.put("scope", msg.getScope());
        map.put("sender", msg.getSender());
        map.put("receiver", msg.getReceiver());
        List<String> tags = new ArrayList<>();
        if ("all".equalsIgnoreCase(msg.getScope())) {
            tags.add("announcement");
            tags.add("system");
        } else {
            tags.add("direct");
        }
        map.put("tags", tags);
        map.put("sortTime", msg.getCreatedAt() == null ? 0L : msg.getCreatedAt().getTime());
        return map;
    }

    private Map<String, Object> buildLogItem(Map<String, Object> log) {
        Map<String, Object> map = new HashMap<>();
        Object id = log.get("id");
        String operation = valueOf(log.get("operation"));
        String operator = valueOf(log.get("operator"));
        String content = valueOf(log.get("content"));
        String ip = valueOf(log.get("ip_address"));
        String status = valueOf(log.get("status"));
        Timestamp time = toTimestamp(log.get("operate_time"));

        map.put("id", "log-" + (id == null ? "0" : id.toString()));
        map.put("title", "系统日志：" + toOperationLabel(operation));
        map.put("content", content + "（操作人：" + operator + "，IP：" + ip + "，结果：" + toStatusLabel(status) + "）");
        map.put("time", formatTime(time));
        map.put("type", "system");
        map.put("source", "system_log");
        map.put("sender", operator);
        map.put("tags", java.util.Arrays.asList("system", "log"));
        map.put("sortTime", time == null ? 0L : time.getTime());
        return map;
    }

    private Map<String, Object> buildViolationItem(Map<String, Object> violation) {
        Map<String, Object> map = new HashMap<>();
        Object id = violation.get("id");
        String plate = valueOf(violation.get("plate_number"));
        String illegalType = valueOf(violation.get("illegal_type"));
        String imagePath = valueOf(violation.get("image_path"));
        Object confidenceObj = violation.get("confidence");
        String confidence = confidenceObj == null ? "-" : String.format("%.2f", ((Number) confidenceObj).doubleValue());
        Timestamp time = toTimestamp(violation.get("created_at"));

        map.put("id", "vio-" + (id == null ? "0" : id.toString()));
        map.put("title", "违规车辆：" + (plate.isEmpty() ? "未知车牌" : plate));
        map.put("content", "违规类型：" + toIllegalTypeLabel(illegalType) + "，置信度：" + confidence + "%" +
                (imagePath.isEmpty() ? "" : "，图片文件：" + imagePath));
        map.put("time", formatTime(time));
        map.put("type", "alert");
        map.put("source", "violation");
        map.put("sender", "系统识别");
        map.put("tags", java.util.Arrays.asList("vehicle", "violation", "urgent"));
        map.put("sortTime", time == null ? 0L : time.getTime());
        return map;
    }

    private boolean isManager(String role) {
        if (role == null) {
            return false;
        }
        return "manager".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role);
    }

    private String formatTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return sdf.format(timestamp);
    }

    private Timestamp toTimestamp(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }
        if (value instanceof java.util.Date) {
            return new Timestamp(((java.util.Date) value).getTime());
        }
        if (value instanceof java.time.LocalDateTime) {
            return Timestamp.valueOf((java.time.LocalDateTime) value);
        }
        if (value instanceof java.time.OffsetDateTime) {
            return Timestamp.from(((java.time.OffsetDateTime) value).toInstant());
        }
        if (value instanceof java.time.Instant) {
            return Timestamp.from((java.time.Instant) value);
        }
        return null;
    }

    private String normalizeLevel(String level) {
        if (level == null) {
            return "info";
        }
        String normalized = level.trim().toLowerCase();
        if (normalized.isEmpty()) {
            return "info";
        }
        if (!java.util.Arrays.asList("info", "warning", "alert", "success").contains(normalized)) {
            return "info";
        }
        return normalized;
    }

    private String mapLevelToType(String level) {
        if (level == null) {
            return "info";
        }
        switch (level.toLowerCase()) {
            case "alert":
                return "alert";
            case "warning":
                return "warning";
            case "success":
                return "success";
            default:
                return "info";
        }
    }

    private String valueOf(Object value) {
        return value == null ? "" : value.toString();
    }

    private String toStatusLabel(String status) {
        if (status == null) {
            return "未知";
        }
        if ("success".equalsIgnoreCase(status) || "成功".equals(status)) {
            return "成功";
        }
        if ("fail".equalsIgnoreCase(status) || "失败".equals(status)) {
            return "失败";
        }
        return status;
    }

    private String toOperationLabel(String operation) {
        if (operation == null || operation.trim().isEmpty()) {
            return "未知操作";
        }
        switch (operation.toLowerCase()) {
            case "login":
                return "登录";
            case "add":
                return "新增";
            case "edit":
                return "编辑";
            case "delete":
                return "删除";
            case "query":
                return "查询";
            case "update":
                return "修改";
            default:
                return operation;
        }
    }

    private String toIllegalTypeLabel(String type) {
        if (type == null || type.trim().isEmpty()) {
            return "未知";
        }
        switch (type) {
            case "red_light":
                return "闯红灯";
            case "wrong_way":
                return "逆行";
            case "line_press":
                return "压线";
            case "illegal_parking":
                return "违停";
            case "helmet":
                return "未戴头盔";
            default:
                return type;
        }
    }
}
