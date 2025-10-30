package com.example.qrcode.model;

public class Ingressante {
    private String keyIngressante,NomeIngressante,EmailIngressante,TelefoneIngressante;

    public Ingressante() {
    }

    public Ingressante(String keyIngressante, String nomeIngressante, String emailIngressante, String telefoneIngressante) {
        this.keyIngressante = keyIngressante;
        NomeIngressante = nomeIngressante;
        EmailIngressante = emailIngressante;
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

    public String getEmailIngressante() {
        return EmailIngressante;
    }

    public void setEmailIngressante(String emailIngressante) {
        EmailIngressante = emailIngressante;
    }

    public String getTelefoneIngressante() {
        return TelefoneIngressante;
    }

    public void setTelefoneIngressante(String telefoneIngressante) {
        TelefoneIngressante = telefoneIngressante;
    }
}
