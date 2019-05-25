package m.google.barberbooking.Fragments;




import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import m.google.barberbooking.Common.Common;
import m.google.barberbooking.Common.SpaceItemDecoration;
import m.google.barberbooking.Interface.IALLSalonLoadListener;
import m.google.barberbooking.Interface.IBranchLoadListener;
import m.google.barberbooking.R;
import m.google.barberbooking.adapter.MySalonAdapter;
import m.google.barberbooking.modelo.Salon;

public class BookingStep1Fragment extends Fragment implements IALLSalonLoadListener, IBranchLoadListener {

    //Variables
    CollectionReference allSalonRef;
    CollectionReference branchRef;

    IALLSalonLoadListener iallSalonLoadListener;
    IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.IdSpinner)
    MaterialSpinner spinner;

    @BindView(R.id.IdRecyclerViewSalon)
    RecyclerView recyclerView_salon;

    AlertDialog dialog;

    Unbinder unbinder;

    static BookingStep1Fragment instance;

    public static BookingStep1Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep1Fragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iallSalonLoadListener = this;
        iBranchLoadListener = this;
        allSalonRef= FirebaseFirestore.getInstance().collection("AllSalon");

        dialog= new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view= inflater.inflate(R.layout.fragment_booking_step_one, container, false);

        unbinder= ButterKnife.bind(this,view);

        initView();
        loadAllSalon();

        return view;
    }

    private void initView() {
        recyclerView_salon.setHasFixedSize(true);
        recyclerView_salon.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recyclerView_salon.addItemDecoration(new SpaceItemDecoration(4));
    }


    private void loadAllSalon() {

        allSalonRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            List<String> list= new ArrayList<>();
                            list.add("Please choose city");
                            for (QueryDocumentSnapshot snapshot: task.getResult())
                            {
                                list.add(snapshot.getId());
                            }

                            iallSalonLoadListener.IAllSalonLoadSucces(list);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iallSalonLoadListener.IAllSalonLoadOnFailed(e.getMessage());
            }
        });


    }

    @Override
    public void IAllSalonLoadSucces(List<String> list) {
         spinner.setItems(list);
         spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
             @Override
             public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                 if (position > 0)
                 {
                     loadBranchOfCity(item.toString());
                 }
                 else
                 {
                     recyclerView_salon.setVisibility(View.GONE);
                 }
             }
         });
    }

    private void loadBranchOfCity(String cityName) {
      dialog.show();

      Common.city= cityName;

      branchRef= FirebaseFirestore.getInstance().collection("AllSalon")
              .document(cityName)
              .collection("Branch");

        branchRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Salon> list= new ArrayList<>();

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult())
                    {
                        Salon salon= snapshot.toObject(Salon.class);
                        salon.setSalonId(snapshot.getId());
                        list.add(salon);
                    }
                    iBranchLoadListener.IBranchLoadSucces(list);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBranchLoadListener.IBranchLoadOnFailed(e.getMessage());
            }
        });

    }

    @Override
    public void IAllSalonLoadOnFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void IBranchLoadSucces(List<Salon> salonlist) {
        MySalonAdapter adapter= new MySalonAdapter(salonlist, getActivity());
        recyclerView_salon.setAdapter(adapter);
        recyclerView_salon.setVisibility(View.VISIBLE);

        dialog.dismiss();
    }

    @Override
    public void IBranchLoadOnFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
