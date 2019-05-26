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
	String filter; // 달팽이승률확인 , 달팽이배팅선택
	String betCoin;
	int choose;		// 달팽이 배팅 필터
	
	ClientHandler(ClientMainUI clientMainUI){
		this.clientMainUI = clientMainUI;
		clientId = clientMainUI.cLogin.idTF.getText();
	}
	
	// ActionListener
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == clientMainUI.questionBtn) { // '물음표' 버튼 누른 경우
			new RuleWindow(this);
			
			
		} else if (e.getSource() == clientMainUI.readyBtn) {// 'ready' 버튼 누른 경우
			String clientId = clientMainUI.cLogin.idTF.getText();
			try {
				if (clientMainUI.readyBtn.getIcon() == clientMainUI.ready_hImg) { // ready버튼 활성화인경우
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
			
			
		} else if (e.getSource() == clientMainUI.exitBtn) { // '종료' 버튼 누른 경우
			int answer = JOptionPane.showConfirmDialog(null, "종료하시겠습니까?", "Program Exit", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (answer == JOptionPane.YES_OPTION)
				System.exit(0);

			
		}else if(e.getSource() == clientMainUI.distBtn) {	// '조작완료' 버튼 누른 경우
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
						clientMainUI.textArea.append("입력 가능 범위는 0 ~ 100 입니다!! 다시 입력해주세요.\n");
						clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());
					} else {
						if (snailRate1 == snailRate2 || snailRate1 == snailRate3 || snailRate1 == snailRate4
								|| snailRate2 == snailRate3 || snailRate2 == snailRate4 || snailRate3 == snailRate4) {
							JOptionPane.showMessageDialog(null, "중복되는 수는 입력할 수 없습니다!! 다시 입력해주세요.", "중복오류", JOptionPane.ERROR_MESSAGE);
							clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());
						} else {
							if (sum == 100) { // 승률 비율이 총 100인경우
								clientMainUI.distBtn.setIcon(clientMainUI.distImg);
								clientMainUI.snailTF1.setEnabled(false);
								clientMainUI.snailTF2.setEnabled(false);
								clientMainUI.snailTF3.setEnabled(false);
								clientMainUI.snailTF4.setEnabled(false);
								// clientMainUI.distBtn.setEnabled(false);

								clientMainUI.clientSender.dos.writeUTF("//Complete_Dist " + snailRate1 + "#"
										+ snailRate2 + "@" + snailRate3 + "$" + snailRate4);
								clientMainUI.clientSender.dos.flush();
							} else { // 총 합이 100이 아닌경우
								clientMainUI.textArea.append("총 합이 100이 아닙니다!! 다시 입력해주세요.(현재 합: " + sum + ")" + "\n");
								clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());
							}
						}
					}
				}
			} catch (NumberFormatException nfe) { // 문자 또는 공백만 입력한 경우
				JOptionPane.showMessageDialog(null, "잘못 입력된 곳이 있습니다!!\n다시 입력해주세요.", "확률 입력 오류",
						JOptionPane.ERROR_MESSAGE);
				// clientMainUI.textArea.append("잘못 입력된 곳이 있습니다!! 다시 입력해주세요.\n");
				// clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());
			} catch (IOException io) {
			}
		} else if (e.getSource() == clientMainUI.bettingBtn) { // '배팅하기' 버튼을 누른경우
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
					System.out.println("클라이언트 닉네임 : " + clientId);
					
					
				}
			} catch (NumberFormatException nfe) { // 문자 또는 공백만 입력한 경우
				JOptionPane.showMessageDialog(null, "숫자만 입력할 수 있습니다.", "배팅오류", JOptionPane.ERROR_MESSAGE);
				// clientMainUI.textArea.append("숫자만 입력해라!! 다시 입력해주세요.\n");
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
		
		// 각 달팽이를 클릭한 경우, 클릭한 달팽이의 승률만 보여주고 나머지 달팽이들은 선택할 수 없게 이벤트 제거 
		if(e.getSource() == clientMainUI.snailLabel1) {  
			if(filter.equals("달팽이 승률 확인"))	clientMainUI.textArea.append("\n▶▶ 현재 클릭한 달팽이의 승률은  " + clientMainUI.snailTF1.getText() + " 입니다.\n");
			else if (filter.equals("달팽이 배팅코인 입력")) choose = 1;
			clientMainUI.snailTF1.setVisible(true);
			removeSnailEvent();
		}else if (e.getSource() == clientMainUI.snailLabel2) {
			if(filter.equals("달팽이 승률 확인"))	clientMainUI.textArea.append("\n▶▶ 현재 클릭한 달팽이의 승률은  " + clientMainUI.snailTF2.getText() + " 입니다.\n");
			else if (filter.equals("달팽이 배팅코인 입력")) choose = 2;
			clientMainUI.snailTF2.setVisible(true);
			removeSnailEvent();
		}else if (e.getSource() == clientMainUI.snailLabel3) {
			if(filter.equals("달팽이 승률 확인"))	clientMainUI.textArea.append("\n▶▶ 현재 클릭한 달팽이의 승률은  " + clientMainUI.snailTF3.getText() + " 입니다.\n");
			else if (filter.equals("달팽이 배팅코인 입력")) choose = 3;
			clientMainUI.snailTF3.setVisible(true);
			removeSnailEvent();
		}else if (e.getSource() == clientMainUI.snailLabel4) {
			if(filter.equals("달팽이 승률 확인"))	clientMainUI.textArea.append("\n▶▶ 현재 클릭한 달팽이의 승률은  " + clientMainUI.snailTF4.getText() + " 입니다.\n");
			else if (filter.equals("달팽이 배팅코인 입력")) choose = 4;
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

