<template>
  <div class="page-wrapper">
    
    <div class="hero-section">
        <div class="hero-subsection">
          <span></span>
          <span></span>
          <span></span>
          <div>

          </div>
          <span></span>
          <span></span>
        </div>

        <div class="example-graph">
        
        
        </div>
    </div>


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