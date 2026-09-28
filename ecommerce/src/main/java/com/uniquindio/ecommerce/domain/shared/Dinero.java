package com.uniquindio.ecommerce.domain.shared;

import java.math.BigDecimal;

/**
 * Value Object que representa una cantidad de dinero en una moneda.
 */
public record Dinero(BigDecimal monto, String moneda) {

    public Dinero {
        if (monto == null) {
            throw new ReglaDominioException("El monto es obligatorio");
        }
        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaDominioException("El monto no puede ser negativo");
        }
        if (moneda == null || moneda.isBlank()) {
            throw new ReglaDominioException("La moneda es obligatoria");
        }
    }

    public static Dinero cero(String moneda) {
        return new Dinero(BigDecimal.ZERO, moneda);
    }

    public Dinero sumar(Dinero otro) {
        if (!moneda.equals(otro.moneda())) {
            throw new ReglaDominioException("No se pueden sumar montos de monedas distintas");
        }
        return new Dinero(monto.add(otro.monto()), moneda);
    }

    public Dinero multiplicar(int factor) {
        return new Dinero(monto.multiply(BigDecimal.valueOf(factor)), moneda);
    }

    public boolean esMayorQueCero() {
        return monto.compareTo(BigDecimal.ZERO) > 0;
    }
}
