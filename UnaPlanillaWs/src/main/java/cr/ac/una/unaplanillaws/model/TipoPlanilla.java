/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.ac.una.unaplanillaws.model;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlTransient;
import java.math.BigInteger;
import java.util.Collection;

/**
 * Entidad JPA representativa del Maestro en Maestro-Detalle y dueña de relaciones complejas.
 * 
 * GUÍA DE ESTUDIO - RELACIONES JPA AVANZADAS (Pregunta fija de examen):
 * 1. @JoinTable (Relación Muchos a Muchos - Dueño de la relación):
 *    - @ManyToMany(fetch = FetchType.LAZY): La entidad TipoPlanilla es dueña de la relación N:M.
 *    - name = "PLAM_EMPLEADOSPLANILLA": Tabla intermedia o pivote en la BD.
 *    - joinColumns: Llave foránea que apunta a la tabla actual (TPLA_ID -> EXP_IDTPLA).
 *    - inverseJoinColumns: Llave foránea que apunta a la entidad asociada (EMP_ID -> EXP_IDEMP).
 * 2. @OneToMany(cascade = CascadeType.ALL, mappedBy = "tmpIdpla"):
 *    - Relación Uno a Muchos con TipMovPlanilla.
 *    - mappedBy indica que el campo de la llave foránea se gestiona en TipMovPlanilla.
 *    - CascadeType.ALL propaga operaciones (persist, merge, remove) a los hijos si se configuran.
 * 3. Uso de funciones SQL dentro de JPQL:
 *    - FUNCTION('TO_CHAR', t.planillaPorMes): Permite invocar funciones nativas del motor SQL (Oracle)
 *      directamente dentro de las NamedQueries de JPQL.
 * 
 * @author cbcar / Luis Elizondo (Anotaciones de Estudio)
 */
@Entity
@Table(name = "PLAM_TIPOPLANILLAS", schema = "UNA")
@NamedQueries({
    @NamedQuery(name = "TipoPlanilla.findAll", query = "SELECT t FROM TipoPlanilla t"),
    @NamedQuery(name = "TipoPlanilla.findById", query = "SELECT t FROM TipoPlanilla t WHERE t.id = :id"),
    @NamedQuery(name = "TipoPlanilla.findByCodigoDesPlaxMes", query = "SELECT t FROM TipoPlanilla t WHERE UPPER(t.codigo) like :codigo and UPPER(t.descripcion) like :descripcion and FUNCTION('TO_CHAR', t.planillaPorMes) like :planillasMes"),
    @NamedQuery(name = "TipoPlanilla.findByCodigoDesPlaxMesIdEmpCedula", query = "SELECT t FROM TipoPlanilla t JOIN t.empleados e WHERE UPPER(t.codigo) like :codigo and UPPER(t.descripcion) like :descripcion and FUNCTION('TO_CHAR', t.planillaPorMes) like :planillasMes and FUNCTION('TO_CHAR', e.id) like :idEmpleado and UPPER(e.cedula) like :cedula")
})
public class TipoPlanilla implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "TPLA_PLAXMES")
    private Integer planillaPorMes;
    @Column(name = "TPLA_ANOULTPLA")
    private BigInteger tplaAnoultpla;
    @Column(name = "TPLA_MESULTPLA")
    private BigInteger tplaMesultpla;
    @Column(name = "TPLA_NUMULTPLA")
    private BigInteger tplaNumultpla;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TPLA_VERSION")
    private BigInteger tplaVersion;
    
    // Relación 1:N con la tabla de movimientos de planilla
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tmpIdpla")
    private Collection<TipMovPlanilla> plamTipmovplanillaCollection;

    private static final long serialVersionUID = 1L;
    
    @Id
    @SequenceGenerator(name = "PLAM_TIPOPLANILLAS_TPLA_ID_GENERATOR", sequenceName = "una.PLAM_TIPOPLANILLAS_SEQ01", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PLAM_TIPOPLANILLAS_TPLA_ID_GENERATOR")
    @Basic(optional = false)
    @Column(name = "TPLA_ID")
    private Long id;
    @Basic(optional = false)
    @Column(name = "TPLA_CODIGO")
    private String codigo;
    @Basic(optional = false)
    @Column(name = "TPLA_DESCRIPCION")
    private String descripcion;
    @Basic(optional = false)
    @Column(name = "TPLA_ESTADO")
    private String estado;
    
    // Mapeo físico de la tabla asociativa Muchos a Muchos (PLAM_EMPLEADOSPLANILLA)
    @JoinTable(name = "PLAM_EMPLEADOSPLANILLA", joinColumns = {
        @JoinColumn(name = "EXP_IDTPLA", referencedColumnName = "TPLA_ID")}, inverseJoinColumns = {
        @JoinColumn(name = "EXP_IDEMP", referencedColumnName = "EMP_ID")})
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Empleado> empleados;

    public TipoPlanilla() {
    }

    public TipoPlanilla(TipoPlanillaDto tipoPlanillaDto) {
        this.id = tipoPlanillaDto.getId();
        actualizar(tipoPlanillaDto);
    }

    public void actualizar(TipoPlanillaDto tipoPlanillaDto) {
        this.codigo = tipoPlanillaDto.getCodigo();
        this.descripcion = tipoPlanillaDto.getDescripcion();
        this.estado = tipoPlanillaDto.getActivo() ? "A" : "I";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Empleado> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<Empleado> empleados) {
        this.empleados = empleados;
    }
    
    public Integer getPlanillaPorMes() {
        return planillaPorMes;
    }
    
    public void setPlanillaPorMes(Integer planillaPorMes) {
        this.planillaPorMes = planillaPorMes;
    }
    
    public BigInteger getTplaAnoultpla() {
        return tplaAnoultpla;
    }
    
    public void setTplaAnoultpla(BigInteger tplaAnoultpla) {
        this.tplaAnoultpla = tplaAnoultpla;
    }
    
    public BigInteger getTplaMesultpla() {
        return tplaMesultpla;
    }
    
    public void setTplaMesultpla(BigInteger tplaMesultpla) {
        this.tplaMesultpla = tplaMesultpla;
    }
    
    public BigInteger getTplaNumultpla() {
        return tplaNumultpla;
    }
    
    public void setTplaNumultpla(BigInteger tplaNumultpla) {
        this.tplaNumultpla = tplaNumultpla;
    }
    
    public BigInteger getTplaVersion() {
        return tplaVersion;
    }
    
    public void setTplaVersion(BigInteger tplaVersion) {
        this.tplaVersion = tplaVersion;
    }
    
    @XmlTransient
    public Collection<TipMovPlanilla> getPlamTipmovplanillaCollection() {
        return plamTipmovplanillaCollection;
    }
    
    public void setPlamTipmovplanillaCollection(Collection<TipMovPlanilla> plamTipmovplanillaCollection) {
        this.plamTipmovplanillaCollection = plamTipmovplanillaCollection;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TipoPlanilla)) {
            return false;
        }
        TipoPlanilla other = (TipoPlanilla) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "cr.ac.una.unaplanilla.model.TipoPlanilla[ Id=" + id + " ]";
    }

}
