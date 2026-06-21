<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSingerDetail, getPlaylistDetail } from '@/api/recommend'

const route = useRoute()
const router = useRouter()

const type = route.params.type
const id = route.params.id
const detail = ref(null)
const songs = ref([])
const loading = ref(true)
const currentPlayingSongId = ref(null)
const currentPlayingState = ref(false)

function onPlayStateChange(event) {
  const { playing, songId } = event.detail
  currentPlayingState.value = playing
  if (songId) currentPlayingSongId.value = songId
}

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

function playSong(item) {
  const song = {
    id: item.id,
    name: item.name,
    cover: item.cover || item.avatar,
    url: item.url,
    singerId: item.singerId,
    singerName: item.singerName,
    duration: item.duration
  }
  window.dispatchEvent(new CustomEvent('play-song', { detail: song }))
}

function isCurrentSong(songId) {
  return currentPlayingSongId.value === songId && currentPlayingState.value
}

onMounted(async () => {
  loading.value = true
  try {
    if (type === 'singer') {
      const res = await getSingerDetail(id)
      detail.value = res.data.singer
      songs.value = res.data.songs || []
    } else if (type === 'playlist') {
      const res = await getPlaylistDetail(id)
      detail.value = res.data
      songs.value = res.data.songs || []
    }
  } catch (err) {
    console.error('获取详情失败:', err)
  } finally {
    loading.value = false
  }
  window.addEventListener('play-state', onPlayStateChange)
})

onUnmounted(() => {
  window.removeEventListener('play-state', onPlayStateChange)
})
</script>

<template>
  <div class="detail-page">
    <header class="detail-header">
      <button class="back-btn" @click="router.back()">← 返回</button>
      <h2>{{ type === 'singer' ? '🎤 歌手详情' : '📋 歌单详情' }}</h2>
      <span></span>
    </header>

    <div class="detail-content" v-loading="loading">
      <template v-if="type === 'singer' && detail">
        <div class="info-card">
          <img :src="getCover(detail)" class="info-avatar" @error="handleImageError" />
          <div class="info-meta">
            <h1 class="info-name">{{ detail.name }}</h1>
            <p class="info-tag" v-if="detail.region">{{ detail.region }}</p>
            <p class="info-desc" v-if="detail.remark">{{ detail.remark }}</p>
          </div>
        </div>
      </template>

      <template v-else-if="type === 'playlist' && detail">
        <div class="info-card">
          <img :src="getCover(detail)" class="info-cover" @error="handleImageError" />
          <div class="info-meta">
            <h1 class="info-name">{{ detail.name }}</h1>
            <p class="info-desc" v-if="detail.description">{{ detail.description }}</p>
            <p class="info-count">{{ songs.length }} 首歌曲</p>
          </div>
        </div>
      </template>

      <div class="song-section" v-if="songs.length > 0">
        <h3>歌曲列表 ({{ songs.length }})</h3>
        <div v-for="(item, idx) in songs" :key="item.id" class="song-row" @click="playSong(item)">
          <span class="row-idx">{{ idx + 1 }}</span>
          <img :src="getCover(item)" class="row-cover" @error="handleImageError" />
          <div class="row-info">
            <span class="row-name">{{ item.name }}</span>
            <span class="row-artist" v-if="item.singerName">{{ item.singerName }}</span>
          </div>
          <span class="row-duration" v-if="item.duration">{{ Math.floor(item.duration / 60) }}:{{ String(item.duration % 60).padStart(2, '0') }}</span>
          <button class="row-play" :class="{ playing: isCurrentSong(item.id) }" @click.stop="playSong(item)">
            <svg v-if="isCurrentSong(item.id)" viewBox="0 0 24 24" width="14" height="14">
              <rect x="6" y="4" width="4" height="16" rx="1"/>
              <rect x="14" y="4" width="4" height="16" rx="1"/>
            </svg>
            <svg v-else viewBox="0 0 24 24" width="14" height="14">
              <polygon points="7,3 21,12 7,21"/>
            </svg>
          </button>
        </div>
      </div>

      <el-empty v-else-if="!loading" description="暂无歌曲" :image-size="120" />
    </div>
  </div>
</template>

<style scoped lang="scss">
$primary: #3b82f6;
$text: #1e293b;
$text-light: #94a3b8;
$shadow: rgba(59, 130, 246, 0.12);

.detail-page { min-height: 100vh; background: linear-gradient(180deg, #eff6ff 0%, #f0f4ff 30%, #dbeafe 100%); padding-bottom: 80px; }

.detail-header {
  background: rgba(255,255,255,0.85); backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(59,130,246,0.08);
  position: sticky; top: 0; z-index: 100;
  display: flex; align-items: center; justify-content: space-between;
  max-width: 800px; margin: 0 auto; padding: 0 24px; height: 56px;
}

.back-btn { background: none; border: none; color: $primary; font-size: 14px; font-weight: 500; cursor: pointer; }
.back-btn:hover { text-decoration: underline; }
.detail-header h2 { font-size: 16px; font-weight: 600; color: $text; }

.detail-content { max-width: 800px; margin: 0 auto; padding: 24px 24px 40px; }

.info-card {
  background: #fff; border-radius: 16px; padding: 28px 24px;
  display: flex; gap: 24px; align-items: center;
  box-shadow: 0 2px 12px $shadow; margin-bottom: 24px;
}

.info-avatar { width: 120px; height: 120px; border-radius: 50%; object-fit: cover; box-shadow: 0 4px 16px $shadow; border: 3px solid #fff; }
.info-cover { width: 140px; height: 140px; border-radius: 14px; object-fit: cover; box-shadow: 0 4px 16px $shadow; }

.info-meta { flex: 1; }
.info-name { font-size: 22px; font-weight: 700; color: $text; margin-bottom: 4px; }
.info-tag { font-size: 13px; color: $text-light; margin-bottom: 4px; }
.info-desc { font-size: 13px; color: #64748b; }
.info-count { font-size: 13px; color: $text-light; margin-top: 4px; }

.song-section h3 { font-size: 15px; font-weight: 600; color: $text; margin-bottom: 12px; }

.song-row {
  display: flex; align-items: center; padding: 12px 16px;
  background: #fff; border-radius: 12px; cursor: pointer;
  transition: all 0.2s; margin-bottom: 6px; box-shadow: 0 1px 6px rgba(59,130,246,0.06);
}
.song-row:hover { background: rgba($primary, 0.04); transform: translateX(4px); }

.row-idx { width: 28px; font-size: 13px; color: $text-light; text-align: center; flex-shrink: 0; }
.row-cover { width: 40px; height: 40px; border-radius: 8px; object-fit: cover; margin: 0 12px; flex-shrink: 0; }
.row-info { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.row-name { font-size: 14px; font-weight: 500; color: $text; }
.row-artist { font-size: 12px; color: $text-light; }
.row-duration { font-size: 12px; color: $text-light; margin: 0 12px; white-space: nowrap; }

.row-play {
  width: 30px; height: 30px; border-radius: 50%; border: none;
  background: rgba($primary, 0.1); color: $primary; font-size: 12px;
  cursor: pointer; transition: all 0.2s; flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
}
.row-play:hover { background: $primary; color: #fff; }
.row-play.playing { background: $primary; color: #fff; }
.row-play svg { fill: currentColor; }
</style>
