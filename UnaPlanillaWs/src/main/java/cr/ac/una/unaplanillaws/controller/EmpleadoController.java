package cr.ac.una.unaplanillaws.controller;

import cr.ac.una.unaplanillaws.model.EmpleadoDto;
import cr.ac.una.unaplanillaws.service.EmpleadoService;
import cr.ac.una.unaplanillaws.util.CodigoRespuesta;
import cr.ac.una.unaplanillaws.util.JwTokenHelper;
import cr.ac.una.unaplanillaws.util.Respuesta;
import cr.ac.una.unaplanillaws.util.Secure;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dominique
 */
@Secure // Requiere autenticación JWT en todos sus métodos (interceptado por SecurityFilter)
@Tag(name = "Empleados", description = "Operaciones sobre empleados")
@SecurityRequirement(name = "jwt-auth")
@Path("Empleados") // URI base del controlador: /ws/Empleados
public class EmpleadoController {
    
    @Context
    SecurityContext securityContext; // Inyección del contexto de seguridad (usuario autenticado)
    
    @EJB
    EmpleadoService empleadoService; // Inyección del EJB con la lógica de negocio
    
    // Endpoint público (excluido en SecurityFilter) para login y obtención del JWT
    @GET
    @Path("/usuario/{usuario}/{clave}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarUsuario(@PathParam("usuario") String usuario,
            @PathParam("clave") String clave) {
        
        try {
            Respuesta respuesta = empleadoService.validarUsuario(usuario, clave);
            
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            EmpleadoDto empleadoDto = (EmpleadoDto)respuesta.getResultado("Empleado");
            empleadoDto.setToken(JwTokenHelper.getInstance().generatePrivateKey(usuario));
            return Response.ok(empleadoDto).build();
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error validando el usuario.").build();
        }
    }
    
    // Consulta de un único empleado por ID (Lectura idempotente - Retorna DTO en JSON)
    @GET
    @Path("/empleado/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmpleado(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = empleadoService.getEmpleado(id);
            
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(respuesta.getResultado("Empleado")).build();
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error consultando el empleado.").build();
        }
    }
    
    // Consulta filtrada por criterios: se usa GenericEntity para preservar el tipo genérico List<EmpleadoDto> en JSON
    @GET
    @Path("/empleado/{cedula}/{nombre}/{pApellido}/{sApellido}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmpleado(@PathParam("cedula") String cedula, 
            @PathParam("nombre") String nombre,
            @PathParam("pApellido") String pApellido,
            @PathParam("sApellido") String sApellido) {
        try {
            Respuesta respuesta = empleadoService.getEmpleados(cedula, nombre, pApellido, sApellido);
            
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            // GenericEntity permite que el MessageBodyWriter de JAX-RS sepa serializar la lista genérica
            return Response.ok(
                    new GenericEntity<List<EmpleadoDto>>(
                    (List<EmpleadoDto>) respuesta.getResultado("Empleados")){})
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error consultando el empleado.").build();
        }
    }

    // Inserción / Actualización: recibe el cuerpo JSON con @Consumes y valida con Bean Validation (@Valid)
    @POST
    @Path("/empleado")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response guardarEmpleado(@Valid EmpleadoDto empleadoDto) {
        try {
            Respuesta respuesta = empleadoService.guardarEmpleado(empleadoDto);
            
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(respuesta.getResultado("Empleado")).build();
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error guardando el empleado.").build();
        }
    }

    // Eliminación física/lógica por ID usando verbo DELETE
    @DELETE
    @Path("/empleado/{id}")
    public Response eliminarEmpleado(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = empleadoService.eliminarEmpleado(id);
            
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok().build();
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error eliminando el empleado.").build();
        }
    }
    
    // Renovación de JWT: el SecurityFilter valida que el token recibido tenga claim 'rnw: true'
    @GET
    @Path("/renovar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response renovarToken() {
        
        try {
            // Se extrae el usuario autenticado que el SecurityFilter inyectó en el Principal
            String usuario = securityContext.getUserPrincipal().getName();
            if(usuario != null && !usuario.isBlank()){
                return Response.ok(JwTokenHelper.getInstance()
                        .generatePrivateKey(usuario)).build();
            }
            return Response.status(CodigoRespuesta.ERROR_PERMISOS.getValue())
                        .entity("No se pudo renovar el token.").build();
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error validando el usuario.").build();
        }
    }
}
