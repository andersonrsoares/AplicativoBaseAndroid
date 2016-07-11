package br.com.anderson.aplicativobase;

/**
 * Created by DevMaker on 7/11/16.
 */
public class ListModel {
    private int id;
    private String name;

    public ListModel(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
