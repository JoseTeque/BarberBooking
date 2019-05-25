package m.google.barberbooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import m.google.barberbooking.Common.Common;
import m.google.barberbooking.Interface.IRecyclerItemSelectedListener;
import m.google.barberbooking.R;
import m.google.barberbooking.modelo.Barber;

public class MyBarberAdapter extends RecyclerView.Adapter<MyBarberAdapter.ViewHolder> {

    private List<Barber> listBarber;
    private Context context;
    private List<CardView> cardViewsList;
    private LocalBroadcastManager localBroadcastManager;

    public MyBarberAdapter(List<Barber> listBarber, Context context) {
        this.listBarber = listBarber;
        this.context = context;
        cardViewsList= new ArrayList<>();
        localBroadcastManager= LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.barber_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.txtnameBarber.setText(listBarber.get(i).getName());
        viewHolder.ratingBar.setRating((float) listBarber.get(i).getRating());

        if (!cardViewsList.contains(viewHolder.cardViewBarber))
            cardViewsList.add(viewHolder.cardViewBarber);


        viewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemListener(View view, int position) {
                //set background for all item not choise

                for (CardView cardView: cardViewsList)
                {
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                }

                //set background for choise
                viewHolder.cardViewBarber.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));

                //send local Broadcast to enable button next
                Intent intent= new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_BARBER_SELECTED,listBarber.get(position));
                intent.putExtra(Common.KEY_STEP,2);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBarber.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.IdTxtBarberName)
        TextView txtnameBarber;

        @BindView(R.id.IdRtingBarber)
        RatingBar ratingBar;

        @BindView(R.id.IdCarviewBarber)
        CardView cardViewBarber;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemListener(v,getAdapterPosition());
        }
    }
}
