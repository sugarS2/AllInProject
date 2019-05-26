package project.allin;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;

public class ClientHandler implements ActionListener, KeyListener, MouseListener{
	ClientMainUI clientMainUI;
	ClientSender clientSender;
	ClientListener clientListener;
	
	String clientId;
	String filter; // �����̽·�Ȯ�� , �����̹��ü���
	String betCoin;
	int choose;		// ������ ���� ����
	
	ClientHandler(ClientMainUI clientMainUI){
		this.clientMainUI = clientMainUI;
		clientId = clientMainUI.cLogin.idTF.getText();
	}
	
	// ActionListener
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clientMainUI.questionBtn) { // '����ǥ' ��ư ���� ���
			new RuleWindow(this);
			
			
		} else if (e.getSource() == clientMainUI.readyBtn) {// 'ready' ��ư ���� ���
			String clientId = clientMainUI.cLogin.idTF.getText();
			try {
				if (clientMainUI.readyBtn.getIcon() == clientMainUI.ready_hImg) { // ready��ư Ȱ��ȭ�ΰ��
					clientMainUI.clientSender.dos.writeUTF("//Ready " + clientId);
					clientMainUI.clientSender.dos.flush();
					clientMainUI.readyBtn.setIcon(clientMainUI.readyImg);
				} else if (clientMainUI.readyBtn.getIcon() == clientMainUI.readyImg) {
					clientMainUI.clientSender.dos.writeUTF("//UnReady " + clientId);
					clientMainUI.clientSender.dos.flush();
					clientMainUI.readyBtn.setIcon(clientMainUI.ready_hImg);
				}
			} catch (IOException io) {
			}
			
			
		} else if (e.getSource() == clientMainUI.exitBtn) { // '����' ��ư ���� ���
			int answer = JOptionPane.showConfirmDialog(null, "�����Ͻðڽ��ϱ�?", "Program Exit", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (answer == JOptionPane.YES_OPTION)
				System.exit(0);

			
		}else if(e.getSource() == clientMainUI.distBtn) {	// '���ۿϷ�' ��ư ���� ���
			Icon icon = clientMainUI.distBtn.getIcon();
			try {
				if(icon == clientMainUI.dist_hImg) { 
					String snail1Str = clientMainUI.snailTF1.getText();
					String snail2Str = clientMainUI.snailTF2.getText();
					String snail3Str = clientMainUI.snailTF3.getText();
					String snail4Str = clientMainUI.snailTF4.getText();
					snail1Str = snail1Str.replaceAll(" ", "");
					snail2Str = snail2Str.replaceAll(" ", "");
					snail3Str = snail3Str.replaceAll(" ", "");
					snail4Str = snail4Str.replaceAll(" ", "");

					int snailRate1 = Integer.parseInt(snail1Str);
					int snailRate2 = Integer.parseInt(snail2Str);
					int snailRate3 = Integer.parseInt(snail3Str);
					int snailRate4 = Integer.parseInt(snail4Str);

					clientMainUI.snailTF1.setText(snail1Str);
					clientMainUI.snailTF2.setText(snail2Str);
					clientMainUI.snailTF3.setText(snail3Str);
					clientMainUI.snailTF4.setText(snail4Str);
					
					int sum = snailRate1 + snailRate2 + snailRate3 + snailRate4;
					if (snailRate1 < 0 || snailRate2 < 0 || snailRate3 < 0 || snailRate4 < 0) {
						clientMainUI.textArea.append("�Է� ���� ������ 0 ~ 100 �Դϴ�!! �ٽ� �Է����ּ���.\n");
						clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());
					} else {
						if (snailRate1 == snailRate2 || snailRate1 == snailRate3 || snailRate1 == snailRate4
								|| snailRate2 == snailRate3 || snailRate2 == snailRate4 || snailRate3 == snailRate4) {
							JOptionPane.showMessageDialog(null, "�ߺ��Ǵ� ���� �Է��� �� �����ϴ�!! �ٽ� �Է����ּ���.", "�ߺ�����", JOptionPane.ERROR_MESSAGE);
							clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());
						} else {
							if (sum == 100) { // �·� ������ �� 100�ΰ��
								clientMainUI.distBtn.setIcon(clientMainUI.distImg);
								clientMainUI.snailTF1.setEnabled(false);
								clientMainUI.snailTF2.setEnabled(false);
								clientMainUI.snailTF3.setEnabled(false);
								clientMainUI.snailTF4.setEnabled(false);
								// clientMainUI.distBtn.setEnabled(false);

								clientMainUI.clientSender.dos.writeUTF("//Complete_Dist " + snailRate1 + "#"
										+ snailRate2 + "@" + snailRate3 + "$" + snailRate4);
								clientMainUI.clientSender.dos.flush();
							} else { // �� ���� 100�� �ƴѰ��
								clientMainUI.textArea.append("�� ���� 100�� �ƴմϴ�!! �ٽ� �Է����ּ���.(���� ��: " + sum + ")" + "\n");
								clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());
							}
						}
					}
				}
			} catch (NumberFormatException nfe) { // ���� �Ǵ� ���鸸 �Է��� ���
				JOptionPane.showMessageDialog(null, "�߸� �Էµ� ���� �ֽ��ϴ�!!\n�ٽ� �Է����ּ���.", "Ȯ�� �Է� ����",
						JOptionPane.ERROR_MESSAGE);
				// clientMainUI.textArea.append("�߸� �Էµ� ���� �ֽ��ϴ�!! �ٽ� �Է����ּ���.\n");
				// clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());
			} catch (IOException io) {
			}
		} else if (e.getSource() == clientMainUI.bettingBtn) { // '�����ϱ�' ��ư�� �������
			Icon icon = clientMainUI.bettingBtn.getIcon();
			try {
				if (icon == clientMainUI.bet_hImg) {
					switch (choose) {
					case 1:
						// clientMainUI.snailTF1.setText(clientMainUI.snailTF1.getText());
						betCoin = clientMainUI.snailTF1.getText();
						betCoin = betCoin.replaceAll(" ", "");
						clientMainUI.snailTF1.setText(betCoin);
						break;
					case 2:
						betCoin = clientMainUI.snailTF2.getText();
						betCoin = betCoin.replaceAll(" ", "");
						clientMainUI.snailTF2.setText(betCoin);
						break;
					case 3:
						betCoin = clientMainUI.snailTF3.getText();
						betCoin = betCoin.replaceAll(" ", "");
						clientMainUI.snailTF3.setText(betCoin);
						break;
					case 4:
						betCoin = clientMainUI.snailTF4.getText();
						betCoin = betCoin.replaceAll(" ", "");
						clientMainUI.snailTF4.setText(betCoin);
					}
					// betCoin = betCoin.replaceAll(" ","");
					int unnecessary = Integer.parseInt(betCoin);
					clientMainUI.clientSender.dos
							.writeUTF("//Complete_Betting " + choose + "$" + betCoin + "#" + clientId);
					clientMainUI.clientSender.dos.flush();
					System.out.println("Ŭ���̾�Ʈ �г��� : " + clientId);
					
					
				}
			} catch (NumberFormatException nfe) { // ���� �Ǵ� ���鸸 �Է��� ���
				JOptionPane.showMessageDialog(null, "���ڸ� �Է��� �� �ֽ��ϴ�.", "���ÿ���", JOptionPane.ERROR_MESSAGE);
				// clientMainUI.textArea.append("���ڸ� �Է��ض�!! �ٽ� �Է����ּ���.\n");
				// clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());
			} catch (IOException io) {
			}
		}
	}
	
	// KeyListener
	public void keyPressed(KeyEvent e) {
	}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
	}
	
	void removeSnailEvent() {
		clientMainUI.snailLabel1.removeMouseListener(this);
		clientMainUI.snailLabel2.removeMouseListener(this);
		clientMainUI.snailLabel3.removeMouseListener(this);
		clientMainUI.snailLabel4.removeMouseListener(this);
	}
	
	// MouseListener
	public void mouseClicked(MouseEvent e) {
		
		// �� �����̸� Ŭ���� ���, Ŭ���� �������� �·��� �����ְ� ������ �����̵��� ������ �� ���� �̺�Ʈ ���� 
		if(e.getSource() == clientMainUI.snailLabel1) {  
			if(filter.equals("������ �·� Ȯ��"))	clientMainUI.textArea.append("\n���� ���� Ŭ���� �������� �·���  " + clientMainUI.snailTF1.getText() + " �Դϴ�.\n");
			else if (filter.equals("������ �������� �Է�")) choose = 1;
			clientMainUI.snailTF1.setVisible(true);
			removeSnailEvent();
		}else if (e.getSource() == clientMainUI.snailLabel2) {
			if(filter.equals("������ �·� Ȯ��"))	clientMainUI.textArea.append("\n���� ���� Ŭ���� �������� �·���  " + clientMainUI.snailTF2.getText() + " �Դϴ�.\n");
			else if (filter.equals("������ �������� �Է�")) choose = 2;
			clientMainUI.snailTF2.setVisible(true);
			removeSnailEvent();
		}else if (e.getSource() == clientMainUI.snailLabel3) {
			if(filter.equals("������ �·� Ȯ��"))	clientMainUI.textArea.append("\n���� ���� Ŭ���� �������� �·���  " + clientMainUI.snailTF3.getText() + " �Դϴ�.\n");
			else if (filter.equals("������ �������� �Է�")) choose = 3;
			clientMainUI.snailTF3.setVisible(true);
			removeSnailEvent();
		}else if (e.getSource() == clientMainUI.snailLabel4) {
			if(filter.equals("������ �·� Ȯ��"))	clientMainUI.textArea.append("\n���� ���� Ŭ���� �������� �·���  " + clientMainUI.snailTF4.getText() + " �Դϴ�.\n");
			else if (filter.equals("������ �������� �Է�")) choose = 4;
			clientMainUI.snailTF4.setVisible(true);
			removeSnailEvent();
		}
		clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	
}

