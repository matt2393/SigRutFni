package com.matt2393.sigrutfni.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matt2393 on 13-01-18.
 * Clase que contendra datos del conductor
 */

public class ClassConductor implements Parcelable {
    private String nombre,placa;
    private ClassUbicacion ubicacion;
    private boolean activo;

    public ClassConductor() {
    }

    public ClassConductor(String nombre, String placa, ClassUbicacion ubicacion, boolean activo) {
        this.nombre = nombre;
        this.placa = placa;
        this.ubicacion = ubicacion;
        this.activo = activo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public ClassUbicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(ClassUbicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Map<String, Object> toMapUbic(){
        Map<String, Object> res=new HashMap<>();
        res.put("ubicacion",ubicacion);
        return res;
    }
    public Map<String, Object> toMapAct(){
        Map<String, Object> res=new HashMap<>();
        res.put("activo",activo);
        return res;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nombre);
        dest.writeString(this.placa);
        dest.writeParcelable(this.ubicacion, flags);
        dest.writeByte(this.activo ? (byte) 1 : (byte) 0);
    }

    protected ClassConductor(Parcel in) {
        this.nombre = in.readString();
        this.placa = in.readString();
        this.ubicacion = in.readParcelable(ClassUbicacion.class.getClassLoader());
        this.activo = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ClassConductor> CREATOR = new Parcelable.Creator<ClassConductor>() {
        @Override
        public ClassConductor createFromParcel(Parcel source) {
            return new ClassConductor(source);
        }

        @Override
        public ClassConductor[] newArray(int size) {
            return new ClassConductor[size];
        }
    };
}
