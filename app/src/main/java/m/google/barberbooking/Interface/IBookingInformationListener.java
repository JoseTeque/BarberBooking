package m.google.barberbooking.Interface;

import java.util.List;

import m.google.barberbooking.modelo.Banner;
import m.google.barberbooking.modelo.BookingInformation;

public interface IBookingInformationListener {

    void IBookingInformationEmpty();
    void IBookingInformationLoadSucces(BookingInformation bookingInformation);
    void IBookingInformationLoadOnFailed(String message);
}
