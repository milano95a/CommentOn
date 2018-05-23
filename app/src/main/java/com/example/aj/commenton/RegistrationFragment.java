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
import com.example.aj.commenton.network.retrofit.model.User;
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
import static com.example.aj.commenton.util.Utils.isPasswordValid;
import static com.example.aj.commenton.util.Utils.showMessage;

public class RegistrationFragment extends Fragment{

    private final String LOG_TAG = RegistrationFragment.class.getName();

    @BindView(R.id.name_layout) TextInputLayout mNameLayout;
    @BindView(R.id.name) TextInputEditText mNameView;
    @BindView(R.id.email_layout) TextInputLayout mEmailLayout;
    @BindView(R.id.email) TextInputEditText mEmailView;
    @BindView(R.id.password_layout) TextInputLayout mPasswordLayout;
    @BindView(R.id.password) TextInputEditText mPasswordView;
    @BindView(R.id.confirm_password_layout) TextInputLayout mConfirmPasswordLayout;
    @BindView(R.id.confirm_password) TextInputEditText mConfirmPasswordView;
    @BindView(R.id.progress_bar) View mProgressView;
    @BindView(R.id.registration_form) View mRegistrationForm;
    @BindView(R.id.login_button) Button mLoginButton;
    @BindView(R.id.registration_button) Button mRegistrationButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration,container,false);
        ButterKnife.bind(this,rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.registration_text);
    }

    @OnClick(R.id.registration_button)
    void attemptRegister(){

        removeErrorMessages();

        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(!password.equals(confirmPassword)){
            mConfirmPasswordLayout.setError(getString(R.string.error_passwords_do_not_match));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if(isPasswordValid(confirmPassword)){
            mConfirmPasswordLayout.setError(getString(R.string.error_invalid_password));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if (isPasswordValid(password)) {
            mPasswordLayout.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailLayout.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailLayout.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(TextUtils.isEmpty(name)){
            mNameLayout.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            hideKeyboardFromFragment(this.getContext(), mRegistrationForm);


            if(!Utils.isNetworkConnected(this.getContext())){
                showProgress(false);
                showMessage(mRegistrationForm, R.string.no_internet_connection);
                return;
            }

            User user = new User(name,email, password);

            AndroidAcademyWebService androidAcademyService = RetrofitInstance
                    .retrofitInstanceWithAndroidAcademy()
                    .create(AndroidAcademyWebService.class);
            Call<Void> registrationCall = androidAcademyService.registration(user);

            RegistrationCallback registrationCallback = new RegistrationCallback();
            registrationCall.enqueue(registrationCallback);
        }
    }

    @OnClick(R.id.login_button)
    void navigateToLogin(){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right)
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();

    }

    private void navigateToHome() {
        Intent intent = new Intent(
                RegistrationFragment.this.getActivity(),
                HomeActivity.class);
        RegistrationFragment.this.getActivity().finish();
        startActivity(intent);
    }

    private void removeErrorMessages() {
        mNameLayout.setError(null);
        mEmailLayout.setError(null);
        mPasswordLayout.setError(null);
        mConfirmPasswordLayout.setError(null);
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mRegistrationForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    class RegistrationCallback implements Callback<Void> {

        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if(response.isSuccessful()){
                Log.wtf(LOG_TAG,"SUCCESS");

                navigateToHome();
            }else{
                Log.wtf(LOG_TAG,"FAILED");

                showProgress(false);

                mEmailLayout.setError(getString(R.string.user_already_registered));
                mEmailView.requestFocus();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            Log.wtf(LOG_TAG,t.getMessage());

            showProgress(false);
            showMessage(mRegistrationForm, R.string.unknown_exception);
        }
    }
}
