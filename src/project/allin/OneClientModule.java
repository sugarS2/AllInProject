package project.allin;

import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;
import java.net.*;

public class OneClientModule extends Thread{
	ServerUI s;
	String clientId;	// 클라이언트가 입력한 닉네임 ID
	String pickC;		// 클라이언트가 선택한 캐릭터
	DataInputStream dis;
	
	DataOutputStream dos;
	
	String[] keys1;	// 조작자
	StopWatch timer;
	
	String highestSnailByAuth = "";
	int highestSnailRateByAuth = -1;
	ClientInfo c;
	
	int randomAuth;
	
	OneClientModule(ServerUI s){
		this.s = s;
		try {
			dis = new DataInputStream(s.socket.getInputStream());
			dos = new DataOutputStream(s.socket.getOutputStream());
		}catch(IOException io) {
		}
	}

	// listen 기능
	public void run() {
		try {
			clientId = dis.readUTF();
			pickC = dis.readUTF();   
			if(s.clientInfo.containsKey(clientId)) {	// 중복 닉네임이 있는 경우,
				//JOptionPane.showMessageDialog(null, "중복 닉네임이 존재!", "접속 오류", JOptionPane.ERROR_MESSAGE);
				s.socket.close();	// 소켓 연결 거부
			}else {
				s.clientList.put(clientId, new DisDos(dis, dos));
				s.clientInfo.put(clientId, 10);
				s.clientCharactor.put(clientId, pickC);
				s.textArea.append(clientId + "님이  접속했습니다.  (현재 접속자 수 : " + s.clientList.size() + " / 5 명)\n");
				s.textArea.setCaretPosition(s.textArea.getDocument().getLength());
				broadCast("▶  "+ clientId + "님이 접속했습니다.  (현재 접속자 수 : " + s.clientList.size() + " / 5명)");
				updateClient();	// 클라이언트 목록 갱신
				if(s.clientList.size() == s.MAX_CLIENT) {
					s.textArea.append("▶  5명의 참가자가 모두 입장했습니다. Ready버튼이 활성화됩니다.\n");
					s.textArea.setCaretPosition(s.textArea.getDocument().getLength());
					broadCast("\n▶  5명의 참가자가 모두 입장했습니다. Ready버튼이 활성화됩니다.");
					broadCast("//AllEnter");
				}
				while(dis!=null) {
					String msg = dis.readUTF();
					msgFilter(msg);
				}
			}
		}catch(IOException io) {  // 클라이언트 퇴장시 제거
			s.clientList.remove(clientId);
			s.clientInfo.remove(clientId);	// 클라이언트  ID, Coin 삭제
			s.clientCharactor.remove(clientId);		// 클라이언트 ID, Character 삭제
			
			s.textArea.append(clientId + "님이 퇴장했습니다.  (현재 접속자 수 : " + s.clientList.size() + " / 5명) \n");
			s.textArea.setCaretPosition(s.textArea.getDocument().getLength());
			broadCast("▶  " + clientId + "님이 퇴장했습니다.  (현재 접속자 수 : " + s.clientList.size() + " / 5명)");
			
			updateClient();	// 클라이언트 목록 갱신
			s.readyCount=0;  // 새로운 클라이언트가 접속해도 게임시작에 문제가 없도록 변수 초기화
			s.gameStart = false;
			s.round=0;
			s.bettingCount=0;
			s.winner.clear();
			broadCast("//GameEnd");
			closeAll();
		}
	}
	
	void gameStart() {
		keys1 = new String[s.clientInfo.size()];
		int index=0;
		for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
		    keys1[index] = mapEntry.getKey();
		    index++;
		}
		randomAuth = new Random().nextInt(s.clientInfo.size());
		if(s.round==4) s.round=0;
		s.textArea.append("이번 게임 조작자는 " + keys1[s.round] + "입니다. \n");
		broadCast("▶  이번 게임 조작자는 " + keys1[s.round] + "입니다. \n");
		
		
		s.clientInfoVector.clear();
		c = new ClientInfo(keys1[s.round], s.clientCharactor.get(keys1[s.round]),
				s.clientInfo.get(keys1[s.round]), 0, "null");

		System.out.println("keys1[s.round] : " + keys1[s.round] + "   c.getId() : " + c.getId());
		s.clientInfoVector.add(c);
		

		broadCast("//GameStart");
		broadCast("//Auth " + keys1[s.round]);	// 권한 부여
	}
	
	void msgFilter(String msg) {
		if(msg.startsWith("//Msg ")) {	// 명령어 : 일반채팅
			s.textArea.append(msg.substring(6) + "\n");
			//s.textArea.setCaretPosition(s.textArea.getDocument().getLength());	// 스크롤바가 항상 밑으로 내려오게 하는 기능
			broadCast(msg.substring(6));
			
			
		}else if(msg.startsWith("//Ready ")) {	// 명령어 : 한 참가자가 Ready한 경우
			s.readyCount++;
			s.textArea.append(msg.substring(8) + "님이 'Ready' 했습니다. (준비자 : " + s.readyCount + " / 5명) \n");
			broadCast("▶  " + msg.substring(8) + "님이 'Ready' 했습니다. (준비자 : " + s.readyCount + " / 5명)");
			// 5명이 꽉 찬 경우, Ready 버튼 활성화 시키기
			// System.out.println("s.readyCount : " + s.readyCount);
			if(s.readyCount == 5) {  // 모든 참가자가 준비되어 게임 시작
				s.gameStart=true;
				broadCast("//Clear");	// 클라이언트 채팅창 clear
				s.textArea.append("모든 참가자가 준비되었습니다. 3초 후 게임을 시작합니다.\n");
				broadCast("▶  모든 참가자가 준비되었습니다. 3초 후 게임을 시작합니다.\n");
				try {
					Thread.sleep(3000);
					broadCast("//RoundStart " + s.round);	// 라운드 시작
					gameStart();
				} catch (InterruptedException e) {
				}
			}
		}else if(msg.startsWith("//UnReady ")) {
			s.readyCount--;
			s.textArea.append(msg.substring(10) + "님이 'UnReady' 했습니다. (준비자 : " + s.readyCount + " / 5)\n");
			broadCast("▶  " + msg.substring(10) + "님이 'UnReady' 했습니다. (준비자 : " + s.readyCount + " / 5)");
			
			
		}else if(msg.startsWith("//Complete_Dist ")) {  // 명령어 : 조작자 승률 조작 완료
			broadCast("▶  조작자가 각 달팽이에게 승률을 부여했습니다.");
			
			// 조작자가 승률 조작하면 각 달팽이의 승률을 저장
			String snailRate1 = msg.substring(16, msg.indexOf("#"));
			String snailRate2 = msg.substring(msg.indexOf("#")+1, msg.indexOf("@"));
			String snailRate3 = msg.substring(msg.indexOf("@")+1, msg.indexOf("$"));
			String snailRate4 = msg.substring(msg.indexOf("$")+1);
			s.snailsRate.put("1", Integer.parseInt(snailRate1));
			s.snailsRate.put("2", Integer.parseInt(snailRate2));
			s.snailsRate.put("3", Integer.parseInt(snailRate3));
			s.snailsRate.put("4", Integer.parseInt(snailRate4));
			
			broadCast(msg);
			timer = new StopWatch(msg, 15);	// 정해진 시간동안 배팅자들은 달팽이 하나를 선택해 승률을 확인
			
			
		}else if(msg.startsWith("//ChatStart ")) {	// 명령어 : 배팅자가 달팽이 승률 확인 후 모든 참가자가 채팅으로 1등 달팽이 유추  시작
			int limit = Integer.parseInt(msg.substring(12));
			timer = new StopWatch(msg, limit);	// 정해진 시간동안 대화로 유추 시작
		
			
		}else if(msg.startsWith("//Complete_Betting ")) {	// 명령어 : 각 배팅자가 선택한 달팽이에 코인을 배팅한 경우
			String bettingSnail = msg.substring(19, msg.indexOf("$"));
			int bettingCoin = Integer.parseInt(msg.substring(msg.indexOf("$")+1 , msg.indexOf("#")));
			String bettor = msg.substring(msg.indexOf("#")+1);
			int holdingCoin = s.clientInfo.get(bettor);	// 배팅자가 가지고 있는 코인 수
			
			if (bettingCoin > holdingCoin)
				broadCast("//Error_Betting ov" + bettor); // 클라이언트에게 경고 줌
			else if (bettingCoin <= 0)
				broadCast("//Error_Betting lo" + bettor);
			else {
				broadCast(msg);
				s.bettingCount++;
				
				c = new ClientInfo(bettor, s.clientCharactor.get(bettor), holdingCoin, bettingCoin, bettingSnail);
				s.clientInfoVector.add(c);
				
				if(s.bettingCount == s.MAX_CLIENT-1) {	// 모든 클라이언트가 배팅완료하면
					broadCast("\n▶  모든 배팅자가 배팅을 완료했습니다. 3초 후 결과를 보여줍니다.");
					try {
						Thread.sleep(3000);
						
						int snail1 = s.snailsRate.get("1");
						int snail2 = s.snailsRate.get("2");
						int snail3 = s.snailsRate.get("3");
						int snail4 = s.snailsRate.get("4");
						
						broadCast("========================================================");
						broadCast("▶▶  조작자가 정한 배팅달팽이 승률 : " + snail1 + " / " + snail2 + " / " + snail3 + " / " + snail4 + "  ◀◀");
						broadCast("========================================================");
						
						findWinner();
					}catch(InterruptedException ie) {}
				}
			}
		}
		s.textArea.setCaretPosition(s.textArea.getDocument().getLength());
	}
	
	// 정답자 찾는 기능
	void findWinner() {
		findHighestSnailByAuth();
		findHighestSnailByBettor();
		rewardWinner();
	}
	
	// 조작자가 정한 가장 승률 높은 달팽이를 구함 (만약, 승률 높은 달팽이가 2마리 이상이면?)
	void findHighestSnailByAuth() {
		for(Map.Entry<String, Integer> mapEntry : s.snailsRate.entrySet()){
			String snail = mapEntry.getKey();
			int snailRate = mapEntry.getValue();
			if(snailRate > highestSnailRateByAuth) {
				highestSnailRateByAuth = snailRate;
				highestSnailByAuth = snail;
			}
		}
	}
	
	// 배팅자 중 가장 승률 높은 달팽이를 택한 배팅자를 구함 ★
	void findHighestSnailByBettor() {
		int cIndex=0;
		for(ClientInfo client : s.clientInfoVector) {
			client = s.clientInfoVector.get(cIndex++);
			
			String id = client.getId();
			int holdingCoin = client.getHoldingCoin();
			int bettingCoin = client.getBettingCoin();
			String bettingSnail = client.getBettingSnail();
			System.out.println(id + " / " + holdingCoin + " / " + bettingCoin + " / " + bettingSnail);
			
			
			if(bettingSnail.equals(highestSnailByAuth)) {
				System.out.println("highestSnailByAuth : " + highestSnailByAuth + " / 클라이언트 " + id);
				s.winner.add(id);
			}
		}
	}
	
	
	// 승자들에게는 보상코인을 줌 
	void rewardWinner() {
		int winnerCount = s.winner.size();	// 승자들이 몇 명인 지 출력
		
		if(winnerCount==0) {	// 정답자가 아무도 없으면, 조작자에게 코인X5배
			int index=0;
			for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
			    String client = mapEntry.getKey();
			    int clientHoldingCoin = mapEntry.getValue();
			    if(keys1[s.round].equals(client)) {
			    	System.out.println("조작자 초기 보유 코인 : " + clientHoldingCoin);
			    	s.clientInfo.put(client, clientHoldingCoin*5);
			    	System.out.println("조작자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }else {	// 나머지는 보유코인-배팅코인
			    	c = s.clientInfoVector.get(index);
			    	s.clientInfo.put(client, clientHoldingCoin-c.getBettingCoin());
			    	System.out.println("패배자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }
			    index++;
			}
		}else if(winnerCount==1) {	// 정답자 1명인 경우, 정답자 추출 후 정답자 코인X5배
			int index=0;
			int result_w=0;
			for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
			    String client = mapEntry.getKey();
			    int clientHoldingCoin = mapEntry.getValue();
			    if(s.winner.get(0).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("정답자 초기 보유 코인 : " + clientHoldingCoin + " 배팅코인 : " + c.getBettingCoin());
			    	result_w = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w);
			    	System.out.println("정답자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }else if (client.equals(keys1[s.round])) {	// 조작자는 코인 그대로
			    }else {	// 나머지는 보유코인-배팅코인
			    	if(!client.equals(keys1[s.round])) {
				    	c = s.clientInfoVector.get(index);
				    	s.clientInfo.put(client, clientHoldingCoin-c.getBettingCoin());
				    	System.out.println("패배자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    	}
			    }
			    index++;
			}
			s.clientInfo.put(keys1[s.round], 10);	//조작자
			s.clientInfo.put(s.winner.get(0), result_w);	//정답자
			
		}else if(winnerCount==2) {	// 정답자 2명인 경우, 정답자 추출 후 정답자 코인X4배
			int index=0;
			int result_w0=0; int result_w1=0;
			for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
			    String client = mapEntry.getKey();
			    int clientHoldingCoin = mapEntry.getValue();
			    if(s.winner.get(0).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("정답자 초기 보유 코인 : " + clientHoldingCoin + " 배팅코인 : " + c.getBettingCoin());
			    	result_w0 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w0);
			    	System.out.println("정답자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }else if(s.winner.get(1).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("정답자 초기 보유 코인 : " + clientHoldingCoin + " 배팅코인 : " + c.getBettingCoin());
			    	result_w1 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w1);
			    	System.out.println("정답자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }else if (client.equals(keys1[s.round])) {	// 조작자는 코인 그대로
			    }else {	// 나머지는 보유코인-배팅코인
			    	if(!client.equals(keys1[s.round])) {
				    	c = s.clientInfoVector.get(index);
				    	s.clientInfo.put(client, clientHoldingCoin-c.getBettingCoin());
				    	System.out.println("패배자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    	}
			    }
			    index++;
			}
			s.clientInfo.put(keys1[s.round], 10);	//조작자
			s.clientInfo.put(s.winner.get(0), result_w0);	//정답자
			s.clientInfo.put(s.winner.get(1), result_w1);	//정답자
		}else if(winnerCount==3) {	// 정답자 3명인 경우, 정답자 추출 후 정답자 코인X3배
			int index=0;
			int result_w0=0; int result_w1=0; int result_w2=0;
			for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
			    String client = mapEntry.getKey();
			    int clientHoldingCoin = mapEntry.getValue();
			    if(s.winner.get(0).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("정답자 초기 보유 코인 : " + clientHoldingCoin + " 배팅코인 : " + c.getBettingCoin());
			    	result_w0 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w0);
			    	System.out.println("정답자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }else if(s.winner.get(1).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("정답자 초기 보유 코인 : " + clientHoldingCoin + " 배팅코인 : " + c.getBettingCoin());
			    	result_w1 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w1);
			    	System.out.println("정답자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }else if(s.winner.get(2).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("정답자 초기 보유 코인 : " + clientHoldingCoin + " 배팅코인 : " + c.getBettingCoin());
			    	result_w2 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w2);
			    	System.out.println("정답자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }else if (client.equals(keys1[s.round])) {	// 조작자는 코인 그대로
			    }else {	// 나머지는 보유코인-배팅코인
			    	if(!client.equals(keys1[s.round])) {
				    	c = s.clientInfoVector.get(index);
				    	s.clientInfo.put(client, clientHoldingCoin-c.getBettingCoin());
				    	System.out.println("패배자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    	}
			    }
			    index++;
			}
			s.clientInfo.put(keys1[s.round], 10);	//조작자
			s.clientInfo.put(s.winner.get(0), result_w0);	//정답자
			s.clientInfo.put(s.winner.get(1), result_w1);	//정답자
			s.clientInfo.put(s.winner.get(2), result_w2);	//정답자
		}else if(winnerCount==4) {	// 정답자 4명(전부)인 경우, 정답자 추출 후 정답자 코인X2배
			int index=0;
			int result_w0=0; int result_w1=0; int result_w2=0; int result_w3=0;
			for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
			    String client = mapEntry.getKey();
			    int clientHoldingCoin = mapEntry.getValue();
			    if(s.winner.get(0).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("정답자 초기 보유 코인 : " + clientHoldingCoin + " 배팅코인 : " + c.getBettingCoin());
			    	result_w0 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w0);
			    	System.out.println("정답자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }else if(s.winner.get(1).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("정답자 초기 보유 코인 : " + clientHoldingCoin + " 배팅코인 : " + c.getBettingCoin());
			    	result_w1 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w1);
			    	System.out.println("정답자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }else if(s.winner.get(2).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("정답자 초기 보유 코인 : " + clientHoldingCoin + " 배팅코인 : " + c.getBettingCoin());
			    	result_w2 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w2);
			    	System.out.println("정답자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }else if(s.winner.get(3).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("정답자 초기 보유 코인 : " + clientHoldingCoin + " 배팅코인 : " + c.getBettingCoin());
			    	result_w3 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w3);
			    	System.out.println("정답자 (" + client + ") 최종 보유 코인 : " + s.clientInfo.get(client));
			    }else if (client.equals(keys1[s.round])) {	// 조작자는 코인 그대로
			    }
			    index++;
			}
			s.clientInfo.put(keys1[s.round], 10);	//조작자
			s.clientInfo.put(s.winner.get(0), result_w0);	//정답자
			s.clientInfo.put(s.winner.get(1), result_w1);	//정답자
			s.clientInfo.put(s.winner.get(2), result_w2);	//정답자
			s.clientInfo.put(s.winner.get(3), result_w3);	//정답자
		}
		showResult();
		updateClient();		// 클라이언트 목록 갱신
		s.readyCount=0;  // 새로운 클라이언트가 접속해도 게임시작에 문제가 없도록 변수 초기화
		s.gameStart = false;
		s.round++;
		s.bettingCount=0;
		s.winner.clear();
		for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()) {
			String client = mapEntry.getKey();	//클라이언트 아이디 구함
			s.clientInfo.put(client, 10);
		}
		
		broadCast("//End");
	}
	
	
	void showResult() {
		String output="\n\n\n";
		int winnerSize = s.winner.size();
		if(winnerSize==0) output += "▶  이번 라운드 정답자는 아무도 없으므로, 승자는 " + keys1[s.round] + " 입니다. \n";
		else output += "▶  이번 라운드 승자는 " + winnerSize + " 명 입니다. \n";
		int windex=0; int cIndex=0;
		for(String winner : s.winner) {
			winner = s.winner.get(windex++);
			cIndex=0;
			for(ClientInfo client : s.clientInfoVector) {
				client = s.clientInfoVector.get(cIndex++);
				if(winner.equals(client.getId()))
					output += "  [ " + winner + " ] 배팅코인 : " + client.getBettingCoin() + " / 획득코인 : " + client.getBettingCoin()*(6-winnerSize) + "\n";
			}
		}
		
		for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
			String clientId = mapEntry.getKey();
			for(ClientInfo c : s.clientInfoVector) 
				if(c.getId()==clientId) c.setHoldingCoin(s.clientInfo.get(clientId));
		}
		
		broadCast(output);
		s.winner.clear();	s.bettingCount=0;
		s.round++;
		broadCast("//NextRound " + s.round);
		gameStart();
	}
	
	
	// 내부 클래스 - 타이머
	class StopWatch extends Thread {
		String msg, tag;
		int count;

		StopWatch(String msg, int count) {
			this.msg = msg;
			this.count = count;
			start();
			if (msg.startsWith("//Complete_Dist")) {
				tag = "[승률 확인] ";
			} else if (msg.startsWith("//ChatStart ")) {
				tag = "[대화 시간] ";
			}
		}

		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
					broadCast("//Timer " + tag + count);
					if (count == 0) { // 정해진 시간이 지난 후,
						if (msg.startsWith("//Complete_Dist ")) {
							broadCast("//ChooseEnd"); // 배팅자는 달팽이 선택을 끝냄
						} else if (msg.startsWith("//ChatStart ")) {
							broadCast("//ChatEnd"); // 대화방 비활성화
						}
						break;
					}
					count--;
				} catch (InterruptedException ie) {
				}
			}
		}
	}
	
	public void closeAll(){
		try{
			if(dos != null) dos.close();
			if(dis != null) dis.close();
			//if(s.socket != null) s.socket.close();	// 소켓을 끊으면, 마지막 클라이언트도 연결 끊김  ★★
		}catch(IOException ie){}
	}
	
	// ClientMainUI.textArea 창에 뿌리는 기능
	void broadCast(String msg) {
		Iterator<String> it = s.clientList.keySet().iterator();
		while(it.hasNext()){
			try{
				DisDos disdos = s.clientList.get(it.next());
				disdos.getDos().writeUTF(msg);
				disdos.getDos().flush();
			}catch(IOException io){}
		}
	}
	
	// 클라이언트 목록 갱신
	void updateClient() {
		keys1 = new String[s.clientInfo.size()];	// id
		int[] values1 = new int[s.clientInfo.size()];		// coin
		String[] keys2 = new String[s.clientCharactor.size()];	// id
		String[] values2 = new String[s.clientCharactor.size()];	// character
		int index1 = 0;
		int index2 = 0;
		for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
		    keys1[index1] = mapEntry.getKey();
		    values1[index1] = mapEntry.getValue();
		    index1++;
		}
		
		for(Map.Entry<String, String> mapEntry : s.clientCharactor.entrySet()){
          keys2[index2] = mapEntry.getKey();
          values2[index2] = mapEntry.getValue();
          index2++;
		}
		
		for(int i=0; i<s.clientList.size(); i++){
			broadCast("//UpdateClient" + keys1[i] + "@" + values1[i] + "#" + values2[i] + "$" + i);
		}
	}
}
