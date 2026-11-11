import { useMutation, useQuery } from "@tanstack/vue-query";
import type {
  CommonErrorResponse
} from "../usermanagement/types";
import { FetchedDashboardAnalyticsData } from "./types";



export function analyticsManagementApi() {

    const { $customFetch } = useNuxtApp()

    const api = {
        dashboard: () => $customFetch<FetchedDashboardAnalyticsData | CommonErrorResponse | any>("/analytics/fetch/dashboard", {
            method: 'GET',
            credentials: 'omit'
        })
    }


  function fetchDashboardAnalytics() {
        return useQuery({
            queryKey: ['dashboard', 'analytics'],
            queryFn: () => api.dashboard()
        });
    }

    return {
        api,
        fetchDashboardAnalytics
    }

}