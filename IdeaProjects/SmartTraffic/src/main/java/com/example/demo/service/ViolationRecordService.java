package com.example.demo.service;

import com.example.demo.dao.ViolationRecordDao;
import com.example.demo.entity.ViolationRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ViolationRecordService {
    @Autowired
    private ViolationRecordDao violationRecordDao;

    public Map<String, Object> listViolations(String plateNumber, String illegalType, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<ViolationRecord> list = violationRecordDao.listViolations(plateNumber, illegalType, startTime, endTime, offset, pageSize);
        int total = violationRecordDao.countViolations(plateNumber, illegalType, startTime, endTime);

        for (ViolationRecord record : list) {
            fillViolationDesc(record);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("list", list);
        result.put("data", data);
        return result;
    }

    public Map<String, Object> statsViolations(String plateNumber, String illegalType, String startTime, String endTime) {
        Map<String, Object> result = new HashMap<>();
        int total = violationRecordDao.countViolations(plateNumber, illegalType, startTime, endTime);
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("byType", violationRecordDao.statsViolations(plateNumber, illegalType, startTime, endTime));
        result.put("success", true);
        result.put("data", data);
        return result;
    }

    public List<ViolationRecord> listViolationsAll(String plateNumber, String illegalType, String startTime, String endTime) {
        List<ViolationRecord> list = violationRecordDao.listViolations(plateNumber, illegalType, startTime, endTime, 0, Integer.MAX_VALUE);
        for (ViolationRecord record : list) {
            fillViolationDesc(record);
        }
        return list;
    }

    public Map<String, Object> clearViolations(List<Integer> ids) {
        Map<String, Object> result = new HashMap<>();
        if (ids == null || ids.isEmpty()) {
            result.put("success", false);
            result.put("message", "请选择要删除的违章记录");
            return result;
        }
        int updated = violationRecordDao.clearViolationsByIds(ids);
        result.put("success", updated > 0);
        result.put("message", updated > 0 ? "批量删除成功" : "未删除任何违章记录");
        result.put("deleted", updated);
        return result;
    }

    private void fillViolationDesc(ViolationRecord record) {
        if (record == null) {
            return;
        }
        String type = record.getViolationType();
        if (type == null) {
            record.setViolationDesc("-");
            record.setPunishResult("-");
            return;
        }
        switch (type) {
            case "red_light":
                record.setViolationDesc("在信号灯为红灯时通过路口");
                record.setPunishResult("罚款200元，记6分");
                break;
            case "wrong_way":
                record.setViolationDesc("在单行道或规定方向道路逆向行驶");
                record.setPunishResult("罚款200元，记3分");
                break;
            case "line_press":
                record.setViolationDesc("压实线变道或越线行驶");
                record.setPunishResult("罚款100元，记3分");
                break;
            case "illegal_parking":
                record.setViolationDesc("在禁止停车路段违规停放车辆");
                record.setPunishResult("罚款200元，不记分");
                break;
            case "helmet":
                record.setViolationDesc("骑乘人员未按规定佩戴安全头盔");
                record.setPunishResult("罚款50元，警告");
                break;
            default:
                record.setViolationDesc("交通违法行为");
                record.setPunishResult("罚款100元");
                break;
        }
    }
}
