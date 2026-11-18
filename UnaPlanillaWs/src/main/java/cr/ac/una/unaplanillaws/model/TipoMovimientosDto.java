package cr.ac.una.unaplanillaws.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Version;
import jakarta.validation.constraints.*;

/**
 * This is a DTO for the TipoMovimento entity.
 *
 * @author Dominique
 * @author Andrew
 */
public class TipoMovimientosDto {

    private Long id;

    @NotNull(message = "El tipo de movimiento no puede estar vacío.")
    private Character tipo;
    @NotBlank(message = "La descripcion no puede estar vacía.")
    @Size(min = 1, max = 40, message = "La descripcion debe tener una longitud entre 1 y 40 caracteres")
    private String descripcion;
    @NotNull(message = "El campo gravable no puede ser nulo")
    private Boolean gravable;
    @NotNull(message = "El campo imprenta no puede ser nulo")
    private Boolean imprenta;
    @NotNull(message = "El tipo de monto no puede estar vacío.")
    private Character tipoMonto;
    @NotNull(message = "El campo monto no puede ser nulo")
    private Double monto;
    @NotNull(message = "El tipo de aplicacion de deducciones no puede estar vacío.")
    private Character tipoAplDeDPorc;
    @NotNull(message = "El campo activo no puede ser nulo")
    private Boolean activo;
    @Version
    private Long version;

    private List<TipMovPlanillaDto> tipMovPlanillaList;

    // Constructors
    public TipoMovimientosDto() {
    }

    public TipoMovimientosDto(TipoMovimientos tipoMovimiento) {
        this.id = tipoMovimiento.getTmovId().longValue();
        this.tipMovPlanillaList = new ArrayList<>();
        actualizar(tipoMovimiento);
    }

    public void actualizar(TipoMovimientos tipoMovimiento) {
        this.tipo = tipoMovimiento.getTmovTipo();
        this.descripcion = tipoMovimiento.getTmovDescripciN();
        this.gravable = tipoMovimiento.getTmovGravable() == 'S';
        this.imprenta = tipoMovimiento.getTmovImprenta() == 'S';
        this.tipoMonto = tipoMovimiento.getTmovTipomonto();
        this.monto = tipoMovimiento.getTmovMonto();
        this.tipoAplDeDPorc = tipoMovimiento.getTmovTipoapldedporc();
        this.activo = tipoMovimiento.getTmovEstado() == 'A';
        this.version = tipoMovimiento.getTmovVersion().longValue();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Character getTipo() {
        return tipo;
    }

    public void setTipo(Character tipo) {
        this.tipo = tipo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getGravable() {
        return gravable;
    }

    public void setGravable(Boolean gravable) {
        this.gravable = gravable;
    }

    public Boolean getImprenta() {
        return imprenta;
    }

    public void setImprenta(Boolean imprenta) {
        this.imprenta = imprenta;
    }

    public Character getTipoMonto() {
        return tipoMonto;
    }
    
    public void setTipoMonto(Character tipoMonto) {
        this.tipoMonto = tipoMonto;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Character getTipoAplDeDPorc() {
        return tipoAplDeDPorc;
    }

    public void setTipoAplDeDPorc(Character tipoAplDeDPorc) {
        this.tipoAplDeDPorc = tipoAplDeDPorc;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<TipMovPlanillaDto> getTipMovPlanillaList() {
        return tipMovPlanillaList;
    }

    public void setTipMovPlanillaList(List<TipMovPlanillaDto> tipMovPlanillaList) {
        this.tipMovPlanillaList = tipMovPlanillaList;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TipoMovimientosDto other = (TipoMovimientosDto) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "TipoMovimentoDto{"
        + "id=" + id 
        + ", tipo=" + tipo 
        + ", descripcion=" + descripcion 
        + ", gravable=" + gravable 
        + ", imprenta=" + imprenta 
        + ", tipoMonto=" + tipoMonto 
        + ", monto=" + monto 
        + ", tipoAplDeDPorc=" + tipoAplDeDPorc 
        + ", activo=" + activo 
        + ", version=" + version + '}';
    }

}
