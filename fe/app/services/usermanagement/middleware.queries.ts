import { useUserStore } from "~/store/user";
import { userManagementApi } from ".";

let inProgress: Promise<void | null> | null= null




export function getUserInfo(force: boolean = false):Promise<void | null> | null{
   
    const userStore = useUserStore();
   
   if(!!userStore.userInfo && !force) return Promise.resolve();

   if(!inProgress) {
        inProgress = userManagementApi().api.user().then(
        (res)=>{
            if(!!res && "aiApiKeys" in res){
                userStore.setUserInfo(res)
            }
        }).finally(()=>{
            inProgress=null
        })
   } 



return inProgress;

}
