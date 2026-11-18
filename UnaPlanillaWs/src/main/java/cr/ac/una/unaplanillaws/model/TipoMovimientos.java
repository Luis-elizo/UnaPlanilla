package cr.ac.una.unaplanillaws.model;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Dominique
 */
@Entity
@Table(name = "PLAM_TIPOMOVIMIENTOS", schema = "UNA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipoMovimientos.findAll", query = "SELECT p FROM TipoMovimientos p"),
    @NamedQuery(name = "TipoMovimientos.findById", query = "SELECT p FROM TipoMovimientos p WHERE p.tmovId = :id"),
    @NamedQuery(name = "TipoMovimientos.findByTmovTipo", query = "SELECT p FROM TipoMovimientos p WHERE p.tmovTipo = :tmovTipo"),
    @NamedQuery(name = "TipoMovimientos.findByTmovDescripciN", query = "SELECT p FROM TipoMovimientos p WHERE p.tmovDescripciN = :tmovDescripciN"),
    @NamedQuery(name = "TipoMovimientos.findByDescripcion", query = "SELECT p FROM TipoMovimientos p WHERE UPPER(p.tmovDescripciN) like :descripcion"),
    @NamedQuery(name = "TipoMovimientos.findByTmovGravable", query = "SELECT p FROM TipoMovimientos p WHERE p.tmovGravable = :tmovGravable"),
    @NamedQuery(name = "TipoMovimientos.findByTmovImprenta", query = "SELECT p FROM TipoMovimientos p WHERE p.tmovImprenta = :tmovImprenta"),
    @NamedQuery(name = "TipoMovimientos.findByTmovTipomonto", query = "SELECT p FROM TipoMovimientos p WHERE p.tmovTipomonto = :tmovTipomonto"),
    @NamedQuery(name = "TipoMovimientos.findByTmovMonto", query = "SELECT p FROM TipoMovimientos p WHERE p.tmovMonto = :tmovMonto"),
    @NamedQuery(name = "TipoMovimientos.findByTmovTipoapldedporc", query = "SELECT p FROM TipoMovimientos p WHERE p.tmovTipoapldedporc = :tmovTipoapldedporc"),
    @NamedQuery(name = "TipoMovimientos.findByTmovEstado", query = "SELECT p FROM TipoMovimientos p WHERE p.tmovEstado = :tmovEstado"),
    @NamedQuery(name = "TipoMovimientos.findByTmovVersion", query = "SELECT p FROM TipoMovimientos p WHERE p.tmovVersion = :tmovVersion")})
public class TipoMovimientos implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @SequenceGenerator(name = "PLAM_TIPOMOVIMIENTOS_TMOV_ID_GENERATOR", sequenceName = "una.PLAM_TIPOMOVIMIENTOS_SEQ01", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLAM_TIPOMOVIMIENTOS_TMOV_ID_GENERATOR")
    @Basic(optional = false)
    @Column(name = "TMOV_ID")
    private Long tmovId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TMOV_TIPO")
    private Character tmovTipo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "TMOV_DESCRIPCI\ufffdN")
    private String tmovDescripciN;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TMOV_GRAVABLE")
    private Character tmovGravable;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TMOV_IMPRENTA")
    private Character tmovImprenta;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TMOV_TIPOMONTO")
    private Character tmovTipomonto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TMOV_MONTO")
    private Double tmovMonto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TMOV_TIPOAPLDEDPORC")
    private Character tmovTipoapldedporc;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TMOV_ESTADO")
    private Character tmovEstado;
    @Version
    @Basic(optional = false)
    @Column(name = "TMOV_VERSION")
    private Long tmovVersion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tmovIdmov")
    private List<TipMovPlanilla> plamTipmovplanillaList;

    public TipoMovimientos() {
    }

    public TipoMovimientos(TipoMovimientosDto tipoMovimientoDto) {
        this.tmovId = tipoMovimientoDto.getId();
        actualizar(tipoMovimientoDto);
    }

    public void actualizar(TipoMovimientosDto tipoMovimientoDto) {
        this.tmovTipo = tipoMovimientoDto.getTipo();
        this.tmovDescripciN = tipoMovimientoDto.getDescripcion();
        this.tmovGravable = tipoMovimientoDto.getGravable() ? 'S':'N';
        this.tmovImprenta = tipoMovimientoDto.getImprenta() ? 'S':'N';
        this.tmovTipomonto = tipoMovimientoDto.getTipoMonto();
        this.tmovMonto = tipoMovimientoDto.getMonto();
        this.tmovTipoapldedporc = tipoMovimientoDto.getTipoAplDeDPorc();
        this.tmovEstado = tipoMovimientoDto.getActivo() ? 'A':'I';
        this.tmovVersion = tipoMovimientoDto.getVersion();
    }

    public Long getTmovId() {
        return tmovId;
    }

    public void setTmovId(Long tmovId) {
        this.tmovId = tmovId;
    }

    public Character getTmovTipo() {
        return tmovTipo;
    }

    public void setTmovTipo(Character tmovTipo) {
        this.tmovTipo = tmovTipo;
    }

    public String getTmovDescripciN() {
        return tmovDescripciN;
    }

    public void setTmovDescripciN(String tmovDescripciN) {
        this.tmovDescripciN = tmovDescripciN;
    }

    public Character getTmovGravable() {
        return tmovGravable;
    }

    public void setTmovGravable(Character tmovGravable) {
        this.tmovGravable = tmovGravable;
    }

    public Character getTmovImprenta() {
        return tmovImprenta;
    }

    public void setTmovImprenta(Character tmovImprenta) {
        this.tmovImprenta = tmovImprenta;
    }

    public Character getTmovTipomonto() {
        return tmovTipomonto;
    }

    public void setTmovTipomonto(Character tmovTipomonto) {
        this.tmovTipomonto = tmovTipomonto;
    }

    public Double getTmovMonto() {
        return tmovMonto;
    }

    public void setTmovMonto(Double tmovMonto) {
        this.tmovMonto = tmovMonto;
    }

    public Character getTmovTipoapldedporc() {
        return tmovTipoapldedporc;
    }

    public void setTmovTipoapldedporc(Character tmovTipoapldedporc) {
        this.tmovTipoapldedporc = tmovTipoapldedporc;
    }

    public Character getTmovEstado() {
        return tmovEstado;
    }

    public void setTmovEstado(Character tmovEstado) {
        this.tmovEstado = tmovEstado;
    }

    public Long getTmovVersion() {
        return tmovVersion;
    }

    public void setTmovVersion(Long tmovVersion) {
        this.tmovVersion = tmovVersion;
    }

    @XmlTransient
    public List<TipMovPlanilla> getPlamTipmovplanillaList() {
        return plamTipmovplanillaList;
    }

    public void setPlamTipmovplanillaList(List<TipMovPlanilla> plamTipmovplanillaList) {
        this.plamTipmovplanillaList = plamTipmovplanillaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tmovId != null ? tmovId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoMovimientos)) {
            return false;
        }
        TipoMovimientos other = (TipoMovimientos) object;
        if ((this.tmovId == null && other.tmovId != null) || (this.tmovId != null && !this.tmovId.equals(other.tmovId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.unaplanillaws.model.PlamTipomovimientos[ tmovId=" + tmovId + " ]";
    }
    
}
