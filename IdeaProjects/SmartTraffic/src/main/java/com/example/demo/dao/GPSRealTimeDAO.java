package com.example.demo.dao;

import com.example.demo.entity.DeviceInfo;
import com.example.demo.entity.GPSRealTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GPSRealTimeDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String UPSERT_SQL = "INSERT INTO gps_realtime (" +
            "sn, lon, lat, dir, spe, gpstime, server_time, server_delay, car_id, tname, " +
            "chepai, stat, start, start_xf, address, gsm, gps, gpsend, cell, alonlat, " +
            "olonlat, vol, oil, bat, maxo, ranges, oldlon, oldlat, model, versions, " +
            "car_img, ctrl, port, simstate, vip_id, t_id, comm) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE " +
            "lon=VALUES(lon), lat=VALUES(lat), dir=VALUES(dir), spe=VALUES(spe), " +
            "gpstime=VALUES(gpstime), server_time=VALUES(server_time), server_delay=VALUES(server_delay), " +
            "tname=VALUES(tname), chepai=VALUES(chepai), stat=VALUES(stat), start=VALUES(start), " +
            "start_xf=VALUES(start_xf), address=VALUES(address), gsm=VALUES(gsm), gps=VALUES(gps), " +
            "gpsend=VALUES(gpsend), cell=VALUES(cell), alonlat=VALUES(alonlat), olonlat=VALUES(olonlat), " +
            "vol=VALUES(vol), oil=VALUES(oil), bat=VALUES(bat), maxo=VALUES(maxo), ranges=VALUES(ranges), " +
            "oldlon=VALUES(oldlon), oldlat=VALUES(oldlat), model=VALUES(model), versions=VALUES(versions), " +
            "car_img=VALUES(car_img), ctrl=VALUES(ctrl), port=VALUES(port), simstate=VALUES(simstate), " +
            "vip_id=VALUES(vip_id), t_id=VALUES(t_id), comm=VALUES(comm)";

    public void updateOrInsert(GPSRealTime data) {
        try {
            jdbcTemplate.update(UPSERT_SQL, ps -> {
                setPreparedStatement(ps, data);
            });
        } catch (DataAccessException e) {
            // 如果UPSERT失败，尝试普通插入
            String insertSql = UPSERT_SQL.substring(0, UPSERT_SQL.indexOf("ON DUPLICATE KEY UPDATE"));
            jdbcTemplate.update(insertSql, ps -> {
                setPreparedStatement(ps, data);
            });
        }
    }

    public void InsertGpsHistory(GPSRealTime data) {
        if (data == null || data.getGpstime() == null) {
            System.out.println("InsertGpsHistory: 数据为空或GPS时间为空");
            return;
        }

        try {
            // 历史表插入SQL（直接插入，不需要更新）
            String insertSql = "INSERT INTO gps_history (" +
                    "sn, lon, lat, dir, spe, gpstime, server_time, server_delay, car_id, tname, " +
                    "chepai, stat, start, start_xf, address, gsm, gps, gpsend, cell, alonlat, " +
                    "olonlat, vol, oil, bat, maxo, ranges, oldlon, oldlat, model, versions, " +
                    "car_img, ctrl, port, simstate, vip_id, t_id, comm) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(insertSql, ps -> {
                setPreparedStatement(ps, data);
            });

        } catch (DataAccessException e) {
            System.err.println("InsertGpsHistory 插入失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setPreparedStatement(PreparedStatement ps, GPSRealTime data) throws SQLException {
        int index = 1;
        ps.setLong(index++, data.getSn());
        ps.setDouble(index++, data.getLon());
        ps.setDouble(index++, data.getLat());
        ps.setInt(index++, data.getDir());
        ps.setInt(index++, data.getSpe());
        ps.setTimestamp(index++, data.getGpstime() != null ?
                new java.sql.Timestamp(data.getGpstime().getTime()) : null);
        ps.setTimestamp(index++, data.getServerTime() != null ?
                new java.sql.Timestamp(data.getServerTime().getTime()) : null);
        ps.setInt(index++, data.getServerDelay());
        ps.setInt(index++, data.getCarId());
        ps.setString(index++, data.getTname());
        ps.setString(index++, data.getChepai());
        ps.setInt(index++, data.getStat());
        ps.setString(index++, data.getStart());
        ps.setString(index++, data.getStartXf());
        ps.setString(index++, data.getAddress());
        ps.setInt(index++, data.getGsm());
        ps.setInt(index++, data.getGps());
        ps.setInt(index++, data.getGpsend());
        ps.setString(index++, data.getCell());
        ps.setString(index++, data.getAlonlat());
        ps.setString(index++, data.getOlonlat());
        ps.setInt(index++, data.getVol());
        ps.setInt(index++, data.getOil());
        ps.setInt(index++, data.getBat());
        ps.setInt(index++, data.getMaxo());
        ps.setInt(index++, data.getRanges());
        ps.setDouble(index++, data.getOldlon());
        ps.setDouble(index++, data.getOldlat());
        ps.setString(index++, data.getModel());
        ps.setString(index++, data.getVersions());
        ps.setString(index++, data.getCarImg());
        ps.setInt(index++, data.getCtrl());
        ps.setInt(index++, data.getPort());
        ps.setInt(index++, data.getSimstate());
        ps.setInt(index++, data.getVipId());
        ps.setInt(index++, data.getTId());
        ps.setString(index++, data.getComm());
    }

    // 获取设备列表
    public List<DeviceInfo> getDeviceList() {
        // 将id添加到SELECT列表
        String sql = "SELECT DISTINCT id, sn, tname FROM gps_realtime WHERE tname IS NOT NULL AND tname != '' ORDER BY id";

        return jdbcTemplate.query(sql, new RowMapper<DeviceInfo>() {
            @Override
            public DeviceInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long snLong = rs.getLong("sn");
                String snStr = snLong != null ? snLong.toString() : "";
                return new DeviceInfo(snStr, rs.getString("tname"));
            }
        });
    }
}