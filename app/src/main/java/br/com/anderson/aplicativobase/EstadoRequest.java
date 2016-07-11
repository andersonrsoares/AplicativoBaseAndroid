package br.com.anderson.aplicativobase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DevMaker on 7/11/16.
 */
public class EstadoRequest {
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("content")
    @Expose
    private List<Estado> content = new ArrayList<>();

    @Override
    public String toString() {
        return "EstadoRequest{" +
                "result='" + result + '\'' +
                ", content=" + content +
                '}';
    }
}
