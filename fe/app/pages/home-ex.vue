<template>
  <main class="landing-page">

    <header class="site-header">

      <NuxtLink class="brand" to="/" aria-label="Monetrax home">
        <span class="brand-mark" aria-hidden="true"><span></span><span></span><span></span></span>
        <span>monetrax</span>
      </NuxtLink>

      <nav class="desktop-nav" aria-label="Main navigation">
        <a href="#features">Features</a>
        <a href="#how-it-works">How it works</a>
      </nav>

      <NuxtLink class="header-login" to="/login">Log in <span aria-hidden="true">↗</span></NuxtLink>

    </header>

    <section class="hero" aria-labelledby="hero-title">

      <div class="hero-copy">
      
        <div class="eyebrow"><span class="eyebrow-dot"></span> A clearer view of your money</div>
        <h1 id="hero-title">Make your money<br />make <span>sense.</span></h1>

        <p class="hero-description">
          Monetrax brings your accounts, spending, and plans together in one calm, clear place—so you can feel confident about what comes next.
        </p>

        <div class="hero-actions">
          <NuxtLink class="button button-primary" to="/login">Get started <span aria-hidden="true">→</span></NuxtLink>
          <a class="text-link" href="#features">Explore Monetrax <span aria-hidden="true">↓</span></a>
        </div>

        <div class="hero-note"><span class="note-check" aria-hidden="true">✓</span> Your finances, organized around you</div>

      </div>

      <div class="visual-wrap" aria-label="Preview of the Monetrax finance dashboard">

        <div class="visual-orbit orbit-one"></div>
        <div class="visual-orbit orbit-two"></div>

        <div class="dashboard-card">

          <div class="dashboard-topline">
            <div>
              <span class="dashboard-label">TOTAL BALANCE</span>
              <div class="balance">$24,680<span>.50</span></div>
            </div>
            <span class="period-pill">This month <span aria-hidden="true">⌄</span></span>
          </div>

          <div class="balance-caption"><span class="positive">↗ 8.2%</span> <span>compared to last month</span></div>

          <div class="chart" aria-hidden="true">
            <div class="chart-grid"><i></i><i></i><i></i></div>
            <svg viewBox="0 0 520 160" preserveAspectRatio="none" role="presentation">
              <defs>
                <linearGradient id="chart-fill" x1="0" x2="0" y1="0" y2="1">
                  <stop offset="0%" stop-color="#a8ead2" stop-opacity=".48" />
                  <stop offset="100%" stop-color="#a8ead2" stop-opacity="0" />
                </linearGradient>
              </defs>
              <path class="chart-area" d="M0,126 C28,118 43,95 70,103 S117,127 145,89 S191,75 218,92 S262,68 291,77 S330,47 356,64 S396,68 422,36 S466,49 490,26 S510,31 520,15 L520,160 L0,160Z" />
              <path class="chart-line" d="M0,126 C28,118 43,95 70,103 S117,127 145,89 S191,75 218,92 S262,68 291,77 S330,47 356,64 S396,68 422,36 S466,49 490,26 S510,31 520,15" />
              <circle cx="490" cy="26" r="5" />
            </svg>
          </div>

          <div class="chart-months" aria-hidden="true"><span>May</span><span>Jun</span><span>Jul</span><span>Aug</span><span>Sep</span><span>Oct</span></div>
          <div class="dashboard-divider"></div>

          <div class="account-row">
            <span class="account-icon bank-icon">⌂</span>
            <span class="account-name">Everyday account<small>Checking ·•• 4821</small></span>
            <strong>$8,420.00</strong>
          </div>

          <div class="account-row">
            <span class="account-icon savings-icon">↗</span>
            <span class="account-name">Future fund<small>Savings ·•• 1093</small></span>
            <strong>$16,260.50</strong>
          </div>

        </div>

        <div class="floating-card spending-card">

          <span class="spending-icon">↘</span>
          <span><small>Spending this month</small><strong>$1,842.60</strong></span>
          <span class="spending-change">−12%</span>

        </div>
       
       
        <div class="floating-card tip-card"><span class="tip-sparkle">✳</span><span><small>Looking good</small><strong>You’re on track</strong></span></div>
        <div class="decor-star star-one" aria-hidden="true">✳</div>
        <div class="decor-star star-two" aria-hidden="true">✦</div>


      </div>
    </section>

    <section class="trust-strip" aria-label="Monetrax benefits">

      <span>BUILT FOR EVERYDAY CLARITY</span>
      <div><span class="trust-icon">◎</span> All your accounts in one view</div>
      <div><span class="trust-icon">⌁</span> Understand where it goes</div>
      <div><span class="trust-icon">✳</span> Plan with confidence</div>

    </section>

    <section id="features" class="features-section">

      <div class="section-heading">
        <div class="eyebrow">LESS GUESSING, MORE LIVING</div>
        <h2>Everything financial.<br /><span>Finally in one place.</span></h2>
        <p>Money management shouldn’t feel like another job. Monetrax gives you the useful picture, without the noise.</p>
      </div>

      <div class="feature-grid">

        <article class="feature-card feature-mint">
          <div class="feature-icon">⌂</div>
          <h3>See the whole picture</h3>
          <p>Bring your accounts together and get a simple, up-to-date view of your balances.</p>
          <span class="feature-art account-art" aria-hidden="true"><i></i><i></i><i></i></span>
        </article>

        <article class="feature-card feature-lavender">
          <div class="feature-icon">◔</div>
          <h3>Know where it goes</h3>
          <p>Make sense of your spending with categories and clear, helpful analytics.</p>
          <span class="feature-art donut-art" aria-hidden="true"><i></i></span>
        </article>

        <article class="feature-card feature-peach">
          <div class="feature-icon">✳</div>
          <h3>Stay a step ahead</h3>
          <p>Set alerts and recurring transactions to keep the important things on your radar.</p>
          <span class="feature-art alert-art" aria-hidden="true"><i>✓</i><i>↗</i></span>
        </article>

      </div>
    </section>

    <section id="how-it-works" class="closing-cta">
      <div>
        <div class="eyebrow"><span class="eyebrow-dot"></span> YOUR NEXT CHAPTER STARTS HERE</div>
        <h2>Feel good about<br />your money.</h2>
      </div>
      <div class="closing-action">
        <p>A little more clarity can change a lot. Get started with Monetrax today.</p>
        <NuxtLink class="button button-dark" to="/login">Go to your dashboard <span aria-hidden="true">→</span></NuxtLink>
      </div>
    </section>

    <footer class="site-footer">
      <NuxtLink class="brand footer-brand" to="/">
        <span class="brand-mark" aria-hidden="true"><span></span><span></span><span></span></span>
        <span>monetrax</span>
      </NuxtLink>
      <span>More clarity for your money, and more room for everything else.</span>
      <NuxtLink class="footer-login" to="/login">Log in <span aria-hidden="true">↗</span></NuxtLink>
    </footer>

  </main>
</template>

<script setup lang="ts">
declare module 'nuxt/app' {
  interface NuxtLayouts {
    'public': unknown
  }
}

definePageMeta({
  layout: 'public',
})
</script>

<style scoped>

:global(*) { 
  box-sizing: border-box; 
  }
:global(html) { scroll-behavior: smooth; }
:global(body) { margin: 0; background: #f8faf8; color: #172b2b; font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; }
:global(a) { color: inherit; }


.landing-page { overflow: hidden; background: #f8faf8; }
.site-header, .hero, .trust-strip, .features-section, .closing-cta, .site-footer { width: min(1160px, calc(100% - 64px)); margin-inline: auto; }
.site-header { height: 86px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #e8eeea; }
.brand { display: inline-flex; gap: 10px; align-items: center; color: #173c36; text-decoration: none; font-weight: 750; font-size: 21px; letter-spacing: -.8px; }
.brand-mark { width: 27px; height: 27px; display: flex; align-items: end; justify-content: center; gap: 3px; padding: 5px; background: #174e43; border-radius: 8px; }
.brand-mark span { display: block; width: 4px; border-radius: 3px; background: #a8ead2; }
.brand-mark span:nth-child(1) { height: 8px; }.brand-mark span:nth-child(2) { height: 13px; }.brand-mark span:nth-child(3) { height: 10px; }
.desktop-nav { display: flex; gap: 38px; margin-left: 90px; }
.desktop-nav a, .header-login, .footer-login { font-size: 13px; font-weight: 600; text-decoration: none; }
.desktop-nav a { color: #70817b; transition: color .2s; }.desktop-nav a:hover { color: #174e43; }
.header-login { padding: 11px 17px; color: #174e43; border: 1px solid #d9e4de; border-radius: 8px; }.header-login span, .footer-login span { margin-left: 7px; }
.hero { min-height: 600px; display: grid; grid-template-columns: .92fr 1.08fr; align-items: center; gap: 30px; padding-top: 26px; padding-bottom: 38px; }
.hero-copy { position: relative; z-index: 1; padding: 30px 0 42px; }
.eyebrow { display: flex; align-items: center; gap: 9px; color: #688078; font-size: 10px; font-weight: 750; letter-spacing: 1.4px; }
.eyebrow-dot { width: 7px; height: 7px; border-radius: 50%; background: #3daf86; box-shadow: 0 0 0 4px #e0f2e9; }
h1 { margin: 24px 0 18px; color: #183b36; font-size: clamp(52px, 6.4vw, 76px); font-weight: 650; letter-spacing: -5.7px; line-height: .98; }
h1 span { color: #55a98a; }
.hero-description { max-width: 445px; margin: 0; color: #71817b; font-size: 15px; line-height: 1.8; }
.hero-actions { display: flex; align-items: center; gap: 25px; margin-top: 28px; }
.button { display: inline-flex; align-items: center; justify-content: center; gap: 21px; min-height: 48px; padding: 0 20px; border-radius: 8px; text-decoration: none; font-size: 13px; font-weight: 700; transition: transform .2s, box-shadow .2s; }
.button:hover { transform: translateY(-2px); box-shadow: 0 8px 18px #173c3624; }.button-primary { color: white; background: #174e43; }.button-primary span, .button-dark span { font-size: 17px; }
.text-link { color: #526960; font-size: 12px; font-weight: 650; text-decoration: none; }.text-link span { margin-left: 5px; }
.hero-note { display: flex; align-items: center; gap: 8px; margin-top: 24px; color: #8a9992; font-size: 11px; }.note-check { display: grid; place-items: center; width: 17px; height: 17px; color: #338b6d; background: #e6f3eb; border-radius: 50%; font-weight: 800; }
.visual-wrap { position: relative; min-height: 490px; display: grid; place-items: center; isolation: isolate; }
.visual-wrap::before { content: ""; position: absolute; z-index: -2; width: 420px; height: 420px; border-radius: 50%; background: #eaf4ed; }
.visual-orbit { position: absolute; z-index: -1; border: 1px solid #d9e9df; border-radius: 50%; }.orbit-one { width: 480px; height: 480px; }.orbit-two { width: 540px; height: 540px; opacity: .56; }
.dashboard-card { position: relative; width: min(440px, 85%); padding: 25px 27px 16px; background: #fff; border: 1px solid #edf1ed; border-radius: 16px; box-shadow: 0 24px 65px #34574a1a, 0 4px 12px #34574a08; }
.dashboard-topline { display: flex; align-items: start; justify-content: space-between; }.dashboard-label { color: #99a7a0; font-size: 9px; font-weight: 750; letter-spacing: 1.05px; }.balance { margin-top: 6px; color: #1c3834; font-size: 30px; font-weight: 680; letter-spacing: -1.4px; }.balance span { color: #9caaa4; font-size: 19px; }
.period-pill { padding: 7px 9px; color: #718079; background: #f6f8f6; border-radius: 6px; font-size: 9px; }.period-pill span { margin-left: 5px; }
.balance-caption { margin-top: 7px; color: #a0aba5; font-size: 9px; }.positive { color: #369a72; font-weight: 750; margin-right: 5px; }
.chart { position: relative; height: 132px; margin-top: 20px; }.chart-grid { position: absolute; inset: 0; display: flex; flex-direction: column; justify-content: space-between; }.chart-grid i { display: block; border-top: 1px dashed #ecf0ed; }.chart svg { position: absolute; inset: 0; width: 100%; height: 100%; overflow: visible; }.chart-area { fill: url(#chart-fill); }.chart-line { fill: none; stroke: #4caf8a; stroke-width: 3; vector-effect: non-scaling-stroke; }.chart circle { fill: #fff; stroke: #4caf8a; stroke-width: 3; }
.chart-months { display: flex; justify-content: space-between; margin-top: 7px; color: #a0aaa5; font-size: 8px; }.dashboard-divider { height: 1px; margin: 17px 0 8px; background: #eff2ef; }
.account-row { min-height: 47px; display: flex; align-items: center; gap: 10px; }.account-icon { flex: none; width: 28px; height: 28px; display: grid; place-items: center; border-radius: 8px; font-size: 15px; }.bank-icon { color: #568b75; background: #eaf5ee; }.savings-icon { color: #8370aa; background: #f1edfa; }.account-name { flex: 1; color: #43554e; font-size: 10px; font-weight: 650; }.account-name small { display: block; margin-top: 4px; color: #a0aaa5; font-size: 8px; font-weight: 450; }.account-row strong { color: #30453e; font-size: 10px; font-weight: 700; }
.floating-card { position: absolute; display: flex; align-items: center; gap: 10px; padding: 12px 14px; background: #fff; border: 1px solid #eef2ee; border-radius: 11px; box-shadow: 0 13px 32px #34574a17; }.spending-card { top: 77px; right: -2px; }.spending-icon { width: 28px; height: 28px; display: grid; place-items: center; color: #498e70; background: #eaf5ed; border-radius: 8px; font-size: 16px; }.floating-card small { display: block; color: #9aa69f; font-size: 8px; }.floating-card strong { display: block; margin-top: 4px; color: #30453e; font-size: 11px; }.spending-change { align-self: end; padding: 4px 5px; color: #398e6b; background: #edf7f0; border-radius: 4px; font-size: 8px; font-weight: 700; }.tip-card { bottom: 66px; left: -4px; }.tip-sparkle { width: 28px; height: 28px; display: grid; place-items: center; color: #967d45; background: #f8f2df; border-radius: 8px; font-size: 15px; }
.decor-star { position: absolute; color: #65b895; }.star-one { top: 83px; left: 35px; font-size: 21px; }.star-two { right: 38px; bottom: 75px; color: #d5b56d; font-size: 17px; }
.trust-strip { min-height: 80px; display: flex; align-items: center; justify-content: space-between; gap: 20px; border-top: 1px solid #e8eeea; border-bottom: 1px solid #e8eeea; }.trust-strip > span { color: #9ba8a1; font-size: 9px; font-weight: 750; letter-spacing: 1.1px; }.trust-strip > div { display: flex; align-items: center; gap: 8px; color: #61736b; font-size: 11px; font-weight: 600; }.trust-icon { color: #4ba17f; font-size: 17px; }
.features-section { padding-top: 107px; padding-bottom: 112px; }.section-heading { max-width: 540px; margin: 0 auto 44px; text-align: center; }.section-heading .eyebrow { justify-content: center; }.section-heading h2, .closing-cta h2 { margin: 16px 0 14px; color: #1b3c36; font-size: clamp(34px, 4.4vw, 48px); font-weight: 630; letter-spacing: -2.4px; line-height: 1.08; }.section-heading h2 span { color: #69a88d; }.section-heading p { max-width: 420px; margin: 0 auto; color: #788881; font-size: 13px; line-height: 1.7; }
.feature-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 18px; }.feature-card { position: relative; min-height: 250px; overflow: hidden; padding: 23px; border-radius: 12px; }.feature-mint { background: #e9f4ec; }.feature-lavender { background: #f0edf7; }.feature-peach { background: #f8f0e7; }.feature-icon { width: 34px; height: 34px; display: grid; place-items: center; color: #347e62; background: #ffffffa3; border-radius: 9px; font-size: 18px; }.feature-lavender .feature-icon { color: #8070a4; }.feature-peach .feature-icon { color: #a7854b; }.feature-card h3 { margin: 18px 0 8px; color: #29463d; font-size: 15px; letter-spacing: -.3px; }.feature-card p { max-width: 260px; margin: 0; color: #77877f; font-size: 11px; line-height: 1.65; }.feature-art { position: absolute; right: 21px; bottom: -8px; opacity: .58; }.account-art { display: flex; gap: 6px; align-items: end; height: 56px; }.account-art i { width: 25px; border-radius: 5px 5px 0 0; background: #87c0a1; }.account-art i:nth-child(1) { height: 26px; }.account-art i:nth-child(2) { height: 43px; }.account-art i:nth-child(3) { height: 56px; }.donut-art { right: 30px; bottom: -18px; width: 93px; height: 93px; border: 15px solid #b9aed4; border-right-color: #8473ab; border-bottom-color: #d2c9e1; border-radius: 50%; transform: rotate(-30deg); }.donut-art i { position: absolute; top: 28px; left: 28px; width: 7px; height: 7px; background: white; border-radius: 50%; }.alert-art { display: flex; gap: 7px; right: 21px; bottom: 14px; }.alert-art i { display: grid; place-items: center; width: 34px; height: 34px; color: #9e804d; background: #fff9; border: 1px solid #e9dac2; border-radius: 9px; font-size: 15px; font-style: normal; }
.closing-cta { display: flex; align-items: center; justify-content: space-between; gap: 40px; margin-bottom: 76px; padding: 42px 50px; background: #e9f3eb; border-radius: 14px; }.closing-cta h2 { margin-bottom: 0; font-size: 39px; }.closing-action { max-width: 320px; }.closing-action p { margin: 0 0 17px; color: #71837a; font-size: 12px; line-height: 1.7; }.button-dark { color: #fff; background: #174e43; }
.site-footer { min-height: 80px; display: flex; align-items: center; justify-content: space-between; gap: 20px; border-top: 1px solid #e8eeea; }.footer-brand { font-size: 17px; }.footer-brand .brand-mark { width: 23px; height: 23px; border-radius: 7px; }.site-footer > span { color: #93a098; font-size: 10px; }.footer-login { color: #567064; }
@media (max-width: 800px) { .site-header, .hero, .trust-strip, .features-section, .closing-cta, .site-footer { width: min(100% - 40px, 620px); }.hero { grid-template-columns: 1fr; gap: 0; padding-top: 42px; }.hero-copy { padding: 10px 0 0; }.visual-wrap { min-height: 450px; margin-top: 5px; }.trust-strip { flex-wrap: wrap; justify-content: center; padding: 18px 0; }.trust-strip > span { width: 100%; text-align: center; }.trust-strip > div { font-size: 10px; }.features-section { padding-top: 78px; padding-bottom: 80px; }.feature-grid { grid-template-columns: 1fr; }.feature-card { min-height: 220px; }.closing-cta { padding: 34px; }.desktop-nav { margin-left: 0; } }
@media (max-width: 520px) { .site-header, .hero, .trust-strip, .features-section, .closing-cta, .site-footer { width: calc(100% - 36px); }.site-header { height: 72px; }.desktop-nav { display: none; }.header-login { padding: 9px 13px; }.hero { padding-top: 35px; }.hero-copy { padding-top: 0; } h1 { font-size: 54px; letter-spacing: -4px; }.hero-description { font-size: 13px; }.hero-actions { gap: 17px; }.visual-wrap { min-height: 385px; margin-inline: -10px; }.visual-wrap::before { width: 310px; height: 310px; }.orbit-one { width: 355px; height: 355px; }.orbit-two { width: 405px; height: 405px; }.dashboard-card { width: min(390px, 89%); padding: 20px 18px 12px; }.chart { height: 105px; }.balance { font-size: 26px; }.spending-card { top: 38px; right: 0; transform: scale(.86); transform-origin: right center; }.tip-card { bottom: 36px; left: -2px; transform: scale(.88); transform-origin: left center; }.star-one { top: 50px; left: 20px; }.star-two { right: 20px; bottom: 45px; }.trust-strip { gap: 13px; }.trust-strip > div { width: calc(50% - 12px); justify-content: center; }.trust-strip > div:last-child { width: 100%; }.section-heading h2 { font-size: 37px; }.closing-cta { display: block; margin-bottom: 50px; padding: 30px 25px; }.closing-cta h2 { font-size: 35px; }.closing-action { margin-top: 24px; }.site-footer { flex-wrap: wrap; padding: 18px 0; }.site-footer > span { order: 3; flex-basis: 100%; }.site-footer .footer-login { margin-left: auto; } }
@media (prefers-reduced-motion: reduce) { :global(html) { scroll-behavior: auto; }.button { transition: none; } }
</style>
