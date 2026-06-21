<script setup>
import { ref, onMounted, computed } from 'vue'
import { getHomeData } from '@/api/recommend'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const banners = ref([])
const todayRecommends = ref([])
const playlists = ref([])
const singers = ref([])
const songs = ref([])
const loading = ref(true)

const resourceTypeLabels = { singer: '歌手', song: '歌曲', playlist: '歌单' }
const resourceTypeIcons = { singer: '🎤', song: '🎵', playlist: '📋' }

const router = useRouter()

const userInfo = computed(() => { try { const info = localStorage.getItem('user_info'); return info ? JSON.parse(info) : null } catch { return null } })
const isLoggedIn = computed(() => !!userInfo.value)

async function fetchData() {
  loading.value = true
  try {
    const res = await getHomeData()
    const data = res.data || {}
    banners.value = data.banners || []
    todayRecommends.value = data.todayRecommends || []
    playlists.value = data.playlists || []
    singers.value = data.singers || []
    songs.value = data.songs || []
  } catch (err) { console.error(err) } finally { loading.value = false }
}

function normalizeMediaUrl(url) {
  if (!url) return ''
  if (/^(https?:)?\/\//i.test(url) || url.startsWith('data:')) return url
  if (url.startsWith('/api/')) return url
  if (url.startsWith('/upload/')) return '/api' + url
  if (url.startsWith('upload/')) return '/api/' + url
  return url
}

function getCover(item) { return normalizeMediaUrl(item.cover || item.resourceCover || item.avatar || '') }
function handleImageError(e) { e.target.onerror = null; e.target.style.display = 'none' }
function formatCount(n) { if (!n && n !== 0) return ''; if (n > 9999) return (n / 10000).toFixed(1) + '万'; return n }

function goPlaylist(item) { router.push('/detail/playlist/' + (item.id || item.resourceId)) }
function goSinger(item) { router.push('/detail/singer/' + (item.id || item.resourceId)) }
function handleTodayClick(item) {
  if (item.resourceType === 'song') { playSong(item) }
  else if (item.resourceType === 'singer') { router.push('/detail/singer/' + (item.resourceId || item.id)) }
  else if (item.resourceType === 'playlist') { router.push('/detail/playlist/' + (item.resourceId || item.id)) }
}
function logout() { localStorage.removeItem('user_token'); localStorage.removeItem('user_info'); router.push('/') }

function playSong(item) {
  const song = {
    id: item.resourceId || item.id,
    name: item.resourceName || item.title || item.name,
    cover: item.resourceCover || item.cover,
    url: item.resourceUrl || item.url,
    singerId: item.singerId,
    singerName: item.singerName || item.resourceSinger,
    duration: item.duration
  }
  window.dispatchEvent(new CustomEvent('play-song', { detail: song }))
}

onMounted(() => fetchData())
</script>

<template>
  <div class="home-page">
    <header class="top-header">
      <div class="header-inner">
        <div class="logo-area">
          <span class="logo-icon">🎵</span>
          <span class="logo-text">悦听音乐</span>
        </div>
        <nav class="header-nav">
          <router-link to="/" class="nav-link active">首页</router-link>
          <router-link to="/list/playlists" class="nav-link">歌单</router-link>
          <router-link to="/list/singers" class="nav-link">歌手</router-link>
          <router-link to="/list/songs" class="nav-link">排行榜</router-link>
        </nav>
        <div class="header-actions">
          <span v-if="isLoggedIn" class="user-info-group">
            <router-link to="/profile" class="user-link">
              <span class="user-avatar-mini">{{ userInfo?.nickname?.charAt(0) || userInfo?.username?.charAt(0) || 'U' }}</span>
              <span class="user-name">{{ userInfo?.nickname || userInfo?.username }}</span>
            </router-link>
            <a href="javascript:;" class="logout-link" @click="logout">退出</a>
          </span>
          <router-link v-else to="/login" class="login-btn">登录</router-link>
        </div>
      </div>
    </header>

    <div class="page-content" v-loading="loading">
      <section class="section" v-if="banners.length > 0">
        <el-carousel :interval="4000" height="380px" indicator-position="outside" class="banner-carousel">
          <el-carousel-item v-for="item in banners" :key="item.id">
            <div class="banner-card" @click="handleTodayClick(item)">
              <img :src="getCover(item)" :alt="item.title" class="banner-cover" @error="handleImageError($event)" />
              <div class="banner-overlay">
                <span class="banner-tag">{{ resourceTypeIcons[item.resourceType] }} {{ resourceTypeLabels[item.resourceType] }}</span>
                <h2 class="banner-title">{{ item.title }}</h2>
                <p class="banner-subtitle" v-if="item.resourceName">{{ item.resourceName }}</p>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <section class="section" v-if="todayRecommends.length > 0">
        <div class="section-header">
          <h2 class="section-title">✨ 今日推荐</h2>
          <span class="section-more">{{ todayRecommends.length }} 个推荐</span>
        </div>
        <div class="today-grid">
          <div v-for="item in todayRecommends" :key="item.id" class="today-card" @click="handleTodayClick(item)">
            <div class="card-img-wrap">
              <img :src="getCover(item)" :alt="item.title" class="card-img" @error="handleImageError($event)" />
              <span class="card-tag">{{ resourceTypeIcons[item.resourceType] }} {{ resourceTypeLabels[item.resourceType] }}</span>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ item.resourceName || item.title }}</h3>
              <p class="card-desc" v-if="item.resourceSinger || item.singerName">{{ item.resourceSinger || item.singerName }}</p>
            </div>
          </div>
        </div>
      </section>

      <section class="section" v-if="playlists.length > 0">
        <div class="section-header">
          <h2 class="section-title">📋 推荐歌单</h2>
          <router-link to="/list/playlists" class="section-more">查看更多 →</router-link>
        </div>
        <div class="card-grid">
          <div v-for="item in playlists" :key="item.id" class="card" @click="goPlaylist(item)">
            <div class="card-img-wrap">
              <img :src="getCover(item)" :alt="item.name" class="card-img" @error="handleImageError($event)" />
              <span class="card-count" v-if="item.songCount">{{ item.songCount }} 首</span>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ item.name }}</h3>
              <p class="card-desc" v-if="item.description">{{ item.description }}</p>
            </div>
          </div>
        </div>
      </section>

      <section class="section" v-if="singers.length > 0">
        <div class="section-header">
          <h2 class="section-title">🎤 热门歌手</h2>
          <router-link to="/list/singers" class="section-more">查看更多 →</router-link>
        </div>
        <div class="singer-grid">
          <div v-for="item in singers" :key="item.id" class="singer-card" @click="goSinger(item)">
            <div class="singer-avatar-wrap">
              <img :src="getCover(item)" :alt="item.name" class="singer-avatar" @error="handleImageError($event)" />
            </div>
            <h3 class="singer-name">{{ item.name }}</h3>
            <p class="singer-region" v-if="item.region">{{ item.region }}</p>
          </div>
        </div>
      </section>

      <section class="section" v-if="songs.length > 0">
        <div class="section-header">
          <h2 class="section-title">🎵 热门歌曲</h2>
          <router-link to="/list/songs" class="section-more">查看更多 →</router-link>
        </div>
        <div class="song-list">
          <div v-for="(item, index) in songs" :key="item.id" class="song-item" @click="playSong(item)">
            <span class="song-index" :class="{ top3: index < 3 }">{{ index + 1 }}</span>
            <img :src="getCover(item)" :alt="item.name" class="song-cover" @error="handleImageError($event)" />
            <div class="song-info">
              <span class="song-name">{{ item.name }}</span>
              <span class="song-artist" v-if="item.singerName">{{ item.singerName }}</span>
            </div>
            <div class="song-stats">
              <span v-if="item.playCount">🎧 {{ formatCount(item.playCount) }}</span>
              <span v-if="item.likeCount">👍 {{ formatCount(item.likeCount) }}</span>
              <span v-if="item.favCount">⭐ {{ formatCount(item.favCount) }}</span>
            </div>
          </div>
        </div>
      </section>

      <el-empty v-if="!loading && !banners.length && !todayRecommends.length && !playlists.length && !singers.length && !songs.length" description="暂无内容" :image-size="200" />
    </div>

    <footer class="page-footer">
      <p>❤ 悦听音乐 · 用音乐温暖时光 ❤</p>
    </footer>
  </div>
</template>

<style scoped>

.home-page { min-height: 100vh; background: linear-gradient(180deg, #eff6ff 0%, #f8fafc 100%); }

.top-header { background: rgba(255,255,255,0.85); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px); border-bottom: 1px solid rgba(59,130,246,0.08); position: sticky; top: 0; z-index: 100; padding: 0 24px; }
.header-inner { max-width: 1280px; margin: 0 auto; display: flex; align-items: center; justify-content: space-between; height: 60px; }
.logo-area { display: flex; align-items: center; gap: 8px; }
.logo-icon { font-size: 24px; }
.logo-text { font-size: 20px; font-weight: 700; color: #3b82f6; letter-spacing: 2px; }
.header-nav { display: flex; gap: 28px; }
.nav-link { font-size: 15px; color: #1e293b; text-decoration: none; padding: 4px 0; border-bottom: 2px solid transparent; transition: all 0.2s; }
.nav-link:hover, .nav-link.active { color: #3b82f6; border-bottom-color: #3b82f6; }
.header-actions { display: flex; align-items: center; gap: 14px; }
.user-info-group { display: flex; align-items: center; gap: 14px; }
.user-link { display: flex; align-items: center; gap: 8px; text-decoration: none; color: #1e293b; }
.user-avatar-mini { width: 32px; height: 32px; border-radius: 50%; background: #3b82f6; color: #fff; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 600; }
.user-name { font-size: 14px; }
.logout-link { font-size: 13px; color: #94a3b8; text-decoration: none; }
.logout-link:hover { color: #3b82f6; }
.login-btn { padding: 6px 20px; border-radius: 20px; background: #3b82f6; color: #fff; font-size: 14px; text-decoration: none; transition: all 0.2s; }
.login-btn:hover { background: #2563eb; }

.page-content { max-width: 1280px; margin: 0 auto; padding: 24px 24px 32px; }
.section { margin-bottom: 32px; }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.section-title { font-size: 20px; font-weight: 700; color: #1e293b; margin: 0; }
.section-more { font-size: 13px; color: #94a3b8; text-decoration: none; }
.section-more:hover { color: #3b82f6; }

.banner-carousel :deep(.el-carousel__container) { border-radius: 16px; overflow: hidden; }
.banner-carousel :deep(.el-carousel__arrow) { background: rgba(255,255,255,0.2); color: #fff; }
.banner-carousel :deep(.el-carousel__arrow:hover) { background: rgba(255,255,255,0.4); }
.banner-carousel :deep(.el-carousel__button) { background: rgba(255,255,255,0.5); width: 28px; height: 4px; border-radius: 2px; }
.banner-carousel :deep(.is-active .el-carousel__button) { background: #fff; width: 44px; }

.banner-card { height: 100%; cursor: pointer; position: relative; overflow: hidden; }
.banner-card:hover .banner-cover { transform: scale(1.06); }
.banner-cover { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }
.banner-overlay { position: absolute; bottom: 0; left: 0; right: 0; padding: 50px 32px 28px; background: linear-gradient(transparent, rgba(0,0,0,0.7)); color: #fff; }
.banner-tag { display: inline-block; padding: 3px 14px; border-radius: 14px; font-size: 12px; margin-bottom: 8px; backdrop-filter: blur(4px); background: rgba(59,130,246,0.5); }
.banner-title { font-size: 1.5rem; font-weight: 700; margin-bottom: 4px; }
.banner-subtitle { font-size: 0.95rem; opacity: 0.85; }

.today-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; }
.today-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 2px 12px rgba(59, 130, 246, 0.08); }
.today-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(59, 130, 246, 0.08); }
.today-card:hover .card-img { transform: scale(1.06); }

.card-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 20px; }
.card { background: #fff; border-radius: 14px; overflow: hidden; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 2px 12px rgba(59, 130, 246, 0.08); }
.card:hover { transform: translateY(-6px); box-shadow: 0 8px 30px rgba(59, 130, 246, 0.08); }
.card:hover .card-img { transform: scale(1.08); }

.card-img-wrap { position: relative; width: 100%; padding-top: 100%; overflow: hidden; background: #f0f4ff; }
.card-img { position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s ease; }
.card-tag { position: absolute; top: 10px; left: 10px; padding: 3px 10px; border-radius: 10px; font-size: 11px; color: #fff; backdrop-filter: blur(4px); background: rgba(59,130,246,0.6); }
.card-count { position: absolute; bottom: 8px; right: 10px; font-size: 11px; color: #fff; background: rgba(0,0,0,0.5); padding: 2px 10px; border-radius: 10px; backdrop-filter: blur(2px); }
.card-body { padding: 10px 14px 14px; }
.card-title { font-size: 14px; font-weight: 600; color: #1e293b; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-desc { font-size: 12px; color: #94a3b8; margin-top: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.singer-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 20px; }
.singer-card { text-align: center; cursor: pointer; transition: all 0.3s ease; }
.singer-card:hover { transform: translateY(-6px); }
.singer-card:hover .singer-avatar { transform: scale(1.1); }
.singer-avatar-wrap { width: 110px; height: 110px; border-radius: 50%; overflow: hidden; margin: 0 auto 10px; box-shadow: 0 4px 16px rgba(59, 130, 246, 0.08); border: 3px solid #fff; }
.singer-avatar { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s ease; border-radius: 50%; }
.singer-name { font-size: 14px; font-weight: 600; color: #1e293b; }
.singer-region { font-size: 12px; color: #94a3b8; margin-top: 2px; }

.song-list { background: #fff; border-radius: 14px; overflow: hidden; box-shadow: 0 2px 12px rgba(59, 130, 246, 0.08); }
.song-item { display: flex; align-items: center; padding: 10px 20px; cursor: pointer; transition: all 0.2s; border-bottom: 1px solid #f1f5f9; }
.song-item:last-child { border-bottom: none; }
.song-item:hover { background: rgba(59,130,246,0.04); }
.song-index { width: 30px; font-size: 14px; font-weight: 600; color: #94a3b8; text-align: center; }
.song-index.top3 { color: #3b82f6; font-size: 16px; }
.song-cover { width: 42px; height: 42px; border-radius: 8px; object-fit: cover; margin: 0 12px; }
.song-info { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.song-name { font-size: 14px; font-weight: 500; color: #1e293b; }
.song-artist { font-size: 12px; color: #94a3b8; }
.song-stats { display: flex; gap: 14px; margin-right: 12px; }
.song-stats span { font-size: 12px; color: #94a3b8; }

.page-footer { text-align: center; padding: 32px 20px; font-size: 13px; color: #94a3b8; }

@media (max-width: 992px) {
  .today-grid, .card-grid { grid-template-columns: repeat(3, 1fr); }
  .singer-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .today-grid, .card-grid { grid-template-columns: repeat(2, 1fr); }
  .singer-grid { grid-template-columns: repeat(2, 1fr); }
  .header-nav { display: none; }
}

</style>
