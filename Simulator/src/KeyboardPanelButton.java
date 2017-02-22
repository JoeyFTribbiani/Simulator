import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by vincent on 16/2/26.
 */
public class KeyboardPanelButton extends JButton{
    public final int value;
    KeyboardPanelButton(String s, int val, Dimension size){
        super(s);
        value = val;
        createUI(size);
    }

    private void createUI(Dimension d){
        this.setPreferredSize(d);
        //this.setBackground(Color.white);
        this.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(value);
                Util.keyboardValue = value;
                synchronized (Util.simulator){
                    Util.simulator.notifyAll();
                }

            }
        });
    }
}
