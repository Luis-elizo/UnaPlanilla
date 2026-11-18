package cr.ac.una.unaplanilla.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestor de Contexto Global de la Aplicación Cliente (Patrón Creacional SINGLETON).
 * 
 * GUÍA DE ESTUDIO - PATRÓN SINGLETON THREAD-SAFE (Pregunta clásica de patrones):
 * 1. Propósito: Garantizar que exista una única instancia de la clase en toda la JVM y proporcionar
 *    un punto de acceso global a ella.
 * 2. Componentes Clave:
 *    - Constructor privado: Evita instanciaciones directas mediante 'new'.
 *    - Variable estática privada INSTANCE: Mantiene la referencia única.
 *    - Doble Bloqueo (Double-Checked Locking): En createInstance(), se valida 'INSTANCE == null',
 *      se bloquea el monitor (synchronized), y se vuelve a comprobar para garantizar seguridad
 *      en entornos multihilo sin penalizar el rendimiento en llamadas posteriores.
 *    - Protección contra clonación: Sobrescribe clone() lanzando CloneNotSupportedException.
 * 3. Uso en UnaPlanilla:
 *    - Almacena en memoria (HashMap<String, Object>) la sesión del usuario logueado ("Usuario"),
 *      los tokens JWT ("Token", "TokenRefresh"), parámetros de búsqueda y la URL base del WS ("resturl").
 * 
 * @author Carlos Carranza / Luis Elizondo (Anotaciones de Estudio)
 */
public class AppContext {

    private static AppContext INSTANCE = null;
    private static HashMap<String, Object> context = new HashMap<>();
    private volatile Logger LOGGER = Logger.getLogger(AppContext.class.getName());
    
    // Constructor privado: impide que otras clases hagan "new AppContext()"
    private AppContext() {
        cargarPropiedades();
    }

    // Double-Checked Locking para garantizar inicialización perezosa (Lazy) e hilo-segura (Thread-Safe)
    private static void createInstance() {
        if (INSTANCE == null) {
            synchronized (AppContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppContext();
                }
            }
        }
    }

    /**
     * Punto de acceso global a la única instancia de AppContext.
     */
    public static AppContext getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }

    private void cargarPropiedades() {
        try {
            FileInputStream configFile;
            configFile = new FileInputStream("config/properties.ini");
            Properties appProperties = new Properties();
            appProperties.load(configFile);
            configFile.close();
            if (appProperties.getProperty("propiedades.resturl") != null) {
                this.set("resturl", appProperties.getProperty("propiedades.resturl"));
            }
        } catch (IOException io) {
            LOGGER.log(Level.CONFIG, "No se pudo cargar la url.", io);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public Object get(String parameter) {
        return context.get(parameter);
    }

    public void set(String nombre, Object valor) {
        context.put(nombre, valor);
    }

    public void delete(String parameter) {
        context.put(parameter, null);
    }

}
