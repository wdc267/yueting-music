<script setup>
import { useRoute } from 'vue-router'
import { menuItems } from './menuConfig'

const route = useRoute()

function getDefaultActive() {
  return route.path
}
</script>

<template>
  <el-menu
    :default-active="getDefaultActive()"
    router
    unique-opened
  >
    <template v-for="item in menuItems" :key="item.path">
      <el-sub-menu v-if="item.children && item.children.length" :index="item.path">
        <template #title>
          <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
          <span>{{ item.name }}</span>
        </template>
        <el-menu-item
          v-for="child in item.children"
          :key="child.path"
          :index="child.path"
        >
          <el-icon v-if="child.icon"><component :is="child.icon" /></el-icon>
          <span>{{ child.name }}</span>
        </el-menu-item>
      </el-sub-menu>
      <el-menu-item v-else :index="item.path">
        <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
        <span>{{ item.name }}</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>