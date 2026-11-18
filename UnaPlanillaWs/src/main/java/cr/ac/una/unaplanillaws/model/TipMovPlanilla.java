/*
 * CoreFx - JavaFX utility library
 * Author: Dominique
 * Date: 22 sept 2026
 * Package: cr.ac.una.unaplanillaws.model
 */
package cr.ac.una.unaplanillaws.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author Dominique
 */
@Entity
@Table(name = "PLAM_TIPMOVPLANILLA", schema = "UNA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TipMovPlanilla.findAll", query = "SELECT p FROM TipMovPlanilla p"),
    @NamedQuery(name = "TipMovPlanilla.findById", query = "SELECT p FROM TipMovPlanilla p WHERE p.tmpId = :id"),
    @NamedQuery(name = "TipMovPlanilla.findByTmpTipomonto", query = "SELECT p FROM TipMovPlanilla p WHERE p.tmpTipomonto = :tmpTipomonto"),
    @NamedQuery(name = "TipMovPlanilla.findByTmpMonto", query = "SELECT p FROM TipMovPlanilla p WHERE p.tmpMonto = :tmpMonto"),
    @NamedQuery(name = "TipMovPlanilla.findByTmpTipoapldedporc", query = "SELECT p FROM TipMovPlanilla p WHERE p.tmpTipoapldedporc = :tmpTipoapldedporc"),
    @NamedQuery(name = "TipMovPlanilla.findByTmpVersion", query = "SELECT p FROM TipMovPlanilla p WHERE p.tmpVersion = :tmpVersion")})
public class TipMovPlanilla implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @SequenceGenerator(name = "PLAM_TIPMOVPLANILLA_TMP_ID_GENERATOR", sequenceName = "una.PLAM_TIPMOVPLANILLA_SEQ01", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLAM_TIPMOVPLANILLA_TMP_ID_GENERATOR")
    @Basic(optional = false)
    @Column(name = "TMP_ID")
    private Long tmpId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TMP_TIPOMONTO")
    private Character tmpTipomonto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TMP_MONTO")
    private Double tmpMonto;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TMP_TIPOAPLDEDPORC")
    private Character tmpTipoapldedporc;
    @Version
    @Basic(optional = false)
    @Column(name = "TMP_VERSION")
    private Long tmpVersion;
    @JoinColumn(name = "TMOV_IDMOV", referencedColumnName = "TMOV_ID")
    @ManyToOne(optional = false)
    private TipoMovimientos tmovIdmov;
    @JoinColumn(name = "TMP_IDPLA", referencedColumnName = "TPLA_ID")
    @ManyToOne(optional = false)
    private TipoPlanilla tmpIdpla;

    public TipMovPlanilla() {
    }

    public TipMovPlanilla(TipMovPlanillaDto tipoMovPlanillaDto) {
        this.tmpId = tipoMovPlanillaDto.getId();
        actualizar(tipoMovPlanillaDto);
    }

    public void actualizar(TipMovPlanillaDto tipoMovPlanillaDto) {
        this.tmpTipomonto = tipoMovPlanillaDto.getTipoMonto();
        this.tmpMonto = tipoMovPlanillaDto.getMonto();
        this.tmpTipoapldedporc = tipoMovPlanillaDto.getTipoAplDeDPorc();
        this.tmpVersion = tipoMovPlanillaDto.getVersion();
    }

    public Long getTmpId() {
        return tmpId;
    }

    public void setTmpId(Long tmpId) {
        this.tmpId = tmpId;
    }

    public Character getTmpTipomonto() {
        return tmpTipomonto;
    }

    public void setTmpTipomonto(Character tmpTipomonto) {
        this.tmpTipomonto = tmpTipomonto;
    }

    public Double getTmpMonto() {
        return tmpMonto;
    }

    public void setTmpMonto(Double tmpMonto) {
        this.tmpMonto = tmpMonto;
    }

    public Character getTmpTipoapldedporc() {
        return tmpTipoapldedporc;
    }

    public void setTmpTipoapldedporc(Character tmpTipoapldedporc) {
        this.tmpTipoapldedporc = tmpTipoapldedporc;
    }

    public Long getTmpVersion() {
        return tmpVersion;
    }

    public void setTmpVersion(Long tmpVersion) {
        this.tmpVersion = tmpVersion;
    }

    public TipoMovimientos getTmovIdmov() {
        return tmovIdmov;
    }

    public void setTmovIdmov(TipoMovimientos tmovIdmov) {
        this.tmovIdmov = tmovIdmov;
    }

    public TipoPlanilla getTmpIdpla() {
        return tmpIdpla;
    }

    public void setTmpIdpla(TipoPlanilla tmpIdpla) {
        this.tmpIdpla = tmpIdpla;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tmpId != null ? tmpId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipMovPlanilla)) {
            return false;
        }
        TipMovPlanilla other = (TipMovPlanilla) object;
        if ((this.tmpId == null && other.tmpId != null) || (this.tmpId != null && !this.tmpId.equals(other.tmpId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "cr.ac.una.unaplanillaws.model.PlamTipmovplanilla[ tmpId=" + tmpId + " ]";
    }
    
}
