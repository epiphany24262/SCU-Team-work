package com.example.demo.service;

import com.example.demo.dao.MenuDao;
import com.example.demo.entity.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuService {

    @Autowired
    private MenuDao menuDao;

    // 根据角色获取菜单树
    public List<Menu> getMenuTreeByRole(String role) {
        // 获取该角色的所有菜单（扁平列表）
        List<Menu> menuList = menuDao.getMenusByRole(role);

        // 构建树形结构
        return buildMenuTree(menuList);
    }

    // 获取所有菜单树
    public List<Menu> getAllMenuTree() {
        List<Menu> menuList = menuDao.getAllMenus();
        return buildMenuTree(menuList);
    }

    // 构建菜单树
    private List<Menu> buildMenuTree(List<Menu> menuList) {
        Map<Integer, Menu> menuMap = new HashMap<>();
        List<Menu> tree = new ArrayList<>();

        // 首先将所有菜单项存入map
        for (Menu menu : menuList) {
            menuMap.put(menu.getId(), menu);
        }

        // 构建树形结构
        for (Menu menu : menuList) {
            if (menu.getParentId() == 0) {
                // 根节点
                tree.add(menu);
            } else {
                // 子节点
                Menu parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(menu);
                }
            }
        }

        return tree;
    }
}