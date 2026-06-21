<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getUserProfile } from '@/api/user'

const router = useRouter()
const userInfo = ref(null)
const profile = ref(null)
const loading = ref(true)
const activeTab = ref('likedSongs')

const tabs = [
  { key: 'likedSongs', label: '赞过的歌曲', icon: '❤️' },
  { key: 'favoriteSongs', label: '收藏的歌曲', icon: '🎵' },
  { key: 'favoritePlaylists', label: '收藏的歌单', icon: '📋' },
  { key: 'favoriteSingers', label: '收藏的歌手', icon: '🎤' },
]

const currentList = computed(() => {
  if (!profile.value) return []
  const map = {
    likedSongs: profile.value.likedSongs,
    favoriteSongs: profile.value.favoriteSongs,
    favoritePlaylists: profile.value.favoritePlaylists,
    favoriteSingers: profile.value.favoriteSingers,
  }
  return map[activeTab.value] || []
})

function getCover(item) {
  return item.cover || item.avatar || ''
}

function handleImageError(e) {
  e.target.src = 'https://via.placeholder.com/300/3b82f6/ffffff?text=Music'
}

function logout() {
  localStorage.removeItem('user_token')
  localStorage.removeItem('user_info')
  router.push('/login')
}

onMounted(async () => {
  const info = localStorage.getItem('user_info')
  if (!info) {
    router.push('/login')
    return
  }
  userInfo.value = JSON.parse(info)
  try {
    const res = await getUserProfile(userInfo.value.id)
    profile.value = res.data
  } catch (err) {
    console.error('获取用户信息失败:', err)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="profile-page">
    <header class="top-header">
      <div class="header-inner">
        <router-link to="/" class="back-link">← 返回首页</router-link>
        <h2>个人中心</h2>
        <button class="logout-btn" @click="logout">退出登录</button>
      </div>
    </header>

    <div class="profile-content" v-loading="loading">
      <!-- 用户信息卡片 -->
      <div class="user-card" v-if="userInfo">
        <div class="user-avatar">
          <img :src="userInfo.avatar || 'https://via.placeholder.com/100/3b82f6/ffffff?text=U'" @error="handleImageError" />
        </div>
        <div class="user-detail">
          <h2>{{ userInfo.nickname || userInfo.username }}</h2>
          <p class="user-username">@{{ userInfo.username }}</p>
          <p class="user-stats" v-if="profile">
            赞过 {{ profile.likedSongs?.length || 0 }} 首歌 ·
            收藏 {{ (profile.favoriteSongs?.length || 0) + (profile.favoritePlaylists?.length || 0) + (profile.favoriteSingers?.length || 0) }} 个内容
          </p>
        </div>
      </div>

      <!-- Tab 切换 -->
      <div class="tab-bar">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          :class="['tab-item', { active: activeTab === tab.key }]"
          @click="activeTab = tab.key"
        >
          {{ tab.icon }} {{ tab.label }}
        </button>
      </div>

      <!-- 列表 -->
      <div class="list-section" v-if="currentList.length > 0">
        <!-- 歌曲列表 -->
        <template v-if="activeTab === 'likedSongs' || activeTab === 'favoriteSongs'">
          <div v-for="item in currentList" :key="item.id" class="list-item">
            <img :src="getCover(item)" class="item-cover" @error="handleImageError" />
            <div class="item-info">
              <span class="item-name">{{ item.name }}</span>
              <span class="item-artist" v-if="item.singerName">{{ item.singerName }}</span>
            </div>
          </div>
        </template>

        <!-- 歌单列表 -->
        <template v-else-if="activeTab === 'favoritePlaylists'">
          <div v-for="item in currentList" :key="item.id" class="list-item">
            <img :src="getCover(item)" class="item-cover" @error="handleImageError" />
            <div class="item-info">
              <span class="item-name">{{ item.name }}</span>
              <span class="item-artist" v-if="item.songCount">{{ item.songCount }} 首</span>
            </div>
          </div>
        </template>

        <!-- 歌手列表 -->
        <template v-else-if="activeTab === 'favoriteSingers'">
          <div v-for="item in currentList" :key="item.id" class="list-item singer-item">
            <img :src="getCover(item)" class="item-avatar" @error="handleImageError" />
            <div class="item-info">
              <span class="item-name">{{ item.name }}</span>
              <span class="item-artist" v-if="item.region">{{ item.region }}</span>
            </div>
          </div>
        </template>
      </div>

      <el-empty v-else-if="!loading" description="暂无内容" :image-size="120" />
    </div>
  </div>
</template>

<style scoped lang="scss">
$primary: #3b82f6;

.profile-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #eff6ff 0%, #dbeafe 100%);
}

.top-header {
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(59,130,246,0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 56px;
}

.back-link { color: $primary; font-size: 14px; font-weight: 500; }
.back-link:hover { text-decoration: underline; }
.header-inner h2 { font-size: 16px; font-weight: 600; color: #1e293b; }

.logout-btn {
  padding: 6px 16px;
  border-radius: 16px;
  border: 1px solid #e2e8f0;
  background: #fff;
  color: #64748b;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}
.logout-btn:hover { color: #ef4444; border-color: #ef4444; }

.profile-content { max-width: 800px; margin: 0 auto; padding: 24px 20px 40px; }

.user-card {
  background: #fff;
  border-radius: 16px;
  padding: 28px 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 12px rgba(59,130,246,0.1);
  margin-bottom: 24px;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #fff;
  box-shadow: 0 4px 12px rgba(59,130,246,0.15);
}
.user-avatar img { width: 100%; height: 100%; object-fit: cover; }

.user-detail h2 { font-size: 20px; font-weight: 700; color: #1e293b; }
.user-username { font-size: 13px; color: #94a3b8; margin-top: 2px; }
.user-stats { font-size: 13px; color: #64748b; margin-top: 6px; }

.tab-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.tab-item {
  padding: 8px 18px;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}
.tab-item:hover { border-color: $primary; color: $primary; }
.tab-item.active { background: $primary; color: #fff; border-color: $primary; }

.list-section {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(59,130,246,0.08);
}

.list-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
  transition: background 0.2s;
}
.list-item:last-child { border-bottom: none; }
.list-item:hover { background: rgba(59,130,246,0.04); }

.item-cover { width: 44px; height: 44px; border-radius: 8px; object-fit: cover; margin-right: 14px; }
.item-avatar { width: 48px; height: 48px; border-radius: 50%; object-fit: cover; margin-right: 14px; }

.item-info { display: flex; flex-direction: column; gap: 2px; }
.item-name { font-size: 14px; font-weight: 500; color: #1e293b; }
.item-artist { font-size: 12px; color: #94a3b8; }
</style>