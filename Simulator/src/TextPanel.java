import javax.swing.*;
import java.awt.*;

/**
 * Created by vincent on 16/2/23.
 */
public class TextPanel extends JScrollPane {
    public JTextArea logArea;

    TextPanel(JTextArea ta){
        super(ta);
        logArea = ta;
        logArea.setLineWrap(true);
        logArea.setEditable(false);
        logArea.setBackground(Color.WHITE);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    }


    public void reset(){
        logArea.setText("");
    }
}
