# Frontend Chinese Auth Verification Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将前端用户可见功能文案改为中文，持续更新开发进度手册，并完成登录/注册接口的自动化验证与实际联调记录。

**Architecture:** 保持现有 Vue 组件结构、路由和接口契约不变，只定点替换用户可见文案并同步调整受影响测试。认证验证分为两层：后端集成测试验证接口契约，运行中的服务做实际注册/登录联调验证，并将结果回写进度手册。

**Tech Stack:** Vue 3、Vue Router、Pinia、Vitest、Spring Boot、MockMvc、Markdown 文档

---

### Task 1: 记录起始状态到开发手册

**Files:**
- Modify: `docs/superpowers/DEVELOPMENT_STATUS.md`

- [ ] **Step 1: 在开发手册中新增本次任务记录占位**

```md
## 八、本次更新记录（2026-04-02）

### 处理中

- 前端页面功能文案中文化
- 浏览器标题与前端说明更新
- 登录/注册接口自动化验证与联调验证
```

- [ ] **Step 2: 检查手册变更是否只记录本次任务**

Run: `git diff -- docs/superpowers/DEVELOPMENT_STATUS.md`
Expected: 只出现“本次更新记录”相关新增内容

- [ ] **Step 3: 提交该阶段改动**

```bash
git add docs/superpowers/DEVELOPMENT_STATUS.md
git commit -m "docs: record frontend localization work start"
```

### Task 2: 先写失败测试覆盖中文文案

**Files:**
- Modify: `frontend/src/App.spec.js`

- [ ] **Step 1: 把顶栏断言改成中文目标文案**

```js
expect(wrapper.text()).toContain('创建账户')
expect(wrapper.text()).toContain('登录')
expect(wrapper.text()).toContain('加载账户')
```

- [ ] **Step 2: 运行前端测试并确认先失败**

Run: `npm test -- --runInBand`
Workdir: `frontend`
Expected: `App.spec.js` 因页面仍是英文而失败

- [ ] **Step 3: 提交失败测试之前不要改生产代码**

Expected: 只有测试文件变更，页面文件尚未修改

### Task 3: 实现前端中文化

**Files:**
- Modify: `frontend/src/App.vue`
- Modify: `frontend/src/views/LoginView.vue`
- Modify: `frontend/src/views/RegisterView.vue`
- Modify: `frontend/src/views/HomeView.vue`
- Modify: `frontend/index.html`
- Modify: `frontend/README.md`

- [ ] **Step 1: 修改顶栏中文文案**

```vue
<p class="topbar__eyebrow">Dungeon Merchant 控制台</p>
<RouterLink class="topbar__link" to="/register">
  创建账户
</RouterLink>
<RouterLink class="topbar__link" to="/login">
  登录
</RouterLink>
<RouterLink class="topbar__link topbar__link--accent" to="/">
  加载账户
</RouterLink>
```

- [ ] **Step 2: 修改登录页中文文案**

```vue
<h2 class="content-card__title">登录</h2>
<span class="field__label">账户名</span>
<span class="field__label">密码</span>
{{ authStore.busy ? '正在打开账本...' : '进入账本' }}
```

- [ ] **Step 3: 修改注册页中文文案**

```vue
<h2 class="content-card__title">创建账户</h2>
<span class="field__label">账户名</span>
<span class="field__label">确认密码</span>
{{ authStore.busy ? '正在签发文书...' : '完成注册' }}
```

- [ ] **Step 4: 修改主页中文文案并切换日期本地化格式**

```js
return new Intl.DateTimeFormat('zh-CN', {
  dateStyle: 'medium',
  timeStyle: 'short',
}).format(new Date(value))
```

```vue
{ label: '账户 ID', value: String(authStore.account.id) }
<h3 class="status-card__title">账户同步失败</h3>
<button class="button button--primary" :disabled="authStore.busy" @click="refreshAccount">
  刷新账户
</button>
```

- [ ] **Step 5: 修改浏览器标题和前端 README**

```html
<html lang="zh-CN">
<title>Dungeon Merchant 前端控制台</title>
```

```md
# Dungeon Merchant 前端

## 启动开发环境

npm install
npm run dev
```

- [ ] **Step 6: 运行前端测试并确认通过**

Run: `npm test`
Workdir: `frontend`
Expected: Vitest 全部通过

- [ ] **Step 7: 提交前端中文化改动**

```bash
git add frontend/src/App.vue frontend/src/views/LoginView.vue frontend/src/views/RegisterView.vue frontend/src/views/HomeView.vue frontend/index.html frontend/README.md frontend/src/App.spec.js
git commit -m "feat: localize frontend copy to chinese"
```

### Task 4: 验证登录注册接口自动化测试

**Files:**
- Test: `src/test/java/com/dungeon/merchant/module/auth/controller/AuthControllerIntegrationTest.java`

- [ ] **Step 1: 运行认证集成测试**

Run: `mvn -Dtest=AuthControllerIntegrationTest test`
Expected: 注册、登录、刷新、重复注册、错误登录相关断言通过

- [ ] **Step 2: 如果输出显示失败，先停下来记录真实失败信息**

Expected: 不修改生产代码，先读取失败日志并定位问题

- [ ] **Step 3: 将自动化验证结果写入手册待完成区**

```md
- 自动化测试：`AuthControllerIntegrationTest` 通过 / 失败（附结论）
```

### Task 5: 执行实际注册登录联调

**Files:**
- Modify: `docs/superpowers/DEVELOPMENT_STATUS.md`

- [ ] **Step 1: 启动后端服务或确认服务正在运行**

Run: `mvn spring-boot:run`
Expected: 应用启动并监听 `http://localhost:8080`

- [ ] **Step 2: 调用注册接口**

Run: `curl -s -X POST http://localhost:8080/api/auth/register -H 'Content-Type: application/json' -d '{"username":"merchant-cn","password":"Password123!"}'`
Expected: 返回 `code=200` 且包含 `accessToken`、`refreshToken`

- [ ] **Step 3: 调用登录接口**

Run: `curl -s -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{"username":"merchant-cn","password":"Password123!"}'`
Expected: 返回 `code=200` 且包含 `accessToken`、`refreshToken`

- [ ] **Step 4: 把联调结果写入开发手册**

```md
- 实际联调：`POST /api/auth/register`、`POST /api/auth/login` 已验证（附关键结果）
```

- [ ] **Step 5: 提交手册最终更新**

```bash
git add docs/superpowers/DEVELOPMENT_STATUS.md
git commit -m "docs: record localization and auth verification results"
```

### Task 6: 汇总并推送

**Files:**
- Modify: `docs/superpowers/DEVELOPMENT_STATUS.md`
- Modify: `frontend/*`

- [ ] **Step 1: 复核最终差异**

Run: `git status --short && git log --oneline -n 5`
Expected: 只包含本轮设计、中文化、手册和验证相关提交

- [ ] **Step 2: 推送到中转仓库**

Run: `git push backup-local main`
Expected: 新提交推送到 `backup-local/main`

- [ ] **Step 3: 记录交付说明**

```md
- 中文化范围
- 自动化测试结果
- 联调结果
- 若有受环境限制未完成项，明确说明
```

