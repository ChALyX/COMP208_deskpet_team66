package pet_state;

public class EmotionState {
    private int emotion = 50;

    public static final int IncreaseStep = 10;
    public static final int ReduceStep = 5;
    public static final int MaxValue = 100;
    public static final int MinValue = 0;

    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public void reduce(){
        emotion = Math.max(MinValue, emotion-ReduceStep);
        System.out.printf("[EmotionState::reduce] - 当前体力值=%d\n", emotion);
    }

    public void increase(){
        emotion = Math.min(MaxValue, emotion+IncreaseStep);
        System.out.printf("[EmotionState::increase]-当前心情值=%d\n", emotion);
    }
}

