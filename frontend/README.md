# Dungeon Merchant 前端

这是 `Dungeon Merchant` 的 Vue 3 + Vite 前端工程，当前已提供登录、注册、首页账户展示，以及与后端认证接口联调所需的基础能力。

## 本地开发

```bash
npm install
npm run dev
```

默认开发地址由 Vite 提供，`/api` 请求会代理到 `http://localhost:8080`。

## 可用脚本

```bash
npm run dev
npm run build
npm run preview
npm test
```

## 目录说明

- `src/App.vue`：应用壳层与顶部导航
- `src/views/`：登录、注册、首页视图
- `src/stores/`：Pinia 状态管理
- `src/services/`：认证、账户与 HTTP 请求封装
- `src/utils/`：本地会话工具

## 联调说明

启动前端前，请先确保后端服务已在 `http://localhost:8080` 运行。
