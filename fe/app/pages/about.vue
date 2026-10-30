<template>
  <div class="au-page">

    <header class="au-header">
      <h1 class="au-title">About us</h1>
      <p class="au-subtitle">
        A full-stack web application built to be fast, reliable, and easy to run — from the Java backend to the Vue frontend.
      </p>
    </header>

    <section class="au-columns">
      <div class="au-column">
        <h2 class="au-heading">What is this?</h2>
        <p class="au-text">
          This is a web platform built with a Java Spring Boot backend and a Nuxt frontend. It is packaged with Docker and served through Nginx, so it runs the same way on a laptop and on a server.
        </p>
      </div>

      <div class="au-column">
        <h2 class="au-heading">What it does</h2>
        <ul class="au-list">
          <li>Serves a clean, responsive web interface</li>
          <li>Exposes a REST API built on Spring Boot</li>
          <li>Renders pages quickly with Nuxt and Vue.js</li>
          <li>Runs in containers with Docker</li>
          <li>Routes traffic through an Nginx reverse proxy</li>
        </ul>
      </div>

      <div class="au-column">
        <h2 class="au-heading">Why I built it</h2>
        <p class="au-text">
          I wanted a real project that connects every layer of a modern web application: backend, frontend, deployment, and version control. Building it end to end is the best way to learn how the pieces fit together.
        </p>
      </div>
    </section>

    <section class="au-tech">
      <h2 class="au-tech-title">Technologies used</h2>
      <div class="au-tags">
        <span class="au-tag">Java</span>
        <span class="au-tag">Java Spring Boot</span>
        <span class="au-tag">Web Development</span>
        <span class="au-tag">Vue.js</span>
        <span class="au-tag">Nuxt</span>
        <span class="au-tag">Docker</span>
        <span class="au-tag">Nginx</span>
        <span class="au-tag">Github</span>
      </div>
    </section>

    <section class="au-contact">
      <h2 class="au-contact-title">Get in touch</h2>

      <a class="au-contact-row" href="mailto:svetoivanovic788@gmail.com">
        <UIcon name="i-lucide-mail" size="22" />
        <span>svetoivanovic788@gmail.com</span>
      </a>

      <a class="au-contact-row" href="tel:+38267243849">
        <UIcon name="i-lucide-phone" size="22" />
        <span>+38267 243 849</span>
      </a>

      <a class="au-contact-row" href="https://www.linkedin.com/in/svetozar-ivanovi%C4%87-ab7261334/" target="_blank">
        <UIcon name="i-lucide-linkedin" size="22" />
        <span>LinkedIn</span>
      </a>
    </section>

  </div>
</template>

<script setup lang="ts">
declare module 'nuxt/app' {
  interface NuxtLayouts {
    "publicCustom": unknown
  }
}

definePageMeta({
  layout: 'public-custom',
})
</script>

<style scoped>
.au-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 72px;
  padding: 48px 24px 96px;
  color: #020079;
}

.au-header {
  text-align: center;
  max-width: 560px;
}

.au-title {
  margin: 0 0 14px;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: clamp(40px, 6vw, 56px);
  font-weight: 700;
  letter-spacing: -0.5px;
  color: #020092;
}

.au-subtitle {
  margin: 0;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: 17px;
  line-height: 1.6;
  color: #0600af;
}

.au-columns {
  display: flex;
  flex-direction: row;
  justify-content: center;
  gap: 56px;
  width: 100%;
  max-width: 1020px;
}

.au-column {
  flex: 1 1 0;
  max-width: 300px;
}

.au-heading {
  margin: 0 0 12px;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: 22px;
  font-weight: 700;
  color: #020080;
}

.au-text,
.au-list {
  margin: 0;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: 15px;
  line-height: 1.8;
  color: #6d78a6;
}

.au-list {
  padding-left: 18px;
}


.au-tech {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  max-width: 820px;
}

.au-tech-title {
  margin: 0;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: 22px;
  font-weight: 700;
  color: #1f1d4a;
}

.au-tags {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
}

.au-tag {
  padding: 8px 18px;
  border: 1px solid #384e68;
  border-radius: 999px;
  background: #b8b4f4;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: 14px;
  color: #2a3555;
}

.au-contact {
  display: flex;
  flex-direction: column;
  gap: 14px;
  width: min(440px, 100%);
  padding: 28px 26px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.28);
  backdrop-filter: blur(6px);
}

.au-contact-title {
  margin: 0 0 4px;
  font-family: 'Trebuchet MS', 'Segoe UI', Arial, sans-serif;
  font-size: 26px;
  font-weight: 800;
  color: #09037c;
}

.au-contact-row {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.4);
  font-family: 'Trebuchet MS', 'Segoe UI', Arial, sans-serif;
  font-size: 16px;
  font-weight: 700;
  color: #0d0958;
  text-decoration: none;
  transition: background 0.2s ease;
}

.au-contact-row:hover {
  background: rgba(255, 255, 255, 0.65);
}

@media (max-width: 860px) {
  .au-columns {
    flex-direction: column;
    align-items: center;
    gap: 40px;
  }

  .au-column {
    max-width: 440px;
    width: 100%;
  }
}
</style>




