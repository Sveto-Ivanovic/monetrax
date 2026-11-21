import { analyticsManagementApi } from "./analytics";
import { userManagementApi } from "./usermanagement";
import { accountManagementApi } from "./accounts";
import { accountTransactionManagementApi } from "./transactions";
import { accountAlertManagementApi } from "./alerts";



export const apiRouter = {
    userApi: userManagementApi,
    analyticsApi: analyticsManagementApi,
    accountApi: accountManagementApi,
    transactionApi: accountTransactionManagementApi,
    alertApi: accountAlertManagementApi
}