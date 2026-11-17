import type { AccountInformation } from "../accounts/types"
import type { AlertInformation } from "../alerts/types"
import type { CategoryInformation, CategoryKind } from "../categories/types"

export interface TransactionInformationPart {
  transactionId: string
  name: string
  description: string | null
  amount: number
  currency: string
  categoryType: CategoryKind
  createdAt: string
  categories: string[]
}

export interface ListOfAccountTransactions {
  account: AccountInformation
  transactions: TransactionInformationPart[]
  categoryInformationList: CategoryInformation[]
  listOfProducts: string[]
  alerts: AlertInformation[]
}