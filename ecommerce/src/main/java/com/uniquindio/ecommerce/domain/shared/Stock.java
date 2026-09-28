package com.uniquindio.ecommerce.domain.shared;

/**
 * Value Object con las unidades disponibles de una publicacion.
 */
public record Stock(int unidades) {

    public Stock {
        if (unidades < 0) {
            throw new ReglaDominioException("El stock no puede ser negativo");
        }
    }

    public boolean hayDisponible(Cantidad cantidad) {
        return unidades >= cantidad.valor();
    }

    public Stock descontar(Cantidad cantidad) {
        if (!hayDisponible(cantidad)) {
            throw new ReglaDominioException("No hay stock suficiente para descontar " + cantidad.valor() + " unidades");
        }
        return new Stock(unidades - cantidad.valor());
    }

    public boolean estaAgotado() {
        return unidades == 0;
    }
}
