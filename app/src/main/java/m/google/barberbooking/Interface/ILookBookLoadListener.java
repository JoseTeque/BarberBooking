package m.google.barberbooking.Interface;

import java.util.List;

import m.google.barberbooking.modelo.Banner;

public interface ILookBookLoadListener {

    void ILookLoadSucces(List<Banner> banners);
    void ILookLoadOnFailed(String message);
}
