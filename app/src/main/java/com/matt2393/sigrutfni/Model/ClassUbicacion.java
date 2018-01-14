package com.matt2393.sigrutfni.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matt2393 on 13-01-18.
 * Clase que guarda la ubicacion
 */

public class ClassUbicacion implements Parcelable {
    private double lat,lng;
    private String descripcion;

    public ClassUbicacion() {
    }

    public ClassUbicacion(double lat, double lng, String descripcion) {
        this.lat = lat;
        this.lng = lng;
        this.descripcion = descripcion;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.descripcion);
    }

    protected ClassUbicacion(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.descripcion = in.readString();
    }

    public static final Parcelable.Creator<ClassUbicacion> CREATOR = new Parcelable.Creator<ClassUbicacion>() {
        @Override
        public ClassUbicacion createFromParcel(Parcel source) {
            return new ClassUbicacion(source);
        }

        @Override
        public ClassUbicacion[] newArray(int size) {
            return new ClassUbicacion[size];
        }
    };
}
