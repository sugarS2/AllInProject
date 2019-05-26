package project.allin;

import java.net.*;
import java.awt.event.*;
import java.io.*;

public class ClientSender extends Thread implements KeyListener, ActionListener {
	ClientMainUI clientMainUI;
	DataOutputStream dos;
	
	ClientSender(ClientMainUI clientMainUI){
		this.clientMainUI = clientMainUI;
		try {
			dos = new DataOutputStream(this.clientMainUI.socket.getOutputStream());
		}catch(IOException io) {
		}
	}
	
	public void run() {
		try {
			dos.writeUTF(clientMainUI.cLogin.idTF.getText());	
			dos.writeUTF(clientMainUI.cLogin.pickC);
		}catch(IOException io) {}
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == clientMainUI.msgBtn) {	//전송버튼 클릭했을 때,
			String msg = clientMainUI.msgTF.getText();
			String clientId = clientMainUI.cLogin.idTF.getText();
			clientMainUI.msgTF.setText("");
			try {
				dos.writeUTF("//Msg " + clientId + " : " + msg);
				dos.flush();
			}catch(IOException io) {}
		}
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			String msg = clientMainUI.msgTF.getText();
			clientMainUI.msgTF.setText("");
			String clientId = clientMainUI.cLogin.idTF.getText();
			try {
				dos.writeUTF("//Msg " + clientId + " : " + msg);
				dos.flush();
			}catch(IOException io) {}
		}
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
	}
}
