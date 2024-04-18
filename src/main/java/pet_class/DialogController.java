package pet_class;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

public class DialogController extends Application{
    private Image backgroundImage;
    private ImageView backgroundImageView;
    private StackPane backgroundPane;
    private Label labelText;
    private Scene scene;
    private Stage stage;
    private ArrayList<String> dialogPicDirList;   //图片路径
    private String dialogPicDir;
    private double screenWidth;
    private double screenHeight;

    DialogController(double screenWidth,double screenHeight){
        this.screenWidth=screenWidth;
        this.screenHeight=screenHeight;
        start(new Stage());

    }
    @Override
    public void start(Stage stage){
        this.stage=stage;
        stage.setTitle("Pet Dialog");
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        // 创建背景容器
        backgroundPane = new StackPane();

        dialogPicDirList=new ArrayList<>();
        dialogPicDirList.add("dialogBox.png");
        dialogPicDir=dialogPicDirList.get(0);
        // 添加背景图片
        setDialogBackground();
        backgroundPane.getChildren().add(backgroundImageView);

        // 创建Label作为文本内容
        labelText = new Label("Hello!");
        labelText.setStyle("-fx-text-fill: black; "); // 设置文本颜色为黑色
        labelText.setWrapText(true);
        labelText.setAlignment(Pos.CENTER);
        labelText.setMaxWidth(screenHeight*backgroundImage.getWidth()/backgroundImage.getHeight()/20);
        labelText.setMaxHeight(screenHeight/20);

        labelText.setTranslateY(-screenHeight/50);
        labelText.setTranslateX(screenHeight*backgroundImage.getWidth()/backgroundImage.getHeight()/180);

        // 将Label放置在背景容器上
        backgroundPane.getChildren().add(labelText);
        backgroundPane.setStyle("-fx-background-color: transparent;");

        backgroundPane.setOnMouseClicked((MouseEvent e)->{
            dialogHide();
            e.consume();
        });

        scene = new Scene(backgroundPane, screenHeight*backgroundImage.getWidth()/backgroundImage.getHeight()/10 , screenHeight/10);
        scene.setFill(null);
        // 设置场景并显示主舞台
        stage.setScene(scene);
        backgroundImageView.fitWidthProperty().bind(stage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(stage.heightProperty());
    }
    public void setDialogBackground() {
        backgroundImage = new Image(getClass().getResourceAsStream(dialogPicDir));
        if(backgroundImageView==null)
            backgroundImageView=new ImageView();
        backgroundImageView.setImage(backgroundImage);
    }

    public void setDialogString(String string){
        labelText.setText(string);
    }
    public void setDialogCod(double x,double y){
        stage.setX(x);
        stage.setY(y);
    }
    public void dialogShow(){
        stage.show();
    }

    public void dialogHide(){
        stage.hide();
    }

    public void dialogClose(){
        stage.close();
    }

}