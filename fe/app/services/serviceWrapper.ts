import { analyticsManagementApi } from "./analytics";
import { userManagementApi } from "./usermanagement";
import { accountManagementApi } from "./accounts";


export const apiRouter = {
    userApi: userManagementApi,
    analyticsApi: analyticsManagementApi,
    accountApi: accountManagementApi
}