import com.apple.laf.AquaButtonRadioUI;
import com.sun.java.swing.plaf.windows.WindowsRadioButtonUI;

import javax.swing.*;
import javax.swing.plaf.metal.MetalRadioButtonUI;
import javax.swing.plaf.synth.SynthRadioButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by vincent on 16/2/20.
 */

public class InstructionRegisterPanel extends Register {
    private CallBack btnClicked;
    public JRadioButton[] digitRadios;
    public JButton submitBtn;

    public InstructionRegisterPanel(int n, CallBack cb){
        super(n);
        btnClicked = cb;
        digitRadios = new JRadioButton[n];
        submitBtn = new JButton("Run Single Step");
        createUI("");
    }

    @Override
    public void createUI(String name){
        this.setLayout(new GridLayout(2,1));

        JPanel upperPanel = new JPanel(new FlowLayout());

        this.setBorder(BorderFactory.createLineBorder(new Color(233,233,233)));
        this.setPreferredSize(new Dimension(420,110));
        this.setOpaque(false);
        for(int i=0; i<digitRadios.length; i++){
            digitRadios[i] = new JRadioButton();
            digitRadios[i].setText(String.format("%d", i));
            upperPanel.add(digitRadios[i]);
        }
        this.add(upperPanel);
        this.add(submitBtn);
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnClicked.methodToCallBack();
            }
        });
    }

    @Override
    public void reset(){
        super.reset();
        for(int i=0; i<digitRadios.length; i++){
            digitRadios[i].setSelected(false);
        }
    }
}
