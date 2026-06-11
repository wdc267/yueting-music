<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import MenuContent from './MenuContent.vue'

const router = useRouter()
const breadcrumbTitle = ref('数据看板')

const menuTitleMap = {
  '/dashboard': '数据看板',
  '/content/song': '歌曲管理',
  '/content/playlist': '歌单管理',
  '/content/singer': '歌手管理',
  '/operation/recommend': '首页推荐',
  '/system/dictionary': '字典管理',
  '/system/user': '用户管理'
}

router.afterEach((to) => {
  breadcrumbTitle.value = menuTitleMap[to.path] || to.meta?.title || to.path
})
</script>

<template>
  <div class="app-container">
    <aside class="app-sidebar">
      <div class="logo">悦听音乐</div>
      <MenuContent />
    </aside>
    <div class="app-main">
      <header class="app-header">
        <div class="header-left">{{ breadcrumbTitle }}</div>
        <div class="header-right">
          <span>管理员</span>
        </div>
      </header>
      <section class="app-content">
        <router-view />
      </section>
    </div>
  </div>
</template>