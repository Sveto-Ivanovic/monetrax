import { useUserStore } from "~/store/user";
import type {
    AccountCreate, AccountInformation,
    FetchAccountsResponse
} from "./types";
import type { CommonErrorResponse } from "../usermanagement/types";
import { useMutation, useQuery } from "@tanstack/vue-query";
import { FetchError } from "ofetch";
import { toValue, type MaybeRef } from 'vue'


export function accountManagementApi() {

    const { $customFetch } = useNuxtApp()

    const api = {
        createAcc: (req: AccountCreate) => $customFetch<AccountInformation>("/accounts/create", {
            method: 'POST',
            body: req,
            credentials: 'omit'
        }),
        fetchAcc: (req: string) => $customFetch<AccountInformation>("/accounts/account/" + req, {
            method: 'GET',
            credentials: 'omit'
        }),
        fetchAccs: (includeArchive: boolean = false) =>
            $customFetch<FetchAccountsResponse>(
                `/accounts/all?includeArchive=${includeArchive}`,
                {
                    method: "GET",
                    credentials: "omit"
                }
            ),
        archiveAcc: (archive: boolean, accountId: string) => $customFetch<AccountInformation>("/accounts/account/"+accountId+"/update/archive", {
             method: 'PATCH',
            body: {archived: archive},
            credentials: 'omit'
        })
    }

    function createAccount() {
        return useMutation<AccountInformation, FetchError, AccountCreate>({
            mutationKey: ['create', 'account'],
            mutationFn: (req: AccountCreate) => api.createAcc(req),
        })
    }

    function archiveAccount() {
        return useMutation<AccountInformation, FetchError, {accountId: string, archive: boolean}>({
            mutationKey: ['archive', 'account'],
            mutationFn: (req: {accountId: string, archive: boolean}) => api.archiveAcc(req.archive, req.accountId),
        })
    }

    function fetchAccount(id: MaybeRef<string>) {
        return useQuery<AccountInformation, FetchError>({
            queryKey: ['account', 'get', id],
            queryFn: () => api.fetchAcc(toValue(id)),
            enabled: () => !!toValue(id),
        })
    }

    function fetchAccounts(toggle: MaybeRef<boolean>) {
        const query = useQuery({
            queryKey: ['accounts', 'get'],
            queryFn: () => api.fetchAccs(toValue(toggle))
        });
        return query;
    }

    return {
        createAccount,
        fetchAccount,
        fetchAccounts,
        archiveAccount
    }
}