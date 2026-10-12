<script setup lang="ts">
import type { NavigationMenuItem } from '@nuxt/ui'

const items = ref<NavigationMenuItem[]>([
  {
    label: 'Home',
    icon: 'i-lucide-home',
    to: '/'
    
  },
  {
    label: 'About',
    icon: 'i-lucide-info',
    to: '/about'
  },
 {
    label: 'GitHub',
    icon: 'i-simple-icons-github',
    to: 'https://github.com/Sveto-Ivanovic/monetrax',
    target: '_blank'
  }
])

function redirectLogin() {
  navigateTo('/login')
}
</script>


<template>
  <div class="page-wrapper">
   
    <header class="navigation-header">
    
        <div class="logo-wrapper">
            
            <span class="brand-mark" aria-hidden="true"> 
                <span></span> 
                <span></span> 
                <span></span>
            </span>

            <span class="logo-txt"> Monetrax </span>
        </div>

        <div class="nav-links">

            <ULink :to="item.to" v-for="item in items" class="nav-element" :key="item.label">
              <UIcon :name="item.icon" class="size-5" /> 
              <span>
                {{item.label}}
              </span>
              
            </ULink>
        
        </div>


      <UButton v-on:click="redirectLogin()" color="neutral" variant="outline" class="login" trailing-icon="i-lucide-arrow-right">Log In</UButton>

    </header>


    <main class="page-main">
      <slot />
    </main>

  </div>
</template>



<style scoped>

.page-wrapper {
  width: 100%;
  display: flex;
  min-height: 100vh;
  flex-direction: column;
  align-items: center;
}

.navigation-header {
  position: fixed;
  top: 16px;
  z-index: 50;
padding-bottom: 16px;

  display: flex;
  justify-content: space-between;

  padding-left: 1rem;
  padding-right: 1rem;

  width: 50%;
  border-bottom: 1px solid blue;
}



.navigation-menu {
  border-radius: 16px;
  border: 1px solid rgb(255 255 255 / 70%);
  background-color: rgb(255 255 255 / 75%);

  padding: 0.5rem 0.75rem;

  box-shadow:
    0 10px 15px -3px rgb(15 23 42 / 5%),
    0 4px 6px -4px rgb(15 23 42 / 5%);

  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
}

.page-main {
  min-height: 100vh;
  padding-top: 6rem;
}

.brand-mark { 
    width: 27px; 
    height: 27px; 
    display: flex; 
    align-items: end; 
    justify-content: center; 
    gap: 3px; 
    padding: 5px; 
    background: #050a47; 
    border-radius: 8px; }

.brand-mark span { 
    display: block; 
    width: 4px; 
    border-radius: 3px; 
    background: #a8ead2; }

.brand-mark span:nth-child(1) { height: 8px; }
.brand-mark span:nth-child(2) { height: 13px; }
.brand-mark span:nth-child(3) { height: 10px; }

.logo-txt {
    font-family: 'Montserrat', sans-serif;
    font-weight: 600;
    background: linear-gradient(90deg, rgba(2, 0, 36, 1) 0%, rgba(9, 9, 121, 1) 35%, rgba(0, 212, 255, 1) 100%);
    -webkit-background-clip: text;
    background-clip: text;
    font-size: 26px;
    -webkit-text-fill-color: transparent;
    color: transparent;
    font-size: 24px;
    letter-spacing: -0.8px;
}

.nav-links{
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.nav-element {
  color: #727c8a;
  font-size: 16px;
  padding: 9px 13px;
  border-radius: 9px;

  display: flex;
  align-items: center;
  gap: 6px;
  font-family: 'Montserrat', sans-serif;
  font-size: 18px;
  font-weight: 600;
  text-decoration: none;

  transition:
    color 180ms ease,
    background-color 180ms ease;
}

.nav-element:hover {
  color: #253db8;
  background-color: #c7d8fc;
box-shadow: inset 0 -1px 0 #253db8;}

.login {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  padding: 10px 19px;
  border-radius: 10px;

  border: 1px solid #415bee;
  color: #2561e0;
  background: transparent;

  font-family: 'Montserrat', sans-serif;
  font-size: 16px;
  font-weight: 600;

  cursor: pointer;
  transition:
    background-color 180ms ease,
    transform 180ms ease,
    box-shadow 180ms ease;
}

.login:hover {
  background-color: #ccdefa;
  box-shadow: 0 5px 14px rgba(37, 61, 184, 0.2);
  transform: translateY(-1px);
}


.page-main {
  width: 100%;
  min-height: 100vh;
  box-sizing: border-box;
  padding-top: 110px;
}

 .logo-wrapper {
    gap: 16px;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
  }




@media (max-width: 1600px) {
.navigation-header {
    width: 50%;
  }
}


@media (max-width: 1250px) {
.navigation-header {
    width: 80%;
  }
}



@media (max-width: 830px) {
.navigation-header {
    width: 100%;
  }
}



@media (max-width: 640px) {
  .navigation-header {
    top: 10px;
    left: 12px;
    right: 12px;
    width: calc(100% - 24px);
    gap: 10px;
    padding: 12px;
    border-radius: 14px;
  }

  .logo-wrapper {
    gap: 7px;
    display: flex;
    flex-direction: row;
       justify-content: center;
    align-items: center;
  }

  .brand-mark {
    width: 30px;
    height: 30px;
  }

  .logo-txt {
    font-size: 19px;
    letter-spacing: -0.6px;
  }

  .nav-links {
    gap: 0;
  }

  .nav-element {
    padding: 8px;
    font-size: 12px;
  }

  .login {
    padding: 9px 12px;
    font-size: 12px;
  }
}

@media (max-width: 420px) {
  .navigation-header {
    flex-wrap: wrap;
  }

  .nav-links {
    order: 3;
    width: 100%;
    justify-content: center;
    border-top: 1px solid #e8edf5;
    padding-top: 6px;
  }

  .page-main {
    padding-top: 120px;
  }
}


</style>