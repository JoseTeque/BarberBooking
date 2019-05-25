package m.google.barberbooking.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import m.google.barberbooking.Common.Common;
import m.google.barberbooking.Common.SpaceItemDecoration;
import m.google.barberbooking.R;
import m.google.barberbooking.adapter.MyBarberAdapter;
import m.google.barberbooking.modelo.Barber;

public class BookingStep2Fragment extends Fragment {

    Unbinder unbinder;

    LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver barberDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Barber> barberArrayList= intent.getParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE);

            //create adapter late
            MyBarberAdapter adapter= new MyBarberAdapter(barberArrayList, getContext());
            recyclerView_step2.setAdapter(adapter);
        }
    };

    @BindView(R.id.IdRecyclerStep2)
    RecyclerView recyclerView_step2;

    static BookingStep2Fragment instance;

    public static BookingStep2Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep2Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localBroadcastManager= LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(barberDoneReceiver, new IntentFilter(Common.KEY_BARBER_LOAD_DONE));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_booking_step_two, container, false);

        unbinder = ButterKnife.bind(this, view);

        initView();

        return view;
    }

    private void initView() {

        recyclerView_step2.setHasFixedSize(true);
        recyclerView_step2.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView_step2.addItemDecoration(new SpaceItemDecoration(4));

    }

    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(barberDoneReceiver);
        super.onDestroy();
    }
}
