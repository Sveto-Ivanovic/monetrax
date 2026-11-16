<script setup lang="ts">
import type { AlertConditionInformation } from '~/services/alerts/types';


const props = defineProps<{
  condition: AlertConditionInformation
  amount: number
}>()


const low = computed(() => props.condition.limitValueLowOrEqual ?? 0)
const high = computed(() => props.condition.limitValueHigh ?? low.value)

const minimum = computed(() => 
{
    if(props.condition.ruleType==="GREATER_OR_EQUAL" || props.condition.ruleType==="EQUAL" || props.condition.ruleType==='GREATER' || props.condition.ruleType==='BETWEEN'){
        const minValBasedOnPropLow = props.condition.limitValueLowOrEqual ? props.condition.limitValueLowOrEqual * 0.9 : 0
        return Math.min(
            minValBasedOnPropLow,
            props.amount * 0.9
        )
    }
    else{
     const minValBasedOnPropHigh = props.condition.limitValueHigh ? props.condition.limitValueHigh * 0.9 : 0
        return Math.min(
            minValBasedOnPropHigh,
            props.amount * 0.9
        )
    }
})









</script>