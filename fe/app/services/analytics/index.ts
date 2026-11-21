import { useMutation, useQuery } from "@tanstack/vue-query";
import type {
  CommonErrorResponse
} from "../usermanagement/types";
import type { FetchedDashboardAnalyticsData } from "./types";
import { FetchError } from "ofetch";


export function analyticsManagementApi() {

    const { $customFetch } = useNuxtApp()

    const api = {
        dashboard: () => $customFetch<FetchedDashboardAnalyticsData>("/analytics/fetch/dashboard", {
            method: 'GET',
            credentials: 'omit'
        })
    }


  function fetchDashboardAnalytics() {
        return useQuery<FetchedDashboardAnalyticsData, FetchError>({
            queryKey: ['dashboard', 'analytics'],
            queryFn: () => api.dashboard()
        });
    }

    return {
        api,
        fetchDashboardAnalytics
    }

}