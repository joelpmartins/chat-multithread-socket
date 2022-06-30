package com.company.socketchat.server;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Server extends Thread{
    private String nome;
    final static String[] options = {"Ok", "Cancel"};
    final static String msg_port = "O servidor foi iniciado na porta: ";
    final static String PORT = "9000";
    
    private final Socket socket;
    private static ServerSocket server;
    private BufferedReader bf_reader;
    
    private static ArrayList<BufferedWriter>clients;
    
    public Server(Socket socket){
        this.socket = socket;
        try {
            bf_reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    
    @Override
    public void run(){
        try{
          String msg;
          
          BufferedWriter bf_writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
          clients.add(bf_writer);
          nome = msg = bf_reader.readLine();

          while(!"Sair".equalsIgnoreCase(msg) && msg != null){
            msg = bf_reader.readLine();
            SendMessageToAll(bf_writer, msg);
            System.out.println(msg);
          }
        }catch (IOException e) {
            System.out.println(e.toString());
        }
    }
    
    public static void main(String []args) throws IOException{
        try{
            JTextField tf_port = new JTextField(PORT);
            
            JPanel panel = new JPanel();
            panel.add(new JLabel("Porta do servidor: "));
            panel.add(tf_port);
            
            int result = JOptionPane.showOptionDialog(null, panel, "Socket chat", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, 0);
            
            if(result == JOptionPane.NO_OPTION){
               System.out.println("O servidor foi finalizado.");
               System.exit(0);
            }
            
            JOptionPane.showMessageDialog(null, msg_port + tf_port.getText());
            
            clients = new ArrayList<>();
            server = new ServerSocket(Integer.parseInt(tf_port.getText()));
            
            while(true){
                System.out.println("O servidor está online, aguardando conexões...");
                Socket socket = server.accept();
                System.out.println("Cliente conectou ao servidor.");
                Thread thread = new Server(socket);
                thread.start();
            }
            
        }catch(HeadlessException e){
            System.out.println(e.toString());
        }
    }
    
    public void SendMessageToAll(BufferedWriter bf_writer, String message) throws IOException{
        BufferedWriter bfa_writer;
        for(BufferedWriter bfw : clients){
            bfa_writer = (BufferedWriter)bfw;
            if(!(bf_writer == bfa_writer)){
                if(message.equals("Desconectado")){
                    bfw.write(nome + " desconectou do chat." + "\r\n");
                }else{
                    bfw.write("[" + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "] "+ nome + ": " + message + "\r\n");
                }
                bfw.flush();
            }
        }
    }
}
