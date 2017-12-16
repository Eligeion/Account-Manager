import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/*

Created by: Eligeion @ OSBot
//TODO: Add explainations for important lines.
 */

public class Automate extends JFrame {
    private JTable gui;
    private static List<String[]> accounts = new ArrayList<>();

    private static boolean closeOSB;
    private static int min;
    private static int max;

    public static void main(String[] args) throws IOException, InterruptedException {

        String filePath = "input.txt";
        File file = new File(filePath);

        if (!file.isFile()) {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            System.out.println("File does not exist. Creating file.");
            FileWriter writeTemplate = new FileWriter(file);
            writeTemplate.write("Template:\nUsername//Password//WorldNumber//ScriptArguments\nUsername//Password//WorldNumber//null");
            writeTemplate.close();
            System.exit(0);
        }


        try {
            String sCurrentLine;
            try (BufferedReader accRead = new BufferedReader(new FileReader(file))) {
                while ((sCurrentLine = accRead.readLine()) != null) {
                    String[] splitLine = sCurrentLine.split("//");
                    if (splitLine[3].equalsIgnoreCase("null")) {
                        splitLine[3] = "";
                    }
                    accounts.add(splitLine);
                }
            }
        }catch (Exception e) {
            System.out.println("File is improperly formatted.");
        }

        /*

        Scanner can work, but it is for parsing, this code will not work. Just a reminder for me. BR is better for reading line by line. (To my knowledge)

        try {
            Scanner accountInfo = new Scanner(file);
            while (accountInfo.hasNextLine()){
                String line = accountInfo.nextLine();
                String[] splitLine = line.split(" : ");
                accounts.add(splitLine);
            }
        }catch (Exception e){
            System.out.println("File doesn't exist.");
        }
        */

        Automate gui = new Automate();
        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gui.setSize(600,330);
        gui.setResizable(false);
        gui.gui.getTableHeader().setReorderingAllowed(false); //Disables the reordering of the columns.
        gui.gui.setDefaultEditor(Object.class, null); //Sets the editor to null meaning the cells cannot be edited by the user.
        gui.setVisible(true);
    }

    private Automate() {
        setLayout(new FlowLayout());
        String[] columnNames = {"Email/Username", "Password", "World", "Script Arguments"};


        gui = new JTable(accounts.toArray(new Object[][] {}), columnNames);
        gui.setPreferredScrollableViewportSize(new Dimension(500, 150));
        gui.setFillsViewportHeight(true);
        gui.setName("Automation Manager");
        JScrollPane scrollPane = new JScrollPane(gui);

        JLabel randLabel = new JLabel();
        randLabel.setText("Randomize Execution by Min-Max(Seconds):         "); //Spaces for appearance sake.

        JTextField randomizeLaunch = new JTextField();
        randomizeLaunch.setPreferredSize(new Dimension(100,25));

        JLabel closeLabel = new JLabel();
        closeLabel.setVisible(false);
        closeLabel.setText("Close OSBuddy upon completion?                            "); //Ditto.

        JCheckBox closeOSBV = new JCheckBox();
        closeOSBV.setVisible(false);
        closeOSBV.setSelected(true);

        JButton stopButton = new JButton();
        stopButton.setPreferredSize(new Dimension(350, 30));
        stopButton.setText("STOP");
        stopButton.setVisible(false);
        stopButton.addActionListener((ActionEvent event) -> {
            Thread.interrupted();
            System.exit(0);
        });

        JButton startButton = new JButton();
        startButton.setPreferredSize(new Dimension(350, 30));
        startButton.setText("START");

        JLabel warningLabel = new JLabel();
        warningLabel.setVisible(false);

        startButton.addActionListener((ActionEvent event) -> {
            closeOSB = closeOSBV.isSelected();
            if(randomizeLaunch.getText().contains("-")){
                min = Integer.parseInt(randomizeLaunch.getText().split("-")[0]);
                max = Integer.parseInt(randomizeLaunch.getText().split("-")[1]);
                if(min > max){
                    warningLabel.setText("<html><font color='red'>Please make sure the first digit is less than the second.</font></html>");
                    warningLabel.setVisible(true);
                } else {
                    min = Integer.parseInt(randomizeLaunch.getText().split("-")[0]);
                    max = Integer.parseInt(randomizeLaunch.getText().split("-")[1]);
                    try {
                        runCommand();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    warningLabel.setVisible(false);
                    startButton.setVisible(false);
                    stopButton.setVisible(true);
                }
            } else {
                warningLabel.setText("<html><font color='red'>Please enter two digits separated by a '-' into the text box.</font></html>");
                warningLabel.setVisible(true);
            }

        });

        System.out.println(randomizeLaunch.getText());

        add(scrollPane);
        add(randLabel);
        add(randomizeLaunch);
        add(warningLabel);
        add(closeLabel);
        add(closeOSBV);
        add(stopButton);
        add(startButton);
    }



    private void runCommand() throws InterruptedException {
        /*
        String command = " -login " + botLogin + " -bot " +
                user + ":" + pass + ":0000 -world " + world + " " + scriptArg;
        String jarName = "osbot.jar";
        */

        int i = 0;
        while(i < accounts.size()) {
            max = max*1000;
            min = min*1000;
            System.out.println("Maximum Bound: " + max);
            System.out.println("Minimum Bound: " + min);
            int random = min + (int)(Math.random()*max);


            String user = gui.getValueAt(i, 0).toString();
            String pass = gui.getValueAt(i, 1).toString();
            int world = Integer.parseInt(String.valueOf(gui.getValueAt(i, 2)));
            String script = gui.getValueAt(i, 3).toString();

            String botLogin = "";
            String command = " -login " + botLogin + " -bot " +
                    user + ":" + pass + ":0000 -world " + world + " " + script;
            String jarName = "osbot.jar";

            try {
                Process x = Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar \"" + jarName + "\" -debug 5005" + command});
                //noinspection StatementWithEmptyBody
                if (closeOSB) {
                    //TODO: Add closing client.
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Thread.sleep(random);
            i++;
        }
    }
}