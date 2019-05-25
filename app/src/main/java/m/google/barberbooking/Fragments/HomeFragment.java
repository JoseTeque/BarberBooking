package m.google.barberbooking.Fragments;


import android.content.Intent;
import android.os.Bundle;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.accountkit.AccountKit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import m.google.barberbooking.BookingActivity;
import m.google.barberbooking.Common.Common;
import m.google.barberbooking.Database.CartDatabase;
import m.google.barberbooking.Database.DatabaseUtils;
import m.google.barberbooking.Interface.IBannerLoadListener;
import m.google.barberbooking.Interface.IBookingInformationListener;
import m.google.barberbooking.Interface.ICountItemInCartListener;
import m.google.barberbooking.Interface.ILookBookLoadListener;
import m.google.barberbooking.R;
import m.google.barberbooking.adapter.HomeAdapter;
import m.google.barberbooking.adapter.LookBookAdapter;
import m.google.barberbooking.modelo.Banner;
import m.google.barberbooking.modelo.BookingInformation;
import m.google.barberbooking.service.PicassoImageLoadingService;
import ss.com.bannerslider.Slider;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements IBannerLoadListener, ILookBookLoadListener, IBookingInformationListener, ICountItemInCartListener {

   private  Unbinder unbinder;

   CartDatabase cartDatabase;

    @BindView(R.id.Id_layout_user_information)
    LinearLayout layout_user_information;

    @BindView(R.id.IdTxtUserName)
    TextView txtUserName;

    @BindView(R.id.IdBannerSlider)
    Slider bannerSlider;

    @BindView(R.id.IdRecycler_Look_Book)
    RecyclerView recyclerView;

    @BindView(R.id.IdCardBookingInfo)
    CardView cardViewInfo;

    @BindView(R.id.IdAddressSalon)
    TextView txtAddressSalon;

    @BindView(R.id.IdTimeSalon)
    TextView txtTimeSalon;

    @BindView(R.id.IdStyleListSalon)
    TextView txtStyleList;

    @BindView(R.id.IdBuillet)
    TextView txtBuillet;

    @BindView(R.id.IdNotificationBadge)
    NotificationBadge notificationBadge;

    @OnClick(R.id.Id_cardview_booking)
     void booking()
    {
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }

    //Firestore
    CollectionReference banneRef, lookRef;

    //interface
    IBannerLoadListener iBannerLoadListener;
    ILookBookLoadListener iLookBookLoadListener;
    IBookingInformationListener iBookingInformationListener;


    public HomeFragment() {
        banneRef= FirebaseFirestore.getInstance().collection("Banner");
        lookRef= FirebaseFirestore.getInstance().collection("LookBook");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
       unbinder=  ButterKnife.bind(this,view);

       cartDatabase= CartDatabase.getInstance(getContext());

        //Init
        Slider.init(new PicassoImageLoadingService());
        iBannerLoadListener= this;
        iLookBookLoadListener=this;
        iBookingInformationListener= this;

        //check is logged?

        if(AccountKit.getCurrentAccessToken() !=null)
        {
            setUserInformation();
            loadBanner();
            loadLookBook();
            loadUserBooking();
            countCartItem();
        }


        return view;
    }

    private void countCartItem() {
        DatabaseUtils.countItemCart(cartDatabase, this);
    }

    private void loadLookBook() {
        lookRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> lookbook= new ArrayList<>();
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot snapshot: task.getResult())
                            {
                                Banner banner= snapshot.toObject(Banner.class);
                                lookbook.add(banner);
                            }
                            iLookBookLoadListener.ILookLoadSucces(lookbook);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iLookBookLoadListener.ILookLoadOnFailed(e.getMessage());
            }
        });

    }

    private void loadBanner() {

        banneRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> banners= new ArrayList<>();
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot snapshot: task.getResult())
                            {
                                Banner banner= snapshot.toObject(Banner.class);
                                banners.add(banner);
                            }
                            iBannerLoadListener.IBannerLoadSucces(banners);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBannerLoadListener.IBannerLoadOnFailed(e.getMessage());
            }
        });
    }

    private void setUserInformation() {
        layout_user_information.setVisibility(View.VISIBLE);
        txtUserName.setText(Common.currentUser.getName());



    }

    @Override
    public void IBannerLoadSucces(List<Banner> banners) {
       bannerSlider.setAdapter(new HomeAdapter(banners));
    }

    @Override
    public void IBannerLoadOnFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void ILookLoadSucces(List<Banner> banners) {
           recyclerView.setHasFixedSize(true);
           recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
           recyclerView.setAdapter(new LookBookAdapter(banners, getActivity()));

    }

    @Override
    public void ILookLoadOnFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
        countCartItem();
    }

    private void loadUserBooking() {

        CollectionReference userBokking= FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhone())
                .collection("Booking");

        Calendar calendar= Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);

        Timestamp todayTimeStamp= new Timestamp(calendar.getTime());

        userBokking.whereGreaterThanOrEqualTo("timestamp",todayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            if (!task.getResult().isEmpty())
                            {

                                for (QueryDocumentSnapshot snapshot: task.getResult())
                                {
                                    BookingInformation bookingInformation= snapshot.toObject(BookingInformation.class);
                                    iBookingInformationListener.IBookingInformationLoadSucces(bookingInformation);
                                    break;
                                }
                            }
                            else
                            {
                                iBookingInformationListener.IBookingInformationEmpty();
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                   iBookingInformationListener.IBookingInformationLoadOnFailed(e.getMessage());
            }
        });

    }

    @Override
    public void IBookingInformationEmpty() {
         cardViewInfo.setVisibility(View.GONE);
    }

    @Override
    public void IBookingInformationLoadSucces(BookingInformation bookingInformation) {

        txtAddressSalon.setText(bookingInformation.getSalonAddress());
        txtStyleList.setText(bookingInformation.getBarberName());
        txtTimeSalon.setText(bookingInformation.getTime());

        String dateRemain= DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookingInformation.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(),0
        ).toString();

        txtBuillet.setText(dateRemain);

        cardViewInfo.setVisibility(View.VISIBLE);

    }

    @Override
    public void IBookingInformationLoadOnFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCartItemCountSucces(int count) {
        notificationBadge.setText(String.valueOf(count));
    }
}
