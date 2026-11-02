export interface LogInResponse{
    userId: string;
    authToken: string;
}

export interface UserInformation {
  userId: string; 
  userEmail: string;
  userName: string;
  name: string;
  surname: string;
  role: string;
  dateOfBirth: string;
  hasFinishedOnboarding: boolean;
  hasVerifiedEmail: boolean;
  additionalInfo: string;
}

export interface UserInformationDetailed {
    userInfo: UserInformation;
    aiApiKeys: string[];
}