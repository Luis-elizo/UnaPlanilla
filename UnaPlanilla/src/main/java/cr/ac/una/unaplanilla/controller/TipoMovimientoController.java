/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.unaplanilla.controller;

import cr.ac.una.unaplanilla.model.TipoMovimientosDto;
import cr.ac.una.unaplanilla.service.TipoMovimientoService;
import cr.ac.una.unaplanilla.util.FlowController;
import cr.ac.una.unaplanilla.util.Formato;
import cr.ac.una.unaplanilla.util.Mensaje;
import cr.ac.una.unaplanilla.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Dominique
 */
public class TipoMovimientoController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private MFXTextField txtId;
    @FXML
    private MFXButton btnNuevo;
    @FXML
    private MFXButton btnBuscar;
    @FXML
    private MFXButton btnEliminar;
    @FXML
    private MFXButton btnGuardar;
    @FXML
    private MFXRadioButton rdbTipoIngreso;
    @FXML
    private ToggleGroup tggTipo;
    @FXML
    private MFXRadioButton rdbTipoDeduccion;
    @FXML
    private MFXTextField txtDescripcion;
    @FXML
    private MFXCheckbox chkGravable;
    @FXML
    private MFXCheckbox chkImprenta;
    @FXML
    private MFXTextField txtMonto;
    @FXML
    private MFXRadioButton rdbTipoMAbsoluto;
    @FXML
    private ToggleGroup tggTipoMonto;
    @FXML
    private MFXRadioButton rdbTipoMPorcentual;
    @FXML
    private ToggleGroup tggAplDeDPorc;
    @FXML
    private MFXRadioButton rdbTiApTodos;
    @FXML
    private MFXRadioButton rdbTiApGravables;
    @FXML
    private MFXRadioButton rdbTiApRenta;

    private TipoMovimientosDto tipoMovimientosDto;
    private ObjectProperty<TipoMovimientosDto> tipoMovimientoProperty = new SimpleObjectProperty<>();
    private List<Node> requeridos = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        VBox.setVgrow(root, Priority.ALWAYS);
        HBox.setHgrow(root, Priority.ALWAYS);
        
        rdbTipoIngreso.setUserData("I");
        rdbTipoDeduccion.setUserData("D");
        rdbTipoMAbsoluto.setUserData("A");
        rdbTipoMPorcentual.setUserData("P");
        rdbTiApTodos.setUserData("T");
        rdbTiApGravables.setUserData("G");
        rdbTiApRenta.setUserData("R");

        txtId.delegateSetTextFormatter(Formato.getInstance().integerFormat());
        txtDescripcion.delegateSetTextFormatter(Formato.getInstance().maxLengthFormat(40));
        txtMonto.delegateSetTextFormatter(Formato.getInstance().twoDecimalFormat());

        // BindingUtils guarda un único listener estático, por lo que no sirve para varios ToggleGroup a la vez.
        escucharToggleGroup(tggTipo, valor -> this.tipoMovimientosDto.setTipo(valor));
        escucharToggleGroup(tggTipoMonto, valor -> this.tipoMovimientosDto.setTipoMonto(valor));
        escucharToggleGroup(tggAplDeDPorc, valor -> this.tipoMovimientosDto.setTipoAplDeDPorc(valor));

        this.tipoMovimientosDto = new TipoMovimientosDto();
        bindMovimiento();
        cargarValoresDefecto();
        indicarRequeridos();
    }

    @Override
    public void initialize() {
    }

    private void escucharToggleGroup(ToggleGroup toggleGroup, Consumer<String> setter) {
        toggleGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && this.tipoMovimientosDto != null) {
                setter.accept((String) newVal.getUserData());
            }
        });
    }

    private void seleccionarToggle(ToggleGroup toggleGroup, String valor, Consumer<String> setter) {
        for (Toggle toggle : toggleGroup.getToggles()) {
            if (toggle.getUserData().equals(valor)) {
                toggleGroup.selectToggle(toggle);
                return;
            }
        }
        // Si el dto no trae valor, se toma el que está seleccionado en pantalla
        if (toggleGroup.getSelectedToggle() != null) {
            setter.accept((String) toggleGroup.getSelectedToggle().getUserData());
        }
    }

    private void bindMovimiento() {
        try {
            tipoMovimientoProperty.addListener((obs, oldVal, newVal) -> {
                if (oldVal != null) {
                    txtDescripcion.textProperty().unbindBidirectional(oldVal.getDescripcionProperty());
                    txtMonto.textProperty().unbindBidirectional(oldVal.getMontoProperty());
                    chkGravable.selectedProperty().unbindBidirectional(oldVal.getGravableProperty());
                    chkImprenta.selectedProperty().unbindBidirectional(oldVal.getImprentaProperty());
                }
                if (newVal != null) {
                    // El id no se bindea para poder digitar otro y buscarlo con Enter
                    txtId.setText(newVal.getIdProperty().get());
                    txtDescripcion.textProperty().bindBidirectional(newVal.getDescripcionProperty());
                    txtMonto.textProperty().bindBidirectional(newVal.getMontoProperty());
                    chkGravable.selectedProperty().bindBidirectional(newVal.getGravableProperty());
                    chkImprenta.selectedProperty().bindBidirectional(newVal.getImprentaProperty());
                    seleccionarToggle(tggTipo, newVal.getTipo(), newVal::setTipo);
                    seleccionarToggle(tggTipoMonto, newVal.getTipoMonto(), newVal::setTipoMonto);
                    seleccionarToggle(tggAplDeDPorc, newVal.getTipoAplDeDPorc(), newVal::setTipoAplDeDPorc);
                }
            });

        } catch (Exception e) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al realizar el bindeo", getStage(),
                    "Ocurrió un error al realizar el bindeo.");
        }
    }

    private void cargarValoresDefecto() {
        this.tipoMovimientosDto = new TipoMovimientosDto();
        this.tipoMovimientosDto.setActivo(Boolean.TRUE);
        this.tipoMovimientosDto.setGravable(Boolean.TRUE);
        this.tipoMovimientosDto.setTipo("I");
        this.tipoMovimientosDto.setTipoMonto("A");
        this.tipoMovimientosDto.setTipoAplDeDPorc("T");
        this.tipoMovimientoProperty.setValue(this.tipoMovimientosDto);
        txtId.clear();
        txtId.requestFocus();
    }

    private void indicarRequeridos() {
        requeridos.clear();
        requeridos.addAll(Arrays.asList(txtDescripcion, txtMonto));
    }

    public String validarRequeridos() {
        Boolean validos = true;
        String invalidos = "";
        for (Node node : requeridos) {
            if (node instanceof MFXTextField && (((MFXTextField) node).getText() == null || ((MFXTextField) node).getText().isBlank())) {
                if (validos) {
                    invalidos += ((MFXTextField) node).getFloatingText();
                } else {
                    invalidos += "," + ((MFXTextField) node).getFloatingText();
                }
                validos = false;
            }
        }
        if (validos) {
            return "";
        } else {
            return "Campos requeridos o con problemas de formato [" + invalidos + "].";
        }
    }

    private void cargarTipoMovimiento(Long id) {
        try {
            TipoMovimientoService service = new TipoMovimientoService();
            Respuesta respuesta = service.getTipoMovimiento(id);
            if (respuesta.getEstado()) {
                this.tipoMovimientosDto = (TipoMovimientosDto) respuesta.getResultado("TipoMovimiento");
                this.tipoMovimientoProperty.setValue(this.tipoMovimientosDto);
                validarRequeridos();
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Tipo Movimiento", getStage(), respuesta.getMensaje());
            }
        } catch (Exception ex) {
            Logger.getLogger(TipoMovimientoController.class.getName()).log(Level.SEVERE, "Error consultando el tipo de movimiento.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Tipo Movimiento", getStage(), "Ocurrio un error consultando el tipo de movimiento.");
        }
    }

    @FXML
    private void onKeyPressedTxtId(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !txtId.getText().isBlank()) {
            cargarTipoMovimiento(Long.valueOf(txtId.getText()));
        }
    }

    @FXML
    private void onActionBtnNuevo(ActionEvent event) {
        if (new Mensaje().showConfirmation("Limpiar tipo movimiento", getStage(), "¿Está seguro que desea limpiar el registro?")) {
            cargarValoresDefecto();
        }
    }

    @FXML
    private void onActionBtnBuscar(ActionEvent event) {
        BusquedaController busquedaController = (BusquedaController) FlowController.getInstance().getController("BusquedaView");
        busquedaController.busquedaTiposMovimiento();
        FlowController.getInstance().goViewInWindowModal("BusquedaView", getStage(), true);
        TipoMovimientosDto tMov = (TipoMovimientosDto) busquedaController.getResultado();
        if (tMov != null) {
            cargarTipoMovimiento(tMov.getId());
        }
    }

    @FXML
    private void onActionBtnEliminar(ActionEvent event) {
        try {
            int relaciones = this.tipoMovimientosDto.getTipMovPlanillaList().size();
            String confirmacion = relaciones > 0
                    ? "Este tipo de movimiento está asignado a " + relaciones + " tipo(s) de planilla y esas asignaciones también se eliminarán. ¿Desea continuar?"
                    : "¿Está seguro que desea eliminar el tipo de movimiento?";
            if (this.tipoMovimientosDto.getId() == null) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar tipo movimiento", getStage(), "Debe cargar el tipo de movimiento que desea eliminar.");
            } else if (new Mensaje().showConfirmation("Eliminar tipo movimiento", getStage(), confirmacion)) {
                TipoMovimientoService service = new TipoMovimientoService();
                Respuesta respuesta = service.eliminarTipoMovimiento(this.tipoMovimientosDto.getId());
                if (!respuesta.getEstado()) {
                    new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar tipo movimiento", getStage(), respuesta.getMensaje());
                } else {
                    new Mensaje().showModal(Alert.AlertType.INFORMATION, "Eliminar tipo movimiento", getStage(), "Tipo movimiento eliminado correctamente.");
                    cargarValoresDefecto();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(TipoMovimientoController.class.getName()).log(Level.SEVERE, "Error eliminando el tipo movimiento.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar tipo movimiento", getStage(), "Ocurrio un error eliminando el tipo movimiento.");
        }
    }

    @FXML
    private void onActionBtnGuardar(ActionEvent event) {
        try {
            String invalidos = validarRequeridos();
            if (!invalidos.isEmpty()) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Movimiento", getStage(), invalidos);
            } else if ("P".equals(this.tipoMovimientosDto.getTipoMonto()) && this.tipoMovimientosDto.getMonto() > 100) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Movimiento", getStage(), "Un monto porcentual no puede ser mayor a 100.");
            } else {
                TipoMovimientoService service = new TipoMovimientoService();
                Respuesta respuesta = service.guardarTipoMovimiento(this.tipoMovimientosDto);
                if (!respuesta.getEstado()) {
                    new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Movimiento", getStage(), respuesta.getMensaje());
                } else {
                    this.tipoMovimientosDto = (TipoMovimientosDto) respuesta.getResultado("TipoMovimiento");
                    this.tipoMovimientoProperty.setValue(this.tipoMovimientosDto);
                    new Mensaje().showModal(Alert.AlertType.INFORMATION, "Guardar Tipo Movimiento", getStage(), "Tipo Movimiento actualizado correctamente.");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(TipoMovimientoController.class.getName()).log(Level.SEVERE, "Error guardando el tipo de movimiento.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Movimiento", getStage(), "Ocurrio un error guardando el tipo de movimiento.");
        }
    }

}
