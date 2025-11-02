package com.example.qrcode.model;

import java.util.Map;
import java.util.HashMap;

public class AdminEvento {
    //private long ID;
    private String key;
    private String nomeEvento, local, data, descricao, organizador ,horarioInicio, horarioFim;
    private Map<String, Presenca> presenca;

    public AdminEvento(){
        this.presenca = new HashMap<>(); // evita nullPointer
    }

    public AdminEvento(String nomeEvento, String local, String data, String descricao, String organizador, String horarioInicio, String horarioFim , String key,String presenca){
        //this.ID = ID;
        this.key = key;
        this.nomeEvento = nomeEvento;
        this.local = local;
        this.data = data;
        this.descricao = descricao;
        this.organizador = organizador;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.presenca = new HashMap<>();
    }

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
    public Map<String, Presenca> getPresenca() { return presenca; }
    public void setPresenca(Map<String, Presenca> presenca) { this.presenca = presenca; }

    public void adicionarPresenca(String keyIngressante, Presenca presenca) {
        this.presenca.put(keyIngressante, presenca);
    }
}


