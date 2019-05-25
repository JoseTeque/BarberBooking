package m.google.barberbooking.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CartDao {

    @Query("SELECT * FROM Cart WHERE userPhone=:userPhone")
    List<CartItem> getAllItemCart(String userPhone);

    @Query("SELECT COUNT(*) from Cart WHERE userPhone=:userPhone")
    int countItemCart(String userPhone);

    @Query("SELECT * FROM Cart WHERE productId=:productId AND userPhone=:userPhone")
    CartItem getProductInCart(String productId, String userPhone);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insert(CartItem...cart);

    @Update(onConflict = OnConflictStrategy.FAIL)
    void update(CartItem cartItem);

    @Delete
    void delete(CartItem cartItem);

    @Query("DELETE FROM Cart WHERE userPhone=:userPhone")
    void cleanCart(String userPhone);
}
