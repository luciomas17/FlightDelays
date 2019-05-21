

/**
 * Sample Skeleton for 'ExtFlightDelays.fxml' Controller Class
 */

package it.polito.tdp.flightdelays;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
/**
 * Sample Skeleton for 'FlightDelays.fxml' Controller Class
 */



public class FlightDelaysController {
	
	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML // fx:id="distanzaMinima"
    private TextField distanzaMinima; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalizza"
    private Button btnAnalizza; // Value injected by FXMLLoader

    @FXML // fx:id="cmbBoxAeroportoPartenza"
    private ComboBox<String> cmbBoxAeroportoPartenza; // Value injected by FXMLLoader

    @FXML // fx:id="cmbBoxAeroportoArrivo"
    private ComboBox<String> cmbBoxAeroportoArrivo; // Value injected by FXMLLoader

    @FXML // fx:id="btnAeroportiConnessi"
    private Button btnAeroportiConnessi; // Value injected by FXMLLoader

    @FXML
    void doAnalizzaAeroporti(ActionEvent event) {
    	this.txtResult.clear();
    	
    	try {
    		int minAvgDistance = Integer.parseInt(this.distanzaMinima.getText().trim());
			
			model.createGraph(minAvgDistance);
	    	this.txtResult.appendText(String.format("Aeroporti analizzati! Trovati %d aeroporti e %d collegamenti.", 
	    			model.getVertexSetSize(), model.getEdgeSetSize()));
	    	
	    	List<Airport> airports = new ArrayList<Airport>(model.getAirportIdMap().values());
	    	for(Airport a : airports) {
	    		this.cmbBoxAeroportoArrivo.getItems().add(a.getId() + " - " + a.getIataCode() + " - " + a.getAirportName());
	    		this.cmbBoxAeroportoPartenza.getItems().add(a.getId() + " - " + a.getIataCode() + " - " + a.getAirportName());
	    	}
	    	
		} catch (NumberFormatException e) {
			e.printStackTrace();
			this.txtResult.appendText("Errore: input distanza minima non corretto!");
			return;
		}
    }

    @FXML
    void doTestConnessione(ActionEvent event) {
    	this.txtResult.clear();
    	
    	if(model.getAirportIdMap().size() == 0) {
    		this.txtResult.appendText("Errore: analizzare aeroporti prima di testare una connessione!");
    		return;
    	}
    	
    	String partenza[] = null;
    	String arrivo[] = null;
    	if(this.cmbBoxAeroportoPartenza.getSelectionModel().isEmpty())
    		this.txtResult.appendText("Errore: selezionare un aeroporto di partenza!");
    	else if(this.cmbBoxAeroportoArrivo.getSelectionModel().isEmpty())
    		this.txtResult.appendText("Errore: selezionare un aeroporto di arrivo!");
    	else {
    		partenza = this.cmbBoxAeroportoPartenza.getSelectionModel().getSelectedItem().split(" - ");
    		arrivo = this.cmbBoxAeroportoArrivo.getSelectionModel().getSelectedItem().split(" - ");
    	}
    	
    	if(!model.testConnection(Integer.parseInt(partenza[0]), Integer.parseInt(arrivo[0])))
    		this.txtResult.appendText("Errore: non esiste connessione tra gli aeroporti selezionati!");
    	else {
    		this.txtResult.appendText("Connessione trovata!\nRotta:  ");
    		
    		this.txtResult.appendText(model.findRoute(Integer.parseInt(partenza[0]), Integer.parseInt(arrivo[0])).toString());
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert distanzaMinima != null : "fx:id=\"distanzaMinima\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert btnAnalizza != null : "fx:id=\"btnAnalizza\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert cmbBoxAeroportoPartenza != null : "fx:id=\"cmbBoxAeroportoPartenza\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert cmbBoxAeroportoArrivo != null : "fx:id=\"cmbBoxAeroportoArrivo\" was not injected: check your FXML file 'FlightDelays.fxml'.";
        assert btnAeroportiConnessi != null : "fx:id=\"btnAeroportiConnessi\" was not injected: check your FXML file 'FlightDelays.fxml'.";

    }
    
    public void setModel(Model model) {
		this.model = model;
	}
}




