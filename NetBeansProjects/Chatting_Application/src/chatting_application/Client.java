package chatting_application;

import static chatting_application.Server.f;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.border.EmptyBorder;
import java.net.*;
import java.io.*;

public class Client implements ActionListener {
    
    JTextField text;
    static JPanel a1 ;
    static Box vertical = Box.createVerticalBox();
    static DataOutputStream dop;
    static JFrame f = new JFrame();
    JScrollPane scp;
    
    Client(){
        f.setLayout(null);
        
        JPanel p1 = new JPanel();
        p1.setBackground(new Color(34, 186, 110));
        p1.setBounds(0,0,450,70);
        p1.setLayout(null);
        f.add(p1);
        
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("Icons/arrow-back-regular-24.png"));
        Image i2 = i1.getImage().getScaledInstance(25, 25,Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel back = new JLabel(i3);
        back.setBounds(5,20,25,25);
        p1.add(back);
        
        back.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent ae){
//                setVisible(false);
                  System.exit(0);
            }
            
        });
        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("Icons/face-solid-24.png"));
        Image i5 = i4.getImage().getScaledInstance(50, 50,Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel profile = new JLabel(i6);
        profile.setBounds(40,10,50,50);
        p1.add(profile);
        
        ImageIcon i7 = new ImageIcon(ClassLoader.getSystemResource("Icons/video-camera.png"));
        Image i8 = i7.getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT);
        ImageIcon i9 = new ImageIcon(i8);
        JLabel video = new JLabel(i9);
        video.setBounds(300,20,30,30);
        p1.add(video);
        
        ImageIcon i10 = new ImageIcon(ClassLoader.getSystemResource("Icons/phone-call-solid-24.png"));
        Image i11 = i10.getImage().getScaledInstance(35, 30,Image.SCALE_DEFAULT);
        ImageIcon i12 = new ImageIcon(i11);
        JLabel phone = new JLabel(i12);
        phone.setBounds(360,20,35,30);
        p1.add(phone);
        
        ImageIcon i13 = new ImageIcon(ClassLoader.getSystemResource("Icons/dots-vertical-rounded-regular-24.png"));
        Image i14 = i13.getImage().getScaledInstance(10, 25,Image.SCALE_DEFAULT);
        ImageIcon i15 = new ImageIcon(i14);
        JLabel moverevert = new JLabel(i15);
        moverevert.setBounds(420,20,10,25);
        p1.add(moverevert);
        
        JLabel name = new JLabel("Ken");
        name.setBounds(110,15,100,18);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        p1.add(name);
        
        JLabel status = new JLabel("Aclive now");
        status.setBounds(110,35,100,18);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN_SERIF", Font.BOLD, 14));
        p1.add(status);
        
        a1 = new JPanel();
        a1.setLayout(new BoxLayout(a1, BoxLayout.Y_AXIS));
        scp = new JScrollPane(a1);
        scp.setBounds(5,75,440,570);
        scp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        f.add(scp);
        
        text = new JTextField();
        text.setBounds(5,655,310,40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 14));
        f.add(text);
        
        JButton send = new JButton("Send");
        send.setBounds(320, 655,123,40);
        send.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // Set the button UI explicitly
        send.setBackground(new Color(53, 135, 66));
        send.setForeground(Color.white);
        send.addActionListener(this);
        send.setFont(new Font("SAN_SERIF", Font.PLAIN, 14));
        send.setOpaque(true);
        f.add(send);

         
        f.setSize(450, 700);
        f.setLocation(200, 50);
        f.setUndecorated(true);
        f.getContentPane().setBackground(Color.WHITE);
        
        f.setVisible(true);
    }
    
    public void actionPerformed (ActionEvent ae){
        try{
            String out = text.getText();
        
            JPanel p2 = formatLabel(out);

            a1.setLayout(new BorderLayout());
            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            a1.add(vertical, BorderLayout.PAGE_START);
            f.revalidate();
            f.repaint();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JScrollBar verticalScrollBar = scp.getVerticalScrollBar();
                    verticalScrollBar.setValue(verticalScrollBar.getMaximum());
                }
            });
            
            dop.writeUTF(out);
            text.setText("");
            f.invalidate();
            f.validate();
        }catch(Exception e){
            e.printStackTrace();
        }
        
  }
    
    public static JPanel formatLabel(String out){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(39, 217, 134));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15,15,15,50));
                
        panel.add(output);
        
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        JLabel time  = new JLabel ();
        time.setText(sdf.format(cal.getTime()));
        panel.add(time);
        return panel;
    }
    
    public static void main(String[] args){
        new Client();
        try{
            Socket s =  new Socket("127.0.0.1", 6001);
            DataInputStream dip = new DataInputStream(s.getInputStream());
            dop = new DataOutputStream(s.getOutputStream());
            
            while(true){
                a1.setLayout(new BorderLayout());
                String msg = dip.readUTF();
                JPanel panel = formatLabel(msg);
                
                JPanel left = new JPanel(new BorderLayout());
                left.add(panel, BorderLayout.LINE_START);
                vertical.add(left);
                vertical.add(Box.createVerticalStrut(15));
                a1.add(vertical , BorderLayout.PAGE_START);
                
                f.validate();
                
            }
                    
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
