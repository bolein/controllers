package ru.xfit.model.service;

import com.controllers.Task;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Response;
import ru.xfit.model.data.AccessToken;
import ru.xfit.model.data.ErrorException;
import ru.xfit.model.data.ErrorResponse;
import ru.xfit.model.data.User;
import ru.xfit.model.data.UserData;
import ru.xfit.model.data.auth.AuthRequest;
import ru.xfit.model.data.auth.AuthResponse;
import ru.xfit.model.data.club.Class;
import ru.xfit.model.data.club.Club;
import ru.xfit.model.data.phoneConfiramtion.ConfirmationRequest;
import ru.xfit.model.data.phoneConfiramtion.ConfirmationResponse;
import ru.xfit.model.data.register.RegisterRequest;
import ru.xfit.model.retrorequest.TaskBuilder;

import java.util.List;

final class ApiImpl implements Api {

    private final NetworkInterface networkInterface;
    private final UserData userData;

    ApiImpl(NetworkInterface networkInterface, UserData userData) {
        this.networkInterface = networkInterface;
        this.userData = userData;
    }

    private <T> T executeSync(Call<T> call) throws Throwable {
        Response<T> response = call.execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            String json = response.errorBody().string();
            ErrorResponse errorResponse = new Gson().fromJson(json, ErrorResponse.class);

            throw new ErrorException(errorResponse.message);
        }
    }

    @Override
    public Task<AuthResponse> auth(String type, AuthRequest request) {
        return new Task<AuthResponse>() {
            @Override
            public AuthResponse exec() throws Throwable {
                return executeSync(networkInterface.auth(type, request));
            }

            @Override
            public void cancel() {

            }
        };
    }

    @Override
    public Task<AuthResponse> register(RegisterRequest request) {
        return new Task<AuthResponse>() {
            @Override
            public AuthResponse exec() throws Throwable {
                return executeSync(networkInterface.register(request));
            }

            @Override
            public void cancel() {

            }
        };
    }

    @Override
    public Task<ConfirmationResponse> pleaseConfirm(ConfirmationRequest request) {
        return null;
    }

    @Override
    public Task<List<Club>> getClubs() {
        return null;
    }

    @Override
    public Task<List<Class>> getClasses(String id) {
        return null;
    }

    @Override
    public Task<Void> addClass(String id) {
        return null;
    }

    @Override
    public Task<Void> deleteClass(String id) {
        return null;
    }


//    public Task<AccessToken> getAccessToken(String url, String clientId,
//                                            String clientSecret, String code) {
//        return TaskBuilder.from(networkInterface.getAccessToken(url, clientId,
//                clientSecret, code));
//    }
//
//    public Task<User> getCurrentUser() {
//        return TaskBuilder.from(networkInterface.getCurrentUser());
//    }
//
//    public Task<List<User>> getFollowers(String username) {
//        return TaskBuilder.from(networkInterface.getFollowers(username));
//    }
//
//    public Task<List<User>> getFollowing(String username) {
//        return TaskBuilder.from(networkInterface.getFollowing(username));
//    }
//
//    public Task<Object> isFollowing(String username) {
//        return TaskBuilder.from(networkInterface.isFollowing(username));
//    }
//
//    public Task<User> getUser(String username) {
//        return TaskBuilder.from(networkInterface.getUser(username));
//    }
//
//
//    public Task<Object> toggleFollowing(User user, boolean follow) {
//        if (follow) {
//            return TaskBuilder.from(networkInterface.follow(user.login));
//        } else {
//            return TaskBuilder.from(networkInterface.unFollow(user.login));
//        }
//    }
//
//    public Task<User> login(String code) {
//        return TaskBuilder.fromAsync(() -> {
//
//            AccessToken token = getAccessToken(
//                    AccessToken.ACCESS_TOKEN_URL,
//                    AccessToken.CLIENT_ID,
//                    AccessToken.CLIENT_SECRET,
//                    code)
//                    .exec();
//
//            userData.setAccessToken(token);
//
//            return getCurrentUser().exec();
//        });
//    }
}
