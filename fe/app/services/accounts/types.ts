export interface AccountInformation {
  accountId: string
  name: string
  description: string | null
  currentBalance: number
  currency: string
  institutionName: string
  accountNumberMasked: string
  active: boolean
}