# Trellis Workflow - Dungeon Merchant

## Session Roles
- **Architect (Claude)**: 负责架构设计、技术规范、代码审查
- **Operator (Human)**: 负责人工操作、测试验证、最终决策

---

## 需求探索流程 (Brainstorming)

### 核心技能组合
本项目使用 **双技能融合** 的苏格拉底式追问流程：

| 技能 | 来源 | 用途 |
|-----|------|-----|
| `superpowers:brainstorming` | superpowers 插件 | 结构化需求探索：上下文 → 提问 → 方案 → 设计 |
| `oh-my-claudecode:deep-interview` | oh-my-claudecode | Socratic 深层追问，带 ambiguity gating |
| `/deep-dive` | oh-my-claudecode | trace 因果追踪 + deep-interview 组合 |

### 融合流程

```
1. 上下文探索
   └─> 检查项目文件、文档、近期提交

2. 需求澄清 (superpowers:brainstorming)
   ├─ 一次一问，多选优先
   ├─ YAGNI 原则：坚决剔除不必要功能
   └─ 渐进式验证：每段获批后再继续

3. 深层追问 (deep-interview 触发条件)
   ├─ 当需求涉及复杂权衡
   ├─ 当存在模糊地带需要数学式澄清
   └─ 使用 /deep-dive 进入 trace + interview 组合

4. 方案提议
   ├─ 提出 2-3 个方案并附 trade-offs
   └─ 明确推荐方案及理由

5. 设计呈现
   ├─ 分段展示，每段获批
   └─ 覆盖：架构、组件、数据流、错误处理

6. 设计文档 → 实施计划
   └─ 写设计文档 → 触发 writing-plans
```

### 追问原则
- **一次一问**：避免信息过载
- **多选优于开放**：降低回答门槛
- **Hard Gate**：设计方案获批前禁止写代码
- **渐进获批**：分段验证，降低返工

---

## 协作流程

### 1. 任务发起
- Operator 提出需求或任务
- Architect 触发 brainstorming 技能组合进行需求探索
- Architect 创建/更新 `.trellis/tasks/` 中的任务单

### 2. 设计与规范
- Architect 创建/更新 `.trellis/specs/` 中的技术规范
- 重大决策需获得 Operator 确认

### 3. 实现与审查
- Architect 实现代码，使用 `trellis:check-backend/frontend` 检查
- 完成后请求 Operator 进行代码审查

### 4. 提交与同步
- 更新后自动执行 git commit
- Context 接近 50% 时执行 Checkpoint 逻辑

---

## 自动化协议

### Git 同步触发条件
- 更新 `.trellis/` 下任何文档
- 更新 `prd.md` 或 `PLANNING.md`
- 完成阶段性任务

### Checkpoint 逻辑
当 Context 占用接近 50% 时：
1. 摘要当前进度到 `CONTEXT.md`
2. 提醒 Operator 重启 Session
3. 保留关键上下文供后续参考

---

## 技能速查

| 场景 | 使用的技能/命令 |
|-----|----------------|
| 通用需求探索 | `superpowers:brainstorming` |
| 复杂模糊需求 | `/deep-dive` 或 `oh-my-claudecode:deep-interview` |
| 实施前检查 | `trellis:check-backend` / `trellis:check-frontend` |
| 跨层检查 | `trellis:check-cross-layer` |
| 结束分支 | `trellis:finish-work` |
| 写实施计划 | `superpowers:writing-plans` |
