package cr.ac.una.unaplanillaws.service;

import cr.ac.una.unaplanillaws.model.Empleado;
import cr.ac.una.unaplanillaws.model.EmpleadoDto;
import cr.ac.una.unaplanillaws.model.TipMovPlanilla;
import cr.ac.una.unaplanillaws.model.TipMovPlanillaDto;
import cr.ac.una.unaplanillaws.model.TipoMovimientos;
import cr.ac.una.unaplanillaws.model.TipoPlanilla;
import cr.ac.una.unaplanillaws.util.CodigoRespuesta;
import cr.ac.una.unaplanillaws.util.Respuesta;
import java.util.logging.Logger;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Dominique
 */
@Stateless
@LocalBean
public class TipMovPlanillaService {

    private static final Logger LOG = 
            Logger.getLogger(EmpleadoService.class.getName());

    @PersistenceContext(unitName = "UnaPlanillaWsPU")
    private EntityManager em;
    
    public Respuesta getTipoMovPlanilla(Long id) {
        try {
            Query qryTipoMovPlanillaService = em.createNamedQuery("TipMovPlanilla.findById", TipMovPlanilla.class);
            qryTipoMovPlanillaService.setParameter("id", id);

            TipMovPlanilla tipMovPlanilla = (TipMovPlanilla) qryTipoMovPlanillaService.getSingleResult();
            TipMovPlanillaDto tipoMovPlanillaDto = new TipMovPlanillaDto(tipMovPlanilla);
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "TipoMovPlanillaDto", tipoMovPlanillaDto);

        } catch (NoResultException ex) {
            return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No existe un tipo de planilla con el código ingresado.", "getTipoPlanilla NoResultException");
        } catch (NonUniqueResultException ex) {
            LOG.log(Level.SEVERE, "Ocurrio un error al consultar el tipo de planilla.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al consultar el tipo de planilla.", "getTipoPlanilla NonUniqueResultException");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Ocurrio un error al consultar el tipo de planilla.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al consultar el tipo de planilla.", "getTipoPlanilla " + ex.getMessage());
        }
    }

    public Respuesta guardarTipoMovPlanilla(TipMovPlanillaDto tipoMovPlanillaDto) {
        try {
            if (tipoMovPlanillaDto.getTipoMovimientoDto() == null || tipoMovPlanillaDto.getTipoMovimientoDto().getId() == null
                    || tipoMovPlanillaDto.getPlanillaDto() == null || tipoMovPlanillaDto.getPlanillaDto().getId() == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE, "Debe indicar el tipo de movimiento y el tipo de planilla.", "guardarTipoMovPlanilla InvalidRelationException");
            }
            TipoMovimientos tipoMovimiento = em.find(TipoMovimientos.class, tipoMovPlanillaDto.getTipoMovimientoDto().getId());
            TipoPlanilla tipoPlanilla = em.find(TipoPlanilla.class, tipoMovPlanillaDto.getPlanillaDto().getId());
            if (tipoMovimiento == null || tipoPlanilla == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No se encontró el tipo de movimiento o el tipo de planilla indicado.", "guardarTipoMovPlanilla NoResultException");
            }

            TipMovPlanilla tipMovPlanilla;
            if (tipoMovPlanillaDto.getId() != null && tipoMovPlanillaDto.getId() > 0) {
                tipMovPlanilla = em.find(TipMovPlanilla.class, tipoMovPlanillaDto.getId());
                if (tipMovPlanilla == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No se encontró el tipo de movimiento planilla a modificar.", "guardarTipoMovPlanilla NoResultException");
                }
                tipMovPlanilla.actualizar(tipoMovPlanillaDto);
                tipMovPlanilla.setTmovIdmov(tipoMovimiento);
                tipMovPlanilla.setTmpIdpla(tipoPlanilla);
                tipMovPlanilla = em.merge(tipMovPlanilla);
            } else {
                tipMovPlanilla = new TipMovPlanilla(tipoMovPlanillaDto);
                tipMovPlanilla.setTmovIdmov(tipoMovimiento);
                tipMovPlanilla.setTmpIdpla(tipoPlanilla);
                em.persist(tipMovPlanilla);
            }
            em.flush();
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "TipMovPlanilla", new TipMovPlanillaDto(tipMovPlanilla));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Ocurrio un error al guardar el tipo de movimiento planilla.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al guardar el tipo de movimiento planilla.", "guardarTipoMovPlanilla " + ex.getMessage());
        }
    }

    public Respuesta eliminarTipoMovPlanilla(Long id) {
        try {
            TipMovPlanilla tipMovPlanilla;
            if (id != null && id > 0) {
                tipMovPlanilla = em.find(TipMovPlanilla.class, id);
                if (tipMovPlanilla == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No se encrontró el tipo de movimiento planilla a eliminar.", "eliminarTipoMovPlanilla NoResultException");
                }
                em.remove(tipMovPlanilla);
            } else {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "Debe cargar el tipo de movimiento planilla a eliminar.", "eliminarTipoMovPlanilla NoResultException");
            }
            em.flush();
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "");
        } catch (Exception ex) {
            if (ex.getCause() != null && ex.getCause().getCause().getClass() == SQLIntegrityConstraintViolationException.class) {
                return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "No se puede eliminar el tipo de movimiento planilla porque tiene relaciones con otros registros.", "eliminarTipoMovPlanilla " + ex.getMessage());
            }
            LOG.log(Level.SEVERE, "Ocurrio un error al guardar el tipo de movimiento planilla.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al eliminar el tipo de movimiento planilla.", "eliminarTipoMovPlanilla " + ex.getMessage());
        }
    }

}
