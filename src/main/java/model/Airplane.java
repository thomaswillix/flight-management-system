package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

import static utils.Validation.*;

@Entity
@Getter
@Setter
public class Airplane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brand;
    private String model;
    private BigDecimal airplaneWeight;

    public Airplane() {}

    public Airplane(String brand, String model, BigDecimal airplaneWeight) {
        this.brand = requireNotBlank(brand, "Brand");
        this.model = requireNotBlank(model, "Model");
        this.airplaneWeight = requireValidWeight(airplaneWeight, "Airplane");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Airplane airplane = (Airplane) o;
        return Objects.equals(id, airplane.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
