import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by vincent on 16/1/29.
 */
public class Register extends JPanel{
    private int value;
    private int maxValue;
    private int minValue;
    public int digits;
    private int status;

    //n means how many digits a register will have; without UI
    public Register(int n){
        digits = n;
        value = 0;// initial value = 0
//        Math.pow(2,n-1);
//        maxValue = (int)Math.pow(2,n-1) - 1;
//        minValue = - (int)Math.pow(2,n-1);
    }

    boolean store(int val){
        value = val;

        return true;
    }

    Integer load(){
        return this.value;
    }

    void reset(){
        store(0);
    }

    void createUI(String name){

    };


}
