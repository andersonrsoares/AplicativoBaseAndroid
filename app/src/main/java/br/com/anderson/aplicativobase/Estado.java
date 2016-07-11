package br.com.anderson.aplicativobase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by DevMaker on 7/11/16.
 */
public class Estado {

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("Sigla")
    @Expose
    private String sigla;
    @SerializedName("Nome")
    @Expose
    private String nome;


    @Override
    public String toString() {
        return "Estado{" +
                "iD='" + iD + '\'' +
                ", sigla='" + sigla + '\'' +
                ", nome='" + nome + '\'' +
                '}';
    }
}
