package com.andretietz.retroauth.demo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.andretietz.retroauth.AndroidTokenStorage;
import com.andretietz.retroauth.AuthAccountManager;
import com.andretietz.retroauth.BaseAndroidTokenApi;
import com.andretietz.retroauth.BasicAuthenticationHandler;
import com.andretietz.retroauth.ChooseAccountCanceledException;
import com.andretietz.retroauth.Retroauth;
import com.andretietz.retroauth.demo.GithubService.Email;

import java.util.List;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class MainActivity extends AppCompatActivity {

	private GithubService githubService;
	private AuthAccountManager authAccountManager;

	@SuppressWarnings("ConstantConditions")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View buttonRequestEmail = findViewById(R.id.buttonRequestEmail);
		View buttonInvalidateToken = findViewById(R.id.buttonInvalidateToken);
		View buttonResetPrefAccount = findViewById(R.id.buttonResetPrefAccount);
		View buttonAddAccount = findViewById(R.id.buttonAddAccount);


		/**
		 * Optional: create your own OkHttpClient
		 */
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient httpClient = new OkHttpClient.Builder()
				.addInterceptor(interceptor)
				.build();

		/**
		 * Create an instance of the {@link AndroidAuthenticationHandler}
		 */
		BaseAndroidTokenApi tokenApi = new BaseAndroidTokenApi(this) {
			@Override
			public Request modifyRequest(String token, Request request) {
				return request.newBuilder()
						.header("Authorization", "token " + token)
						.build();
			}
		};

		AndroidTokenStorage storage = new AndroidTokenStorage(this);


		BasicAuthenticationHandler authHandler = new BasicAuthenticationHandler<>(
				Executors.newSingleThreadExecutor(),
				tokenApi,
				storage
		);


		/**
		 * Create your Retrofit Object using the {@link Retroauth.Builder}
		 */
		Retrofit retrofit = new Retroauth.Builder<>(authHandler)
				//.methodCache(new AndroidMethodCache()) // optional: using sparsearray instead of hashmap
				.baseUrl("https://api.github.com")
				.client(httpClient)
				.addConverterFactory(MoshiConverterFactory.create())
				.build();

		/**
		 * Create your API Service
		 */
		githubService = retrofit.create(GithubService.class);


		buttonRequestEmail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/**
				 * Use it!
				 */
				githubService.getEmails().enqueue(new Callback<List<Email>>() {
					@Override
					public void onResponse(Call<List<Email>> call, Response<List<Email>> response) {
						if (response.isSuccessful()) {
							showEmails(response.body());
						} else {
							show("Error: " + response.message());
						}
					}

					@Override
					public void onFailure(Call<List<Email>> call, Throwable t) {
						showError(t);
					}
				});
			}
		});

		authAccountManager = new AuthAccountManager(this);
		buttonInvalidateToken.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Account activeAccount = authAccountManager.getActiveAccount(GithubService.ACCOUNT_TYPE, false);

					// override the current token to force a 401
					AccountManager.get(MainActivity.this)
							.setAuthToken(
									authAccountManager.getActiveAccount(GithubService.ACCOUNT_TYPE, false),
									GithubService.TOKEN_TYPE,
									"some-invalid-token"
							);
				} catch (ChooseAccountCanceledException e) {
					e.printStackTrace();
				}

			}
		});

		buttonResetPrefAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				authAccountManager.resetActiveAccount(GithubService.ACCOUNT_TYPE);
			}
		});

		buttonAddAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				authAccountManager.addAccount(MainActivity.this, GithubService.ACCOUNT_TYPE, GithubService.TOKEN_TYPE);
			}
		});
	}

	private void showEmails(List<Email> emailList) {
		StringBuilder sb = new StringBuilder();
		sb.append("Your protected emails:\n");
		for (int i = 0; i < emailList.size(); i++) {
			sb.append(emailList.get(i).email).append('\n');
		}
		show(sb.toString());
	}

	private void show(String toShow) {
		Toast.makeText(this, toShow, Toast.LENGTH_SHORT).show();
	}

	private void showError(Throwable error) {
		error.printStackTrace();
		show(error.toString());
	}
}
