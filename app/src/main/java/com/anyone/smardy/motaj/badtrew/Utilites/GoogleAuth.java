package com.anyone.smardy.motaj.badtrew.Utilites;

import android.content.Context;

import com.anyone.smardy.motaj.badtrew.R;
import com.anyone.smardy.motaj.badtrew.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class GoogleAuth {

    private static GoogleSignInOptions getGoogleSignInOptions(Context context) {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.ClientID))
                .requestId()
                .build();
    }

    public static GoogleSignInClient getGoogleSignInClient (Context context) {
        return GoogleSignIn.getClient(context, getGoogleSignInOptions(context));
    }

    public static User getCurrentUserFromGoogleAccount(Context context) {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(context);
        User user = new User();
        if (googleSignInAccount!=null) {
            user.setName(googleSignInAccount.getDisplayName());
            if (googleSignInAccount.getPhotoUrl()!=null)
            user.setPhoto_url(googleSignInAccount.getPhotoUrl().toString());
            return user ;
        }
        return null ;
    }
}
