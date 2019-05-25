package m.google.barberbooking.Interface;

import java.util.List;

import m.google.barberbooking.modelo.Banner;
import m.google.barberbooking.modelo.Salon;

public interface IBranchLoadListener {

    void IBranchLoadSucces(List<Salon> salonlist);
    void IBranchLoadOnFailed(String message);
}
