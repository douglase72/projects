<template>
  <Menubar class="px-4 py-2">
    <template #start>
      <RouterLink to="/" class="text-4xl font-bold text-white">EMDB</RouterLink>
    </template>

    <template #end>
      <Button :label="user" @click="login" class="bg-black border-black hover:text-zinc-400 text-white" />
      <Menu ref="userPopup" :model="userMenu" :popup="true" />
    </template>
  </Menubar>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue';

  import keycloak from '@/auth/keycloak';
  
  const isAuthenticated = ref(false);
  const user = ref('Login');
  const userMenu = ref([
    { 
      label: "Profile"
    },
    {
      label: "Logout",
      command: () => { logout(); }
    }
  ]);
  const userPopup = ref();

  const login = (event: Event) => {
    if (!isAuthenticated.value) {
      keycloak.login();
    } else {
      userPopup.value.toggle(event);
    }
  };

  const logout = () => {
    user.value = "Login";
    keycloak.logout({
      redirectUri: window.location.origin + '/emdb/'
    });
  };

  onMounted(() => {
    if (keycloak.authenticated) {
      isAuthenticated.value = true;
      user.value = keycloak.idTokenParsed?.given_name || keycloak.idTokenParsed?.preferred_username;
    }
  });
</script>