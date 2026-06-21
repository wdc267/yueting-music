<script setup>
import { ref, onMounted, computed } from 'vue'
import { getHomeData } from '@/api/recommend'
import { toggleLike, toggleFavorite, getLikeStatus, getFavoriteStatus } from '@/api/user'
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
const likedMap = ref({})
const favoritedMap = ref({})

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
async function handleLike(item, type) {
  if (!isLoggedIn.value) { ElMessage.warning('请先登录'); return }
  const key = type + '-' + item.id
  const res = await toggleLike({ userId: userInfo.value.id, resourceType: type, resourceId: item.id })
  likedMap.value[key] = res.data.liked
  if (res.data.count !== undefined) item.likeCount = res.data.count
}

async function handleFavorite(item, type) {
  if (!isLoggedIn.value) { ElMessage.warning('请先登录'); return }
  const key = type + '-' + item.id
  const res = await toggleFavorite({ userId: userInfo.value.id, resourceType: type, resourceId: item.id })
  favoritedMap.value[key] = res.data.favorited
  if (res.data.count !== undefined) {
    if (type === 'song') item.favCount = res.data.count
  }
}

async function loadUserStatus() {
  if (!isLoggedIn.value) return
  const uid = userInfo.value.id
  for (const item of songs.value) {
    try {
      const [likeRes, favRes] = await Promise.all([
        getLikeStatus(uid, 'song', item.id),
        getFavoriteStatus(uid, 'song', item.id)
      ])
      likedMap.value['song-' + item.id] = likeRes.data
      favoritedMap.value['song-' + item.id] = favRes.data
    } catch {}
  }
}


function goPlaylist(item) { router.push('/detail/playlist/' + (item.id || item.resourceId)) }
function goSinger(item) { router.push('/detail/singer/' + (item.id || item.resourceId)) }
function handleTodayClick(item) {
  if (item.resourceType === 'song') { playSong(item) }
  else if (item.resourceType === 'singer') { router.push('/detail/singer/' + (item.resourceId || item.id)) }
  else if (item.resourceType === 'playlist') { router.push('/detail/playlist/' + (item.resourceId || item.id)) }
}
function logout() { localStorage.removeItem('user_token'); localStorage.removeItem('user_info'); window.location.href = '/' }

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

onMounted(() => { fetchData(); setTimeout(loadUserStatus, 200) })
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
              <div class="banner-overlay"><span class="banner-tag">{{ resourceTypeIcons[item.resourceType] }} {{ resourceTypeLabels[item.resourceType] }}</span><h2 class="banner-title">{{ item.title }}</h2><p class="banner-subtitle" v-if="item.resourceName">{{ item.resourceName }}</p></div>
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
            <div class="card-actions" @click.stop>
              <button class="action-btn-sm" :class="{ active: favoritedMap['playlist-' + item.id] }" @click="handleFavorite(item, 'playlist')" title="收藏">
                {{ favoritedMap['playlist-' + item.id] ? '🌟' : '☆' }}
              </button>
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
            <div class="singer-actions" @click.stop>
              <button class="action-btn-sm" :class="{ active: favoritedMap['singer-' + item.id] }" @click="handleFavorite(item, 'singer')" title="收藏">
                {{ favoritedMap['singer-' + item.id] ? '🌟' : '☆' }}
              </button>
            </div>
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
            <div class="song-actions" @click.stop>
              <button class="action-btn action-btn-like" :class="{ active: likedMap['song-' + item.id] }" @click="handleLike(item, 'song')" title="点赞">
                {{ likedMap['song-' + item.id] ? '💕' : '🤍' }}
              </button>
              <button class="action-btn action-btn-fav" :class="{ active: favoritedMap['song-' + item.id] }" @click="handleFavorite(item, 'song')" title="收藏">
                {{ favoritedMap['song-' + item.id] ? '🌟' : '☆' }}
              </button>
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

<style scoped lang="scss">
/* ========== 二次元轻快明亮主题 ========== */
$c-pink: #ff85a2;
$c-blue: #60a5fa;
$c-purple: #c084fc;
$c-cyan: #67e8f9;
$c-yellow: #facc15;
$c-mint: #6ee7b7;
$c-coral: #fb7185;
$bg-page: linear-gradient(180deg, #f0f9ff 0%, #fdf2f8 50%, #faf5ff 100%);
$bg-card: rgba(255,255,255,0.85);
$bg-card-hover: rgba(255,255,255,0.95);
$shadow-card: 0 4px 20px rgba(148, 163, 184, 0.12);
$shadow-hover: 0 12px 36px rgba(148, 163, 184, 0.18);
$radius-lg: 20px;
$radius-md: 14px;
$radius-sm: 10px;

.home-page {
  min-height: 100vh;
  background: $bg-page;
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

/* 顶部导航 */
.top-header {
  background: rgba(255,255,255,0.75);
  backdrop-filter: blur(24px) saturate(180%);
  -webkit-backdrop-filter: blur(24px) saturate(180%);
  border-bottom: 1px solid rgba(196, 164, 252, 0.15);
  position: sticky; top: 0; z-index: 100; padding: 0 24px;
}
.header-inner { max-width: 1280px; margin: 0 auto; display: flex; align-items: center; justify-content: space-between; height: 60px; }
.logo-area { display: flex; align-items: center; gap: 8px; }
.logo-icon { font-size: 26px; filter: drop-shadow(0 2px 4px rgba(196, 164, 252, 0.4)); animation: logoBounce 2s ease-in-out infinite; }
@keyframes logoBounce { 0%,100% { transform: translateY(0) } 50% { transform: translateY(-4px) } }
.logo-text {
  font-size: 22px; font-weight: 800;
  background: linear-gradient(135deg, #ff85a2, #c084fc);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 3px;
}
.header-nav { display: flex; gap: 28px; }
.nav-link {
  font-size: 15px; color: #64748b; text-decoration: none; padding: 4px 0;
  position: relative; transition: all 0.3s;
}
.nav-link::after {
  content: ''; position: absolute; bottom: -2px; left: 50%; transform: translateX(-50%);
  width: 0; height: 2.5px; border-radius: 2px;
  background: linear-gradient(90deg, $c-pink, $c-purple);
  transition: width 0.3s ease;
}
.nav-link:hover, .nav-link.active { color: $c-purple; }
.nav-link:hover::after, .nav-link.active::after { width: 100%; }
.header-actions { display: flex; align-items: center; gap: 14px; }
.user-info-group { display: flex; align-items: center; gap: 14px; }
.user-link { display: flex; align-items: center; gap: 8px; text-decoration: none; color: #475569; }
.user-avatar-mini { width: 34px; height: 34px; border-radius: 50%; background: linear-gradient(135deg, #ff85a2, #c084fc); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 13px; font-weight: 700; box-shadow: 0 2px 8px rgba(196, 164, 252, 0.35); }
.user-name { font-size: 14px; font-weight: 500; }
.logout-link { font-size: 13px; color: #94a3b8; text-decoration: none; transition: color 0.2s; }
.logout-link:hover { color: $c-coral; }
.login-btn {
  padding: 7px 22px; border-radius: 22px;
  background: linear-gradient(135deg, #ff85a2, #c084fc);
  color: #fff; font-size: 14px; font-weight: 600; text-decoration: none;
  transition: all 0.3s; box-shadow: 0 4px 14px rgba(196, 164, 252, 0.3);
}
.login-btn:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(196, 164, 252, 0.45); }

/* 页面内容 */
.page-content { max-width: 1280px; margin: 0 auto; padding: 28px 24px 40px; }
.section { margin-bottom: 36px; }
.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 18px; }
.section-title {
  font-size: 22px; font-weight: 800; margin: 0;
  background: linear-gradient(135deg, #ff85a2, #c084fc);
  -webkit-background-clip: text; -webkit-text-fill-color: transparent;
  background-clip: text;
}
.section-more {
  font-size: 13px; color: #a78bfa; text-decoration: none; font-weight: 500;
  transition: all 0.2s; padding: 4px 12px; border-radius: 14px;
  background: rgba(196, 164, 252, 0.08);
}
.section-more:hover { background: rgba(196, 164, 252, 0.18); color: #7c3aed; }

/* 轮播图 */
.banner-carousel :deep(.el-carousel__container) { border-radius: 20px; overflow: hidden; }
.banner-carousel :deep(.el-carousel__arrow) { background: rgba(255,255,255,0.25); color: #fff; border-radius: 50%; width: 40px; height: 40px; }
.banner-carousel :deep(.el-carousel__arrow:hover) { background: rgba(255,255,255,0.5); }
.banner-carousel :deep(.el-carousel__button) { background: rgba(255,255,255,0.5); width: 32px; height: 5px; border-radius: 3px; }
.banner-carousel :deep(.is-active .el-carousel__button) { background: #fff; width: 48px; }
.banner-card { height: 100%; cursor: pointer; position: relative; overflow: hidden; }
.banner-card:hover .banner-cover { transform: scale(1.06); }
.banner-cover { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }
.banner-overlay {
  position: absolute; bottom: 0; left: 0; right: 0;
  padding: 60px 32px 28px;
  background: none;
  color: #fff;
  pointer-events: none;
}
.banner-tag {
  display: inline-block; padding: 4px 16px; border-radius: 16px;
  font-size: 12px; font-weight: 600; margin-bottom: 10px;
  backdrop-filter: blur(8px);
  background: rgba(0, 0, 0, 0.35);
  border: 1px solid rgba(255,255,255,0.3);
}
.banner-title { font-size: 1.6rem; font-weight: 800; margin-bottom: 4px; text-shadow: 0 2px 6px rgba(0,0,0,0.4); }
.banner-subtitle { font-size: 0.95rem; opacity: 0.9; }

/* 今日推荐 - 紧凑卡片 */
.today-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; }
.today-card {
  background: $bg-card; backdrop-filter: blur(12px);
  border-radius: $radius-md; overflow: hidden; cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: $shadow-card; border: 1px solid rgba(196, 164, 252, 0.1);
}
.today-card:hover {
  transform: translateY(-6px); box-shadow: $shadow-hover;
  border-color: rgba(196, 164, 252, 0.3);
}
.today-card:hover .card-img { transform: scale(1.08); }

/* 推荐歌单卡片 */
.card-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 20px; }
.card {
  background: $bg-card; backdrop-filter: blur(12px);
  border-radius: $radius-lg; overflow: hidden; cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: $shadow-card; border: 1px solid rgba(196, 164, 252, 0.1);
  position: relative;
}
.card:hover {
  transform: translateY(-8px); box-shadow: $shadow-hover;
  border-color: rgba(196, 164, 252, 0.3);
}
.card:hover .card-img { transform: scale(1.1); }

.card-img-wrap { position: relative; width: 100%; padding-top: 100%; overflow: hidden; background: linear-gradient(135deg, #ff85a2, #c084fc); }
.card-img { position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }
.card-tag {
  position: absolute; top: 10px; left: 10px;
  padding: 4px 12px; border-radius: 12px;
  font-size: 11px; font-weight: 600; color: #fff;
  backdrop-filter: blur(6px);
  background: rgba(196, 164, 252, 0.7);
}
.card-count {
  position: absolute; bottom: 8px; right: 10px;
  font-size: 11px; color: #fff;
  background: rgba(196, 132, 252, 0.7);
  padding: 3px 12px; border-radius: 12px; backdrop-filter: blur(4px);
}
.card-body { padding: 12px 14px 8px; }
.card-title { font-size: 14px; font-weight: 700; color: #334155; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-desc { font-size: 12px; color: #94a3b8; margin-top: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

/* ========== 二次元风格按钮 ========== */

/* 歌曲行的操作按钮 */
.song-actions {
  display: flex; gap: 6px; margin-left: 8px;
}
.action-btn {
  width: 34px; height: 34px; border-radius: 50%;
  border: none; cursor: pointer; font-size: 16px;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative; overflow: hidden;
}
/* 点赞按钮 */
.action-btn-like {
  background: transparent !important;
  color: #94a3b8;
  box-shadow: none !important;
}
.action-btn-like:hover { color: #fb7185; transform: scale(1.2); background: transparent !important; box-shadow: none !important; }
.action-btn-like.active { color: #fb7185; background: transparent !important; box-shadow: none !important; animation: heartBeat 0.6s ease; }
@keyframes heartBeat { 0% { transform: scale(1) } 25% { transform: scale(1.3) } 50% { transform: scale(0.95) } 100% { transform: scale(1) } }

/* 收藏按钮 */
.action-btn-fav {
  background: transparent !important;
  color: #94a3b8;
  box-shadow: none !important;
}
.action-btn-fav:hover { color: #facc15; transform: scale(1.2); background: transparent !important; box-shadow: none !important; }
.action-btn-fav.active { color: #facc15; background: transparent !important; box-shadow: none !important; animation: starPop 0.5s ease; }
@keyframes starPop { 0% { transform: scale(1) rotate(0deg) } 50% { transform: scale(1.4) rotate(15deg) } 100% { transform: scale(1) rotate(0deg) } }

/* 卡片内的收藏按钮（歌单/歌手卡片左下角） */
.card-actions, .singer-actions { position: absolute; bottom: 10px; right: 10px; z-index: 5;
  display: flex; gap: 6px;
}
.action-btn-sm {
  border: none; cursor: pointer; font-size: 15px;
  padding: 2px; outline: none;
  background: transparent; color: #94a3b8;
  transition: all 0.3s ease;
}
.action-btn-sm:hover { color: #facc15; transform: scale(1.25); background: transparent; }
.card .action-btn-sm.active { color: #facc15; background: transparent; }

/* 卡片内收藏 - 歌手 */
.singer-card .action-btn-sm {
  border: none; cursor: pointer; font-size: 15px;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.3s ease;
  background: transparent;
  color: #94a3b8;
  padding: 2px;
  outline: none;
}
.singer-card .action-btn-sm:hover { color: #facc15; transform: scale(1.25); background: transparent; }
.singer-card .action-btn-sm.active { color: #facc15; background: transparent; }

/* 歌手区域 */
.singer-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 20px; }
.singer-card {
  text-align: center; cursor: pointer; transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  background: $bg-card; backdrop-filter: blur(12px);
  border-radius: $radius-lg; padding: 20px 12px 16px;
  box-shadow: $shadow-card; border: 1px solid rgba(196, 164, 252, 0.1);
  position: relative;
}
.singer-card:hover {
  transform: translateY(-8px); box-shadow: $shadow-hover;
  border-color: rgba(196, 164, 252, 0.3);
}
.singer-card:hover .singer-avatar { transform: scale(1.1); }
.singer-avatar-wrap {
  width: 100px; height: 100px; border-radius: 50%; overflow: hidden;
  margin: 0 auto 12px;
  box-shadow: 0 6px 20px rgba(196, 164, 252, 0.25);
  border: 3px solid rgba(255,255,255,0.8);
}
.singer-avatar { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; border-radius: 50%; }
.singer-name { font-size: 14px; font-weight: 700; color: #334155; }
.singer-region { font-size: 12px; color: #a78bfa; margin-top: 4px; font-weight: 500; }

/* 歌曲列表 */
.song-list {
  background: $bg-card; backdrop-filter: blur(12px);
  border-radius: $radius-lg; overflow: hidden;
  box-shadow: $shadow-card; border: 1px solid rgba(196, 164, 252, 0.1);
}
.song-item {
  display: flex; align-items: center; padding: 12px 20px; cursor: pointer;
  transition: all 0.25s ease; border-bottom: 1px solid rgba(196, 164, 252, 0.08);
}
.song-item:last-child { border-bottom: none; }
.song-item:hover { background: rgba(196, 164, 252, 0.06); }
.song-index { width: 30px; font-size: 14px; font-weight: 700; color: #c4b5fd; text-align: center; }
.song-index.top3 { color: #a78bfa; font-size: 16px; }
.song-cover { width: 44px; height: 44px; border-radius: 10px; object-fit: cover; margin: 0 12px; }
.song-info { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.song-name { font-size: 14px; font-weight: 600; color: #334155; }
.song-artist { font-size: 12px; color: #94a3b8; }
.song-stats { display: flex; gap: 14px; margin-right: 12px; }
.song-stats span { font-size: 12px; color: #a78bfa; font-weight: 500; }

.page-footer { text-align: center; padding: 36px 20px; font-size: 13px; color: #a78bfa; font-weight: 500; }

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

