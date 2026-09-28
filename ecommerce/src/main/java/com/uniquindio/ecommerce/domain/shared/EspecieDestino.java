package com.uniquindio.ecommerce.domain.shared;

/**
 * Especie a la que va dirigido un producto. Solo vendemos para perros y gatos.
 */
public enum EspecieDestino {
    PERRO,
    GATO;

    public static EspecieDestino desde(String texto) {
        if (texto != null) {
            for (EspecieDestino especie : values()) {
                if (especie.name().equalsIgnoreCase(texto.trim())) {
                    return especie;
                }
            }
        }
        throw new ReglaDominioException("Solo se venden productos para perros o gatos");
    }
}
