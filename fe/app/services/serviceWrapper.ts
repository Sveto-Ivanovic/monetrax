import { analyticsManagementApi } from "./analytics";
import { userManagementApi } from "./usermanagement";



export const apiRouter = {
    userApi: userManagementApi,
    analyticsApi: analyticsManagementApi
}