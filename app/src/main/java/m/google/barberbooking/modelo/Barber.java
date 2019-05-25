package m.google.barberbooking.modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Barber implements Parcelable {

    private String name;
    private String password;
    private String username;
    private Long rating;
    private String barberId;

    public Barber() {
    }

    public Barber(String name, String password, String username, Long  rating) {
        this.name = name;
        this.password = password;
        this.username = username;
        this.rating = rating;
    }

    protected Barber(Parcel in) {
        name = in.readString();
        password = in.readString();
        username = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readLong();
        }
        barberId = in.readString();
    }

    public static final Creator<Barber> CREATOR = new Creator<Barber>() {
        @Override
        public Barber createFromParcel(Parcel in) {
            return new Barber(in);
        }

        @Override
        public Barber[] newArray(int size) {
            return new Barber[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long  getRating() {
        return rating;
    }

    public void setRating(Long  rating) {
        this.rating = rating;
    }

    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(password);
        dest.writeString(username);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(rating);
        }
        dest.writeString(barberId);
    }
}
