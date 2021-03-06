package ru.mail.tp.callbackpal;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import java.lang.ref.WeakReference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.mail.tp.callbackpal.api.models.ValidationCode;
import ru.mail.tp.callbackpal.api.ValidationService;

/**
 * Created by Martin on 25.12.2016.
 */

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;
    private final String mPhone;
    private static WeakReference<LoginActivity> mActivityRef;

    public static void updateActivity(LoginActivity activity) {
        mActivityRef = new WeakReference<LoginActivity>(activity);
    }

    UserLoginTask(String email, String password, String phone) {
        mEmail = email;
        mPassword = password;
        mPhone = phone;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        ValidationService validationService = ValidationService.retrofit.create(ValidationService.class);
        final Call<ValidationCode> call = validationService.requestValidationCode("+7" + mPhone);

        call.enqueue(new Callback<ValidationCode>() {
            @Override
            public void onResponse(Call<ValidationCode> call, Response<ValidationCode> response) {
                Log.d("[Phone Validation]", String.valueOf(response.body().getPin()));
                mActivityRef.get().validationPin = response.body().getPin();
            }

            @Override
            public void onFailure(Call<ValidationCode> call, Throwable t) {
                Log.d("[Phone Validation]", t.getMessage());
                final EditText editText = (EditText) mActivityRef.get().findViewById(R.id.password);
                editText.setText("Something went wrong: " + t.getMessage());
            }
        });

        // TODO: register the new account here.
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        mActivityRef.get().mAuthTask = null;

        if (success) {
            mActivityRef.get().showProgress(false);
//            mActivityRef.get().finish();
        } else {
            mActivityRef.get().mPasswordView.setError(mActivityRef.get().getString(R.string.error_incorrect_password));
            mActivityRef.get().mPasswordView.requestFocus();
        }
    }

    @Override
    protected void onCancelled() {
        mActivityRef.get().mAuthTask = null;
        mActivityRef.get().showProgress(false);
    }
}