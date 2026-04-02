# 工作流与文档规范

## 1. 目标
- 明确长期的 Trellis 协作流程、角色边界与文档责任，确保 Architect 与 Operator 在架构、实现、验证、文档三条线上的期望一致。
- 让变更、提交、复审等工作在清晰的阶段中展开，避免短期任务驱动的临时规则侵蚀长期规范。

## 2. 角色职责
- **Architect** 作为技术负责人，主导系统设计、模块边界、规范产出与代码质量；除具体实现外，还负责撰写 `.trellis/specs/` 里的长期规则，以及触发、审核任务单与设计文档。
- **Operator** 作为产品/测试负责人，掌握优先级、业务验收与线上发布批准；负责测试验证，接受 Architect 提交的实现与文档，并在阶段性决策与上线前提供最终认可。
- 两者共识是：Architect 提供长期可复用的方案与规范，Operator 负责验证与执行决策，避免职责交叉引发模糊或延迟。

## 3. 工作流阶段
1. **上下文准备**：先审阅现有 `.trellis/specs/`、`AGENTS.md` 以及相关项目文档，评估影响面，避免重复或阶段性内容进入长期规范。
2. **需求探索与澄清**：使用 `superpowers:brainstorming` 为主，必要时结合 `oh-my-claudecode:deep-interview` 或 `/deep-dive` 追问模糊边界，每次只问一个问题，多选题优先，逐段获得 Operator 认可后再继续。
3. **方案与规范**：衡量 2-3 个方案的 trade-offs，推荐的长效方案需书写于 `.trellis/specs/`；设计文档承载细节，并在获得审批后触发 `superpowers:writing-plans`。
4. **实现与审查**：根据规范实现代码，执行相应检查（如 `trellis:check-backend`/`trellis:check-frontend`），并请求 Operator 审查；若涉及跨层、跨模块改动，使用 `trellis:check-cross-layer` 预防遗漏。
5. **交付与同步**：完成后确保文档、任务、提交同步，遵守提交与 Checkpoint 规则，保证长期规范与实施成果一致。

## 4. 文档分类与用途
- `.trellis/specs/`：长期规范主目录，用于模块边界、工作流、编码标准等长期有效规则；是项目协作与实现的统一基线。
- `.trellis/tasks/`：任务单与拆分仅用于具体执行项；不应写入长期原则。
- `docs/superpowers/specs/`：每轮需求/功能的设计文档，在获得 Operator 审批后保存，并作为实施计划的参考；不作为长期规则。
- `docs/superpowers/plans/`、`docs/superpowers/DEVELOPMENT_STATUS.md` 等用于实施计划与状态记录，不宜写入模块边界或角色职责内容。
- 任何规范类更新必须同时说明适用对象与边界，避免读者在不同文档之间产生职责冲突。

## 5. 规范优先级
1. `.trellis/specs/` 中的长期规范，尤其涉及边界、角色与文档职责的部分，优先于其他说明。
2. `AGENTS.md` 中定义的角色与项目偏好作为背景参考；当出现差异时，优先以 `.trellis/specs/` 的最新口径为准，确保 Operator/Architect 协作有一致方向。
3. 其他设计、计划、进度与历史说明文档只作为上下文补充，不替代本目录中的长期规范。

## 6. 提交与同步约定
- 更新 `.trellis/specs/`、`prd.md`、`PLANNING.md` 等核心规范后应立即完成 git 提交；在必要时配合自动化脚本完成推送，保持规范变更可追踪。
- 任何影响角色、边界或工作流的变更都需同步告知 Operator，确保双角色认知一致。
- 项目要求每次规范变更以文档形式记录关键点，附带必要的变更说明，并保留在 `.trellis/specs/` 目录。

## 7. Checkpoint 规则
- 当上下文占用或工作量提示接近 50% 时，立即在 `CONTEXT.md` 中写入当前状态摘要，并提醒 Operator 进行 session 重启，以便保留关键上下文供后续使用。
- 同步后继续前进前，确认 `CONTEXT.md` 中的变更已被理解，避免因上下文超过载荷造成误解。
- Checkpoint 行动包括记录进度、提交更改、通知 Operator，并保留说明以便下次恢复。
