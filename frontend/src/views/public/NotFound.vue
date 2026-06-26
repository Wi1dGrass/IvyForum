<template>
  <div class="not-found">
    <div class="code">404</div>
    <p>页面走丢了…</p>
    <el-button type="primary" @click="$router.push('/')">回到首页</el-button>
  </div>
</template>
<script setup lang="ts"></script>
<style scoped lang="scss">
.not-found { min-height: 60vh; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 12px; }
.code { font-size: 90px; font-weight: 800; background: var(--sf-gradient); -webkit-background-clip: text; background-clip: text; color: transparent; }
</style>