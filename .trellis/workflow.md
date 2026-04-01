# Trellis Workflow - Dungeon Merchant

## Session Roles
- **Architect (Claude)**: 负责架构设计、技术规范、代码审查
- **Operator (Human)**: 负责人工操作、测试验证、最终决策

## 协作流程

### 1. 任务发起
- Operator 提出需求或任务
- Architect 使用 `trellis:brainstorm` 技能进行需求探索
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
