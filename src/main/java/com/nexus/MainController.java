// Placeholder for Member 2's Logic Engine
// Eventually: private FileIndexerService indexerService = new FileIndexerService();

package com.nexus;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import javafx.stage.DirectoryChooser;
import java.io.File;
import java.util.List;

import javafx.util.Callback;

public class MainController {
    @FXML
    private TableView<NexusFile> fileTable;
    @FXML
    private TableColumn<NexusFile, String> nameColumn;
    @FXML
    private TableColumn<NexusFile, String> typeColumn;
    @FXML
    private TableColumn<NexusFile, String> pathColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;

    private ObservableList<NexusFile> masterData = FXCollections.observableArrayList();
    private FilteredList<NexusFile> filteredData;

    @FXML
    public void initialize(){
        //Sets rules for what columnn to have what
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        pathColumn.setCellValueFactory(cellData -> cellData.getValue().pathProperty());
        statusLabel.setText("Nexus Explorer v1.0 | System Online");

        //All files show on table
        filteredData = new FilteredList<>(masterData, p -> true);
        fileTable.setItems(filteredData);

        //Search filter that changes the table live
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {filterData(newValue);});

        //If no file there
        fileTable.setPlaceholder(new Label("No files found. Try scanning a folder or changing your search."));
        //Display selected in status bar
        fileTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                statusLabel.setText("Selected: " + newSelection.getPath());
            }
        });

        setupContextMenu();
        simulateScan();

    }

    private void filterData(String query) {
        // We update the "Predicate" (the rule) of our FilteredList
        filteredData.setPredicate(file -> {
            // If search bar is empty, show everything
            if (query == null || query.isEmpty()) return true;
            String lowerCaseFilter = query.toLowerCase();

            // PLACEHOLDER: Member 2 will replace this .contains() with Fuzzy Search logic
            boolean matchesName = file.getName().toLowerCase().contains(lowerCaseFilter);
            boolean matchesTag = file.getTag().toLowerCase().contains(lowerCaseFilter);

            return matchesName || matchesTag;
        });


        // Update the UI feedback
        if (filteredData.isEmpty() && !query.isEmpty()) {
            statusLabel.setText("No results found for: " + query);
        } else {
            statusLabel.setText("Showing " + filteredData.size() + " files.");
        }
}
    @FXML
    private void handleScan() {
        DirectoryChooser dc = new DirectoryChooser();
        File selectedDir = dc.showDialog(searchField.getScene().getWindow());

        if (selectedDir != null) {
            // 1. Clear previous results so we don't double-up
            masterData.clear();
            startSmartScan(selectedDir);
        }
    }

    

    private void simulateScan() {
        // Member 1 prepares the UI
        progressBar.setVisible(true);
        statusLabel.setText("Scanning directory...");

        // This is how you run things in the background so the UI doesn't freeze
        Thread backgroundThread = new Thread(() -> {
            try {
                // This is where Member 2's code would go.
                // We simulate a 3-second scan:
                Thread.sleep(3000);

                // IMPORTANT: You can only touch UI elements from the Main Thread
                javafx.application.Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    statusLabel.setText("Scan Complete!");
                });
            } catch (InterruptedException e) { e.printStackTrace(); }
        });
        backgroundThread.setDaemon(true); // Closes the thread if you close the app
        backgroundThread.start();
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteFile = new MenuItem("Delete from View");
        deleteFile.setOnAction(e -> {
            NexusFile selected = fileTable.getSelectionModel().getSelectedItem();
            if (selected != null) masterData.remove(selected);
        });
        contextMenu.getItems().addAll(new MenuItem("Open File"), deleteFile, new SeparatorMenuItem(), new MenuItem("Properties"));
        fileTable.setContextMenu(contextMenu);
    }


private void startSmartScan(File rootFolder) {
    // 1. Prepare UI (Main Thread)
    progressBar.setVisible(true);
    statusLabel.setText("Scanning: " + rootFolder.getName());

    // 2. Start Background Engine (Worker Thread)
    Thread scanThread = new Thread(() -> {

        /// Member 2's scanning function call here -> (should return a "List<NexusFile> results")*****
        /// List<NexusFile> results = 'Member 2's function here'

        // 3. Update UI with Results (Back to Main Thread)
        Platform.runLater(() -> {
            masterData.addAll(results); // Add all found files to the table
            progressBar.setVisible(false);
            statusLabel.setText("Scan Complete. Found " + results.size() + " files.");

            /// Member 3's save to database function call here -> (take "masterData" as arguement)*****
            saveFilesToDatabase(masterData);
        });
    });

    scanThread.setDaemon(true); // Ensures the thread closes if you exit the app
    scanThread.start();
}

private boolean isFuzzyMatch(String query, String target) {
    /// Member 2 references their fuzzy search algorithm here
    return target.toLowerCase().contains(query.toLowerCase()); //placeholder
}

private void saveFilesToDatabase(List<NexusFile> files) {
    /// Member 3 references their saving function here
}

private List<NexusFile> loadFilesFromDatabase() {
    /// Member 3 references their loading function
    return new java.util.ArrayList<>();//placeholder
}
}
