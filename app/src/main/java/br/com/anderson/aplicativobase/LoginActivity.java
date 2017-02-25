package br.com.anderson.aplicativobase;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{



    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    FirebaseAuth auth;
    DatabaseReference database;
    CallbackManager callbackManager;
    private android.widget.ProgressBar loginprogress;
    private EditText email;
    private EditText password;
    private Button emailsigninbutton;
    private Button facebook;
    private Button google;
    private Button registerbutton;
    private android.widget.LinearLayout emailloginform;
    private android.widget.ScrollView loginform;
    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.google = (Button) findViewById(R.id.google);
        this.facebook = (Button) findViewById(R.id.facebook);

        this.loginprogress = (ProgressBar) findViewById(R.id.login_progress);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        FacebookSdk.sdkInitialize(getApplicationContext());
        FBKeyHash.getHash(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
              //  .requestIdToken("1020884024809-ssmag4285av8guocbr1622ijdrlc8s28.apps.googleusercontent.com")
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.registerConnectionCallbacks(this);
        if(!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {

                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmailView.getText().toString();
                final String password = mPasswordView.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Minimo 6 caracteres password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //authenticate user
                showProgress(true);
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    AuthResult authResult = task.getResult();

                                    String username = usernameFromEmail(authResult.getUser().getEmail());

                                    // Write new user
                                    writeNewUser(authResult.getUser().getUid(), username,email,null);

                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    preferences.edit().putString("token",authResult.getUser().getUid()).commit();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                showProgress(false);
                            }
                        });
            }
        });



        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        this.facebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginfacebook();
            }
        });

        this.google.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoogleApiClient.isConnected()){
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }else{
                    Toast.makeText(LoginActivity.this, "Não é possivel logar com o google", Toast.LENGTH_SHORT).show();
                }

            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }



    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name,String email,String token) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setToken(token);

        database.child("users").child(userId).setValue(user);

    }
    // [END basic_write]

    private static final int RC_SIGN_IN = 1;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN && resultCode ==RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                final GoogleSignInAccount acct = result.getSignInAccount();
                // Get account information
               // mFullName = acct.getDisplayName();
               // mEmail = acct.getEmail();
                showProgress(true);
                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    AuthResult authResult = task.getResult();

                                    String username = acct.getDisplayName();
                                    // Write new user
                                    writeNewUser(authResult.getUser().getUid(), username,acct.getEmail(),acct.getIdToken());

                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    preferences.edit().putString("token",authResult.getUser().getUid()).commit();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                showProgress(false);
                            }
                        });
            }else{
                Toast.makeText(this, "Ocorreu um erro ao logar com o Google", Toast.LENGTH_SHORT).show();
            }
        }
        else
        if(callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void loginfacebook(){
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                                JSONObject jsonObject1 = jsonObject;
                                String id = "";
                                String mail = "";
                                String name = "";
                                String username = "";
                                String link = "";
                                String gender = "";
                                String avatar = "";
                                try {
                                    id = jsonObject.getString("id");

                                    mail = id + "@facebook.com";
                                    if (jsonObject1.has("email")) {
                                        mail = jsonObject1.getString("email");
                                    }

                                    username = id;
                                    if (jsonObject1.has("username")) {
                                        username = jsonObject1.getString("username");
                                    }

                                    if (jsonObject1.has("name")) {
                                        name = jsonObject1.getString("name");
                                    }
                                    link = "facebook.com/" + id;
                                    if (jsonObject1.has("link")) {
                                        link = jsonObject1.getString("link");
                                    }

                                    avatar = "https://graph.facebook.com/" + id + "/picture?type=large";
                                    if (jsonObject1.has("avatar")) {
                                        avatar = jsonObject1.getString("avatar");
                                    }


                                    showProgress(true);
                                    AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                                    auth.signInWithCredential(credential)
                                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (!task.isSuccessful()) {
                                                        Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                                                Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        AuthResult authResult = task.getResult();

                                                        String username = usernameFromEmail(authResult.getUser().getEmail());
                                                        // Write new user
                                                        writeNewUser(authResult.getUser().getUid(), username,authResult.getUser().getEmail(),loginResult.getAccessToken().getToken());

                                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                                        preferences.edit().putString("token",authResult.getUser().getUid()).commit();

                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    showProgress(false);
                                                }
                                            });

                                } catch (JSONException e) {
                                    Toast.makeText(getBaseContext(), "Não foi possivel logar com facebook", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                    LoginManager.getInstance().logOut();
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,link,picture");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        LoginManager.getInstance().logOut();
                        Toast.makeText(getBaseContext(), "Não foi possível logar com o Facebook. Por favor, tente novamente.", Toast.LENGTH_LONG).show();
                        Log.e("oncacel", "oncacel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        LoginManager.getInstance().logOut();
                        DefaultDialog  errorDialog = DefaultDialog.newInstance("Erro", "Erro ao logar com o Facebook. Por favor.", "Tentar novamente", "Cancelar", new DefaultDialog.OnDialogButtonClick() {
                            @Override
                            public void onPositiveClick() {
                                loginfacebook();
                            }

                            @Override
                            public void onNegativeClick() {
                                finishAffinity();
                            }
                        });
                        errorDialog.show(LoginActivity.this.getFragmentManager(), null);
                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
        // LoginManager.getInstance().logInWithPublishPermissions(PerfilFragment.this, Arrays.asList("public_profile", "email", "user_about_me"));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}

