package com.example.demo.dao;

import com.example.demo.entity.EquipmentReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EquipmentReservationDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 查询预约列表（分页+关联查询）
    public List<EquipmentReservation> findAll(String keyword, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        String sql = "SELECT er.*, e.name as equipment_name, u.username as user_name, " +
                "au.username as approved_by_name " +
                "FROM equipment_reservations er " +
                "LEFT JOIN equipment e ON er.equipment_id = e.id " +
                "LEFT JOIN users u ON er.user_id = u.id " +
                "LEFT JOIN users au ON er.approved_by = au.id " +
                "WHERE (e.name LIKE ? OR u.username LIKE ?) ";

        if (status != null && !status.isEmpty()) {
            sql += "AND er.status = ? ";
        }

        sql += "ORDER BY er.created_at DESC LIMIT ? OFFSET ?";

        if (status != null && !status.isEmpty()) {
            return jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(EquipmentReservation.class),
                    "%" + keyword + "%", "%" + keyword + "%", status, pageSize, offset);
        } else {
            return jdbcTemplate.query(sql,
                    new BeanPropertyRowMapper<>(EquipmentReservation.class),
                    "%" + keyword + "%", "%" + keyword + "%", pageSize, offset);
        }
    }

    // 查询总数
    public int count(String keyword, String status) {
        String sql = "SELECT COUNT(*) FROM equipment_reservations er " +
                "LEFT JOIN equipment e ON er.equipment_id = e.id " +
                "LEFT JOIN users u ON er.user_id = u.id " +
                "WHERE (e.name LIKE ? OR u.username LIKE ?) ";

        if (status != null && !status.isEmpty()) {
            sql += "AND er.status = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class,
                    "%" + keyword + "%", "%" + keyword + "%", status);
        } else {
            sql += "AND 1=1";
            return jdbcTemplate.queryForObject(sql, Integer.class,
                    "%" + keyword + "%", "%" + keyword + "%");
        }
    }

    // 根据ID查询
    public EquipmentReservation findById(Integer id) {
        String sql = "SELECT er.*, e.name as equipment_name, u.username as user_name, " +
                "au.username as approved_by_name " +
                "FROM equipment_reservations er " +
                "LEFT JOIN equipment e ON er.equipment_id = e.id " +
                "LEFT JOIN users u ON er.user_id = u.id " +
                "LEFT JOIN users au ON er.approved_by = au.id " +
                "WHERE er.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(EquipmentReservation.class), id);
        } catch (Exception e) {
            return null;
        }
    }

    // 新增预约
    public int insert(EquipmentReservation reservation) {
        String sql = "INSERT INTO equipment_reservations (user_id, equipment_id, reservation_date, " +
                "start_time, end_time, quantity, purpose, status, comment, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, reservation.getUserId());
            ps.setInt(2, reservation.getEquipmentId());
            ps.setObject(3, reservation.getReservationDate());
            ps.setObject(4, reservation.getStartTime());
            ps.setObject(5, reservation.getEndTime());
            ps.setInt(6, reservation.getQuantity());
            ps.setString(7, reservation.getPurpose());
            ps.setString(8, reservation.getStatus());
            ps.setString(9, reservation.getComment());
            ps.setObject(10, LocalDateTime.now());
            ps.setObject(11, LocalDateTime.now());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // 更新预约状态
    public int updateStatus(Integer id, String status, String comment, Integer approvedBy) {
        String sql = "UPDATE equipment_reservations SET status = ?, comment = ?, approved_by = ?, " +
                "approved_at = ?, updated_at = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, comment, approvedBy, LocalDateTime.now(), LocalDateTime.now(), id);
    }

    // 取消预约
    public int cancelReservation(Integer id, String comment, Integer cancelledBy) {
        String sql = "UPDATE equipment_reservations SET status = 'CANCELLED', comment = ?, " +
                "cancelled_by = ?, cancelled_at = ?, updated_at = ? WHERE id = ?";
        return jdbcTemplate.update(sql, comment, cancelledBy, LocalDateTime.now(), LocalDateTime.now(), id);
    }

    // 删除预约
    public int delete(Integer id) {
        String sql = "DELETE FROM equipment_reservations WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // 获取我的预约记录
    public List<EquipmentReservation> findMyReservations(Integer userId, String status) {
        String sql = "SELECT er.*, e.name as equipment_name, u.username as user_name " +
                "FROM equipment_reservations er " +
                "LEFT JOIN equipment e ON er.equipment_id = e.id " +
                "LEFT JOIN users u ON er.user_id = u.id " +
                "WHERE er.user_id = ? ";

        if (status != null && !status.isEmpty()) {
            sql += "AND er.status = ? ";
        }
        
        sql += "ORDER BY er.reservation_date DESC, er.start_time DESC";
        
        if (status != null && !status.isEmpty()) {
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(EquipmentReservation.class), userId, status);
        } else {
            return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(EquipmentReservation.class), userId);
        }
    }

    // 检查时间冲突
    public boolean hasTimeConflict(Integer equipmentId, String reservationDate, String startTime, String endTime, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM equipment_reservations " +
                "WHERE equipment_id = ? AND reservation_date = ? " +
                "AND status IN ('PENDING', 'APPROVED') " +
                "AND ((start_time <= ? AND end_time > ?) OR (start_time < ? AND end_time >= ?) OR (start_time >= ? AND end_time <= ?)) ";

        List<Object> params = new ArrayList<>();
        params.add(equipmentId);
        params.add(reservationDate);
        params.add(startTime);
        params.add(startTime);
        params.add(endTime);
        params.add(endTime);
        params.add(startTime);
        params.add(endTime);

        if (excludeId != null) {
            sql += "AND id != ? ";
            params.add(excludeId);
        }

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, params.toArray());
        return count != null && count > 0;
    }

    // 获取某日期的预约情况
    public List<Map<String, Object>> getReservationsByDate(Integer equipmentId, String reservationDate) {
        String sql = "SELECT start_time, end_time, quantity, status " +
                "FROM equipment_reservations " +
                "WHERE equipment_id = ? AND reservation_date = ? " +
                "AND status IN ('PENDING', 'APPROVED') " +
                "ORDER BY start_time";

        return jdbcTemplate.query(sql, new Object[]{equipmentId, reservationDate}, (rs, rowNum) -> {
            Map<String, Object> reservation = new HashMap<>();
            reservation.put("startTime", rs.getString("start_time"));
            reservation.put("endTime", rs.getString("end_time"));
            reservation.put("quantity", rs.getInt("quantity"));
            reservation.put("status", rs.getString("status"));
            return reservation;
        });
    }

    // 获取器材的预约统计
    public Map<String, Object> getReservationStats(Integer equipmentId) {
        String sql = "SELECT " +
                "COUNT(*) as total_reservations, " +
                "SUM(CASE WHEN status = 'APPROVED' THEN 1 ELSE 0 END) as approved_reservations, " +
                "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as pending_reservations, " +
                "SUM(CASE WHEN status = 'REJECTED' THEN 1 ELSE 0 END) as rejected_reservations, " +
                "SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled_reservations " +
                "FROM equipment_reservations WHERE equipment_id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{equipmentId}, (rs, rowNum) -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalReservations", rs.getInt("total_reservations"));
            stats.put("approvedReservations", rs.getInt("approved_reservations"));
            stats.put("pendingReservations", rs.getInt("pending_reservations"));
            stats.put("rejectedReservations", rs.getInt("rejected_reservations"));
            stats.put("cancelledReservations", rs.getInt("cancelled_reservations"));
            return stats;
        });
    }

    // 批量更新预约状态
    public int batchUpdateStatus(List<Integer> ids, String status, String comment, Integer approvedBy) {
        String sql = "UPDATE equipment_reservations SET status = ?, comment = ?, approved_by = ?, " +
                "approved_at = ?, updated_at = ? WHERE id = ?";

        List<Object[]> batchArgs = new ArrayList<>();
        for (Integer id : ids) {
            batchArgs.add(new Object[]{status, comment, approvedBy, LocalDateTime.now(), LocalDateTime.now(), id});
        }

        int[] results = jdbcTemplate.batchUpdate(sql, batchArgs);

        // 返回成功更新的记录数
        int successCount = 0;
        for (int result : results) {
            if (result > 0) {
                successCount++;
            }
        }
        return successCount;
    }
}