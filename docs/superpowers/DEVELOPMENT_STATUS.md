# Dungeon Merchant 开发进度报告

> 生成时间: 2026-04-02
> 基于规范: `docs/superpowers/specs/2026-04-01-dungeon-merchant-backend-v1.md`
> 更新: 2026-04-02 团队并行开发后

---

## 一、模块开发状态总览

### 后端模块

| 模块 | 规范名称 | 状态 | 说明 |
|------|---------|------|------|
| M1 - Auth | 认证模块 | ✅ 已完成 | JWT注册/登录/刷新Token |
| M2 - Account | 账户模块 | ✅ 已完成 | 账户CRUD |
| M3 - Character | 角色模块 | ❌ 未开发 | 角色CRUD/特性/派驻 |
| M4 - Facility | 设施模块 | ❌ 未开发 | 设施CRUD/派驻管理 |
| M5 - Warehouse | 仓库模块 | ⚠️ 进行中 | Worker-2 开发中 |
| M6 - Production | 生产模块 | ❌ 未开发 | 生产计算/定时任务 |
| M7 - Config | 配置模块 | ⚠️ 进行中 | Worker-2 开发中 |
| M8 - Common | 公共模块 | ✅ 已完成 | 响应封装/异常处理 + Lombok/MapStruct |

### 前端模块

| 模块 | 状态 | 说明 |
|------|------|------|
| Frontend (Vue 3) | ✅ 基础完成 | 登录/注册/主页 + 路由/Pinia |

---

## 二、团队开发成果

### ✅ Worker-1 已完成 P0 任务

**修改的文件:**
1. `pom.xml` - 添加 Lombok + MapStruct + annotation processors
2. `src/main/resources/application.yml` - PostgreSQL/JWT 默认配置
3. `src/main/resources/db/migration/V1__init.sql` - 完整建表SQL和种子数据

### ✅ Worker-3 已完成 Vue 3 前端基础

**创建的文件:**
```
frontend/src/
├── main.js                    # 应用入口
├── App.vue                    # 根组件 (Topbar + RouterView)
├── App.spec.js               # 测试
├── style.css                 # 全局样式 (深紫色主题)
├── router/
│   └── index.js              # 路由配置 (/, /login, /register)
├── stores/
│   ├── auth.js               # Pinia auth store
│   ├── session.js            # Pinia session store
│   ├── auth.test.js
│   └── session.test.js
├── services/
│   ├── auth.js               # 认证 API 服务
│   ├── http.js               # axios 实例
│   └── account.js            # 账户 API 服务
├── lib/
│   └── api.js                # API 封装
├── utils/
│   ├── session.js            # Session 工具
│   └── session.test.js
├── views/
│   ├── HomeView.vue          # 主页
│   ├── LoginView.vue         # 登录页
│   └── RegisterView.vue      # 注册页
└── components/
    ├── HelloWorld.vue
    └── BrandPanel.vue        # 品牌面板组件
```

**设计风格:**
- 深紫色渐变背景 (#120f1e → #1b1630)
- 白色文字 (#f5f2ff)
- 紫色光晕效果
- 不对称布局 (BrandPanel + ContentCard)
- 交错动画效果

---

## 三、已开发模块详情

### ✅ M1 - Auth 模块 (认证)

**API 实现:**
- ✅ `POST /api/auth/register` - 注册
- ✅ `POST /api/auth/login` - 登录
- ✅ `POST /api/auth/refresh` - 刷新Token

---

### ✅ M2 - Account 模块 (账户)

**API 实现:**
- ✅ `GET /api/account` - 获取账户信息
- ✅ `PUT /api/account` - 更新账户

---

## 四、未开发模块详情

### ❌ M3 - Character 模块 (角色)

**API 端点:**
```
POST   /api/characters          - 创建角色
GET    /api/characters           - 获取角色列表
GET    /api/characters/{id}      - 获取角色详情
DELETE /api/characters/{id}      - 删除角色
POST   /api/characters/{id}/assign    - 派驻到设施
DELETE /api/characters/{id}/unassign   - 从设施卸离
```

---

### ❌ M4 - Facility 模块 (设施)

**API 端点:**
```
POST   /api/facilities           - 建造设施
GET    /api/facilities           - 获取设施列表
GET    /api/facilities/{id}      - 获取设施详情
PUT    /api/facilities/{id}/upgrade     - 升级设施
PUT    /api/facilities/{id}/slots/{slotIndex}      - 派驻角色到槽位
DELETE /api/facilities/{id}/slots/{slotIndex}      - 卸离槽位角色
```

---

### ⚠️ M5 - Warehouse 模块 (仓库) - 进行中

**需要:**
- entity: Warehouse.java, ResourceDef.java
- repository: WarehouseMapper.java, ResourceDefMapper.java
- service: WarehouseService.java, WarehouseServiceImpl.java
- controller: WarehouseController.java

**API 端点:**
```
GET    /api/warehouse            - 获取仓库
POST   /api/warehouse/test-add   - 测试添加资源
```

---

### ❌ M6 - Production 模块 (生产)

**API 端点:**
```
POST   /api/production/trigger/{facilityId}  - 手动触发生产
POST   /api/production/settle                - 离线结算
```

---

### ⚠️ M7 - Config 模块 (配置) - 进行中

**需要:**
- entity: RarityDef.java, TraitDef.java, FacilityType.java, Conversion.java
- repository: RarityDefMapper.java, TraitDefMapper.java, FacilityTypeMapper.java, ConversionMapper.java
- service: ConfigService.java, ConfigServiceImpl.java
- controller: ConfigController.java

**API 端点:**
```
GET    /api/config/rarities       - 获取稀有度列表
GET    /api/config/traits        - 获取特性列表
GET    /api/config/facility-types - 获取设施类型
GET    /api/config/conversions   - 获取转换规则
```

---

## 五、进度统计

| 指标 | 数值 |
|------|------|
| 总后端模块数 | 8 |
| 已完成后端模块 | 3 (M1, M2, M8) |
| 进行中模块 | 2 (M5, M7) |
| 未开发模块 | 3 (M3, M4, M6) |
| 前端模块 | ✅ 基础完成 |
| 后端完成度 | ~50% |
| API 端点已完成 | 5/21 |
| API 端点进行中 | 8 (M5, M7) |
| API 端点未完成 | 8 (M3, M4, M6) |

---

## 六、项目结构现状

```
src/main/java/com/dungeon/merchant/
├── DungeonMerchantApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── JwtProperties.java
└── module/
    ├── auth/          ✅ 完整
    ├── account/       ✅ 完整
    ├── character/     ❌ 不存在
    ├── facility/      ❌ 不存在
    ├── warehouse/     ⚠️ 进行中
    ├── production/    ❌ 不存在
    ├── config/        ⚠️ 进行中
    └── common/        ✅ 完成

src/main/resources/
├── application.yml    ✅ 新增
└── db/migration/
    └── V1__init.sql   ✅ 新增

frontend/             ✅ 新建
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── style.css
│   ├── router/index.js
│   ├── stores/
│   ├── services/
│   ├── views/
│   └── components/
└── package.json
```

---

## 七、待办事项

### P0 - 阻塞性问题
- [x] 创建 application.yml ✅
- [x] 创建 V1__init.sql ✅
- [x] 添加 Lombok 依赖 ✅
- [x] 添加 MapStruct 依赖 ✅

### P1 - 核心模块
- [ ] 完成 M5 - Warehouse 模块
- [ ] 完成 M7 - Config 模块
- [ ] 开发 M3 - Character 模块
- [ ] 开发 M4 - Facility 模块
- [ ] 开发 M6 - Production 模块

### P2 - 前端完善
- [ ] 添加仓库页面
- [ ] 添加角色页面
- [ ] 添加设施页面
- [ ] 添加生产页面

### P3 - 测试
- [ ] 为新模块编写单元测试
- [ ] 集成测试

---

## 八、本次更新记录（2026-04-02）

### 已完成

- 前端页面功能文案已改为中文，保留 `Dungeon Merchant`、`Guild Ledger`、`JWT`、`Pinia` 等专有名词英文
- 已更新浏览器标题 `frontend/index.html` 与前端说明 `frontend/README.md`
- 登录、注册、首页、顶部导航的中文文案测试已补齐
- 前端兜底错误提示与认证失败提示已统一为中文

### 验证结果

- 前端自动化测试：已执行 `cd frontend && npm test`，结果 `4` 个测试文件、`11` 个测试全部通过
- 后端认证自动化测试：尝试执行 `mvn -Dtest=AuthControllerIntegrationTest test`，当前 WSL 环境受限，返回 `JAVA_HOME environment variable is not defined correctly`
- 实际接口联调：尝试从当前 WSL 环境访问 `http://localhost:8080/actuator/health`，返回连接失败；说明 Windows 侧服务当前对该环境不可达或未保持运行，需在 Windows 侧继续验证 `POST /api/auth/register` 与 `POST /api/auth/login`

### 建议后续动作

- 在 Windows 环境中保持后端服务运行后，执行注册与登录接口联调
- 若需要彻底统一用户可见错误文案，可继续将认证外其他模块的英文错误提示改为中文
