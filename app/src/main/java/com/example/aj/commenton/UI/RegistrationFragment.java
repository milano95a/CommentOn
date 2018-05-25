package com.example.aj.commenton.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import static com.example.aj.commenton.util.Utils.isNetworkConnected;
import static com.example.aj.commenton.util.Utils.isPasswordValid;
import static com.example.aj.commenton.util.Utils.showMessage;

public class RegistrationFragment extends Fragment implements Callback<Void> {

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
    private FragmentActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration,container,false);
        ButterKnife.bind(this,rootView);

        mActivity = this.getActivity();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mActivity.setTitle(R.string.registration_text);
    }

    @OnClick(R.id.registration_button)
    void attemptRegistration(){

        if (isFormValid()){
            if(isNetworkConnected(mActivity)){

                showProgressBar(true);

                hideKeyboard();

                User user = new User(mName,mEmail, mPassword);

                AndroidAcademyWebService webService = RetrofitInstance
                        .retrofitInstanceWithAndroidAcademy()
                        .create(AndroidAcademyWebService.class);
                Call<Void> registrationTask = webService.registration(user);

                registrationTask.enqueue(this);
            }else{
                showMessage(mRegistrationForm, R.string.no_internet_connection);
            }
        }

    }

    @OnClick(R.id.login_button)
    void navigateToLogin(){
        mActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right)
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    @Override
    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
        if(response.isSuccessful()){
            Log.wtf(LOG_TAG,"SUCCESS");
            Utils.storeValue(getActivity(), Constants.USERNAME,mName);
            navigateToHome();
        }else{
            Log.wtf(LOG_TAG,"FAILED");

            showProgressBar(false);

            mEmailLayout.setError(getString(R.string.user_already_registered));
            mEmailView.requestFocus();
        }
    }

    @Override
    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
        Log.wtf(LOG_TAG,t.getMessage());

        showProgressBar(false);
        showMessage(mRegistrationForm, R.string.unknown_exception);
    }

    private void hideKeyboard() {
        if(this.getActivity() != null ) {
            hideKeyboardFromFragment(this.getActivity(), mRegistrationForm);
        }else{
            Log.wtf(LOG_TAG, "getActivity() returns null");
        }
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

    private void navigateToHome() {
        Intent intent = new Intent(
                RegistrationFragment.this.getActivity(),
                AlbumListActivity.class);
        mActivity.finish();
        startActivity(intent);
    }

    private void removeErrorMessages() {
        mNameLayout.setError(null);
        mEmailLayout.setError(null);
        mPasswordLayout.setError(null);
        mConfirmPasswordLayout.setError(null);
    }

    private void showProgressBar(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mRegistrationForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

}
