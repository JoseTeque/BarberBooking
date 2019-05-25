package m.google.barberbooking.Database;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.SQLClientInfoException;
import java.util.List;

import m.google.barberbooking.Common.Common;
import m.google.barberbooking.Interface.ICountItemInCartListener;

public class DatabaseUtils {

    //because all room handle new work on other thread

    public static void getAllItemFromCart(CartDatabase db)
    {
        GetAllCartAsyn task= new GetAllCartAsyn(db);
        task.execute(Common.currentUser.getPhone());
    }

    public static void insertCart(CartDatabase db,CartItem... cartItems)
    {
        InsertCartAsyn task= new InsertCartAsyn(db);
        task.execute(cartItems);
    }

    public static void countItemCart(CartDatabase db, ICountItemInCartListener iCountItemInCartListener)
    {
        CountItemCartAsyn task= new CountItemCartAsyn(db,iCountItemInCartListener);
        task.execute();
    }

    /*
    ----------------------------------------------------------
    ASYN TASK DEFINE
    -----------------------------------------------------------
     */

    private static class GetAllCartAsyn extends AsyncTask<String,Void,Void>{

         CartDatabase db;
        public GetAllCartAsyn(CartDatabase cartDatabase)
        {
            this.db= cartDatabase;
        }

        @Override
        protected Void doInBackground(String... strings) {
           getAllItemFromCartByUserPhone(db,strings[0]);
            return null;
        }

        private void getAllItemFromCartByUserPhone(CartDatabase db, String userPhone) {

         List<CartItem> cartItems =  db.cartDao().getAllItemCart(userPhone);
            Log.d("COUNT_CARTS","" +  cartItems.size());
        }
    }

    private static class InsertCartAsyn extends AsyncTask<CartItem,Void,Void>{

        CartDatabase db;

        public InsertCartAsyn(CartDatabase cartDatabase)
        {
            this.db= cartDatabase;
        }

        @Override
        protected Void doInBackground(CartItem... cartItems) {
            insertCartUserPhone(db,cartItems[0]);
            return null;
        }

        private void insertCartUserPhone(CartDatabase db, CartItem cartItem) {

            //if item already available int cart, just increase quantity
             try {
                 db.cartDao().insert(cartItem);

             }catch (SQLiteConstraintException exception)
             {
                 CartItem updateCartItem= db.cartDao().getProductInCart(cartItem.getProductId(),Common.currentUser.getPhone());
                 updateCartItem.setProductQuantity(updateCartItem.getProductQuantity() + 1);
                 db.cartDao().update(updateCartItem);
             }
        }
    }

    private static class CountItemCartAsyn extends AsyncTask<Void,Void,Integer>{

        CartDatabase db;
        ICountItemInCartListener listener;

        public CountItemCartAsyn(CartDatabase cartDatabase, ICountItemInCartListener iCountItemInCartListener)
        {
            this.db= cartDatabase;
            listener= iCountItemInCartListener;
        }


        @Override
        protected Integer doInBackground(Void... voids) {
            return  Integer.parseInt(String.valueOf(countItemCartRun(db)));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            listener.onCartItemCountSucces(integer.intValue());
        }

        private int countItemCartRun(CartDatabase db) {

            return db.cartDao().countItemCart(Common.currentUser.getPhone());
        }
    }
}
