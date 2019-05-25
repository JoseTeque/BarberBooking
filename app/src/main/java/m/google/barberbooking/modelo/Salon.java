package m.google.barberbooking.modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Salon implements Parcelable {

    private String name;
    private String address;
    private String salonId;
    private String webSite;
    private String phone;
    private String openHor;

    public Salon() {
    }

    public Salon(String name, String address, String salonId, String webSite, String phone, String openHor) {
        this.name = name;
        this.address = address;
        this.salonId = salonId;
        this.webSite = webSite;
        this.phone = phone;
        this.openHor = openHor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSalonId() {
        return salonId;
    }

    public void setSalonId(String salonId) {
        this.salonId = salonId;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenHor() {
        return openHor;
    }

    public void setOpenHor(String openHor) {
        this.openHor = openHor;
    }

    protected Salon(Parcel in) {
        name = in.readString();
        address = in.readString();
        salonId = in.readString();
        webSite = in.readString();
        phone = in.readString();
        openHor = in.readString();
    }

    public static final Creator<Salon> CREATOR = new Creator<Salon>() {
        @Override
        public Salon createFromParcel(Parcel in) {
            return new Salon(in);
        }

        @Override
        public Salon[] newArray(int size) {
            return new Salon[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(salonId);
        dest.writeString(webSite);
        dest.writeString(phone);
        dest.writeString(openHor);
    }
}
