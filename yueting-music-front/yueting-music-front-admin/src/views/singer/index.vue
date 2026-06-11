<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSingerList, saveSinger, updateSinger, deleteSinger, updateSingerSort } from '@/api/singer'
import { getDictByCode } from '@/api/dict'
import Sortable from 'sortablejs'

const singerList = ref([])
const regionOptions = ref([])
const queryForm = ref({ name: '', region: '', status: null })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formData = ref({
  id: null, name: '', avatar: '', region: '', status: 1, remark: '', sortOrder: 0
})
const sortChanged = ref(false)
const tableRef = ref(null)
let sortableInstance = null

const rules = {
  name: [{ required: true, message: '请输入歌手名称', trigger: 'blur' }]
}

async function loadList() {
  const res = await getSingerList(queryForm.value)
  singerList.value = res.data || []
  sortChanged.value = false
  await nextTick()
  initSortable()
}

async function loadRegionOptions() {
  try {
    const res = await getDictByCode('singer_region')
    regionOptions.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

function resetQuery() {
  queryForm.value = { name: '', region: '', status: null }
  loadList()
}

function initSortable() {
  if (sortableInstance) {
    sortableInstance.destroy()
  }
  const el = tableRef.value?.$el?.querySelector('.el-table__body-wrapper tbody')
  if (!el) return
  sortableInstance = new Sortable(el, {
    handle: '.drag-handle',
    animation: 200,
    onEnd: () => {
      sortChanged.value = true
    }
  })
}

function addSinger() {
  dialogTitle.value = '新增歌手'
  formData.value = { id: null, name: '', avatar: '', region: '', status: 1, remark: '', sortOrder: 0 }
  dialogVisible.value = true
}

function editSinger(row) {
  dialogTitle.value = '编辑歌手'
  formData.value = { ...row }
  dialogVisible.value = true
}

function closeDialog() {
  dialogVisible.value = false
}

async function handleSave() {
  if (formData.value.id) {
    await updateSinger(formData.value)
    ElMessage.success('更新成功')
  } else {
    await saveSinger(formData.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  await loadList()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除歌手「' + row.name + '」吗？', '提示', { type: 'warning' })
  await deleteSinger(row.id)
  ElMessage.success('删除成功')
  await loadList()
}

function handleAvatarSuccess(response) {
  if (response.code === 200) {
    formData.value.avatar = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

function handleAvatarError() {
  ElMessage.error('上传失败')
}

async function handleSaveSort() {
  const list = singerList.value.map((item, index) => ({
    id: item.id,
    sortOrder: index
  }))
  await updateSingerSort(list)
  ElMessage.success('排序保存成功')
  sortChanged.value = false
  await nextTick()
  initSortable()
}

onMounted(() => {
  loadRegionOptions()
  loadList()
})
</script>

<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :model="queryForm" inline>
        <el-form-item label="歌手名称">
          <el-input v-model="queryForm.name" placeholder="搜索歌手" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="地区">
          <el-select v-model="queryForm.region" placeholder="全部地区" clearable style="width: 130px">
            <el-option v-for="item in regionOptions" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
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
          <span>歌手列表</span>
          <div>
            <el-button
              size="small"
              type="warning"
              :disabled="!sortChanged"
              @click="handleSaveSort"
            >保存排序</el-button>
            <el-button type="primary" size="small" @click="addSinger" style="margin-left: 8px">新增歌手</el-button>
          </div>
        </div>
      </template>
      <el-table ref="tableRef" :data="singerList" stripe row-key="id">
        <el-table-column label="排序" width="60">
          <template #default>
            <el-icon class="drag-handle" style="cursor: grab; color: #999; font-size: 16px"><Rank /></el-icon>
          </template>
        </el-table-column>
        <el-table-column label="头像" width="70">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40" v-if="row.avatar" />
            <el-avatar :size="40" v-else icon="UserFilled" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="歌手名称" min-width="120" />
        <el-table-column prop="region" label="地区" width="100">
          <template #default="{ row }">
            <span>{{ regionOptions.find(r => r.dictValue === row.region)?.dictLabel || row.region }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" @click="editSinger(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" :close-on-click-modal="false">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="歌手名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入歌手名称" />
        </el-form-item>
        <el-form-item label="头像">
          <div style="display: flex; align-items: center; gap: 12px;">
            <el-upload
              :action="'/api/upload/image'"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :on-error="handleAvatarError"
              accept="image/*"
            >
              <el-button size="small" type="primary">选择图片</el-button>
            </el-upload>
            <el-avatar :src="formData.avatar" :size="60" v-if="formData.avatar" />
          </div>
        </el-form-item>
        <el-form-item label="地区">
          <el-select v-model="formData.region" placeholder="请选择地区" style="width: 100%">
            <el-option v-for="item in regionOptions" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="可选备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>