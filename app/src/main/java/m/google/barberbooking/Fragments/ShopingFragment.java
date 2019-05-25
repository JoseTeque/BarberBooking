package m.google.barberbooking.Fragments;


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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import m.google.barberbooking.Common.SpaceItemDecoration;
import m.google.barberbooking.Interface.IShoppingLoadListener;
import m.google.barberbooking.R;
import m.google.barberbooking.adapter.ShoppingAdapter;
import m.google.barberbooking.modelo.Shopping;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShopingFragment extends Fragment implements IShoppingLoadListener {

    Unbinder unbinder;

    CollectionReference reference;

    IShoppingLoadListener iShoppingLoadListener;

    @BindView(R.id.Id_ChipGroup)
    ChipGroup chipGroup;

    @BindView(R.id.IdChipWax)
    Chip chip_wax;

    @BindView(R.id.IdChipSpray)
    Chip chip_spray;

    @BindView(R.id.IdChipBodyCare)
    Chip chip_bodyCare;

    @BindView(R.id.IdChipHairCare)
    Chip chip_hairCare;

    @BindView(R.id.IdRecyclerShoping)
    RecyclerView recyclerViewShoping;

    @OnClick(R.id.IdChipWax)
    void chipWaxClick(){
        setSelectedChip(chip_wax);
        loadShopingItems("Wax");
    }

    @OnClick(R.id.IdChipBodyCare)
    void chipBodyCareClick(){
        setSelectedChip(chip_bodyCare);
        loadShopingItems("BodyCare");
    }

    @OnClick(R.id.IdChipHairCare)
    void chipHairCareClick(){
        setSelectedChip(chip_hairCare);
        loadShopingItems("HairCare");
    }

    @OnClick(R.id.IdChipSpray)
    void chipSprayClick(){
        setSelectedChip(chip_spray);
        loadShopingItems("Spray");
    }

    private void loadShopingItems(String itemsMenu) {

        reference= FirebaseFirestore.getInstance()
                .collection("Shopping")
                .document(itemsMenu)
                .collection("items");

        reference.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             iShoppingLoadListener.IShoppingLoadOnFailed(e.getMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful())
                {
                    List<Shopping> list= new ArrayList<>();

                    for (DocumentSnapshot snapshot: task.getResult())
                    {
                        Shopping shopping= snapshot.toObject(Shopping.class);
                        shopping.setId(snapshot.getId());
                        list.add(shopping);
                    }

                    iShoppingLoadListener.IShoppingLoadSucces(list);
                }
            }
        });

    }

    private void setSelectedChip(Chip chip_wax) {

        //set color

        for (int i= 0 ; i<chipGroup.getChildCount(); i++)
        {
            Chip chipItems= (Chip)chipGroup.getChildAt(i);
            if (chipItems.getId() != chip_wax.getId())
            {
                chipItems.setChipBackgroundColorResource(android.R.color.darker_gray);
                chipItems.setTextColor(getResources().getColor(android.R.color.white));
            }
            else
            {
                chipItems.setChipBackgroundColorResource(android.R.color.holo_orange_dark);
                chipItems.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }


    public ShopingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shoping, container, false);

        unbinder= ButterKnife.bind(this,view);

        initView();
        return view;
    }

    private void initView() {
        recyclerViewShoping.setHasFixedSize(true);
        recyclerViewShoping.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerViewShoping.addItemDecoration(new SpaceItemDecoration(8));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iShoppingLoadListener = this;
    }

    @Override
    public void IShoppingLoadSucces(List<Shopping> listShopping) {

        ShoppingAdapter adapter= new ShoppingAdapter(getContext(),listShopping);
        recyclerViewShoping.setAdapter(adapter);
    }

    @Override
    public void IShoppingLoadOnFailed(String message) {
        Toast.makeText(getContext(), message , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        loadShopingItems("Wax");
        loadShopingItems("BodyCare");
        loadShopingItems("HairCare");
        loadShopingItems("Spray");
    }
}
