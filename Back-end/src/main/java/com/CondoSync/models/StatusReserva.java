package com.CondoSync.models;

public enum StatusReserva {
    PENDENTE("Pendente"),
    APROVADA("Aprovada"),
    FINALIZADA("Finalizada"),
    REJEITADA("Rejeitada"),
    CANCELADA("Cancelada");

    private final String status;

    StatusReserva(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static StatusReserva fromString(String status) {
        for (StatusReserva s : StatusReserva.values()) {
            if (s.status.equalsIgnoreCase(status)) {
                return s;
            }
        }
        return null;
    }

}
