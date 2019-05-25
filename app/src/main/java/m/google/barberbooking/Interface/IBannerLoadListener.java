package m.google.barberbooking.Interface;

import java.util.List;

import m.google.barberbooking.modelo.Banner;

public interface IBannerLoadListener {
    void IBannerLoadSucces(List<Banner> banners);
    void IBannerLoadOnFailed(String message);
}
