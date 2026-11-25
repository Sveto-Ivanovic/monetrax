import type {
    ListOfAccountTransactions, TransactionInformationPart, TransactionCreate,
    TransactionCreateUpdateResponse
} from "./types";
import { useMutation, useQuery } from "@tanstack/vue-query";
import { FetchError } from "ofetch";
import { toValue, type MaybeRef } from 'vue'


export function accountTransactionManagementApi() {

    const { $customFetch } = useNuxtApp()

    const api = {
        createTrans: (req: TransactionCreate, account_id: string) => $customFetch<TransactionCreateUpdateResponse>(`/transactions/account/${account_id}/transaction/create`, {
            method: 'POST',
            body: req,
            credentials: 'omit'
        }),
        getAccTransactions: (account_id: string) => $customFetch<ListOfAccountTransactions>(`/transactions/account/${account_id}/fetch`, {
            method: 'GET',
            credentials: 'omit'
        }),
        deleteTransaction: (transactionId: string)=> $customFetch<TransactionCreateUpdateResponse>(`/transactions/transaction/${transactionId}/delete`,{
           method: 'DELETE',
            credentials: 'omit' 
        })
    }

function createTransaction() {
    return useMutation<
        TransactionCreateUpdateResponse,               
        FetchError,                                    
        { req: TransactionCreate; account_id: string } 
    >({
        mutationKey: ['create', 'transaction'],
        mutationFn: (vars) => api.createTrans(vars.req, vars.account_id),
    })
}

    function fetchAccountTransactions(id: MaybeRef<string>) {
        return useQuery<ListOfAccountTransactions, FetchError>({
            queryKey: ['account', 'get', toValue(id), 'transactions'],
            queryFn: () => api.getAccTransactions(toValue(id)),
            enabled: () => !!toValue(id),
        })
    }


    function deleteTransaction() {
        return useMutation<
            TransactionCreateUpdateResponse,
            FetchError,
            { transactionId: string; }
        >({
            mutationKey: ['delete', 'transaction'],
            mutationFn: (vars) => api.deleteTransaction(vars.transactionId)
        })
    }

    return {
    fetchAccountTransactions,
    createTransaction,
    deleteTransaction
    }
}