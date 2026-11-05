import { defineStore } from "pinia";
import { computed, ref } from 'vue';
import type { LogInResponse } from "../../services/usermanagement/types";
import type { UserInformationDetailed } from '../../services/usermanagement/types'


export const useUserStore = defineStore("userStore", () => {

  const user = ref<LogInResponse>();

  const isAuthenticated = computed(() => !!user.value?.authToken);
  const accessToken = computed(() => user.value?.authToken);

  const email = ref("unknow@gmail.com")
  const userInfo = ref<UserInformationDetailed | undefined>(undefined)
  const hasUserInfo = ref(false)
  const hasApiKey = ref(false)

  function initUser(data: LogInResponse) {
    user.value = data;
  }

  function removeUser() {
    user.value = undefined;
  }

  function setEmail(data: string) {
    email.value = data
  }

  function removeEmail() {
    email.value = "unknow@gmail.com"
  }

  function setUserInfo(data: UserInformationDetailed) {
    userInfo.value = data
    hasUserInfo.value = true
    if (!!userInfo.value?.aiApiKeys && userInfo.value?.aiApiKeys.length>0) {
      hasApiKey.value = true
    }
    else {
      hasApiKey.value = false
    }
  }

  function removeUserInfo() {
    userInfo.value = undefined
    hasUserInfo.value = false
    hasApiKey.value = false
  }

  function logOutUser() {
    removeEmail()
    removeUser()
    removeUserInfo()
  }


  return {
    user,
    email,
    isAuthenticated,
    accessToken,
    userInfo,
    hasUserInfo,
    hasApiKey,
    initUser,
    removeUser,
    setEmail,
    removeEmail,
    setUserInfo,
    removeUserInfo,
    logOutUser,
  };
}, {
  persist: true,
});