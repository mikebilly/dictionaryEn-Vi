package com.example.dictionaryenvi.Account;
import com.backend.Connection.UserDataAccess;
import com.backend.User.User;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class CheckExistingUserService extends Service<Boolean> {
    private User user;
    private UserDataAccess userDataAccess;
    private String usernameStr;
    public CheckExistingUserService(User user , UserDataAccess userDataAccess , String usernameStr) {
        this.user = user;
        this.userDataAccess = userDataAccess;
        this.usernameStr = usernameStr;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                boolean check = userDataAccess.isExistingUser(usernameStr);
                if(check)   userDataAccess.updateAccount(user);
                return check;
            }
        };
    }
}
