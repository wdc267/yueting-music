<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import { getHomeData } from '@/api/recommend'

const route = useRoute()
const router = useRouter()
const items = ref([])
const loading = ref(true)
const currentPlayingId = ref(null)
const isPlaying = ref(false)

const category = route.params.category || 'playlists'

const configMap = {
  playlists: { title: '歌单', icon: '📋', key: 'playlists', gridClass: 'grid-5col' },
  singers: { title: '歌手', icon: '🎤', key: 'singers', gridClass: 'singer-grid' },
  songs: { title: '歌曲', icon: '🎵', key: 'songs', gridClass: 'song-list' }
}

const config = configMap[category] || configMap.playlists

function getCover(item) {
  const url = item.cover || item.resourceCover || item.avatar || ''
  if (url && (url.startsWith('upload/') || url.startsWith('/upload/'))) {
    return '/api' + (url.startsWith('/') ? url : '/' + url)
  }
  return url || ''
}

function handleImageError(e) {
  e.target.style.display = 'none'
}

function goDetail(item, type) {
  router.push('/detail/' + type + '/' + (item.id || item.resourceId))
}

onMounted(async () => {
  window.addEventListener('play-song', (e) => {
    currentPlayingId.value = e.detail.id || e.detail.resourceId
  })
  window.addEventListener('play-state', (e) => {
    isPlaying.value = e.detail.playing
  })
  loading.value = true
  try {
    const res = await getHomeData()
    items.value = res.data?.[config.key] || []
  } catch (err) {
    console.error('获取列表失败:', err)
  } finally {
    loading.value = false
  }
})
function playSong(item) {
  const song = {
    id: item.id,
    name: item.name,
    cover: item.cover,
    url: item.url,
    singerId: item.singerId,
    singerName: item.singerName,
    duration: item.duration
  }
  currentPlayingId.value = item.id
  window.dispatchEvent(new CustomEvent('play-song', { detail: song }))
}
</script>

<template>
  <div class="list-page">
    <header class="list-header">
      <router-link to="/" class="back-link">← 返回首页</router-link>
      <h2>{{ config.icon }} {{ config.title }}列表</h2>
      <span></span>
    </header>

    <div class="list-content" v-loading="loading">
      <!-- 歌单网格 -->
      <div v-if="category === 'playlists'" class="grid-5col">
        <div v-for="item in items" :key="item.id" class="card" @click="goDetail(item, 'playlist')">
          <div class="card-img-wrap">
            <img :src="getCover(item)" :alt="item.name" class="card-img" @error="handleImageError" />
            <span class="card-count" v-if="item.songCount">{{ item.songCount }} 首</span>
          </div>
          <div class="card-body">
            <h3 class="card-title">{{ item.name }}</h3>
            <p class="card-desc" v-if="item.description">{{ item.description }}</p>
          </div>
        </div>
      </div>

      <!-- 歌手网格 -->
      <div v-else-if="category === 'singers'" class="singer-grid">
        <div v-for="item in items" :key="item.id" class="singer-card" @click="goDetail(item, 'singer')">
          <div class="singer-avatar-wrap">
            <img :src="getCover(item)" :alt="item.name" class="singer-avatar" @error="handleImageError" />
          </div>
          <h3 class="singer-name">{{ item.name }}</h3>
          <p class="singer-region" v-if="item.region">{{ item.region }}</p>
        </div>
      </div>

      <!-- 歌曲列表 -->
      <div v-else-if="category === 'songs'" class="song-list">
        <div v-for="(item, index) in items" :key="item.id" class="song-item">
          <span class="song-index" :class="{ top3: index < 3 }">{{ index + 1 }}</span>
          <img :src="getCover(item)" :alt="item.name" class="song-cover" @error="handleImageError" />
          <div class="song-info">
            <span class="song-name">{{ item.name }}</span>
            <span class="song-artist" v-if="item.singerName">{{ item.singerName }}</span>
          </div>
          <span class="song-duration" v-if="item.duration">
            {{ Math.floor(item.duration / 60) }}:{{ String(item.duration % 60).padStart(2, '0') }}
          </span>
          <button class="song-play-btn" :class="{ playing: currentPlayingId === item.id && isPlaying }" @click.stop="playSong(item)" :title="currentPlayingId === item.id && isPlaying ? '暂停' : '播放'">
          <svg v-if="currentPlayingId === item.id && isPlaying" width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
            <rect x="6" y="4" width="4" height="16" rx="1"/>
            <rect x="14" y="4" width="4" height="16" rx="1"/>
          </svg>
          <svg v-else width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
            <polygon points="7,3 21,12 7,21"/>
          </svg>
        </button>
        </div>
      </div>

      <el-empty v-else-if="!loading" description="暂无内容" :image-size="120" />
    </div>
  </div>
</template>

<style scoped lang="scss">
$primary: #3b82f6;
$text: #1e293b;
$text-light: #94a3b8;
$shadow: rgba(59, 130, 246, 0.12);

.list-page { min-height: 100vh; background: linear-gradient(180deg, #eff6ff 0%, #f0f4ff 30%, #dbeafe 100%); padding-bottom: 80px; }

.list-header {
  background: rgba(255,255,255,0.85); backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(59,130,246,0.08);
  position: sticky; top: 0; z-index: 100;
  display: flex; align-items: center; justify-content: space-between;
  max-width: 1200px; margin: 0 auto; padding: 0 24px; height: 56px;
}

.back-link { color: $primary; font-size: 14px; font-weight: 500; }
.back-link:hover { text-decoration: underline; }
.list-header h2 { font-size: 16px; font-weight: 600; color: $text; }

.list-content { max-width: 1200px; margin: 0 auto; padding: 24px 24px 40px; }

.grid-5col { display: grid; grid-template-columns: repeat(5, 1fr); gap: 20px; }

.card { background: #fff; border-radius: 14px; overflow: hidden; transition: all 0.3s ease; box-shadow: 0 2px 12px $shadow; }
.card:hover { transform: translateY(-6px); box-shadow: 0 8px 30px $shadow; }
.card-img-wrap { position: relative; width: 100%; padding-top: 100%; overflow: hidden; background: #f0f4ff; }
.card-img { position: absolute; top: 0; left: 0; width: 100%; height: 100%; object-fit: cover; }
.card-count { position: absolute; bottom: 8px; right: 10px; font-size: 11px; color: #fff; background: rgba(0,0,0,0.5); padding: 2px 10px; border-radius: 10px; }
.card-body { padding: 10px 14px 14px; }
.card-title { font-size: 14px; font-weight: 600; color: $text; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.card-desc { font-size: 12px; color: $text-light; margin-top: 4px; }

.singer-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 20px; }
.singer-card { text-align: center; cursor: pointer; transition: all 0.3s ease; }
.singer-card:hover { transform: translateY(-6px); }
.singer-avatar-wrap { width: 140px; height: 140px; border-radius: 50%; overflow: hidden; margin: 0 auto 10px; box-shadow: 0 4px 16px ; }
.singer-avatar { width: 100%; height: 100%; object-fit: cover; }
.singer-name { font-size: 14px; font-weight: 600; color: $text; }
.singer-region { font-size: 12px; color: $text-light; margin-top: 2px; }

.song-list { background: #fff; border-radius: 14px; overflow: hidden; box-shadow: 0 2px 12px $shadow; }
.song-item { display: flex; align-items: center; padding: 12px 20px; border-bottom: 1px solid #f1f5f9; }
.song-item:last-child { border-bottom: none; }
.song-index { width: 30px; font-size: 14px; font-weight: 600; color: $text-light; text-align: center; }
.song-index.top3 { color: $primary; font-size: 16px; }
.song-cover { width: 44px; height: 44px; border-radius: 8px; object-fit: cover; margin: 0 14px; }
.song-info { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.song-name { font-size: 14px; font-weight: 500; color: $text; }
.song-artist { font-size: 12px; color: $text-light; }
.song-duration { font-size: 12px; color: $text-light; }

.song-play-btn { width: 30px; height: 30px; border-radius: 50%; border: none; background: linear-gradient(135deg, $primary, #6366f1); color: #fff; font-size: 13px; cursor: pointer; display: flex; align-items: center; justify-content: center; flex-shrink: 0; margin-left: 10px; transition: all 0.2s; }
.song-play-btn:hover { transform: scale(1.1); box-shadow: 0 3px 10px rgba(59, 130, 246, 0.3); }
.song-play-btn svg { fill: currentColor; stroke: none; }

@media (max-width: 992px) {
  .grid-5col { grid-template-columns: repeat(3, 1fr); }
  .singer-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .grid-5col { grid-template-columns: repeat(2, 1fr); }
  .singer-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>

