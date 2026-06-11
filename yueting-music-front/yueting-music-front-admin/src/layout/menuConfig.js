import { DataBoard, Microphone, Headset, Promotion, Setting, User } from '@element-plus/icons-vue'

export const menuItems = [
  {
    name: '控制台',
    path: '/dashboard',
    icon: 'DataBoard',
    children: [
      {
        name: '数据看板',
        path: '/dashboard',
        icon: 'DataBoard'
      }
    ]
  },
  {
    name: '内容管理',
    path: '/content',
    icon: 'Microphone',
    children: [
      {
        name: '歌曲管理',
        path: '/content/song',
        icon: 'Microphone'
      },
      {
        name: '歌单管理',
        path: '/content/playlist',
        icon: 'Headset'
      },
      {
        name: '歌手管理',
        path: '/content/singer',
        icon: 'Microphone'
      }
    ]
  },
  {
    name: '运营管理',
    path: '/operation',
    icon: 'Promotion',
    children: [
      {
        name: '首页推荐',
        path: '/operation/recommend',
        icon: 'Promotion'
      }
    ]
  },
  {
    name: '系统',
    path: '/system',
    icon: 'Setting',
    children: [
      {
        name: '字典管理',
        path: '/system/dictionary',
        icon: 'Setting'
      },
      {
        name: '用户管理',
        path: '/system/user',
        icon: 'User'
      }
    ]
  }
]