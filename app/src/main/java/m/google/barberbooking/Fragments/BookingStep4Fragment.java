package m.google.barberbooking.Fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import m.google.barberbooking.Common.Common;
import m.google.barberbooking.R;
import m.google.barberbooking.modelo.BookingInformation;

public class BookingStep4Fragment extends Fragment {

    SimpleDateFormat simpleDateFormat;
    LocalBroadcastManager localBroadcastManager;

    AlertDialog dialog;

    Unbinder unbinder;

    @BindView(R.id.IdTxtName_Salon)
    TextView txtNameSalon;

    @BindView(R.id.IdTxtLocation)
    TextView txtLocation;

    @BindView(R.id.IdTxtWebSite)
    TextView txtWebSite;

    @BindView(R.id.IdTxtPhone)
    TextView txtPhone;

    @BindView(R.id.IdTxtOpenTime)
    TextView txtOpenTime;

    @BindView(R.id.IdTxtTimeBooking)
    TextView txtTimeBookingInformation;

    @BindView(R.id.IdTxtNameBooking)
    TextView txtNameBookingInformation;

    @OnClick(R.id.IdBtnConfirmar)
    void confirm() {

        //proccess TimeStamp
        //we will use timestamp to filter  all booking with data is greater today
        //fir only display all future booking

        dialog.show();

        String starTime= Common.convertTimeSlotToString(Common.currentTimeSlot);
        String[] convertTime= starTime.split("-");

        //get Start Time: get 9:00
        String[] startTimeConvert = convertTime[0].split(":");
        int startHourint= Integer.parseInt(startTimeConvert[0].trim());
        int startMinuint= Integer.parseInt(startTimeConvert[1].trim());

        Calendar bookingDateWithThourHouse= Calendar.getInstance();
        bookingDateWithThourHouse.setTimeInMillis(Common.bookingDate.getTimeInMillis());
        bookingDateWithThourHouse.set(Calendar.HOUR_OF_DAY,startHourint);
        bookingDateWithThourHouse.set(Calendar.MINUTE,startMinuint);

        //create timeStamp object and apply to bookingInformation

        Timestamp timestamp= new Timestamp(bookingDateWithThourHouse.getTime());


        //create booking information
        BookingInformation bookingInformation = new BookingInformation();

        bookingInformation.setDone(false);
        bookingInformation.setTimestamp(timestamp);
        bookingInformation.setBarberId(Common.currentBarber.getBarberId());
        bookingInformation.setBarberName(Common.currentBarber.getName());
        bookingInformation.setCustomerName(Common.currentUser.getName());
        bookingInformation.setCustomerPhone(Common.currentUser.getPhone());
        bookingInformation.setSalonId(Common.currentSalon.getSalonId());
        bookingInformation.setSalonAddress(Common.currentSalon.getAddress());
        bookingInformation.setSalonName(Common.currentSalon.getName());
        bookingInformation.setTime(Common.convertTimeSlotToString(Common.currentTimeSlot) +
                "at" +
                simpleDateFormat.format(bookingDateWithThourHouse.getTime()));
        bookingInformation.setSlot((long) Common.currentTimeSlot);

        //submit to barber document

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("AllSalon")
                .document(Common.city)
                .collection("Branch")
                .document(Common.currentSalon.getSalonId())
                .collection("Barber")
                .document(Common.currentBarber.getBarberId())
                .collection(Common.simpleDateFormt.format(Common.bookingDate.getTime()))
                .document(String.valueOf(Common.currentTimeSlot));

        //write data

        documentReference.set(bookingInformation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        //here ew can write an function to check
                            // if already exist an booking, we will prevent new booking

                            addToUserBooking(bookingInformation);


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void addToUserBooking(BookingInformation bookingInformation) {

        //first, create new collections
        CollectionReference userBokking= FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhone())
                .collection("Booking");

        userBokking.whereEqualTo("done", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.getResult().isEmpty())
                        {
                            userBokking.document()
                                    .set(bookingInformation)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            if (dialog.isShowing())
                                                dialog.dismiss();

                                            addToCalendar(Common.bookingDate,Common.convertTimeSlotToString(Common.currentTimeSlot));

                                            resetStaticData();
                                            getActivity().finish();
                                            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (dialog.isShowing())
                                        dialog.dismiss();
                                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                        else
                        {
                            if (dialog.isShowing())
                                dialog.dismiss();

                            resetStaticData();
                            getActivity().finish();
                            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void addToCalendar(Calendar bookingDate, String convertTimeSlotToString) {

        String starTime= Common.convertTimeSlotToString(Common.currentTimeSlot);
        String[] convertTime= starTime.split("-");

        //get Start Time: get 9:00
        String[] startTimeConvert = convertTime[0].split(":");
        int startHourint= Integer.parseInt(startTimeConvert[0].trim());
        int startMinuint= Integer.parseInt(startTimeConvert[1].trim());

        String[] endTimeConvert = convertTime[0].split(":");
        int endHourint= Integer.parseInt(endTimeConvert[0].trim());
        int endMinuint= Integer.parseInt(endTimeConvert[1].trim());

        Calendar starEvent= Calendar.getInstance();
        starEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        starEvent.set(Calendar.HOUR_OF_DAY,startHourint);
        starEvent.set(Calendar.MINUTE,startMinuint);

        Calendar endEvent= Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        endEvent.set(Calendar.HOUR_OF_DAY,endHourint);
        endEvent.set(Calendar.MINUTE,endMinuint);

        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String starEventTime= simpleDateFormat.format(starEvent.getTime());
        String endEventTime= simpleDateFormat.format(endEvent.getTime());

        addToDeviceCalendar(starEventTime, endEventTime, "Hairout Booking", "Haicout from" +
                starTime +
                " with " +
                Common.currentBarber.getName() +
                " at " +
                Common.currentSalon.getName(), "Address: " +
                Common.currentSalon.getAddress());

    }

    private void addToDeviceCalendar(String starEventTime, String endEventTime, String titulo, String descripcion, String location) {

        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy HH:mm");

        try{

            Date start= simpleDateFormat.parse(starEventTime);
            Date end= simpleDateFormat.parse(endEventTime);

            ContentValues contentValues= new ContentValues();

            //put
            contentValues.put(CalendarContract.Events.CALENDAR_ID, getCalendar(getContext()));
            contentValues.put(CalendarContract.Events.TITLE, titulo);
            contentValues.put(CalendarContract.Events.DESCRIPTION, descripcion);
            contentValues.put(CalendarContract.Events.EVENT_LOCATION, location);

            //Time
            contentValues.put(CalendarContract.Events.DTSTART, start.getTime());
            contentValues.put(CalendarContract.Events.DTEND, end.getTime());
            contentValues.put(CalendarContract.Events.ALL_DAY, 0);
            contentValues.put(CalendarContract.Events.HAS_ALARM, 1);

            String timezone= TimeZone.getDefault().getID();
            contentValues.put(CalendarContract.Events.EVENT_TIMEZONE, timezone);

            Uri calendars;

            if (Build.VERSION.SDK_INT >= 8)
                calendars=  Uri.parse("content://com.android.calendar/events");
            else
                calendars=  Uri.parse("content://calendar/events");

            getActivity().getContentResolver().insert(calendars, contentValues);


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private String getCalendar(Context context) {

        String gmailIdCalendar= "";
        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars=  Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver= context.getContentResolver();

        Cursor managedCursor= contentResolver.query(calendars,projection,null,null,null);

        if (managedCursor.moveToFirst())
        {
            String calName;
            int nameCol= managedCursor.getColumnIndex(projection[1]);
            int idCol= managedCursor.getColumnIndex(projection[0]);
            do{
                calName= managedCursor.getString(nameCol);
                if (calName.contains("@gmail.com"))
                {
                    gmailIdCalendar= managedCursor.getString(idCol);
                    break;
                }
            }while (managedCursor.moveToNext());

            managedCursor.close();
        }

        return gmailIdCalendar;

    }


    private void resetStaticData() {
        Common.step = 0;
        Common.currentBarber= null;
        Common.currentSalon= null;
        Common.currentTimeSlot= -1;
        Common.bookingDate.add(Calendar.DATE,0);
    }

    BroadcastReceiver confirmBookingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setData();
        }
    };

    private void setData() {

        txtNameSalon.setText(Common.currentSalon.getName());
        txtTimeBookingInformation.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" at ")
                .append(simpleDateFormat.format(Common.bookingDate.getTime())));

        txtLocation.setText(Common.currentSalon.getAddress());
        txtWebSite.setText(Common.currentSalon.getWebSite());
        txtOpenTime.setText(Common.currentSalon.getOpenHor());
        txtPhone.setText(Common.currentSalon.getPhone());
        txtNameBookingInformation.setText(Common.currentBarber.getName());
    }


    static BookingStep4Fragment instance;

    public static BookingStep4Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep4Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyy");

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(confirmBookingReceiver, new IntentFilter(Common.KEY_CONFIRM_BOOKING));

        dialog= new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_booking_step_four, container, false);

        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(confirmBookingReceiver);
        super.onDestroy();
    }
}
