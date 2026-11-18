/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.ac.una.unaplanillaws.service;

import cr.ac.una.unaplanillaws.model.Empleado;
import cr.ac.una.unaplanillaws.model.EmpleadoDto;
import cr.ac.una.unaplanillaws.model.TipMovPlanilla;
import cr.ac.una.unaplanillaws.model.TipMovPlanillaDto;
import cr.ac.una.unaplanillaws.model.TipoMovimientos;
import cr.ac.una.unaplanillaws.model.TipoPlanilla;
import cr.ac.una.unaplanillaws.model.TipoPlanillaDto;
import cr.ac.una.unaplanillaws.util.CodigoRespuesta;
import cr.ac.una.unaplanillaws.util.Respuesta;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio EJB para la entidad TipoPlanilla (Patrón Maestro-Detalle Completo).
 * 
 * CONCEPTOS CLAVE PARA EXAMEN / REPOSICIÓN:
 * 1. @Stateless: EJB sin estado. El servidor gestiona el pool de instancias.
 *    - Transacciones automáticas (CMT - Container-Managed Transactions): Cada método
 *      público se ejecuta en una transacción (REQUIRED por defecto).
 * 2. @PersistenceContext: Inyección del EntityManager vinculado a "UnaPlanillaWsPU".
 * 3. Gestión Maestro-Detalle y Relaciones:
 *    - Many-to-Many (M:N): TipoPlanilla <-> Empleado. Sincronización bidireccional de listas.
 *    - One-to-Many (1:N): TipoPlanilla -> TipMovPlanilla. Persistencia y borrado explícito de hijos.
 * 4. Prevención de Recursividad Cíclica JSON-B:
 *    - Al serializar entidades bidireccionales, romper la referencia circular (ej. setPlanillaDto(null))
 *      para evitar StackOverflowError o ciclos infinitos en JSON.
 * 5. Manejo de Integridad Referencial:
 *    - Captura de SQLIntegrityConstraintViolationException al intentar eliminar registros con hijos (FKs).
 * 
 * @author Carlos Carranza / Luis Elizondo (Anotaciones de Estudio)
 */
@Stateless
@LocalBean
public class TipoPlanillaService {
    
    private static final Logger LOG = 
            Logger.getLogger(TipoPlanillaService.class.getName());

    @PersistenceContext(unitName = "UnaPlanillaWsPU")
    private EntityManager em;

    /**
     * Consulta un TipoPlanilla por ID e hidrata sus colecciones hijas (Empleados y Movimientos).
     * 
     * NOTA DE ESTUDIO (Evitar Recursión Cíclica en REST/JSON-B):
     * - Si TipMovPlanillaDto contiene TipoPlanillaDto, y este contiene a su vez la lista de TipMovPlanillaDto,
     *   el serializador entra en un bucle infinito.
     * - Solución: Se limpia la referencia hacia el padre en el DTO hijo: tipMovPlanillaDto.setPlanillaDto(null).
     */
    public Respuesta getTipoPlanilla(Long id) {
        try {
            Query qryTipoPlanilla = em.createNamedQuery("TipoPlanilla.findById", TipoPlanilla.class);
            qryTipoPlanilla.setParameter("id", id);

            TipoPlanilla tipoPlanilla = (TipoPlanilla) qryTipoPlanilla.getSingleResult();
            TipoPlanillaDto tipoPlanillaDto = new TipoPlanillaDto(tipoPlanilla);
            
            // Cargar empleados asociados (Relación Many-to-Many)
            for (Empleado emp : tipoPlanilla.getEmpleados()) {
                tipoPlanillaDto.getEmpleados().add(new EmpleadoDto(emp));
            }
            
            // Cargar movimientos asociados (Relación One-to-Many)
            for (TipMovPlanilla tipMovPlanilla : tipoPlanilla.getPlamTipmovplanillaCollection()) {
                TipMovPlanillaDto tipMovPlanillaDto = new TipMovPlanillaDto(tipMovPlanilla);
                // Rompe ciclo de referencia para serialización JSON-B:
                tipMovPlanillaDto.setPlanillaDto(null);
                tipoPlanillaDto.getTiposMovimiento().add(tipMovPlanillaDto);
            }
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "TipoPlanilla", tipoPlanillaDto);

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

    public Respuesta getTiposPlanilla(String codigo, String descripcion, String plaxmes, String idemp, String cedula) {
        try {
            Query query;
            if ("%%".equals(idemp) && "%%".equals(cedula)) {
                query = em.createNamedQuery("TipoPlanilla.findByCodigoDesPlaxMes", TipoPlanilla.class);
                query.setParameter("codigo", codigo);
                query.setParameter("descripcion", descripcion);
                query.setParameter("planillasMes", plaxmes);
            } else {
                query = em.createNamedQuery("TipoPlanilla.findByCodigoDesPlaxMesIdEmpCedula", TipoPlanilla.class);
                query.setParameter("codigo", codigo);
                query.setParameter("descripcion", descripcion);
                query.setParameter("planillasMes", plaxmes);
                query.setParameter("idEmpleado", idemp);
                query.setParameter("cedula", cedula);
            }
            List<TipoPlanilla> tipoPlanillas = (List<TipoPlanilla>) query.getResultList();
            List<TipoPlanillaDto> tipoPlanillasDto = new ArrayList<>();
            for (TipoPlanilla pla : tipoPlanillas) {
                tipoPlanillasDto.add(new TipoPlanillaDto(pla));
            }
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "TiposPlanilla", tipoPlanillasDto);
        } catch (NoResultException ex) {
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "No existen tipos de planillas con los criterios ingresados.", "getTipoPlanillas NoResultException");
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error obteniendo tipos de planillas.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Error obteniendo tipos de planillas.", "getTipoPlanillas " + ex.getMessage());
        }
    }

    /**
     * Guarda o actualiza un TipoPlanilla sincronizando sus relaciones Maestro-Detalle.
     * 
     * PASOS DE SINCRONIZACIÓN MAESTRO-DETALLE EN JPA (Pregunta típica de defensa/examen):
     * 1. Si id > 0 -> MODO MODIFICACIÓN:
     *    a. em.find(): Se carga la entidad administrada (managed entity) desde la BD.
     *    b. actualizar(dto): Se copian atributos escalares simples (código, descripción, etc.).
     *    c. Empleados Eliminados (M:N): Se retiran los empleados desasociados de la lista.
     *    d. Empleados Agregados (M:N): Si un empleado fue marcado como 'modificado', se busca y
     *       se añade bidireccionalmente (empleado.getTiposPlanilla().add + tipoPlanilla.getEmpleados().add).
     *    e. Movimientos Eliminados (1:N): Se eliminan tanto de la colección del padre como
     *       directamente del contexto de persistencia mediante em.remove(tipMovPlanilla).
     *    f. Movimientos Nuevos (1:N): Se instancia TipMovPlanilla, se asocia el padre (setTmpIdpla)
     *       y el tipo de movimiento maestro (setTmovIdmov), y se invoca em.persist().
     *    g. em.merge(tipoPlanilla): Se unifican todos los cambios en el contexto de persistencia.
     * 2. Si id == null -> MODO CREACIÓN:
     *    a. Se instancia la nueva entidad con new TipoPlanilla(dto).
     *    b. em.persist(tipoPlanilla): Pasa de estado New a Managed.
     * 3. em.flush(): Fuerza el envío de todas las sentencias SQL (INSERT, UPDATE, DELETE) pendientes a la BD
     *    dentro de la transacción activa antes del commit.
     */
    public Respuesta guardarTipoPlanilla(TipoPlanillaDto tipoPlanillaDto) {
        try {
            TipoPlanilla tipoPlanilla;
            if (tipoPlanillaDto.getId() != null && tipoPlanillaDto.getId() > 0) {
                tipoPlanilla = em.find(TipoPlanilla.class, tipoPlanillaDto.getId());
                if (tipoPlanilla == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No se encontró el tipo de planilla a modificar.", "guardarTipoPlanilla NoResultException");
                }
                tipoPlanilla.actualizar(tipoPlanillaDto);
                
                // 1. Remover empleados desasociados (Relación N:M)
                for (EmpleadoDto emp : tipoPlanillaDto.getEmpleadosEliminados()) {
                    tipoPlanilla.getEmpleados().remove(new Empleado(emp.getId()));
                }
                
                // 2. Asociar nuevos empleados a la planilla (Relación N:M)
                if (!tipoPlanillaDto.getEmpleados().isEmpty()) {
                    for (EmpleadoDto emp : tipoPlanillaDto.getEmpleados()) {
                        if (emp.getModificado()) {
                            Empleado empleado = em.find(Empleado.class, emp.getId());
                            empleado.getTiposPlanilla().add(tipoPlanilla);
                            tipoPlanilla.getEmpleados().add(empleado);
                        }
                    }
                }
                
                // 3. Eliminar movimientos de planilla huérfanos (Relación 1:N)
                for (TipMovPlanillaDto tmp : tipoPlanillaDto.getTiposMovimientoEliminados()) {
                    TipMovPlanilla tipMovPlanilla = em.find(TipMovPlanilla.class, tmp.getId());
                    if (tipMovPlanilla != null) {
                        tipoPlanilla.getPlamTipmovplanillaCollection().remove(tipMovPlanilla);
                        em.remove(tipMovPlanilla);
                    }
                }
                
                // 4. Agregar nuevos movimientos de planilla (Relación 1:N)
                for (TipMovPlanillaDto tmp : tipoPlanillaDto.getTiposMovimiento()) {
                    if (Boolean.TRUE.equals(tmp.getModificado())) {
                        TipMovPlanilla tipMovPlanilla = new TipMovPlanilla(tmp);
                        tipMovPlanilla.setTmovIdmov(em.find(TipoMovimientos.class, tmp.getTipoMovimientoDto().getId()));
                        tipMovPlanilla.setTmpIdpla(tipoPlanilla);
                        em.persist(tipMovPlanilla);
                        tipoPlanilla.getPlamTipmovplanillaCollection().add(tipMovPlanilla);
                    }
                }
                tipoPlanilla = em.merge(tipoPlanilla);
            } else {
                tipoPlanilla = new TipoPlanilla(tipoPlanillaDto);
                em.persist(tipoPlanilla);
            }
            em.flush();
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "TipoPlanilla", new TipoPlanillaDto(tipoPlanilla));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Ocurrio un error al guardar el tipo de planilla.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al guardar el tipo de planilla.", "guardarTipoPlanilla " + ex.getMessage());
        }
    }

    /**
     * Elimina un TipoPlanilla de la base de datos.
     * 
     * MANEJO DE INTEGRIDAD REFERENCIAL (FK Constraints):
     * - Si existen registros hijos en tablas vinculadas (ej. planillas liquidadas o movimientos sin cascada),
     *   la base de datos lanza SQLIntegrityConstraintViolationException.
     * - Se atrapa en la causa interna de la excepción (ex.getCause().getCause()) para retornar un mensaje
     *   amigable de negocio al cliente en lugar de un error 500 no controlado.
     */
    public Respuesta eliminarTipoPlanilla(Long id) {
        try {
            TipoPlanilla tipoPlanilla;
            if (id != null && id > 0) {
                tipoPlanilla = em.find(TipoPlanilla.class, id);
                if (tipoPlanilla == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No se encrontró el tipo de planilla a eliminar.", "eliminarTipoPlanilla NoResultException");
                }
                em.remove(tipoPlanilla);
            } else {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "Debe cargar el tipo de planilla a eliminar.", "eliminarTipoPlanilla NoResultException");
            }
            em.flush();
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "");
        } catch (Exception ex) {
            if (ex.getCause() != null && ex.getCause().getCause().getClass() == SQLIntegrityConstraintViolationException.class) {
                return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "No se puede eliminar el tipo de planilla porque tiene relaciones con otros registros.", "eliminarTipoPlanilla " + ex.getMessage());
            }
            LOG.log(Level.SEVERE, "Ocurrio un error al guardar el tipo de planilla.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al eliminar el tipo de planilla.", "eliminarTipoPlanilla " + ex.getMessage());
        }
    }

}
