package controle;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socitybusiness.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import controle.cadastros.Activity_Cad_IN;
import controle.cadastros.Activity_ResetSenha;
import controle.cadastros.Activity_Cad_EM;
import controle.firebase.Conexao;
import modelo.Empreendedor;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "lo" ;
    private static final int RC_SIGN_IN = 0;
    private CallbackManager mCallBackManager;
    private Button btnFacebook;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText barsenha;
    private EditText baremail;
    private  FirebaseAuth auth;
    private TextView esqueci;
    private Button btnlogar;
    private Button btnCad;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference ;
    private GoogleSignInClient mGoogleSignInClient;
    public String tipo ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializarComponentes();
        inicializarfirebase();
        FacebookSdk.sdkInitialize(getApplicationContext());
        mAuth =FirebaseAuth.getInstance();
        firebaseauth();
        gerenciadorLogin();
        cliquesdebotao();
        Empreendedor empreendedor = new Empreendedor();
        tipo = empreendedor.getTipo();
        //---------------LOGIN GOOGLE--------------------------------
        findViewById(R.id.sign_in_button).setOnClickListener(cliquegoogle);
        setUpGoogleApiClient();
    }
    private void inicializarComponentes() {
        esqueci = findViewById(R.id.btnEsqueci);
        baremail = findViewById(R.id.barEmailLogin);
        barsenha = findViewById(R.id.barSenhaLogin);
        btnlogar = findViewById(R.id.btnLogarLogin);
        btnCad = findViewById(R.id.btnCadEmail);
        btnFacebook = findViewById(R.id.btnFacebookLogin);
        mCallBackManager = CallbackManager.Factory.create();


    }
    private void cliquesdebotao() {
        btnlogar.setOnClickListener(logar);
        btnCad.setOnClickListener(telacadastro);
        esqueci.setOnClickListener(resetarsenha);
        btnFacebook.setOnClickListener(Loginfb);
    }
    private void inicializarfirebase() {

        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    private void gerenciadorLogin() {
        LoginManager.getInstance().registerCallback(mCallBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthCredential credential = FacebookAuthProvider
                        .getCredential(loginResult.getAccessToken().getToken());
                signInCredential(credential);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
    private void firebaseauth() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    String username = user.getDisplayName();
                    Toast.makeText(MainActivity.this,"Seja Bem Vindo"+username,Toast.LENGTH_LONG).show();
                }
            }
        };
    }
    private void signInCredential(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser usuario = mAuth.getCurrentUser();
                            Intent intent = new Intent(MainActivity.this,Activity_Inicio.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(MainActivity.this,"O login Falhou",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void escolherTipoCad() {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setMessage("Qual o tipo de conta Você deseja?");
        builder.setPositiveButton("Empreendedor", EM);
        builder.setNegativeButton("Investidor", IA);
        alertDialog = builder.create();
        alertDialog.show();
    }
    private void login(String email,String senha) {
        try {
            auth.signInWithEmailAndPassword(email,senha).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(MainActivity.this, Activity_Inicio.class);
                        startActivity(intent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);


                    }else{
                        Toast.makeText(MainActivity.this,"Erro ao efetuar login",Toast.LENGTH_SHORT).show();
                    }



                }
            });
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    View.OnClickListener logar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = baremail.getText().toString().trim();
            String senha = barsenha.getText().toString().trim();
            login(email,senha);
        }
    };


    View.OnClickListener telacadastro = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            escolherTipoCad();

        }};
    View.OnClickListener Loginfb = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email"));
            Intent intent = new Intent(MainActivity.this,Activity_Inicio.class);
            startActivity(intent);
            finish();
        }
    };
    DialogInterface.OnClickListener EM = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intenEM = new Intent(MainActivity.this, Activity_Cad_EM.class);
            startActivity(intenEM);
            tipo="EM";
        }
    };
    DialogInterface.OnClickListener IA = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            Intent intent = new Intent(MainActivity.this, Activity_Cad_IN.class);
            startActivity(intent);
            tipo="IN";
        }
    };
    View.OnClickListener resetarsenha = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, Activity_ResetSenha.class);
            startActivity(intent);
        }
    };



    public void  updateUI(GoogleSignInAccount account){
        if(account != null){
            Toast.makeText(this,"U Signed In successfully",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,Activity_Inicio.class));
        }else {
            Toast.makeText(this,"U Didnt signed in",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        auth = Conexao.getFirebaseAuth();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

    }
    public void setUpGoogleApiClient(){
        //GoogleSignInOptions
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    View.OnClickListener cliquegoogle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sign_in_button:
                    signIn();
                    break;
                // ...
            }
        }
    };
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {

            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(acct);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }}


