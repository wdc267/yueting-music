<script setup>
import { ref } from 'vue'

const props = defineProps({
  visible: Boolean,
  song: Object
})

const emit = defineEmits(['update:visible'])

const audioRef = ref(null)
const currentTime = ref(0)
const duration = ref(0)
const isPlaying = ref(false)
const progress = ref(0)

function formatTime(seconds) {
  if (!seconds || isNaN(seconds)) return '0:00'
  const m = Math.floor(seconds / 60)
  const s = Math.floor(seconds % 60)
  return m + ':' + String(s).padStart(2, '0')
}

function handleClose() {
  if (audioRef.value) {
    audioRef.value.pause()
    audioRef.value.currentTime = 0
  }
  isPlaying.value = false
  currentTime.value = 0
  progress.value = 0
  emit('update:visible', false)
}

function togglePlay() {
  if (!audioRef.value) return
  if (isPlaying.value) {
    audioRef.value.pause()
  } else {
    audioRef.value.play().catch(() => {})
  }
  isPlaying.value = !isPlaying.value
}

function onTimeUpdate() {
  if (!audioRef.value) return
  currentTime.value = audioRef.value.currentTime
  if (audioRef.value.duration) {
    progress.value = (audioRef.value.currentTime / audioRef.value.duration) * 100
  }
}

function onLoadedMetadata() {
  if (audioRef.value) {
    duration.value = audioRef.value.duration
  }
}

function onEnded() {
  isPlaying.value = false
  progress.value = 0
  currentTime.value = 0
}

function seek(event) {
  if (!audioRef.value || !duration.value) return
  const rect = event.currentTarget.getBoundingClientRect()
  const x = (event.clientX - rect.left) / rect.width
  audioRef.value.currentTime = x * duration.value
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    @close="handleClose"
    width="420px"
    :close-on-click-modal="false"
    title="音乐播放"
    top="15vh"
  >
    <div v-if="song" class="player">
      <div class="player-cover">
        <el-avatar :src="song.cover" :size="100" shape="square" v-if="song.cover" />
        <el-avatar :size="100" shape="square" v-else icon="Headset" style="background: #f0f2f5; color: #999" />
      </div>
      <div class="player-info">
        <div class="player-title">{{ song.name }}</div>
        <div class="player-singer">{{ song.singerName || '未知歌手' }}</div>
      </div>

      <audio
        ref="audioRef"
        :src="song.url"
        preload="metadata"
        @timeupdate="onTimeUpdate"
        @loadedmetadata="onLoadedMetadata"
        @ended="onEnded"
        style="display:none"
      ></audio>

      <div class="player-progress" @click="seek">
        <div class="player-progress-bar">
          <div class="player-progress-current" :style="{ width: progress + '%' }"></div>
        </div>
      </div>
      <div class="player-time">
        <span>{{ formatTime(currentTime) }}</span>
        <span>{{ formatTime(duration) }}</span>
      </div>

      <div class="player-controls">
        <el-button :icon="isPlaying ? 'VideoPause' : 'VideoPlay'" circle size="large" type="primary" @click="togglePlay" />
      </div>
    </div>
    <div v-else style="text-align: center; padding: 40px; color: #999">未选择歌曲</div>
  </el-dialog>
</template>

<style scoped>
.player {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
  padding: 8px 0;
}
.player-cover {
  margin-top: 4px;
}
.player-info {
  text-align: center;
}
.player-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}
.player-singer {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}
.player-progress {
  width: 100%;
  padding: 0 8px;
  cursor: pointer;
}
.player-progress-bar {
  width: 100%;
  height: 6px;
  background: #e0e0e0;
  border-radius: 3px;
  position: relative;
}
.player-progress-current {
  height: 100%;
  background: #409eff;
  border-radius: 3px;
  transition: width 0.1s linear;
}
.player-time {
  width: 100%;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}
.player-controls {
  display: flex;
  justify-content: center;
}
</style>