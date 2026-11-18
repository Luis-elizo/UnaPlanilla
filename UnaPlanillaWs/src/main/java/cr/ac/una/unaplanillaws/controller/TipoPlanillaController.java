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
 *
 * @author Dominique
 */
@Path("TipoPlanilla")
public class TipoPlanillaController {
    
    @EJB
    TipoPlanillaService tipoPlanillaService;
    
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
