import { dibootApi } from '@/utils/request'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import storage from 'store'
import store from '@/store'

// 状态码字符集
const STATE_CHARS = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopgrstuvwxyz'

// 授权中心
const authCenterUrl = process.env.VUE_APP_AUTH_CENTER_URL
// 当前客户端地址
const clientUrl = process.env.VUE_APP_CLIENT_URL
// 当前客户端ID
const clientId = process.env.VUE_APP_CLIENT_ID
// 平台端退出路由（实现单点退出）
const ssoLogoutPath = process.env.VUE_APP_SSO_LOGOUT_PATH

/**
 * 获取授权地址
 *
 * @param state 状态码
 * @return {string} 授权地址
 */
function getAuthorizeUri (state) {
  return `${authCenterUrl}/oauth/authorize?client_id=${clientId}&redirect_uri=${clientUrl}/callback&response_type=code&state=${state}`
}

/**
 * 登录
 */
export function login () {
  sessionStorage.setItem('refererUri', window.location.href)
  const state = getState()
  sessionStorage.setItem('state', state)
  window.location = getAuthorizeUri(state)
}

/**
 * 回滚
 */
export function callback () {
  let state = getQueryVariable('state')
  if (!state || state !== sessionStorage.getItem('state')) {
    sessionStorage.setItem('state', state = getState())
    window.location = getAuthorizeUri(state)
  }
  // 获取token后回滚
  dibootApi.get(`/auth/token`, { code: getQueryVariable('code') }).then(res => {
    storage.set(ACCESS_TOKEN, res.data, 7 * 24 * 60 * 60 * 1000)
    store.commit('SET_TOKEN', res.data)
    const uri = sessionStorage.getItem('refererUri') || process.env.BASE_URL
    window.location = uri.includes('login') ? process.env.BASE_URL : uri
  }).catch(() => {
    alert('获取token异常')
  })
}

/**
 * 注销
 */
export function logout () {
  if (isEnableSso() && ssoLogoutPath) {
    window.location = ssoLogoutPath
    // window.location = `${authCenterUrl}/logout?redirect_uri=${clientUrl}`
    return true
  }
}

/**
 * 获取状态
 *
 * @return {string} 随机状态值
 */
function getState () {
  let state = ''
  for (let i = 0; i < 6; i++) {
    state += STATE_CHARS[Math.floor(Math.random() * 52)]
  }
  return state
}

/**
 * 获取请变量值
 *
 * @param variable 变量
 * @return {string} 值
 */
function getQueryVariable (variable) {
  const query = window.location.search.substring(1)
  const vars = query.split('&')
  for (let i = 0; i < vars.length; i++) {
    const pair = vars[i].split('=')
    if (pair[0] === variable) return pair[1]
  }
  return ''
}

/**
 * 是否配置了SSO
 *
 * @return {boolean}
 */
export function isEnableSso () {
  return !!clientId
}
