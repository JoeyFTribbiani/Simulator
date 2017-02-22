import javax.swing.*;
import java.awt.*;

/**
 * Created by vincent on 16/4/10.
 */
public class PipelineInstruction extends JPanel{
    JTextField fetchField;
    JTextField decodeField;
    JTextField executeField;

    PipelineInstruction(String name){
        this.add(new JLabel(name));
        fetchField = new JTextField("IF");
        decodeField = new JTextField("ID");
        executeField = new JTextField("EXE");

        fetchField.setBackground(Color.green);
        decodeField.setBackground(Color.green);
        executeField.setBackground(Color.green);

        fetchField.setEditable(false);
        decodeField.setEditable(false);
        executeField.setEditable(false);

        this.add(fetchField);
        this.add(decodeField);
        this.add(executeField);
        this.setPreferredSize(new Dimension(280,30));
        this.setLayout(new FlowLayout(0));
    }

    public void enableFetching(){
        fetchField.setBackground(Color.red);
    }

    public void disableFetching(){
        fetchField.setBackground(Color.green);
    }

    public void enableDecoding(){
        decodeField.setBackground(Color.red);
    }

    public void disableDecoding(){
        decodeField.setBackground(Color.green);
    }

    public void enableExecution(){
        executeField.setBackground(Color.red);
    }

    public void disableExecution(){
        executeField.setBackground(Color.green);
    }
}
