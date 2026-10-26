package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import com.uniquindio.ecommerce.domain.valueobject.Dinero;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * El value object Dinero guardado como dos columnas (monto y moneda) dentro de la tabla
 * que lo usa. Cada entidad le pone su propio nombre a esas columnas con @AttributeOverride.
 */
@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class DineroEmbeddable {

    private BigDecimal monto;

    private String moneda;

    public static DineroEmbeddable desde(Dinero dinero) {
        return new DineroEmbeddable(dinero.monto(), dinero.moneda());
    }

    public Dinero aDominio() {
        return new Dinero(monto, moneda);
    }
}
