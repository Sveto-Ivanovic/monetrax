<script setup lang="ts">
import type { AlertConditionInformation } from '~/services/alerts/types';
import { useElementSize } from '@vueuse/core'

const props = defineProps<{
  condition: AlertConditionInformation,
  amount: number,
  currency: string
}>()


const low = computed(() => props.condition.limitValueLowOrEqual || 0)
const high = computed(() => props.condition.limitValueHigh || 0)

const minimum = computed(() => {
    if(props.condition.ruleType==="GREATER_OR_EQUAL" || props.condition.ruleType==="EQUAL" || props.condition.ruleType==='GREATER' || props.condition.ruleType==='BETWEEN'){
        const minValBasedOnPropLow = props.condition.limitValueLowOrEqual ? props.condition.limitValueLowOrEqual * 0.8 : 0
        return Math.min(
            minValBasedOnPropLow,
            props.amount * 0.8
        )
    }
    else{
     const minValBasedOnPropHigh = props.condition.limitValueHigh ? props.condition.limitValueHigh * 0.8 : 0
        return Math.min(
            minValBasedOnPropHigh,
            props.amount * 0.8
        )
    }
})

const maximum = computed(() => {
    if(props.condition.ruleType==="LESS_OR_EQUAL"  || props.condition.ruleType==='LESS' || props.condition.ruleType==='BETWEEN'){
        const maxValBasedOnPropHigh = props.condition.limitValueHigh ? props.condition.limitValueHigh * 1.2 : 0
        return Math.max(
            maxValBasedOnPropHigh,
            props.amount * 1.2
        )
    }
    else{
     const maxValBasedOnPropLow = props.condition.limitValueLowOrEqual ? props.condition.limitValueLowOrEqual * 1.2 : 0
        return Math.max(
            maxValBasedOnPropLow,
            props.amount * 1.2
        )
    }
})

const mainEl = useTemplateRef('el')
const { width, height } = useElementSize(mainEl)



const divParams = computed(() => {

  if (!width.value) {
    return {
      breached: false,
      toFill: { from: 0, width: 0 },
      bars: []
    }
  }
  const rule = props.condition.ruleType
  const barWidthInPx = 18
  const rawZone = maximum.value - minimum.value
  const zone = rawZone === 0 ? 1 : rawZone
  // where our amount goes
  const amountPx = Math.ceil(width.value * ( props.amount - minimum.value) / zone)
  const amount = props.amount

  const limitCol = 'black'
  if (rule === "GREATER_OR_EQUAL") {

    const lowPx = Math.ceil(width.value * (low.value - minimum.value) / zone)
    const breached = !(amount >= low.value);

    const toReturn = {
      breached: breached,
      toFill: {
        from: 0,
        width: lowPx
      },
      bars: [
        {
          color: limitCol,
          from: lowPx,
          width: 3,
          val: low.value
        },
        {
          color: breached ? 'red' : 'green',
          from: amountPx - Math.floor(barWidthInPx / 2),
          width: barWidthInPx,
          val: amount
        }
      ]
    }
    return toReturn
  }

  else if (rule === "EQUAL") {
    const lowPx = Math.ceil(width.value * (low.value - minimum.value) / zone)
    const breached = !(amount == low.value);

    const toReturn = {
      breached: breached,
      toFill: {
        from: 0,
        width: 0
      },
      bars: [
        {
          color: limitCol,
          from: lowPx,
          width: 3,
          val: low.value
        },
        {
          color: breached ? 'red' : 'green',
          from: amountPx - Math.floor(barWidthInPx / 2),
          width: barWidthInPx,
          val: amount
        }
      ]
    }
    return toReturn
  }

  else if (rule === 'GREATER') {
    const lowPx = Math.ceil(width.value * (low.value - minimum.value) / zone)
    const breached = !(amount > low.value);

    const toReturn = {
      breached: breached,
      toFill: {
        from: 0,
        width: lowPx
      },
      bars: [
            {
          color: limitCol,
          from: lowPx,
          width: 3,
          val: low.value
        },
        {
          color: breached ? 'red' : 'green',
          from: amountPx - Math.floor(barWidthInPx / 2),
          width: barWidthInPx,
          val: amount
        }
      ]
    }
    return toReturn
  }

  else if (rule === 'BETWEEN') {
    const lowPx = Math.ceil(width.value * (low.value - minimum.value) / zone)
    const highPx = Math.ceil(width.value * (high.value - minimum.value) / zone)
    const breached = !(amount >= low.value && amount <= high.value);

    const toReturn = {
      breached: breached,
      toFill: {
        from: lowPx,
        width: highPx - lowPx
      },
      bars: [
        {
          color:limitCol,
          from: lowPx,
          width: 3,
          val: low.value
        },
        {
          color: limitCol,
          from: highPx,
          width: 3,
          val: high.value
        },
        {
          color: breached ? 'red' : 'green',
          from: amountPx - Math.floor(barWidthInPx / 2),
          width: barWidthInPx,
          val: amount
        }
      ]
    }
    return toReturn
  }

  else if (rule === 'LESS') {
    const highPx = Math.ceil(width.value * (high.value - minimum.value) / zone)
    const breached = !(amount < high.value);

    const toReturn = {
      breached: breached,
      toFill: {
        from: highPx,
        width: width.value - highPx
      },
      bars: [
        {
          color: limitCol,
          from: highPx,
          width: 3,
          val: high.value
        },
        {
          color: breached ? 'red' : 'green',
          from: amountPx - Math.floor(barWidthInPx / 2),
          width: barWidthInPx,
          val: amount
        }
      ]
    }
    return toReturn
  }

  else if (rule === 'LESS_OR_EQUAL') {
    const highPx = Math.ceil(width.value * (high.value - minimum.value) / zone)
    const breached = !(amount <= high.value);

    const toReturn = {
      breached: breached,
      toFill: {
        from: highPx,
        width: width.value - highPx
      },
      bars: [
        {
          color: limitCol,
          from: highPx,
          width: 3,
          val: high.value
        },
        {
          color: breached ? 'red' : 'green',
          from: amountPx - Math.floor(barWidthInPx / 2),
          width: barWidthInPx,
          val: amount
        }
      ]
    }
    return toReturn
  }
  else {
    const toReturn = {
      breached: false,
      toFill: {
        from: 0,
        width: 0
      },
      bars: [
        {
          color: 'gray',
          from: amountPx - Math.floor(barWidthInPx / 2),
          width: barWidthInPx,
          val: amount
        }
      ]
    }
    return toReturn
  }

})
</script>


<template>
    <div class="bar-comp">

      <div ref="el" class="parent-bar" :class="props.condition.ruleType ==='BETWEEN' ? 'parent-bar-between' : 'parent-bar-not-between'">
       
        <div class="div-fill-red" v-if="divParams.toFill.width > 0 && props.condition.ruleType !=='BETWEEN' && props.condition.ruleType !=='EQUAL'"
          :style="{ width: `${divParams.toFill.width}px`, left: `${divParams.toFill.from}px` }">
          
        </div>
        <div class="div-fill-gray" v-else-if="props.condition.ruleType ==='EQUAL'"
          :style="{ width: '100%', left: `${divParams.toFill.from}px` }">
          
        </div>
        <div class="div-fill-green" v-else="divParams.toFill.width > 0"
          :style="{ width: `${divParams.toFill.width}px`, left: `${divParams.toFill.from}px` }">
        
        </div>

        <div
            v-for="(item, index) in divParams.bars"
            :key="index"
            :class="item.color !== 'red' && item.color !== 'green' ? 'amount-bar' : 'amount-bar-target'"
            :style="{
                width: item.color !== 'red' && item.color !== 'green'
                    ? '3px'
                    : `${item.width}px`,
                left: `${item.from}px`,
                backgroundColor: item.color
            }"
        >

          <div class="bar-tooltip">
            <div>Amount: {{ item.val }}</div>
            <div>Type: {{ item.color === 'blue' ? 'Limit Value' : 'Current Value' }}</div>
          </div>

        </div>



      </div>


      <div class="bar-comp-footer">
        <span :class="divParams.breached ? 'current-val-breached' : 'current-val-normal'">
          <span v-if="divParams.breached">⚠</span>
          <span v-else style="color:green">✓</span>
          <span class="category-name">
            {{ props.condition.categoryName }}:
          </span>

          <span class="category-amount">
            {{ props.amount }}
          </span>
        </span>

        <span class="condition-info">
          Condition:
          <span class="condition-rule"> {{ props.condition.ruleType.toLowerCase().replaceAll('_', ' ') }}</span>
        </span>
      </div>

    </div>
</template>


<style scoped>
.bar-comp {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

}


.parent-bar {
  position: relative;
  border-radius: 8px;
  width: 90%;
  min-height: 10px;
}



.parent-bar-not-between {
  border: 1px solid #d0d0d0;
  background-color: #e6f4ea;
  
}

.parent-bar-between {
  border: 1px solid #d0d0d0;
  background-color: #fde8e8;
}

.amount-bar {
  height: 25px;
  position: absolute;
  top: -7px;


}


.amount-bar-target {
  height: 18px;
  position: absolute;
  top: -5px;
  border-radius: 50%;
  border: 2px solid #fff;
    box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.25);
    opacity: 0.9;
}
.amount-bar:hover {
  box-shadow: 0 0 0 1px black;
  cursor: pointer;
  transition: box-shadow 0.3s ease;
}

.amount-bar-target:hover{
    box-shadow: 0 0 0 1px black;
  cursor: pointer;
  transition: box-shadow 0.3s ease;
}


.bar-tooltip {
  display: none;
  position: absolute;
  bottom: calc(100% + 10px);
  left: 50%;
  transform: translateX(-50%);

  background: #222;
  color: white;
  padding: 8px 12px;
  border-radius: 6px;

  white-space: nowrap;
  font-size: 12px;
  z-index: 999;
  pointer-events: none;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.2);
}

.amount-bar:hover .bar-tooltip {
  display: block;
}

.amount-bar-target:hover .bar-tooltip {
  display: block;
}

.bar-comp-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  font-size: 13px;
  width: 90%;
}


.current-val-normal,
.current-val-breached {
    display: flex;
    flex-wrap: wrap;
    align-items: baseline;
    gap: 4px;
    min-width: 0;
    overflow-wrap: anywhere;
    font-weight: 600;
}

.current-val-normal {
    color: #333;
}

.current-val-breached {
    color: #dc2626;
}

.category-name,
.category-amount {
    min-width: 0;
    overflow-wrap: anywhere;
}




.condition-info {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #777;
  font-size: 12px;
  min-width: 180px;
  justify-content: end;
}

.condition-rule {
  color: #444;
  font-weight: 600;
  text-transform: capitalize;
  background-color: lightgoldenrodyellow;
  padding: 2px 5px;
  border: 1px solid brown;
  border-radius: 12px;
}


.div-fill-red{
    height: 10px;
  border-radius: 8px;
  position: absolute;
    background-color:#fde8e8;
}


.div-fill-green{
    height: 10px;
  border-radius: 8px;
  position: absolute;
    background-color: #e6f4ea;
}

.div-fill-gray{
      height: 10px;
  border-radius: 8px;
  position: absolute;
  background-color: #e6e6e6;
}

</style>