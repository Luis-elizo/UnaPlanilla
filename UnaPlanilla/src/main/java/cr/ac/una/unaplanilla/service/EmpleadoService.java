package cr.ac.una.unaplanilla.service;

import cr.ac.una.unaplanilla.model.EmpleadoDto;
import cr.ac.una.unaplanilla.util.Request;
import cr.ac.una.unaplanilla.util.Respuesta;
import jakarta.ws.rs.core.GenericType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio Cliente para el consumo de la API REST de Empleados.
 * 
 * GUÍA DE ESTUDIO - CONSUMO DE SERVICIOS REST EN CLIENTE (JAX-RS Client):
 * 1. Patrón Service Proxy: Encapsula las llamadas HTTP hacia el Web Service backend.
 * 2. Manejo de Autenticación y Tokens JWT:
 *    - getUsuario(): Invoca request.getToken(). El backend genera el JWT y lo envía en el encabezado
 *      'Authorization'. La clase Request lo captura y almacena en AppContext ("Token").
 *    - renovarToken(): Invoca request.getRenewal() para enviar el token secundario de renovación (rnt)
 *      y actualizar el token principal antes de que expire.
 * 3. Deserialización de Entidades y Colecciones:
 *    - readEntity(EmpleadoDto.class): Deserializa un objeto individual desde el JSON devuelto.
 *    - readEntity(new GenericType<List<EmpleadoDto>>() {}): Usa GenericType de JAX-RS para deserializar
 *      listas polimórficas sin perder el tipo genérico en tiempo de ejecución.
 * 
 * @author cbcar / Luis Elizondo (Anotaciones de Estudio)
 */
public class EmpleadoService {
    
    public Respuesta getUsuario(String usuario, String clave){
        try {
            
            Map<String, Object> parametros = new HashMap<>();
            
            parametros.put("usuario", usuario);
            parametros.put("clave", clave);
            
            Request request = new Request("Empleados/usuario", "/{usuario}/{clave}", parametros);
            
            request.getToken();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            EmpleadoDto empleadoDto = (EmpleadoDto) request.readEntity(EmpleadoDto.class);
            
            return new Respuesta(true, "", "", "Usuario", empleadoDto);
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error obteniendo el usuario [" + usuario + "]", ex);
            return new Respuesta(false, "Error obteniendo el usuario.", "getUsuario " + ex.getMessage());
        } 
    }
    
    public Respuesta getEmpleado(Long id){
        try {
            
            Map<String, Object> parametros = new HashMap<>();
            
            parametros.put("id", id);
            
            Request request = new Request("Empleados/empleado", "/{id}", parametros);
            
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            EmpleadoDto empleadoDto = (EmpleadoDto) request.readEntity(EmpleadoDto.class);
            
            return new Respuesta(true, "", "", "Empleado", empleadoDto);
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error obteniendo el empleado [" + id + "]", ex);
            return new Respuesta(false, "Error obteniendo el empleado.", "getEmpleado " + ex.getMessage());
        } 
    }
    
    public Respuesta renovarToken() {
        try {
            
            Request request = new Request("Empleados/renovar");
            request.getRenewal();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            String token = (String) request.readEntity(String.class);
            return new Respuesta(true, "", "", "Token", token);
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error renovando el token.", ex);
            return new Respuesta(false, "Error renovando el token.", "renovarToken " + ex.getMessage());
        } 
    }
    
    public Respuesta getEmpleados(String cedula, String nombre, String pApellido, String sApellido) {
        try {
            
            Map<String, Object> parametros = new HashMap<>();
            
            parametros.put("cedula", cedula);
            parametros.put("nombre", nombre);
            parametros.put("pApellido", pApellido);
            parametros.put("sApellido", sApellido);
            
            Request request = new Request("Empleados/empleado", "/{cedula}/{nombre}/{pApellido}/{sApellido}", parametros);
            
            request.get();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            List<EmpleadoDto> empleadosDto = (List<EmpleadoDto>) request.readEntity(new GenericType<List<EmpleadoDto>>(){});
            
            return new Respuesta(true, "", "", "Empleados", empleadosDto);
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error obteniendo empleados.", ex);
            return new Respuesta(false, "Error obteniendo empleados.", "getEmpleados " + ex.getMessage());
        }
    }
    
    public Respuesta guardarEmpleado(EmpleadoDto empleadoDto){
        try {
            
            Request request = new Request("Empleados/empleado");

            request.post(empleadoDto);
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            EmpleadoDto empleado = (EmpleadoDto) request.readEntity(EmpleadoDto.class);
            
            return new Respuesta(true, "", "", "Empleado", empleado);
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Ocurrio un error al guardar el empleado.", ex);
            return new Respuesta(false, "Ocurrio un error al guardar el empleado.", "guardarEmpleado " + ex.getMessage());
        }
    }
    
    public Respuesta eliminarEmpleado(Long id){
        try {
            
            Map<String, Object> parametros = new HashMap<>();
            
            parametros.put("id", id);
            
            Request request = new Request("Empleados/empleado", "/{id}", parametros);

            request.delete();
            
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "");
            }
            
            return new Respuesta(true, "", "");
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoService.class.getName()).log(Level.SEVERE, "Error eliminando el empleado", ex);
            return new Respuesta(false, "Error eliminando el empleado.", "eliminarEmpleado " + ex.getMessage());
        }
    }
}
