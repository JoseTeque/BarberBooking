package m.google.barberbooking.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;
import m.google.barberbooking.Common.Common;
import m.google.barberbooking.Common.SpaceItemDecoration;
import m.google.barberbooking.Interface.ITimeSlotLoadListener;
import m.google.barberbooking.R;
import m.google.barberbooking.adapter.MyTimeSlotAdapter;
import m.google.barberbooking.modelo.TimeSlot;

public class BookingStep3Fragment extends Fragment implements ITimeSlotLoadListener {

    //variables
    DocumentReference reference;

    ITimeSlotLoadListener iTimeSlotLoadListener;

    Unbinder unbinder;

    AlertDialog dialog;

    LocalBroadcastManager localBroadcastManager;

    @BindView(R.id.IdRecyclerTimeSlot)
    RecyclerView recyclerView_time;

    @BindView(R.id.IdCalendarHorizontal)
    HorizontalCalendarView horizontalCalendarView;


    SimpleDateFormat simpleDateFormat;

    BroadcastReceiver displayTimeSlot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             Calendar date= Calendar.getInstance();
             date.add(Calendar.DATE,0);
             loadAvailavleTimeSlotOfBarber(Common.currentBarber.getBarberId(),simpleDateFormat.format(date.getTime()));
        }
    };

    private void loadAvailavleTimeSlotOfBarber(final String barberId, final String bookdate) {

        dialog.show();

        ///AllSalon/NewYork/Branch/3bN8VwhyN7sUdPhOfDRK/Barber/dSBfGRTUxU6lsgoHJqE2
        reference = FirebaseFirestore.getInstance().collection("AllSalon")
                .document(Common.city)
                .collection("Branch")
                .document(Common.currentSalon.getSalonId())
                .collection("Barber")
                .document(barberId);

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful())
                {
                    DocumentSnapshot snapshot= task.getResult();
                    if (snapshot.exists())
                    {
                        CollectionReference date= FirebaseFirestore.getInstance().collection("AllSalon")
                                .document(Common.city)
                                .collection("Branch")
                                .document(Common.currentSalon.getSalonId())
                                .collection("Barber")
                                .document(barberId)
                                .collection(bookdate);

                        date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful())
                                {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot.isEmpty())
                                    {
                                        iTimeSlotLoadListener.ITimeSlotLoadEmpty();
                                    }
                                    else {

                                        List<TimeSlot> timeSlots= new ArrayList<>();
                                        for (QueryDocumentSnapshot documentSnapshot: task.getResult())
                                            timeSlots.add(documentSnapshot.toObject(TimeSlot.class));
                                        iTimeSlotLoadListener.ITimeSlotLoadSucces(timeSlots);
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                iTimeSlotLoadListener.ITimeSlotLoadOnFailed(e.getMessage());
                            }
                        });
                    }
                }
            }
        });

    }

    static BookingStep3Fragment instance;

    public static BookingStep3Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep3Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iTimeSlotLoadListener = this;

        localBroadcastManager= LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(displayTimeSlot,new IntentFilter(Common.KEY_DISPLAY_TIME_SLOT));

        simpleDateFormat= new SimpleDateFormat("dd_MM_yyyy");

        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_booking_step_three, container, false);

        unbinder = ButterKnife.bind(this, view);

         InitView(view);
        return view;
    }

    private void InitView(View view) {

        recyclerView_time.setHasFixedSize(true);
        recyclerView_time.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView_time.addItemDecoration(new SpaceItemDecoration(8));

        Calendar starCalendar= Calendar.getInstance();
        starCalendar.add(Calendar.DATE,0);
        Calendar endCalendar= Calendar.getInstance();
        endCalendar.add(Calendar.DATE,2);

        HorizontalCalendar horizontalCalendarView= new HorizontalCalendar.Builder(view,R.id.IdCalendarHorizontal)
                .range(starCalendar,endCalendar)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(starCalendar)
                .build();

        horizontalCalendarView.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

                if (Common.bookingDate.getTimeInMillis() != date.getTimeInMillis())
                {
                   Common.bookingDate = date;
                    loadAvailavleTimeSlotOfBarber(Common.currentBarber.getBarberId(), simpleDateFormat.format(date.getTime()));
                }
            }
        });

    }

    @Override
    public void ITimeSlotLoadSucces(List<TimeSlot> listTime) {
        MyTimeSlotAdapter adapter= new MyTimeSlotAdapter(getContext(), listTime);
        recyclerView_time.setAdapter(adapter);
        dialog.dismiss();
    }

    @Override
    public void ITimeSlotLoadOnFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    @Override
    public void ITimeSlotLoadEmpty() {
        MyTimeSlotAdapter adapter= new MyTimeSlotAdapter(getContext());
        recyclerView_time.setAdapter(adapter);
        dialog.dismiss();
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(displayTimeSlot);
        super.onDestroy();
    }
}
