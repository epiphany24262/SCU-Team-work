package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@Service
public class CardAndVipService {

    @Autowired
    private UserDao userDao;

    /**
     * 续费健身卡
     * @param userId 用户ID
     * @param months 续费月数
     * @return 是否成功
     */
    @Transactional
    public boolean renewGymCard(Integer userId, Integer months) {
        User user = userDao.findById(userId);
        if (user == null) {
            return false;
        }

        Date currentExpireTime = user.getExpireTime();
        Date newExpireTime;

        if (currentExpireTime == null || currentExpireTime.before(new Date())) {
            // 如果健身卡已过期或未激活，从今天开始计算
            newExpireTime = calculateNewExpireTime(new Date(), months);
            // 如果是第一次激活，设置开卡时间为现在
            if (user.getCardTime() == null) {
                user.setCardTime(new Date());
            }
        } else {
            // 从当前到期时间开始计算
            newExpireTime = calculateNewExpireTime(currentExpireTime, months);
        }

        user.setExpireTime(newExpireTime);
        user.setUpdateTime(new Date());

        // 更新用户信息
        int result = userDao.update(user);

        // 这里可以添加续费记录到数据库（需要创建续费记录表）
        // saveRenewalRecord(user, months, newExpireTime);

        return result > 0;
    }

    /**
     * 计算新的到期时间
     */
    private Date calculateNewExpireTime(Date startDate, Integer months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    /**
     * 升级为VIP会员
     * @param userId 用户ID
     * @return 是否成功
     */
    @Transactional
    public boolean upgradeToVip(Integer userId) {
        User user = userDao.findById(userId);
        if (user == null) {
            return false;
        }

        // 如果已经是VIP，无需重复升级
        if ("vip".equals(user.getRole())) {
            return true;
        }

        user.setRole("vip");
        user.setUpdateTime(new Date());

        int result = userDao.update(user);

        // 这里可以添加VIP购买记录到数据库（需要创建VIP购买记录表）
        // saveVipPurchaseRecord(user);

        return result > 0;
    }

    /**
     * 获取用户健身卡信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public User getGymCardInfo(Integer userId) {
        return userDao.findById(userId);
    }

    /**
     * 获取用户VIP信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public User getVipInfo(Integer userId) {
        return userDao.findById(userId);
    }

    /**
     * 检查用户是否是VIP
     * @param userId 用户ID
     * @return 是否是VIP
     */
    public boolean isVip(Integer userId) {
        User user = userDao.findById(userId);
        return user != null && "vip".equals(user.getRole());
    }

    /**
     * 计算剩余天数
     * @param userId 用户ID
     * @return 剩余天数，负数表示已过期
     */
    public int calculateRemainingDays(Integer userId) {
        User user = userDao.findById(userId);
        if (user == null || user.getExpireTime() == null) {
            return 0;
        }

        Date expireDate = user.getExpireTime();
        Date now = new Date();

        long diffTime = expireDate.getTime() - now.getTime();
        long diffDays = diffTime / (1000 * 60 * 60 * 24);

        return (int) diffDays;
    }

    /**
     * 续费健身卡并升级为VIP（组合操作）
     * @param userId 用户ID
     * @param months 续费月数
     * @return 是否成功
     */
    @Transactional
    public boolean renewCardAndUpgradeToVip(Integer userId, Integer months) {
        boolean renewSuccess = renewGymCard(userId, months);
        boolean vipSuccess = upgradeToVip(userId);
        return renewSuccess && vipSuccess;
    }
}