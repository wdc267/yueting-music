<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSongList, saveSong, updateSong, deleteSong } from '@/api/song'
import { getSingerList } from '@/api/singer'
import { getDictByCode } from '@/api/dict'
import MusicPlayer from '@/components/MusicPlayer.vue'

const songList = ref([])
const singerList = ref([])
const typeOptions = ref([])
const styleOptions = ref([])
const queryForm = ref({ name: '', singerId: null, musicType: '', status: null })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const playerVisible = ref(false)
const playingSong = ref(null)
const formData = ref({
  id: null, name: '', singerId: null, cover: '', url: '',
  musicType: '', musicStyle: '', duration: null, lyric: '',
  playCount: 0, likeCount: 0, favCount: 0,
  status: 1, remark: '', sortOrder: 0
})

const rules = {
  name: [{ required: true, message: '请输入歌曲名称', trigger: 'blur' }]
}

async function loadList() {
  const res = await getSongList(queryForm.value)
  songList.value = res.data || []
}

async function loadSingers() {
  try { const res = await getSingerList({}); singerList.value = res.data || [] } catch (e) {}
}

async function loadDicts() {
  try {
    const [typeRes, styleRes] = await Promise.all([
      getDictByCode('music_type'), getDictByCode('music_style')
    ])
    typeOptions.value = typeRes.data || []
    styleOptions.value = styleRes.data || []
  } catch (e) {}
}

function resetQuery() {
  queryForm.value = { name: '', singerId: null, musicType: '', status: null }
  loadList()
}

function addSong() {
  dialogTitle.value = '新增歌曲'
  formData.value = {
    id: null, name: '', singerId: null, cover: '', url: '',
    musicType: '', musicStyle: '', duration: null, lyric: '',
    playCount: 0, likeCount: 0, favCount: 0,
    status: 1, remark: '', sortOrder: 0
  }
  dialogVisible.value = true
}

function editSong(row) {
  dialogTitle.value = '编辑歌曲'
  formData.value = { ...row }
  dialogVisible.value = true
}

function closeDialog() { dialogVisible.value = false }

async function handleSave() {
  if (formData.value.id) {
    await updateSong(formData.value)
    ElMessage.success('更新成功')
  } else {
    await saveSong(formData.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await loadList()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除歌曲「' + row.name + '」吗？', '提示', { type: 'warning' })
  await deleteSong(row.id)
  ElMessage.success('删除成功')
  await loadList()
}

function playSong(row) {
  if (!row.url) {
    ElMessage.warning('该歌曲暂无音频文件')
    return
  }
  playingSong.value = row
  playerVisible.value = true
}

function handleCoverSuccess(response) {
  if (response.code === 200) { formData.value.cover = response.data }
  else { ElMessage.error(response.msg || '上传失败') }
}
function handleCoverError() { ElMessage.error('上传失败') }
function handleMusicSuccess(response) {
  if (response.code === 200) { formData.value.url = response.data }
  else { ElMessage.error(response.msg || '上传失败') }
}
function handleMusicError() { ElMessage.error('上传失败') }

function formatDuration(seconds) {
  if (!seconds) return '-'
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return m + ':' + String(s).padStart(2, '0')
}

function formatCount(n) {
  if (n == null) return '-'
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  return n
}

onMounted(() => {
  loadSingers()
  loadDicts()
  loadList()
})
</script>

<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :model="queryForm" inline>
        <el-form-item label="歌曲名称">
          <el-input v-model="queryForm.name" placeholder="搜索歌曲" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="歌手">
          <el-select v-model="queryForm.singerId" placeholder="全部歌手" clearable style="width: 160px" filterable>
            <el-option v-for="s in singerList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="音乐类型">
          <el-select v-model="queryForm.musicType" placeholder="全部类型" clearable style="width: 120px">
            <el-option v-for="item in typeOptions" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 100px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>歌曲列表</span>
          <el-button type="primary" size="small" @click="addSong">新增歌曲</el-button>
        </div>
      </template>
      <el-table :data="songList" stripe>
        <el-table-column label="封面" width="65">
          <template #default="{ row }">
            <el-avatar :src="row.cover" :size="36" v-if="row.cover" />
            <el-avatar :size="36" v-else icon="Headset" style="background:#eee;color:#999" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="歌曲名称" min-width="130" show-overflow-tooltip />
        <el-table-column prop="singerName" label="歌手" width="120" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">{{ typeOptions.find(t => t.dictValue === row.musicType)?.dictLabel || row.musicType }}</template>
        </el-table-column>
        <el-table-column label="时长" width="65">
          <template #default="{ row }">{{ formatDuration(row.duration) }}</template>
        </el-table-column>
        <el-table-column label="播放" width="60" align="center">
          <template #default="{ row }">{{ formatCount(row.playCount) }}</template>
        </el-table-column>
        <el-table-column label="点赞" width="60" align="center">
          <template #default="{ row }">{{ formatCount(row.likeCount) }}</template>
        </el-table-column>
        <el-table-column label="收藏" width="60" align="center">
          <template #default="{ row }">{{ formatCount(row.favCount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="65">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="185" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" type="success" @click="playSong(row)">
              <el-icon style="margin-right: 2px; vertical-align: middle"><VideoPlay /></el-icon> 播放
            </el-button>
            <el-button link size="small" @click="editSong(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" :close-on-click-modal="false">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="歌曲名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入歌曲名称" />
        </el-form-item>
        <el-form-item label="歌手">
          <el-select v-model="formData.singerId" placeholder="请选择歌手" style="width: 100%" filterable>
            <el-option v-for="s in singerList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图">
          <div style="display: flex; align-items: center; gap: 12px;">
            <el-upload :action="'/api/upload/image'" :show-file-list="false"
              :on-success="handleCoverSuccess" :on-error="handleCoverError" accept="image/*">
              <el-button size="small" type="primary">选择图片</el-button>
            </el-upload>
            <el-avatar :src="formData.cover" :size="60" v-if="formData.cover" />
          </div>
        </el-form-item>
        <el-form-item label="歌曲文件">
          <el-upload :action="'/api/upload/image'" :show-file-list="false"
            :on-success="handleMusicSuccess" :on-error="handleMusicError">
            <el-button size="small" type="primary">上传文件</el-button>
          </el-upload>
          <span v-if="formData.url" style="margin-left: 8px; font-size: 12px; color: #999">{{ formData.url }}</span>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="音乐类型">
              <el-select v-model="formData.musicType" placeholder="请选择" style="width: 100%">
                <el-option v-for="item in typeOptions" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="风格">
              <el-select v-model="formData.musicStyle" placeholder="请选择" style="width: 100%">
                <el-option v-for="item in styleOptions" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="时长(秒)">
          <el-input-number v-model="formData.duration" :min="0" :max="99999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="歌词">
          <el-input v-model="formData.lyric" type="textarea" :rows="4" placeholder="可选，输入歌词文本" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="播放数">
              <el-input-number v-model="formData.playCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="点赞数">
              <el-input-number v-model="formData.likeCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="收藏数">
              <el-input-number v-model="formData.favCount" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <MusicPlayer v-model:visible="playerVisible" :song="playingSong" />
  </div>
</template>