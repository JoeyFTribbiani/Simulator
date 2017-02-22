import javax.swing.*;
import java.awt.*;

/**
 * Created by vincent on 16/2/26.
 */
public class KeyboardPanel extends JPanel {
    KeyboardPanelButton [] buttons;

    KeyboardPanel(){
        buttons = new KeyboardPanelButton[37];
        createUI();
    }

    private void createUI(){
        this.setLayout(new GridLayout(4,1));
        this.setOpaque(false);
        char [][] row = new char[][]{{'1','2','3','4','5','6','7','8','9','0'},
                {'q','w','e','r','t','y','u','i','o','p'},
                {'a','s','d','f','g','h','j','k','l'},
                {'z','x','c','v','b','n','m'}};
        int i = 0;
        for(char[] r:row){
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
            for(char c:r){
                buttons[i] = new KeyboardPanelButton(String.format("%c", c),c,new Dimension(30,30));
                panel.add(buttons[i]);
                i++;
                if(i==buttons.length-1){
                    buttons[i] = new KeyboardPanelButton("Enter",13,new Dimension(70,30));
                    panel.add(buttons[i]);
                }
            }
            this.add(panel);
        }
    }
}
