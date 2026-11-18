package cr.ac.una.unaplanillaws.controller;

import cr.ac.una.unaplanillaws.model.EmpleadoDto;
import cr.ac.una.unaplanillaws.model.TipoMovimientosDto;
import cr.ac.una.unaplanillaws.service.EmpleadoService;
import cr.ac.una.unaplanillaws.service.TipoMovimientoService;
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
@Path("TipoMovimiento")
public class TipoMovimientoController {

    @EJB
    TipoMovimientoService tipoMovimientoService;

    @GET
    @Path("/movimiento/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTipoMovimiento(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = tipoMovimientoService.getTipoMovimientoById(id);
            if(!respuesta.getEstado()){
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(respuesta.getResultado("TipoMovimiento")).build();
        } catch (Exception ex) {
            Logger.getLogger(TipoMovimientoController.class.getName())
                .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error consultado el TipoMovimiento").build();
        }
    }

    @GET
    @Path("/movimientos/{descripcion}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTiposMovimiento(@PathParam("descripcion") String descripcion) {
        try {
            Respuesta respuesta = tipoMovimientoService.getTiposMovimiento(descripcion);
            if(!respuesta.getEstado()){
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(
                    new GenericEntity<List<TipoMovimientosDto>>(
                    (List<TipoMovimientosDto>) respuesta.getResultado("TiposMovimiento")){})
                    .build();
        } catch (Exception ex) {
            Logger.getLogger(TipoMovimientoController.class.getName())
                .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error consultando los TipoMovimiento").build();
        }
    }

    @POST
    @Path("/movimientos/guardar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response guardarTipoMovimiento(@Valid TipoMovimientosDto tipoMovimiento) {
        try {
            Respuesta respuesta = tipoMovimientoService.guardarTipoMovimiento(tipoMovimiento);
            if(!respuesta.getEstado()){
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok(respuesta.getResultado("TipoMovimiento")).build();
        } catch (Exception ex) {
            Logger.getLogger(TipoMovimientoController.class.getName())
                .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error guardando el TipoMovimiento").build();
        }
    }

    @DELETE
    @Path("/movimiento/eliminar/{id}")
    public Response eliminarTipoMovimiento(@PathParam("id") Long id) {
        try {
            Respuesta respuesta = tipoMovimientoService.eliminarTipoMovimiento(id);
            if(!respuesta.getEstado()){
                return Response.status(respuesta.getCodigoRespuesta().getValue())
                        .entity(respuesta.getMensaje()).build();
            }
            return Response.ok().build();
        } catch (Exception ex) {
            Logger.getLogger(TipoMovimientoController.class.getName())
                .log(Level.SEVERE, null, ex);
            return Response.status(CodigoRespuesta.ERROR_INTERNO.getValue())
                        .entity("Error eliminando el TipoMovimiento").build();
        }
    }

}
