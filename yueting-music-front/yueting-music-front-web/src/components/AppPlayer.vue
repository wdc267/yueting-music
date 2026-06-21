<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { recordPlay } from '@/api/user'

const currentSong = ref(null)
const isPlaying = ref(false)
const userId = ref(null)
const currentTime = ref(0)
const duration = ref(0)
const progress = ref(0)
const lastPlayedSongId = ref(null)
let audioInstance = null

function getCoverUrl(item) {
  const url = item.cover || item.resourceCover || item.avatar || ''
  if (url && (url.startsWith('upload/') || url.startsWith('/upload/'))) {
    return '/api' + (url.startsWith('/') ? url : '/' + url)
  }
  if (url && url.startsWith('/api/')) {
    return url
  }
  return url || ''
}

function normalizeUrl(url) {
  if (!url) return ''
  if (url.startsWith('upload/') || url.startsWith('/upload/')) {
    return '/api' + (url.startsWith('/') ? url : '/' + url)
  }
  if (url.startsWith('/api/')) {
    return url
  }
  return url
}

function getUserId() {
  if (userId.value) return userId.value
  try {
    const info = JSON.parse(localStorage.getItem('user_info') || '{}')
    userId.value = info.id || null
    return userId.value
  } catch { return null }
}

function onPlayStarted(songId) {
  if (songId && songId !== lastPlayedSongId.value) {
    lastPlayedSongId.value = songId
    recordPlay(songId, getUserId()).catch(() => {})
  }
}

function playSong(item) {
  const isFullSong = !!(item.url || item.singerId !== undefined)
  const song = isFullSong ? item : {
    id: item.resourceId || item.id,
    name: item.resourceName || item.title || item.name,
    cover: item.resourceCover || item.cover,
    url: item.resourceUrl || item.url,
    singerId: item.singerId,
    singerName: item.singerName || item.resourceSinger,
    duration: item.duration
  }

  if (currentSong.value && currentSong.value.id === song.id) {
    if (isPlaying.value) {
      audioInstance?.pause()
      isPlaying.value = false
      window.dispatchEvent(new CustomEvent('play-state', { detail: { playing: false, songId: currentSong.value?.id } }))
    } else {
      audioInstance?.play().then(() => {
        onPlayStarted(song.id)
      }).catch(() => {})
      isPlaying.value = true
      window.dispatchEvent(new CustomEvent('play-state', { detail: { playing: true, songId: currentSong.value?.id } }))
    }
    return
  }

  if (audioInstance) {
    audioInstance.pause()
    audioInstance = null
  }

  currentSong.value = song
  isPlaying.value = true
  window.dispatchEvent(new CustomEvent('play-state', { detail: { playing: true, songId: song.id } }))

  const songUrl = song.url || ''
  if (songUrl) {
    const audioUrl = normalizeUrl(songUrl)
    audioInstance = new Audio(audioUrl)
    audioInstance.ontimeupdate = () => {
      if (audioInstance && audioInstance.duration) {
        currentTime.value = audioInstance.currentTime
        duration.value = audioInstance.duration
        progress.value = (audioInstance.currentTime / audioInstance.duration) * 100
      }
    }
    audioInstance.onloadedmetadata = () => { duration.value = audioInstance.duration }
    audioInstance.onended = () => { isPlaying.value = false; progress.value = 0; currentTime.value = 0; window.dispatchEvent(new CustomEvent('play-state', { detail: { playing: false, songId: currentSong.value?.id } })) }
    audioInstance.play().then(() => {
      onPlayStarted(song.id)
    }).catch((err) => {
      console.error('播放失败:', err)
      isPlaying.value = false
    })
  } else {
    isPlaying.value = false
  }
}

function togglePlay() {
  if (!audioInstance) {
    if (currentSong.value) {
      playSong(currentSong.value)
    }
    return
  }
  if (isPlaying.value) {
    audioInstance.pause()
    isPlaying.value = false
    window.dispatchEvent(new CustomEvent('play-state', { detail: { playing: false, songId: currentSong.value?.id } }))
  } else {
    audioInstance.play().then(() => {
      onPlayStarted(currentSong.value?.id)
    }).catch(() => {})
    isPlaying.value = true
    window.dispatchEvent(new CustomEvent('play-state', { detail: { playing: true, songId: currentSong.value?.id } }))
  }
}

function seek(event) {
  if (!audioInstance || !duration.value) return
  const rect = event.currentTarget.getBoundingClientRect()
  const x = event.clientX - rect.left
  const pct = Math.max(0, Math.min(1, x / rect.width))
  audioInstance.currentTime = pct * duration.value
  progress.value = pct * 100
}

function formatTime(seconds) {
  if (!seconds || isNaN(seconds)) return '00:00'
  const m = Math.floor(seconds / 60)
  const s = Math.floor(seconds % 60)
  return String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0')
}

function handlePlaySongEvent(event) {
  playSong(event.detail)
}

onMounted(() => {
  window.addEventListener('play-song', handlePlaySongEvent)
  window.addEventListener('toggle-play', togglePlay)
})

onUnmounted(() => {
  window.removeEventListener('play-song', handlePlaySongEvent)
  window.removeEventListener('toggle-play', togglePlay)
  if (audioInstance) {
    audioInstance.pause()
    audioInstance = null
  }
})
</script>

<template>
  <div class="player-bar" v-if="currentSong">
    <div class="player-inner">
      <div class="player-cover-area">
        <img :src="getCoverUrl(currentSong)" class="player-cover" @error="(e) => { e.target.style.display='none' }" />
        <div class="player-song-info">
          <span class="player-song-name">{{ currentSong.name }}</span>
          <span class="player-artist">{{ currentSong.singerName || '' }}</span>
        </div>
      </div>

      <div class="player-controls">
        <button class="player-btn play-btn" @click="togglePlay">
          <svg v-if="isPlaying" class="player-icon" viewBox="0 0 24 24" width="18" height="18">
            <rect x="6" y="4" width="4" height="16" rx="1"/>
            <rect x="14" y="4" width="4" height="16" rx="1"/>
          </svg>
          <svg v-else class="player-icon" viewBox="0 0 24 24" width="18" height="18">
            <polygon points="7,3 21,12 7,21"/>
          </svg>
        </button>
        <div class="player-progress" @click="seek">
          <div class="progress-track">
            <div class="progress-fill" :style="{ width: progress + '%' }"></div>
            <div class="progress-thumb" :style="{ left: progress + '%' }"></div>
          </div>
          <div class="progress-time">
            <span>{{ formatTime(currentTime) }}</span>
            <span>{{ formatTime(duration) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
$primary: #3b82f6;
$primary-light: #60a5fa;
$text: #1e293b;
$text-light: #94a3b8;

.player-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(16px);
  border-top: 1px solid rgba(59, 130, 246, 0.1);
  box-shadow: 0 -4px 24px rgba(59, 130, 246, 0.08);
}

.player-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 10px 24px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.player-cover-area {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 180px;
  flex-shrink: 0;
}

.player-cover {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  object-fit: cover;
}

.player-song-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.player-song-name {
  font-size: 14px;
  font-weight: 600;
  color: $text;
}

.player-artist {
  font-size: 12px;
  color: $text-light;
}

.player-controls {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16px;
}

.player-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, $primary, #6366f1);
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  flex-shrink: 0;

  .player-icon {
    fill: currentColor;
    stroke: none;
  }

  &:hover {
    transform: scale(1.08);
    box-shadow: 0 4px 12px rgba($primary, 0.35);
  }
}

.player-progress {
  flex: 1;
  cursor: pointer;
}

.progress-track {
  position: relative;
  height: 4px;
  background: #e2e8f0;
  border-radius: 2px;
  overflow: visible;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, $primary, $primary-light);
  border-radius: 2px;
  transition: width 0.1s linear;
}

.progress-thumb {
  position: absolute;
  top: 50%;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid $primary;
  transform: translate(-50%, -50%);
  transition: left 0.1s linear;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.15);
}

.progress-time {
  display: flex;
  justify-content: space-between;
  margin-top: 4px;
  font-size: 11px;
  color: $text-light;
}
</style>
