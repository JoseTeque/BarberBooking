package m.google.barberbooking.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import m.google.barberbooking.Fragments.BookingStep1Fragment;
import m.google.barberbooking.Fragments.BookingStep2Fragment;
import m.google.barberbooking.Fragments.BookingStep3Fragment;
import m.google.barberbooking.Fragments.BookingStep4Fragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i)
        {
            case 0:
                return BookingStep1Fragment.getInstance();
            case 1:
                return BookingStep2Fragment.getInstance();
            case 2:
                return BookingStep3Fragment.getInstance();
            case 3:
                return BookingStep4Fragment.getInstance();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
