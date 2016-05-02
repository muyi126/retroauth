package com.andretietz.retroauth;

import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * The Provider interface is a very specific provider endpoint dependent implementation,
 * to authenticate your request and defines when or if to retry.
 */
public interface Provider<OWNER, TOKEN_TYPE, TOKEN> {

    /**
     * Authenticates a {@link Request}.
     *
     * @param request request to authenticate
     * @param token Token to authenticate
     * @return a modified version of the incoming request, which is authenticated
     */
    Request authenticateRequest(Request request, TOKEN token);

    /**
     * TODO: This method is likely to change in future releases
     * Checks if the retry of an request is required or not. If your provider provides a refresh token
     * mechanism, you can do it in here.
     *
     * @param count value contains how many times this request has been executed already
     * @param retrofit to create your refresh api for refreshing the token
     * @param response response to check what the result was
     * @param tokenStorage storage to delete and store tokens if the have been refreshed
     * @param owner owner of the token used
     * @param type type of the token used
     * @param token token used for the last execution of the request
     * @return {@code true} if a retry is required, {@code false} if not
     */
    boolean retryRequired(int count, Retrofit retrofit, Response response,
                          TokenStorage<OWNER, TOKEN_TYPE, TOKEN> tokenStorage, OWNER owner, TOKEN_TYPE type, TOKEN token);
}