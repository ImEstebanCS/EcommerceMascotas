package com.uniquindio.ecommerce.domain.publicacion;

import com.uniquindio.ecommerce.domain.shared.ReglaDominioException;

public enum CategoriaProducto {
    ALIMENTO,
    JUGUETE,
    ACCESORIO,
    HIGIENE,
    SALUD;

    public static CategoriaProducto desde(String texto) {
        if (texto != null) {
            for (CategoriaProducto categoria : values()) {
                if (categoria.name().equalsIgnoreCase(texto.trim())) {
                    return categoria;
                }
            }
        }
        throw new ReglaDominioException("La categoria del producto no es valida: " + texto);
    }
}
