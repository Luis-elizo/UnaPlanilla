package cr.ac.una.unaplanilla.controller;

import cr.ac.una.unaplanilla.model.EmpleadoDto;
import cr.ac.una.unaplanilla.model.TipMovPlanillaDto;
import cr.ac.una.unaplanilla.model.TipoMovimientosDto;
import cr.ac.una.unaplanilla.model.TipoPlanillaDto;
import cr.ac.una.unaplanilla.service.EmpleadoService;
import cr.ac.una.unaplanilla.service.TipoMovimientoService;
import cr.ac.una.unaplanilla.service.TipoPlanillaService;
import cr.ac.una.unaplanilla.util.FlowController;
import cr.ac.una.unaplanilla.util.Formato;
import cr.ac.una.unaplanilla.util.Mensaje;
import cr.ac.una.unaplanilla.util.Respuesta;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Controlador JavaFX para la pantalla de Tipos de Planilla (Patrón Maestro-Detalle Completo en Cliente).
 * 
 * GUÍA DE ESTUDIO - MAESTRO-DETALLE Y COMPONENTES AVANZADOS JAVAFX:
 * 1. TabPane con Múltiples Pestañas:
 *    - Pestaña 1: Datos generales del Tipo de Planilla (Maestro).
 *    - Pestaña 2: Inclusión de Empleados (Detalle M:N).
 *    - Pestaña 3: Configuración de Tipos de Movimiento (Detalle 1:N).
 * 2. Patrón de Bindeo Bidireccional Seguro:
 *    - bindTipoPlanilla(): Escucha cambios en tipoPlanillaProperty. Cuando se asigna un nuevo DTO,
 *      primero desvincula (unbindBidirectional) los controles del DTO anterior para evitar memory leaks
 *      o sobreescrituras accidentales, y luego vincula (bindBidirectional) las propiedades del nuevo DTO.
 * 3. Renderizado Personalizado en TableView (TableCell con Botones):
 *    - ButtonCell y ButtonCellMovimiento: Sobrescriben TableCell para incrustar un botón gráfico
 *      en cada fila de la tabla. Al presionar el botón de eliminar, no solo se remueve visualmente
 *      de la TableView, sino que se agrega a la lista de eliminados del DTO (getEmpleadosEliminados()),
 *      la cual viajará en el JSON hacia el Backend para su eliminación física o lógica.
 * 4. Sincronización Post-Guardado:
 *    - Al invocar guardarTipoPlanilla(), si la respuesta es exitosa, se vuelve a llamar a
 *      cargarTipoPlanilla(id) para refrescar todos los datos y llaves generadas desde el servidor.
 * 
 * @author cbcar / Luis Elizondo (Anotaciones de Estudio)
 */
public class TiposPlanillaController extends Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private TabPane tbpTipoPlanilla;
    @FXML
    private Tab tbpTipoPlanillas;
    @FXML
    private MFXTextField txtId;
    @FXML
    private MFXCheckbox chkActivo;
    @FXML
    private MFXTextField txtCodigo;
    @FXML
    private MFXTextField txtDescripcion;
    @FXML
    private MFXTextField txtPlanillasMes;
    @FXML
    private Tab tbpInclusionEmpleados;
    @FXML
    private MFXTextField txtIdEmpleado;
    @FXML
    private MFXTextField txtNombreEmpleado;
    @FXML
    private MFXButton btnAgregarEmpleado;
    @FXML
    private TableView<EmpleadoDto> tbvEmpleados;
    @FXML
    private TableColumn<EmpleadoDto, String> tbcCodigo;
    @FXML
    private TableColumn<EmpleadoDto, String> tbcNombre;
    @FXML
    private TableColumn<EmpleadoDto, Boolean> tbcEliminar;
    @FXML
    private Tab tbpTiposMovimiento;
    @FXML
    private MFXTextField txtIdMovimiento;
    @FXML
    private MFXTextField txtDescripcionMovimiento;
    @FXML
    private MFXTextField txtMontoMovimiento;
    @FXML
    private MFXButton btnAgregarMovimiento;
    @FXML
    private TableView<TipMovPlanillaDto> tbvMovimientos;
    @FXML
    private TableColumn<TipMovPlanillaDto, String> tbcMovCodigo;
    @FXML
    private TableColumn<TipMovPlanillaDto, String> tbcMovDescripcion;
    @FXML
    private TableColumn<TipMovPlanillaDto, Double> tbcMovMonto;
    @FXML
    private TableColumn<TipMovPlanillaDto, Boolean> tbcMovEliminar;
    @FXML
    private MFXButton btnNuevo;
    @FXML
    private MFXButton btnBuscar;
    @FXML
    private MFXButton btnEliminar;
    @FXML
    private MFXButton btnGuardar;

    private TipoPlanillaDto tipoPlanillaDto;
    private ObjectProperty<TipoPlanillaDto> tipoPlanillaProperty = new SimpleObjectProperty<>();
    private EmpleadoDto empleadoDto;
    private ObjectProperty<EmpleadoDto> empleadoProperty = new SimpleObjectProperty<>();
    private TipoMovimientosDto tipoMovimientoDto;
    private ObjectProperty<TipoMovimientosDto> tipoMovimientoProperty = new SimpleObjectProperty<>();
    private List<Node> requeridos = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        VBox.setVgrow(root, Priority.ALWAYS);
        HBox.setHgrow(root, Priority.ALWAYS);
        
        txtId.delegateSetTextFormatter(Formato.getInstance().integerFormat());
        txtCodigo.delegateSetTextFormatter(Formato.getInstance().maxLengthFormat(4));
        txtDescripcion.delegateSetTextFormatter(Formato.getInstance().letrasFormat(40));
        txtPlanillasMes.delegateSetTextFormatter(Formato.getInstance().integerFormat());
        this.tipoPlanillaDto = new TipoPlanillaDto();
        bindTipoPlanilla();
        this.empleadoDto = new EmpleadoDto();
        bindEmpleado();
        this.tipoMovimientoDto = new TipoMovimientosDto();
        bindTipoMovimiento();
        cargarValoresDefecto();
        indicarRequeridos();

        txtIdEmpleado.delegateSetTextFormatter(Formato.getInstance().integerFormat());
        tbcCodigo.setCellValueFactory((cd) -> cd.getValue().getIdProperty());
        tbcNombre.setCellValueFactory((cd) -> cd.getValue().getNombreProperty());
        tbcEliminar.setCellValueFactory((cd) -> new SimpleBooleanProperty(cd.getValue() != null));
        tbcEliminar.setCellFactory((cd) -> new ButtonCell());

        tbvEmpleados.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                this.empleadoDto = newValue;
                this.empleadoProperty.setValue(this.empleadoDto);
            }
        });

        txtIdMovimiento.delegateSetTextFormatter(Formato.getInstance().integerFormat());
        tbcMovCodigo.setCellValueFactory((cd) -> cd.getValue().getTipoMovimientoDto().getIdProperty());
        tbcMovDescripcion.setCellValueFactory((cd) -> cd.getValue().getTipoMovimientoDto().getDescripcionProperty());
        tbcMovMonto.setCellValueFactory((cd) -> cd.getValue().getMontoProperty());
        tbcMovEliminar.setCellValueFactory((cd) -> new SimpleBooleanProperty(cd.getValue() != null));
        tbcMovEliminar.setCellFactory((cd) -> new ButtonCellMovimiento());

        tbvMovimientos.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                this.tipoMovimientoDto = newValue.getTipoMovimientoDto();
                this.tipoMovimientoProperty.setValue(this.tipoMovimientoDto);
            }
        });
    }

    @Override
    public void initialize() {

    }

    private void bindTipoPlanilla() {
        try {
            tipoPlanillaProperty.addListener((obs, oldVal, newVal) -> {
                if (oldVal != null) {
                    txtCodigo.textProperty().unbindBidirectional(oldVal.getCodigoProperty());
                    txtDescripcion.textProperty().unbindBidirectional(oldVal.getDescripcionProperty());
                    txtPlanillasMes.textProperty().unbindBidirectional(oldVal.getPlanillaPorMesProperty());
                    chkActivo.selectedProperty().unbindBidirectional(oldVal.getActivoProperty());
                }
                if (newVal != null) {
                    // El id no se bindea para poder digitar otro y buscarlo con Enter
                    txtId.setText(newVal.getIdProperty().get());
                    txtCodigo.textProperty().bindBidirectional(newVal.getCodigoProperty());
                    txtDescripcion.textProperty().bindBidirectional(newVal.getDescripcionProperty());
                    txtPlanillasMes.textProperty().bindBidirectional(newVal.getPlanillaPorMesProperty());
                    chkActivo.selectedProperty().bindBidirectional(newVal.getActivoProperty());
                }
            });

        } catch (Exception ex) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al realizar el bindeo", getStage(),
                    "Ocurrió un error al realizar el bindeo.");
        }
    }

    private void bindEmpleado() {
        try {
            empleadoProperty.addListener((obs, oldVal, newVal) -> {
                if (oldVal != null) {
                    txtNombreEmpleado.textProperty().unbindBidirectional(oldVal.getNombreProperty());
                }
                if (newVal != null) {
                    // El id no se bindea para poder digitar otro y buscarlo con Enter
                    txtIdEmpleado.setText(newVal.getIdProperty().get());
                    txtNombreEmpleado.textProperty().bindBidirectional(newVal.getNombreProperty());
                }
            });

        } catch (Exception ex) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al realizar el bindeo", getStage(),
                    "Ocurrió un error al realizar el bindeo.");
        }
    }

    private void bindTipoMovimiento() {
        try {
            tipoMovimientoProperty.addListener((obs, oldVal, newVal) -> {
                if (oldVal != null) {
                    txtDescripcionMovimiento.textProperty().unbindBidirectional(oldVal.getDescripcionProperty());
                    txtMontoMovimiento.textProperty().unbindBidirectional(oldVal.getMontoProperty());
                }
                if (newVal != null) {
                    // El id no se bindea para poder digitar otro y buscarlo con Enter
                    txtIdMovimiento.setText(newVal.getIdProperty().get());
                    txtDescripcionMovimiento.textProperty().bindBidirectional(newVal.getDescripcionProperty());
                    txtMontoMovimiento.textProperty().bindBidirectional(newVal.getMontoProperty());
                }
            });

        } catch (Exception ex) {
            new Mensaje().showModal(Alert.AlertType.ERROR, "Error al realizar el bindeo", getStage(),
                    "Ocurrió un error al realizar el bindeo.");
        }
    }

    private void cargarValoresDefecto() {
        this.tipoPlanillaDto = new TipoPlanillaDto();
        this.tipoPlanillaDto.setActivo(Boolean.TRUE);
        this.tipoPlanillaProperty.setValue(this.tipoPlanillaDto);
        txtId.clear();
        txtId.requestFocus();
        limpiarEmpleado();
        cargarEmpleados();
        limpiarMovimiento();
        cargarMovimientos();
    }

    private void limpiarEmpleado() {
        tbvEmpleados.getSelectionModel().select(null);
        this.empleadoDto = new EmpleadoDto();
        this.empleadoProperty.setValue(this.empleadoDto);
        txtIdEmpleado.clear();
        txtIdEmpleado.requestFocus();
    }

    private void cargarEmpleados() {
        tbvEmpleados.getItems().clear();
        tbvEmpleados.setItems(this.tipoPlanillaDto.getEmpleados());
        tbvEmpleados.refresh();
    }

    private void limpiarMovimiento() {
        tbvMovimientos.getSelectionModel().select(null);
        this.tipoMovimientoDto = new TipoMovimientosDto();
        this.tipoMovimientoProperty.setValue(this.tipoMovimientoDto);
        txtIdMovimiento.clear();
        txtIdMovimiento.requestFocus();
    }

    private void cargarMovimientos() {
        tbvMovimientos.setItems(this.tipoPlanillaDto.getTiposMovimiento());
        tbvMovimientos.refresh();
    }

    private void cargarTipoMovimiento(Long id) {
        try {
            TipoMovimientoService service = new TipoMovimientoService();
            Respuesta respuesta = service.getTipoMovimiento(id);
            if (respuesta.getEstado()) {
                this.tipoMovimientoDto = (TipoMovimientosDto) respuesta.getResultado("TipoMovimiento");
                this.tipoMovimientoProperty.setValue(this.tipoMovimientoDto);
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Buscar Tipo Movimiento", getStage(), respuesta.getMensaje());
            }
        } catch (Exception ex) {
            Logger.getLogger(TiposPlanillaController.class.getName()).log(Level.SEVERE, "Error buscando el tipo de movimiento.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Buscar Tipo Movimiento", getStage(), "Ocurrió un error buscando el tipo de movimiento.");
        }
    }

    private void indicarRequeridos() {
        requeridos.clear();
        requeridos.addAll(Arrays.asList(txtDescripcion, txtCodigo, txtPlanillasMes));
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
            } else if (node instanceof MFXPasswordField && (((MFXPasswordField) node).getText() == null || ((MFXPasswordField) node).getText().isBlank())) {
                if (validos) {
                    invalidos += ((MFXPasswordField) node).getFloatingText();
                } else {
                    invalidos += "," + ((MFXPasswordField) node).getFloatingText();
                }
                validos = false;
            } else if (node instanceof MFXDatePicker && ((MFXDatePicker) node).getValue() == null) {
                if (validos) {
                    invalidos += ((MFXDatePicker) node).getFloatingText();
                } else {
                    invalidos += "," + ((MFXDatePicker) node).getFloatingText();
                }
                validos = false;
            } else if (node instanceof MFXComboBox && ((MFXComboBox) node).getSelectionModel().getSelectedIndex() < 0) {
                if (validos) {
                    invalidos += ((MFXComboBox) node).getFloatingText();
                } else {
                    invalidos += "," + ((MFXComboBox) node).getFloatingText();
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

    @FXML
    private void onKeyPressTxtId(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !txtId.getText().isBlank()) {
            cargarTipoPlanilla(Long.valueOf(txtId.getText()));
        }
    }

    private void cargarTipoPlanilla(Long id) {
        try {
            TipoPlanillaService service = new TipoPlanillaService();
            Respuesta respuesta = service.getTipoPlanilla(id);
            if (respuesta.getEstado()) {
                this.tipoPlanillaDto = (TipoPlanillaDto) respuesta.getResultado("TipoPlanilla");
                this.tipoPlanillaProperty.setValue(this.tipoPlanillaDto);
                validarRequeridos();
                cargarEmpleados();
                limpiarMovimiento();
                cargarMovimientos();
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Tipo Planilla", getStage(), respuesta.getMensaje());
            }
        } catch (Exception ex) {
            Logger.getLogger(TiposPlanillaController.class.getName()).log(Level.SEVERE, "Error consultando el tipo de planilla.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Cargar Tipo Planilla", getStage(), "Ocurrio un error consultando el tipo de planilla.");
        }
    }

    private void cargarEmpleado(Long id) {
        try {
            EmpleadoService empleadoService = new EmpleadoService();
            Respuesta respuesta = empleadoService.getEmpleado(id);
            if (respuesta.getEstado()) {
                this.empleadoDto = (EmpleadoDto) respuesta.getResultado("Empleado");
                this.empleadoProperty.setValue(this.empleadoDto);
            } else {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Buscar Empleado", getStage(), respuesta.getMensaje());
            }
        } catch (Exception ex) {
            Logger.getLogger(EmpleadosController.class.getName()).log(Level.SEVERE, "Error buscando el empleado.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Buscar Empleado", getStage(), "Ocurrió un error buscando el empleado.");
        }
    }

    @FXML
    private void onKeyPressedTxtCodigo(KeyEvent event) {
    }

    @FXML
    private void onKeyPressedTxtIdEmpleado(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !txtIdEmpleado.getText().isBlank()) {
            cargarEmpleado(Long.valueOf(txtIdEmpleado.getText()));
        }
    }

    @FXML
    private void onActionBtnAgregarEmpleado(ActionEvent event) {
        if (this.empleadoDto.getId() == null || this.empleadoDto.getNombre().isBlank()) {
            new Mensaje().showModal(Alert.AlertType.WARNING, "Agregar Empleado", getStage(), "Es necesario cargar un empleado para agregarlo a lista.");
        } else if (tbvEmpleados.getItems().stream().noneMatch(e -> e.equals(this.empleadoDto))) {
            this.empleadoDto.setModificado(true);
            tbvEmpleados.getItems().add(this.empleadoDto);
            tbvEmpleados.refresh();
        }
    }

    @FXML
    private void selectionChangeTabEmp(Event event) {
        if (tbpInclusionEmpleados.isSelected()) {
            if (this.tipoPlanillaDto.getId() == null) {
                new Mensaje().showModal(Alert.AlertType.WARNING, "Agregar Empleado", getStage(),
                        "Debe cargar el tipo de planilla al que desea agregar empleados.");
                tbpTipoPlanilla.getSelectionModel().select(tbpTipoPlanillas);
            }
        }
    }

    @FXML
    private void selectionChangeTabMov(Event event) {
        if (tbpTiposMovimiento.isSelected()) {
            if (this.tipoPlanillaDto.getId() == null) {
                new Mensaje().showModal(Alert.AlertType.WARNING, "Agregar Tipo Movimiento", getStage(),
                        "Debe cargar el tipo de planilla al que desea agregar tipos de movimiento.");
                tbpTipoPlanilla.getSelectionModel().select(tbpTipoPlanillas);
            }
        }
    }

    @FXML
    private void onKeyPressedTxtIdMovimiento(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !txtIdMovimiento.getText().isBlank()) {
            cargarTipoMovimiento(Long.valueOf(txtIdMovimiento.getText()));
        }
    }

    @FXML
    private void onActionBtnAgregarMovimiento(ActionEvent event) {
        if (this.tipoMovimientoDto.getId() == null || this.tipoMovimientoDto.getDescripcion().isBlank()) {
            new Mensaje().showModal(Alert.AlertType.WARNING, "Agregar Tipo Movimiento", getStage(), "Es necesario cargar un tipo de movimiento para agregarlo a lista.");
        } else if (tbvMovimientos.getItems().stream().noneMatch(t -> t.getTipoMovimientoDto().equals(this.tipoMovimientoDto))) {
            TipMovPlanillaDto tipMovPlanilla = new TipMovPlanillaDto();
            tipMovPlanilla.setTipoMovimientoDto(this.tipoMovimientoDto);
            tipMovPlanilla.setMonto(this.tipoMovimientoDto.getMonto());
            tipMovPlanilla.setTipoMonto(this.tipoMovimientoDto.getTipoMonto().charAt(0));
            tipMovPlanilla.setTipoAplDeDPorc(this.tipoMovimientoDto.getTipoAplDeDPorc().charAt(0));
            tipMovPlanilla.setModificado(true);
            tbvMovimientos.getItems().add(tipMovPlanilla);
            tbvMovimientos.refresh();
        }
    }

    @FXML
    private void onActionBtnNuevo(ActionEvent event) {
        if (tbpInclusionEmpleados.isSelected()) {
            limpiarEmpleado();
        } else if (tbpTiposMovimiento.isSelected()) {
            limpiarMovimiento();
        } else if (new Mensaje().showConfirmation("Limpiar tipo planilla", getStage(), "¿Está seguro que desea limpiar el registro?")) {
            cargarValoresDefecto();
        }
    }

    @FXML
    private void onActionBtnBuscar(ActionEvent event) {
        BusquedaController busquedaController = (BusquedaController) FlowController.getInstance().getController("BusquedaView");
        busquedaController.busquedaTiposPlanilla();
        FlowController.getInstance().goViewInWindowModal("BusquedaView", getStage(), true);
        TipoPlanillaDto tPla = (TipoPlanillaDto) busquedaController.getResultado();
        if (tPla != null) {
            cargarTipoPlanilla(tPla.getId());
        }
    }

    @FXML
    private void onActionBtnEliminar(ActionEvent event) {
        try {
            if (this.tipoPlanillaDto.getId() == null) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar tipo planilla", getStage(), "Debe cargar el tipo de planilla que desea eliminar.");
            } else {
                TipoPlanillaService service = new TipoPlanillaService();
                Respuesta respuesta = service.eliminarTipoPlanilla(this.tipoPlanillaDto.getId());
                if (!respuesta.getEstado()) {
                    new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar tipo planilla", getStage(), respuesta.getMensaje());
                } else {
                    new Mensaje().showModal(Alert.AlertType.INFORMATION, "Eliminar tipo planilla", getStage(), "Tipo planilla eliminado correctamente.");
                    cargarValoresDefecto();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(TiposPlanillaController.class.getName()).log(Level.SEVERE, "Error eliminando el tipo planilla.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Eliminar tipo planilla", getStage(), "Ocurrio un error eliminando el tipo planilla.");
        }
    }

    @FXML
    private void onActionBtnGuardar(ActionEvent event) {
        try {
            String invalidos = validarRequeridos();
            if (!invalidos.isEmpty()) {
                new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Planilla", getStage(), invalidos);
            } else {
                TipoPlanillaService tipoPlanillaService = new TipoPlanillaService();
                Respuesta respuesta = tipoPlanillaService.guardarTipoPlanilla(this.tipoPlanillaDto);
                if (!respuesta.getEstado()) {
                    new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Planilla", getStage(), respuesta.getMensaje());
                } else {
                    // Se recarga para traer las listas de empleados y movimientos ya guardadas
                    cargarTipoPlanilla(((TipoPlanillaDto) respuesta.getResultado("TipoPlanilla")).getId());
                    new Mensaje().showModal(Alert.AlertType.INFORMATION, "Guardar Tipo Planilla", getStage(), "Tipo Planilla actualizada correctamente.");
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(EmpleadosController.class.getName()).log(Level.SEVERE, "Error guardando el tipo de planilla.", ex);
            new Mensaje().showModal(Alert.AlertType.ERROR, "Guardar Tipo Planilla", getStage(), "Ocurrio un error guardando el tipo de planilla.");
        }
    }

    private class ButtonCell extends TableCell<EmpleadoDto, Boolean> {

        final Button cellButton = new Button();

        ButtonCell() {
            cellButton.setPrefWidth(500);
            cellButton.getStyleClass().add("jfx-btnimg-tbveliminar");

            cellButton.setOnAction((ActionEvent t) -> {
                EmpleadoDto emp = (EmpleadoDto) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                tipoPlanillaDto.getEmpleadosEliminados().add(emp);
                tbvEmpleados.getItems().remove(emp);
                tbvEmpleados.refresh();
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(cellButton);
            }
        }
    }

    private class ButtonCellMovimiento extends TableCell<TipMovPlanillaDto, Boolean> {

        final Button cellButton = new Button();

        ButtonCellMovimiento() {
            cellButton.setPrefWidth(500);
            cellButton.getStyleClass().add("jfx-btnimg-tbveliminar");

            cellButton.setOnAction((ActionEvent t) -> {
                TipMovPlanillaDto tmp = (TipMovPlanillaDto) ButtonCellMovimiento.this.getTableView().getItems().get(ButtonCellMovimiento.this.getIndex());
                // Solo se eliminan en el WS los que ya estaban guardados
                if (tmp.getId() != null) {
                    tipoPlanillaDto.getTiposMovimientoEliminados().add(tmp);
                }
                tbvMovimientos.getItems().remove(ButtonCellMovimiento.this.getIndex());
                tbvMovimientos.refresh();
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            setGraphic(empty ? null : cellButton);
        }
    }

}
