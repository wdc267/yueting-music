<script setup>
import { ref, watch } from 'vue'
import { toggleFavorite, getFavoriteStatus } from '@/api/user'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: Boolean,
  type: String,    // 'singer' | 'playlist'
  data: Object,    // 详情数据
  songs: { type: Array, default: () => [] }
})
const emit = defineEmits(['update:visible', 'play'])
const isFavorited = ref(false)

watch(() => props.visible, (v) => {
  if (v && props.type === 'singer') checkFavorite()
  if (v && props.type === 'playlist') checkFavorite()
})

const userInfo = () => {
  try { return JSON.parse(localStorage.getItem('user_info')) }
  catch { return null }
}

async function checkFavorite() {
  const u = userInfo()
  if (!u) return
  const type = props.type === 'singer' ? 'singer' : 'playlist'
  const id = props.data?.id
  if (!id) return
  try {
    const res = await getFavoriteStatus(u.id, type, id)
    isFavorited.value = res.data
  } catch {}
}

async function handleToggleFavorite() {
  const u = userInfo()
  if (!u) { ElMessage.warning('请先登录'); return }
  const type = props.type === 'singer' ? 'singer' : 'playlist'
  try {
    const res = await toggleFavorite({ userId: u.id, resourceType: type, resourceId: props.data.id })
    isFavorited.value = res.data.favorited
    ElMessage.success(res.data.favorited ? '已收藏' : '已取消收藏')
  } catch {}
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

function handlePlaySong(item) {
  emit('play', item)
}
</script>

<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    :title="type === 'singer' ? '🎤 ' + (data?.name || '歌手详情') : '📋 ' + (data?.name || '歌单详情')"
    width="600px"
    top="5vh"
    class="detail-dialog"
    destroy-on-close
  >
    <div v-if="type === 'singer'" class="singer-detail">
      <div class="singer-header">
        <img :src="getCover(data)" class="singer-avatar-lg" @error="handleImageError" />
        <div class="singer-meta">
          <h2>{{ data?.name }}</h2>
          <p class="singer-region" v-if="data?.region">{{ data.region }}</p>
          <p class="singer-remark" v-if="data?.remark">{{ data.remark }}</p>
          <button
            :class="['fav-btn', { active: isFavorited }]"
            @click="handleToggleFavorite"
          >{{ isFavorited ? '⭐ 已收藏' : '☆ 收藏歌手' }}</button>
        </div>
      </div>
      <div class="song-section">
        <h3>歌曲列表 ({{ songs.length }})</h3>
        <div v-for="(item, idx) in songs" :key="item.id" class="song-row" @click="handlePlaySong(item)">
          <span class="row-idx">{{ idx + 1 }}</span>
          <div class="row-info">
            <span class="row-name">{{ item.name }}</span>
            <span class="row-duration" v-if="item.duration">{{ Math.floor(item.duration / 60) }}:{{ String(item.duration % 60).padStart(2, '0') }}</span>
          </div>
          <button class="row-play" @click.stop="handlePlaySong(item)">▶</button>
        </div>
        <el-empty v-if="songs.length === 0" description="暂无歌曲" :image-size="80" />
      </div>
    </div>

    <div v-else-if="type === 'playlist'" class="playlist-detail">
      <div class="playlist-header">
        <img :src="getCover(data)" class="playlist-cover" @error="handleImageError" />
        <div class="playlist-meta">
          <h2>{{ data?.name }}</h2>
          <p class="playlist-desc" v-if="data?.description">{{ data.description }}</p>
          <p class="playlist-count">{{ songs.length }} 首歌曲</p>
          <button
            :class="['fav-btn', { active: isFavorited }]"
            @click="handleToggleFavorite"
          >{{ isFavorited ? '⭐ 已收藏' : '☆ 收藏歌单' }}</button>
        </div>
      </div>
      <div class="song-section">
        <h3>歌曲列表 ({{ songs.length }})</h3>
        <div v-for="(item, idx) in songs" :key="item.id" class="song-row" @click="handlePlaySong(item)">
          <span class="row-idx">{{ idx + 1 }}</span>
          <div class="row-info">
            <span class="row-name">{{ item.name }}</span>
            <span class="row-duration" v-if="item.duration">{{ Math.floor(item.duration / 60) }}:{{ String(item.duration % 60).padStart(2, '0') }}</span>
          </div>
          <button class="row-play" @click.stop="handlePlaySong(item)">▶</button>
        </div>
        <el-empty v-if="songs.length === 0" description="暂无歌曲" :image-size="80" />
      </div>
    </div>
  </el-dialog>
</template>

<style scoped lang="scss">
$primary: #3b82f6;

.singer-header, .playlist-header {
  display: flex; gap: 20px; margin-bottom: 24px;
}

.singer-avatar-lg {
  width: 120px; height: 120px; border-radius: 50%; object-fit: cover;
  border: 3px solid #fff; box-shadow: 0 4px 16px rgba(59,130,246,0.15);
}

.playlist-cover {
  width: 120px; height: 120px; border-radius: 14px; object-fit: cover;
  box-shadow: 0 4px 16px rgba(59,130,246,0.15);
}

.singer-meta, .playlist-meta {
  flex: 1; display: flex; flex-direction: column; gap: 4px;
}
.singer-meta h2, .playlist-meta h2 { font-size: 20px; font-weight: 700; color: #1e293b; }
.singer-region, .playlist-count { font-size: 13px; color: #94a3b8; }
.singer-remark { font-size: 13px; color: #64748b; margin-top: 2px; }
.playlist-desc { font-size: 13px; color: #64748b; }

.fav-btn {
  margin-top: 8px; padding: 6px 18px; border-radius: 18px;
  border: 1px solid #e2e8f0; background: #fff; font-size: 13px;
  cursor: pointer; transition: all 0.2s; align-self: flex-start;
}
.fav-btn:hover { border-color: $primary; color: $primary; }
.fav-btn.active { background: rgba($primary, 0.08); border-color: $primary; color: $primary; }

.song-section h3 { font-size: 15px; font-weight: 600; color: #1e293b; margin-bottom: 12px; }

.song-row {
  display: flex; align-items: center; padding: 10px 12px;
  border-radius: 10px; cursor: pointer; transition: background 0.2s;
}
.song-row:hover { background: rgba($primary, 0.05); }

.row-idx { width: 28px; font-size: 13px; color: #94a3b8; text-align: center; }
.row-info { flex: 1; display: flex; align-items: center; gap: 12px; }
.row-name { font-size: 14px; font-weight: 500; color: #1e293b; }
.row-duration { font-size: 12px; color: #94a3b8; margin-left: auto; }

.row-play {
  width: 28px; height: 28px; border-radius: 50%; border: none;
  background: rgba($primary, 0.1); color: $primary; font-size: 12px;
  cursor: pointer; transition: all 0.2s; flex-shrink: 0;
}
.row-play:hover { background: $primary; color: #fff; }
</style>