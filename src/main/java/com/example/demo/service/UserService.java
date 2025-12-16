package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 用户登录验证
     */
    public User login(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.getStatus() == 1) {
            return user;
        }
        return null;
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    /**
     * 根据ID获取用户
     */
    public User getUserById(Integer id) {
        return userDao.findById(id);
    }

    /**
     * 搜索用户
     */
    public List<User> searchUsers(String username, String phone) {
        return userDao.findByUsernameAndPhone(username, phone);
    }

    /**
     * 新增用户
     */
    public User addUser(User user) {
        // 检查用户名是否为空
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }

        // 检查密码是否为空
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        // 检查用户名是否已存在
        if (userDao.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 设置默认值
        if (user.getRealName() == null || user.getRealName().trim().isEmpty()) {
            user.setRealName(user.getUsername());
        }

        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("member");
        }

        int result = userDao.insert(user);
        if (result > 0) {
            return user;
        } else {
            throw new RuntimeException("新增用户失败");
        }
    }

    /**
     * 更新用户
     */
    public User updateUser(User user) {
        // 检查用户是否存在
        if (!userDao.exists(user.getId())) {
            throw new RuntimeException("用户不存在");
        }
        // 检查用户名是否被其他用户使用
        User existingUser = userDao.findByUsername(user.getUsername());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            throw new RuntimeException("用户名已被其他用户使用");
        }

        int result = userDao.update(user);
        if (result > 0) {
            return userDao.findById(user.getId());
        } else {
            throw new RuntimeException("更新用户失败");
        }
    }

    /**
     * 删除用户
     */
    public void deleteUser(Integer id) {
        if (!userDao.exists(id)) {
            throw new RuntimeException("用户不存在");
        }

        int result = userDao.delete(id);
        if (result <= 0) {
            throw new RuntimeException("删除用户失败");
        }
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return userDao.existsByUsername(username);
    }

    /**
     * 获取用户统计信息
     */
    public UserStats getUserStats() {
        UserStats stats = new UserStats();
        stats.setTotal(userDao.count());
        stats.setAdminCount(userDao.countByRole("admin"));
        stats.setVipCount(userDao.countByRole("vip"));
        stats.setMemberCount(userDao.countByRole("member"));
        return stats;
    }

    // 统计信息内部类
    public static class UserStats {
        private Long total;
        private Long adminCount;
        private Long vipCount;
        private Long memberCount;

        // Getter和Setter
        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }
        public Long getAdminCount() { return adminCount; }
        public void setAdminCount(Long adminCount) { this.adminCount = adminCount; }
        public Long getVipCount() { return vipCount; }
        public void setVipCount(Long vipCount) { this.vipCount = vipCount; }
        public Long getMemberCount() { return memberCount; }
        public void setMemberCount(Long memberCount) { this.memberCount = memberCount; }
    }
}