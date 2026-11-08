// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
   runtimeConfig: {
    public: {
      baseUrl:  'http://localhost:8080',
    },
  },
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
    modules: ['@nuxt/ui', '@pinia/nuxt','pinia-plugin-persistedstate/nuxt', 'shadcn-nuxt'],

    css: ['~/assets/css/main.css'],
    ui: {
    colorMode: false
  },
  build: {
        transpile: ['@unovis/vue', '@unovis/ts'],
  },
  vite: {
        optimizeDeps: {
             include: ['@unovis/ts > striptags'],
        },
  }
})
