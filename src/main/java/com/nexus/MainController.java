// Placeholder for Member 2's Logic Engine
// Eventually: private FileIndexerService indexerService = new FileIndexerService();

package com.nexus;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableCell;
import javafx.application.Platform;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import javafx.stage.DirectoryChooser;
import java.io.File;
import java.util.List;

import java.awt.Desktop;

import javafx.util.Callback;

public class MainController {
    private FileIndexerService indexerService = new FileIndexerService();
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
    @FXML
    private VBox sidebarVBox;
    @FXML
    private Button allFilesBtn;
    @FXML
    private Button javaProjectBtn;
    @FXML
    private Button assignmentsBtn;
    @FXML
    private Button addWorkspaceBtn;

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
        setupContextMenu();
        fileTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && fileTable.getSelectionModel().getSelectedItem() != null) {
                NexusFile selectedFile = fileTable.getSelectionModel().getSelectedItem();
                openFileInOS(selectedFile.getPath());
            }
        });

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

        // Load previously saved files from the database on startup
        List<NexusFile> savedFiles = loadFilesFromDatabase();
        masterData.addAll(savedFiles);

        // Listen for any changes (scans, loads, or deletes) to update counts
        masterData.addListener((javafx.beans.Observable c) -> {
            updateWorkspaceCounts();
        });
    }


    private void filterData(String query) {
        filteredData.setPredicate(file -> {
            // 1. If the search bar is empty, show everything
            if (query == null || query.isEmpty()) {
                return true;
            }

            // Member 2's fuzzy search methods here
            boolean matchesName = isFuzzyMatch(query, file.getName());
            boolean matchesTag = isFuzzyMatch(query, file.getTag());

            // 4. If either matches, keep the file in the table
            return matchesName || matchesTag;
        });

        // 5. Update the UI Label to tell the user how many matches were found
        if (filteredData.isEmpty() && !query.isEmpty()) {
            statusLabel.setText("No results found for: " + query);
        } else {
            statusLabel.setText("Matches found: " + filteredData.size());
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

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteFile = new MenuItem("Delete from View");
        MenuItem clearWorkspace = new MenuItem("Clear Workspace");
        clearWorkspace.setOnAction(e -> {
            masterData.clear();
            clearDatabase();
            statusLabel.setText("Workspace cleared.");
        });
        deleteFile.setOnAction(e -> {
            NexusFile selected = fileTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                    masterData.remove(selected);
                    deleteFileFromDatabase(selected);
            }
        });
        contextMenu.getItems().addAll(new MenuItem("Open File"), deleteFile, new SeparatorMenuItem(), clearWorkspace, new SeparatorMenuItem(), new MenuItem("Properties"));
        fileTable.setContextMenu(contextMenu);
    }

    private void updateWorkspaceCounts() {
        // 1. All Files Count
        allFilesBtn.setText("All Files (" + masterData.size() + ")");

        // 2. Java Project Count
        long javaCount = masterData.stream()
            .filter(f -> f.getType().equalsIgnoreCase("JAVA") || f.getTag().equals("#Code"))
            .count();
        javaProjectBtn.setText("Java Project (" + javaCount + ")");

        // 3. Assignments Count
        long assignCount = masterData.stream()
            .filter(f -> f.getTag().equalsIgnoreCase("#Notes"))
            .count();
        assignmentsBtn.setText("Assignments (" + assignCount + ")");
        
        // Note: If you add dynamic buttons, you'd loop through sidebarVBox here.
    }

    @FXML
    private void handleSidebarAction(javafx.event.ActionEvent event) {
        // 1. Identify which button was clicked
        Button clickedButton = (Button) event.getSource();
        String rawText = clickedButton.getText();
        String workspaceName = rawText.contains("(") ? rawText.substring(0, rawText.indexOf(" (")).trim() : rawText.trim();

        // 2. Update the status bar for visual feedback
        statusLabel.setText("Workspace: " + workspaceName);

        // 3. Apply the workspace filter
        filteredData.setPredicate(file -> {
            // "All Files" shows everything
            if (workspaceName.equalsIgnoreCase("All Files")) {
                return true;
            }

            // Filter logic based on the workspace name
            switch (workspaceName.toLowerCase()) {
                case "java project":
                    return file.getType().equals("JAVA") || file.getTag().equals("#Code");
                case "assignments":
                    return file.getTag().equals("#Notes");
                default:
                    // For custom tags, check if the file's tag matches the button text
                    return file.getName().toLowerCase().contains(workspaceName.toLowerCase()) ||
                       file.getTag().equalsIgnoreCase("#" + workspaceName);
            }
        });
        statusLabel.setText("Active Workspace: " + workspaceName);
        // Remove "active" style from all buttons (assuming you have a VBox 'sidebarVBox')
        sidebarVBox.getChildren().forEach(node -> node.getStyleClass().remove("active-workspace"));
        // Add "active" style to the clicked button`
        clickedButton.getStyleClass().add("active-workspace");
        searchField.clear();
    }
    @FXML
    private void handleAddNewWorkspace() {
        TextInputDialog dialog = new TextInputDialog("New Workspace");
        dialog.setTitle("Create Workspace");
        dialog.setHeaderText("Enter workspace name (e.g., 'Exams'):");

        dialog.showAndWait().ifPresent(name -> {
            // 1. Create the new button
            Button newBtn = new Button(name);
            newBtn.setMaxWidth(Double.MAX_VALUE); // Make it fill the sidebar width
            newBtn.getStyleClass().add("sidebar-button"); // Keep your styling
            // 2. Wire it to the same action handler as the others
            newBtn.setOnAction(this::handleSidebarAction);

            // 3. Add it to the sidebar container
            sidebarVBox.getChildren().add(sidebarVBox.getChildren().size() - 1, newBtn);

            statusLabel.setText("Created Workspace: " + name);
        });
    }

    private void startSmartScan(File rootFolder) {
        // 1. Prepare UI (Main Thread)
        progressBar.setVisible(true);
        statusLabel.setText("Scanning: " + rootFolder.getName());

        // 2. Start Background Engine (Worker Thread)
        Thread scanThread = new Thread(() -> {

            /// Member 2's scanning function call here -> (should return a "List<NexusFile> results")*****
            /// List<NexusFile> results = 'Member 2's function here' 
            List<NexusFile> results = indexerService.simpleScan(rootFolder);
            // 3. Update UI with Results (Back to Main Thread)
            Platform.runLater(() -> {
                masterData.addAll(results);
                filterData("");
                fileTable.refresh();
                allFilesBtn.fire();
                searchField.clear();

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

    private void deleteFileFromDatabase(NexusFile file) { 
    /// Member 3's delete from database method here
    }

}