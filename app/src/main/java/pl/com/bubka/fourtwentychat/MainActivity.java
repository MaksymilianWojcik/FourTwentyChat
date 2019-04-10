package pl.com.bubka.fourtwentychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText authCode, phoneNumber;
    Button verifyButton;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        userIsLoggedIn();

        authCode = findViewById(R.id.authCode);
        phoneNumber = findViewById(R.id.phoneNumber);
        verifyButton = findViewById(R.id.verifyButton);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVerificationId != null){
                    verifyPhoneWithCode();
                } else {
                    startVerification();
                }
            }
        });


        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.i("AuthVer", "Completed");
                signInWithPhoneCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.i("AuthVer", "Failed with " + e.toString());
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.i("AuthVer", "Code sent");
                mVerificationId = s;
                verifyButton.setText("Verify code");

            }
        };
    }

    private void verifyPhoneWithCode(){
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, authCode.getText().toString());
        signInWithPhoneCredentials(phoneAuthCredential);
    }


    private void startVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber.getText().toString(), 60, TimeUnit.SECONDS, this, mCallback);
    }

    private void signInWithPhoneCredentials(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    userIsLoggedIn();
                }
            }
        });
    }

    private void userIsLoggedIn() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }


}
