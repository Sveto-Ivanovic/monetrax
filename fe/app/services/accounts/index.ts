import { useUserStore } from "~/store/user";
import type {
    AccountCreate, AccountInformation
} from "./types";
import type { CommonErrorResponse } from "../usermanagement/types";
import { useMutation, useQuery } from "@tanstack/vue-query";

export function accountManagementApi() {

    const { $customFetch } = useNuxtApp()

    const api = {
        createAcc: (req: AccountCreate) => $customFetch<AccountInformation | CommonErrorResponse | any>("/accounts/create", {
            method: 'POST',
            body: req,
            credentials: 'omit'
        }),
        fetchAcc: (req: string) => $customFetch<AccountInformation | CommonErrorResponse | any>("/accounts/account/" + req, {
            method: 'GET',
            credentials: 'omit'
        }),
        fetchAccs: (includeArchive: boolean = false) =>
            $customFetch<AccountInformation[] | CommonErrorResponse | any>(
                `/accounts/all?includeArchive=${includeArchive}`,
                {
                    method: "GET",
                    credentials: "omit"
                }
            )
    }

    function createAccount() {
        return useMutation({
            mutationKey: ['create', 'account'],
            mutationFn: (req: AccountCreate) => api.createAcc(req),
        })
    }


    function fetchAccount() {
        const query = useQuery({
            queryKey: ['account', 'get'],
            queryFn: (req: string) => api.fetchAcc(req)
        });
        return { ...query };
    }

    function fetchAccounts() {
        const query = useQuery({
            queryKey: ['accounts', 'get'],
            queryFn: (includeArchive: boolean) => api.fetchAccs(includeArchive)
        });
        return { ...query };
    }

    return {
        createAccount,
        fetchAccount,
        fetchAccounts
    }
}