package com.example.demo.dao;

import com.example.demo.entity.MessageCenterMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class MessageCenterDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void ensureMessageCenterTable() {
        String sql = "CREATE TABLE IF NOT EXISTS message_center (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "sender VARCHAR(64) NOT NULL," +
                "receiver VARCHAR(64) NULL," +
                "scope VARCHAR(16) NOT NULL," +
                "title VARCHAR(200) NOT NULL," +
                "content TEXT NOT NULL," +
                "level VARCHAR(16) DEFAULT 'info'," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            System.err.println("初始化消息中心表失败: " + e.getMessage());
        }
    }

    public List<MessageCenterMessage> listMessagesForUser(String username, int limit) {
        ensureMessageCenterTable();
        String sql = "SELECT id, sender, receiver, scope, title, content, level, created_at " +
                "FROM message_center " +
                "WHERE scope = 'all' OR receiver = ? " +
                "ORDER BY created_at DESC LIMIT ?";
        try {
            return jdbcTemplate.query(sql, new Object[]{username, limit}, new MessageRowMapper());
        } catch (Exception e) {
            System.err.println("查询消息中心失败: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public int insertMessage(MessageCenterMessage message) {
        ensureMessageCenterTable();
        String sql = "INSERT INTO message_center (sender, receiver, scope, title, content, level, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, NOW())";
        try {
            return jdbcTemplate.update(sql,
                message.getSender(),
                message.getReceiver(),
                message.getScope(),
                message.getTitle(),
                message.getContent(),
                message.getLevel()
            );
        } catch (Exception e) {
            System.err.println("写入消息中心失败: " + e.getMessage());
            return 0;
        }
    }

    public List<Map<String, Object>> listSystemLogs(int limit) {
        String sql = "SELECT id, operator, operation, content, operate_time, ip_address, status " +
                "FROM system_logs ORDER BY operate_time DESC LIMIT ?";
        try {
            return jdbcTemplate.queryForList(sql, limit);
        } catch (Exception e) {
            System.err.println("读取系统日志失败: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public List<Map<String, Object>> listViolations(int limit) {
        String sql = "SELECT id, plate_number, illegal_type, confidence, image_path, created_at " +
                "FROM car_list " +
                "WHERE illegal_type IS NOT NULL AND illegal_type <> '' " +
                "ORDER BY created_at DESC LIMIT ?";
        try {
            return jdbcTemplate.queryForList(sql, limit);
        } catch (Exception e) {
            System.err.println("读取违规车辆失败: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    public List<Map<String, Object>> listActiveUsers() {
        String sql = "SELECT id, username, real_name, role_id, status " +
                "FROM sys_user " +
                "WHERE status IS NULL OR status = 1 " +
                "ORDER BY id ASC";
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            System.err.println("读取用户列表失败: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private static class MessageRowMapper implements RowMapper<MessageCenterMessage> {
        @Override
        public MessageCenterMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
            MessageCenterMessage msg = new MessageCenterMessage();
            msg.setId(rs.getInt("id"));
            msg.setSender(rs.getString("sender"));
            msg.setReceiver(rs.getString("receiver"));
            msg.setScope(rs.getString("scope"));
            msg.setTitle(rs.getString("title"));
            msg.setContent(rs.getString("content"));
            msg.setLevel(rs.getString("level"));
            msg.setCreatedAt(rs.getTimestamp("created_at"));
            return msg;
        }
    }
}
