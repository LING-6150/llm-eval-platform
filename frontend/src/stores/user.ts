import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userId = ref<number | null>(null)
  const username = ref<string>('')
  const isLoggedIn = ref(false)

  function setUser(id: number, name: string) {
    userId.value = id
    username.value = name
    isLoggedIn.value = true
  }

  function logout() {
    userId.value = null
    username.value = ''
    isLoggedIn.value = false
  }

  return { userId, username, isLoggedIn, setUser, logout }
})
