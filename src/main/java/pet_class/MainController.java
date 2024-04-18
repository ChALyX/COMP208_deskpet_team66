package pet_class;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Objects;

public class MainController extends Application {
    PetController petController;
    FXTrayIcon icon;
    ArrayList<String> petNameList;
    Stage stage;
    int currentPet;

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage){
        petNameList=new ArrayList<>();
        petNameList.add("biu");
        petNameList.add("knight");
        currentPet=0;
        this.stage=stage;

        petController=new PetController(petNameList.get(currentPet));
        petController.petShow();
        icon = new FXTrayIcon.Builder(stage, Objects.requireNonNull(getClass().getResource("systemTray.png")))
                .menuItem("change",e->stageChange())
                .menuItem("show",e->stageShow())
                .menuItem("hide",e->stageHide())
                .addExitMenuItem("quit")
                .build();
        icon.show();

    }

    public void stageChange(){
        PetController oldPet=petController;
        petController.petClose();
        currentPet=1-currentPet;
        petController=new PetController(petNameList.get(currentPet));
        petController.setCode(oldPet.getX(), oldPet.getY());
        oldPet.petClose();
        petController.petShow();
    }
    public void stageShow(){
        petController.petShow();
    }

    public void stageHide(){
        petController.petHide();
    }

}
