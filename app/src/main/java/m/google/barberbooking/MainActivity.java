package m.google.barberbooking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.BasePermissionListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import m.google.barberbooking.Common.Common;

public class MainActivity extends AppCompatActivity {

    private static final int APP_REQUEST_CODE= 7171;

    @BindView(R.id.IdBtnLogin)
    Button btnLogin;

    @BindView(R.id.IdTxtFont)
    TextView txtSkip;

    @OnClick(R.id.IdBtnLogin)
    void loginUser()
    {
        Intent intent= new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder=
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent,APP_REQUEST_CODE);
    }

    @OnClick(R.id.IdTxtFont)
    void SkipLoginGoHome()
    {
        Intent intent= new Intent(MainActivity.this,HomeActivity.class);
         intent.putExtra(Common.Is_Login, false);
         startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dexter.withActivity(this)
                .withPermissions(new String[] {
                                Manifest.permission.WRITE_CALENDAR,
                                Manifest.permission.READ_CALENDAR
                        }).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                AccessToken accessToken = AccountKit.getCurrentAccessToken();

                if (accessToken!=null) //if already logued
                {
                    Intent intent= new Intent(MainActivity.this,HomeActivity.class);
                    intent.putExtra(Common.Is_Login, true);
                    startActivity(intent);
                    finish();

                }else {
                    setContentView(R.layout.activity_main);
                    ButterKnife.bind(MainActivity.this);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }
        }).check();

        printKeyHash();

    }

    private void printKeyHash() {

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info= getPackageManager().getPackageInfo("joseguerra.ordereat",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures)
            {
                MessageDigest md= MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("keyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== APP_REQUEST_CODE)
        {
            AccountKitLoginResult loginResult= data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (loginResult.getError()!=null)
            {
                Toast.makeText(this, " "+ loginResult.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
            }
            else if (loginResult.wasCancelled())
            {
                Toast.makeText(this, "Cancel Login Facebook Account ", Toast.LENGTH_SHORT).show();
            }
            else {

                Intent intent= new Intent(MainActivity.this,HomeActivity.class);
                intent.putExtra(Common.Is_Login, true);
                startActivity(intent);
                finish();
            }
        }
    }
}
