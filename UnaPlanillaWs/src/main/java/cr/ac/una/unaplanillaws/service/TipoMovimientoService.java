package cr.ac.una.unaplanillaws.service;

import cr.ac.una.unaplanillaws.model.Empleado;
import cr.ac.una.unaplanillaws.model.TipMovPlanilla;
import cr.ac.una.unaplanillaws.model.TipMovPlanillaDto;
import cr.ac.una.unaplanillaws.model.TipoPlanilla;
import cr.ac.una.unaplanillaws.model.TipoMovimientos;
import cr.ac.una.unaplanillaws.model.TipoPlanillaDto;
import cr.ac.una.unaplanillaws.model.TipoMovimientosDto;
import cr.ac.una.unaplanillaws.util.CodigoRespuesta;
import cr.ac.una.unaplanillaws.util.Respuesta;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Logger;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 *
 * @author Dominique
 */
@Stateless
@LocalBean
public class TipoMovimientoService {

    private static final Logger LOG = 
            Logger.getLogger(TipoPlanillaService.class.getName());

    @PersistenceContext(unitName = "UnaPlanillaWsPU")
    private EntityManager em;

    public Respuesta getTipoMovimientoById(Long id) {
        try {
            Query qryTipoMovimienot = em.createNamedQuery("TipoMovimientos.findById", TipoMovimientos.class);
            qryTipoMovimienot.setParameter("id", id);
            TipoMovimientos tipoMovimiento = (TipoMovimientos) qryTipoMovimienot.getSingleResult();
            TipoMovimientosDto tipoMovimientoDto = new TipoMovimientosDto(tipoMovimiento);
            for (TipMovPlanilla tipoMovPlanilla : tipoMovimiento.getPlamTipmovplanillaList()) {
                tipoMovimientoDto.getTipMovPlanillaList().add(sinTipoMovimiento(new TipMovPlanillaDto(tipoMovPlanilla)));
            }
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "TipoMovimiento", tipoMovimientoDto);
        } catch (NoResultException ex) {
            LOG.log(Level.WARNING, "No se encontró el tipo de movimiento con el id: {0}", id);
            return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No se encontró el tipo de movimiento con el id: " + id, "getTipoMovimientoById NoResultException");
        } catch (NonUniqueResultException ex) {
            LOG.log(Level.SEVERE, "Se encontró más de un tipo de movimiento con el id: {0}", id);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Se encontró más de un tipo de movimiento con el id: " + id, "getTipoMovimientoById NonUniqueResultException");
        }
        catch (Exception ex) {
            LOG.severe("Error al consultar el tipo de movimiento por id: " + ex.getMessage());
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,"Error al consultar el tipo de movimiento por id: " + ex.getMessage(), "getTipoMovimientoById Exception");
        }
    }

    public Respuesta getTiposMovimiento(String descripcion) {
        try {
            Query qryTiposMovimiento = em.createNamedQuery("TipoMovimientos.findByDescripcion", TipoMovimientos.class);
            qryTiposMovimiento.setParameter("descripcion", descripcion);
            List<TipoMovimientos> tiposMovimiento = (List<TipoMovimientos>) qryTiposMovimiento.getResultList();
            List<TipoMovimientosDto> tiposMovimientoDto = new ArrayList<>();
            for (TipoMovimientos tipoMovimiento : tiposMovimiento) {
                tiposMovimientoDto.add(new TipoMovimientosDto(tipoMovimiento));
            }
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "TiposMovimiento", tiposMovimientoDto);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al consultar los tipos de movimiento.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Error al consultar los tipos de movimiento.", "getTiposMovimiento " + ex.getMessage());
        }
    }

    public Respuesta guardarTipoMovimiento(TipoMovimientosDto tipoMovimientoDto) {
        try {
            TipoMovimientos tipoMovimiento;

            if (tipoMovimientoDto.getId() != null && tipoMovimientoDto.getId() > 0) {
                tipoMovimiento = em.find(TipoMovimientos.class, tipoMovimientoDto.getId());
                if (tipoMovimiento == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No se encontró el tipo de movimiento con el id: " + tipoMovimientoDto.getId(), "guardarTipoMovimiento NoResultException");
                }
                tipoMovimiento.actualizar(tipoMovimientoDto);
            } else {
                tipoMovimiento = new TipoMovimientos(tipoMovimientoDto);
                tipoMovimiento.setPlamTipmovplanillaList(new ArrayList<>());
                em.persist(tipoMovimiento);
            }

            // Si el cliente no envía la lista, las relaciones existentes no se tocan
            if (tipoMovimientoDto.getTipMovPlanillaList() != null) {
                List<TipMovPlanillaDto> relacionesDto = tipoMovimientoDto.getTipMovPlanillaList();
                List<TipMovPlanilla> relacionesActuales = tipoMovimiento.getPlamTipmovplanillaList();
                Set<Long> idsRecibidos = new HashSet<>();
                for (TipMovPlanillaDto tipMovPlanillaDto : relacionesDto) {
                    if (tipMovPlanillaDto.getId() != null && tipMovPlanillaDto.getId() > 0) {
                        idsRecibidos.add(tipMovPlanillaDto.getId());
                    }
                }

                // Se eliminan primero las relaciones que ya no vienen, para no tocar las nuevas
                for (TipMovPlanilla relacionActual : new ArrayList<>(relacionesActuales)) {
                    if (relacionActual.getTmpId() != null
                            && !idsRecibidos.contains(relacionActual.getTmpId())) {
                        em.remove(relacionActual);
                        relacionesActuales.remove(relacionActual);
                    }
                }

                for (TipMovPlanillaDto tipMovPlanillaDto : relacionesDto) {
                    if (tipMovPlanillaDto.getPlanillaDto() == null || tipMovPlanillaDto.getPlanillaDto().getId() == null) {
                        return new Respuesta(false, CodigoRespuesta.ERROR_CLIENTE,
                                "Debe indicar el tipo de planilla de cada tipo de movimiento de planilla.",
                                "guardarTipoMovimiento InvalidRelationException");
                    }
                    TipoPlanilla tipoPlanilla = em.find(TipoPlanilla.class, tipMovPlanillaDto.getPlanillaDto().getId());
                    if (tipoPlanilla == null) {
                        return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                                "No se encontró el tipo de planilla con el id: " + tipMovPlanillaDto.getPlanillaDto().getId(),
                                "guardarTipoMovimiento NoResultException");
                    }

                    TipMovPlanilla tipMovPlanilla;
                    if (tipMovPlanillaDto.getId() != null && tipMovPlanillaDto.getId() > 0) {
                        tipMovPlanilla = em.find(TipMovPlanilla.class, tipMovPlanillaDto.getId());
                        if (tipMovPlanilla == null) {
                            return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO,
                                    "No se encontró el tipo de movimiento de planilla con el id: " + tipMovPlanillaDto.getId(),
                                    "guardarTipoMovimiento NoResultException");
                        }
                        if (tipMovPlanilla.getTmovIdmov() != null
                                && !tipoMovimiento.equals(tipMovPlanilla.getTmovIdmov())) {
                            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO,
                                    "El tipo de movimiento de planilla no pertenece al tipo de movimiento indicado.",
                                    "guardarTipoMovimiento InvalidRelationException");
                        }
                        tipMovPlanilla.actualizar(tipMovPlanillaDto);
                        tipMovPlanilla.setTmovIdmov(tipoMovimiento);
                        tipMovPlanilla.setTmpIdpla(tipoPlanilla);
                    } else {
                        tipMovPlanilla = new TipMovPlanilla(tipMovPlanillaDto);
                        tipMovPlanilla.setTmovIdmov(tipoMovimiento);
                        tipMovPlanilla.setTmpIdpla(tipoPlanilla);
                        em.persist(tipMovPlanilla);
                        relacionesActuales.add(tipMovPlanilla);
                    }
                }
            }

            tipoMovimiento = em.merge(tipoMovimiento);
            em.flush();
            TipoMovimientosDto tipoMovimientoGuardado = new TipoMovimientosDto(tipoMovimiento);
            for (TipMovPlanilla tipMovPlanilla : tipoMovimiento.getPlamTipmovplanillaList()) {
                tipoMovimientoGuardado.getTipMovPlanillaList().add(sinTipoMovimiento(new TipMovPlanillaDto(tipMovPlanilla)));
            }
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "TipoMovimiento", tipoMovimientoGuardado);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error al guardar el tipo de movimiento: {0}", ex.getMessage());
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Error al guardar el tipo de movimiento: " + ex.getMessage(), "guardarTipoMovimiento Exception");
        }
    }

    public Respuesta eliminarTipoMovimiento(Long id) {
        try {
            TipoMovimientos tipoMovimiento;
            if(id != null && id > 0){
               tipoMovimiento = em.find(TipoMovimientos.class, id);
               if (tipoMovimiento == null) {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No se encontró el tipo de movimiento con el id: " + id, "eliminarTipoMovimiento NoResultException");
                }
                em.remove(tipoMovimiento);
            } else {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "El id proporcionado no es válido: " + id, "eliminarTipoMovimiento InvalidIdException");
            }
            em.flush();
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "");
        } catch (Exception ex) {
            if (esViolacionIntegridad(ex)) {
                return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "No se puede eliminar el tipo de movimiento porque tiene relaciones con otros registros.", "eliminarTipoMovimiento " + ex.getMessage());
            }
            LOG.log(Level.SEVERE, "Error al eliminar el tipo de movimiento: {0}", ex.getMessage());
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Error al eliminar el tipo de movimiento: " + ex.getMessage(), "eliminarTipoMovimiento Exception");
        }
    }

    // No se devuelve el tipo de movimiento dentro de cada relación:
    // JSON-B compara con equals (por id) y lo toma como referencia recursiva
    private TipMovPlanillaDto sinTipoMovimiento(TipMovPlanillaDto tipMovPlanillaDto) {
        tipMovPlanillaDto.setTipoMovimientoDto(null);
        return tipMovPlanillaDto;
    }

    private boolean esViolacionIntegridad(Throwable ex) {
        for (Throwable causa = ex; causa != null; causa = causa.getCause()) {
            if (causa instanceof SQLIntegrityConstraintViolationException) {
                return true;
            }
        }
        return false;
    }

}
