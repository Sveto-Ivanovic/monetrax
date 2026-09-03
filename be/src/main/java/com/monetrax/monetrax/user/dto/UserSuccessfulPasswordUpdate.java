package com.monetrax.monetrax.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSuccessfulPasswordUpdate {
    boolean successfulPasswordUpdate;
    String message;

    public UserSuccessfulPasswordUpdate(boolean isSuccess){
        this.successfulPasswordUpdate=isSuccess;
        this.message = isSuccess? "Successfully updated the password for the user." : "Failure to update password.";
    }
}
