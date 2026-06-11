<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPlaylistList, savePlaylist, updatePlaylist, deletePlaylist } from '@/api/playlist'
import { getSongList } from '@/api/song'

const playlistList = ref([])
const songList = ref([])
const queryForm = ref({ name: '', status: null })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const songDialogVisible = ref(false)

const formData = ref({
  id: null, name: '', cover: '', description: '', status: 1, sortOrder: 0, songIds: []
})

const selectedSongIds = ref([])

const rules = {
  name: [{ required: true, message: '请输入歌单名称', trigger: 'blur' }]
}

async function loadList() {
  const res = await getPlaylistList(queryForm.value)
  playlistList.value = res.data || []
}

async function loadSongs() {
  try { const res = await getSongList({}); songList.value = res.data || [] } catch (e) {}
}

function resetQuery() {
  queryForm.value = { name: '', status: null }
  loadList()
}

function addPlaylist() {
  dialogTitle.value = '新增歌单'
  formData.value = { id: null, name: '', cover: '', description: '', status: 1, sortOrder: 0, songIds: [] }
  selectedSongIds.value = []
  dialogVisible.value = true
}

function editPlaylist(row) {
  dialogTitle.value = '编辑歌单'
  formData.value = {
    id: row.id, name: row.name, cover: row.cover || '', description: row.description || '',
    status: row.status, sortOrder: row.sortOrder || 0, songIds: []
  }
  selectedSongIds.value = []
  dialogVisible.value = true
}

function closeDialog() {
  dialogVisible.value = false
  selectedSongIds.value = []
}

function openSongSelector() {
  selectedSongIds.value = [...formData.value.songIds]
  songDialogVisible.value = true
}

function confirmSongs() {
  formData.value.songIds = [...selectedSongIds.value]
  songDialogVisible.value = false
}

async function handleSave() {
  if (formData.value.id) {
    await updatePlaylist(formData.value)
    ElMessage.success('更新成功')
  } else {
    await savePlaylist(formData.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await loadList()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除歌单「' + row.name + '」吗？', '提示', { type: 'warning' })
  await deletePlaylist(row.id)
  ElMessage.success('删除成功')
  await loadList()
}

function handleCoverSuccess(response) {
  if (response.code === 200) { formData.value.cover = response.data }
  else { ElMessage.error(response.msg || '上传失败') }
}

onMounted(() => {
  loadSongs()
  loadList()
})
</script>

<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :model="queryForm" inline>
        <el-form-item label="歌单名称">
          <el-input v-model="queryForm.name" placeholder="搜索歌单" clearable style="width: 180px" />
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
          <span>歌单列表</span>
          <el-button type="primary" size="small" @click="addPlaylist">新增歌单</el-button>
        </div>
      </template>
      <el-table :data="playlistList" stripe>
        <el-table-column label="封面" width="65">
          <template #default="{ row }">
            <el-avatar :src="row.cover" :size="36" shape="square" v-if="row.cover" />
            <el-avatar :size="36" shape="square" v-else icon="CollectionTag" style="background:#eee;color:#999" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="歌单名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="songCount" label="歌曲数" width="70" align="center" />
        <el-table-column label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" @click="editPlaylist(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" :close-on-click-modal="false">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="歌单名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入歌单名称" />
        </el-form-item>
        <el-form-item label="封面图">
          <div style="display: flex; align-items: center; gap: 12px;">
            <el-upload :action="'/api/upload/image'" :show-file-list="false"
              :on-success="handleCoverSuccess" accept="image/*">
              <el-button size="small" type="primary">选择图片</el-button>
            </el-upload>
            <el-avatar :src="formData.cover" :size="60" shape="square" v-if="formData.cover" />
          </div>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="可选" />
        </el-form-item>
        <el-form-item label="选择歌曲">
          <div style="display: flex; align-items: center; gap: 8px; width: 100%;">
            <el-button size="small" @click="openSongSelector">选择歌曲</el-button>
            <span style="font-size: 12px; color: #999">已选 {{ formData.songIds.length }} 首</span>
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="songDialogVisible" title="选择歌曲" width="600px" :close-on-click-modal="false">
      <el-input v-model="songSearch" placeholder="搜索歌曲" clearable style="margin-bottom: 12px" />
      <el-table
        :data="songList.filter(s => !songSearch || s.name.includes(songSearch))"
        @selection-change="selectedSongIds = $event.map(s => s.id)"
        :row-key="s => s.id"
        ref="songTable"
        height="360"
      >
        <el-table-column type="selection" width="45" :reserve-selection="true" />
        <el-table-column prop="name" label="歌曲名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="singerName" label="歌手" width="120" />
        <el-table-column label="时长" width="65">
          <template #default="{ row }">
            {{ row.duration ? Math.floor(row.duration/60) + ':' + String(row.duration%60).padStart(2,'0') : '-' }}
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="songDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSongs">确认 ({{ selectedSongIds.length }})</el-button>
      </template>
    </el-dialog>
  </div>
</template>