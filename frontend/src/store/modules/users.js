import users from '@/api/users'


const state = () => ({
  accessToken: null,
  isLogin: false,
  userId: null,
  name: null,
  issuedDate: null,
  isIssued: false
})

const actions = {
  async getAccessToken({ commit }, credentials) {
    try {
      const response = await users.login(credentials)
      const accessToken = response.data.token
      commit('SET_ACCESS_TOKEN', accessToken)
      const payload = {
        userId: response.data.id,
        name: response.data.name,
        issuedDate: response.data.issuedAt
      }
      commit('SET_USERINFO', payload)
    } catch (error) {
      commit('LOGOUT')
    }
  },
  async didIssue({ commit }, { userId, didData }) {
    try {
      await users.didIssue(userId, didData)
      commit('SET_ISISSUED', true)
    } catch (error) {
      commit('SET_ISISSUED', false)
    }
  }
}

const mutations = {
  SET_ACCESS_TOKEN(state, accessToken) {
    state.accessToken = accessToken
    state.isLogin = true
  },
  SET_ISISSUED(state, payload) {
    state.isIssued = payload
    if (payload) {
      state.issuedDate = new Date().toLocaleDateString().replace(/\s/g, '')
    }
  },
  SET_USERINFO(state, payload) {
    state.userId = payload.userId
    state.name = payload.name
    state.issuedDate = payload.issuedDate
  },
  LOGOUT(state) {
    state.accessToken = null
    state.isLogin = false
    state.userId = null
    state.name = null
    state.issuedDate = null
    state.isIssued = false
  }
}

const getters = {

}

export default {
  namespaced: true,
  state,
  actions,
  mutations,
  getters
}