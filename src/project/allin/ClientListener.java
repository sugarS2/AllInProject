package project.allin;

import java.net.*;

import javax.swing.JOptionPane;

import java.io.*;

public class ClientListener extends Thread{
	Socket socket;
	ClientMainUI clientMainUI;
	DataInputStream dis;
	
	String clientId, clientCoin, clientPickC, clientIdx;	// id, coin, char, index
	boolean auth, gameStart;  // ������ ���� ���� , ���� ���� ����
	
	ClientListener(Socket socket, ClientMainUI clientMainUI){
		this.socket = socket;
		this.clientMainUI = clientMainUI;
		try {
			dis = new DataInputStream(this.socket.getInputStream());
		}catch(IOException io) {}
	}
	
	public void run() {
		while(dis!=null) {
			try {
				String msg = dis.readUTF();
				// filter
				if(msg.startsWith("//UpdateClient")) {	// ��ɾ� : Ŭ���̾�Ʈ ��� ����
					clientId = msg.substring(14, msg.indexOf("@"));
	                clientCoin = msg.substring(msg.indexOf("@") + 1, msg.indexOf("#"));
	                clientPickC = msg.substring(msg.indexOf("#") + 1, msg.indexOf("$"));
	                clientIdx = msg.substring(msg.indexOf("$")+1);
					updateClient();
					//System.out.println(clientId + " " + clientCoin + " " + clientPickC + " " + clientIdx);
					
					
				}else if(msg.startsWith("//AllEnter")){  // ��ɾ� : ��� ������ ��� 
					clientMainUI.readyBtn.setEnabled(true);
					clientMainUI.readyBtn.setIcon(clientMainUI.ready_hImg);
					clientMainUI.readyBtn.addActionListener(clientMainUI.clientHandler);
				
					
				}else if(msg.startsWith("//Clear")) {	// ��ɾ� : ä��â ����
					clientMainUI.textArea.setText("");
					
					
				}else if(msg.startsWith("//RoundStart ")) {	// ��ɾ� : 1���� ����
					String round = msg.substring(13);
					System.out.println(round + "round");
					
					
				}else if(msg.startsWith("//NextRound ")) {	// ��ɾ� : ���� ���� ����
					String round = msg.substring(12);
					System.out.println(round + "round");
					// ���� ���带 �����ϱ� ���� 
					auth = false;
					clientMainUI.readyBtn.setIcon(clientMainUI.readyImg);
					clientMainUI.readyBtn.removeActionListener(clientMainUI.clientHandler);
					clientMainUI.snailTF1.setText("");	clientMainUI.snailTF2.setText("");
					clientMainUI.snailTF3.setText("");	clientMainUI.snailTF4.setText("");
					clientMainUI.timerLabel.setText("Ÿ�̸� �κ�");
					clientMainUI.hideRateManipulation();  // ������ �·� �����ϴ� �κ��� �Ⱥ��̰� �Ѵ�.
					clientMainUI.bettingBtn.setIcon(clientMainUI.bet_hImg);
					
				}else if(msg.startsWith("//GameStart")) {	// ��ɾ� : ���� ���� -> ready��ư ��Ȱ��ȭ
					gameStart = true;
					clientMainUI.readyBtn.setIcon(clientMainUI.readyImg);	
					clientMainUI.distBtn.setIcon(clientMainUI.dist_hImg);
					clientMainUI.readyBtn.removeActionListener(clientMainUI.clientHandler);
					
					
				}else if(msg.startsWith("//GameEnd")) {		// ��ɾ� : ���� ����
					gameStart = false;
					auth = false;
					clientMainUI.textArea.append("\n�� ������ �� �� ���� �����Ͽ� ������ ����˴ϴ�.\n\n");
					clientMainUI.readyBtn.setIcon(clientMainUI.readyImg);
					clientMainUI.readyBtn.removeActionListener(clientMainUI.clientHandler);
					clientMainUI.snailTF1.setText("");	clientMainUI.snailTF2.setText("");
					clientMainUI.snailTF3.setText("");	clientMainUI.snailTF4.setText("");
					clientMainUI.timerLabel.setText("Ÿ�̸�");
					clientMainUI.hideRateManipulation();  // ������ �·� �����ϴ� �κ��� �Ⱥ��̰� �Ѵ�.
					
					
				}else if(msg.startsWith("//End")) {		// ��ɾ� : ���� ����
					gameStart = false;
					auth = false;
					clientMainUI.textArea.append("\n��  ���� ����.\n\n");
					clientMainUI.readyBtn.setIcon(clientMainUI.ready_hImg);
					clientMainUI.readyBtn.removeActionListener(clientMainUI.clientHandler);
					clientMainUI.snailTF1.setText("");	clientMainUI.snailTF2.setText("");
					clientMainUI.snailTF3.setText("");	clientMainUI.snailTF4.setText("");
					clientMainUI.timerLabel.setText("Ÿ�̸�");
					clientMainUI.hideRateManipulation();  // ������ �·� �����ϴ� �κ��� �Ⱥ��̰� �Ѵ�.
					clientMainUI.readyBtn.addActionListener(clientMainUI.clientHandler);
					
					
				}else if(msg.startsWith("//Auth ")) {	// ��ɾ� : ���� �ο�
					if(gameStart == true) {
						if(clientMainUI.cLogin.idTF.getText().equals(msg.substring(7))) {
							clientMainUI.textArea.append("����  ����� �̹� ���� '������'�Դϴ�.  ����  \n");
							clientMainUI.textArea.append("����  �����̵鿡�� �·��� �ο����ּ���.  ����  \n");
							auth = true;	//�����ڸ� ���� �ο�
							
							// �����ڸ� �·� ������ �� �ְ� �ϴ� �κ� 
							clientMainUI.snailTF1.setVisible(true);		clientMainUI.snailTF1.setEnabled(true);
							clientMainUI.snailTF2.setVisible(true);		clientMainUI.snailTF2.setEnabled(true);
							clientMainUI.snailTF3.setVisible(true);		clientMainUI.snailTF3.setEnabled(true);
							clientMainUI.snailTF4.setVisible(true);		clientMainUI.snailTF4.setEnabled(true);
							clientMainUI.distBtn.setVisible(true);		clientMainUI.distBtn.setEnabled(true);
						}
					}
					
				}else if(msg.startsWith("//Complete_Dist ")){	// ��ɾ� : �����ڰ� �� �����̿��� �·� ������ �� 
					clientMainUI.textArea.append("��  ������ �ð����� �����ڵ��� ������ �ϳ��� ������ �·��� Ȯ���ϼ���. \n");
					if(auth==false) {
						String snailRate1 = msg.substring(16, msg.indexOf("#"));
						String snailRate2 = msg.substring(msg.indexOf("#")+1, msg.indexOf("@"));
						String snailRate3 = msg.substring(msg.indexOf("@")+1, msg.indexOf("$"));
						String snailRate4 = msg.substring(msg.indexOf("$")+1);
						clientMainUI.snailTF1.setText(snailRate1);
						clientMainUI.snailTF2.setText(snailRate2);
						clientMainUI.snailTF3.setText(snailRate3);
						clientMainUI.snailTF4.setText(snailRate4);
						
						clientMainUI.clientHandler.filter = "������ �·� Ȯ��";
						addSnailEvent();
					}
					
					
				}else if(msg.startsWith("//Timer ")){   // ��ɾ� : Ÿ�̸�
		               if(gameStart==true) {
		                  clientMainUI.timerLabel.setText(msg.substring(8) + " ��");
		               }
					
					
				}else if(msg.startsWith("//ChooseEnd")) {	// ��ɾ� : �����ڵ��� �����̸� ������ �·� ���� �ð� '����'
					if(gameStart==true) {
						clientMainUI.textArea.append("\n��  ������ ���� �ð�(�·� Ȯ��)�� ����Ǿ����ϴ�.\n");
						// ���� �ƹ��͵� ���þ��ߴٸ�, ������ �ð� ���Ŀ��� ������ �·� Ȯ�� �Ұ�
						if(clientMainUI.snailTF1.isVisible()==false && clientMainUI.snailTF2.isVisible()==false 
							&& clientMainUI.snailTF3.isVisible()==false && clientMainUI.snailTF4.isVisible()==false) {
							clientMainUI.textArea.append("��  ������ �ð� ���� ������ ����(�·� Ȯ��)�� ���� �ʾұ� ������, �������� �·��� Ȯ�� ���� ���մϴ�. \n");
							removeSnailEvent();
						}
						clientMainUI.textArea.append("��  �����ڰ� ������ �·� Ȯ�� �� ��� �����ڴ� ä������ 1�� �����̸� �����ϱ� �����մϴ�. (�ð� : 60��)\n");
						clientMainUI.clientSender.dos.writeUTF("//ChatStart " + 60);
						clientMainUI.clientSender.dos.flush();
					}
					
					
				}else if(msg.startsWith("//ChatEnd")){	// ��ɾ� : ��ȭ�ð� ����
					clientMainUI.textArea.append("\n��  ��ȭ �ð�(������ ����)�� ����Ǿ����ϴ�. \n");
					if(gameStart==true && auth==false) {
						clientMainUI.textArea.append("�����ڵ��� ���� �·��� ���ٰ� �����Ǵ� �����̸� �������ֽð�, ���� ������ �Է��ϼ���. \n");
						// ���� ���� �Է��ϴ� â �ʱ�ȭ 
						clientMainUI.snailTF1.setText("");	clientMainUI.snailTF1.setEnabled(true);	clientMainUI.snailTF1.setVisible(false);
						clientMainUI.snailTF2.setText("");	clientMainUI.snailTF2.setEnabled(true);	clientMainUI.snailTF2.setVisible(false);
						clientMainUI.snailTF3.setText("");	clientMainUI.snailTF3.setEnabled(true);	clientMainUI.snailTF3.setVisible(false);
						clientMainUI.snailTF4.setText("");	clientMainUI.snailTF4.setEnabled(true);	clientMainUI.snailTF4.setVisible(false);
						clientMainUI.clientHandler.filter = "������ �������� �Է�";
						clientMainUI.bettingBtn.setVisible(true);	clientMainUI.bettingBtn.setEnabled(true);
						clientMainUI.msgTF.setEnabled(false);	// �����ϴ� ������ ä�� ��Ȱ��ȭ
						addSnailEvent();
					}
					
					
				} else if (msg.startsWith("//Error_Betting ")) { // ��ɾ� : ���� ���� ���
					String sort = msg.substring(16, 18);
					String bettor = msg.substring(18);
					String clientId = clientMainUI.cLogin.idTF.getText();
					if (bettor.equals(clientId)) {
						switch (sort) {
						case "ov":
							JOptionPane.showMessageDialog(null, "���� ���� ����  ������ ���� �� ���� �����ϴ�. \n�ٽ� �Է����ּ���.", "���ÿ���",
									JOptionPane.ERROR_MESSAGE);
							break;
						case "lo":
							JOptionPane.showMessageDialog(null, "���� �ּ� ������ 1���� �Դϴ�. \n�ٽ� �Է����ּ���.", "���ÿ���",
									JOptionPane.ERROR_MESSAGE);
							break;
						}
					}

				}else if(msg.startsWith("//Complete_Betting ")) {	// ��ɾ� : ���� ���� �Ϸ�
					String bettor = msg.substring(msg.indexOf("#")+1);
					String clientId = clientMainUI.cLogin.idTF.getText();
					if(bettor.equals(clientId)){
						clientMainUI.bettingBtn.setIcon(clientMainUI.betImg);
						clientMainUI.snailTF1.setEnabled(false);	clientMainUI.snailTF2.setEnabled(false);
						clientMainUI.snailTF3.setEnabled(false);	clientMainUI.snailTF4.setEnabled(false);
					}
					
					
				}else {	// ��ɾ� : �Ϲ� ä�� ���
					clientMainUI.textArea.append(msg + "\n");
				}
				clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());	// ��ũ�ѹٰ� �׻� ������ �������� �ϴ� ���
			}catch(IOException io) {	// ������ ������ ���� ���
				clientMainUI.textArea.append("�� ������ ������ ������ϴ�. �� \n");
				clientMainUI.textArea.append("�� �г��� �ߺ�, ���� ���� �ʰ��� ��� ������ �źε˴ϴ�. �� ");
				try {
					Thread.sleep(2000);
					System.exit(0);
				}catch(InterruptedException ie) {}
			}
		}
	}
	
	void addSnailEvent() {
		clientMainUI.snailLabel1.addMouseListener(clientMainUI.clientHandler);
		clientMainUI.snailLabel2.addMouseListener(clientMainUI.clientHandler);
		clientMainUI.snailLabel3.addMouseListener(clientMainUI.clientHandler);
		clientMainUI.snailLabel4.addMouseListener(clientMainUI.clientHandler);
	}
	
	void removeSnailEvent() {
		clientMainUI.snailLabel1.removeMouseListener(clientMainUI.clientHandler);
		clientMainUI.snailLabel2.removeMouseListener(clientMainUI.clientHandler);
		clientMainUI.snailLabel3.removeMouseListener(clientMainUI.clientHandler);
		clientMainUI.snailLabel4.removeMouseListener(clientMainUI.clientHandler);
	}
	
	void updateClient() { // Ŭ���̾�Ʈ ��� �߰�
		if (Integer.parseInt(clientIdx) == 0) {
			clientMainUI.userInfoLabel1.setText("[ �г��� : " + clientId + " / " + "���� : " + clientCoin + " ]");
			if (clientPickC.equals("ĳ��1")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char1);
			} else if (clientPickC.equals("ĳ��2")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char2);
			} else if (clientPickC.equals("ĳ��3")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char3);
			} else if (clientPickC.equals("ĳ��4")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char4);
			} else if (clientPickC.equals("ĳ��5")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char5);
			} else if (clientPickC.equals("ĳ��6")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char6);
			} else if (clientPickC.equals("ĳ��7")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char7);
			} else if (clientPickC.equals("ĳ��8")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char8);
			} else if (clientPickC.equals("ĳ��9")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char9);
			}
			clientMainUI.userImgLabel2.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel3.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel4.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel5.setIcon(clientMainUI.userImg1);
			// Ŭ���̾�Ʈ ��� ����
			clientMainUI.userInfoLabel2.setText("[ �г��� / ���� ] ");
			clientMainUI.userInfoLabel3.setText("[ �г��� / ���� ] ");
			clientMainUI.userInfoLabel4.setText("[ �г��� / ���� ] ");
			clientMainUI.userInfoLabel5.setText("[ �г��� / ���� ] ");
		} else if (Integer.parseInt(clientIdx) == 1) {
			clientMainUI.userInfoLabel2.setText("[ �г��� : " + clientId + " / " + "���� : " + clientCoin + " ]");
			if (clientPickC.equals("ĳ��1")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char1);
			} else if (clientPickC.equals("ĳ��2")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char2);
			} else if (clientPickC.equals("ĳ��3")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char3);
			} else if (clientPickC.equals("ĳ��4")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char4);
			} else if (clientPickC.equals("ĳ��5")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char5);
			} else if (clientPickC.equals("ĳ��6")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char6);
			} else if (clientPickC.equals("ĳ��7")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char7);
			} else if (clientPickC.equals("ĳ��8")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char8);
			} else if (clientPickC.equals("ĳ��9")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char9);
			}
			clientMainUI.userImgLabel3.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel4.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel5.setIcon(clientMainUI.userImg1);
			clientMainUI.userInfoLabel3.setText("[ �г��� / ���� ] ");
			clientMainUI.userInfoLabel4.setText("[ �г��� / ���� ] ");
			clientMainUI.userInfoLabel5.setText("[ �г��� / ���� ] ");
		} else if (Integer.parseInt(clientIdx) == 2) {
			clientMainUI.userInfoLabel3.setText("[ �г��� : " + clientId + " / " + "���� : " + clientCoin + " ]");
			if (clientPickC.equals("ĳ��1")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char1);
			} else if (clientPickC.equals("ĳ��2")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char2);
			} else if (clientPickC.equals("ĳ��3")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char3);
			} else if (clientPickC.equals("ĳ��4")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char4);
			} else if (clientPickC.equals("ĳ��5")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char5);
			} else if (clientPickC.equals("ĳ��6")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char6);
			} else if (clientPickC.equals("ĳ��7")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char7);
			} else if (clientPickC.equals("ĳ��8")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char8);
			} else if (clientPickC.equals("ĳ��9")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char9);
			}
			clientMainUI.userImgLabel4.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel5.setIcon(clientMainUI.userImg1);
			clientMainUI.userInfoLabel4.setText("[ �г��� / ���� ] ");
			clientMainUI.userInfoLabel5.setText("[ �г��� / ���� ] ");
		} else if (Integer.parseInt(clientIdx) == 3) {
			clientMainUI.userInfoLabel4.setText("[ �г��� : " + clientId + " / " + "���� : " + clientCoin + " ]");
			if (clientPickC.equals("ĳ��1")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char1);
			} else if (clientPickC.equals("ĳ��2")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char2);
			} else if (clientPickC.equals("ĳ��3")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char3);
			} else if (clientPickC.equals("ĳ��4")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char4);
			} else if (clientPickC.equals("ĳ��5")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char5);
			} else if (clientPickC.equals("ĳ��6")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char6);
			} else if (clientPickC.equals("ĳ��7")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char7);
			} else if (clientPickC.equals("ĳ��8")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char8);
			} else if (clientPickC.equals("ĳ��9")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char9);
			}
			clientMainUI.userImgLabel5.setIcon(clientMainUI.userImg1);
			clientMainUI.userInfoLabel5.setText("[ �г��� / ���� ] ");
		} else if (Integer.parseInt(clientIdx) == 4) {
			clientMainUI.userInfoLabel5.setText("[ �г��� : " + clientId + " / " + "���� : " + clientCoin + " ]");
			if (clientPickC.equals("ĳ��1")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char1);
			} else if (clientPickC.equals("ĳ��2")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char2);
			} else if (clientPickC.equals("ĳ��3")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char3);
			} else if (clientPickC.equals("ĳ��4")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char4);
			} else if (clientPickC.equals("ĳ��5")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char5);
			} else if (clientPickC.equals("ĳ��6")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char6);
			} else if (clientPickC.equals("ĳ��7")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char7);
			} else if (clientPickC.equals("ĳ��8")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char8);
			} else if (clientPickC.equals("ĳ��9")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char9);
			}
		}
	}
}