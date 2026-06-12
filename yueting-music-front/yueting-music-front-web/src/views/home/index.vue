<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { getHomeData } from '@/api/recommend'

const banners = ref([])
const todayRecommends = ref([])
const playlists = ref([])
const singers = ref([])
const songs = ref([])
const loading = ref(true)

// 播放器状态
const currentSong = ref(null)
const isPlaying = ref(false)
const audioRef = ref(null)
const currentTime = ref(0)
const duration = ref(0)
const progress = ref(0)

const resourceTypeLabels = { singer: '歌手', song: '歌曲', playlist: '歌单' }
const resourceTypeIcons = { singer: '🎤', song: '🎵', playlist: '📋' }

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
  } catch (err) {
    console.error('获取首页数据失败:', err)
  } finally {
    loading.value = false
  }
}

function normalizeMediaUrl(url) {
  if (!url) return ''
  if (/^(https?:)?\/\//i.test(url) || url.startsWith('data:')) return url
  if (url.startsWith('/api/')) return url
  if (url.startsWith('/upload/')) return '/api' + url
  if (url.startsWith('upload/')) return '/api/' + url
  return url
}

function getCover(item) {
  return normalizeMediaUrl(item.cover || item.resourceCover || item.avatar || '')
}

function getAudioUrl(item) {
  return normalizeMediaUrl(item.url || '')
}

function handleImageError(e, type) {
  if (type === 'banner') e.target.src = 'https://via.placeholder.com/1200x400/3b82f6/ffffff?text=YueTing'
  else if (type === 'avatar') e.target.src = 'https://via.placeholder.com/150/3b82f6/ffffff?text=Singer'
  else e.target.src = 'https://via.placeholder.com/300/3b82f6/ffffff?text=Music'
}

function formatCount(n) {
  if (!n && n !== 0) return ''
  if (n > 9999) return (n / 10000).toFixed(1) + '万'
  return n
}

function playSong(item) {
  if (currentSong.value && currentSong.value.id === item.id) {
    isPlaying.value = !isPlaying.value
  } else {
    currentSong.value = item
    isPlaying.value = true
  }
}

function formatTime(seconds) {
  if (!seconds || Number.isNaN(seconds)) return '0:00'
  const m = Math.floor(seconds / 60)
  const s = Math.floor(seconds % 60)
  return `${m}:${String(s).padStart(2, '0')}`
}

function seekTo(event) {
  if (!audioRef.value || !duration.value) return
  const nextProgress = Number(event.target.value) || 0
  progress.value = nextProgress
  audioRef.value.currentTime = (nextProgress / 100) * duration.value
  currentTime.value = audioRef.value.currentTime || 0
}

function onTimeUpdate() {
  if (!audioRef.value) return
  currentTime.value = audioRef.value.currentTime || 0
  duration.value = audioRef.value.duration || duration.value || 0
  if (duration.value > 0) {
    progress.value = (currentTime.value / duration.value) * 100
  }
}

function onLoadedMetadata() {
  if (!audioRef.value) return
  duration.value = audioRef.value.duration || 0
}

function onEnded() {
  isPlaying.value = false
  progress.value = 0
  currentTime.value = 0
}

function togglePlay() {
  if (!audioRef.value || !currentSong.value) return
  isPlaying.value = !isPlaying.value
}

watch([currentSong, isPlaying], async ([song, playing]) => {
  if (!song) return
  await nextTick()
  const audio = audioRef.value
  if (!audio) return
  const nextSrc = getAudioUrl(song)
  if (audio.getAttribute('src') !== nextSrc) {
    audio.src = nextSrc
    audio.load()
  }
  if (playing) {
    audio.play().catch(() => {
      isPlaying.value = false
    })
  } else {
    audio.pause()
  }
}, { immediate: true })

onMounted(() => { fetchData() })
</script>

<template>
  <div class="home-page">
    <!-- 顶部导航 -->
    <header class="top-header">
      <div class="header-inner">
        <a href="/" class="logo"><span class="logo-icon">🎵</span><span class="logo-text">悦听音乐</span></a>
        <nav class="main-nav">
          <a href="/" class="nav-item active">首页</a>
          <a href="#" class="nav-item">歌单</a>
          <a href="#" class="nav-item">歌手</a>
          <a href="#" class="nav-item">排行榜</a>
        </nav>
        <el-button class="login-btn" round size="small">登录</el-button>
      </div>
    </header>

    <div class="page-content" v-loading="loading" :style="{ paddingBottom: currentSong ? '80px' : '0' }">
      <!-- 轮播图 -->
      <section class="section" v-if="banners.length > 0">
        <el-carousel :interval="4000" height="380px" indicator-position="outside" class="banner-carousel">
          <el-carousel-item v-for="item in banners" :key="item.id">
            <div class="banner-card">
              <img :src="getCover(item)" :alt="item.title" class="banner-cover" @error="handleImageError($event, 'banner')" />
              <div class="banner-overlay">
                <span class="banner-tag">{{ resourceTypeIcons[item.resourceType] }} {{ resourceTypeLabels[item.resourceType] }}</span>
                <h2 class="banner-title">{{ item.title }}</h2>
                <p class="banner-subtitle" v-if="item.resourceName">{{ item.resourceName }}</p>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <!-- 今日推荐 -->
      <section class="section" v-if="todayRecommends.length > 0">
        <div class="section-header"><h2>✨ 今日推荐</h2></div>
        <div class="grid-4col">
          <div v-for="item in todayRecommends" :key="item.id" class="card">
            <div class="card-img-wrap square">
              <img :src="getCover(item)" :alt="item.title" class="card-img" @error="handleImageError($event, 'card')" />
              <span class="card-tag">{{ resourceTypeIcons[item.resourceType] }} {{ resourceTypeLabels[item.resourceType] }}</span>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ item.title }}</h3>
              <p class="card-desc" v-if="item.resourceName">{{ item.resourceName }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 推荐歌单 -->
      <section class="section" v-if="playlists.length > 0">
        <div class="section-header"><h2>📋 推荐歌单</h2><a href="#" class="more">查看更多 &gt;</a></div>
        <div class="grid-5col">
          <div v-for="item in playlists" :key="item.id" class="card">
            <div class="card-img-wrap">
              <img :src="getCover(item)" :alt="item.name" class="card-img" @error="handleImageError($event, 'card')" />
              <span class="card-count" v-if="item.songCount">{{ item.songCount }} 首</span>
            </div>
            <div class="card-body">
              <h3 class="card-title">{{ item.name }}</h3>
              <p class="card-desc" v-if="item.description">{{ item.description }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 热门歌手 -->
      <section class="section" v-if="singers.length > 0">
        <div class="section-header"><h2>🎤 热门歌手</h2><a href="#" class="more">查看更多 &gt;</a></div>
        <div class="singer-grid">
          <div v-for="item in singers" :key="item.id" class="singer-card">
            <div class="singer-avatar-wrap"><img :src="getCover(item)" :alt="item.name" class="singer-avatar" @error="handleImageError($event, 'avatar')" /></div>
            <h3 class="singer-name">{{ item.name }}</h3>
            <p class="singer-region" v-if="item.region">{{ item.region }}</p>
          </div>
        </div>
      </section>

      <!-- 热门歌曲 -->
      <section class="section" v-if="songs.length > 0">
        <div class="section-header"><h2>🎵 热门歌曲</h2><a href="#" class="more">查看更多 &gt;</a></div>
        <div class="song-list">
          <div v-for="(item, index) in songs" :key="item.id" class="song-item" @click="playSong(item)">
            <span class="song-index" :class="{ top3: index < 3 }">{{ index + 1 }}</span>
            <img :src="getCover(item)" :alt="item.name" class="song-cover" @error="handleImageError($event, 'card')" />
            <div class="song-info">
              <span class="song-name">{{ item.name }}</span>
              <span class="song-artist" v-if="item.singerName">{{ item.singerName }}</span>
            </div>
            <div class="song-stats">
              <span v-if="item.playCount"><i class="el-icon-headset"></i> {{ formatCount(item.playCount) }}</span>
              <span v-if="item.likeCount"><i class="el-icon-thumb"></i> {{ formatCount(item.likeCount) }}</span>
              <span v-if="item.favCount"><i class="el-icon-star-on"></i> {{ formatCount(item.favCount) }}</span>
            </div>
            <button class="play-btn" @click.stop="playSong(item)">{{ currentSong?.id === item.id && isPlaying ? '⏸' : '▶' }}</button>
          </div>
        </div>
      </section>

      <el-empty v-if="!loading && banners.length === 0 && todayRecommends.length === 0 && playlists.length === 0 && singers.length === 0 && songs.length === 0" description="暂无内容" :image-size="200" />
    </div>

    <!-- 底部音乐播放条 -->
    <div class="player-bar" v-if="currentSong">
      <div class="player-inner">
        <img :src="getCover(currentSong)" class="player-cover" @error="handleImageError($event, 'card')" />
        <div class="player-info">
          <span class="player-title">{{ currentSong.name || currentSong.title }}</span>
          <span class="player-artist" v-if="currentSong.singerName">{{ currentSong.singerName }}</span>
        </div>
        <div class="player-controls">
          <button class="ctrl-btn" @click="togglePlay">{{ isPlaying ? '⏸' : '▶' }}</button>
        </div>
        <div class="player-progress">
          <input
            type="range"
            class="progress-slider"
            min="0"
            max="100"
            step="0.1"
            :value="progress"
            @input="seekTo"
          />
          <div class="progress-meta">
            <span>{{ formatTime(currentTime) }}</span>
            <span>{{ formatTime(duration) }}</span>
          </div>
        </div>
        <audio
          ref="audioRef"
          :src="getAudioUrl(currentSong)"
          preload="metadata"
          @timeupdate="onTimeUpdate"
          @loadedmetadata="onLoadedMetadata"
          @ended="onEnded"
          style="display: none"
        ></audio>
      </div>
    </div>

    <footer class="page-footer" :class="{ 'has-player': currentSong }">
      <p>❤ 悦听音乐 · 用音乐温暖时光 ❤</p>
    </footer>
  </div>
</template>

<style scoped lang="scss">
$primary: #3b82f6;
$primary-light: #60a5fa;
$primary-lighter: #93bbfd;
$primary-dark: #2563eb;
$shadow: rgba(59, 130, 246, 0.12);
$text: #1e293b;
$text-light: #94a3b8;

.home-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #eff6ff 0%, #f0f4ff 30%, #dbeafe 100%);
}

// 顶部导航 — 二次元风格毛玻璃
.top-header {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(59, 130, 246, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 0 24px;
  height: 60px;
}

.logo { display: flex; align-items: center; gap: 6px; margin-right: 36px; }
.logo-icon { font-size: 24px; }
.logo-text { font-size: 18px; font-weight: 700; background: linear-gradient(135deg, $primary, #8b5cf6); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }

.main-nav { display: flex; gap: 4px; flex: 1; }
.nav-item { padding: 6px 14px; border-radius: 8px; color: #555; font-size: 14px; transition: all 0.2s; }
.nav-item:hover { color: $primary; background: rgba($primary, 0.06); }
.nav-item.active { color: $primary; font-weight: 600; background: rgba($primary, 0.1); }

.login-btn { border-radius: 20px; background: $primary; color: #fff; border: none; }
.login-btn:hover { background: $primary-dark; }

// Section
.section { max-width: 1200px; margin: 0 auto; padding: 0 24px; margin-bottom: 44px; }

.section-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 18px; }
.section-header h2 { font-size: 1.3rem; font-weight: 700; color: $text; }
.section-header .more { font-size: 13px; color: $primary; transition: all 0.2s; }
.section-header .more:hover { color: $primary-dark; }

// 轮播图 — 圆角卡片
.banner-carousel { border-radius: 16px; overflow: hidden; box-shadow: 0 8px 30px $shadow; }
.banner-carousel :deep(.el-carousel__button) { background: rgba(255,255,255,0.5); width: 28px; height: 4px; border-radius: 2px; transition: all 0.3s; }
.banner-carousel :deep(.is-active .el-carousel__button) { background: #fff; width: 44px; }

.banner-card { height: 100%; cursor: pointer; position: relative; overflow: hidden; }
.banner-card:hover .banner-cover { transform: scale(1.06); }

.banner-cover { width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease; }

.banner-overlay {
  position: absolute; bottom: 0; left: 0; right: 0;
  padding: 50px 32px 28px;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  color: #fff;
}
.banner-tag { display: inline-block; padding: 3px 14px; border-radius: 14px; font-size: 12px; margin-bottom: 8px; backdrop-filter: blur(4px); background: rgba($primary, 0.5); }
.banner-title { font-size: 1.5rem; font-weight: 700; margin-bottom: 4px; }
.banner-subtitle { font-size: 0.95rem; opacity: 0.85; }

// 卡片网格
.grid-4col { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.grid-5col { display: grid; grid-template-columns: repeat(5, 1fr); gap: 20px; }

.card {
  background: #fff; border-radius: 14px; overflow: hidden; cursor: pointer;
  transition: all 0.3s ease; box-shadow: 0 2px 12px $shadow;
}
.card:hover { transform: translateY(-6px); box-shadow: 0 8px 30px $shadow; }
.card:hover .card-img { transform: scale(1.08); }

.card-img-wrap { position: relative; width: 100%; padding-top: 100%; overflow: hidden; background: #f0f4ff; }
.card-img-wrap.square { padding-top: 100%; }

.card-img { position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s ease; }

.card-tag { position: absolute; top: 10px; left: 10px; padding: 3px 10px; border-radius: 10px; font-size: 11px; color: #fff; backdrop-filter: blur(4px); background: rgba($primary, 0.6); }
.card-count { position: absolute; bottom: 8px; right: 10px; font-size: 11px; color: #fff; background: rgba(0,0,0,0.5); padding: 2px 10px; border-radius: 10px; backdrop-filter: blur(2px); }

.card-body { padding: 10px 14px 14px; }
.card-title { font-size: 14px; font-weight: 600; color: $text; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-desc { font-size: 12px; color: $text-light; margin-top: 4px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

// 歌手
.singer-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 20px; }

.singer-card { text-align: center; cursor: pointer; transition: all 0.3s ease; }
.singer-card:hover { transform: translateY(-6px); }
.singer-card:hover .singer-avatar { transform: scale(1.1); }

.singer-avatar-wrap { width: 110px; height: 110px; border-radius: 50%; overflow: hidden; margin: 0 auto 10px; box-shadow: 0 4px 16px $shadow; border: 3px solid #fff; }
.singer-avatar { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s ease; border-radius: 50%; }
.singer-name { font-size: 14px; font-weight: 600; color: $text; }
.singer-region { font-size: 12px; color: $text-light; margin-top: 2px; }

// 歌曲列表
.song-list { background: #fff; border-radius: 14px; overflow: hidden; box-shadow: 0 2px 12px $shadow; }
.song-item { display: flex; align-items: center; padding: 10px 20px; cursor: pointer; transition: all 0.2s; border-bottom: 1px solid #f1f5f9; }
.song-item:last-child { border-bottom: none; }
.song-item:hover { background: rgba($primary, 0.04); }

.song-index { width: 30px; font-size: 14px; font-weight: 600; color: $text-light; text-align: center; }
.song-index.top3 { color: $primary; font-size: 16px; }

.song-cover { width: 42px; height: 42px; border-radius: 8px; object-fit: cover; margin: 0 12px; }

.song-info { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.song-name { font-size: 14px; font-weight: 500; color: $text; }
.song-artist { font-size: 12px; color: $text-light; }

.song-stats { display: flex; gap: 14px; margin-right: 12px; }
.song-stats span { font-size: 12px; color: $text-light; }

.play-btn { width: 32px; height: 32px; border-radius: 50%; border: none; background: rgba($primary, 0.1); color: $primary; font-size: 14px; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; justify-content: center; }
.play-btn:hover { background: $primary; color: #fff; }

// 底部播放条
.player-bar {
  position: fixed; bottom: 0; left: 0; right: 0; z-index: 999;
  background: rgba(255,255,255,0.92);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-top: 1px solid rgba(59,130,246,0.1);
  box-shadow: 0 -4px 20px rgba(59,130,246,0.1);
}

.player-inner {
  max-width: 1200px; margin: 0 auto;
  display: flex; align-items: center;
  padding: 10px 24px; gap: 16px;
}

.player-cover { width: 44px; height: 44px; border-radius: 10px; object-fit: cover; }

.player-info { display: flex; flex-direction: column; gap: 2px; min-width: 120px; }
.player-title { font-size: 14px; font-weight: 600; color: $text; }
.player-artist { font-size: 12px; color: $text-light; }

.player-controls { display: flex; align-items: center; gap: 8px; }
.ctrl-btn { width: 36px; height: 36px; border-radius: 50%; border: none; background: $primary; color: #fff; font-size: 16px; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; justify-content: center; }
.ctrl-btn:hover { background: $primary-dark; transform: scale(1.08); }

.player-progress { flex: 1; display: flex; flex-direction: column; gap: 4px; }
.progress-slider { width: 100%; accent-color: $primary; cursor: pointer; }
.progress-slider::-webkit-slider-runnable-track { height: 4px; border-radius: 2px; background: #e2e8f0; }
.progress-slider::-webkit-slider-thumb { -webkit-appearance: none; appearance: none; width: 12px; height: 12px; border-radius: 50%; background: #fff; border: 2px solid $primary; margin-top: -4px; box-shadow: 0 2px 6px rgba(59, 130, 246, 0.25); }
.progress-slider::-moz-range-track { height: 4px; border-radius: 2px; background: #e2e8f0; }
.progress-slider::-moz-range-thumb { width: 12px; height: 12px; border-radius: 50%; background: #fff; border: 2px solid $primary; box-shadow: 0 2px 6px rgba(59, 130, 246, 0.25); }
.progress-meta { display: flex; justify-content: space-between; color: $text-light; font-size: 11px; }

// 页脚
.page-footer { text-align: center; padding: 32px 20px; color: $text-light; font-size: 13px; letter-spacing: 1px; }
.page-footer.has-player { padding-bottom: 80px; }

// 响应式
@media (max-width: 992px) {
  .grid-5col { grid-template-columns: repeat(3, 1fr); }
  .singer-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .grid-4col, .grid-5col { grid-template-columns: repeat(2, 1fr); }
  .singer-grid { grid-template-columns: repeat(2, 1fr); }
  .main-nav { display: none; }
  .banner-overlay .banner-title { font-size: 1.2rem; }
}
</style>