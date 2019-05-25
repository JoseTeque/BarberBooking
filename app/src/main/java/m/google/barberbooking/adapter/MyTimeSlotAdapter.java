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
import m.google.barberbooking.modelo.TimeSlot;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.ViewHolder> {

    private Context context;
    private List<TimeSlot> listTime;
    private List<CardView> cardViewsList;
    private LocalBroadcastManager localBroadcastManager;


    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        listTime = new ArrayList<>();
        cardViewsList= new ArrayList<>();
        localBroadcastManager= LocalBroadcastManager.getInstance(context);

    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> listTime) {
        this.context = context;
        this.listTime = listTime;
        cardViewsList= new ArrayList<>();
        localBroadcastManager= LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(context).inflate(R.layout.time_layout_slot,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.txtTimeSlot.setText(new StringBuilder(Common.convertTimeSlotToString(i)).toString());

        if (listTime.size() == 0)
        {
            viewHolder.txtTimeDescripcion.setText("Available");
            viewHolder.txtTimeDescripcion.setTextColor(context.getResources().getColor(android.R.color.black));
            viewHolder.txtTimeSlot.setTextColor(context.getResources().getColor(android.R.color.black));
            viewHolder.cardViewtime.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
        }
        else
        {
            for (TimeSlot slotValue: listTime)
            {
                int slot= Integer.parseInt(slotValue.getSlot().toString());
                if (slot == i)
                {

                    viewHolder.txtTimeSlot.setTag(Common.DISABLE_TAG);
                    viewHolder.cardViewtime.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                    viewHolder.txtTimeDescripcion.setTextColor(context.getResources().getColor(android.R.color.white));
                    viewHolder.txtTimeSlot.setTextColor(context.getResources().getColor(android.R.color.white));
                    viewHolder.txtTimeDescripcion.setText("full");
                }
            }
        }

        if (!cardViewsList.contains(viewHolder.cardViewtime))
            cardViewsList.add(viewHolder.cardViewtime);

        viewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemListener(View view, int position) {

                for (CardView cardView: cardViewsList)
                {
                    if (cardView.getTag() ==null)
                        cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                }

                viewHolder.cardViewtime.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));

                Intent intent= new Intent(Common.KEY_ENABLE_BUTTON_NEXT);
                intent.putExtra(Common.KEY_TIME_SLOT,i);
                intent.putExtra(Common.KEY_STEP,3);
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.IdTxttimeSlot)
        TextView txtTimeSlot;

        @BindView(R.id.IdTxtTimeDescripcionSlot)
        TextView txtTimeDescripcion;

        @BindView(R.id.IdCarviewTime)
        CardView cardViewtime;

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
