<template>
  <div class="page-wrapper">
    
    <div class="hero-section">
        <div class="hero-subsection">
          
          <div class="main-text">
            
            <div style="display: flex; flex-direction: row; align-items: center; gap: 8px; 
              color: #688078;
              font-size: 10px;
              font-weight: 750;
              letter-spacing: 1.4px;"
            >
              <span class="eye-shadow"></span>
                A clearer view of your money
            </div>

            <br />
            
            <span class="main-text-1">Make your money make </span>
            <span class="main-text-2">sense.</span>
            
            <br />
            <br />
            <br />
            
            <span class="main-text-3">Monetrax brings your accounts, spending, and plans together in one calm, clear place—so you can feel confident about what comes next.</span>
            
            <br />
            <br />
            <br />
            
            <div class="main-button-wrapper">
                <UButton trailing-icon="i-lucide-arrow-right" size="md"  class="get-started">Get Started</UButton>
                <UButton trailing-icon="i-lucide-arrow-down" size="md" variant="outline"  class="explore">Explore Monetrax</UButton>

            </div>
            
            <br />
            
            <span style="margin-top: 24px;
                color: #8a9992;
                font-size: 12px;"
            >
              ✓ Your finances, organized around you
            </span>

          </div>
         
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


<style scoped>

  .main-text {
    max-width: 320px;
    line-height: 1;
  }

  .main-text-1 {
    font-size: clamp(40px, 5vw, 60px);
    font-weight: 700;
    color: #060057;
    font-family:  'Times New Roman', Times, serif;
  }

  .main-text-2 {
    font-size: clamp(40px, 5vw, 60px);
    font-weight: 700;
    color: #a7c6f5;
    font-family: 'Times New Roman', Times, serif;
  }

  .main-text-3 {
    font-size: clamp(14px, 2.5vw, 18px);
    font-weight: 400;
    color: #797979;
    font-family: 'Times New Roman', Times, serif;
    line-height: 1.2;
  }

  .eye-shadow {
   width: 7px;
    height: 7px;
    border-radius: 50%;
    background: #5abce9;
    box-shadow: 0 0 0 4px #abf7f7;
  }

  .main-button-wrapper {
    display: flex;
    flex-direction: row;
    gap: 16px;
  }

  .get-started {
    background-color: #0d319d;;
    color: white;
    padding: 12px 16px;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .get-started:hover {
    transform: translateY(-4px)


  }

  .explore {
    color: #0d319d;
    padding: 12px 16px;
    border-color: transparent;
    cursor: pointer;
  }

</style>