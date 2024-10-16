package com.example;

import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class Window extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    private Button createFolder;
    private Stage primaryStage;
    private File selectedDirectory;
    private Scene mainScene;
    private MenuBar menuBar;
    private String css;
    private Text statusText;
    private TextField nameTextField;
    private Spinner<Integer> seasonSpinner;
    private Spinner<Integer> episodeSpinner;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("File Maker");
        Image icon = new Image("icon.png");
        primaryStage.getIcons().add(icon);

        VBox mainVBox = new VBox();

        mainVBox.getChildren().add(menuBar());
        mainVBox.getChildren().add(createButtons());

        mainScene = new Scene(mainVBox);
        mainScene.setFill(Color.rgb(240, 233, 223));

        css = this.getClass().getResource("/Styling.css").toExternalForm();
        mainScene.getStylesheets().add(css);

        primaryStage.setHeight(400);
        primaryStage.setWidth(900);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        //https://stackoverflow.com/questions/47585211/a-menu-that-triggers-purely-on-hover-in-javafx
        // HBox container = (HBox) menuBar.lookup("HBox");

        // for (int i = 0; i < container.getChildren().size(); i++) {
        //     Node parentNode = container.getChildren().get(i);
        //     Menu menu = menuBar.getMenus().get(i);

        //     parentNode.setOnMouseMoved(e -> {
        //         menu.show();
        //     });
        // }
    }

    private Node menuBar() {
        menuBar = new MenuBar();

        Menu chooseDirectoryMenu = new Menu("File");
        MenuItem chooseDirectoryItem = new MenuItem("Choose Directory");
        chooseDirectoryItem.setOnAction(e -> chooseDirectory());
        chooseDirectoryMenu.getItems().add(chooseDirectoryItem);

        Menu helpMenu = new Menu("Help");
        MenuItem helpItem = new MenuItem("Help");
        helpItem.setOnAction(e -> help());
        helpMenu.getItems().add(helpItem);

        menuBar.getMenus().add(chooseDirectoryMenu);
        menuBar.getMenus().add(helpMenu);

        return menuBar;
    }

    private void help() {
        String rules =  "-Below type in what you want the folder to look like\n" +
                        "-Example: 'The Office', then select directory for folders to be located\n" +
                        "-Select the season and number of episodes and click 'generate'\n" +
                        "-Program will then add the desired number of folders in the directory\n" +
                        "-Result will look like 'The Office S03E01' + etc.";
        Stage rulesStage = new Stage();
        rulesStage.setTitle("Help");

        TextArea rulesArea = new TextArea(rules);
        rulesArea.setEditable(false);

        Scene rulesScene = new Scene(rulesArea, 650, 150);

        rulesStage.setScene(rulesScene);
        rulesScene.getStylesheets().add(css);

        rulesStage.show();
    }

    private VBox createButtons() {
        VBox mainVBox = new VBox();
        VBox seasonVBox = new VBox();
        VBox episodeVBox = new VBox();
        VBox showNameVBox = new VBox();
        VBox generateTextVBox = new VBox();

        nameTextField = new TextField();

        seasonSpinner = new Spinner<>(1, 999, 1);
        episodeSpinner = new Spinner<>(1, 999, 1);
        Platform.runLater(() -> {
            nameTextField.setPrefWidth(seasonSpinner.getWidth() * 2.5);
            nameTextField.setText("show/movie name");
            nameTextField.setStyle("-fx-font-size: 12px;");

            Text text = new Text(nameTextField.getText());
            text.setFont(nameTextField.getFont());
            double height = text.getLayoutBounds().getHeight();

            nameTextField.setPrefHeight(height);

        });
        seasonSpinner.setEditable(true);
        episodeSpinner.setEditable(true);
        nameTextField.setEditable(true);
        
        Text seasonText = new Text("Season");
        Text episodeText = new Text("Episodes");
        Text showNameText = new Text("Show/Movie Name");
        Text generateText = new Text("");
        statusText = new Text("");

        seasonVBox.getChildren().addAll(seasonText, seasonSpinner);
        episodeVBox.getChildren().addAll(episodeText, episodeSpinner);
        showNameVBox.getChildren().addAll(showNameText, nameTextField);

        HBox mainHBox = new HBox();
        HBox statusHBox = new HBox();

        createFolder = new Button("Generate Folders");
        createFolder.setOnAction(e -> generateFolders());
        generateTextVBox.getChildren().addAll(generateText, createFolder);

        mainHBox.getChildren().addAll(showNameVBox, seasonVBox, episodeVBox, generateTextVBox);
        mainHBox.setAlignment(Pos.CENTER);
        statusHBox.getChildren().add(statusText);
        statusHBox.setAlignment(Pos.CENTER);


        mainVBox.getChildren().addAll(mainHBox, statusHBox);

        return mainVBox;
    }

    private void generateFolders() {
        if (selectedDirectory == null) {
            statusText.setText("Select a directory!");
            return;
        }
        statusText.setText("");
        Platform.runLater(() -> mainScene.setCursor(Cursor.WAIT));
        System.out.println("Working");

        String directoryPath = selectedDirectory.getPath() + "";
        if (directoryPath.contains("\\")) { // windows file system
            directoryPath += "\\";
        } else { // linux or mac file system
            directoryPath += "/";
        }

        String baseFileName = nameTextField.getText();
        baseFileName = baseFileName.trim();
        System.out.println(baseFileName);
        if (seasonSpinner.getValue() < 10) {
            baseFileName += " S0" + seasonSpinner.getValue() + "E";
        } else {
            baseFileName += " S" + seasonSpinner.getValue() + "E";
        }

        int numberOfEpisodes = (int)(episodeSpinner.getValue());

        for (int i = 1; i <= numberOfEpisodes; i++) {
            String tempFileName = "";
            if (i <= 9) {
                tempFileName = baseFileName + "0" + Integer.toString(i);
            } else {
                tempFileName = baseFileName + Integer.toString(i);
            }

            File file = new File(directoryPath + tempFileName);

                if (file.mkdir()) {
                    System.out.println("Successful");
                } else {
                    System.out.println("File already exists");
                }
            }
        Platform.runLater(() -> mainScene.setCursor(Cursor.DEFAULT));
        System.out.println("done");
    }

    private void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        selectedDirectory = directoryChooser.showDialog(primaryStage);
    }
}