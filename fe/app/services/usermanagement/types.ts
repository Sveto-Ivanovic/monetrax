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

export interface UserRegistration {
  userEmail: string;
  userName: string;
  name: string;
  surname: string;
  dateOfBirth: string; // ISO format: YYYY-MM-DD
  password: string;
}

export interface UserInformationDetailed {
    userInfo: UserInformation;
    aiApiKeys: string[];
}

export interface CommonErrorResponse {
    status: number;
    errors: Error[]; 
}

export interface Error{
    message: string;
    clue: string;
    
}


export interface LogInRequest{
    email: string;
    password: string;
}

export interface UserInformationDetailed{
    userInfo: UserInformation;
    aiApiKeys: string[];
}

export interface UserUpdate {
  userName?: string;
  name?: string;
  surname?: string;
  dateOfBirth?: string;
}

export interface UpdatePassword {
    oldPassword: string;
    newPassword: string;
}

export interface PasswordUpdateResponse {
  successfulPasswordUpdate: boolean;
  message: string;
}