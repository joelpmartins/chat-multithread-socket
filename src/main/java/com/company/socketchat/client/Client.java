package com.company.socketchat.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;

public class Client extends JFrame implements ActionListener {
    private final JTextField tf_ip;
    private final JTextField tf_port;
    private final JTextField tf_name;
    
    private final JPanel chat = new JPanel();
    private final JTextArea textArea = new JTextArea(30, 53);
    private final JTextField tf_message = new JTextField(50);
    private final JLabel lb_title = new JLabel("Histórico do chat");
    
    private final JLabel lb_commands = new JLabel("Comandos úteis: /sair  /limparchat.");
    
    private final JButton b_send = new JButton("Enviar");
    private final JButton b_clear = new JButton("Limpar");
    private final JButton b_exit = new JButton("Sair");
    
    private Socket socket;
    private BufferedWriter bfr_writer;
    
    public Client() throws IOException{
        JPanel panel_ip = new JPanel();
        JPanel panel_port = new JPanel();
        JPanel panel_name = new JPanel();
        
        tf_ip = new JTextField("127.0.0.1");
        tf_port = new JTextField("9000");
        tf_name = new JTextField("VISITANTE");
        
        panel_ip.add(new JLabel("ENDEREÇO DE IP: "));
        panel_ip.add(tf_ip);
        
        panel_port.add(new JLabel("PORTA: "));
        panel_port.add(tf_port);
        
        panel_name.add(new JLabel("QUAL SEU NOME? "));
        panel_name.add(tf_name);
        
        Object[] obj_info = {panel_ip, panel_port, panel_name};
        JOptionPane.showMessageDialog(null, obj_info);
        
        lb_title.setForeground(Color.WHITE);
        
        lb_commands.setForeground(Color.WHITE);
        
        chat.setBackground(Color.DARK_GRAY);
        
        textArea.setBackground(Color.LIGHT_GRAY);
        textArea.setForeground(Color.BLACK);
        textArea.setFont(new Font("Verdana", Font.ITALIC, 12));
        textArea.setMargin(new Insets(20,20,0,0));
        textArea.setEditable(false);
         
        tf_message.setPreferredSize(new Dimension(100, 25));  
        
        b_send.addActionListener(this);
        b_clear.addActionListener(this);
        b_exit.addActionListener(this);
        
        JScrollPane s_pane = new JScrollPane(textArea);
        
        chat.add(lb_title);
        chat.add(s_pane); 
        chat.add(tf_message);
        chat.add(b_send);
        chat.add(b_clear);
        chat.add(b_exit);
        chat.add(lb_commands);

        setSize(600,600);
        setTitle(tf_name.getText());
        setContentPane(chat);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);
    }
    
    public void ConnectClient() throws IOException{
        socket = new Socket(tf_ip.getText(),Integer.parseInt(tf_port.getText()));
        bfr_writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bfr_writer.write(tf_name.getText() + "\r\n");
        textArea.append("Você conectou ao chat. Seja bem-vindo(a) " + tf_name.getText() + ".\r\n");
        bfr_writer.flush();
    }
    
    public void SendMessage(String message) throws IOException{
        if(message.length() < 1){
            textArea.append("Você deve escrever algo no campo de texto para enviar! \r\n");
        }else if(message.equals("/sair")){
          bfr_writer.write("Desconectado");
          textArea.append("Você desconectou do chat. \r\n");
        }else if(message.equals("/limparchat")){
          textArea.selectAll();
          textArea.replaceSelection("O chat foi limpado. \r\n");  
        }else{
          bfr_writer.write(message +"\r\n");
          textArea.append("[" + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "] " + tf_name.getText() + ": " +         tf_message.getText()+"\r\n");
        }
        bfr_writer.flush();
        tf_message.setText("");
    }
    
    public void UpdateClient() throws IOException{
        String message = "";
        BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while(!"/sair".equalsIgnoreCase(message)){
            if(bfr.ready()){
                message = bfr.readLine();
                if(message.equals("/sair")){
                    textArea.append("Você desconectou do chat! \r\n");
                }    
                else{
                    textArea.append(message + "\r\n");
                }
            }
        }    
    }
    
    public void DisconnectClient() throws IOException{
        SendMessage("/sair");
        bfr_writer.close();
        socket.close();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if(e.getActionCommand().equals(b_send.getActionCommand())){
                SendMessage(tf_message.getText());
            }else if(e.getActionCommand().equals(b_clear.getActionCommand())){
                textArea.selectAll();
                textArea.replaceSelection("O chat foi limpado. \r\n"); 
            }else if(e.getActionCommand().equals(b_exit.getActionCommand())){
                DisconnectClient();
            }
        }catch (IOException error){
            System.out.println(error.toString());
        }
    }
    
    public static void main(String []args) throws IOException{
        Client client = new Client();
        client.ConnectClient();
        client.UpdateClient();
    }
}