package com.CondoSync.models;

public enum StatusOcorrencia {

    ABERTO("Em aberto"),
    EM_ANDAMENTO("Em andamento"),
    RESOLVIDA("Resolvida"),
    CANCELADA("Cancelada");

    private String status;

    StatusOcorrencia(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
