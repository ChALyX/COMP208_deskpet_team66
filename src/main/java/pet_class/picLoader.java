package pet_class;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class picLoader extends Application {

    private final ArrayList<String> selectedFiles = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Image Folder Explorer");

        Button browseButton = new Button("Browse");
        Label resultLabel = new Label();
        Button processButton = new Button("Process Selected Folder");

        browseButton.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);

            if (selectedDirectory != null) {
                // 清空之前的选择
                selectedFiles.clear();
                List<String> missingFiles=getMissingImageFiles(selectedDirectory, "front.gif", "back.gif", "left.gif", "right.gif");
                // 判断是否包含指定的图片文件
                if (missingFiles.isEmpty()) {
                    resultLabel.setText("Import Successful!");
                    resultLabel.setStyle("-fx-text-fill: green;");
                } else {
                    resultLabel.setText("Import Failed. Missing required image files.\n"+ String.join(", \n", missingFiles));
                    resultLabel.setStyle("-fx-text-fill: red;");
                }
            }
        });

        processButton.setOnAction(e -> {
            String selectedFolderPath = resultLabel.getText();
            if (!selectedFolderPath.isEmpty()) {
                System.out.println("Selected Folder Path: " + selectedFolderPath);
            } else {
                System.out.println("No folder selected.");
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(browseButton, resultLabel, processButton);

        Scene scene = new Scene(layout, 400, 150);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private List<String> getMissingImageFiles(File directory, String... imageFiles) {
        List<String> requiredFiles = Arrays.asList(imageFiles);
        List<String> missingFiles = new ArrayList<>();

        File[] files = directory.listFiles();
        if (files != null) {
            for (String requiredFile : requiredFiles) {
                boolean found = false;
                for (File file : files) {
                    if (file.isFile() && file.getName().equals(requiredFile)) {
                        selectedFiles.add(file.getAbsolutePath());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    missingFiles.add(requiredFile);
                }
            }
        }

        return missingFiles;
    }
}
