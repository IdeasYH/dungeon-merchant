# Trellis 长期编程规范整理设计

> 日期：2026-04-02  
> 适用仓库：`dungeon-merchant-claude`

## 目标

将当前项目中分散的、长期有效的编程规范整理并统一收敛到 `.trellis/specs/` 目录下，形成后续开发的主规范来源。

本次整理重点只覆盖：

- 编程思想
- 代码结构与模块边界要求
- 长期有效的编码约束
- 文档与协作流程约定

不纳入一次性的任务设计、阶段性计划和临时进度记录。

## 范围

### 包含

- 从以下文件提炼长期有效规范：
  - `AGENTS.md`
  - `CLAUDE.md`
  - `docs/ai-coding-standards.md`
  - `.trellis/workflow.md`
- 更新 `.trellis/specs/README.md`
- 在 `.trellis/specs/` 下建立按主题拆分的规范文件

### 不包含

- `docs/superpowers/specs/` 下的任务级设计文档
- `docs/superpowers/plans/` 下的实施计划
- `docs/superpowers/DEVELOPMENT_STATUS.md` 这类阶段性进度文档
- 某一次需求、某一轮任务、某一个页面的临时要求

## 现状问题

当前项目的长期规范分散在多个位置：

- `AGENTS.md` 中定义了角色、权限和部分协作约束
- `CLAUDE.md` 中定义了实现偏好
- `docs/ai-coding-standards.md` 中定义了编码思想
- `.trellis/workflow.md` 中定义了 Trellis 工作流与协作方式

这些内容虽然有效，但存在以下问题：

1. **规范入口分散**：后续开发时需要在多个文件来回查找
2. **长期规范与临时文档混杂**：不利于建立稳定的开发基线
3. **Trellis 主目录未成为唯一入口**：与“以后以 Trellis specs 为主”的目标不一致
4. **索引文件过空**：`.trellis/specs/README.md` 当前几乎没有实质内容

## 方案选择

本次采用“按主题拆分长期规范文件 + 索引统一入口”的方式。

### 方案说明

在 `.trellis/specs/` 下建立多份聚焦文件，每份文件只承担一个长期职责，并由 `README.md` 统一索引：

- `01-engineering-principles.md`
- `02-module-boundaries.md`
- `03-code-quality-rules.md`
- `04-workflow-and-docs.md`

### 选择理由

- 比单一大文件更容易维护
- 比只做链接索引更能真正完成规范收敛
- 更适合作为后续开发的主参考来源
- 便于以后继续扩展主题规范而不破坏现有结构

## 目标结构

整理完成后，`.trellis/specs/` 目录应至少包含：

```text
.trellis/specs/
├── README.md
├── 01-engineering-principles.md
├── 02-module-boundaries.md
├── 03-code-quality-rules.md
└── 04-workflow-and-docs.md
```

## 各规范文件职责

### 1. `01-engineering-principles.md`

聚焦长期有效的编程思想：

- 小而单一职责
- 组合优于继承
- 状态最小化
- 增量扩展优于重写
- 保持核心逻辑易于理解与测试

主要来源：

- `docs/ai-coding-standards.md`
- `CLAUDE.md`

### 2. `02-module-boundaries.md`

聚焦系统边界与模块协作规则：

- 显式类型边界
- 输入校验
- 依赖显式注入
- 模块通过稳定接口通信
- 领域逻辑与 IO / 框架胶水分离

主要来源：

- `docs/ai-coding-standards.md`
- `CLAUDE.md`

### 3. `03-code-quality-rules.md`

聚焦日常编码要求：

- 函数职责与长度控制
- 命名与可读性要求
- 避免隐藏状态与隐式耦合
- 错误处理与边界防御
- 代码修改优先选择最小安全变更

主要来源：

- `docs/ai-coding-standards.md`
- `AGENTS.md`

### 4. `04-workflow-and-docs.md`

聚焦开发协作与 Trellis 使用方式：

- Architect / Operator 的职责边界
- Trellis 工作流中的文档位置和用途
- 设计文档、实施计划、进度文档的区别
- 以后以 `.trellis/specs/` 为主规范来源
- 何时更新规范、何时更新任务/进度文档

主要来源：

- `.trellis/workflow.md`
- `AGENTS.md`

## 索引文件策略

`.trellis/specs/README.md` 将从“空壳索引”升级为真正的入口文件，至少包含：

- 本目录用途
- 各规范文件的用途摘要
- 推荐阅读顺序
- 明确声明：后续开发默认以 `.trellis/specs/` 为主规范来源

## 迁移原则

本次不是机械复制，而是“提炼 + 去重 + 统一口径”。

### 具体原则

1. **保留长期有效内容**
   - 只迁移长期适用的约束与原则

2. **移除任务型内容**
   - 不把某次需求或某轮实现说明当作长期规范

3. **统一表述**
   - 相同意思的规则只保留一份，避免多个文件重复表达

4. **保持 Trellis 友好**
   - 文件名、职责、目录结构应适合以后持续扩展

## 风险与控制

### 风险 1：把临时要求混入长期规范

**控制方式：**
- 每条迁移内容都检查是否能长期适用
- 不能长期适用的内容保留在原文档，不迁入 `.trellis/specs/`

### 风险 2：迁移后口径不一致

**控制方式：**
- 以 `.trellis/specs/` 为最终主口径
- 在 README 中明确规范优先级

### 风险 3：文件拆分过细或过粗

**控制方式：**
- 控制在 4 份左右主题文件
- 每份文件一个清晰职责，避免过度切分

## 验收标准

整理完成后应满足：

1. `.trellis/specs/` 中存在清晰的长期规范结构
2. `README.md` 可作为唯一入口
3. 长期编程思想与要求已从分散文档中提炼出来
4. 规范按主题拆分，职责清楚
5. 后续可明确以 `.trellis/specs/` 为主规范来源

## 实施顺序

1. 扫描现有规范来源并提炼长期内容
2. 设计 `.trellis/specs/` 的最终结构
3. 写入主题规范文件
4. 更新索引 README
5. 自检是否包含临时内容或重复内容
6. 提交整理结果

