package com.example.qrcode.model;

public class Presenca {
    private String fk_KeyIngressante,fk_Nome,horarioEntrada,horarioSaida;

    public Presenca() {}

    public Presenca(String fk_KeyIngressante, String fk_Nome, String horarioEntrada, String horarioSaida) {
        this.fk_KeyIngressante = fk_KeyIngressante;
        this.fk_Nome = fk_Nome;
        this.horarioEntrada = horarioEntrada;
        this.horarioSaida = horarioSaida;
    }

    public String getFk_KeyIngressante() { return fk_KeyIngressante; }
    public void setFk_KeyIngressante(String fk_KeyIngressante) { this.fk_KeyIngressante = fk_KeyIngressante; }

    public String getFk_Nome() { return fk_Nome; }
    public void setFk_Nome(String fk_Nome) { this.fk_Nome = fk_Nome; }

    public String getHorarioEntrada() { return horarioEntrada; }
    public void setHorarioEntrada(String horarioEntrada) { this.horarioEntrada = horarioEntrada; }

    public String getHorarioSaida() { return horarioSaida; }
    public void setHorarioSaida(String horarioSaida) { this.horarioSaida = horarioSaida; }
}

