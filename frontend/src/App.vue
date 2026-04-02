<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="topbar__brand">
        <span class="topbar__sigil">DM</span>
        <div>
          <p class="topbar__eyebrow">Dungeon Merchant 控制台</p>
          <h1 class="topbar__title">Guild Ledger</h1>
        </div>
      </div>

      <nav class="topbar__nav" aria-label="Primary">
        <RouterLink class="topbar__link" to="/register">
          创建账户
        </RouterLink>
        <RouterLink class="topbar__link" to="/login">
          登录
        </RouterLink>
        <RouterLink class="topbar__link topbar__link--accent" to="/">
          加载账户
        </RouterLink>
      </nav>
    </header>

    <main class="page-stage">
      <RouterView />
    </main>
  </div>
</template>
