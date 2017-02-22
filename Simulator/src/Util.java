import javax.swing.*;

/**
 * Created by vincent on 16/1/29.
 */
public class Util {
    private static TextPanel logPanel;
    private static TextPanel printerPanel;
    public static int keyboardValue = -1;
    public static Simulator simulator;

    Util(){

    }

    public static void setLogPanel(TextPanel tp){
        logPanel = tp;
    }
    public static void setPrinterPanel(TextPanel tp){
        printerPanel = tp;
    }

    public static String hex2binString(int val, int digits){
        String s = Integer.toBinaryString(val);
        if(s.length()>digits){
            s = s.substring(s.length()-digits,s.length());
        }

        for(int i=0; i<digits-Integer.toBinaryString(val).length(); i++){
            s = "0" + s;
        }
        return s;
    }

    public static void writeLog(String s){
        if(s!=""){
            if(logPanel.logArea.getText().isEmpty()){
                logPanel.logArea.setText(logPanel.logArea.getText()+s);
            }else{
                logPanel.logArea.setText(logPanel.logArea.getText()+"\n"+s);
            }
        }
    };


    public static void print(String s){
        printerPanel.logArea.setText(printerPanel.logArea.getText()+s);
    };
}
