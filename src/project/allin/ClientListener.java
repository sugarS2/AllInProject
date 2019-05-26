package project.allin;

import java.net.*;

import javax.swing.JOptionPane;

import java.io.*;

public class ClientListener extends Thread{
	Socket socket;
	ClientMainUI clientMainUI;
	DataInputStream dis;
	
	String clientId, clientCoin, clientPickC, clientIdx;	// id, coin, char, index
	boolean auth, gameStart;  // 출제자 권한 변수 , 게임 시작 설정
	
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
				if(msg.startsWith("//UpdateClient")) {	// 명령어 : 클라이언트 목록 갱신
					clientId = msg.substring(14, msg.indexOf("@"));
	                clientCoin = msg.substring(msg.indexOf("@") + 1, msg.indexOf("#"));
	                clientPickC = msg.substring(msg.indexOf("#") + 1, msg.indexOf("$"));
	                clientIdx = msg.substring(msg.indexOf("$")+1);
					updateClient();
					//System.out.println(clientId + " " + clientCoin + " " + clientPickC + " " + clientIdx);
					
					
				}else if(msg.startsWith("//AllEnter")){  // 명령어 : 모두 입장한 경우 
					clientMainUI.readyBtn.setEnabled(true);
					clientMainUI.readyBtn.setIcon(clientMainUI.ready_hImg);
					clientMainUI.readyBtn.addActionListener(clientMainUI.clientHandler);
				
					
				}else if(msg.startsWith("//Clear")) {	// 명령어 : 채팅창 비우기
					clientMainUI.textArea.setText("");
					
					
				}else if(msg.startsWith("//RoundStart ")) {	// 명령어 : 1라운드 시작
					String round = msg.substring(13);
					System.out.println(round + "round");
					
					
				}else if(msg.startsWith("//NextRound ")) {	// 명령어 : 다음 라운드 진행
					String round = msg.substring(12);
					System.out.println(round + "round");
					// 다음 라운드를 진행하기 위해 
					auth = false;
					clientMainUI.readyBtn.setIcon(clientMainUI.readyImg);
					clientMainUI.readyBtn.removeActionListener(clientMainUI.clientHandler);
					clientMainUI.snailTF1.setText("");	clientMainUI.snailTF2.setText("");
					clientMainUI.snailTF3.setText("");	clientMainUI.snailTF4.setText("");
					clientMainUI.timerLabel.setText("타이머 부분");
					clientMainUI.hideRateManipulation();  // 달팽이 승률 조작하는 부분을 안보이게 한다.
					clientMainUI.bettingBtn.setIcon(clientMainUI.bet_hImg);
					
				}else if(msg.startsWith("//GameStart")) {	// 명령어 : 게임 시작 -> ready버튼 비활성화
					gameStart = true;
					clientMainUI.readyBtn.setIcon(clientMainUI.readyImg);	
					clientMainUI.distBtn.setIcon(clientMainUI.dist_hImg);
					clientMainUI.readyBtn.removeActionListener(clientMainUI.clientHandler);
					
					
				}else if(msg.startsWith("//GameEnd")) {		// 명령어 : 게임 종료
					gameStart = false;
					auth = false;
					clientMainUI.textArea.append("\n▶ 참가자 중 한 명이 퇴장하여 게임이 종료됩니다.\n\n");
					clientMainUI.readyBtn.setIcon(clientMainUI.readyImg);
					clientMainUI.readyBtn.removeActionListener(clientMainUI.clientHandler);
					clientMainUI.snailTF1.setText("");	clientMainUI.snailTF2.setText("");
					clientMainUI.snailTF3.setText("");	clientMainUI.snailTF4.setText("");
					clientMainUI.timerLabel.setText("타이머");
					clientMainUI.hideRateManipulation();  // 달팽이 승률 조작하는 부분을 안보이게 한다.
					
					
				}else if(msg.startsWith("//End")) {		// 명령어 : 게임 종료
					gameStart = false;
					auth = false;
					clientMainUI.textArea.append("\n▶  게임 종료.\n\n");
					clientMainUI.readyBtn.setIcon(clientMainUI.ready_hImg);
					clientMainUI.readyBtn.removeActionListener(clientMainUI.clientHandler);
					clientMainUI.snailTF1.setText("");	clientMainUI.snailTF2.setText("");
					clientMainUI.snailTF3.setText("");	clientMainUI.snailTF4.setText("");
					clientMainUI.timerLabel.setText("타이머");
					clientMainUI.hideRateManipulation();  // 달팽이 승률 조작하는 부분을 안보이게 한다.
					clientMainUI.readyBtn.addActionListener(clientMainUI.clientHandler);
					
					
				}else if(msg.startsWith("//Auth ")) {	// 명령어 : 권한 부여
					if(gameStart == true) {
						if(clientMainUI.cLogin.idTF.getText().equals(msg.substring(7))) {
							clientMainUI.textArea.append("▶▶  당신이 이번 라운드 '조작자'입니다.  ◀◀  \n");
							clientMainUI.textArea.append("▶▶  달팽이들에게 승률을 부여해주세요.  ◀◀  \n");
							auth = true;	//조작자만 권한 부여
							
							// 조작자만 승률 조작할 수 있게 하는 부분 
							clientMainUI.snailTF1.setVisible(true);		clientMainUI.snailTF1.setEnabled(true);
							clientMainUI.snailTF2.setVisible(true);		clientMainUI.snailTF2.setEnabled(true);
							clientMainUI.snailTF3.setVisible(true);		clientMainUI.snailTF3.setEnabled(true);
							clientMainUI.snailTF4.setVisible(true);		clientMainUI.snailTF4.setEnabled(true);
							clientMainUI.distBtn.setVisible(true);		clientMainUI.distBtn.setEnabled(true);
						}
					}
					
				}else if(msg.startsWith("//Complete_Dist ")){	// 명령어 : 조작자가 각 달팽이에게 승률 조작한 후 
					clientMainUI.textArea.append("▶  정해진 시간동안 배팅자들은 달팽이 하나를 선택해 승률을 확인하세요. \n");
					if(auth==false) {
						String snailRate1 = msg.substring(16, msg.indexOf("#"));
						String snailRate2 = msg.substring(msg.indexOf("#")+1, msg.indexOf("@"));
						String snailRate3 = msg.substring(msg.indexOf("@")+1, msg.indexOf("$"));
						String snailRate4 = msg.substring(msg.indexOf("$")+1);
						clientMainUI.snailTF1.setText(snailRate1);
						clientMainUI.snailTF2.setText(snailRate2);
						clientMainUI.snailTF3.setText(snailRate3);
						clientMainUI.snailTF4.setText(snailRate4);
						
						clientMainUI.clientHandler.filter = "달팽이 승률 확인";
						addSnailEvent();
					}
					
					
				}else if(msg.startsWith("//Timer ")){   // 명령어 : 타이머
		               if(gameStart==true) {
		                  clientMainUI.timerLabel.setText(msg.substring(8) + " 초");
		               }
					
					
				}else if(msg.startsWith("//ChooseEnd")) {	// 명령어 : 배팅자들이 달팽이를 선택해 승률 보는 시간 '종료'
					if(gameStart==true) {
						clientMainUI.textArea.append("\n▶  달팽이 선택 시간(승률 확인)이 종료되었습니다.\n");
						// 만약 아무것도 선택안했다면, 정해진 시간 이후에는 달팽이 승률 확인 불가
						if(clientMainUI.snailTF1.isVisible()==false && clientMainUI.snailTF2.isVisible()==false 
							&& clientMainUI.snailTF3.isVisible()==false && clientMainUI.snailTF4.isVisible()==false) {
							clientMainUI.textArea.append("▶  정해진 시간 내에 달팽이 선택(승률 확인)을 하지 않았기 때문에, 달팽이의 승률을 확인 하지 못합니다. \n");
							removeSnailEvent();
						}
						clientMainUI.textArea.append("▶  배팅자가 달팽이 승률 확인 후 모든 참가자는 채팅으로 1등 달팽이를 유추하기 시작합니다. (시간 : 60초)\n");
						clientMainUI.clientSender.dos.writeUTF("//ChatStart " + 60);
						clientMainUI.clientSender.dos.flush();
					}
					
					
				}else if(msg.startsWith("//ChatEnd")){	// 명령어 : 대화시간 종료
					clientMainUI.textArea.append("\n▶  대화 시간(달팽이 유추)이 종료되었습니다. \n");
					if(gameStart==true && auth==false) {
						clientMainUI.textArea.append("배팅자들은 가장 승률이 높다고 생각되는 달팽이를 선택해주시고, 배팅 코인을 입력하세요. \n");
						// 배팅 코인 입력하는 창 초기화 
						clientMainUI.snailTF1.setText("");	clientMainUI.snailTF1.setEnabled(true);	clientMainUI.snailTF1.setVisible(false);
						clientMainUI.snailTF2.setText("");	clientMainUI.snailTF2.setEnabled(true);	clientMainUI.snailTF2.setVisible(false);
						clientMainUI.snailTF3.setText("");	clientMainUI.snailTF3.setEnabled(true);	clientMainUI.snailTF3.setVisible(false);
						clientMainUI.snailTF4.setText("");	clientMainUI.snailTF4.setEnabled(true);	clientMainUI.snailTF4.setVisible(false);
						clientMainUI.clientHandler.filter = "달팽이 배팅코인 입력";
						clientMainUI.bettingBtn.setVisible(true);	clientMainUI.bettingBtn.setEnabled(true);
						clientMainUI.msgTF.setEnabled(false);	// 배팅하는 동안은 채팅 비활성화
						addSnailEvent();
					}
					
					
				} else if (msg.startsWith("//Error_Betting ")) { // 명령어 : 코인 배팅 경고
					String sort = msg.substring(16, 18);
					String bettor = msg.substring(18);
					String clientId = clientMainUI.cLogin.idTF.getText();
					if (bettor.equals(clientId)) {
						switch (sort) {
						case "ov":
							JOptionPane.showMessageDialog(null, "배팅 코인 수가  보유한 코인 수 보다 많습니다. \n다시 입력해주세요.", "배팅오류",
									JOptionPane.ERROR_MESSAGE);
							break;
						case "lo":
							JOptionPane.showMessageDialog(null, "배팅 최소 단위는 1코인 입니다. \n다시 입력해주세요.", "배팅오류",
									JOptionPane.ERROR_MESSAGE);
							break;
						}
					}

				}else if(msg.startsWith("//Complete_Betting ")) {	// 명령어 : 코인 배팅 완료
					String bettor = msg.substring(msg.indexOf("#")+1);
					String clientId = clientMainUI.cLogin.idTF.getText();
					if(bettor.equals(clientId)){
						clientMainUI.bettingBtn.setIcon(clientMainUI.betImg);
						clientMainUI.snailTF1.setEnabled(false);	clientMainUI.snailTF2.setEnabled(false);
						clientMainUI.snailTF3.setEnabled(false);	clientMainUI.snailTF4.setEnabled(false);
					}
					
					
				}else {	// 명령어 : 일반 채팅 출력
					clientMainUI.textArea.append(msg + "\n");
				}
				clientMainUI.textArea.setCaretPosition(clientMainUI.textArea.getDocument().getLength());	// 스크롤바가 항상 밑으로 내려오게 하는 기능
			}catch(IOException io) {	// 서버와 연결이 끊긴 경우
				clientMainUI.textArea.append("▶ 서버와 연결이 끊겼습니다. ◀ \n");
				clientMainUI.textArea.append("▶ 닉네임 중복, 서버 정원 초과인 경우 연결이 거부됩니다. ◀ ");
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
	
	void updateClient() { // 클라이언트 목록 추가
		if (Integer.parseInt(clientIdx) == 0) {
			clientMainUI.userInfoLabel1.setText("[ 닉네임 : " + clientId + " / " + "코인 : " + clientCoin + " ]");
			if (clientPickC.equals("캐릭1")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char1);
			} else if (clientPickC.equals("캐릭2")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char2);
			} else if (clientPickC.equals("캐릭3")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char3);
			} else if (clientPickC.equals("캐릭4")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char4);
			} else if (clientPickC.equals("캐릭5")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char5);
			} else if (clientPickC.equals("캐릭6")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char6);
			} else if (clientPickC.equals("캐릭7")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char7);
			} else if (clientPickC.equals("캐릭8")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char8);
			} else if (clientPickC.equals("캐릭9")) {
				clientMainUI.userImgLabel1.setIcon(clientMainUI.cLogin.char9);
			}
			clientMainUI.userImgLabel2.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel3.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel4.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel5.setIcon(clientMainUI.userImg1);
			// 클라이언트 목록 제거
			clientMainUI.userInfoLabel2.setText("[ 닉네임 / 코인 ] ");
			clientMainUI.userInfoLabel3.setText("[ 닉네임 / 코인 ] ");
			clientMainUI.userInfoLabel4.setText("[ 닉네임 / 코인 ] ");
			clientMainUI.userInfoLabel5.setText("[ 닉네임 / 코인 ] ");
		} else if (Integer.parseInt(clientIdx) == 1) {
			clientMainUI.userInfoLabel2.setText("[ 닉네임 : " + clientId + " / " + "코인 : " + clientCoin + " ]");
			if (clientPickC.equals("캐릭1")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char1);
			} else if (clientPickC.equals("캐릭2")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char2);
			} else if (clientPickC.equals("캐릭3")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char3);
			} else if (clientPickC.equals("캐릭4")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char4);
			} else if (clientPickC.equals("캐릭5")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char5);
			} else if (clientPickC.equals("캐릭6")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char6);
			} else if (clientPickC.equals("캐릭7")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char7);
			} else if (clientPickC.equals("캐릭8")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char8);
			} else if (clientPickC.equals("캐릭9")) {
				clientMainUI.userImgLabel2.setIcon(clientMainUI.cLogin.char9);
			}
			clientMainUI.userImgLabel3.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel4.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel5.setIcon(clientMainUI.userImg1);
			clientMainUI.userInfoLabel3.setText("[ 닉네임 / 코인 ] ");
			clientMainUI.userInfoLabel4.setText("[ 닉네임 / 코인 ] ");
			clientMainUI.userInfoLabel5.setText("[ 닉네임 / 코인 ] ");
		} else if (Integer.parseInt(clientIdx) == 2) {
			clientMainUI.userInfoLabel3.setText("[ 닉네임 : " + clientId + " / " + "코인 : " + clientCoin + " ]");
			if (clientPickC.equals("캐릭1")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char1);
			} else if (clientPickC.equals("캐릭2")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char2);
			} else if (clientPickC.equals("캐릭3")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char3);
			} else if (clientPickC.equals("캐릭4")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char4);
			} else if (clientPickC.equals("캐릭5")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char5);
			} else if (clientPickC.equals("캐릭6")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char6);
			} else if (clientPickC.equals("캐릭7")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char7);
			} else if (clientPickC.equals("캐릭8")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char8);
			} else if (clientPickC.equals("캐릭9")) {
				clientMainUI.userImgLabel3.setIcon(clientMainUI.cLogin.char9);
			}
			clientMainUI.userImgLabel4.setIcon(clientMainUI.userImg1);
			clientMainUI.userImgLabel5.setIcon(clientMainUI.userImg1);
			clientMainUI.userInfoLabel4.setText("[ 닉네임 / 코인 ] ");
			clientMainUI.userInfoLabel5.setText("[ 닉네임 / 코인 ] ");
		} else if (Integer.parseInt(clientIdx) == 3) {
			clientMainUI.userInfoLabel4.setText("[ 닉네임 : " + clientId + " / " + "코인 : " + clientCoin + " ]");
			if (clientPickC.equals("캐릭1")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char1);
			} else if (clientPickC.equals("캐릭2")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char2);
			} else if (clientPickC.equals("캐릭3")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char3);
			} else if (clientPickC.equals("캐릭4")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char4);
			} else if (clientPickC.equals("캐릭5")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char5);
			} else if (clientPickC.equals("캐릭6")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char6);
			} else if (clientPickC.equals("캐릭7")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char7);
			} else if (clientPickC.equals("캐릭8")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char8);
			} else if (clientPickC.equals("캐릭9")) {
				clientMainUI.userImgLabel4.setIcon(clientMainUI.cLogin.char9);
			}
			clientMainUI.userImgLabel5.setIcon(clientMainUI.userImg1);
			clientMainUI.userInfoLabel5.setText("[ 닉네임 / 코인 ] ");
		} else if (Integer.parseInt(clientIdx) == 4) {
			clientMainUI.userInfoLabel5.setText("[ 닉네임 : " + clientId + " / " + "코인 : " + clientCoin + " ]");
			if (clientPickC.equals("캐릭1")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char1);
			} else if (clientPickC.equals("캐릭2")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char2);
			} else if (clientPickC.equals("캐릭3")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char3);
			} else if (clientPickC.equals("캐릭4")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char4);
			} else if (clientPickC.equals("캐릭5")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char5);
			} else if (clientPickC.equals("캐릭6")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char6);
			} else if (clientPickC.equals("캐릭7")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char7);
			} else if (clientPickC.equals("캐릭8")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char8);
			} else if (clientPickC.equals("캐릭9")) {
				clientMainUI.userImgLabel5.setIcon(clientMainUI.cLogin.char9);
			}
		}
	}
}