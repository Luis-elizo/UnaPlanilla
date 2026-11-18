package cr.ac.una.unaplanillaws.controller;

import cr.ac.una.unaplanillaws.model.EmpleadoDto;
import cr.ac.una.unaplanillaws.model.TipMovPlanillaDto;
import cr.ac.una.unaplanillaws.model.TipoPlanillaDto;
import cr.ac.una.unaplanillaws.service.TipMovPlanillaService;
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

@Path("TipoMovPlanilla")
public class TipoMovPlanillaController {
    
    @EJB 
    TipMovPlanillaService tipMovPlanillaService;

    @GET
    @Path("/tipoMovPlanilla/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTipoMovPlanilla(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = tipMovPlanillaService.getTipoMovPlanilla(id);
            
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(respuesta.getResultado("TipoMovPlanillaDto")).build();
        } catch (Exception ex) {
            Logger.getLogger(TipoMovPlanillaController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error consultando el tipo de movimiento planilla.").build();
        }
    }

    @POST
    @Path("/tipoMovPlanilla")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response guardarTipoMovPlanilla(@Valid TipMovPlanillaDto tipoMovPlanillaDto) {
        try {
            Respuesta respuesta = tipMovPlanillaService.guardarTipoMovPlanilla(tipoMovPlanillaDto);
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(respuesta.getResultado("TipMovPlanilla")).build();
        } catch (Exception ex) {
            Logger.getLogger(TipoMovPlanillaController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error guardando el tipo de movimiento planilla.").build();
        }
    }

    @DELETE
    @Path("/tipoMovPlanilla/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarTipoMovPlanilla(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = tipMovPlanillaService.eliminarTipoMovPlanilla(id);
            
            if (!respuesta.getEstado()) {
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }

            return Response.ok().build();
        } catch (Exception ex) {
            Logger.getLogger(TipoMovPlanillaController.class.getName())
                    .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error eliminando el tipo de movimiento planilla.").build();
        }
    }

}
