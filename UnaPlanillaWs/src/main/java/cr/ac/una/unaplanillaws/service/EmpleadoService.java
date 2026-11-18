/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cr.ac.una.unaplanillaws.service;

import java.util.List;
import java.util.logging.Level;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Query;
import cr.ac.una.unaplanillaws.model.Empleado;
import cr.ac.una.unaplanillaws.model.EmpleadoDto;
import cr.ac.una.unaplanillaws.util.CodigoRespuesta;
import cr.ac.una.unaplanillaws.util.Respuesta;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.PersistenceContext;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author Carlos
 */
@Stateless
@LocalBean
public class EmpleadoService {
    
    private static final Logger LOG = 
            Logger.getLogger(EmpleadoService.class.getName());

    @PersistenceContext(unitName = "UnaPlanillaWsPU")
    private EntityManager em;

    public Respuesta validarUsuario(String usuario, String clave) {
        try {
            Query qryActividad = em.createNamedQuery("Empleado.findByUsuClave", Empleado.class);
            qryActividad.setParameter("usuario", usuario);
            qryActividad.setParameter("clave", clave);

            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Empleado", new EmpleadoDto((Empleado) qryActividad.getSingleResult()));

        } catch (NoResultException ex) {
            return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No existe un usuario con las credenciales ingresadas.", "validarUsuario NoResultException");
        } catch (NonUniqueResultException ex) {
            LOG.log(Level.SEVERE, "Ocurrio un error al consultar el usuario.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al consultar el usuario.", "validarUsuario NonUniqueResultException");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Ocurrio un error al consultar el usuario.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al consultar el usuario.", "validarUsuario " + ex.getMessage());
        }
    }

    public Respuesta getEmpleado(Long id) {
        try {
            Query qryEmpleado = em.createNamedQuery("Empleado.findByEmpId", Empleado.class);
            qryEmpleado.setParameter("id", id);

            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Empleado", new EmpleadoDto((Empleado) qryEmpleado.getSingleResult()));

        } catch (NoResultException ex) {
            return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No existe un empleado con el código ingresado.", "getEmpleado NoResultException");
        } catch (NonUniqueResultException ex) {
            LOG.log(Level.SEVERE, "Ocurrio un error al consultar el empleado.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al consultar el empleado.", "getEmpleado NonUniqueResultException");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Ocurrio un error al consultar el empleado.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al consultar el empleado.", "getEmpleado " + ex.getMessage());
        }
    }

    public Respuesta getEmpleados(String cedula, String nombre, String pApellido, String sApellido) {
        try {
            Query query = em.createNamedQuery("Empleado.findByCedulaNombreApellidos",Empleado.class);
            query.setParameter("nombre", nombre);
            query.setParameter("cedula", cedula);
            query.setParameter("pApellido", pApellido);
            query.setParameter("sApellido", sApellido);
            List<Empleado> empleados = (List<Empleado>) query.getResultList();
            List<EmpleadoDto> empleadosDto = new ArrayList<>();
            for (Empleado empleado : empleados) {
                empleadosDto.add(new EmpleadoDto(empleado));
            }

            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Empleados", empleadosDto);

        } catch (NoResultException ex) {
            return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No existen empleados con los criterios ingresados.", "getEmpleados NoResultException");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Ocurrio un error al consultar el empleado.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al consultar el empleado.", "getEmpleado " + ex.getMessage());
        }
    }

    // Ciclo de vida JPA: UPDATE (merge) si ya tiene ID, o INSERT (persist) si es nuevo
    public Respuesta guardarEmpleado(EmpleadoDto empleadoDto) {
        try {
            Empleado empleado;
            if (empleadoDto.getId() != null && empleadoDto.getId() > 0) {
                // Modificación: buscar entidad existente, actualizar campos y llamar a merge()
                empleado = em.find(Empleado.class, empleadoDto.getId());
                if (empleado == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No se encrontró el empleado a modificar.", "guardarEmpleado NoResultException");
                }
                empleado.actualizar(empleadoDto);
                empleado = em.merge(empleado); // Sincroniza cambios (UPDATE)
            } else {
                // Inserción: crear nueva entidad y llamar a persist()
                empleado = new Empleado(empleadoDto);
                em.persist(empleado); // Registra nueva entidad administrada (INSERT)
            }
            em.flush(); // Fuerza la ejecución inmediata de la sentencia SQL en BD
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "", "Empleado", new EmpleadoDto(empleado));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Ocurrio un error al guardar el empleado.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al guardar el empleado.", "guardarEmpleado " + ex.getMessage());
        }
    }
    

    // Eliminación JPA: buscar entidad y llamar a remove()
    public Respuesta eliminarEmpleado(Long id) {
        try {
            Empleado empleado;
            if (id != null && id > 0) {
                empleado = em.find(Empleado.class, id); // Debe estar en estado administrado para poder removerse
                if (empleado == null) {
                    return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "No se encrontró el empleado a eliminar.", "eliminarEmpleado NoResultException");
                }
                em.remove(empleado); // Marca la entidad para eliminación física (DELETE)
            } else {
                return new Respuesta(false, CodigoRespuesta.ERROR_NOENCONTRADO, "Debe cargar el empleado a eliminar.", "eliminarEmpleado NoResultException");
            }
            em.flush(); // Ejecuta el DELETE en la BD
            return new Respuesta(true, CodigoRespuesta.CORRECTO, "", "");
        } catch (Exception ex) {
            if (ex.getCause() != null && ex.getCause().getCause().getClass() == SQLIntegrityConstraintViolationException.class) {
                return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "No se puede eliminar el empleado porque tiene relaciones con otros registros.", "eliminarEmpleado " + ex.getMessage());
            }
            LOG.log(Level.SEVERE, "Ocurrio un error al guardar el empleado.", ex);
            return new Respuesta(false, CodigoRespuesta.ERROR_INTERNO, "Ocurrio un error al eliminar el empleado.", "eliminarEmpleado " + ex.getMessage());
        }
    }

}
