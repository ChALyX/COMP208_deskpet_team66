package pet_state;

public class CleanState {
    private int cleanState = 50;

    public static final int ReduceStep = 2;
    public static final int MaxValue = 100;
    public static final int MinValue = 0;

    public int getCleanState() {
        return cleanState;
    }

    public void setCleanState(int cleanState) {
        this.cleanState = cleanState;
    }

    public void reduce(){
        cleanState = Math.max(MinValue, cleanState-ReduceStep);
        System.out.printf("[CleanState::reduce] - 当前体力值=%d\n", cleanState);
    }

    public void increase(int num){
        cleanState = Math.min(MaxValue, cleanState+num);
        System.out.printf("[StaminaState::increase(%d)]-当前干净值=%d\n", num,cleanState);
    }

    public boolean canIncrease(){
        return cleanState < MaxValue;
    }
}

