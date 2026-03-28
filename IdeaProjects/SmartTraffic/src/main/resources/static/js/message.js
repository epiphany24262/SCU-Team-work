// 模拟消息数据（兜底）
const mockMessages = [
    {
        id: 1,
        title: "车辆异常报警",
        text: "车牌号：川A12345 的车辆检测到异常行驶行为，速度超过限速值30%，请及时处理。",
        time: "10分钟前",
        type: "alert",
        tags: ["urgent", "vehicle"],
        unread: true,
        starred: true,
        icon: "exclamation-triangle"
    },
    {
        id: 2,
        title: "车辆维护提醒",
        text: "车牌号：川B67890 的车辆已到维护周期，下次维护日期：2024-06-15，请安排维护。",
        time: "1小时前",
        type: "warning",
        tags: ["vehicle"],
        unread: true,
        starred: false,
        icon: "car"
    },
    {
        id: 3,
        title: "月度报告生成",
        text: "3月份车辆使用情况报告已生成，包含行驶里程统计、油耗分析和维护记录，请查看。",
        time: "1天前",
        type: "info",
        tags: ["system"],
        unread: false,
        starred: true,
        icon: "chart-line"
    },
    {
        id: 4,
        title: "新车辆注册成功",
        text: "车牌号：川C54321 的车辆已完成系统注册，车辆信息已添加到数据库中。",
        time: "2天前",
        type: "success",
        tags: ["system", "vehicle"],
        unread: false,
        starred: false,
        icon: "check-circle"
    },
    {
        id: 5,
        title: "超速报警",
        text: "车牌号：川D98765 在成华大道检测到超速行驶，当前速度：85km/h，限速：60km/h。",
        time: "3天前",
        type: "alert",
        tags: ["urgent", "vehicle"],
        unread: false,
        starred: false,
        icon: "tachometer-alt"
    },
    {
        id: 6,
        title: "系统维护通知",
        text: "系统将于本周六凌晨2:00-4:00进行例行维护，期间部分功能可能无法使用。",
        time: "5天前",
        type: "info",
        tags: ["system"],
        unread: false,
        starred: false,
        icon: "tools"
    }
];

// 当前消息数据
let messages = [];

// 当前用户与本地状态
let currentUser = null;
let readState = new Set();
let hiddenState = new Set();

// 当前状态变量
let currentFilter = 'all';
let currentSearchKeyword = '';
let currentSort = 'time-desc';
let currentPageIndex = 1;
const pageSize = 10;

function getLocalUser() {
    const userData = localStorage.getItem('traffic_user');
    if (!userData) return null;
    try {
        return JSON.parse(userData);
    } catch (e) {
        return null;
    }
}

function isManager() {
    return currentUser && currentUser.role === 'manager';
}

function applyRoleRestrictions() {
    if (isManager()) return;
    document.querySelectorAll('.admin-only').forEach(el => {
        el.style.display = 'none';
    });
    if (currentFilter === 'logs' || currentFilter === 'violations') {
        currentFilter = 'all';
    }
}

function getReadStorageKey() {
    return currentUser ? `message_read_${currentUser.username}` : 'message_read_anonymous';
}

function getHiddenStorageKey() {
    return currentUser ? `message_hidden_${currentUser.username}` : 'message_hidden_anonymous';
}

function loadReadState() {
    const raw = localStorage.getItem(getReadStorageKey());
    if (!raw) return new Set();
    try {
        const ids = JSON.parse(raw);
        return new Set(Array.isArray(ids) ? ids : []);
    } catch (e) {
        return new Set();
    }
}

function saveReadState() {
    localStorage.setItem(getReadStorageKey(), JSON.stringify(Array.from(readState)));
}

function loadHiddenState() {
    const raw = localStorage.getItem(getHiddenStorageKey());
    if (!raw) return new Set();
    try {
        const ids = JSON.parse(raw);
        return new Set(Array.isArray(ids) ? ids : []);
    } catch (e) {
        return new Set();
    }
}

function saveHiddenState() {
    localStorage.setItem(getHiddenStorageKey(), JSON.stringify(Array.from(hiddenState)));
}

function parseTimeString(timeStr) {
    if (!timeStr) return null;
    const normalized = timeStr.replace('T', ' ').replace(/\//g, '-');
    const date = new Date(normalized.includes(' ') ? normalized.replace(' ', 'T') : normalized);
    return isNaN(date.getTime()) ? null : date;
}

function formatRelativeTime(timeStr) {
    const date = parseTimeString(timeStr);
    if (!date) return timeStr || '未知时间';
    const diff = Date.now() - date.getTime();
    const minute = 60 * 1000;
    const hour = 60 * minute;
    const day = 24 * hour;
    if (diff < minute) return '刚刚';
    if (diff < hour) return `${Math.floor(diff / minute)}分钟前`;
    if (diff < day) return `${Math.floor(diff / hour)}小时前`;
    if (diff < 7 * day) return `${Math.floor(diff / day)}天前`;
    return timeStr;
}

function getIconByType(type, source) {
    if (source === 'system_log') return 'clipboard-list';
    if (source === 'violation') return 'car-burst';
    switch (type) {
        case 'alert':
            return 'exclamation-triangle';
        case 'warning':
            return 'triangle-exclamation';
        case 'success':
            return 'check-circle';
        case 'system':
            return 'gear';
        case 'message':
            return 'comment-dots';
        default:
            return 'info-circle';
    }
}

function normalizeMessageItem(raw) {
    const id = raw.id || raw.messageId || raw.msgId || raw.logId || raw.violationId || '';
    const time = raw.time || raw.createdAt || raw.created_at || raw.operateTime || raw.operate_time || '';
    const type = raw.type || (raw.source === 'system_log' ? 'system' : raw.source === 'violation' ? 'alert' : 'info');
    const tags = Array.isArray(raw.tags) ? raw.tags : [];
    const source = raw.source || 'message';
    const text = raw.content || raw.text || '';
    const sender = raw.sender || raw.operator || '系统';
    const scope = raw.scope || (tags.includes('announcement') ? 'all' : 'user');
    const displayTime = formatRelativeTime(time);
    const sortTime = parseTimeString(time)?.getTime() || 0;

    return {
        id: String(id),
        title: raw.title || '系统通知',
        text,
        time,
        displayTime,
        type,
        tags,
        source,
        scope,
        sender,
        receiver: raw.receiver || '',
        unread: !readState.has(String(id)),
        starred: raw.level === 'alert' || tags.includes('urgent'),
        icon: raw.icon || getIconByType(type, source),
        sortTime
    };
}

function normalizeMessages(list) {
    return (Array.isArray(list) ? list : [])
        .map(item => normalizeMessageItem(item))
        .filter(item => !hiddenState.has(item.id));
}

// 搜索功能
function searchMessages(keyword) {
    if (!keyword.trim()) {
        return messages;
    }

    keyword = keyword.toLowerCase();
    return messages.filter(msg =>
        msg.title.toLowerCase().includes(keyword) ||
        msg.text.toLowerCase().includes(keyword)
    );
}

// 排序功能
function sortMessages(messagesArray, sortType = 'time-desc') {
    const sortedMessages = [...messagesArray];
    const getSortValue = (msg) => msg.sortTime || msg.id || 0;

    switch(sortType) {
        case 'time-asc':
            return sortedMessages.sort((a, b) => getSortValue(a) - getSortValue(b));
        case 'time-desc':
            return sortedMessages.sort((a, b) => getSortValue(b) - getSortValue(a));
        case 'unread-first':
            return sortedMessages.sort((a, b) => {
                if (a.unread && !b.unread) return -1;
                if (!a.unread && b.unread) return 1;
                return getSortValue(b) - getSortValue(a);
            });
        case 'starred-first':
            return sortedMessages.sort((a, b) => {
                if (a.starred && !b.starred) return -1;
                if (!a.starred && b.starred) return 1;
                return getSortValue(b) - getSortValue(a);
            });
        default:
            return sortedMessages;
    }
}

// 获取标签显示文本
function getTagLabel(tag) {
    const labels = {
        'urgent': '紧急',
        'system': '系统',
        'vehicle': '车辆',
        'announcement': '公告',
        'direct': '私信',
        'log': '日志',
        'violation': '违规'
    };
    return labels[tag] || tag;
}

function getSourceLabel(msg) {
    if (msg.source === 'system_log') return '系统日志';
    if (msg.source === 'violation') return '违规车辆';
    if (msg.scope === 'all' || msg.tags.includes('announcement')) return '系统公告';
    if (msg.tags.includes('direct')) return '定向消息';
    return '站内消息';
}

function applyFilter(list, filter) {
    let filtered = list;
    if (!isManager()) {
        filtered = filtered.filter(msg => msg.source !== 'system_log' && msg.source !== 'violation');
        if (filter === 'logs' || filter === 'violations') {
            filter = 'all';
        }
    }

    if (filter === "unread") {
        filtered = filtered.filter(msg => msg.unread);
    } else if (filter === "announcements") {
        filtered = filtered.filter(msg => msg.tags.includes("announcement") || msg.scope === 'all');
    } else if (filter === "direct") {
        filtered = filtered.filter(msg => msg.tags.includes("direct") || msg.scope === 'user');
    } else if (filter === "logs") {
        filtered = filtered.filter(msg => msg.source === 'system_log' || msg.tags.includes("log"));
    } else if (filter === "violations") {
        filtered = filtered.filter(msg => msg.source === 'violation' || msg.tags.includes("violation"));
    }

    return filtered;
}

function paginate(list) {
    const totalPages = Math.max(1, Math.ceil(list.length / pageSize));
    if (currentPageIndex > totalPages) {
        currentPageIndex = totalPages;
    }
    const start = (currentPageIndex - 1) * pageSize;
    return {
        totalPages,
        pageItems: list.slice(start, start + pageSize)
    };
}

function renderPagination(totalItems) {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;
    if (totalItems === 0) {
        pagination.innerHTML = '';
        return;
    }

    const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
    const prevDisabled = currentPageIndex <= 1;
    const nextDisabled = currentPageIndex >= totalPages;
    const startPage = Math.max(1, currentPageIndex - 2);
    const endPage = Math.min(totalPages, currentPageIndex + 2);

    let html = '';
    html += `<div class="pagination-button ${prevDisabled ? 'disabled' : ''}" data-page="${currentPageIndex - 1}"><i class="fas fa-chevron-left"></i></div>`;
    for (let i = startPage; i <= endPage; i += 1) {
        html += `<div class="pagination-button ${i === currentPageIndex ? 'active' : ''}" data-page="${i}">${i}</div>`;
    }
    html += `<div class="pagination-button ${nextDisabled ? 'disabled' : ''}" data-page="${currentPageIndex + 1}"><i class="fas fa-chevron-right"></i></div>`;
    pagination.innerHTML = html;
}

// 渲染消息列表
function renderMessages(filter = "all", searchKeyword = "", sortType = "time-desc") {
    const container = document.getElementById('messageItems');
    let filteredMessages = searchKeyword ? searchMessages(searchKeyword) : messages;

    filteredMessages = applyFilter(filteredMessages, filter);

    // 应用排序
    filteredMessages = sortMessages(filteredMessages, sortType);

    const { pageItems } = paginate(filteredMessages);
    renderPagination(filteredMessages.length);

    if (filteredMessages.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-search"></i>
                <h3>未找到相关消息</h3>
                <p>请尝试其他搜索词或筛选条件</p>
            </div>
        `;
        return;
    }

    container.innerHTML = pageItems.map(msg => `
        <div class="message-item ${msg.unread ? 'unread' : ''}" data-id="${msg.id}">
            <div class="message-checkbox">
                <input type="checkbox" class="message-select" id="msg-${msg.id}">
            </div>
            <div class="message-icon ${msg.type}">
                <i class="fas fa-${msg.icon}"></i>
            </div>
            <div class="message-content">
                <div class="message-title-row">
                    <div class="message-title">
                        ${msg.title}
                        ${msg.starred ? '<span class="star-badge"><i class="fas fa-star"></i> 重要</span>' : ''}
                    </div>
                    <div class="message-time">${msg.displayTime || msg.time}</div>
                </div>
                <div class="message-meta">
                    <span><i class="fas fa-user"></i> 发送人：${msg.sender || '系统'}</span>
                    <span><i class="fas fa-folder-open"></i> 来源：${getSourceLabel(msg)}</span>
                    ${msg.scope === 'user' && msg.receiver ? `<span><i class="fas fa-user-check"></i> 接收人：${msg.receiver}</span>` : ''}
                </div>
                <div class="message-text">${msg.text}</div>
                <div class="message-tags">
                    ${msg.tags.map(tag => `
                        <span class="message-tag ${tag}">${getTagLabel(tag)}</span>
                    `).join('')}
                </div>
            </div>
        </div>
    `).join('');

    // 添加消息点击事件
    document.querySelectorAll('.message-item').forEach(item => {
        item.addEventListener('click', function(e) {
            if (e.target.type === 'checkbox') return;

            const msgId = this.getAttribute('data-id');
            const msg = messages.find(m => String(m.id) === String(msgId));

            if (msg && msg.unread) {
                msg.unread = false;
                readState.add(String(msg.id));
                saveReadState();
                this.classList.remove('unread');
                updateStats();
            }
        });
    });
}

// 更新统计信息
function updateStats() {
    const totalMessages = messages.length;
    const unreadMessages = messages.filter(msg => msg.unread).length;
    const alertMessages = messages.filter(msg => msg.source === 'violation' || msg.type === 'alert').length;
    const systemMessages = messages.filter(msg => msg.source === 'system_log' || msg.tags.includes('log')).length;

    // 更新统计卡片
    const statCards = document.querySelectorAll('.stat-card .count');
    if (statCards.length >= 4) {
        statCards[0].textContent = totalMessages;
        statCards[1].textContent = unreadMessages;
        statCards[2].textContent = alertMessages;
        statCards[3].textContent = systemMessages;
    }
}

// 显示提示消息
function showToast(message, type = 'success') {
    const colors = {
        success: '#52c41a',
        error: '#ff4d4f',
        info: '#1890ff'
    };

    const toast = document.createElement('div');
    toast.className = 'toast-message';
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${colors[type] || colors.success};
        color: white;
        padding: 12px 24px;
        border-radius: 6px;
        z-index: 9999;
        animation: slideIn 0.3s ease;
    `;

    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            document.body.removeChild(toast);
        }, 300);
    }, 2000);
}

// 本地存储功能
function saveUserPreferences() {
    const preferences = {
        filter: currentFilter,
        sort: currentSort,
        searchKeyword: currentSearchKeyword
    };
    localStorage.setItem('messageCenterPrefs', JSON.stringify(preferences));
}

function loadUserPreferences() {
    const savedPrefs = localStorage.getItem('messageCenterPrefs');
    if (savedPrefs) {
        try {
            const prefs = JSON.parse(savedPrefs);
            currentFilter = prefs.filter || 'all';
            currentSort = prefs.sort || 'time-desc';
            currentSearchKeyword = prefs.searchKeyword || '';

            const allowedFilters = new Set(['all', 'unread', 'announcements', 'direct', 'logs', 'violations']);
            if (!allowedFilters.has(currentFilter)) {
                currentFilter = 'all';
            }

            // 更新UI
            document.getElementById('searchInput').value = currentSearchKeyword;
            document.getElementById('sortSelect').value = currentSort;

            // 设置过滤器按钮状态
            document.querySelectorAll('.filter-button').forEach(btn => {
                if (btn.getAttribute('data-filter') === currentFilter) {
                    btn.classList.add('active');
                } else {
                    btn.classList.remove('active');
                }
            });

            return true;
        } catch (e) {
            console.error('加载用户偏好设置失败:', e);
        }
    }
    return false;
}

async function fetchFeed() {
    if (!currentUser) return [];
    const params = new URLSearchParams({
        username: currentUser.username,
        role: currentUser.role || 'user',
        limit: '200'
    });
    const response = await fetch(`/api/message-center/feed?${params.toString()}`);
    if (!response.ok) {
        throw new Error(`获取消息失败（${response.status}）`);
    }
    const result = await response.json();
    if (result && result.success && result.data) {
        return result.data.list || [];
    }
    return [];
}

async function loadMessages() {
    try {
        const list = await fetchFeed();
        const normalized = normalizeMessages(list);
        const roleFiltered = isManager()
            ? normalized
            : normalized.filter(item => item.source !== 'system_log' && item.source !== 'violation');
        messages = roleFiltered.length ? roleFiltered : normalizeMessages(mockMessages);
        currentPageIndex = 1;
    } catch (e) {
        console.error('加载消息失败:', e);
        showToast('消息中心接口异常，已展示模拟数据', 'info');
        messages = normalizeMessages(mockMessages);
        currentPageIndex = 1;
    }
}

async function loadRecipients() {
    const receiverSelect = document.getElementById('receiverSelect');
    if (!receiverSelect) return;
    try {
        const response = await fetch('/api/message-center/users');
        const result = await response.json();
        if (result && result.success && result.data && Array.isArray(result.data.list)) {
            const options = result.data.list
                .filter(user => user.username && (!currentUser || user.username !== currentUser.username))
                .map(user => {
                    const name = user.real_name ? `${user.real_name}（${user.username}）` : user.username;
                    return `<option value="${user.username}">${name}</option>`;
                });
            receiverSelect.innerHTML = options.join('') || '<option value="">暂无可选用户</option>';
        }
    } catch (e) {
        console.error('加载用户列表失败:', e);
        receiverSelect.innerHTML = '<option value="">加载失败</option>';
    }
}

function initAdminCompose() {
    const composeCard = document.getElementById('adminCompose');
    if (!composeCard) return;
    if (!currentUser || currentUser.role !== 'manager') {
        composeCard.style.display = 'none';
        return;
    }
    composeCard.style.display = 'block';

    const scopeToggle = document.getElementById('scopeToggle');
    const receiverField = document.getElementById('receiverField');
    const receiverSelect = document.getElementById('receiverSelect');
    if (scopeToggle) {
        scopeToggle.querySelectorAll('.scope-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                scopeToggle.querySelectorAll('.scope-btn').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                const scope = btn.getAttribute('data-scope');
                if (scope === 'user') {
                    receiverField.style.display = 'flex';
                    if (!receiverSelect.innerHTML) {
                        loadRecipients();
                    }
                } else {
                    receiverField.style.display = 'none';
                }
            });
        });
    }

    document.getElementById('sendMessageBtn')?.addEventListener('click', async () => {
        const scope = scopeToggle.querySelector('.scope-btn.active')?.getAttribute('data-scope') || 'all';
        const receiver = receiverSelect?.value || '';
        const title = document.getElementById('messageTitle')?.value.trim();
        const content = document.getElementById('messageContent')?.value.trim();
        const level = document.getElementById('messageLevel')?.value || 'info';

        if (!title) {
            showToast('请输入消息标题', 'error');
            return;
        }
        if (!content) {
            showToast('请输入消息内容', 'error');
            return;
        }
        if (scope === 'user' && !receiver) {
            showToast('请选择接收用户', 'error');
            return;
        }

        try {
            const response = await fetch('/api/message-center/send', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    sender: currentUser.username,
                    scope,
                    receiver: scope === 'user' ? receiver : null,
                    title,
                    content,
                    level
                })
            });
            const result = await response.json();
            if (result.success) {
                showToast('消息发送成功', 'success');
                document.getElementById('messageTitle').value = '';
                document.getElementById('messageContent').value = '';
                await loadMessages();
                renderMessages(currentFilter, currentSearchKeyword, currentSort);
                updateStats();
            } else {
                showToast(result.message || '消息发送失败', 'error');
            }
        } catch (e) {
            showToast('消息发送异常', 'error');
        }
    });

    document.getElementById('clearComposeBtn')?.addEventListener('click', () => {
        const titleInput = document.getElementById('messageTitle');
        const contentInput = document.getElementById('messageContent');
        if (titleInput) titleInput.value = '';
        if (contentInput) contentInput.value = '';
    });
}

// 初始化
document.addEventListener('DOMContentLoaded', function() {
    initMessageCenter();
});

async function initMessageCenter() {
    currentUser = getLocalUser();
    readState = loadReadState();
    hiddenState = loadHiddenState();

    // 加载用户偏好设置
    loadUserPreferences();
    applyRoleRestrictions();

    // 加载消息数据
    await loadMessages();

    // 初始渲染
    renderMessages(currentFilter, currentSearchKeyword, currentSort);
    updateStats();

    // 初始化管理员发布面板
    initAdminCompose();

    // ============ 搜索框事件 ============
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            currentSearchKeyword = this.value;
            currentPageIndex = 1;
            renderMessages(currentFilter, currentSearchKeyword, currentSort);
            saveUserPreferences();
        });
    }

    // ============ 排序下拉框事件 ============
    const sortSelect = document.getElementById('sortSelect');
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            currentSort = this.value;
            currentPageIndex = 1;
            renderMessages(currentFilter, currentSearchKeyword, currentSort);
            saveUserPreferences();
        });
    }

    // ============ 过滤器按钮事件 ============
    document.querySelectorAll('.filter-button').forEach(button => {
        button.addEventListener('click', function() {
            // 移除所有按钮的active类
            document.querySelectorAll('.filter-button').forEach(btn => {
                btn.classList.remove('active');
            });

            // 为当前按钮添加active类
            this.classList.add('active');

            // 应用过滤器
            currentFilter = this.getAttribute('data-filter');
            currentPageIndex = 1;
            renderMessages(currentFilter, currentSearchKeyword, currentSort);
            saveUserPreferences();
        });
    });

    // ============ 分页点击事件 ============
    document.getElementById('pagination')?.addEventListener('click', function(e) {
        const btn = e.target.closest('.pagination-button');
        if (!btn || btn.classList.contains('disabled')) {
            return;
        }
        const page = parseInt(btn.getAttribute('data-page'), 10);
        if (Number.isNaN(page)) return;
        currentPageIndex = page;
        renderMessages(currentFilter, currentSearchKeyword, currentSort);
    });

    // ============ 全部标记已读按钮事件 ============
    document.getElementById('markAllRead')?.addEventListener('click', function() {
        messages.forEach(msg => {
            msg.unread = false;
            readState.add(String(msg.id));
        });
        saveReadState();
        renderMessages(currentFilter, currentSearchKeyword, currentSort);
        updateStats();
        showToast('所有消息已标记为已读');
        saveUserPreferences();
    });

    // ============ 删除选中消息按钮事件 ============
    document.getElementById('deleteSelected')?.addEventListener('click', function() {
        const selectedCheckboxes = document.querySelectorAll('.message-select:checked');

        if (selectedCheckboxes.length === 0) {
            alert('请先选择要删除的消息');
            return;
        }

        if (confirm(`确定要删除选中的 ${selectedCheckboxes.length} 条消息吗？`)) {
            let removedCount = 0;
            let blockedCount = 0;
            selectedCheckboxes.forEach(cb => {
                const msgId = cb.id.replace('msg-', '');
                const msg = messages.find(item => String(item.id) === String(msgId));
                if (!msg) return;
                if (msg.source && msg.source !== 'message') {
                    blockedCount += 1;
                    return;
                }
                hiddenState.add(String(msgId));
                removedCount += 1;
            });
            saveHiddenState();
            messages = messages.filter(item => !hiddenState.has(String(item.id)));
            renderMessages(currentFilter, currentSearchKeyword, currentSort);
            updateStats();
            if (removedCount > 0) {
                showToast(`已删除 ${removedCount} 条消息`, 'success');
            }
            if (blockedCount > 0) {
                showToast(`系统日志与违规信息不可删除（${blockedCount} 条）`, 'info');
            }
        }
    });

    // ============ 清空所有消息按钮事件 ============
    document.getElementById('clearAll')?.addEventListener('click', function() {
        if (messages.length === 0) {
            alert('当前没有消息可清空');
            return;
        }

        if (confirm(`确定要清空所有可清理消息吗？系统日志与违规信息不会被清空。`)) {
            messages.forEach(msg => {
                if (msg.source === 'message') {
                    hiddenState.add(String(msg.id));
                }
            });
            saveHiddenState();
            messages = messages.filter(msg => msg.source !== 'message' || !hiddenState.has(String(msg.id)));
            renderMessages(currentFilter, currentSearchKeyword, currentSort);
            updateStats();
            showToast('可清理消息已清空', 'success');
        }
    });

    // ============ 导出消息按钮事件 ============
    document.getElementById('exportMessages')?.addEventListener('click', function() {
        if (messages.length === 0) {
            alert('没有消息可以导出');
            return;
        }

        // 创建CSV格式的数据
        const headers = ['标题', '内容', '类型', '标签', '状态', '重要', '时间'];
        const csvData = messages.map(msg => [
            `"${msg.title}"`,
            `"${msg.text}"`,
            msg.type,
            msg.tags.join(','),
            msg.unread ? '未读' : '已读',
            msg.starred ? '是' : '否',
            msg.time
        ]);

        // 创建CSV字符串
        const csvContent = [
            headers.join(','),
            ...csvData.map(row => row.join(','))
        ].join('\n');

        // 创建Blob并下载
        const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);

        link.setAttribute('href', url);
        link.setAttribute('download', `消息中心_${new Date().toISOString().split('T')[0]}.csv`);
        link.style.visibility = 'hidden';

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        showToast('消息已导出为CSV文件', 'success');
    });

    // ============ 刷新消息按钮事件 ============
    document.getElementById('refreshMessages')?.addEventListener('click', function() {
        const btn = this;
        const originalHtml = btn.innerHTML;

        // 添加旋转动画
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 刷新中...';
        btn.disabled = true;

        // 模拟网络请求延迟
        setTimeout(async () => {
            await loadMessages();
            renderMessages(currentFilter, currentSearchKeyword, currentSort);
            updateStats();

            // 恢复按钮状态
            btn.innerHTML = originalHtml;
            btn.disabled = false;

            showToast('消息列表已刷新', 'success');
        }, 600);
    });

    // ============ 键盘快捷键支持 ============
    document.addEventListener('keydown', function(e) {
        // 检查是否在搜索框中，如果是则不触发全局快捷键
        if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA' || e.target.tagName === 'SELECT') {
            return;
        }

        // Ctrl/Cmd + F: 聚焦搜索框
        if ((e.ctrlKey || e.metaKey) && e.key === 'f') {
            e.preventDefault();
            const searchInput = document.getElementById('searchInput');
            if (searchInput) {
                searchInput.focus();
            }
        }

        // Ctrl/Cmd + R: 刷新消息
        if ((e.ctrlKey || e.metaKey) && e.key === 'r') {
            e.preventDefault();
            const refreshBtn = document.getElementById('refreshMessages');
            if (refreshBtn) {
                refreshBtn.click();
            }
        }

        // Esc: 清空搜索框
        if (e.key === 'Escape') {
            const searchInput = document.getElementById('searchInput');
            if (searchInput && searchInput.value) {
                searchInput.value = '';
                currentSearchKeyword = '';
                renderMessages(currentFilter, currentSearchKeyword, currentSort);
                saveUserPreferences();
            }
        }
    });
}