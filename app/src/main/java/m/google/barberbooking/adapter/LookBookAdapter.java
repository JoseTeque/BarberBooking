package m.google.barberbooking.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import m.google.barberbooking.R;
import m.google.barberbooking.modelo.Banner;

public class LookBookAdapter extends RecyclerView.Adapter<LookBookAdapter.ViewHolder> {

    private List<Banner> lisLook;
    private Context context;

    public LookBookAdapter(List<Banner> lisLook, Context context) {
        this.lisLook = lisLook;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.look_book_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picasso.get().load(lisLook.get(i).getImage()).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return lisLook.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.Id_ImageLookBook)
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);

        }
    }
}
