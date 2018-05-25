package com.example.aj.commenton.UI;

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

import com.example.aj.commenton.R;
import com.example.aj.commenton.network.retrofit.RetrofitInstance;
import com.example.aj.commenton.model.User;
import com.example.aj.commenton.network.service.AndroidAcademyWebService;
import com.example.aj.commenton.util.Constants;
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

    private String mName;
    private String mEmail;
    private String mPassword;
    private String mConfirmPassword;

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

        if (isFormValid() && isNetworkAvailable()){
            showProgress(true);

            hideKeyboardFromFragment(this.getContext(), mRegistrationForm);

            User user = new User(mName,mEmail, mPassword);

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

    private boolean isFormValid(){

        removeErrorMessages();

        mName = mNameView.getText().toString();
        mEmail = mEmailView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        mConfirmPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(!mPassword.equals(mConfirmPassword)){
            mConfirmPasswordLayout.setError(getString(R.string.error_passwords_do_not_match));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if(isPasswordValid(mConfirmPassword)){
            mConfirmPasswordLayout.setError(getString(R.string.error_invalid_password));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

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

        if(TextUtils.isEmpty(mName)){
            mNameLayout.setError(getString(R.string.error_field_required));
            focusView = mNameView;
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
            showMessage(mRegistrationForm, R.string.no_internet_connection);
            return false;
        }

        return true;
    }

    private void navigateToHome() {
        Intent intent = new Intent(
                RegistrationFragment.this.getActivity(),
                AlbumListActivity.class);
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
                Utils.storeValue(getActivity(), Constants.USERNAME,mName);
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
