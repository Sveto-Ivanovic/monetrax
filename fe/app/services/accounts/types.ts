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

export interface AccountCreate {

    name: string;
    description: string;
    currency: string;
    institutionName: string;
    accountNumberMasked?: string;
    initialBalance?: number;
}


export interface FetchAccountsResponse{
  entityList: AccountInformation[];
  message: string
}