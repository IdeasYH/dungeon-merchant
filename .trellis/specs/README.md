# Trellis 技术规范索引

本目录存放 `Dungeon Merchant` 项目长期有效的技术规范与编码约束。

后续开发默认以 `.trellis/specs/` 为主规范来源。若历史说明、阶段性文档或分散在仓库其他位置的旧规则与本目录不一致，以本目录为准。

## 目录用途

- 记录长期有效的工程原则、模块边界、代码质量要求与协作规则
- 作为 Architect 与 Operator 的共同规范基线
- 为后续功能设计、任务拆分与代码实现提供统一约束

本目录**不用于**存放一次性的功能设计、某一轮实施计划、阶段性进度或临时任务说明。

## 推荐阅读顺序

1. `01-engineering-principles.md`：核心工程思想与长期设计偏好
2. `02-module-boundaries.md`：模块边界、契约与分层协作规则
3. `03-code-quality-rules.md`：日常编码、修改与自检要求
4. `04-workflow-and-docs.md`：Trellis 工作流、角色职责与文档使用方式

## 文档列表

- `01-engineering-principles.md`
  - 定义长期有效的工程原则，如单一职责、组合优于继承、状态最小化、增量扩展优先
- `02-module-boundaries.md`
  - 定义模块、依赖、边界校验、文档边界与分层协作方式
- `03-code-quality-rules.md`
  - 定义代码质量、自检、修改策略与提交时应遵循的长期要求
- `04-workflow-and-docs.md`
  - 定义 Architect / Operator 协作方式，以及 Trellis 文档的职责划分

## 与其他文档的关系

- `.trellis/specs/`
  - 存放长期有效规范
- `.trellis/tasks/`
  - 存放任务单与执行拆分
- `docs/superpowers/specs/`
  - 存放具体需求或某一轮任务的设计文档
- `docs/superpowers/plans/`
  - 存放实施计划
- `docs/superpowers/DEVELOPMENT_STATUS.md`
  - 存放开发进度与验证记录

## 维护原则

- 只保留长期有效、可持续复用的规则
- 同类规则只保留一处主口径，避免重复与冲突
- 发生稳定共识后优先更新本目录，再决定是否同步其他说明文档
