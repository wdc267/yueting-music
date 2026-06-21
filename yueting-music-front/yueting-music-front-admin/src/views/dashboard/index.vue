<script setup>
import { ref, onMounted } from 'vue'
import { getDashboardStatistics, getTopSongs, getTopSingers, getTopPlaylists } from '@/api/dashboard'

const stats = ref({
  songCount: 0, singerCount: 0, playlistCount: 0, userCount: 0,
  totalPlayCount: 0, totalLikeCount: 0, totalFavCount: 0
})
const topSongs = ref([])
const topSingers = ref([])
const topPlaylists = ref([])
const loading = ref(false)

const cards = [
  { key: 'songCount', label: '歌曲总数', icon: 'Headset', color: '#ff85a2' },
  { key: 'singerCount', label: '歌手总数', icon: 'Microphone', color: '#c084fc' },
  { key: 'playlistCount', label: '歌单总数', icon: 'List', color: '#6ee7b7' },
  { key: 'userCount', label: '用户总数', icon: 'User', color: '#fbbf24' }
]

function formatNum(num) {
  if (!num && num !== 0) return '-'
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  return num.toLocaleString()
}

function getCoverStyle(url) {
  if (url) return { backgroundImage: 'url(' + url + ')' }
  return { background: 'linear-gradient(135deg, #ff85a2, #c084fc)' }
}

async function loadData() {
  loading.value = true
  try {
    const [statRes, songsRes, singersRes, playlistsRes] = await Promise.all([
      getDashboardStatistics(),
      getTopSongs(),
      getTopSingers(),
      getTopPlaylists()
    ])
    if (statRes.data) stats.value = statRes.data
    topSongs.value = songsRes.data || []
    topSingers.value = singersRes.data || []
    topPlaylists.value = playlistsRes.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => { loadData() })
</script>

<template>
  <div v-loading="loading">
    <div style="margin-bottom: 24px;">
      <h2 style="margin: 0 0 4px 0; font-size: 20px; color: #1e293b;">数据看板</h2>
      <p style="margin: 0; color: #94a3b8; font-size: 13px;">悦听音乐数据总览</p>
    </div>

    <el-row :gutter="16" style="margin-bottom: 24px;">
      <el-col :span="6" v-for="card in cards" :key="card.key">
        <el-card shadow="hover" style="border-radius: 12px;">
          <div style="display: flex; align-items: center; gap: 16px;">
            <div :style="{
              width: '56px', height: '56px', borderRadius: '14px',
              background: card.color,
              display: 'flex', alignItems: 'center', justifyContent: 'center'
            }">
              <el-icon :size="28" color="#fff"><component :is="card.icon" /></el-icon>
            </div>
            <div>
              <div style="font-size: 13px; color: #94a3b8; margin-bottom: 4px;">{{ card.label }}</div>
              <div style="font-size: 28px; font-weight: 700; color: #1e293b;">{{ formatNum(stats[card.key]) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card style="border-radius: 12px;">
          <template #header>
            <div style="display: flex; align-items: center; gap: 8px;">
              <el-icon color="#ff85a2" :size="20"><Headset /></el-icon>
              <span style="font-weight: 600; color: #1e293b;">热门歌曲 Top10</span>
            </div>
          </template>
          <div v-if="topSongs.length" style="max-height: 420px; overflow-y: auto;">
            <div v-for="(item, idx) in topSongs" :key="item.id"
              style="display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid #f1f5f9;">
              <span style="width: 24px; text-align: center; font-weight: 700; color: #94a3b8; font-size: 14px;">{{ idx + 1 }}</span>
              <div :style="{ ...getCoverStyle(item.cover), width: '40px', height: '40px', borderRadius: '8px', backgroundSize: 'cover', backgroundPosition: 'center', flexShrink: 0 }" />
              <div style="flex: 1; min-width: 0;">
                <div style="font-size: 14px; font-weight: 500; color: #1e293b; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">{{ item.name }}</div>
                <div style="font-size: 12px; color: #94a3b8;">{{ item.singerName || '-' }}</div>
              </div>
              <div style="display: flex; gap: 12px; font-size: 12px; color: #64748b; flex-shrink: 0;">
                <span title="播放数">▶ {{ formatNum(item.playCount) }}</span>
                <span title="点赞数">❤ {{ formatNum(item.likeCount) }}</span>
                <span title="收藏数">⭐ {{ formatNum(item.favCount) }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无歌曲数据" :image-size="80" />
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card style="border-radius: 12px; margin-bottom: 16px;">
          <template #header>
            <div style="display: flex; align-items: center; gap: 8px;">
              <el-icon color="#c084fc" :size="20"><Microphone /></el-icon>
              <span style="font-weight: 600; color: #1e293b;">热门歌手 Top10</span>
            </div>
          </template>
          <div v-if="topSingers.length" style="max-height: 180px; overflow-y: auto;">
            <div v-for="(item, idx) in topSingers" :key="item.id"
              style="display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid #f1f5f9;">
              <span style="width: 24px; text-align: center; font-weight: 700; color: #94a3b8; font-size: 14px;">{{ idx + 1 }}</span>
              <el-avatar :src="item.cover" :size="40" shape="square" />
              <div style="flex: 1; min-width: 0;">
                <div style="font-size: 14px; font-weight: 500; color: #1e293b;">{{ item.name }}</div>
              </div>
              <div style="display: flex; gap: 12px; font-size: 12px; color: #64748b; flex-shrink: 0;">
                <span>▶ {{ formatNum(item.playCount) }}</span>
                <span>❤ {{ formatNum(item.likeCount) }}</span>
                <span>⭐ {{ formatNum(item.favCount) }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无歌手数据" :image-size="80" />
        </el-card>

        <el-card style="border-radius: 12px;">
          <template #header>
            <div style="display: flex; align-items: center; gap: 8px;">
              <el-icon color="#6ee7b7" :size="20"><List /></el-icon>
              <span style="font-weight: 600; color: #1e293b;">热门歌单 Top10</span>
            </div>
          </template>
          <div v-if="topPlaylists.length" style="max-height: 180px; overflow-y: auto;">
            <div v-for="(item, idx) in topPlaylists" :key="item.id"
              style="display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid #f1f5f9;">
              <span style="width: 24px; text-align: center; font-weight: 700; color: #94a3b8; font-size: 14px;">{{ idx + 1 }}</span>
              <div :style="{ ...getCoverStyle(item.cover), width: '40px', height: '40px', borderRadius: '8px', backgroundSize: 'cover', backgroundPosition: 'center', flexShrink: 0 }" />
              <div style="flex: 1; min-width: 0;">
                <div style="font-size: 14px; font-weight: 500; color: #1e293b; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">{{ item.name }}</div>
              </div>
              <div style="display: flex; gap: 12px; font-size: 12px; color: #64748b; flex-shrink: 0;">
                <span>▶ {{ formatNum(item.playCount) }}</span>
                <span>❤ {{ formatNum(item.likeCount) }}</span>
                <span>⭐ {{ formatNum(item.favCount) }}</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无歌单数据" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
