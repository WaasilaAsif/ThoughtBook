package com.example.thoughtbook;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public void ensureSignedIn(OnCompleteListener<AuthResult> callback) {
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously().addOnCompleteListener(callback);
        } else {
            callback.onComplete(null); // already signed in, proceed
        }
    }

    public String getUid() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
