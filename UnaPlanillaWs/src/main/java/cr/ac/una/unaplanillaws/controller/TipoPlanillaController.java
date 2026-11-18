/*
 * CoreFx - JavaFX utility library
 * Author: Dominique
 * Date: 8 sept 2026
 * Package: cr.ac.una.unaplanillaws.controller
 */
package cr.ac.una.unaplanillaws.controller;

import cr.ac.una.unaplanillaws.model.TipoPlanillaDto;
import cr.ac.una.unaplanillaws.service.TipoPlanillaService;
import cr.ac.una.unaplanillaws.util.CodigoRespuesta;
import cr.ac.una.unaplanillaws.util.Respuesta;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador REST JAX-RS para la gestión de Tipos de Planilla.
 * 
 * GUÍA DE ESTUDIO - CONCEPTOS DE ARQUITECTURA REST / JAX-RS:
 * 1. @Path("TipoPlanilla"): Define el recurso base expuesto en la URL (/unaplanillaws/TipoPlanilla).
 * 2. @EJB: Inyección de dependencia del componente de lógica de negocio (EJB Stateless).
 * 3. Verbos HTTP:
 *    - @GET: Recuperación idempotente y segura de recursos sin efectos colaterales.
 *    - @POST: Creación o actualización (guardado) de recursos enviando cuerpo JSON.
 *    - @DELETE: Eliminación idempotente de recursos identificados por @PathParam.
 * 4. Tipos MIME:
 *    - @Consumes(MediaType.APPLICATION_JSON): Espera JSON en el payload (Content-Type: application/json).
 *    - @Produces(MediaType.APPLICATION_JSON): Retorna JSON en la respuesta (Accept: application/json).
 * 5. GenericEntity<List<T>>:
 *    - Necesario al retornar listas genéricas para que el proveedor de serialización JSON-B / Jackson
 *      pueda conservar la información del tipo de datos en tiempo de ejecución (evita Type Erasure de Java).
 * 
 * @author Dominique / Luis Elizondo (Anotaciones de Estudio)
 */
@Path("TipoPlanilla")
public class TipoPlanillaController {
    
    @EJB
    TipoPlanillaService tipoPlanillaService;
    
    /**
     * Consulta un TipoPlanilla por su llave primaria ID.
     * Endpoint: GET /TipoPlanilla/tipoPlanilla/{id}
     */
    @GET
    @Path("/tipoPlanilla/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTipoPlanilla(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = tipoPlanillaService.getTipoPlanilla(id);
            
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(respuesta.getResultado("TipoPlanilla")).build();
        } catch (Exception ex) {
            Logger.getLogger(TipoPlanillaController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error consultando la planilla.").build();
        }
    }
    
    /**
     * Consulta filtrada con múltiples parámetros en la URL.
     * Endpoint: GET /TipoPlanilla/tipoPlanilla/{codigo}/{descripcion}/{plaxmes}/{idemp}/{cedula}
     * 
     * NOTA: Utiliza GenericEntity para preservar el tipo concreto List<TipoPlanillaDto>
     * frente al borrado de tipos genéricos de Java durante la serialización JSON.
     */
    @GET
    @Path("/tipoPlanilla/{codigo}/{descripcion}/{plaxmes}/{idemp}/{cedula}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTipoPlanilla(@PathParam("codigo") String codigo,
            @PathParam("descripcion") String descripcion,
            @PathParam("plaxmes") String plaxmes,
            @PathParam("idemp") String idemp,
            @PathParam("cedula") String cedula) {
        try {
            Respuesta respuesta = tipoPlanillaService.getTiposPlanilla(codigo, descripcion, plaxmes, idemp, cedula);
            
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(
                    new GenericEntity<List<TipoPlanillaDto>>(
                    (List<TipoPlanillaDto>) respuesta.getResultado("TiposPlanilla")){})
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(TipoPlanillaController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error consultando la planilla.").build();
        }
    }

    /**
     * Guarda o actualiza un TipoPlanilla completo con sus detalles.
     * Endpoint: POST /TipoPlanilla/tipoPlanilla
     * @param tipoPlanillaDto Recibe el DTO deserializado automáticamente desde el JSON del body.
     */
    @POST
    @Path("/tipoPlanilla")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response guardarTipoPlanilla(@Valid TipoPlanillaDto tipoPlanillaDto) {
        try {
            Respuesta respuesta = tipoPlanillaService.guardarTipoPlanilla(tipoPlanillaDto);
            
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(respuesta.getResultado("TipoPlanilla")).build();
        } catch (Exception ex) {
            Logger.getLogger(TipoPlanillaController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error guardando la planilla.").build();
        }
    }

    /**
     * Elimina un TipoPlanilla por ID.
     * Endpoint: DELETE /TipoPlanilla/tipoPlanilla/{id}
     */
    @DELETE
    @Path("/tipoPlanilla/{id}")
    public Response eliminarTipoPlanilla(@PathParam("id") Long id) {
        
        try {
            Respuesta respuesta = tipoPlanillaService.eliminarTipoPlanilla(id);
            
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok().build();
        } catch (Exception ex) {
            Logger.getLogger(TipoPlanillaController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error eliminando la planilla.").build();
        }
    }

}
