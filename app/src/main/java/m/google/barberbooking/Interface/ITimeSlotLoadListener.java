package m.google.barberbooking.Interface;

import java.util.List;

import m.google.barberbooking.modelo.TimeSlot;

public interface ITimeSlotLoadListener {

    void ITimeSlotLoadSucces(List<TimeSlot> listTime);
    void ITimeSlotLoadOnFailed(String message);
    void ITimeSlotLoadEmpty();
}
