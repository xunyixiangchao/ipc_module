package com.lis.ipc_module.location;

import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable {
    String address;
    double lat;
    double lng;

    protected Location(Parcel in) {
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }
}
