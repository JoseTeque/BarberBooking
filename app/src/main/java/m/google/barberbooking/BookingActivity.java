package m.google.barberbooking;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import m.google.barberbooking.Common.Common;
import m.google.barberbooking.Common.NonSwipeViewPage;
import m.google.barberbooking.adapter.ViewPagerAdapter;
import m.google.barberbooking.modelo.Barber;

public class BookingActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    AlertDialog dialog;
    CollectionReference barbeRef;

    @BindView(R.id.IdStepView)
    StepView stepView;

    @BindView(R.id.IdViewPager)
    NonSwipeViewPage viewPager;

    @BindView(R.id.IdBtnPreviusStep)
    Button btnPrevious;

    @BindView(R.id.IdBtnNextStep)
    Button btnNext;

    //event

    @OnClick(R.id.IdBtnPreviusStep)
    void previous() {
        if (Common.step == 3 || Common.step > 0) {
            Common.step--;
            viewPager.setCurrentItem(Common.step);
            if (Common.step < 3)
            {
                btnNext.setEnabled(true);
                setButtomColor();
            }
        }
    }

    @OnClick(R.id.IdBtnNextStep)
    void nextClick() {
        if (Common.step < 3 || Common.step == 0) {
            Common.step++;
            if (Common.step == 1) {
                if (Common.currentSalon != null) {
                    loadBarberBySalon(Common.currentSalon.getSalonId());
                }
            }else if (Common.step == 2)
            {
                if (Common.currentBarber !=null)
                {
                    loadTimeSlotOfBarber(Common.currentBarber.getBarberId());
                }
            }else if (Common.step == 3)
            {
                if (Common.currentTimeSlot != -1)
                {
                    loadConfirmBooking();
                }
            }


            viewPager.setCurrentItem(Common.step);
        }
    }

    private void loadConfirmBooking() {

        Intent intent= new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadTimeSlotOfBarber(String barberId) {
        //send local broadcast to fragment step 3
        Intent intent= new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);
    }

    private void loadBarberBySalon(String salonId) {

        dialog.show();

        //now, select all barbers the salon
         if (!TextUtils.isEmpty(Common.city)) {
             barbeRef = FirebaseFirestore.getInstance().collection("AllSalon")
                     .document(Common.city)
                     .collection("Branch")
                     .document(salonId)
                     .collection("Barber");

             barbeRef.get()
                     .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<QuerySnapshot> task) {
                           if (task.isSuccessful()) {
                               ArrayList<Barber> barbers= new ArrayList<>();
                               for (QueryDocumentSnapshot snapshot : task.getResult())
                               {
                                   Barber barber= snapshot.toObject(Barber.class);
                                   barber.setPassword(""); //Remove password because client app
                                   barber.setBarberId(snapshot.getId());

                                   barbers.add(barber);
                               }

                               //send broadcast to bookingstep2fragment to load recycler
                               Intent intent= new Intent(Common.KEY_BARBER_LOAD_DONE);
                               intent.putParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE,barbers);
                               localBroadcastManager.sendBroadcast(intent);

                               dialog.dismiss();
                           }
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     dialog.dismiss();
                 }
             });
         }
    }

    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int step= intent.getIntExtra(Common.KEY_STEP,0);

            if (step ==1)
                Common.currentSalon = intent.getParcelableExtra(Common.KEY_SALON_STORE);
            else if (step == 2)
                Common.currentBarber = intent.getParcelableExtra(Common.KEY_BARBER_SELECTED);
            else if (step == 3)
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT,-1);


            btnNext.setEnabled(true);
            setButtomColor();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENABLE_BUTTON_NEXT));

        ButterKnife.bind(BookingActivity.this);

        setupStepView();
        setButtomColor();

        //View

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4); //we have 4 fragment so we need keep state of this 4 screen page
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                //show step
                stepView.go(i, true);

                if (i == 0) {
                    btnPrevious.setEnabled(false);
                } else
                    btnPrevious.setEnabled(true);

                btnNext.setEnabled(false);

                setButtomColor();

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setButtomColor() {

        if (btnNext.isEnabled()) {
            btnNext.setBackgroundResource(R.color.colorBtnLogin);
        } else {
            btnNext.setBackgroundResource(android.R.color.darker_gray);
        }

        if (btnPrevious.isEnabled()) {
            btnPrevious.setBackgroundResource(R.color.colorBtnLogin);
        } else {
            btnPrevious.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();

        stepList.add("Salon");
        stepList.add("Barber");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);


    }

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }
}
