import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by vincent on 16/2/21.
 */
public class RegisterDisplayPanel extends Register {
    public JLabel valLabel;

    public RegisterDisplayPanel(int n, String name){
        super(n);
        valLabel = new JLabel();
        createUI(name);
    }

    @Override
    public void createUI(String name){
        this.setLayout(new FlowLayout(0));
        //this.setBorder(BorderFactory.createLineBorder(new Color(233,233,233)));
        this.setPreferredSize(new Dimension(300,20));
        this.setOpaque(false);
        JLabel nameLabel = new JLabel(name+":");
        this.add(nameLabel);

        valLabel.setForeground(Color.red);
        this.add(valLabel);
    }

    @Override
    boolean store(int val) {
        super.store(val);
        valLabel.setText(String.format("%d",val));
        return true;
    }

    @Override
    public void reset(){
        super.reset();
        valLabel.setText("");
    }
}
