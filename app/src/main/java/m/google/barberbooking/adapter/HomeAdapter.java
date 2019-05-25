package m.google.barberbooking.adapter;

import java.util.List;

import m.google.barberbooking.modelo.Banner;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class HomeAdapter extends SliderAdapter {

   private List<Banner> listBanner;

    public HomeAdapter(List<Banner> listBanner) {
        this.listBanner = listBanner;
    }

    @Override
    public int getItemCount() {
        return listBanner.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
         imageSlideViewHolder.bindImageSlide(listBanner.get(position).getImage());
    }
}
