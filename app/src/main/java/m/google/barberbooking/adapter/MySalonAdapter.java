package m.google.barberbooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import m.google.barberbooking.modelo.Salon;

public class MySalonAdapter extends RecyclerView.Adapter<MySalonAdapter.ViewHolder> {

    private List<Salon> listSalon;
    private Context context;
    private List<CardView> cardViewsList;
    private LocalBroadcastManager localBroadcastManager;

    public MySalonAdapter(List<Salon> listSalon, Context context) {
        this.listSalon = listSalon;
        this.context = context;
        cardViewsList= new ArrayList<>();
        localBroadcastManager= LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(context).inflate(R.layout.salon_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final Salon salon= listSalon.get(i);

        viewHolder.txtnameSalon.setText(salon.getName());
        viewHolder.txtaddressSalon.setText(salon.getAddress());

        if (!cardViewsList.contains(viewHolder.cardView))
            cardViewsList.add(viewHolder.cardView);

        viewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemListener(View view, int position) {
                //set white background for all card not be selected

                for (CardView cardView: cardViewsList)
                {
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                }

                //set selected BG for only selected item
                viewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));

                //send Broadcast to tell booking activity enable button next
                Intent intent= new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_SALON_STORE,salon);
                intent.putExtra(Common.KEY_STEP,1);
                localBroadcastManager.sendBroadcast(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return listSalon.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.IdTxtNameSalon)
        TextView txtnameSalon;

        @BindView(R.id.IdTxtAddresSalon)
        TextView txtaddressSalon;

        @BindView(R.id.IdCarviewSalon)
        CardView cardView;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemListener(v,getAdapterPosition());
        }
    }
}
