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

export type AdjustmentKind = "DEDUCTION" | "ADDITION";

export type TransactionRecurrenceType = "DAY" | "WEEK" | "MONTH" | "YEAR";

export interface RequestedCategoryInformation {
  categoryId: string;
  name: string;
}

export interface TransactionAdditionalInfoCreate {
  kind: AdjustmentKind;
  label: string;
  amount: number; 
}

export interface TransactionLineItemsCreate {
  productName: string;
  amount: number; 
}

export interface TransactionRecurrenceRule {
  ruleType: TransactionRecurrenceType;
  recurrenceNum: number;
  maxNumOfOccurrencesAllowed?: number | null;
}

export interface TransactionCreate {
  name: string;
  description?: string | null;
  amount: number; 
  currency: string;
  customCreationDate?: string | null; 
  categories: RequestedCategoryInformation[];
  additionalInfo: TransactionAdditionalInfoCreate[];
  lineInformation: TransactionLineItemsCreate[];
  transactionRecurrenceRule?: TransactionRecurrenceRule | null;
}

export interface TransactionCreateUpdateResponse {
  msg: string;
  transactionId: string;
}