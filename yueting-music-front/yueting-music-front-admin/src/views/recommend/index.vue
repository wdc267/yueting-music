<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRecommendList, saveRecommend, updateRecommend, deleteRecommend } from '@/api/recommend'
import { getSingerList } from '@/api/singer'
import { getSongList } from '@/api/song'
import { getPlaylistList } from '@/api/playlist'

const activeTab = ref('banner')
const list = ref([])
const singerList = ref([])
const songList = ref([])
const playlistList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')

const formData = ref({
  id: null, title: '', cover: '', resourceType: 'song', resourceId: null,
  recommendType: 'banner', status: 1, sortOrder: 0
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  cover: [{ required: true, message: '请上传轮播图封面', trigger: 'change' }],
  resourceId: [{ required: true, message: '请选择资源', trigger: 'change' }]
}

function getRules() {
  if (activeTab.value === 'banner') {
    return { title: rules.title, cover: rules.cover, resourceId: rules.resourceId }
  }
  return { title: rules.title, resourceId: rules.resourceId }
}

const resourceOptions = computed(() => {
  const map = { singer: singerList.value, song: songList.value, playlist: playlistList.value }
  const items = map[formData.value.resourceType] || []
  return items.map(item => ({
    label: item.name || item.title || item.dictLabel,
    value: item.id
  }))
})

async function loadList() {
  const res = await getRecommendList({ recommendType: activeTab.value })
  list.value = res.data || []
}

async function loadResources() {
  try {
    const [sr, sg, pl] = await Promise.all([
      getSingerList({}), getSongList({}), getPlaylistList({})
    ])
    singerList.value = sr.data || []
    songList.value = sg.data || []
    playlistList.value = pl.data || []
  } catch (e) { console.error(e) }
}

function switchTab(tab) {
  activeTab.value = tab
  loadList()
}

function addItem() {
  dialogTitle.value = activeTab.value === 'banner' ? '新增轮播图' : '新增今日推荐'
  formData.value = {
    id: null, title: '', cover: '', resourceType: 'song', resourceId: null,
    recommendType: activeTab.value, status: 1, sortOrder: 0
  }
  dialogVisible.value = true
}

function editItem(row) {
  dialogTitle.value = activeTab.value === 'banner' ? '编辑轮播图' : '编辑今日推荐'
  formData.value = { ...row }
  dialogVisible.value = true
}

function closeDialog() { dialogVisible.value = false }

async function handleSave() {
  if (formData.value.id) {
    await updateRecommend(formData.value)
    ElMessage.success('更新成功')
  } else {
    await saveRecommend(formData.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await loadList()
}

async function handleDelete(row) {
  const label = activeTab.value === 'banner' ? '轮播图' : '推荐项'
  await ElMessageBox.confirm('确认删除该' + label + '吗？', '提示', { type: 'warning' })
  await deleteRecommend(row.id)
  ElMessage.success('删除成功')
  await loadList()
}

function handleCoverSuccess(response) {
  if (response.code === 200) { formData.value.cover = response.data }
  else { ElMessage.error(response.msg || '上传失败') }
}

onMounted(() => {
  loadResources()
  loadList()
})
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <el-radio-group :model-value="activeTab" @change="switchTab">
            <el-radio-button value="banner">轮播图推荐</el-radio-button>
            <el-radio-button value="today">今日推荐</el-radio-button>
          </el-radio-group>
          <el-button type="primary" size="small" @click="addItem">
            {{ activeTab === 'banner' ? '新增轮播图' : '新增推荐' }}
          </el-button>
        </div>
      </template>
      <el-table :data="list" stripe>
        <el-table-column label="封面" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.cover" :size="activeTab === 'banner' ? 40 : 36"
              :shape="activeTab === 'banner' ? 'square' : 'square'" v-if="row.cover" />
            <el-avatar :size="36" shape="square" v-else icon="Picture" style="background:#eee;color:#999" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="140" show-overflow-tooltip />
        <el-table-column label="资源类型" width="100">
          <template #default="{ row }">
            {{ { singer: '歌手', song: '歌曲', playlist: '歌单' }[row.resourceType] || row.resourceType }}
          </template>
        </el-table-column>
        <el-table-column prop="resourceName" label="资源名称" min-width="140" show-overflow-tooltip />
        <el-table-column label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" @click="editItem(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" :close-on-click-modal="false">
      <el-form :model="formData" :rules="getRules()" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入推荐标题" />
        </el-form-item>
        <el-form-item v-if="activeTab === 'banner'" label="封面图" prop="cover">
          <div style="display: flex; align-items: center; gap: 12px;">
            <el-upload :action="'/api/upload/image'" :show-file-list="false"
              :on-success="handleCoverSuccess" accept="image/*">
              <el-button size="small" type="primary">选择图片</el-button>
            </el-upload>
            <el-avatar :src="formData.cover" :size="60" shape="square" v-if="formData.cover" />
            <span v-else style="font-size: 12px; color: #999">建议尺寸：16:9 长方形</span>
          </div>
        </el-form-item>
        <el-form-item v-else label="封面图">
          <div style="display: flex; align-items: center; gap: 12px;">
            <el-upload :action="'/api/upload/image'" :show-file-list="false"
              :on-success="handleCoverSuccess" accept="image/*">
              <el-button size="small" type="primary">选择图片</el-button>
            </el-upload>
            <el-avatar :src="formData.cover" :size="60" shape="square" v-if="formData.cover" />
            <span v-else style="font-size: 12px; color: #999">可选，不传则使用资源封面</span>
          </div>
        </el-form-item>
        <el-form-item label="资源类型">
          <el-select v-model="formData.resourceType" style="width: 100%">
            <el-option label="歌手" value="singer" />
            <el-option label="歌曲" value="song" />
            <el-option label="歌单" value="playlist" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择资源" prop="resourceId">
          <el-select v-model="formData.resourceId" placeholder="请选择" style="width: 100%" filterable>
            <el-option v-for="item in resourceOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>