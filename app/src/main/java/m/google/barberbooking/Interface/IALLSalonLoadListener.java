package m.google.barberbooking.Interface;

import java.util.List;

import m.google.barberbooking.modelo.Banner;

public interface IALLSalonLoadListener {

    void IAllSalonLoadSucces(List<String> list);
    void IAllSalonLoadOnFailed(String message);
}
