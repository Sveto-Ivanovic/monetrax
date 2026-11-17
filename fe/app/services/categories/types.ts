export type CategoryKind =
  | 'INCOME'
  | 'EXPENSE'
  | 'TRANSFER_FROM'
  | 'TRANSFER_TO'
  | 'ADJUSTMENT_PLUS'
  | 'ADJUSTMENT_MINUS'


export interface CategoryInformation {
  categoryId: string
  categoryType: CategoryKind
  name: string
  description: string | null
  defaultCategory: boolean
}