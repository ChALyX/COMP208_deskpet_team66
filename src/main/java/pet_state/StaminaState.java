package pet_state;

public class StaminaState {

    private int stamina = 50;

    public static final int ReduceStep = 2;
    public static final int MaxValue = 100;
    public static final int MinValue = 0;

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public void reduce(){
        stamina = Math.min(MaxValue, stamina-ReduceStep);
        System.out.printf("[StaminaState::reduce] - 当前体力值=%d\n", stamina);
    }

    public void increase(int num){
        stamina = Math.min(stamina + num, MaxValue);
        System.out.printf("[StaminaState::increase(%d)]-当前体力值=%d\n", num,stamina);
    }

    public boolean canIncrease(){
        return stamina < MaxValue;
    }

}
