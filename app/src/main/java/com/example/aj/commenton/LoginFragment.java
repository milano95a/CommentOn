package com.example.aj.commenton;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.aj.commenton.network.retrofit.RetrofitInstance;
import com.example.aj.commenton.network.retrofit.model.Users;
import com.example.aj.commenton.network.retrofit.service.AndroidAcademyWebService;
import com.example.aj.commenton.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.aj.commenton.util.Utils.hideKeyboardFromFragment;
import static com.example.aj.commenton.util.Utils.isEmailValid;
import static com.example.aj.commenton.util.Utils.isNetworkConnected;
import static com.example.aj.commenton.util.Utils.isPasswordValid;
import static com.example.aj.commenton.util.Utils.showMessage;

public class LoginFragment extends Fragment{

    private final String LOG_TAG = LoginFragment.class.getName();

    @BindView(R.id.email_layout) TextInputLayout mEmailLayout;
    @BindView(R.id.email) TextInputEditText mEmailView;
    @BindView(R.id.password_layout) TextInputLayout mPasswordLayout;
    @BindView(R.id.password) TextInputEditText mPasswordView;
    @BindView(R.id.progress_bar) View mProgressView;
    @BindView(R.id.login_form) View mLoginFormView;
    @BindView(R.id.login_button) Button mLoginButton;
    @BindView(R.id.registration_button) Button mRegistrationButton;

    private String mEmail;
    private String mPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login,container,false);
        ButterKnife.bind(this,rootView);

        return rootView;
    }

    @OnClick(R.id.login_button)
    void attemptLogin(){

        if (isFormValid() && isNetworkAvailable()) {
            showProgress(true);

            hideKeyboardFromFragment(this.getContext(), mLoginFormView);

            AndroidAcademyWebService androidAcademyService = RetrofitInstance
                    .retrofitInstanceWithAndroidAcademyWithBasicAuth(
                            mEmailView.getText().toString(),
                            mPasswordView.getText().toString())
                    .create(AndroidAcademyWebService.class);

            Call<Users> loginCall = androidAcademyService.user();

            LoginCallback loginCallback = new LoginCallback();

            loginCall.enqueue(loginCallback);
        }
    }

    @OnClick(R.id.registration_button)
    void navigateToRegistration(){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left)
                .replace(R.id.fragment_container, new RegistrationFragment())
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.login_text);
    }

    private boolean isFormValid(){

        removeErrorMessages();

        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (isPasswordValid(mPassword)) {
            mPasswordLayout.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mEmail)) {
            mEmailLayout.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(mEmail)) {
            mEmailLayout.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;

        } else {
            return true;
        }
    }

    private boolean isNetworkAvailable(){
        if(!Utils.isNetworkConnected(this.getContext())){
            showMessage(mLoginFormView, R.string.no_internet_connection);
            return false;
        }

        return true;
    }

    private void navigateToHome() {
        Intent intent = new Intent(
                LoginFragment.this.getActivity(),
                HomeActivity.class);
        LoginFragment.this.getActivity().finish();
        startActivity(intent);
    }

    private void removeErrorMessages() {
        mEmailLayout.setError(null);
        mPasswordLayout.setError(null);
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    class LoginCallback implements Callback<Users>{
        @Override
        public void onResponse(Call<Users> call, Response<Users> response) {
            if(response.isSuccessful()){
                Log.wtf(LOG_TAG,"SUCCESS");

                navigateToHome();
            }else{
                Log.wtf(LOG_TAG,"FAILED");

                showProgress(false);

                mEmailLayout.setError(getString(R.string.check_your_email));
                mPasswordLayout.setError(getString(R.string.check_your_password));

                mEmailView.requestFocus();
            }
        }

        @Override
        public void onFailure(Call<Users> call, Throwable t) {
            showProgress(false);
            showMessage(mLoginFormView, R.string.unknown_exception);
        }
    }
}
