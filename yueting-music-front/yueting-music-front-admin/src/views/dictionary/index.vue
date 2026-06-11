<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getDictTypes,
  getDictByCode,
  getDictById,
  saveDict,
  updateDict,
  deleteDict
} from '@/api/dict'

const typeList = ref([])
const dataList = ref([])
const selectedType = ref(null)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formType = ref('type')
const formData = ref({
  id: null,
  parentId: 0,
  dictName: '',
  dictCode: '',
  dictLabel: '',
  dictValue: '',
  status: 1,
  remark: '',
  sortOrder: 0
})

const rules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictCode: [{ required: true, message: '请输入字典编码', trigger: 'blur' }],
  dictLabel: [{ required: true, message: '请输入数据标签', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入数据键值', trigger: 'blur' }]
}

function formatRules() {
  if (formType.value === 'type') {
    return { dictName: rules.dictName, dictCode: rules.dictCode }
  }
  return { dictLabel: rules.dictLabel, dictValue: rules.dictValue }
}

async function loadTypes() {
  try {
    const res = await getDictTypes()
    typeList.value = res.data || []
  } catch (e) {
    console.error('loadTypes error', e)
  }
}

function selectType(type) {
  selectedType.value = type
  if (type) {
    loadTypeData(type)
  }
}

function addType() {
  formType.value = 'type'
  dialogTitle.value = '新增字典类型'
  formData.value = { id: null, parentId: 0, dictName: '', dictCode: '', dictLabel: '', dictValue: '', status: 1, remark: '', sortOrder: 0 }
  dialogVisible.value = true
}

function addData(type) {
  formType.value = 'data'
  dialogTitle.value = '新增字典数据 - ' + type.dictName
  formData.value = { id: null, parentId: type.id, dictName: '', dictCode: '', dictLabel: '', dictValue: '', status: 1, remark: '', sortOrder: 0 }
  dialogVisible.value = true
}

function editData(data) {
  formType.value = 'data'
  dialogTitle.value = '编辑字典数据'
  formData.value = { ...data }
  dialogVisible.value = true
}

function closeDialog() {
  dialogVisible.value = false
}

async function handleSave() {
  if (formData.value.id) {
    await updateDict(formData.value)
    ElMessage.success('更新成功')
  } else {
    await saveDict(formData.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  if (formType.value === 'type') {
    await loadTypes()
  } else if (selectedType.value) {
    await loadTypeData(selectedType.value)
    await loadTypes()
  }
}

async function handleDelete(row, isType) {
  await ElMessageBox.confirm('确认删除该' + (isType ? '字典类型' : '字典数据') + '吗？', '提示', { type: 'warning' })
  await deleteDict(row.id)
  ElMessage.success('删除成功')
  if (isType) {
    selectedType.value = null
    dataList.value = []
    await loadTypes()
  } else {
    await loadTypeData(selectedType.value)
    await loadTypes()
  }
}

async function loadTypeData(type) {
  try {
    const res = await getDictByCode(type.dictCode)
    dataList.value = res.data || []
  } catch (e) {
    console.error('loadTypeData error', e)
  }
}

onMounted(() => {
  loadTypes()
})
</script>

<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="10">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>字典类型</span>
              <el-button type="primary" size="small" @click="addType">新增类型</el-button>
            </div>
          </template>
          <el-table
            :data="typeList"
            highlight-current-row
            @row-click="selectType"
            style="cursor: pointer"
          >
            <el-table-column prop="dictName" label="字典名称" min-width="100" />
            <el-table-column prop="dictCode" label="编码" min-width="80" />
            <el-table-column label="状态" width="60">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button type="danger" link size="small" @click.stop="handleDelete(row, true)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>字典数据 {{ selectedType ? '- ' + selectedType.dictName : '' }}</span>
              <el-button
                type="primary"
                size="small"
                :disabled="!selectedType"
                @click="addData(selectedType)"
              >新增数据</el-button>
            </div>
          </template>
          <el-table :data="dataList" v-if="selectedType">
            <el-table-column prop="dictLabel" label="标签" min-width="100" />
            <el-table-column prop="dictValue" label="键值" min-width="100" />
            <el-table-column label="状态" width="60">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="sortOrder" label="排序" width="60" />
            <el-table-column prop="remark" label="备注" min-width="100" />
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <el-button link size="small" @click="editData(row)">编辑</el-button>
                <el-button type="danger" link size="small" @click.stop="handleDelete(row, false)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="请选择一个字典类型" />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" :close-on-click-modal="false">
      <el-form :model="formData" :rules="formatRules()" ref="formRef" label-width="100px">
        <template v-if="formType === 'type'">
          <el-form-item label="字典名称" prop="dictName">
            <el-input v-model="formData.dictName" placeholder="如：音乐类型" />
          </el-form-item>
          <el-form-item label="字典编码" prop="dictCode">
            <el-input v-model="formData.dictCode" placeholder="如：music_type" />
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item label="数据标签" prop="dictLabel">
            <el-input v-model="formData.dictLabel" placeholder="如：流行" />
          </el-form-item>
          <el-form-item label="数据键值" prop="dictValue">
            <el-input v-model="formData.dictValue" placeholder="如：pop" />
          </el-form-item>
        </template>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>