import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by vincent on 16/2/21.
 */
public class MainMemoryPanel extends Memory{
    JLabel addressLabel;
    JLabel valueLabel;
    JTextField addressField;
    JTextField valueField;
    JButton btnStore;
    private int status;

    MainMemoryPanel(Cache ca){
        super(ca);
        this.setLayout(new GridLayout(2,1));

        addressLabel = new JLabel("Address");
        addressField = new JTextField();

        valueLabel = new JLabel("Value");
        valueField = new JTextField();
        btnStore = new JButton("Deposit");

        createUI();
    }

    @Override
    void createUI() {
        JPanel memoryUpperPanel = new JPanel();
        memoryUpperPanel.setLayout(new GridLayout(2,2));
        memoryUpperPanel.setOpaque(false);
        JPanel memoryDownPanel = new JPanel();
        memoryDownPanel.add(btnStore);

        memoryUpperPanel.add(addressLabel);
        memoryUpperPanel.add(addressField);
        addressField.setPreferredSize(new Dimension(70,30));

        memoryUpperPanel.add(valueLabel);
        memoryUpperPanel.add(valueField);
        valueField.setPreferredSize(new Dimension(70,30));

        this.add(memoryUpperPanel);
        this.add(btnStore);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(150,110));

        btnStore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                store(Integer.parseInt(addressField.getText()), Integer.parseInt(valueField.getText()));
            }
        });
        this.add(btnStore);
    }

    @Override
    void reset() {
        super.reset();
        valueField.setText("");
        addressField.setText("");
    }
}
