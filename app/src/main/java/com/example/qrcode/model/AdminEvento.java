package com.example.qrcode.model;

public class AdminEvento {
    //private long ID;
    private String key;
    private String nomeEvento, local, data, descricao, organizador ,horarioInicio, horarioFim;


    public AdminEvento(){

    }

    public AdminEvento(String nomeEvento, String local, String data, String descricao, String organizador, String horarioInicio, String horarioFim , String key){
        //this.ID = ID;
        this.key = key;
        this.nomeEvento = nomeEvento;
        this.local = local;
        this.data = data;
        this.descricao = descricao;
        this.organizador = organizador;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
    }

    /*public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }*/

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNomeEvento() {
        return nomeEvento;
    }

    public void setNomeEvento(String nomeEvento) {
        this.nomeEvento = nomeEvento;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOrganizador() {
        return organizador;
    }

    public void setOrganizador(String organizador) {
        this.organizador = organizador;
    }

    public String getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(String horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public String getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(String horarioFim) {
        this.horarioFim = horarioFim;
    }
}


