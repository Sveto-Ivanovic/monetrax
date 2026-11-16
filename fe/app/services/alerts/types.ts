import type { CategoryKind } from "../categories/types"

export type RuleType =
  | 'LESS_OR_EQUAL'
  | 'GREATER_OR_EQUAL'
  | 'BETWEEN'
  | 'EQUAL'
  | 'LESS'
  | 'GREATER'


  export interface AlertConditionInformation {
  conditionId: string
  categoryId: string
  categoryName: string
  ruleType: RuleType
  limitValueLowOrEqual: number | null
  limitValueHigh: number | null
}


export interface AlertState {
  categoryId: string
  categoryName: string
  categoryKind: CategoryKind
  amount: number
  numOfTransactions: number
  avgPerTransaction: number
}

export interface AlertInformation {
  alertId: string
  name: string
  description: string | null
  dateFrom: string
  dateTo: string
  filters: AlertConditionInformation[]
  alertStates: AlertState[]
  breached: boolean
  active: boolean
}