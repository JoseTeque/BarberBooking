package m.google.barberbooking.Interface;

import java.util.List;

import m.google.barberbooking.modelo.Banner;
import m.google.barberbooking.modelo.Shopping;

public interface IShoppingLoadListener {

    void IShoppingLoadSucces(List<Shopping> listShopping);
    void IShoppingLoadOnFailed(String message);
}
