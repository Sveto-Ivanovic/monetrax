import { useUserStore } from "~/store/user";
import type {
    LogInRequest, LogInResponse, CommonErrorResponse,
    UserInformation, UserRegistration, UserInformationDetailed,
    UserUpdate, UpdatePassword, PasswordUpdateResponse
} from "./types";
import { useMutation, useQuery } from "@tanstack/vue-query";

export function userManagementApi() {

    const { $customFetch } = useNuxtApp()

    const api = {
        login: (req: LogInRequest) => $customFetch<LogInResponse | CommonErrorResponse | any>("/auth/login", {
            method: 'POST',
            body: req,
            credentials: 'include'
        }),
        register: (req: UserRegistration) => $customFetch<UserInformation | CommonErrorResponse | any>("/user/create", {
            method: 'POST',
            body: req,
            credentials: 'omit'
        }),
        logout: () => $customFetch<CommonErrorResponse | any>("/auth/token/logout", {
            method: 'POST',
            body: {},
            credentials: 'include'
        }),
        user: () => $customFetch<UserInformationDetailed | CommonErrorResponse | any>("/user/me", {
            method: 'GET',
            credentials: 'omit'
        }),
        updateUser: (req: UserUpdate) => $customFetch<UserInformation | CommonErrorResponse | any>("/user/update", {
            method: 'PATCH',
            body: req,
            credentials: 'omit'
        }),
        updatePassword: (req: UpdatePassword) => $customFetch<PasswordUpdateResponse | CommonErrorResponse | any>("/user/update/password", {
            method: 'PATCH',
            body: req,
            credentials: 'omit'
        })
    }


    function loginUser() {
        const userStore = useUserStore();
        return useMutation({
            mutationKey: ['login'],
            mutationFn: (req: LogInRequest) => api.login(req),
            onSuccess: (data) => {
                userStore.logOutUser()
                if (typeof data === 'object' && data !== null && 'authToken' in data) {
                    userStore.initUser(data);
                }
            }
        })

    }



    function registerUser() {
        return useMutation({
            mutationKey: ['register'],
            mutationFn: (req: UserRegistration) => api.register(req),
        })
    }


    function fetchUserInfo() {
        const userStore = useUserStore();
        const query = useQuery({
            queryKey: ['user_info', 'auth'],
            queryFn: () => api.user()
        });

        watch(query.data, (data) => {
            const response = data
            if (typeof data === 'object' && data !== null && "userName" in data) {
                userStore.setUserInfo(response as UserInformationDetailed);

            }
        });

        return { ...query };
    }



    return {
        api,
        loginUser,
        registerUser,
        fetchUserInfo
    }
}