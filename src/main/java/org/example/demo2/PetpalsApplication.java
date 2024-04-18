package org.example.demo2;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pet_state.TotalState;
import pet_class.PetController; // Import the PetController

public class PetpalsApplication extends Application {
    private static PetController petController; // 用于控制桌宠的实例
    private FXTrayIcon trayIcon;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("petpals.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // 第一个 SplitPane
        SplitPane mySplitPane = (SplitPane) root.lookup("#mySplitPane");
        if (mySplitPane != null) {
            final double fixedPosition = 0.28;
            mySplitPane.setDividerPositions(fixedPosition);
            mySplitPane.getDividers().get(0).positionProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() != fixedPosition) {
                    mySplitPane.setDividerPositions(fixedPosition);
                }
            });
        }

        stage.setMinWidth(600);
        stage.setMinHeight(400);
        stage.setMaxWidth(600);
        stage.setMaxHeight(400);
        stage.setTitle("Petpals");
        stage.show();
        TotalState.getInstance().loadStateFromFile("pet_state.txt");

        // 初始化系统托盘图标
        initializeTrayIcon(stage);
    }
    // 将initializePetController方法声明为静态方法，以确保它能够正确地处理静态变量
    private static void initializePetController() {
        if (petController == null) {
            // 只有当宠物控制器还没有被初始化时，才进行初始化
            petController = new PetController("biu");
        }
    }
    private void initializeTrayIcon(Stage stage) {
        trayIcon = new FXTrayIcon(stage, getClass().getResource("/pet_class/systemTray.png"));

        trayIcon.setTooltip("Petpals");

        // 显示宠物菜单项
        MenuItem showPetItem = new MenuItem("Show Pet");
        showPetItem.setOnAction(event -> Platform.runLater(() -> {
            initializePetController();
            petController.petShow();
        }));
        showPetItem.setOnAction(event -> Platform.runLater(() -> {
            if (petController == null) {
                // 第一次点击时，初始化并显示宠物
                petController = new PetController("biu");
                petController.petShow();
            } else {
                // 宠物已经初始化，只需要显示
                petController.petShow();
            }
        }));

        // 隐藏宠物菜单项
        MenuItem hidePetItem = new MenuItem("Hide Pet");
        hidePetItem.setOnAction(event -> Platform.runLater(() -> {
            if (petController != null) {
                // 如果宠物已经初始化，隐藏宠物
                petController.petHide();
            }
        }));

        // 退出应用程序菜单项
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> {
            Platform.exit();
            System.exit(0); // 完全退出应用
        });

        // 添加所有菜单项到托盘图标
        trayIcon.addMenuItem(showPetItem);
        trayIcon.addMenuItem(hidePetItem);
        trayIcon.addSeparator(); // 添加分隔线
        trayIcon.addMenuItem(exitItem);

        trayIcon.show(); // 显示系统托盘图标
    }

    @Override
    public void stop() throws Exception {
        // 保存宠物状态
        TotalState.getInstance().saveStateToFile("pet_state.txt");
        if (petController != null) {
            petController.stop();
        }
        if (trayIcon != null) {
            trayIcon.hide(); // 移除系统托盘图标
        }
        super.stop();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
