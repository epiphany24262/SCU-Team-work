
// 全局变量
let app;

// Vue 应用
document.addEventListener('DOMContentLoaded', function() {
    app = new Vue({
        el: '#app',
        data() {
            // 数据
            return {
                isSidebarCollapsed: false,
                currentMenu: 'home',
                currentUser: {
                    id: '',
                    username: '',
                    password: '',
                    realName: '',
                    age: '',
                    gender: '',
                    phone: '',
                    birthDate: '',
                    cardTime: '',
                    expireTime: '',
                    role: '',
                    status: '',
                    createTime: '',
                    updateTime: '',
                },
                userMenus: []
            }
        },
        async mounted() {
            // 初始化应用
            await this.initializeApp();

            // 检查URL参数决定加载哪个页面
            const urlParams = new URLSearchParams(window.location.search);
            let page = urlParams.get('page');

            // 如果没有指定页面，根据用户角色决定加载哪个首页
            if (!page) {
                if (this.currentUser.role === 'admin') {
                    page = 'home'; // 管理员首页
                } else {
                    page = 'user_home'; // 用户（会员/VIP）首页
                }
            }

            // 检查权限后再加载
            if (this.hasPermission(page)) {
                this.loadContent(page);
            } else {
                this.$message.error('您没有权限访问该页面');
                // 根据角色跳转到对应的首页
                if (this.currentUser.role === 'admin') {
                    this.loadContent('home');
                    window.history.pushState({ page: 'home' }, '', `?page=home`);
                } else {
                    this.loadContent('user_home');
                    window.history.pushState({ page: 'user_home' }, '', `?page=user_home`);
                }
            }
        },
        created() {
            // 创建全局事件总线
            window.userUpdateEvent = new Vue();
            // 添加浏览器前进后退支持
            window.addEventListener('popstate', (event) => {
                if (event.state && event.state.page) {
                    this.loadContent(event.state.page);
                }
            });
        },
        methods: {
            // 初始化应用
            async initializeApp() {
                try {
                    // 获取用户信息
                    const userInfo = await this.getCurrentUser();
                    if (!userInfo) {
                        // 没有有效的用户信息，跳转到登录页
                        this.$message.warning('请先登录');
                        setTimeout(() => {
                            window.location.href = '/login.html';
                        }, 1000);
                        return;
                    }
                    // 有有效的用户信息，继续获取菜单
                    await this.getUserMenus();

                } catch (error) {
                    console.error('初始化应用失败:', error);
                    this.$message.error('应用初始化失败');
                }
            },

            // 获取用户信息
            async getCurrentUser() {
                try {
                    // 只从 localStorage 获取
                    const savedUserInfo = localStorage.getItem('userInfo');
                    const token = localStorage.getItem('token');

                    if (!token) {
                        console.log('未找到token，用户未登录');
                        return null;
                    }

                    if (savedUserInfo) {
                        try {
                            this.currentUser = JSON.parse(savedUserInfo);
                            console.log('从本地存储加载用户信息');
                            return this.currentUser;
                        } catch (e) {
                            console.error('解析用户信息失败:', e);
                            // 清除损坏的数据
                            localStorage.removeItem('userInfo');
                            localStorage.removeItem('token');
                            return null;
                        }
                    } else {
                        console.log('未找到用户信息，需要重新登录');
                        // 有token但没有用户信息，可能是数据损坏
                        localStorage.removeItem('token');
                        return null;
                    }

                } catch (error) {
                    console.error('获取用户信息失败:', error);
                    return null;
                }
            },

            // 获取用户菜单
            async getUserMenus() {
                try {
                    const response = await axios.post('/api/menu/getUserMenus', {
                        role: this.currentUser.role
                    });

                    if (response.data && response.data.success) {
                        this.userMenus = response.data.data;
                        console.log('菜单数据:', this.userMenus);
                    } else {
                        // 获取菜单失败，但不影响主要功能
                        console.warn('获取菜单失败，使用空菜单');
                        this.userMenus = [];
                    }
                } catch (error) {
                    console.error('获取用户菜单失败:', error);
                    // 菜单获取失败不影响用户使用基本功能
                    this.userMenus = [];
                    this.$message.warning('菜单加载失败，刷新页面重试');
                }
            },

            // 切换侧边栏
            toggleSidebar() {
                this.isSidebarCollapsed = !this.isSidebarCollapsed;
            },

            // 处理用户命令
            handleUserCommand(command) {
                if (command === 'logout') {
                    this.logout();
                } else if (command === 'profile') {
                    this.loadContent('profile');
                }
            },

            // 退出登录
            async logout() {
                try {
                    await this.$confirm('确定要退出登录吗?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    });

                    // 清除本地存储
                    localStorage.removeItem('token');
                    localStorage.removeItem('userInfo');

                    // 跳转到登录页
                    window.location.href = '/login.html';

                } catch (error) {
                    if (error !== 'cancel') {
                        console.error('退出登录失败:', error);
                        this.$message.error('退出登录失败');
                    }
                }
            },

            // 打开GitHub
            openGithub() {
                window.open('https://github.com/Buskyi', '_blank');
            },

            // 加载页面内容
            async loadContent(pageName) {
                // 检查用户是否有权限访问该页面
                if (!this.hasPermission(pageName)) {
                    this.$message.error('您没有权限访问该页面');
                    return;
                }

                // 回到当前url页面
                this.currentMenu = pageName;
                const contentArea = document.getElementById('content-area');
                window.history.pushState({ page: pageName }, '', `?page=${pageName}`);

                // 显示加载中
                contentArea.innerHTML = `
                <div style="text-align: center; padding: 50px;">
                    <i class="el-icon-loading" style="font-size: 24px;"></i>
                    <div style="margin-top: 10px;">加载中...</div>
                </div>`;

                try {
                    // 直接使用axios加载HTML文件
                    const response = await axios.get(`${pageName}.html`);  // 注意路径
                    contentArea.innerHTML = response.data;
                    // 手动执行脚本
                    this.executeScripts(contentArea);
                } catch (error) {
                    console.error('加载页面失败:', error);
                    contentArea.innerHTML = `
                    <div style="text-align: center; padding: 50px; color: #f56c6c;">
                        <i class="el-icon-error" style="font-size: 48px;"></i>
                        <div style="margin-top: 16px; font-size: 16px;">页面加载失败</div>
                        <el-button type="primary" style="margin-top: 16px;" @click="loadContent('${pageName}')">重新加载</el-button>
                    </div>`;
                }
            },
            // 权限检查方法
            hasPermission(pageName) {
                // 获取当前用户角色
                const userRole = this.currentUser?.role;
                if (!userRole) return false;

                // 定义页面权限映射
                const pagePermissions = {
                    'admin': ['home', 'profile', 'member', 'coaches', 'equipment', 'schedules_course', 'schedules_room', 'schedules_enrollment', 'schedules_table', 'equipment_reservation'],
                    'vip': ['user_home', 'profile', 'mycard', 'myvip', 'reserve_equipment', 'reserve_course'],
                    'member': ['user_home', 'profile', 'mycard', 'myvip']
                };

                // 检查用户角色是否有权限访问该页面
                const allowedPages = pagePermissions[userRole] || [];
                return allowedPages.includes(pageName);
            },
            // 执行动态加载内容中的脚本
            executeScripts(container) {
                const scripts = container.querySelectorAll('script');
                scripts.forEach(script => {
                    const newScript = document.createElement('script');

                    // 复制script的属性
                    Array.from(script.attributes).forEach(attr => {
                        newScript.setAttribute(attr.name, attr.value);
                    });

                    // 复制script的内容
                    if (script.src) {
                        // 外部脚本
                        newScript.src = script.src;
                    } else {
                        // 内联脚本
                        newScript.textContent = script.textContent;
                    }

                    // 移除原script，添加新script
                    script.parentNode.removeChild(script);
                    document.body.appendChild(newScript);
                });
            },

            // 更新用户信息并通知所有页面
            updateUserInfo(userInfo) {
                this.currentUser = userInfo;
                console.log('更新后信息：',this.currentUser);
                localStorage.setItem('userInfo', JSON.stringify(userInfo));
                // 触发全局事件
                window.userUpdateEvent.$emit('userInfoUpdated', userInfo);
                // 重新加载菜单
                this.getUserMenus();
            }
        }
    });
});