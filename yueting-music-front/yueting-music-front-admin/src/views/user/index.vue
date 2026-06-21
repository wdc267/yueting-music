<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUser, updateUserPassword, deleteUser } from '@/api/user'

const userList = ref([])
const loading = ref(false)
const queryForm = reactive({
  username: '',
  nickname: '',
  status: null,
  page: 1,
  pageSize: 10
})
const total = ref(0)

const editDialogVisible = ref(false)
const editForm = ref({ id: null, nickname: '', gender: 0, status: 1 })

const passwordDialogVisible = ref(false)
const passwordForm = ref({ id: null, newPassword: '' })

async function loadList() {
  loading.value = true
  try {
    const res = await getUserList(queryForm)
    userList.value = res.data || []
    total.value = res.data ? res.data.length : 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryForm.username = ''
  queryForm.nickname = ''
  queryForm.status = null
  queryForm.page = 1
  loadList()
}

function handleSizeChange(val) {
  queryForm.pageSize = val
  loadList()
}

function handleCurrentChange(val) {
  queryForm.page = val
  loadList()
}

function openEdit(row) {
  editForm.value = { id: row.id, nickname: row.nickname, gender: row.gender, status: row.status }
  editDialogVisible.value = true
}

async function handleEditSave() {
  try {
    await updateUser(editForm.value)
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    loadList()
  } catch (e) {
    console.error(e)
  }
}

function openPassword(row) {
  passwordForm.value = { id: row.id, newPassword: '' }
  passwordDialogVisible.value = true
}

async function handlePasswordSave() {
  if (!passwordForm.value.newPassword.trim()) {
    ElMessage.warning('请输入新密码')
    return
  }
  try {
    await updateUserPassword(passwordForm.value)
    ElMessage.success('密码修改成功')
    passwordDialogVisible.value = false
  } catch (e) {
    console.error(e)
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除用户「' + row.username + '」吗？删除后不可恢复。', '提示', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  loadList()
}

onMounted(() => {
  loadList()
})
</script>

<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :model="queryForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="搜索用户名" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="queryForm.nickname" placeholder="搜索昵称" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 100px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
        <span>用户列表（共 {{ total }} 条）</span>
      </template>
      <el-table :data="userList" stripe v-loading="loading">
        <el-table-column label="头像" width="70">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40" v-if="row.avatar" />
            <el-avatar :size="40" v-else icon="UserFilled" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column label="性别" width="70">
          <template #default="{ row }">
            <span>{{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '未知' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="170" />
        <el-table-column prop="createTime" label="注册时间" width="170" />
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link size="small" @click="openPassword(row)">改密</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 16px; display: flex; justify-content: flex-end;">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="editDialogVisible" title="编辑用户" width="460px" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender">
            <el-radio :value="0">未知</el-radio>
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="editForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px" :close-on-click-modal="false">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePasswordSave">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>
