package cr.ac.una.unaplanilla.service;

import cr.ac.una.unaplanilla.model.TipMovPlanillaDto; 
import cr.ac.una.unaplanilla.util.Request;
import cr.ac.una.unaplanilla.util.Respuesta;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TipoMovPlanillaService {

    public Respuesta getTipoMovPlanilla(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);

            Request request = new Request("TipoMovPlanilla/tipoMovPlanilla", "/{id}", parametros);
            request.get();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            TipMovPlanillaDto tipMovPlanillaDto = (TipMovPlanillaDto) request.readEntity(TipMovPlanillaDto.class);
            return new Respuesta(true, "", "", "TipMovPlanilla", tipMovPlanillaDto);

        } catch (Exception ex) {
            Logger.getLogger(TipoMovPlanillaService.class.getName())
                .log(Level.SEVERE, "Ocurrió un error al consultar el tipo de movimiento de planilla.", ex);
            return new Respuesta(false, "Ocurrió un error al consultar el tipo de movimiento de planilla.", "getTipoMovPlanilla " + ex.getMessage());
        }
    }

    public Respuesta guardarTipoMovPlanilla(TipMovPlanillaDto tipMovPlanilla) {
        try {
            Request request = new Request("TipoMovPlanilla/tipoMovPlanilla");
            request.post(tipMovPlanilla);

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            TipMovPlanillaDto tipMovPlanillaDto = (TipMovPlanillaDto) request.readEntity(TipMovPlanillaDto.class);
            return new Respuesta(true, "", "", "TipMovPlanilla", tipMovPlanillaDto);

        } catch (Exception ex) {
            Logger.getLogger(TipoMovPlanillaService.class.getName())
                .log(Level.SEVERE, "Ocurrió un error al guardar el tipo de movimiento de planilla.", ex);
            return new Respuesta(false, "Ocurrió un error al guardar el tipo de movimiento de planilla.", "guardarTipoMovPlanilla " + ex.getMessage());
        }
    }

    public Respuesta eliminarTipoMovPlanilla(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);

            Request request = new Request("TipoMovPlanilla/tipoMovPlanilla", "/{id}", parametros);
            request.delete();

            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }

            return new Respuesta(true, "", "");

        } catch (Exception ex) {
            Logger.getLogger(TipoMovPlanillaService.class.getName())
                .log(Level.SEVERE, "Ocurrió un error al eliminar el tipo de movimiento de planilla.", ex);
            return new Respuesta(false, "Ocurrió un error al eliminar el tipo de movimiento de planilla.", "eliminarTipoMovPlanilla " + ex.getMessage());
        }
    }
}