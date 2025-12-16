package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.CardAndVipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cardAndVip")
public class CardAndVipController {

    @Autowired
    private CardAndVipService cardAndVipService;

    /**
     * 获取健身卡信息
     */
    @GetMapping("/gymCard/{userId}")
    public ResponseEntity<?> getGymCardInfo(@PathVariable Integer userId) {
        try {
            User user = cardAndVipService.getGymCardInfo(userId);
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.ok(response);
            }

            int remainingDays = cardAndVipService.calculateRemainingDays(userId);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("phone", user.getPhone());
            userInfo.put("cardTime", user.getCardTime());
            userInfo.put("expireTime", user.getExpireTime());
            userInfo.put("role", user.getRole());

            Map<String, Object> cardInfo = new HashMap<>();
            cardInfo.put("user", userInfo);
            cardInfo.put("remainingDays", remainingDays);
            cardInfo.put("cardStatus", getCardStatus(user.getExpireTime()));
            cardInfo.put("cardNumber", "GYM-" + String.format("%06d", userId));

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", cardInfo);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取信息失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取VIP信息
     */
    @GetMapping("/vipInfo/{userId}")
    public ResponseEntity<?> getVipInfo(@PathVariable Integer userId) {
        try {
            User user = cardAndVipService.getVipInfo(userId);
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.ok(response);
            }

            boolean isVip = cardAndVipService.isVip(userId);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("phone", user.getPhone());
            userInfo.put("role", user.getRole());
            userInfo.put("expireTime", user.getExpireTime());

            Map<String, Object> vipInfo = new HashMap<>();
            vipInfo.put("user", userInfo);
            vipInfo.put("isVip", isVip);
            vipInfo.put("vipStatus", isVip ? "VIP会员" : "普通会员");
            vipInfo.put("remainingDays", cardAndVipService.calculateRemainingDays(userId));

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", vipInfo);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取信息失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 续费健身卡
     */
    @PostMapping("/renewGymCard")
    public ResponseEntity<?> renewGymCard(@RequestBody Map<String, Object> params) {
        try {
            Integer userId = (Integer) params.get("userId");
            Integer months = (Integer) params.get("months");

            if (userId == null || months == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "参数不完整");
                return ResponseEntity.ok(response);
            }

            boolean success = cardAndVipService.renewGymCard(userId, months);

            if (success) {
                // 获取更新后的信息
                User user = cardAndVipService.getGymCardInfo(userId);
                int remainingDays = cardAndVipService.calculateRemainingDays(userId);

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("realName", user.getRealName());
                userInfo.put("phone", user.getPhone());
                userInfo.put("cardTime", user.getCardTime());
                userInfo.put("expireTime", user.getExpireTime());
                userInfo.put("role", user.getRole());

                Map<String, Object> cardInfo = new HashMap<>();
                cardInfo.put("user", userInfo);
                cardInfo.put("remainingDays", remainingDays);
                cardInfo.put("cardStatus", getCardStatus(user.getExpireTime()));

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "续费成功");
                result.put("data", cardInfo);

                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "续费失败");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "续费失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 升级为VIP
     */
    @PostMapping("/upgradeToVip")
    public ResponseEntity<?> upgradeToVip(@RequestBody Map<String, Object> params) {
        try {
            Integer userId = (Integer) params.get("userId");

            if (userId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "参数不完整");
                return ResponseEntity.ok(response);
            }

            // 检查用户是否已经是VIP
            if (cardAndVipService.isVip(userId)) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "您已经是VIP会员");
                return ResponseEntity.ok(response);
            }

            boolean success = cardAndVipService.upgradeToVip(userId);

            if (success) {
                User user = cardAndVipService.getVipInfo(userId);

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("realName", user.getRealName());
                userInfo.put("phone", user.getPhone());
                userInfo.put("role", user.getRole());

                Map<String, Object> vipInfo = new HashMap<>();
                vipInfo.put("user", userInfo);
                vipInfo.put("isVip", true);
                vipInfo.put("vipStatus", "VIP会员");

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "升级为VIP成功");
                result.put("data", vipInfo);

                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "升级失败");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "升级失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 续费并升级为VIP
     */
    @PostMapping("/renewAndUpgrade")
    public ResponseEntity<?> renewAndUpgrade(@RequestBody Map<String, Object> params) {
        try {
            Integer userId = (Integer) params.get("userId");
            Integer months = (Integer) params.get("months");

            if (userId == null || months == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "参数不完整");
                return ResponseEntity.ok(response);
            }

            boolean success = cardAndVipService.renewCardAndUpgradeToVip(userId, months);

            if (success) {
                User user = cardAndVipService.getVipInfo(userId);
                int remainingDays = cardAndVipService.calculateRemainingDays(userId);

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("realName", user.getRealName());
                userInfo.put("phone", user.getPhone());
                userInfo.put("cardTime", user.getCardTime());
                userInfo.put("expireTime", user.getExpireTime());
                userInfo.put("role", user.getRole());

                Map<String, Object> resultInfo = new HashMap<>();
                resultInfo.put("user", userInfo);
                resultInfo.put("remainingDays", remainingDays);
                resultInfo.put("isVip", true);
                resultInfo.put("vipStatus", "VIP会员");
                resultInfo.put("cardStatus", getCardStatus(user.getExpireTime()));

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "续费并升级VIP成功");
                result.put("data", resultInfo);

                return ResponseEntity.ok(result);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "操作失败");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取健身卡状态
     */
    @GetMapping("/cardStatus/{userId}")
    public ResponseEntity<?> getCardStatus(@PathVariable Integer userId) {
        try {
            User user = cardAndVipService.getGymCardInfo(userId);
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.ok(response);
            }

            int remainingDays = cardAndVipService.calculateRemainingDays(userId);

            Map<String, Object> statusInfo = new HashMap<>();
            statusInfo.put("status", getCardStatus(user.getExpireTime()));
            statusInfo.put("remainingDays", remainingDays);
            statusInfo.put("expireTime", user.getExpireTime());
            statusInfo.put("cardNumber", "GYM-" + String.format("%06d", userId));

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", statusInfo);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取状态失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取VIP状态
     */
    @GetMapping("/vipStatus/{userId}")
    public ResponseEntity<?> getVipStatus(@PathVariable Integer userId) {
        try {
            boolean isVip = cardAndVipService.isVip(userId);

            Map<String, Object> statusInfo = new HashMap<>();
            statusInfo.put("isVip", isVip);
            statusInfo.put("status", isVip ? "VIP会员" : "普通会员");

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", statusInfo);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取状态失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 辅助方法：根据到期时间获取健身卡状态
     */
    private String getCardStatus(Date expireTime) {
        if (expireTime == null) {
            return "未激活";
        }

        Date now = new Date();
        if (expireTime.before(now)) {
            return "已过期";
        } else {
            return "有效";
        }
    }
}