package pet_class;


import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.leewyatt.rxcontrols.controls.RXTranslationButton;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import org.example.demo2.Controller;
import org.example.demo2.weather;
import pet_state.TotalState;

public class PetController extends Application {

    enum action{
        stay,
        left,
        right
    }
    Stage stage;
    ImageView petView;
    Image gifImage;
    String petName;
    ArrayList<Image> gifImageList;
    double screenHeight;
    double screenWidth;
    double disx;
    double disy;
    private StackPane root;
    private Scene scene;
    private action currentAction;
    DialogController dialogController;
    private Timeline autoMoveTimeline;
    private Random random = new Random();
    private int stepsInCurrentDirection = 0; // 在当前方向上的步数
    private final int MAX_STEPS_IN_DIRECTION = 60; // 在改变方向之前的最大步数
    private boolean isDraggable = false;
    @FXML
    private JFXCheckBox dragCheckBox;


    public PetController(String petName){
        this.petName=petName;
        start(new Stage());

    }    @Override
    public void start(Stage stage) {
        this.stage=stage;
        stageInit();

        petView=new ImageView();
        petViewInit();

        loadGifToList();

        currentAction=action.stay;
        petSetPic();

        root = new StackPane();
        root.getChildren().add(petView);
        root.setStyle("-fx-background-color: transparent;");
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        screenHeight=bounds.getHeight();
        screenWidth= bounds.getWidth();
        // 创建场景
        scene = new Scene(root,screenHeight*gifImage.getWidth()/gifImage.getHeight()/7 , screenHeight/7);
        //设置背景为空
        scene.setFill(null);
        // 设置场景并显示舞台
        stage.setScene(scene);
        dialogController=new DialogController(screenWidth,screenHeight);
        petSetCod(screenWidth/2,screenHeight/2);
        initializeContextMenu();
        initializeStateReductionTimeline();

        initAutoMove();

    }
    private void initAutoMove() {
        autoMoveTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> autoMove())); //平滑移动
        autoMoveTimeline.setCycleCount(Timeline.INDEFINITE);
        autoMoveTimeline.play();
    }

    private void autoMove() {
        // 随机选择移动方向：0=左，1=右,2=停下来
        int direction = random.nextInt(3);
        if (stepsInCurrentDirection >= MAX_STEPS_IN_DIRECTION) {
            direction = random.nextInt(3); // 选择新方向
            stepsInCurrentDirection = 0; // 重置步数计数器
        }


        switch (direction) {
            case 0: // 左
                if (stage.getX() > 0) {
                    petSetCod(stage.getX() - 15, stage.getY());
                    petAct(action.left);
                    resetAutoMoveTimer(2); // 每次移动2秒
                }
                break;
            case 1: // 右
                if (stage.getX() + gifImage.getWidth() < screenWidth) {
                    petSetCod(stage.getX() + 15, stage.getY());
                    petAct(action.right);
                    resetAutoMoveTimer(2); // 每次移动2秒
                }
                break;
            case 2: // stay
                petAct(action.stay);
                resetAutoMoveTimer(10); // stay状态停留10秒
                break;
        }
    }

    private void resetAutoMoveTimer(int seconds) {
        autoMoveTimeline.stop();
        autoMoveTimeline.getKeyFrames().setAll(new KeyFrame(Duration.seconds(seconds), e -> autoMove()));
        autoMoveTimeline.playFromStart();
    }
    /**--------------------------------公开接口------------------------------**/
    @Override
    public void stop()
    {
        stage.close();
        TotalState.getInstance().saveStateToFile("pet_state.txt");
    }
    public void petShow(){
        stage.show();
        dialogController.dialogShow();
    }
    public void petHide(){
        stage.hide();
        dialogController.dialogHide();
    }
    public void petClose(){
        dialogController.dialogClose();
        stage.close();
    }
    public double getX(){
        return stage.getX();
    }
    public double getY(){
        return stage.getY();
    }
    public void setCode(double x,double y){
        petSetCod(x,y);
    }

    private void initializeContextMenu() {
        // 创建上下文菜单
        ContextMenu contextMenu = new ContextMenu();

        // 创建菜单项
        MenuItem feedItem = new MenuItem("Feed");
        feedItem.setOnAction(event -> feedPet());

        MenuItem batheItem = new MenuItem("Bath");
        batheItem.setOnAction(event ->bathePet());
        // 添加显示和隐藏宠物的菜单项
        MenuItem showPetItem = new MenuItem("Show pet");
        showPetItem.setOnAction(event -> petShow());

        MenuItem hidePetItem = new MenuItem("Hide pet");
        hidePetItem.setOnAction(event ->petHide());
        // 将菜单项添加到上下文菜单
        contextMenu.getItems().addAll(feedItem, batheItem, showPetItem, hidePetItem);

        // 设置当右击宠物图像时显示上下文菜单
        petView.setOnContextMenuRequested(event -> contextMenu.show(petView, event.getScreenX(), event.getScreenY()));
    }
    /**---------------------------------逻辑函数--------------------------------------**/
    public void eatGif() {
        Image eatingGif = new Image(getClass().getResourceAsStream("/pet_class/biu/eat.gif"));
        petView.setImage(eatingGif);

        Timeline resetTimeline = new Timeline(new KeyFrame(
                Duration.seconds(9),
                ae -> resetToMainGif()));
        resetTimeline.setCycleCount(1); // 只执行一次
        resetTimeline.play();
    }
    public void batheGif(){
        Image batheGif = new Image(getClass().getResourceAsStream("/pet_class/biu/bathe.gif"));
        petView.setImage(batheGif);

        Timeline resetTimeline = new Timeline(new KeyFrame(
                Duration.seconds(5),
                ae -> resetToMainGif()));
        resetTimeline.setCycleCount(1); // 只执行一次
        resetTimeline.play();
    }
    private void feedPet() {
        // 实现投喂宠物的逻辑
        TotalState state=TotalState.getInstance();
        if(state.getStaminaState().canIncrease()){
            state.getStaminaState().increase(10);
        }
        state.getEmotionState().increase();
        state.saveStateToFile("pet_state.txt");
        updateStatus("Thank you for the food");

    }

    private void bathePet() {
        // 实现洗澡宠物的逻辑
        TotalState state=TotalState.getInstance();

        if(state.getCleanState().canIncrease()){
            state.getCleanState().increase(10);
        }
        state.getEmotionState().increase();
        state.saveStateToFile("pet_state.txt");
        updateStatus("I love baths~");
    }

    public void updateStatus(String message){
        dialogController.setDialogString(message);
        dialogController.dialogShow();
    }
    public void playGif() {
        Image playGif = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pet_class/biu/play.gif")));
        petView.setImage(playGif);
    }

    public void resetToMainGif(){
        petView.setImage(gifImageList.get(0));
    }
    private void initializeStateReductionTimeline(){
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(30), // 每30秒执行一次
                e -> {
                    TotalState state = TotalState.getInstance();
                    state.getEmotionState().reduce();
                    state.getStaminaState().reduce();
                    state.getCleanState().reduce();
                    state.saveStateToFile("pet_state.txt");
                    //controller.updateState();
                }
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }


    private void petAct(action a){
        if (currentAction.equals(a))
            return;
        currentAction=a;
        switch (a){
            case right:
                dialogController.setDialogString("I'm moving right!");
                break;
            case left:
                dialogController.setDialogString("I'm moving left!");
                break;
            case stay:
                dialogController.setDialogString("I stopped!");
                break;
        }
        petSetPic();
    }

    /**----------------------------------功能函数------------------------------------------**/
    private void petSetPic(){
        switch (currentAction) {
            case stay:
                gifImage=gifImageList.get(0);
                break;
            case left:
                gifImage=gifImageList.get(1);
                break;
            case right:
                gifImage=gifImageList.get(2);
                break;
        }
        // 创建ImageView对象并设置GIF图像
        petView.setImage(gifImage);

    }

    private void loadGifToList(){
        gifImageList=new ArrayList<>();
        gifImageList.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(petName+'/'+"main.gif"))));
        gifImageList.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(petName+'/'+"left.gif"))));
        gifImageList.add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(petName+'/'+"right.gif"))));
    }

    //设置宠物坐标
    private void petSetCod(double x,double y) {
        stage.setX(x);
        stage.setY(y);
        dialogController.setDialogCod(x+screenHeight*gifImage.getWidth()/gifImage.getHeight()/15,y-screenHeight/15);
    }


    public void Timeline(){
        // 创建Timeline对象用于播放GIF动画
//        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
//            // 在此处添加GIF动画每一帧后续的逻辑（如果需要）
//            System.out.println("time line activate");
//            if(ImageArgs.getImageArgs().randomChangeArg) {
//                Image gifImage=new Image(Objects.requireNonNull(getClass().getResourceAsStream(ImageArgs.getImageArgs().getImageName())));
//                petView.setImage(gifImage);
//            }
//        }));
//        timeline.setCycleCount(Animation.INDEFINITE); // 无限循环播放
//        timeline.play();
    }



    public void petSpeak(){

    }

    /**---------------------------------------------初始化函数----------------------------------------------**/
    private void stageInit(){
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("deskpot_pet");
        stage.setAlwaysOnTop(true);
    }

    private void petViewInit(){
        petView.setOnMouseDragged((MouseEvent event) -> {
            if (isDraggable) { // 检查拖拽是否启用
                petSetCod(event.getScreenX() - disx, event.getScreenY() - disy);
                event.consume();
            }
        });//设置拖拽
        petView.setOnMousePressed((MouseEvent event) -> {
            if (event.isPrimaryButtonDown()) {
                playGif();
            }
            disx=event.getScreenX() - stage.getX();
            disy=event.getScreenY() - stage.getY();
            //获取焦点时键盘事件可用
            petView.requestFocus();
            event.consume();
        });
        petView.setOnMouseReleased((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                resetToMainGif();
            }
        });
        petView.setOnMouseDragged((MouseEvent event) -> {
            petSetCod(event.getScreenX() - disx,event.getScreenY() - disy);
            event.consume();
        });
//        petView.setOnKeyPressed((KeyEvent event)->{
//            petMove(event.getCode());
//            event.consume();
//        });
        petView.setOnKeyReleased((KeyEvent event)->{
            petAct(action.stay);
            event.consume();
        });
        petView.setOnMouseClicked((MouseEvent event)->{
            dialogController.setDialogString("Hello!");
            event.consume();
            dialogController.dialogShow();
        });


        petView.fitWidthProperty().bind(stage.widthProperty());
        petView.fitHeightProperty().bind(stage.heightProperty());


    }
    /**-------------------------------------------------------------------------------------------**/


}
