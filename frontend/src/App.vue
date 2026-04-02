<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="topbar__brand">
        <span class="topbar__sigil">DM</span>
        <div>
          <p class="topbar__eyebrow">Dungeon Merchant Console</p>
          <h1 class="topbar__title">Guild Ledger</h1>
        </div>
      </div>

      <nav class="topbar__nav" aria-label="Primary">
        <RouterLink class="topbar__link" to="/register">
          Create an account
        </RouterLink>
        <RouterLink class="topbar__link" to="/login">
          Sign in
        </RouterLink>
        <RouterLink class="topbar__link topbar__link--accent" to="/">
          Load account
        </RouterLink>
      </nav>
    </header>

    <main class="page-stage">
      <RouterView />
    </main>
  </div>
</template>
