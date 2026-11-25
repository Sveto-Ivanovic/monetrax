import type { AlertCreate, AlertCreateUpdateDeleteResponse } from "./types";
import { useMutation, useQuery } from "@tanstack/vue-query";
import { FetchError } from "ofetch";
import { toValue, type MaybeRef } from 'vue'


export function accountAlertManagementApi() {

    const { $customFetch } = useNuxtApp()

    const api = {
        createAlert: (req: AlertCreate, account_id: string) => $customFetch<AlertCreateUpdateDeleteResponse>(`/alerts/account/${account_id}/alert/create`, {
            method: 'POST',
            body: req,
            credentials: 'omit'
        }),
        deleteAlert: (alertId: string) => $customFetch<AlertCreateUpdateDeleteResponse>(`/alerts/alert/${alertId}/delete`, {
            method: 'DELETE',
            credentials: 'omit'
        }),
    }

function createAlert() {
    return useMutation<
        AlertCreateUpdateDeleteResponse,               
        FetchError,                                    
        { req: AlertCreate; account_id: string } 
    >({
        mutationKey: ['create', 'alerts'],
        mutationFn: (vars) => api.createAlert(vars.req, vars.account_id),
    })
}

function deleteAlert() {
    return useMutation<
        AlertCreateUpdateDeleteResponse,               
        FetchError,                                    
        { alertId: string } 
    >({
        mutationKey: ['delete', 'alerts'],
        mutationFn: (vars) => api.deleteAlert(vars.alertId),
    })
}

    return {
    createAlert, deleteAlert    }
}