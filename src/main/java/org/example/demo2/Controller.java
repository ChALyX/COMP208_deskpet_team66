package org.example.demo2;
import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.leewyatt.rxcontrols.controls.RXDigit;
import com.leewyatt.rxcontrols.controls.RXLineButton;
import com.leewyatt.rxcontrols.controls.RXTextField;
import com.leewyatt.rxcontrols.controls.RXTranslationButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.util.Duration;
import pet_class.PetController;
import pet_state.TotalState;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Objects;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.nio.file.FileSystems;



public class Controller  {
    private weather weather = new weather();
    PetController petController;
    FXTrayIcon icon;
    ArrayList<String> petNameList;
    Stage stage;
    int currentPet;
    private boolean timelineInitialized = false;
    @FXML
    private RXTranslationButton launchButton;
    @FXML
    private JFXButton refreshButton;
    @FXML
    private Label weatherLabel;
    @FXML
    private ImageView iconWeather;
    @FXML
    private ProgressBar energyBar;
    @FXML
    private ProgressBar CleanBar;
    @FXML
    private ProgressBar MoodBar;
    @FXML
    private JFXButton feedBtn;
    @FXML
    private JFXButton showerBtn;
    @FXML
    private TextField searchField;
    @FXML
    private RXTranslationButton searchButton;
    @FXML
    private Label notepadContentLabel;
    @FXML
    private RXLineButton openNotepadButton;
    @FXML
    private RXDigit hoursTensDigit; // 时钟的十位
    @FXML
    private RXDigit hoursOnesDigit; // 时钟的个位
    @FXML
    private RXDigit minutesTensDigit; // 分钟的十位
    @FXML
    private RXDigit minutesOnesDigit; // 分钟的个位
    @FXML
    private RXDigit secondsTensDigit; // 秒钟的十位
    @FXML
    private RXDigit secondsOnesDigit; // 秒钟的个位
    @FXML
    private LocalDateTime alarmTime;
    @FXML
    private MediaPlayer mediaPlayer;
    @FXML
    private Timer timer;
    @FXML
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    @FXML
    private RXTranslationButton setReminderButton; // 设置提醒的按钮
    @FXML
    private RXTextField myTextField;

    @FXML
    private LocalTime reminderTime;
    @FXML
    private LocalDateTime reminderDateTime; // 存储日期和时间

    @FXML
    private TimerTask reminderTask;
    @FXML
    private JFXCheckBox allowInteractionCheckBox; // 用于允许交互的复选框

    public void initialize() {
        loadNotepadContent();
        prepareMediaPlayer();

        try {
            // 创建并启动文件监听器
            Path path = Paths.get(System.getProperty("user.dir")); // 使用当前用户目录
            FileWatcher watcher = new FileWatcher(path, this);
            watcher.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 初始化一个Timer对象来更新时间
        if (this.timer != null) {
            this.timer.cancel();  // 如果之前有timer，先取消它
        }
        this.timer = new Timer(true); // 创建一个新的timer，守护线程模式
        TimerTask clockUpdateTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    LocalTime now = LocalTime.now();
                    updateDigits(now);
                });
            }
        };
        // 每秒更新一次
        this.timer.scheduleAtFixedRate(clockUpdateTask, 0, 1000);

        TotalState state = TotalState.getInstance();
        state.loadStateFromFile("pet_state.txt");
        updateState(); // 进度条
    }

    @FXML
    private void clearTextField() {
        myTextField.clear();
    }

    // 初始化MediaPlayer以播放音乐文件
    private void prepareMediaPlayer() {
        try {
            // 使用类加载器获取资源的URL
            URL resource = getClass().getResource("/org/example/demo2/mimimusic7sec.mp3");
            if (resource == null) {
                throw new IllegalArgumentException("Cannot find media file");
            }
            Media sound = new Media(resource.toString());
            mediaPlayer = new MediaPlayer(sound);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing the media player: " + e.getMessage());
        }
    }

    @FXML
    private void updateDigits(LocalTime now) {
        int hours = now.getHour();
        int minutes = now.getMinute();
        int seconds = now.getSecond();

        hoursTensDigit.setDigit(hours / 10);
        hoursOnesDigit.setDigit(hours % 10);
        minutesTensDigit.setDigit(minutes / 10);
        minutesOnesDigit.setDigit(minutes % 10);
        secondsTensDigit.setDigit(seconds / 10);
        secondsOnesDigit.setDigit(seconds % 10);
        if (alarmTime != null && LocalDateTime.now().isAfter(alarmTime)) {
            alarmTime = null;
            triggerAlarm();
        }



    }

    private void triggerAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }
    @FXML
    public void handleRefresh() {
        refreshButton.setText("Refresh");
        weather.refreshWeather();
        System.out.println("Weather refreshed!");
        weatherLabel.setText(weather.getMainWeather());
        String icon = weather.getIcon();
        iconWeather.setImage(new Image("https://openweathermap.org/img/wn/"+icon+"@2x.png"));

    }

    public void petState(){
        updateState();

    }
    @FXML
    public void updateState(){
        TotalState state=TotalState.getInstance();
        double energy = state.getStaminaState().getStamina()*0.01;
        double stamina = state.getCleanState().getCleanState()*0.01;
        double emotion = state.getEmotionState().getEmotion()*0.01;
        energyBar.setProgress(energy);
        CleanBar.setProgress(stamina);
        MoodBar.setProgress(emotion);

    }
    @FXML
    private void feedPet() {
        // 实现投喂宠物的逻辑
        TotalState state=TotalState.getInstance();
        if(state.getStaminaState().canIncrease()){
            state.getStaminaState().increase(10);
            state.getEmotionState().increase();
            state.saveStateToFile("pet_state.txt");
            updateState();

            if(petController != null) {
                petController.eatGif();  // 改变为进食的GIF
                petController.updateStatus("Yammy!");
            }
        }
    }
    @FXML
    private void bathePet() {
        // 实现洗澡宠物的逻辑
        TotalState state=TotalState.getInstance();

        if(state.getCleanState().canIncrease()){
            state.getCleanState().increase(10);
            state.getEmotionState().increase();
            state.saveStateToFile("pet_state.txt");
            updateState();

            if(petController != null) {
                petController.batheGif();  // 改变为洗澡的GIF
                petController.updateStatus("lalala~");
            }
        }

    }
    public void initializeStateReductionTimeline(){
        if (!timelineInitialized) {
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(30), // 每30秒执行一次
                    e -> {
                        TotalState state = TotalState.getInstance();
                        // 减少状态的逻辑
                        state.getEmotionState().reduce();
                        state.getStaminaState().reduce();
                        state.getCleanState().reduce();
                        state.saveStateToFile("pet_state.txt");

                        // 在JavaFX主线程上更新UI
                        Platform.runLater(() -> {
                            try {
                                updateState();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
            ));

            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
            timelineInitialized = true;
        }
    }
    @FXML  //网页快捷搜索
    private void handleSearchButtonAction(ActionEvent event) {
        String searchText = searchField.getText();
        if (searchText != null && !searchText.trim().isEmpty()) {
            try {
                searchText = URLEncoder.encode(searchText, StandardCharsets.UTF_8.toString());
                Desktop.getDesktop().browse(new URI("https://www.google.com/search?q=" + searchText));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace(); // TODO: handle exception
            }
        }
    }

    @FXML
    private void handleOpenNotepad(ActionEvent event) {
        File notepadFile = new File("notepad.txt");

        try {
            // 检查文件是否存在，如果不存在则创建一个新的文件
            if (!notepadFile.exists()) {
                boolean created = notepadFile.createNewFile();
                if (created) {
                    notepadContentLabel.setText("A new memo has been created.");
                    // 可选：在这里可以写入一些初始内容到文件中
                } else {
                    notepadContentLabel.setText("Unable to create memo file");
                    return;
                }
            }

            // 文件现在存在，可以打开它
            Desktop.getDesktop().open(notepadFile);

            // 读取文件内容并显示前40个字符
            BufferedReader reader = new BufferedReader(new FileReader(notepadFile));
            String firstLine = reader.readLine();
            reader.close();

            if (firstLine != null && firstLine.length() > 40) {
                notepadContentLabel.setText(firstLine.substring(0, 40) + "...");
            } else {
                notepadContentLabel.setText(firstLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
            notepadContentLabel.setText("备忘录文件操作失败。");
        }
    }

    void loadNotepadContent() {
        File notepadFile = new File("notepad.txt");
        if (notepadFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(notepadFile))) {
                String firstLine = reader.readLine();
                if (firstLine != null && firstLine.length() > 40) {
                    notepadContentLabel.setText(firstLine.substring(0, 40) + "...");
                } else {
                    notepadContentLabel.setText(firstLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
                notepadContentLabel.setText("Failed to read the memo file");
            }
        } else {
            notepadContentLabel.setText("The memo is empty.");
        }
    }
    // "Set"按钮的事件处理器
    private void setupReminderTimer() {
        timer = new Timer(true);
        reminderTask = new TimerTask() {
            @Override
            public void run() {
                LocalTime now = LocalTime.now();
                if (reminderTime != null && !now.isBefore(reminderTime)) {
                    Platform.runLater(() -> triggerAlarm());
                    reminderTime = null; // 清除提醒时间，避免重复播放
                    cancel(); // 取消定时任务
                }
            }
        };
        // 定时任务启动后不会立即执行，直到用户设置提醒时间
    }
    @FXML
    private void handleSetReminderAction() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime time = LocalTime.parse(myTextField.getText(), formatter);
            reminderDateTime = LocalDateTime.of(LocalDate.now(), time);

            // 如果设定的时间已经过去，则设置为第二天
            if (reminderDateTime.isBefore(LocalDateTime.now())) {
                reminderDateTime = reminderDateTime.plusDays(1);
            }

            // 如果已经有提醒任务在运行，先取消它
            if (reminderTask != null) {
                reminderTask.cancel();
                timer.purge(); // 清除已取消的任务
            }

            // 创建新的提醒任务
            reminderTask = new TimerTask() {
                @Override
                public void run() {
                    checkAndPlayReminder();
                }
            };

            // 计算延迟时间：从现在到设定提醒时间的毫秒数
            long delay = java.time.Duration.between(LocalDateTime.now(), reminderDateTime).toMillis();

            System.out.println("Setting reminder for " + delay + " milliseconds from now.");

            // 安排单次执行任务
            timer.schedule(reminderTask, delay);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid time format. Please use HH:mm format.");
        }
    }


    // 检查是否到了设置的提醒时间b并重新创建重新创建 MediaPlayer让闹钟可复用
    private void checkAndPlayReminder() {
        Platform.runLater(() -> {
            try {
                // 使用类加载器获取资源的URL
                URL resource = getClass().getResource("/org/example/demo2/mimimusic7sec.mp3");
                if (resource == null) {
                    throw new IllegalArgumentException("Cannot find media file");
                }
                Media sound = new Media(resource.toString());
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                }
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();
                System.out.println("MediaPlayer is playing.");
            } catch (Exception e) {
                System.err.println("Error initializing the media player: " + e.getMessage());
            }
        });
    }


    @FXML
    private void launchPet() {
        petNameList=new ArrayList<>();
        petNameList.add("biu");
        petNameList.add("knight");
        currentPet=0;
        petController=new PetController(petNameList.get(currentPet));
        petController.petShow();
    }

}
