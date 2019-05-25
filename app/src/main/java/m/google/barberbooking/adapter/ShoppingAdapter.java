package m.google.barberbooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import m.google.barberbooking.Common.Common;
import m.google.barberbooking.Database.CartDatabase;
import m.google.barberbooking.Database.CartItem;
import m.google.barberbooking.Database.DatabaseUtils;
import m.google.barberbooking.Interface.IRecyclerItemSelectedListener;
import m.google.barberbooking.R;
import m.google.barberbooking.modelo.Shopping;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> {

   private Context context;
   private List<Shopping> shoppingList;
   private CartDatabase cartDatabase;

    public ShoppingAdapter(Context context, List<Shopping> shoppingList) {
        this.context = context;
        this.shoppingList = shoppingList;
        cartDatabase= CartDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.shopping_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Shopping shopping= shoppingList.get(position);

        holder.txtName.setText(Common.formatShopping(shopping.getName()));
        Picasso.get().load(shopping.getImage()).into(holder.imageViewShopping);
        holder.txtPrice.setText(new StringBuilder("$").append(shopping.getPrice()).toString());

        holder.setListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemListener(View view, int position) {
                //create cart item

                CartItem cartItem= new CartItem();
                cartItem.setProductId(shopping.getId());
                cartItem.setProductName(shopping.getName());
                cartItem.setProductImage(shopping.getImage());
                cartItem.setProductQuantity(1);
                cartItem.setProductPrice(shopping.getPrice());
                cartItem.setUserPhone(Common.currentUser.getPhone());

                //Insert AT db

                DatabaseUtils.insertCart(cartDatabase,cartItem);
                Toast.makeText(context, "Add to Cart..!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.IdImageShopping)
        ImageView imageViewShopping;

        @BindView(R.id.IdtxtNameShopping)
        TextView txtName;

        @BindView(R.id.IdTxtPriceShopping)
        TextView txtPrice;

        @BindView(R.id.IdTxtCartShopping)
        TextView txtCartShopping;

        IRecyclerItemSelectedListener listener;

        public void setListener(IRecyclerItemSelectedListener listener) {
            this.listener = listener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            txtCartShopping.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemListener(v, getAdapterPosition());
        }
    }
}
