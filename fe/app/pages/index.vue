<template>
  <div class="page-wrapper">

    <div class="hero-section">
      <div class="hero-subsection">

        <div class="main-text">

          <div style="display: flex; flex-direction: row; align-items: center; gap: 8px; 
              color: #688078;
              font-size: 10px;
              font-weight: 750;
              letter-spacing: 1.4px;">
            <span class="eye-shadow"></span>
            A clearer view of your money
          </div>

          <br />

          <span class="main-text-1">Make your money make </span>
          <span class="main-text-2">sense.</span>

          <br />
          <br />
          <br />

          <span class="main-text-3">Monetrax brings your accounts, spending, and plans together in one calm, clear
            place—so you can feel confident about what comes next.</span>

          <br />
          <br />
          <br />

          <div class="main-button-wrapper">
            <UButton trailing-icon="i-lucide-arrow-right" size="md" class="get-started">Get Started</UButton>
            <UButton trailing-icon="i-lucide-arrow-down" size="md" variant="outline" class="explore">Explore Monetrax
            </UButton>

          </div>

          <br />

          <span style="margin-top: 24px;
                color: #8a9992;
                font-size: 12px;">
            ✓ Your finances, organized around you
          </span>

        </div>


        <div class="graph-example">
          <div class="concentric-circle1"> </div>
          <div class="concentric-circle2"> </div>
          <div class="example-analytic">

            <div class="topline">
              <span class="txt-style-topline"> Total Balance </span>
              <br />
              <span class="txt-style-amount"> € 24,680.50 </span>
            </div>

            <div class="balance-caption">
              <span class="positive">
                ↗ 8.2%
              </span>
              <span>
                compared to last month
              </span>
            </div>

            <div class="chart">
              <div class="grid">
                <i />
                <i />
                <i />
              </div>


              <svg viewBox="0 0 220 160" preserveAspectRatio="none" role="presentation">

                <defs>
                  <linearGradient id="area-fill" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="0%" stop-color="#5eb1fa" stop-opacity="0.35" />
                    <stop offset="100%" stop-color="#5eb1fa" stop-opacity="0" />
                  </linearGradient>
                </defs>

                <polygon fill="url(#area-fill)" stroke="none" points="
                        0,120
                        40,60
                        80,80
                        100,20
                        140,30
                        220,80
                        220,160
                        0,160" />

                <polyline fill="none" stroke="#5eb1fa" stroke-width="2" points="
                      0,120
                      40,60
                      80,80
                      100,20
		                  140, 30
		                  220, 80
			              " />



              </svg>


            </div>

            <div class="chart-months" aria-hidden="true">
              <span>May</span>
              <span>Jun</span>
              <span>Jul</span>
              <span>Aug</span>
              <span>Sep</span>
              <span>Oct</span>
            </div>


            <div class="graph-divider"></div>


            <div class="account-section">
              <span class="account-bank-icon">⌂</span>
              <span class="account-name">
                Everyday account<small>Checking ·•• 4821</small>
              </span>
              <strong>€ 8,420.00</strong>

            </div>
            <div class="account-section">
              <span class="account-bank-icon">⌂</span>
              <span class="account-name">
                Savings account<small>Checking ·•• 9581</small>
              </span>
              <strong>€ 20,620.50</strong>

            </div>

          </div>

        </div>
      </div>

      <div class="trust-section">
        <span>
          Built for everyday clarity
        </span>
        <div>
          ◎ All your accounts in one view
        </div>
        <div>
          ◎ See where your money goes
        </div>
        <div>
          ◎ Plan with confidence
        </div>

      </div>


      <div class="heading-section">
        <span style="font-size: 42px; font-weight: 700; color: #004195;">Your money.</span>
        <span style="font-size: 42px; font-weight: 700; color: #a7c6f2;">Organized in one place.</span>

        <span style="font-size: 18px; font-weight: 700; color:#8a9992; padding-top: 22px;">
          Monetrax is a personal finance app that helps
          you
          manage your money with ease.</span>



      </div>


      <div class="feature-section">
        <div class="feature-item">
          <div class="feature-icon1">
          <UIcon name="i-lucide-pie-chart" size="24" style="color: darkgreen;" />
          </div>
          <span class="feature-title">All your accounts in one view</span>
          <span class="feature-description">Connect your bank accounts, credit cards, and investments to see your
            complete financial picture in one place.</span>
        </div>

        <div class="feature-item">
          <div class="feature-icon2">
            <UIcon name="i-lucide-trending-up" size="24" style="color: darkmagenta;" />
          </div>
          <span class="feature-title">See where your money goes</span>
          <span class="feature-description">Track your spending and categorize your transactions to understand your
            financial habits and make informed decisions.</span>
        </div>

        <div class="feature-item">
            <div class="feature-icon3">
            <UIcon name="i-lucide-calendar" size="24" style="color: darkblue;" />
          </div>
          <span class="feature-title">Plan with confidence</span>
          <span class="feature-description">Set budgets, create savings goals, and plan for the future with our
            intuitive tools that help you stay on track.</span>
        </div>



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
.page-wrapper {
  display: flex;
  flex-direction: column;
  padding-top: 40px;
  padding-bottom: 80px;
}

.hero-section {
  display: flex;
  flex-direction: column;
  gap: 60px;
}

.main-text {
  max-width: 320px;
  line-height: 1;
}

.main-text-1 {
  font-size: clamp(40px, 5vw, 60px);
  font-weight: 700;
  color: #060057;
  font-family: 'Times New Roman', Times, serif;
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
  background-color: #0d319d;
  ;
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

.graph-example {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 16px;
  justify-content: center;
  align-items: center;
  width: 540px;
}


.concentric-circle1 {
  width: 490px;
  height: 490px;
  position: absolute;
  z-index: -1;
  border: 1px solid #95f3f3;
  border-radius: 50%;
}


.concentric-circle2 {
  width: 540px;
  height: 540px;
  position: absolute;
  z-index: -1;
  border: 1px solid #82f3f3;
  border-radius: 50%;
}

.example-analytic {
  width: min(440px, 85%);
  padding: 26px 30px;
  background: #fff;
  border: 1px solid #edf1ed;
  border-radius: 16px;
  box-shadow: 0 24px 65px #34574a1a, 0 4px 12px #34574a08;
  position: relative;
  display: flex;
  flex-direction: column;
}

.topline {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.txt-style-topline {
  color: #99a7a0;
  font-size: 9px;
  font-weight: 750;
  letter-spacing: 1.05px;
}

.txt-style-amount {
  margin-top: 6px;
  color: #1c3834;
  font-size: 30px;
  font-weight: 680;
  letter-spacing: -1.4px;
}

.balance-caption {
  margin-top: 7px;
  color: #a0aba5;
  font-size: 9px;
}


.positive {
  color: #369a72;
  font-weight: 750;
  margin-right: 5px;
}

.chart {
  position: relative;
  height: 150px;
  padding-top: 24px;
}

.chart svg {
  display: block;
  width: 100%;
  height: 100%;
}

.grid {
  position: absolute;
  inset: 0;
  top: 24px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}


.grid i {
  border-top: 1px dashed #ecf0ed;
}

.chart-months {
  display: flex;
  justify-content: space-between;
  margin-top: 7px;
  color: #a0aaa5;
  font-size: 8px;
}

.graph-divider {
  height: 1px;
  margin: 17px 0 8px;
  background: #eff2ef;
}


.account-section {
  display: flex;
  gap: 16px;
  align-items: center;
  padding-top: 16px;

}

.account-bank-icon {
  background-color: #cdd5ed;
  color: #1e1185;
  font-size: 17px;
  padding: 2px 11px;
  border-radius: 8px;
}


.account-name {
  flex: 1;
  color: #004195;
  font-size: 10px;
  font-weight: 650;
}

.account-name small {
  display: block;
  margin-top: 4px;
  color: #a0aaa5;
  font-size: 8px;
  font-weight: 450;
}

.account-section strong {
  color: #004195;
  font-size: 10px;
  font-weight: 700;
}

.hero-subsection {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  gap: 32px;
}

.trust-section {
  margin-top: 80px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  gap: 16px;
  border-top: 1px solid #82acf0;
  border-bottom: 1px solid #7197d4;
  padding: 22px 0;
  align-items: center;
  font-size: 14px;
  font-weight: 650;
  color: #004195;
}

.trust-section span {
  color: #71767c;
  font-size: 12px;
  font-weight: 700;
}

.heading-section {
  margin-top: 80px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  
  text-align: center;
}

.heading-section span {
  max-width: 500px;
  line-height: 1.1;
}

.feature-section {
  padding-top: 20px;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  gap: 32px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 300px;
}

.feature-item:nth-of-type(1) {
  background-color: #a4e1de;
  padding: 24px;
  border-radius: 24px;
}

.feature-item:nth-of-type(2) {
  background-color: #ecb8f7;
  padding: 24px;
  border-radius: 24px;
}

.feature-item:nth-of-type(3) {
  background-color: #b9c7f3;
  padding: 24px;
  border-radius: 24px;
} 

.feature-item:nth-of-type(1) .feature-title {
  font-size: 16px;
  font-weight: 700;
  color: #00521c;
}

.feature-item:nth-of-type(2) .feature-title {
  font-size: 16px;
  font-weight: 700;
  color: #3d2596;
}

.feature-item:nth-of-type(3) .feature-title {
  font-size: 16px;
  font-weight: 700;
  color: #04448d;
}

.feature-description {
  font-size: 14px;
  font-weight: 500;
  color: #565758;
}

.feature-icon1 {
  padding: 8px;
  background-color: #d4f0e9;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.feature-icon2 {
  padding: 8px;
  background-color: #f0d4e9;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}


.feature-icon3 {
  padding: 8px;
  background-color: #d4e9f0;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}

@media (max-width: 1050px) {
  .hero-subsection {
    flex-direction: column;
    align-items: center;
    gap: 64px;
  }

}
</style>