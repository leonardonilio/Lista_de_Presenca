package com.example.qrcode.model;

public class Ingressante {
    private String keyIngressante,NomeIngressante,EmailIgressante,TelefoneIngressante;

    public Ingressante() {
    }

    public Ingressante(String keyIngressante, String nomeIngressante, String emailIgressante, String telefoneIngressante) {
        this.keyIngressante = keyIngressante;
        NomeIngressante = nomeIngressante;
        EmailIgressante = emailIgressante;
        TelefoneIngressante = telefoneIngressante;
    }

    public String getKeyIngressante() {
        return keyIngressante;
    }

    public void setKeyIngressante(String keyIngressante) {
        this.keyIngressante = keyIngressante;
    }

    public String getNomeIngressante() {
        return NomeIngressante;
    }

    public void setNomeIngressante(String nomeIngressante) {
        NomeIngressante = nomeIngressante;
    }

    public String getEmailIgressante() {
        return EmailIgressante;
    }

    public void setEmailIgressante(String emailIgressante) {
        EmailIgressante = emailIgressante;
    }

    public String getTelefoneIngressante() {
        return TelefoneIngressante;
    }

    public void setTelefoneIngressante(String telefoneIngressante) {
        TelefoneIngressante = telefoneIngressante;
    }
}
