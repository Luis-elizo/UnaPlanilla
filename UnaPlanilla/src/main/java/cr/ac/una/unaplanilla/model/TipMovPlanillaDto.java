package cr.ac.una.unaplanilla.model;

import jakarta.json.bind.annotation.JsonbTransient;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * DTO para TipoMovPlanilla en el Cliente (JavaFX)
 *
 * @author Dominique
 * @author Andrew
 */
public class TipMovPlanillaDto {

    private StringProperty id;
    private ObjectProperty<Character> tipoMonto;
    private ObjectProperty<Double> monto;
    private ObjectProperty<Character> tipoAplDeDPorc;
    private TipoMovimientosDto tipoMovimientoDto;
    private TipoPlanillaDto planillaDto;
    private Long version;
    private Boolean modificado;

    public TipMovPlanillaDto() {
        this.id = new SimpleStringProperty("");
        this.tipoMonto = new SimpleObjectProperty<>(' ');
        this.monto = new SimpleObjectProperty<>(0.0);
        this.tipoAplDeDPorc = new SimpleObjectProperty<>(' ');
        this.tipoMovimientoDto = new TipoMovimientosDto();
        this.planillaDto = new TipoPlanillaDto();
        this.modificado = false;
    }

    public Long getId() {
        if (this.id.get() != null && !this.id.get().isBlank()) {
            return Long.valueOf(this.id.get());
        }
        return null;
    }

    public void setId(Long id) {
        this.id.set(id != null ? id.toString() : "");
    }

    @JsonbTransient
    public StringProperty getIdProperty() {
        return id;
    }

    public Character getTipoMonto() {
        return tipoMonto.get();
    }

    public void setTipoMonto(Character tipoMonto) {
        this.tipoMonto.set(tipoMonto);
    }

    @JsonbTransient
    public ObjectProperty<Character> getTipoMontoProperty() {
        return tipoMonto;
    }

    public Double getMonto() {
        return monto.get();
    }

    public void setMonto(Double monto) {
        this.monto.set(monto != null ? monto : 0.0);
    }

    @JsonbTransient
    public ObjectProperty<Double> getMontoProperty() {
        return monto;
    }

    public Character getTipoAplDeDPorc() {
        return tipoAplDeDPorc.get();
    }

    public void setTipoAplDeDPorc(Character tipoAplDeDPorc) {
        this.tipoAplDeDPorc.set(tipoAplDeDPorc);
    }

    @JsonbTransient
    public ObjectProperty<Character> getTipoAplDeDPorcProperty() {
        return tipoAplDeDPorc;
    }

    public TipoMovimientosDto getTipoMovimientoDto() {
        return tipoMovimientoDto;
    }

    public void setTipoMovimientoDto(TipoMovimientosDto tipoMovimientoDto) {
        this.tipoMovimientoDto = tipoMovimientoDto;
    }

    public TipoPlanillaDto getPlanillaDto() {
        return planillaDto;
    }

    public void setPlanillaDto(TipoPlanillaDto planillaDto) {
        this.planillaDto = planillaDto;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getModificado() {
        return modificado;
    }

    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id.get());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final TipMovPlanillaDto other = (TipMovPlanillaDto) obj;
        return Objects.equals(this.id.get(), other.id.get());
    }

    @Override
    public String toString() {
        return "TipMovPlanillaDto{" 
            + "id=" + id.get() 
            + ", tipoMonto=" + tipoMonto.get() 
            + ", monto=" + monto.get() 
            + ", tipoAplDeDPorc=" + tipoAplDeDPorc.get() 
            + ", version=" + version + '}';
    }
}