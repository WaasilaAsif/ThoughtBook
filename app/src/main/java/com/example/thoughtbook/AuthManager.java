package com.example.thoughtbook;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
    public interface AuthCallback {
        void onResult(String uid);
    }
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void ensureSignedIn(AuthCallback callback)
    {
        if (auth.getCurrentUser() == null)
        {
            auth.signInAnonymously().addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        callback.onResult(auth.getUid());
                    }
                    else
                    {
                        callback.onResult(null);
                    }
                }
            });

        }else {
        callback.onResult(getUid());
        }
    }

    public String getUid() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
