package m.google.barberbooking;




import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseExceptionMapper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import m.google.barberbooking.Common.Common;
import m.google.barberbooking.Fragments.HomeFragment;
import m.google.barberbooking.Fragments.ShopingFragment;
import m.google.barberbooking.modelo.User;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.IdBottomNavigation)
    BottomNavigationView btnNavigaion;

    BottomSheetDialog bottomSheetDialog;

    AlertDialog dialog;

    CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(HomeActivity.this);

        //Init

        userRef= FirebaseFirestore.getInstance().collection("User");
        dialog= new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        //check intent, if is login = true, enable full access
        //if is login= false, just let user around shoping to view
          if (getIntent() != null)
          {
              boolean isLogin= getIntent().getBooleanExtra(Common.Is_Login, false);
              if (isLogin)
              {
                  dialog.show();
                  //check if user exists

                  AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                      @Override
                      public void onSuccess(final Account account) {

                          if (account!=null)
                          {
                              DocumentReference docRef= userRef.document(account.getPhoneNumber().toString());
                              docRef.get()
                                      .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                          @Override
                                          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                              if (task.isSuccessful())
                                              {
                                                  DocumentSnapshot snapshot= task.getResult();

                                                  if (!snapshot.exists())
                                                  {
                                                      showUpdateDialog(account.getPhoneNumber().toString());
                                                  }else
                                                  {
                                                      //if user already available on our system

                                                      Common.currentUser = snapshot.toObject(User.class);
                                                      btnNavigaion.setSelectedItemId(R.id.IdHome);
                                                  }

                                                  if (dialog.isShowing())
                                                      dialog.dismiss();
                                              }
                                          }
                                      });
                          }
                      }

                      @Override
                      public void onError(AccountKitError accountKitError) {
                          Toast.makeText(HomeActivity.this, ""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                      }
                  });
              }
          }

          //view
            btnNavigaion.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                Fragment fragment= null;

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.IdHome)
                    {
                      fragment= new HomeFragment();

                    }else if(menuItem.getItemId() == R.id.IdCart)
                    {
                        fragment= new ShopingFragment();
                    }

                    return loadFragment(fragment);
                }
            });

    }

    private boolean loadFragment(Fragment fragment) {

        if (fragment!= null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.Id_Frame_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void showUpdateDialog(final String phoneNumber) {

        //Init Dialog

        bottomSheetDialog= new BottomSheetDialog(this);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setTitle("One more step");
        bottomSheetDialog.setCancelable(false);

        View shewView= getLayoutInflater().inflate(R.layout.update_information,null);

        Button btnUpdate = shewView.findViewById(R.id.IdBtnUpdateInformation);
        final TextInputEditText nameUpdate = shewView.findViewById(R.id.IdEdtxName);
        final TextInputEditText AddressUpdate = shewView.findViewById(R.id.IdEdtxAddress);

           btnUpdate.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (dialog.isShowing())
                       dialog.dismiss();

                   final User user= new User(nameUpdate.getText().toString(), AddressUpdate.getText().toString(),phoneNumber);

                   userRef.document(phoneNumber)
                           .set(user)
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   bottomSheetDialog.dismiss();
                                   if (dialog.isShowing())
                                       dialog.dismiss();

                                   Common.currentUser= user;
                                   btnNavigaion.setSelectedItemId(R.id.IdHome);

                                   Toast.makeText(HomeActivity.this, "Thank you", Toast.LENGTH_SHORT).show();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           bottomSheetDialog.dismiss();
                           if (dialog.isShowing())
                               dialog.dismiss();
                           Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
               }
           });

           bottomSheetDialog.setContentView(shewView);
           bottomSheetDialog.show();
    }
}
