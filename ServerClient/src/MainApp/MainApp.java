
package MainApp;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

 public class MainApp extends JFrame implements ActionListener, MouseListener {
    //Instance Variables
   private JDesktopPane theDesktop, theDesktop2;
   private JButton but1, but2;
   private JTextField serv_port_field, serv_IP_field, client_port_field, client_pload_field, client_IP_field, client_path_field;
   private JInternalFrame serv_info, client_info;
   private JPanel serv_panel, client_panel;
   private JCheckBox box;
   private JTextArea a, b;
   private JScrollPane sp;
   
   //Streams
   Scanner in = new Scanner(System.in);
  
   // Constructor
    private MainApp() {
            setTitle("UDP Implementation");
            drawFrame();
            setVisible(true);
    }
    
    public static void main(String[] args) {
            MainApp trial = new MainApp();
            trial.setBounds(300, 300, 500, 325);
            trial.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            trial.setVisible(true);
    }
    
    public void drawFrame() {
        JTabbedPane tabbedPane = new JTabbedPane();
        theDesktop = new JDesktopPane();
        JLabel label1 = new JLabel("Server", SwingConstants.CENTER);
        JLabel serv_port_Label = new JLabel("Enter the specified port:", SwingConstants.CENTER);
        serv_port_field = new JTextField(20);
        JLabel serv_IP_Label = new JLabel("Enter the server's IP:", SwingConstants.CENTER);
        serv_IP_field = new JTextField(20);
        JLabel boxLabel = new JLabel("Enable exponential delay: ", SwingConstants.CENTER);
        box = new JCheckBox();
        JPanel panel1 = new JPanel();
        serv_panel = new JPanel();
        
        //panel1.add(label1);
        //panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.setLayout(new GridLayout(0, 2, 2, 2));
        panel1.add(serv_port_Label);
        panel1.add(serv_port_field);
        panel1.add(serv_IP_Label);
        panel1.add(serv_IP_field);
        panel1.add(boxLabel);
        panel1.add(box);
        
        // mhnymata
        b = new JTextArea(10, 10);
        sp = new JScrollPane(b);
        serv_panel.setLayout(new BorderLayout());
        serv_panel.add( sp );
        
        
        JInternalFrame frame = new JInternalFrame("Set Server Parameters", true, false, true, true);
        but1 = new JButton("Submit");
            panel1.add(but1);
            but1.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        String IP = serv_IP_field.getText();
                        if(IP.equals("")) return;
                        int port;
                        try {
                            port = Integer.parseInt(serv_port_field.getText());
                        } catch(NumberFormatException e) {
                            return;
                        }
                        new Thread() {
                            public void run() {
                                Server.Server.run(port, IP, box.isSelected(), b );
                            }
                        }.start();
                        serv_info = new JInternalFrame("Information", true, true, true, true);
                        //serv_panel.setLayout(new BorderLayout());
                        serv_info.add(serv_panel, BorderLayout.CENTER);
                        serv_info.pack();
                        theDesktop.add(serv_info);
                        serv_info.setVisible(true);
                        but1.setEnabled(false);
                    }
              });
        frame.add(panel1, BorderLayout.CENTER);
        frame.pack();
        theDesktop.add(frame);
        frame.setVisible(true);
        pack();
        tabbedPane.addTab("Server", null, theDesktop, "Server Panel");
       
        theDesktop2 = new JDesktopPane();
        JLabel label2 = new JLabel("Client", SwingConstants.CENTER);
        JLabel client_port_Label = new JLabel("Enter the specified port:", SwingConstants.CENTER);
        client_port_field = new JTextField(5);
        JLabel client_IP_Label = new JLabel("Enter the specified IP:", SwingConstants.CENTER);
        client_IP_field = new JTextField(5);
        JLabel client_path_Label = new JLabel("Enter the file absolute/relative path:", SwingConstants.CENTER);
        client_path_field = new JTextField(5);
        client_path_field.addMouseListener(new MouseListener(){
            @Override
            public void mouseClicked(MouseEvent event) {
                JFileChooser chooser = new JFileChooser();
                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    client_path_field.setText(file.getAbsolutePath());
                }
            }
            @Override
            public void mouseExited(MouseEvent event){}
            @Override
            public void mouseEntered(MouseEvent event){}
            @Override
            public void mouseReleased(MouseEvent event){}
            @Override
            public void mousePressed(MouseEvent event){}
        });
        JLabel client_pload_Label = new JLabel("Enter the maximum payload:", SwingConstants.CENTER);
        client_pload_field = new JTextField(5);
        JPanel panel2 = new JPanel();
        client_panel = new JPanel();
        
        panel2.setLayout(new GridLayout(0, 2, 2, 2));
        //panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        //panel2.add(label2);
        panel2.add(client_port_Label);
        panel2.add(client_port_field);
        panel2.add(client_IP_Label);
        panel2.add(client_IP_field);
        panel2.add(client_path_Label);
        panel2.add(client_path_field);
        panel2.add(client_pload_Label);
        panel2.add(client_pload_field);
        
        
        // mhnymata
        a = new JTextArea(10, 10);
        sp = new JScrollPane(a);
        client_panel.setLayout(new BorderLayout());
        client_panel.add( sp );
        
        
        JInternalFrame frame2 = new JInternalFrame("Set Client Parameters", true, false, true, true);
        but2 = new JButton("Submit");
            panel2.add(but2);
            but2.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        String IP = client_IP_field.getText();
                        if(IP.equals("")) return;
                        String file = client_path_field.getText();
                        if(file.equals("")) return;
                        int port, payload;
                        try {
                            port = Integer.parseInt(client_port_field.getText());
                        } catch(NumberFormatException e) {
                            return;
                        }
                        try {
                            payload = Integer.parseInt(client_pload_field.getText());
                        } catch(NumberFormatException e) {
                            return;
                        }
                        new Thread() {
                            public void run() {
                                Client.Client.run(port, IP, file, payload, box.isSelected(), a);
                            }
                        }.start();
                        client_info = new JInternalFrame("Information", true, true, true, true);
                        //client_panel.setLayout(new BorderLayout());
                        client_info.add(client_panel, BorderLayout.CENTER);
                        client_info.pack();
                        theDesktop2.add(client_info);
                        client_info.setVisible(true);
                    }
                            
              });
        frame2.add(panel2, BorderLayout.CENTER);
        frame2.pack();
        theDesktop2.add(frame2);
        frame2.setVisible(true);
        pack();
        tabbedPane.addTab("Client", null, theDesktop2, "Client Panel");
        
        add(tabbedPane);
    }
    
        @Override
    public void actionPerformed(ActionEvent event) {}
    
        @Override
    public void mouseClicked(MouseEvent e) {}
    
    public void mouseExited(MouseEvent event){}
    public void mouseEntered(MouseEvent event){}
    public void mouseReleased(MouseEvent event){}
    public void mousePressed(MouseEvent event){}

}
