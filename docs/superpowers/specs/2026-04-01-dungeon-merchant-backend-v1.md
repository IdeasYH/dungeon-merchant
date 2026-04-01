# Dungeon Merchant 后端技术规范 v1.0

> 本文档是后端开发的唯一依据，所有 Agent 编码必须严格遵循。
> 文档采用模块化设计，支持多 Agent 并行开发。

---

## 目录

1. [系统架构](#1-系统架构)
2. [技术栈](#2-技术栈)
3. [数据库设计](#3-数据库设计)
4. [模块清单与并行策略](#4-模块清单与并行策略)
5. [AI-Friendly Coding Standards](#5-ai-friendly-coding-standards)
6. [API 设计](#6-api-设计)
7. [生产计算系统](#7-生产计算系统)
8. [配置驱动设计](#8-配置驱动设计)
9. [认证与安全](#9-认证与安全)
10. [项目结构](#10-项目结构)

---

## 1. 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端 (Vue 3)                          │
│                    http://localhost:3000                     │
└─────────────────────────────┬───────────────────────────────┘
                              │ REST API / JWT
┌─────────────────────────────▼───────────────────────────────┐
│                    API Gateway (Nginx)                        │
│                   http://localhost:8080                       │
└─────────────────────────────┬───────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│                   Dungeon Merchant Backend                    │
│                   Spring Boot + MyBatis-Plus                  │
│                         :8080                                │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ Account   │  │ Character │  │ Facility  │  │Production│   │
│  │ Module    │  │ Module    │  │ Module    │  │ Module    │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                  │
│  │ Warehouse │  │   Config  │  │   Auth    │                  │
│  │ Module    │  │   Module   │  │  Module   │                  │
│  └──────────┘  └──────────┘  └──────────┘                  │
└─────────────────────────────┬───────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────┐
│                      PostgreSQL 16                            │
│                     localhost:5432                            │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. 技术栈

| 层级 | 技术 | 版本 | 用途 |
|-----|------|------|------|
| 运行时 | JDK | 17+ | Java 运行环境 |
| 框架 | Spring Boot | 3.2.x | 核心框架 |
| 安全 | Spring Security | 6.x | 认证/授权 |
| ORM | MyBatis-Plus | 3.5.x | 持久层 |
| 数据库 | PostgreSQL | 16.x | 主数据库 |
| 工具 | Lombok | 1.18.x | 简化代码 |
| 工具 | MapStruct | 1.5.x | 对象转换 |
| 构建 | Maven | 3.9.x | 项目构建 |

---

## 3. 数据库设计

### 3.1 ER 图

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   account   │────<│  character  │     │   facility  │
├─────────────┤     ├─────────────┤     ├─────────────┤
│ id          │     │ id          │     │ id          │
│ username    │     │ account_id  │     │ account_id  │
│ password    │     │ name        │     │ name        │
│ created_at  │     │ rarity_id   │     │ type_id     │
│ updated_at  │     │ level       │     │ level       │
└─────────────┘     │ created_at  │     │ created_at  │
                    └──────┬───────┘     └──────┬───────┘
                           │                    │
                    ┌──────▼───────┐     ┌──────▼───────┐
                    │char_trait    │     │facility_slot │
                    ├──────────────┤     ├──────────────┤
                    │character_id  │     │facility_id   │
                    │trait_id      │     │character_id  │
                    │bonus_value   │     │slot_index    │
                    └──────────────┘     └──────────────┘
                                                │
                    ┌───────────────────────────┘
                    │
             ┌──────▼───────┐     ┌─────────────┐     ┌─────────────┐
             │  warehouse   │────<│resource_def │     │ conversion  │
             ├──────────────┤     ├─────────────┤     ├─────────────┤
             │ id           │     │ id          │     │ id          │
             │ account_id   │     │ name        │     │ facility_id │
             │ resource_id  │     │ type        │     │ input_json  │
             │ quantity     │     │ rarity      │     │ output_id   │
             │ updated_at   │     │ icon        │     │ output_qty  │
             └──────────────┘     └─────────────┘     └─────────────┘
                                        ▲
                    ┌───────────────────┴───────────────────┐
                    │           rarity_def                  │
                    ├──────────────────────────────────────┤
                    │ id                                    │
                    │ name                                  │
                    │ color                                 │
                    │ trait_count_min                        │
                    │ trait_count_max                        │
                    │ base_bonus_min                        │
                    │ base_bonus_max                        │

                    ┌─────────────────┐
                    │     trait_def    │
                    ├──────────────────┤
                    │ id               │
                    │ name             │
                    │ icon             │
                    │ facility_type_id │
                    │ bonus_percentage │
                    └──────────────────┘
```

### 3.2 表结构定义

#### account (账户表)
```sql
CREATE TABLE account (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_account_username ON account(username);
```

#### character (角色表)
```sql
CREATE TABLE character (
    id          BIGSERIAL PRIMARY KEY,
    account_id  BIGINT NOT NULL REFERENCES account(id),
    name        VARCHAR(100) NOT NULL,
    rarity_id   BIGINT NOT NULL REFERENCES rarity_def(id),
    level       INT DEFAULT 1,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_character_account ON character(account_id);
```

#### char_trait (角色特性关联表)
```sql
CREATE TABLE char_trait (
    id            BIGSERIAL PRIMARY KEY,
    character_id  BIGINT NOT NULL REFERENCES character(id),
    trait_id      BIGINT NOT NULL REFERENCES trait_def(id),
    bonus_value   DECIMAL(5,2) NOT NULL  -- 加成百分比，如 15.5 表示 15.5%
);
CREATE INDEX idx_char_trait_character ON char_trait(character_id);
```

#### facility_type (设施类型定义表)
```sql
CREATE TABLE facility_type (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    tier        INT NOT NULL,              -- 产业链层级 1/2/3
    max_slots   INT DEFAULT 1,              -- 最大派驻角色数
    base_output_interval INT DEFAULT 60,   -- 基础产出间隔(秒)
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### facility (角色设施实例表)
```sql
CREATE TABLE facility (
    id          BIGSERIAL PRIMARY KEY,
    account_id  BIGINT NOT NULL REFERENCES account(id),
    type_id     BIGINT NOT NULL REFERENCES facility_type(id),
    name        VARCHAR(100) NOT NULL,
    level       INT DEFAULT 1,
    last_produce_at TIMESTAMP,             -- 上次产出时间
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_facility_account ON facility(account_id);
```

#### facility_slot (设施派驻槽位表)
```sql
CREATE TABLE facility_slot (
    id          BIGSERIAL PRIMARY KEY,
    facility_id BIGINT NOT NULL REFERENCES facility(id),
    slot_index  INT NOT NULL,              -- 槽位索引 0/1/2...
    character_id BIGINT REFERENCES character(id),  -- NULL 表示空槽
    assigned_at  TIMESTAMP
);
CREATE INDEX idx_facility_slot_facility ON facility_slot(facility_id);
CREATE UNIQUE INDEX idx_facility_slot_unique ON facility_id, slot_index;
```

#### resource_def (资源定义表)
```sql
CREATE TABLE resource_def (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    type        VARCHAR(50) NOT NULL,       -- 'raw'/'intermediate'/'final'
    rarity      INT DEFAULT 0,              -- 稀有度等级
    icon        VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### conversion (生产转换规则表)
```sql
CREATE TABLE conversion (
    id              BIGSERIAL PRIMARY KEY,
    facility_type_id BIGINT NOT NULL REFERENCES facility_type(id),
    input_json      JSONB NOT NULL,         -- [{"resource_id":1,"qty":2},{"resource_id":3,"qty":1}]
    output_resource_id BIGINT NOT NULL REFERENCES resource_def(id),
    output_qty      INT NOT NULL,
    base_time        INT NOT NULL,          -- 基础生产时间(秒)
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_conversion_facility ON conversion(facility_type_id);
```

#### warehouse (仓库表)
```sql
CREATE TABLE warehouse (
    id          BIGSERIAL PRIMARY KEY,
    account_id  BIGINT NOT NULL REFERENCES account(id),
    resource_id BIGINT NOT NULL REFERENCES resource_def(id),
    quantity    BIGINT NOT NULL DEFAULT 0,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(account_id, resource_id)
);
CREATE INDEX idx_warehouse_account ON warehouse(account_id);
```

#### rarity_def (稀有度定义表)
```sql
CREATE TABLE rarity_def (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    color           VARCHAR(20) NOT NULL,   -- 颜色代码如 '#FF0000'
    trait_count_min INT NOT NULL,
    trait_count_max INT NOT NULL,
    bonus_min       DECIMAL(5,2) NOT NULL,   -- 基础加成最小值%
    bonus_max       DECIMAL(5,2) NOT NULL   -- 基础加成最大值%
);
```

#### trait_def (特性定义表)
```sql
CREATE TABLE trait_def (
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(100) NOT NULL,
    icon              VARCHAR(255),
    facility_type_id  BIGINT REFERENCES facility_type(id),  -- NULL 表示通用
    bonus_percentage  DECIMAL(5,2) NOT NULL   -- 加成百分比
);
```

#### production_log (生产日志表)
```sql
CREATE TABLE production_log (
    id              BIGSERIAL PRIMARY KEY,
    facility_id     BIGINT NOT NULL REFERENCES facility(id),
    output_resource_id BIGINT NOT NULL REFERENCES resource_def(id),
    output_qty      INT NOT NULL,
    produced_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_production_log_facility ON production_log(facility_id);
```

### 3.3 初始数据

```sql
-- 设施类型 (三级产业链)
INSERT INTO facility_type (name, tier, max_slots, base_output_interval) VALUES
('矿场', 1, 1, 60),
('伐木场', 1, 1, 60),
('冶炼厂', 2, 2, 120),
('木工坊', 2, 2, 120),
('铸造厂', 3, 3, 180),
('炼金工坊', 3, 3, 180);

-- 资源定义
INSERT INTO resource_def (name, type, rarity) VALUES
('矿石', 'raw', 0),
('木材', 'raw', 0),
('金属锭', 'intermediate', 1),
('木板', 'intermediate', 1),
('铁剑', 'final', 2),
('生命药水', 'final', 2);

-- 转换规则
INSERT INTO conversion (facility_type_id, input_json, output_resource_id, output_qty, base_time) VALUES
(1, '[{"resource_id":null,"qty":0}]', 1, 10, 60),           -- 矿场直接产出矿石
(2, '[{"resource_id":null,"qty":0}]', 2, 8, 60),            -- 伐木场直接产出木材
(3, '[{"resource_id":1,"qty":2}]', 3, 1, 120),              -- 冶炼厂: 2矿石->1金属锭
(4, '[{"resource_id":2,"qty":3}]', 4, 1, 120),              -- 木工坊: 3木材->1木板
(5, '[{"resource_id":3,"qty":2}]', 5, 1, 180),              -- 铸造厂: 2金属锭->1铁剑
(6, '[{"resource_id":1,"qty":1},{"resource_id":3,"qty":1}]', 6, 1, 180);  -- 炼金: 1矿石+1金属锭->1药水

-- 稀有度定义
INSERT INTO rarity_def (name, color, trait_count_min, trait_count_max, bonus_min, bonus_max) VALUES
('N', '#AAAAAA', 1, 1, 5.0, 10.0),
('R', '#00FF00', 1, 2, 10.0, 20.0),
('SR', '#0000FF', 2, 2, 15.0, 30.0),
('SSR', '#FF00FF', 2, 3, 25.0, 50.0);

-- 特性定义
INSERT INTO trait_def (name, facility_type_id, bonus_percentage) VALUES
('矿工', 1, 30.0),
('伐木工', 2, 30.0),
('铁匠', 3, 30.0),
('木匠', 4, 30.0),
('铸造师', 5, 30.0),
('炼金师', 6, 30.0),
('多才多艺', NULL, 15.0);  -- 通用特性，所有设施都加成

-- 初始账户
INSERT INTO account (username, password) VALUES
('test', '$2a$10$...');  -- 密码: test123 (BCrypt加密)
```

---

## 4. 模块清单与并行策略

### 4.1 模块划分 (每个模块 = 独立 Agent 可开发)

| # | 模块 | 负责人 | 依赖 | 职责 |
|---|-----|-------|------|------|
| M1 | **Auth Module** | Agent-A | 无 | JWT 认证/注册/登录 |
| M2 | **Account Module** | Agent-B | M1 | 账户 CRUD |
| M3 | **Character Module** | Agent-B | M1,M2 | 角色 CRUD/特性/派驻 |
| M4 | **Facility Module** | Agent-C | M1,M2 | 设施 CRUD/派驻管理 |
| M5 | **Warehouse Module** | Agent-C | M1,M2 | 仓库存取 |
| M6 | **Production Module** | Agent-C | M1,M2,M3,M4,M5 | 生产计算/定时任务 |
| M7 | **Config Module** | Agent-A | 无 | 稀有度/特性/产业链配置 |
| M8 | **Common Module** | Agent-A | 无 | 工具类/常量/异常/响应封装 |

### 4.2 并行开发顺序

```
Phase 1 (可并行):
  Agent-A: M1 (Auth) + M8 (Common)
  Agent-B: M2 (Account)
  Agent-C: M7 (Config)

Phase 2 (可并行):
  Agent-A: M3 (Character)
  Agent-B: M4 (Facility)
  Agent-C: M5 (Warehouse)

Phase 3 (M1-M5 完成后):
  Agent-A: M6 (Production)

Phase 4 (全部完成后):
  Integration Test + API 联调
```

### 4.3 模块接口契约

每个模块必须定义清晰的接口契约，供其他 Agent 调用：

#### M1-AuthModule
```java
// 登录
LoginResponse login(LoginRequest request);
// 注册
RegisterResponse register(RegisterRequest request);
// 验证Token
Claims validateToken(String token);
// 获取当前用户
Account getCurrentAccount();
```

#### M2-AccountModule
```java
// 获取账户信息
AccountDTO getAccount(Long accountId);
// 更新账户
void updateAccount(Long accountId, UpdateAccountRequest request);
```

#### M3-CharacterModule
```java
// 创建角色
CharacterDTO createCharacter(Long accountId, CreateCharacterRequest request);
// 获取角色列表
List<CharacterDTO> getCharacters(Long accountId);
// 派驻角色到设施
void assignCharacter(Long characterId, Long facilityId, Integer slotIndex);
// 卸离角色
void unassignCharacter(Long facilityId, Integer slotIndex);
// 计算角色加成
BigDecimal calculateBonus(Long characterId, Long facilityTypeId);
```

#### M4-FacilityModule
```java
// 建造设施
FacilityDTO buildFacility(Long accountId, CreateFacilityRequest request);
// 升级设施
FacilityDTO upgradeFacility(Long facilityId);
// 获取设施列表
List<FacilityDTO> getFacilities(Long accountId);
// 获取设施详情(含派驻情况)
FacilityDetailDTO getFacilityDetail(Long facilityId);
// 检查原料是否足够
boolean checkMaterials(Long facilityId, Long conversionId, Integer qty);
```

#### M5-WarehouseModule
```java
// 获取仓库
List<WarehouseItemDTO> getWarehouse(Long accountId);
// 增删资源
void addResource(Long accountId, Long resourceId, Long qty);
void removeResource(Long accountId, Long resourceId, Long qty);
// 检查资源
boolean hasResource(Long accountId, Long resourceId, Long qty);
Map<Long, Long> getResourceMap(Long accountId);
```

#### M6-ProductionModule
```java
// 触发生产(玩家操作时)
void triggerProduction(Long facilityId);
// 计算单次产出
ProductionResult calculateOutput(Long facilityId);
// 批量计算(定时任务)
void batchCalculate(Long accountId, LocalDateTime from, LocalDateTime to);
// 离线结算(登录时)
void settleOffline(Long accountId);
```

#### M7-ConfigModule
```java
// 获取稀有度列表
List<RarityDefDTO> getAllRarity();
// 获取特性列表
List<TraitDefDTO> getAllTrait();
// 获取设施类型列表
List<FacilityTypeDTO> getAllFacilityType();
// 获取转换规则
ConversionDTO getConversion(Long facilityTypeId);
// 动态配置更新(运营后台)
void updateRarity(Long id, UpdateRarityRequest request);
void updateTrait(Long id, UpdateTraitRequest request);
void addTrait(AddTraitRequest request);
```

---

## 5. AI-Friendly Coding Standards

> 所有 Agent 必须严格遵循以下规范，否则代码审查不通过。

### 5.1 极致模块化 (Extreme Modularity)

```java
// ❌ BAD: 超过20行的复杂方法
public void handleProduction(Long facilityId) {
    // 50行逻辑...
}

// ✅ GOOD: 拆分为纯函数
public ProductionResult calculateOutput(Long facilityId) {
    // 单一职责：只计算产出
}

public void persistProduction(Long facilityId, ProductionResult result) {
    // 单一职责：只写数据库
}

public void notifyPlayer(Long accountId, ProductionResult result) {
    // 单一职责：只发通知
}
```

```java
// ❌ BAD: 深度继承
public class BaseService -> AccountService -> AccountServiceImpl

// ✅ GOOD: 接口组合
public interface AccountService { ... }
public class AccountServiceImpl implements AccountService { ... }
```

```java
// ❌ BAD: 有状态方法，依赖类成员变量
public class ProductionService {
    private Facility currentFacility;  // 隐式状态
    public void calculate() { ... }
}

// ✅ GOOD: 无状态纯函数
public ProductionResult calculate(Facility facility, List<Character> characters) {
    // Input -> Output，无副作用
}
```

### 5.2 显式契约 (Explicit Contracts)

```java
// ❌ BAD: 参数无校验
public void addResource(Long accountId, Long resourceId, Long qty) {
    warehouseRepo.save(...);
}

// ✅ GOOD: 防御式编程
public void addResource(Long accountId, Long resourceId, Long qty) {
    if (accountId == null || accountId <= 0) {
        throw new IllegalArgumentException("Invalid accountId");
    }
    if (resourceId == null || resourceId <= 0) {
        throw new IllegalArgumentException("Invalid resourceId");
    }
    if (qty == null || qty <= 0) {
        throw new IllegalArgumentException("Invalid qty");
    }
    warehouseRepo.save(...);
}
```

```java
// ❌ BAD: 隐式全局调用
public void assignCharacter(...) {
    // 隐式调用了某个全局 service
    globalService.doSomething();
}

// ✅ GOOD: 显式依赖注入
public void assignCharacter(CharacterService characterService, ...) {
    characterService.assignToFacility(...);
}
```

### 5.3 隔离与边界 (Isolation & Boundaries)

```java
// ✅ GOOD: 计算逻辑(纯) 与 IO 操作(副作用) 分离
public class ProductionCalculator {
    // 纯计算，无数据库调用
    public ProductionResult calculate(Facility f, List<Character> chars, Conversion conv) {
        BigDecimal bonus = calculateBonus(f, chars);
        int actualOutput = (int) (conv.getOutputQty() * bonus / 100);
        return new ProductionResult(conv.getOutputResourceId(), actualOutput);
    }
}

public class ProductionService {
    @Transactional
    public void triggerProduction(Long facilityId) {
        Facility f = facilityRepo.findById(facilityId);
        ProductionResult result = calculator.calculate(f, chars, conv);  // 纯计算
        warehouseService.addResource(f.getAccountId(), result);  // IO
    }
}
```

### 5.4 增量式扩展 (Incremental Extension)

```java
// ❌ BAD: 修改已稳定逻辑
public BigDecimal calculateBonus(Character c, Facility f) {
    if (c.getRarity() == 1) {  // 增加稀有度判断，修改已有逻辑
        return base.multiply(new BigDecimal("1.2"));
    }
    return base;
}

// ✅ GOOD: 新增策略类
public interface BonusStrategy {
    BigDecimal calculate(Character c, Facility f);
}

@Component
public class RarityBonusStrategy implements BonusStrategy {
    @Override
    public BigDecimal calculate(Character c, Facility f) {
        return base.multiply(rarity.getBonusMultiplier());
    }
}

// 使用 Map<String, BonusStrategy> 策略注册，old code 不动
```

---

## 6. API 设计

### 6.1 认证相关

| 方法 | 路径 | 描述 |
|-----|------|------|
| POST | /api/auth/register | 注册 |
| POST | /api/auth/login | 登录 |
| POST | /api/auth/refresh | 刷新Token |

### 6.2 账户相关

| 方法 | 路径 | 描述 |
|-----|------|------|
| GET | /api/account | 获取账户信息 |
| PUT | /api/account | 更新账户 |

### 6.3 角色相关

| 方法 | 路径 | 描述 |
|-----|------|------|
| POST | /api/characters | 创建角色 |
| GET | /api/characters | 获取角色列表 |
| GET | /api/characters/{id} | 获取角色详情 |
| DELETE | /api/characters/{id} | 删除角色 |
| POST | /api/characters/{id}/assign | 派驻到设施 |
| DELETE | /api/characters/{id}/unassign | 从设施卸离 |

### 6.4 设施相关

| 方法 | 路径 | 描述 |
|-----|------|------|
| POST | /api/facilities | 建造设施 |
| GET | /api/facilities | 获取设施列表 |
| GET | /api/facilities/{id} | 获取设施详情 |
| PUT | /api/facilities/{id}/upgrade | 升级设施 |
| PUT | /api/facilities/{id}/slots/{slotIndex} | 派驻角色到槽位 |
| DELETE | /api/facilities/{id}/slots/{slotIndex} | 卸离槽位角色 |

### 6.5 仓库相关

| 方法 | 路径 | 描述 |
|-----|------|------|
| GET | /api/warehouse | 获取仓库 |
| POST | /api/warehouse/test-add | 测试添加资源(仅测试环境) |

### 6.6 生产相关

| 方法 | 路径 | 描述 |
|-----|------|------|
| POST | /api/production/trigger/{facilityId} | 手动触发生产 |
| POST | /api/production/settle | 离线结算(登录时调用) |

### 6.7 配置相关

| 方法 | 路径 | 描述 |
|-----|------|------|
| GET | /api/config/rarities | 获取稀有度列表 |
| GET | /api/config/traits | 获取特性列表 |
| GET | /api/config/facility-types | 获取设施类型 |
| GET | /api/config/conversions | 获取转换规则 |

### 6.8 统一响应格式

```json
{
    "code": 200,
    "message": "success",
    "data": { ... },
    "timestamp": "2026-04-01T12:00:00Z"
}
```

```json
{
    "code": 400,
    "message": "参数错误: accountId 不能为空",
    "data": null,
    "timestamp": "2026-04-01T12:00:00Z"
}
```

---

## 7. 生产计算系统

### 7.1 计算公式

```
设施基础产出 = conversion.output_qty
设施等级系数 = 1 + (level - 1) * 0.1   // 每级+10%
驻场角色加成 = Σ(character.bonus_value)  // 所有派驻角色加成之和
设施总加成 = 设施等级系数 * (1 + 驻场角色加成/100)

实际产出 = floor(设施基础产出 * 设施总加成)
```

### 7.2 离线结算算法

```
登录时:
1. 获取所有设施
2. 计算每个设施: (当前时间 - last_produce_at) / base_output_interval = 可生产次数
3. 验证仓库原料是否足够(足够才生产)
4. 扣除原料，添加产出，更新last_produce_at
```

### 7.3 定时任务设计

```java
@Component
public class ProductionScheduler {
    @Scheduled(fixedRate = 300000)  // 5分钟
    public void batchProduction() {
        // 1. 查询所有"上次产出时间 > 5分钟前"的设施
        // 2. 分页处理，每批1000
        // 3. 计算产出，扣除原料，添加产出
        // 4. 批量UPDATE
    }
}
```

---

## 8. 配置驱动设计

### 8.1 为什么配置驱动？

- 稀有度等级数量可调整 → 改 rarity_def 表，不改代码
- 特性可动态添加 → 改 trait_def 表，不改代码
- 产业链可重新配置 → 改 conversion 表，不改代码
- 加成百分比可运营调整 → 改 trait_def 表

### 8.2 关键配置表

```sql
-- 稀有度 (运行时可调整)
rarity_def: id, name, color, trait_count_min, trait_count_max, bonus_min, bonus_max

-- 特性 (运行时可添加)
trait_def: id, name, icon, facility_type_id, bonus_percentage

-- 产业链 (运行时可配置)
conversion: id, facility_type_id, input_json, output_resource_id, output_qty, base_time
```

---

## 9. 认证与安全

### 9.1 JWT 配置

```yaml
jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here}
  expiration: 86400000  # 24小时
  refresh-expiration: 604800000  # 7天
```

### 9.2 密码加密

使用 BCrypt，强度 10。

### 9.3 安全过滤链

```
请求 -> JwtAuthenticationFilter -> UsernamePasswordAuthenticationFilter
                                          |
                                    JwtTokenProvider
                                          |
                                    SecurityContextHolder
```

---

## 10. 项目结构

```
dungeon-merchant/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/dungeon/merchant/
│   │   │   ├── DungeonMerchantApplication.java
│   │   │   │
│   │   │   ├── module/                    # 模块(按 M1-M8 划分)
│   │   │   │   ├── auth/
│   │   │   │   │   ├── controller/
│   │   │   │   │   ├── service/
│   │   │   │   │   ├── repository/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── dto/
│   │   │   │   │   └── security/
│   │   │   │   │
│   │   │   │   ├── account/
│   │   │   │   ├── character/
│   │   │   │   ├── facility/
│   │   │   │   ├── warehouse/
│   │   │   │   ├── production/
│   │   │   │   ├── config/
│   │   │   │   └── common/
│   │   │   │
│   │   │   ├── config/                     # 全局配置
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── MybatisPlusConfig.java
│   │   │   │   └── JwtConfig.java
│   │   │   │
│   │   │   ├── exception/                  # 统一异常处理
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── BusinessException.java
│   │   │   │
│   │   │   └── util/                       # 工具类
│   │   │       ├── JwtUtil.java
│   │   │       └── ResponseUtil.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── migration/
│   │               └── V1__init.sql
│   │
│   └── test/
│       └── java/com/dungeon/merchant/
│           ├── module/
│           │   ├── auth/
│           │   ├── character/
│           │   ├── facility/
│           │   ├── production/
│           │   └── warehouse/
│           └── common/
│               └── ProductionCalculatorTest.java
│
├── docs/
│   └── superpowers/
│       └── specs/
│           └── 2026-04-01-dungeon-merchant-backend-v1.md
│
└── .trellis/
    ├── specs/
    ├── tasks/
    └── workflow.md
```

---

## 附录

### A. 关键文件清单

| 文件 | 描述 | 所属模块 |
|-----|------|---------|
| SecurityConfig.java | Spring Security 配置 | Auth |
| JwtTokenProvider.java | JWT 工具类 | Auth |
| JwtAuthenticationFilter.java | JWT 过滤器 | Auth |
| RarityDef.java | 稀有度实体 | Config |
| TraitDef.java | 特性实体 | Config |
| Character.java | 角色实体 | Character |
| Facility.java | 设施实体 | Facility |
| FacilitySlot.java | 设施槽位实体 | Facility |
| Conversion.java | 转换规则实体 | Config |
| Warehouse.java | 仓库实体 | Warehouse |
| ProductionCalculator.java | 生产计算(纯函数) | Production |
| ProductionScheduler.java | 定时任务 | Production |
| GlobalExceptionHandler.java | 统一异常处理 | Common |

### B. TODO 列表

- [ ] 项目初始化 (pom.xml, application.yml)
- [ ] Common 模块 (响应封装/异常/工具)
- [ ] Auth 模块 (JWT/注册/登录)
- [ ] Config 模块 (初始数据/配置查询)
- [ ] Account 模块
- [ ] Character 模块
- [ ] Facility 模块
- [ ] Warehouse 模块
- [ ] Production 模块
- [ ] 单元测试
- [ ] API 联调

---

> 文档版本: v1.0
> 创建日期: 2026-04-01
> 下次更新: 确认后进入 Phase 1 开发
