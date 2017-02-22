import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by vincent on 16/2/21.
 */
public class GeneralPurposeRegisterPanel extends Register {
    //private CallBack btnClickedFn;
    public JRadioButton[] digitRadios;
    public JButton btnDeposit;
    public JLabel valLabel;
    public JTextField valText;

    public GeneralPurposeRegisterPanel(int n, String name){
        super(n);
        digitRadios = new JRadioButton[n];
        btnDeposit = new JButton("Deposit");
        valLabel = new JLabel("VAL");
        valText = new JTextField();
        createUI(name);
    }

    @Override
    public void createUI(String name){
        this.setLayout(new FlowLayout(0));
        //this.setBorder(BorderFactory.createLineBorder(new Color(233,233,233)));
        this.setPreferredSize(new Dimension(700,30));
        this.setOpaque(false);
        JLabel nameLabel = new JLabel(name);
        this.add(nameLabel);

        for(int i=0; i<this.digits; i++){
            digitRadios[i] = new JRadioButton();
            digitRadios[i].setPreferredSize(new Dimension(25,20));
            this.add(digitRadios[i]);
        }

        valLabel.setForeground(Color.red);
        this.add(valLabel);

        valText.setPreferredSize(new Dimension(50, 25));
        this.add(valText);

        this.add(btnDeposit);
        btnDeposit.setPreferredSize(new Dimension(80,25));

        btnDeposit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                store(Integer.parseInt(valText.getText()));
            }
        });
    }

    @Override
    boolean store(int val) {
        super.store(val);
        valLabel.setText(String.format("%d",val));
        String binString = Util.hex2binString(val,digits);
        for(int i=0; i<binString.length(); i++){
            digitRadios[i].setSelected(binString.charAt(i)=='1');
        }
        return true;
    }

    @Override
    public void reset(){
        super.reset();
        for(int i=0; i<digitRadios.length; i++){
            digitRadios[i].setSelected(false);
        }
        valLabel.setText("VAL");
        valText.setText("");
    }
}
