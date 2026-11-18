package cr.ac.una.unaplanillaws.model;

import java.util.Objects;

import jakarta.persistence.Version;
import jakarta.validation.constraints.*;

/**
 * This is a DTO for the TipoMovPlanilla entity.
 *
 * @author Dominique
 * @author Andrew
 */
public class TipMovPlanillaDto {

    private Long id;

    @NotNull(message = "El tipo de movimiento no puede estar vacío.")
    private Character TipoMonto;

    @NotNull(message = "El campo monto no puede ser nulo")
    private Double Monto;

    @NotNull(message = "El tipo de aplicacion de deducciones no puede estar vacío.")
    private Character TipoAplDeDPorc;

    private TipoMovimientosDto tipoMovimientoDto;
    private TipoPlanillaDto planillaDto;

    @Version
    private Long version;

    private Boolean modificado;

    // Constructors
    public TipMovPlanillaDto() {
    }

    public TipMovPlanillaDto(TipMovPlanilla tipMovPlanilla) {
        this.id = tipMovPlanilla.getTmpId().longValue();
        actualizar(tipMovPlanilla);
    }

    public void actualizar(TipMovPlanilla tipMovPlanilla) {
        this.TipoMonto = tipMovPlanilla.getTmpTipomonto();
        this.Monto = tipMovPlanilla.getTmpMonto();
        this.TipoAplDeDPorc = tipMovPlanilla.getTmpTipoapldedporc();
        this.version = tipMovPlanilla.getTmpVersion();
        this.tipoMovimientoDto = tipMovPlanilla.getTmovIdmov() != null ? new TipoMovimientosDto(tipMovPlanilla.getTmovIdmov()) : null;
        this.planillaDto = tipMovPlanilla.getTmpIdpla() != null ? new TipoPlanillaDto(tipMovPlanilla.getTmpIdpla()) : null;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Character getTipoMonto() {
        return TipoMonto;
    }

    public void setTipoMonto(Character tipoMonto) {
        TipoMonto = tipoMonto;
    }

    public Double getMonto() {
        return Monto;
    }

    public void setMonto(Double monto) {
        Monto = monto;
    }

    public Character getTipoAplDeDPorc() {
        return TipoAplDeDPorc;
    }

    public void setTipoAplDeDPorc(Character tipoAplDeDPorc) {
        TipoAplDeDPorc = tipoAplDeDPorc;
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

    // Overrides
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
        final TipMovPlanillaDto other = (TipMovPlanillaDto) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "TipoMovPlanillaDto{" 
        + "id=" + id 
        + ", TipoMonto=" + TipoMonto 
        + ", Monto=" + Monto 
        + ", TipoAplDeDPorc=" + TipoAplDeDPorc 
        + ", tipoMovimientoDto=" + tipoMovimientoDto 
        + ", planillaDto=" + planillaDto 
        + ", version=" + version + '}';
    }

}
