package cr.ac.una.unaplanilla.model;

import jakarta.json.bind.annotation.JsonbTransient;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para el Cliente UnaPlanilla
 * 
 * @author Luis_elizo
 */
public class TipoMovimientosDto {

    private StringProperty id;
    private StringProperty tipo;
    private StringProperty descripcion;
    private BooleanProperty gravable;
    private BooleanProperty imprenta;
    private StringProperty tipoMonto;
    private StringProperty monto;
    private StringProperty tipoAplDeDPorc;
    private BooleanProperty activo;
    private Long version;
    private Boolean modificado;

    public TipoMovimientosDto() {
        this.id = new SimpleStringProperty("");
        this.tipo = new SimpleStringProperty("");
        this.descripcion = new SimpleStringProperty("");
        this.gravable = new SimpleBooleanProperty(false);
        this.imprenta = new SimpleBooleanProperty(false);
        this.tipoMonto = new SimpleStringProperty("");
        this.monto = new SimpleStringProperty("");
        this.tipoAplDeDPorc = new SimpleStringProperty("");
        this.activo = new SimpleBooleanProperty(true);
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

    public String getTipo() {
        return tipo.get();
    }

    public void setTipo(String tipo) {
        this.tipo.set(tipo != null ? tipo : "");
    }

    @JsonbTransient
    public StringProperty getTipoProperty() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion != null ? descripcion : "");
    }

    @JsonbTransient
    public StringProperty getDescripcionProperty() {
        return descripcion;
    }

    public Boolean getGravable() {
        return gravable.get();
    }

    public void setGravable(Boolean gravable) {
        this.gravable.set(gravable != null ? gravable : false);
    }

    @JsonbTransient
    public BooleanProperty getGravableProperty() {
        return gravable;
    }

    public Boolean getImprenta() {
        return imprenta.get();
    }

    public void setImprenta(Boolean imprenta) {
        this.imprenta.set(imprenta != null ? imprenta : false);
    }

    @JsonbTransient
    public BooleanProperty getImprentaProperty() {
        return imprenta;
    }

    public String getTipoMonto() {
        return tipoMonto.get();
    }

    public void setTipoMonto(String tipoMonto) {
        this.tipoMonto.set(tipoMonto != null ? tipoMonto : "");
    }

    @JsonbTransient
    public StringProperty getTipoMontoProperty() {
        return tipoMonto;
    }

    public Double getMonto() {
        if (this.monto.get() != null && !this.monto.get().isBlank()) {
            try {
                return Double.valueOf(this.monto.get());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return null;
    }

    public void setMonto(Double monto) {
        this.monto.set(monto != null ? monto.toString() : "");
    }

    @JsonbTransient
    public StringProperty getMontoProperty() {
        return monto;
    }

    public String getTipoAplDeDPorc() {
        return tipoAplDeDPorc.get();
    }

    public void setTipoAplDeDPorc(String tipoAplDeDPorc) {
        this.tipoAplDeDPorc.set(tipoAplDeDPorc != null ? tipoAplDeDPorc : "");
    }

    @JsonbTransient
    public StringProperty getTipoAplDeDPorcProperty() {
        return tipoAplDeDPorc;
    }

    public Boolean getActivo() {
        return activo.get();
    }

    public void setActivo(Boolean activo) {
        this.activo.set(activo != null ? activo : true);
    }

    @JsonbTransient
    public BooleanProperty getActivoProperty() {
        return activo;
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
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        final TipoMovimientosDto other = (TipoMovimientosDto) obj;
        return Objects.equals(this.id.get(), other.id.get());
    }

    @Override
    public String toString() {
        return "TipoMovimientosDto{" + "id=" + id.get() + ", descripcion=" + descripcion.get() + '}';
    }

    private List<TipMovPlanillaDto> tipMovPlanillaList = new ArrayList<>();

    // Solo se recibe del WS; no se envía al guardar para que el WS no toque las relaciones con planillas
    @JsonbTransient
    public List<TipMovPlanillaDto> getTipMovPlanillaList() {
        return tipMovPlanillaList;
    }

    public void setTipMovPlanillaList(List<TipMovPlanillaDto> tipMovPlanillaList) {
        this.tipMovPlanillaList = tipMovPlanillaList != null ? tipMovPlanillaList : new ArrayList<>();
    }

}