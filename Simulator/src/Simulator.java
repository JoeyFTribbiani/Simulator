import javax.sound.midi.SysexMessage;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Created by vincent on 16/2/6.
 */

public class Simulator extends JFrame{
    CallBack bootingProgram;
    Thread programThread;

    Register pc; //Program Counter
    Register cc; //Condition Code
    GeneralPurposeRegisterPanel[] regs; //General Purpose Registers
    GeneralPurposeRegisterPanel[] ixRegs;//Index Registers
    GeneralPurposeRegisterPanel mar; //Memory Address Register
    GeneralPurposeRegisterPanel mbr; //Memory Buffer Register
    Register msr; //Machine Status Register
    Register mfr; //Machine Fault Register
    InstructionRegisterPanel ir; //Instruction Register

    JButton btnSwitchOn;
    JButton btnIPL;
    JButton btnRunStepByStep;
    JButton btnRunInMicroStep;

    JLabel opcodeLabel;

    KeyboardPanel keyboardPanel;

    TextPanel logPanel;
    TextPanel printPanel;
    JButton btnClearLog;

    Cache cache;

    JPanel leftPanel;
    JPanel midPanel;
    JPanel rightPanel;
    String log;

    JRadioButton program1Radio;
    JRadioButton program2Radio;
    JRadioButton pipelineRadio;


    PipelineInstruction [] pipelineInstructions;

    private boolean isOn;

    Memory mem; //Memory
    private JPanel rootPanel;

    public Simulator(){
        super("Simulator");
        Util.simulator = this;

        rootPanel = new JPanel();
        isOn = false;

        log="";
        btnSwitchOn = new JButton(new ImageIcon(this.getClass().getResource("/img/off.png")));
        btnSwitchOn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isOn = !isOn;
                if(isOn){
                    turnOn();
                }else{
                    turnOff();
                }
                String path = isOn? "/img/on.png" : "/img/off.png";
                btnSwitchOn.setIcon(new ImageIcon(this.getClass().getResource(path)));
            }
        });

        cache = new Cache();
        mem = new MainMemoryPanel(cache);

        ir = new InstructionRegisterPanel(16,new CallBack() {
            @Override
            public void methodToCallBack() {
                String s = "";
                for(int i=0; i<ir.digitRadios.length; i++){
                    s += ir.digitRadios[i].isSelected()?"1":"0";
                }
                ir.store(Integer.parseInt(s,2));
                executeOnce();
            }
        });

        opcodeLabel = new JLabel("OPCode:");

        pc = new RegisterDisplayPanel(12,"PC");

        cc = new RegisterDisplayPanel(4,"CC");

        //4 General Purpose Registers
        regs = new GeneralPurposeRegisterPanel[4];
        for(int i=0; i<regs.length; i++){
            regs[i] = new GeneralPurposeRegisterPanel(16,"R"+i);
        }

        //3 Index Registers
        ixRegs = new GeneralPurposeRegisterPanel[3];
        for(int i=0; i<ixRegs.length; i++){
            ixRegs[i] = new GeneralPurposeRegisterPanel(16,"IX"+(i+1));
        }

        mar = new GeneralPurposeRegisterPanel(16,"MAR");
        mbr = new GeneralPurposeRegisterPanel(16,"MBR");

        msr = new Register(16);
        mfr = new RegisterDisplayPanel(4, "MFR");


        keyboardPanel = new KeyboardPanel();

        btnIPL = new JButton("IPL");
        btnIPL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bootingProgram.methodToCallBack();
                executeProgram();
            }
        });

//        btnRunStepByStep = new JButton("Test Instruction\nStep By Step");
//        btnRunInMicroStep = new JButton("Test Instruction\nIn Micro Step");

        logPanel = new TextPanel(new JTextArea());
        printPanel = new TextPanel(new JTextArea());
        Util.setLogPanel(logPanel);
        Util.setPrinterPanel(printPanel);

        leftPanel = new JPanel();
        midPanel = new JPanel();
        rightPanel = new JPanel();

        program1Radio = new JRadioButton("Program 1");
        program2Radio = new JRadioButton("Program 2");
        pipelineRadio = new JRadioButton("Pipeline");

        pipelineInstructions = new PipelineInstruction[3];
        for(int i=0; i<=2; i++){
            pipelineInstructions[i] = new PipelineInstruction("Inst "+(i+1));
        }

        cache=new Cache();

        createUI();
    }

    public void createUI(){
        this.setSize(1150, 800);
        this.setResizable(false);
        this.setBackground(new Color(243,243,243));

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(1,3));

        Border padding = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        rootPanel.setBorder(padding);
        rootPanel.setOpaque(false);
        setContentPane(rootPanel);

        rootPanel.add(btnSwitchOn);

        leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        rightPanel.setPreferredSize(new Dimension(320,750));

        rightPanel.setOpaque(false);

        JPanel leftSubPanel1 = new JPanel();
        leftSubPanel1.setLayout(new FlowLayout(0));
        leftSubPanel1.setOpaque(false);
        leftSubPanel1.add(btnSwitchOn);
        btnSwitchOn.setPreferredSize(new Dimension(136, 56));
        leftSubPanel1.add(btnIPL);
        leftSubPanel1.add(program1Radio);
        leftSubPanel1.add(program2Radio);
        leftSubPanel1.add(pipelineRadio);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(program1Radio);
        buttonGroup.add(program2Radio);
        buttonGroup.add(pipelineRadio);
        btnIPL.setPreferredSize(new Dimension(56,56));
//        leftSubPanel1.add(btnRunStepByStep);
//        btnRunStepByStep.setPreferredSize(new Dimension(200,56));
//        leftSubPanel1.add(btnRunInMicroStep);
//        btnRunInMicroStep.setPreferredSize(new Dimension(200,56));

        btnClearLog = new JButton("Clear");

        JPanel leftSubPanel2 = new JPanel();
        leftSubPanel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        leftSubPanel2.setOpaque(false);
        leftSubPanel2.add(mem);
        leftSubPanel2.add(ir);

        leftPanel.add(leftSubPanel1);
        leftPanel.add(leftSubPanel2);

        leftPanel.add(new JSeparator());
        leftPanel.add(keyboardPanel);
        leftPanel.add(new JSeparator());

        for(int i=0; i<regs.length; i++){
            leftPanel.add(regs[i]);
            leftPanel.add(new JSeparator());
        }

        for(int i=0; i<ixRegs.length; i++){
            leftPanel.add(ixRegs[i]);
            leftPanel.add(new JSeparator());
        }

        leftPanel.add(mar);
        leftPanel.add(new JSeparator());
        leftPanel.add(mbr);

        JLabel printLabel = new JLabel("Printer");
        rightPanel.add(printLabel);
        printPanel.setPreferredSize(new Dimension(300, 200));
        rightPanel.add(printPanel);

        JPanel titlePanel = new JPanel(new FlowLayout());
        titlePanel.add(new JLabel("Field Engineer's Console"));
        titlePanel.add(btnClearLog);
        rightPanel.add(titlePanel);

        logPanel.setPreferredSize(new Dimension(300, 250));
        logPanel.setBorder(BorderFactory.createLineBorder(new Color(233, 233, 233)));
        rightPanel.add(logPanel);

        JPanel labelPanel = new JPanel(new FlowLayout(0));
        labelPanel.setOpaque(false);
        labelPanel.setPreferredSize(new Dimension(300, 20));
        labelPanel.add(opcodeLabel);
        rightPanel.add(labelPanel);
        rightPanel.add(pc);
        rightPanel.add(cc);
        rightPanel.add(mfr);
        for(int i=0; i<3; i++){
            rightPanel.add(pipelineInstructions[i]);
        }
        btnClearLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logPanel.reset();
            }
        });

        this.add(leftPanel);
        this.add(new JSeparator(1));
        this.add(midPanel);
        this.add(new JSeparator(1));
        this.add(rightPanel);

        turnOff();
        this.setVisible(true);
    }

    public void turnOff(){
        pc.reset();
        cc.reset();

        regs[0].reset();
        regs[1].reset();
        regs[2].reset();
        regs[3].reset();

        ixRegs[0].reset();
        ixRegs[1].reset();
        ixRegs[2].reset();

        mar.reset();
        mbr.reset();
        msr.reset();
        mfr.reset();
        ir.reset();

        mem.reset();

        logPanel.reset();
        printPanel.reset();
        setPanelEnabled(rootPanel, false);
        btnSwitchOn.setEnabled(true);
    }

    public void turnOn(){
        pc.store(6);
        cc.store(0);
        for(int i=0; i<regs.length; i++){
            regs[i].store(0);
        }
        for(int i=0; i<ixRegs.length; i++){
            ixRegs[i].store(0);
        }
        mar.store(0);
        mbr.store(0);
        msr.store(0);
        mfr.store(0);
        setPanelEnabled(rootPanel,true);
    }

    void throwFault(MachineFault id){

    }

    boolean loadProgramFromFile(String path){
        Util.writeLog("Loading from: "+path);

        try {
            InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream(path));
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            try {
                line = br.readLine();
            }catch (IOException e){
                System.out.print(e.getMessage());
            }
            int address = 50;
            while (line != null) {
                mem.store(address, Integer.parseInt(line, 2));
                address++;
                try {
                    line = br.readLine();
                }catch (IOException e){
                    System.out.print(e.getMessage());
                }
            }
        }catch (Exception e){
            System.out.print(e.getMessage());
        }

        return true;
    }

    boolean executeProgram(){
        //To execute in a new thread so that it could wait until keyboard responses.
        programThread = new Thread(){
            public void run(){
                synchronized (this) {
                    Integer val = mem.load(pc.load());
                    while(val!=null){
                        ir.store(val);
                        executeOnce();
                        val=mem.load(pc.load());
                    }
                }
            }
        };
        synchronized (programThread){
            programThread.start();
        }
        return true;
    }


    boolean executeOnce(){
        Integer code = ir.load();
        if(code!=null){
            String s = Util.hex2binString(code, ir.digits);

            int opCode = Integer.parseInt(s.substring(0,6),2); // OperationCode

            opcodeLabel.setText("OPCode:"+s.substring(0,6));

            int regCode = Integer.parseInt(s.substring(6,8),2); // GeneralPurposeRegisterCode
            int ixCode = Integer.parseInt(s.substring(8,10),2); // IndexRegisterCode
            int inDirCode = Integer.parseInt(s.substring(10,11),2); // IndirectCode
            int addressCode = Integer.parseInt(s.substring(11,ir.digits),2);// AddressCode

            int alCode=Integer.parseInt(s.substring(8,9),2);
            int lrCode=Integer.parseInt(s.substring(9,10),2);
            int countCode=Integer.parseInt(s.substring(12,ir.digits),2);

            int deviceID=Integer.parseInt(s.substring(11,ir.digits),2);
            String ins = "";
            switch (opCode){
                case 0: // HLT

                    break;
                case 1: // LDR
                    ins = "LDR "+regCode+", "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;
                case 2: // STR
                    ins = "STR "+regCode+", "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;
                case 3:// LDA
                    ins = "LDA "+regCode+", "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;
                case 4: // AMR
                    ins = "AMR "+regCode+", "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;
                case 5: // SMR
                    ins = "SMR "+regCode+", "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;
                case 6: // AIR
                    ins = "AIR "+regCode+", "+addressCode;
                    break;
                case 7: // SIR
                    ins = "SIR "+regCode+", "+addressCode;
                    break;
                case 10://JZ
                    ins = "JZ "+regCode+", "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;
                case 11://JNE
                    ins = "JNE "+regCode+", "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;

                case 12://JCC
                    ins = "JCC "+regCode+", "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;
                case 13://JMA
                    ins = "JMA "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;
                case 14://JSR
                    ins = "JSR "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;

                case 15://RFS
                    pc.store(regs[3].load());
                    ins = "RFS "+addressCode;
                    break;
                case 16://SOB
                    ins = "SOB "+regCode+", "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;

                case 17://JGE
                    ins = "JGE "+regCode+", "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;

                case 20://MLT
                    ins="MLT "+regCode+", "+ixCode;
                    break;
                case 21://DVD
                    ins="DVD "+regCode+", "+ixCode;
                    break;
                case 22://TRR
                    ins="TRR "+regCode+", "+ixCode;
                    break;
                case 23://AND
                    ins="AND "+regCode+", "+ixCode;
                    break;
                case 24://OR
                    ins="OR "+regCode+", "+ixCode;
                    break;
                case 25://NOT
                    ins="NOT "+regCode;
                    break;
                case 31://SRC
                    ins="SRC "+regCode+", "+countCode+", "+lrCode+","+alCode;
                    break;
                case 32://PRC
                    ins="PRC "+regCode+", "+countCode+", "+lrCode+","+alCode;
                    break;

                case 36: // Trap Code

                    break;
                case 41: // LDX
                    ins = "LDX "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;
                case 42: // STX
                    ins = "STX "+ixCode+", "+addressCode;
                    if(inDirCode!=0)ins+=", "+inDirCode;
                    break;

                case 61://IN
                    ins="IN "+regCode+", "+deviceID;
                    break;
                case 62://OUT
                    ins="OUT "+regCode+", "+deviceID;
                    break;
                case 63://CHK
                    ins="CHK "+regCode+", "+deviceID;
                    break;
                default:
                    throwFault(MachineFault.Illegal_Operation_Code);
                    break;
            }

            Util.writeLog(ins);
            // calculate Effective Address(EA)
            int ea = 0;
            if(inDirCode==0){
                if(ixCode==0){
                    ea = addressCode;
                }else{
                    ea = ixRegs[ixCode-1].load() + addressCode;
                }
            }else{
                if(ixCode==0){
                    ea = mem.load(addressCode);
                }else{
                    ea = mem.load(ixRegs[ixCode-1].load() + addressCode);

                }
            }

            boolean pcFlag = false;

            switch (opCode){
                case 0: // HLT
                    synchronized (this){
                        try{
                            wait();
                        }catch (Exception e){

                        }
                    }
                    break;
                case 1: //
                    regs[regCode].store(mem.load(ea));
                    break;
                case 2: // STR
                    mem.store(ea,regs[regCode].load());
                    break;
                case 3:// LDA
                    regs[regCode].store(ea);
                    break;
                case 4: // AMR
                    regs[regCode].store(regs[regCode].load()+mem.load(ea));
                    break;
                case 5: // SM
                    regs[regCode].store(regs[regCode].load()-mem.load(ea));

                    break;
                case 6: // AIR
                    regs[regCode].store(regs[regCode].load()+addressCode);
                    break;
                case 7: // SIR
                    regs[regCode].store(regs[regCode].load()-addressCode);
                    break;
                case 10://JZ
                    if (regs[regCode].load()==0)
                    {
                        pcFlag = true;
                        pc.store(ea);
                    }
                    break;
                case 11://JNE
                    if (regs[regCode].load()!=0)
                    {
                        pcFlag = true;
                        pc.store(ea);
                    }
                    break;

                case 12://JCC
                    if (regCode==1)
                    {
                        pcFlag = true;
                        pc.store(ea);
                    }
                    break;
                case 13://JMA
                    pcFlag = true;
                    pc.store(ea);
                    break;
                case 14://JSR
                    pcFlag = true;
                    regs[3].store(pc.load());
                    pc.store(ea);
                    break;

                case 15://RFS
                    pcFlag = true;
                    pc.store(regs[3].load());
                    break;
                case 16://SOB
                    regs[regCode].store(regs[regCode].load()-1);
                    if (regs[regCode].load()>0)
                    {
                        pcFlag = true;
                        pc.store(ea);
                    }
                    break;

                case 17://JGE
                    if (regs[regCode].load()>=0)
                    {
                        pcFlag = true;
                        pc.store(ea);
                    }
                    break;

                case 20://MLT

                    int mlt=regs[regCode].load()*regs[ixCode].load();
                    regs[regCode].store(mlt/(int)Math.pow(2,16));
                    regs[regCode+1].store(mlt%(int)Math.pow(2,16));
                    break;

                case 21://DVD
                    if (regs[ixCode].load()==0)
                    {
                        cc.store((cc.load()|4));
                    }
                    else
                    {
                        int q = regs[regCode].load()/regs[ixCode].load();
                        int r = regs[regCode].load()%regs[ixCode].load();
                        regs[regCode].store(q);
                        regs[regCode+1].store(r);
                    }
                    break;
                case 22://TRR
                    if (regs[regCode].load()==regs[ixCode].load())
                    {
                        cc.store((cc.load()|8));
                    }
                    else
                    {
                        cc.store(cc.load()&(~8));
                    }
                    break;
                case 23://AND
                    if (regs[regCode].load()!=0)
                        if (regs[ixCode].load()!=0)
                            regs[regCode].store(1);
                        else
                            regs[regCode].store(0);
                    else
                        regs[regCode].store(0);
                    break;
                case 24://OR
                    if (regs[regCode].load()!=0)
                    {
                        regs[regCode].store(1);
                    }
                    else if (regs[ixCode].load()!=0)
                    {
                        regs[regCode].store(1);
                    }
                    else
                    {
                        regs[regCode].store(0);
                    }
                    break;
                case 25://NOT
                    if (regs[regCode].load()!=0)
                    {
                        regs[regCode].store(0);
                    }
                    else
                    {
                        regs[regCode].store(1);
                    }
                    break;
                case 31://SRC
                    int base1=(int)Math.pow(2,16)-1;
                    int highest=(int)Math.pow(2, 15);
                    if ((countCode>0)&&(countCode<16))
                    {
                        if (lrCode==1)
                        {
                            int temp=regs[regCode].load()<<countCode;
                            //int result=temp&base1;
                            if ((temp&highest)>0)
                            {
                                temp=(~base1)|temp;
                            }
                            else
                            {
                                temp=temp&base1;
                            }
                            regs[regCode].store(temp);
                        }
                        else if (lrCode==0)
                        {
                            if (alCode==1)
                            {
                                int remains=~((int)Math.pow(2, 16-countCode)-1);

                                regs[regCode].store((regs[regCode].load()>>countCode)|remains);
                            }
                            else if (alCode==0)
                            {
                                int remains=((int)Math.pow(2, 16-countCode)-1);
                                regs[regCode].store((regs[regCode].load()>>countCode)&remains);
                            }
                        }
                    }
                    break;
                case 32://PRC
                    int base=(int)Math.pow(2,16)-1;
                    int highest2=(int)Math.pow(2, 15);
                    //regs[regCode].store(regs[regCode].load()&base);
                    if ((countCode>0)&&(countCode<16))
                    {
                        if (lrCode==1)
                        {
                            int base2=(int)Math.pow(2,countCode)-1;
                            int remains=(((regs[regCode].load()<<countCode)&(~base))>>16)&base2;

                            int temp=(regs[regCode].load()<<countCode)+remains;
                            if ((highest2&temp)>0)
                            {
                                temp=(~base)|temp;
                            }
                            else
                            {
                                temp=temp&base;
                            }
                            regs[regCode].store(temp);
                        }
                        else if (lrCode==0)
                        {
                            if (alCode==1)
                            {
                                int remains=regs[regCode].load()&((int)Math.pow(2, countCode)-1);
                                int temp=(regs[regCode].load()>>countCode);
                                int cut=(int)Math.pow(2, 16-countCode);
                                temp=temp&cut;

                                remains=remains<<(16-countCode);

                                temp=temp+remains;
                                if ((highest2&temp)>0)
                                {
                                    temp=(~base)|temp;
                                }
                                else
                                {
                                    temp=temp&base;
                                }
                                regs[regCode].store(temp);
                            }
                        }
                    }
                    break;

                case 36: // Trap Code

                    break;
                case 41: // LDX
                    if(inDirCode==0){
                        ea = mem.load(addressCode);
                    }else{
                        ea = mem.load(mem.load(addressCode));
                    }
                    ixRegs[ixCode-1].store(ea);
                    break;
                case 42: // STX
                    if(inDirCode==0){
                        ea = addressCode;
                    }else{
                        ea = mem.load(addressCode);
                    }
                    Util.writeLog(ins);
                    mem.store(ea, ixRegs[ixCode-1].load());
                    break;

                case 61://IN
                    synchronized (this){
                        try{
                           wait();
                        }catch (Exception e){

                        }
                    }

                    switch(deviceID)
                    {
                        case 0:
                            regs[regCode].store(Util.keyboardValue);
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                    }
                    break;

                case 62://OUT
                    switch(deviceID)
                    {
                        case 0:
                            break;
                        case 1:
                            switch (regs[regCode].load()){
                                case 0:
                                    Util.print("\n\nEnter the key word:(case sensitive/partial match)\n");
                                    break;
                                case 1:
                                    Util.print("\n\nComplete\n");
                                    break;
                                case 13:
                                    Util.print("\n");
                                    break;
                                default:
                                    Util.print(String.valueOf((char)regs[regCode].load().intValue()));
                                    break;
                            }
                            break;
                        case 2:
                            break;
                    }
                    break;
                case 63://CHK
                    switch(deviceID)
                    {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                    }
                    break;

                default:
                    throwFault(MachineFault.Illegal_Operation_Code);
                    break;
            }
            mar.store(ea);
            if(!pcFlag)pc.store(pc.load()+1);
            return true;
        }else{
            return false;
        }
    }

    void setPanelEnabled(JPanel panel, Boolean isEnabled) {
        panel.setEnabled(isEnabled);

        Component[] components = panel.getComponents();

        for(int i = 0; i < components.length; i++) {
            if(components[i].getClass().getName()=="javax.swing.JPanel"||components[i].getClass().getGenericSuperclass().getTypeName()=="Register"||components[i].getClass().getGenericSuperclass().getTypeName()=="Memory") {
                setPanelEnabled((JPanel) components[i], isEnabled);
            }
            components[i].setEnabled(isEnabled);
        }
    }

    public static void main(String[] args){
        Simulator simulator = new Simulator();
        Util.simulator.bootingProgram = new CallBack() {
            @Override
            public void methodToCallBack() {
                Util.writeLog("Booting Program...");

//                int [][] preservedMem = new int[][]{
//                        {6,0}, {7,400}, {8,510}, {9,800},{400,48}, {401,518}, {402,530}, {431,546}, {403,558}, {404,566},{405,572},{406,585},{407,594},
//                        {408,600},{409,609},{410,617},{411,632},{412,640},{420,526},{421,528},{430,65535}
//                };
//                for(int[] preservedVal:preservedMem){
//                    Util.simulator.mem.store(preservedVal[0],preservedVal[1]);
//                }
                int [][] preservedMem = new int[][]{
                        {8,0}, {9,30},{10,400},{7,200},{11,0},{12,0},{13,0},{14,0},{15,400},{16,0},{17,0},{18,0},{19,0},{20,0},{30,142},{31,68}, {39,64}, {41,53},{32,78},
                        {40,84}, {37,99},{35,113}, {36,132}, {33,119}, {34,141}, {42,144},{38,140},{21,48},{22,58},{23,32},{24,46}
                };
                for(int[] preservedVal:preservedMem){
                    Util.simulator.mem.store(preservedVal[0],preservedVal[1]);
                }
                try {
                    InputStreamReader reader = new InputStreamReader(this.getClass().getResourceAsStream("sentences.txt"));
                    BufferedReader br = new BufferedReader(reader);
                    String line = "";
                    int address = 400;
                    int total = 0;
                    while ((line=br.readLine())!= null) {
                        try {
                            char [] chars = line.toCharArray();
                            for(char letter:chars){
                                Util.simulator.mem.store(address++,letter);
                                total++;
                            }
                        }catch (Exception e){
                            System.out.print(e.getMessage());
                        }
                    }
                    Util.simulator.mem.store(11,total);
                }catch (Exception e){
                    System.out.print(e.getMessage());
                }
                Util.simulator.loadProgramFromFile("program2.txt");
                Util.writeLog("Launch Program2");
                Util.simulator.pc.store(50);
            }
        };

    }

}
