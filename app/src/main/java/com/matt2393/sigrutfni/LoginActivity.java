package com.matt2393.sigrutfni;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.matt2393.sigrutfni.Dialog.DialogProgress;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText user,pass;
    private Button ingresar,cond,univ;
    private DialogProgress progress;
    private CardView conten;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user=findViewById(R.id.user_login);
        pass=findViewById(R.id.pass_login);
        ingresar=findViewById(R.id.sing_in);
        cond=findViewById(R.id.sing_in_cond);
        univ=findViewById(R.id.sing_in_univ);
        conten=findViewById(R.id.conten_login);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singIn();
            }
        });

        cond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conten.setVisibility(View.VISIBLE);
                cond.setVisibility(View.GONE);
                univ.setVisibility(View.GONE);
            }
        });
        univ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,MapsActivity.class));
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}
                    , 1);
        }
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            AlertNoGps();

        progress=new DialogProgress();
        progress.setCancelable(false);

    }
    public void AlertNoGps() {
        AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),222);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        verficarAuth(FirebaseInit.user());

    }

    private void verficarAuth(FirebaseUser user){
        hideProgress();
        if(user!=null){
            startActivity(new Intent(this,MapsActivity.class));
            finish();
        }
    }

    private void singIn(){
        if(!validar())
            return;

        showProgress();

        String userText=user.getText().toString();
        String passText=pass.getText().toString();

        FirebaseInit.getAuth().signInWithEmailAndPassword(userText,passText)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,
                                    "Error, intente denuevo",
                                    Toast.LENGTH_SHORT)
                                    .show();

                        }
                        verficarAuth(FirebaseInit.user());
                    }
                });
    }

    private boolean validar(){
        String userText=user.getText().toString();
        String passText=pass.getText().toString();
        boolean valido=true;
        if(TextUtils.isEmpty(userText)){
            valido=false;
            user.setError("Usuario Requerido");
        }
        else
            user.setError(null);

        if(TextUtils.isEmpty(passText)){
            valido=false;
            pass.setError("Contraseña Requerida");
        }
        else
            pass.setError(null);

        return valido;
    }

    private void showProgress(){
        if(!progress.isAdded())
            progress.show(getSupportFragmentManager(),"ProgressLogin");
    }
    private void hideProgress(){
        if(progress.isAdded())
            progress.dismiss();
    }
}
