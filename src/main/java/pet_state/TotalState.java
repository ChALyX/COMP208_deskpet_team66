package pet_state;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TotalState {
    private static TotalState totalState;

    private final EmotionState emotionState;
    private final StaminaState staminaState;
    private final CleanState cleanState;

    private TotalState() {
        emotionState = new EmotionState();
        staminaState = new StaminaState();
        cleanState = new CleanState();
    }

    public static TotalState getInstance() {
        if (totalState == null) totalState = new TotalState();
        return totalState;
    }

    public EmotionState getEmotionState() {
        return emotionState;
    }

    public StaminaState getStaminaState() {
        return staminaState;
    }

    public CleanState getCleanState() {
        return cleanState;
    }

    public void saveStateToFile(String filename) {
        Path path = Paths.get(filename);
        // 尝试创建文件，如果文件已经存在，则这个方法不会做任何事情
        try {
            Files.createFile(path);
        } catch (FileAlreadyExistsException e) {
            // 文件已经存在，不需要处理这个异常
        } catch (IOException e) {
            e.printStackTrace(); // 处理其他文件创建异常
        }

        // 保存状态到文件
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(path))) {
            writer.println(emotionState.getEmotion());
            writer.println(staminaState.getStamina());
            writer.println(cleanState.getCleanState());
            System.out.println("状态已保存到文件: " + filename);
        } catch (Exception e) {
            e.printStackTrace(); // 处理写入文件的异常
        }
    }

    public void loadStateFromFile(String filename) {
        Path path = Paths.get(filename);
        // 检查文件是否存在
        if (!Files.exists(path)) {
            // 文件不存在，创建新的文件并初始化状态值
            try {
                Files.createFile(path);
                saveStateToFile(filename); // 使用初始状态值保存到文件
            } catch (IOException e) {
                e.printStackTrace(); // 处理创建文件的异常
            }
        }

        // 从现有文件加载状态
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            emotionState.setEmotion(Integer.parseInt(reader.readLine()));
            staminaState.setStamina(Integer.parseInt(reader.readLine()));
            cleanState.setCleanState(Integer.parseInt(reader.readLine()));
        } catch (Exception e) {
            e.printStackTrace(); // 处理读取文件的异常
        }
    }
}
