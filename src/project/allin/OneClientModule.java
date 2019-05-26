package project.allin;

import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;
import java.net.*;

public class OneClientModule extends Thread{
	ServerUI s;
	String clientId;	// Ŭ���̾�Ʈ�� �Է��� �г��� ID
	String pickC;		// Ŭ���̾�Ʈ�� ������ ĳ����
	DataInputStream dis;
	
	DataOutputStream dos;
	
	String[] keys1;	// ������
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

	// listen ���
	public void run() {
		try {
			clientId = dis.readUTF();
			pickC = dis.readUTF();   
			if(s.clientInfo.containsKey(clientId)) {	// �ߺ� �г����� �ִ� ���,
				//JOptionPane.showMessageDialog(null, "�ߺ� �г����� ����!", "���� ����", JOptionPane.ERROR_MESSAGE);
				s.socket.close();	// ���� ���� �ź�
			}else {
				s.clientList.put(clientId, new DisDos(dis, dos));
				s.clientInfo.put(clientId, 10);
				s.clientCharactor.put(clientId, pickC);
				s.textArea.append(clientId + "����  �����߽��ϴ�.  (���� ������ �� : " + s.clientList.size() + " / 5 ��)\n");
				s.textArea.setCaretPosition(s.textArea.getDocument().getLength());
				broadCast("��  "+ clientId + "���� �����߽��ϴ�.  (���� ������ �� : " + s.clientList.size() + " / 5��)");
				updateClient();	// Ŭ���̾�Ʈ ��� ����
				if(s.clientList.size() == s.MAX_CLIENT) {
					s.textArea.append("��  5���� �����ڰ� ��� �����߽��ϴ�. Ready��ư�� Ȱ��ȭ�˴ϴ�.\n");
					s.textArea.setCaretPosition(s.textArea.getDocument().getLength());
					broadCast("\n��  5���� �����ڰ� ��� �����߽��ϴ�. Ready��ư�� Ȱ��ȭ�˴ϴ�.");
					broadCast("//AllEnter");
				}
				while(dis!=null) {
					String msg = dis.readUTF();
					msgFilter(msg);
				}
			}
		}catch(IOException io) {  // Ŭ���̾�Ʈ ����� ����
			s.clientList.remove(clientId);
			s.clientInfo.remove(clientId);	// Ŭ���̾�Ʈ  ID, Coin ����
			s.clientCharactor.remove(clientId);		// Ŭ���̾�Ʈ ID, Character ����
			
			s.textArea.append(clientId + "���� �����߽��ϴ�.  (���� ������ �� : " + s.clientList.size() + " / 5��) \n");
			s.textArea.setCaretPosition(s.textArea.getDocument().getLength());
			broadCast("��  " + clientId + "���� �����߽��ϴ�.  (���� ������ �� : " + s.clientList.size() + " / 5��)");
			
			updateClient();	// Ŭ���̾�Ʈ ��� ����
			s.readyCount=0;  // ���ο� Ŭ���̾�Ʈ�� �����ص� ���ӽ��ۿ� ������ ������ ���� �ʱ�ȭ
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
		s.textArea.append("�̹� ���� �����ڴ� " + keys1[s.round] + "�Դϴ�. \n");
		broadCast("��  �̹� ���� �����ڴ� " + keys1[s.round] + "�Դϴ�. \n");
		
		
		s.clientInfoVector.clear();
		c = new ClientInfo(keys1[s.round], s.clientCharactor.get(keys1[s.round]),
				s.clientInfo.get(keys1[s.round]), 0, "null");

		System.out.println("keys1[s.round] : " + keys1[s.round] + "   c.getId() : " + c.getId());
		s.clientInfoVector.add(c);
		

		broadCast("//GameStart");
		broadCast("//Auth " + keys1[s.round]);	// ���� �ο�
	}
	
	void msgFilter(String msg) {
		if(msg.startsWith("//Msg ")) {	// ��ɾ� : �Ϲ�ä��
			s.textArea.append(msg.substring(6) + "\n");
			//s.textArea.setCaretPosition(s.textArea.getDocument().getLength());	// ��ũ�ѹٰ� �׻� ������ �������� �ϴ� ���
			broadCast(msg.substring(6));
			
			
		}else if(msg.startsWith("//Ready ")) {	// ��ɾ� : �� �����ڰ� Ready�� ���
			s.readyCount++;
			s.textArea.append(msg.substring(8) + "���� 'Ready' �߽��ϴ�. (�غ��� : " + s.readyCount + " / 5��) \n");
			broadCast("��  " + msg.substring(8) + "���� 'Ready' �߽��ϴ�. (�غ��� : " + s.readyCount + " / 5��)");
			// 5���� �� �� ���, Ready ��ư Ȱ��ȭ ��Ű��
			// System.out.println("s.readyCount : " + s.readyCount);
			if(s.readyCount == 5) {  // ��� �����ڰ� �غ�Ǿ� ���� ����
				s.gameStart=true;
				broadCast("//Clear");	// Ŭ���̾�Ʈ ä��â clear
				s.textArea.append("��� �����ڰ� �غ�Ǿ����ϴ�. 3�� �� ������ �����մϴ�.\n");
				broadCast("��  ��� �����ڰ� �غ�Ǿ����ϴ�. 3�� �� ������ �����մϴ�.\n");
				try {
					Thread.sleep(3000);
					broadCast("//RoundStart " + s.round);	// ���� ����
					gameStart();
				} catch (InterruptedException e) {
				}
			}
		}else if(msg.startsWith("//UnReady ")) {
			s.readyCount--;
			s.textArea.append(msg.substring(10) + "���� 'UnReady' �߽��ϴ�. (�غ��� : " + s.readyCount + " / 5)\n");
			broadCast("��  " + msg.substring(10) + "���� 'UnReady' �߽��ϴ�. (�غ��� : " + s.readyCount + " / 5)");
			
			
		}else if(msg.startsWith("//Complete_Dist ")) {  // ��ɾ� : ������ �·� ���� �Ϸ�
			broadCast("��  �����ڰ� �� �����̿��� �·��� �ο��߽��ϴ�.");
			
			// �����ڰ� �·� �����ϸ� �� �������� �·��� ����
			String snailRate1 = msg.substring(16, msg.indexOf("#"));
			String snailRate2 = msg.substring(msg.indexOf("#")+1, msg.indexOf("@"));
			String snailRate3 = msg.substring(msg.indexOf("@")+1, msg.indexOf("$"));
			String snailRate4 = msg.substring(msg.indexOf("$")+1);
			s.snailsRate.put("1", Integer.parseInt(snailRate1));
			s.snailsRate.put("2", Integer.parseInt(snailRate2));
			s.snailsRate.put("3", Integer.parseInt(snailRate3));
			s.snailsRate.put("4", Integer.parseInt(snailRate4));
			
			broadCast(msg);
			timer = new StopWatch(msg, 15);	// ������ �ð����� �����ڵ��� ������ �ϳ��� ������ �·��� Ȯ��
			
			
		}else if(msg.startsWith("//ChatStart ")) {	// ��ɾ� : �����ڰ� ������ �·� Ȯ�� �� ��� �����ڰ� ä������ 1�� ������ ����  ����
			int limit = Integer.parseInt(msg.substring(12));
			timer = new StopWatch(msg, limit);	// ������ �ð����� ��ȭ�� ���� ����
		
			
		}else if(msg.startsWith("//Complete_Betting ")) {	// ��ɾ� : �� �����ڰ� ������ �����̿� ������ ������ ���
			String bettingSnail = msg.substring(19, msg.indexOf("$"));
			int bettingCoin = Integer.parseInt(msg.substring(msg.indexOf("$")+1 , msg.indexOf("#")));
			String bettor = msg.substring(msg.indexOf("#")+1);
			int holdingCoin = s.clientInfo.get(bettor);	// �����ڰ� ������ �ִ� ���� ��
			
			if (bettingCoin > holdingCoin)
				broadCast("//Error_Betting ov" + bettor); // Ŭ���̾�Ʈ���� ��� ��
			else if (bettingCoin <= 0)
				broadCast("//Error_Betting lo" + bettor);
			else {
				broadCast(msg);
				s.bettingCount++;
				
				c = new ClientInfo(bettor, s.clientCharactor.get(bettor), holdingCoin, bettingCoin, bettingSnail);
				s.clientInfoVector.add(c);
				
				if(s.bettingCount == s.MAX_CLIENT-1) {	// ��� Ŭ���̾�Ʈ�� ���ÿϷ��ϸ�
					broadCast("\n��  ��� �����ڰ� ������ �Ϸ��߽��ϴ�. 3�� �� ����� �����ݴϴ�.");
					try {
						Thread.sleep(3000);
						
						int snail1 = s.snailsRate.get("1");
						int snail2 = s.snailsRate.get("2");
						int snail3 = s.snailsRate.get("3");
						int snail4 = s.snailsRate.get("4");
						
						broadCast("========================================================");
						broadCast("����  �����ڰ� ���� ���ô����� �·� : " + snail1 + " / " + snail2 + " / " + snail3 + " / " + snail4 + "  ����");
						broadCast("========================================================");
						
						findWinner();
					}catch(InterruptedException ie) {}
				}
			}
		}
		s.textArea.setCaretPosition(s.textArea.getDocument().getLength());
	}
	
	// ������ ã�� ���
	void findWinner() {
		findHighestSnailByAuth();
		findHighestSnailByBettor();
		rewardWinner();
	}
	
	// �����ڰ� ���� ���� �·� ���� �����̸� ���� (����, �·� ���� �����̰� 2���� �̻��̸�?)
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
	
	// ������ �� ���� �·� ���� �����̸� ���� �����ڸ� ���� ��
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
				System.out.println("highestSnailByAuth : " + highestSnailByAuth + " / Ŭ���̾�Ʈ " + id);
				s.winner.add(id);
			}
		}
	}
	
	
	// ���ڵ鿡�Դ� ���������� �� 
	void rewardWinner() {
		int winnerCount = s.winner.size();	// ���ڵ��� �� ���� �� ���
		
		if(winnerCount==0) {	// �����ڰ� �ƹ��� ������, �����ڿ��� ����X5��
			int index=0;
			for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
			    String client = mapEntry.getKey();
			    int clientHoldingCoin = mapEntry.getValue();
			    if(keys1[s.round].equals(client)) {
			    	System.out.println("������ �ʱ� ���� ���� : " + clientHoldingCoin);
			    	s.clientInfo.put(client, clientHoldingCoin*5);
			    	System.out.println("������ (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }else {	// �������� ��������-��������
			    	c = s.clientInfoVector.get(index);
			    	s.clientInfo.put(client, clientHoldingCoin-c.getBettingCoin());
			    	System.out.println("�й��� (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }
			    index++;
			}
		}else if(winnerCount==1) {	// ������ 1���� ���, ������ ���� �� ������ ����X5��
			int index=0;
			int result_w=0;
			for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
			    String client = mapEntry.getKey();
			    int clientHoldingCoin = mapEntry.getValue();
			    if(s.winner.get(0).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("������ �ʱ� ���� ���� : " + clientHoldingCoin + " �������� : " + c.getBettingCoin());
			    	result_w = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w);
			    	System.out.println("������ (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }else if (client.equals(keys1[s.round])) {	// �����ڴ� ���� �״��
			    }else {	// �������� ��������-��������
			    	if(!client.equals(keys1[s.round])) {
				    	c = s.clientInfoVector.get(index);
				    	s.clientInfo.put(client, clientHoldingCoin-c.getBettingCoin());
				    	System.out.println("�й��� (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    	}
			    }
			    index++;
			}
			s.clientInfo.put(keys1[s.round], 10);	//������
			s.clientInfo.put(s.winner.get(0), result_w);	//������
			
		}else if(winnerCount==2) {	// ������ 2���� ���, ������ ���� �� ������ ����X4��
			int index=0;
			int result_w0=0; int result_w1=0;
			for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
			    String client = mapEntry.getKey();
			    int clientHoldingCoin = mapEntry.getValue();
			    if(s.winner.get(0).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("������ �ʱ� ���� ���� : " + clientHoldingCoin + " �������� : " + c.getBettingCoin());
			    	result_w0 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w0);
			    	System.out.println("������ (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }else if(s.winner.get(1).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("������ �ʱ� ���� ���� : " + clientHoldingCoin + " �������� : " + c.getBettingCoin());
			    	result_w1 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w1);
			    	System.out.println("������ (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }else if (client.equals(keys1[s.round])) {	// �����ڴ� ���� �״��
			    }else {	// �������� ��������-��������
			    	if(!client.equals(keys1[s.round])) {
				    	c = s.clientInfoVector.get(index);
				    	s.clientInfo.put(client, clientHoldingCoin-c.getBettingCoin());
				    	System.out.println("�й��� (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    	}
			    }
			    index++;
			}
			s.clientInfo.put(keys1[s.round], 10);	//������
			s.clientInfo.put(s.winner.get(0), result_w0);	//������
			s.clientInfo.put(s.winner.get(1), result_w1);	//������
		}else if(winnerCount==3) {	// ������ 3���� ���, ������ ���� �� ������ ����X3��
			int index=0;
			int result_w0=0; int result_w1=0; int result_w2=0;
			for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
			    String client = mapEntry.getKey();
			    int clientHoldingCoin = mapEntry.getValue();
			    if(s.winner.get(0).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("������ �ʱ� ���� ���� : " + clientHoldingCoin + " �������� : " + c.getBettingCoin());
			    	result_w0 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w0);
			    	System.out.println("������ (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }else if(s.winner.get(1).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("������ �ʱ� ���� ���� : " + clientHoldingCoin + " �������� : " + c.getBettingCoin());
			    	result_w1 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w1);
			    	System.out.println("������ (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }else if(s.winner.get(2).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("������ �ʱ� ���� ���� : " + clientHoldingCoin + " �������� : " + c.getBettingCoin());
			    	result_w2 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w2);
			    	System.out.println("������ (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }else if (client.equals(keys1[s.round])) {	// �����ڴ� ���� �״��
			    }else {	// �������� ��������-��������
			    	if(!client.equals(keys1[s.round])) {
				    	c = s.clientInfoVector.get(index);
				    	s.clientInfo.put(client, clientHoldingCoin-c.getBettingCoin());
				    	System.out.println("�й��� (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    	}
			    }
			    index++;
			}
			s.clientInfo.put(keys1[s.round], 10);	//������
			s.clientInfo.put(s.winner.get(0), result_w0);	//������
			s.clientInfo.put(s.winner.get(1), result_w1);	//������
			s.clientInfo.put(s.winner.get(2), result_w2);	//������
		}else if(winnerCount==4) {	// ������ 4��(����)�� ���, ������ ���� �� ������ ����X2��
			int index=0;
			int result_w0=0; int result_w1=0; int result_w2=0; int result_w3=0;
			for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()){
			    String client = mapEntry.getKey();
			    int clientHoldingCoin = mapEntry.getValue();
			    if(s.winner.get(0).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("������ �ʱ� ���� ���� : " + clientHoldingCoin + " �������� : " + c.getBettingCoin());
			    	result_w0 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w0);
			    	System.out.println("������ (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }else if(s.winner.get(1).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("������ �ʱ� ���� ���� : " + clientHoldingCoin + " �������� : " + c.getBettingCoin());
			    	result_w1 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w1);
			    	System.out.println("������ (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }else if(s.winner.get(2).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("������ �ʱ� ���� ���� : " + clientHoldingCoin + " �������� : " + c.getBettingCoin());
			    	result_w2 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w2);
			    	System.out.println("������ (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }else if(s.winner.get(3).equals(client)) {
			    	c = s.clientInfoVector.get(index);
			    	System.out.println("������ �ʱ� ���� ���� : " + clientHoldingCoin + " �������� : " + c.getBettingCoin());
			    	result_w3 = (clientHoldingCoin-c.getBettingCoin()) + (c.getBettingCoin()*5);
			    	s.clientInfo.put(client, result_w3);
			    	System.out.println("������ (" + client + ") ���� ���� ���� : " + s.clientInfo.get(client));
			    }else if (client.equals(keys1[s.round])) {	// �����ڴ� ���� �״��
			    }
			    index++;
			}
			s.clientInfo.put(keys1[s.round], 10);	//������
			s.clientInfo.put(s.winner.get(0), result_w0);	//������
			s.clientInfo.put(s.winner.get(1), result_w1);	//������
			s.clientInfo.put(s.winner.get(2), result_w2);	//������
			s.clientInfo.put(s.winner.get(3), result_w3);	//������
		}
		showResult();
		updateClient();		// Ŭ���̾�Ʈ ��� ����
		s.readyCount=0;  // ���ο� Ŭ���̾�Ʈ�� �����ص� ���ӽ��ۿ� ������ ������ ���� �ʱ�ȭ
		s.gameStart = false;
		s.round++;
		s.bettingCount=0;
		s.winner.clear();
		for(Map.Entry<String, Integer> mapEntry : s.clientInfo.entrySet()) {
			String client = mapEntry.getKey();	//Ŭ���̾�Ʈ ���̵� ����
			s.clientInfo.put(client, 10);
		}
		
		broadCast("//End");
	}
	
	
	void showResult() {
		String output="\n\n\n";
		int winnerSize = s.winner.size();
		if(winnerSize==0) output += "��  �̹� ���� �����ڴ� �ƹ��� �����Ƿ�, ���ڴ� " + keys1[s.round] + " �Դϴ�. \n";
		else output += "��  �̹� ���� ���ڴ� " + winnerSize + " �� �Դϴ�. \n";
		int windex=0; int cIndex=0;
		for(String winner : s.winner) {
			winner = s.winner.get(windex++);
			cIndex=0;
			for(ClientInfo client : s.clientInfoVector) {
				client = s.clientInfoVector.get(cIndex++);
				if(winner.equals(client.getId()))
					output += "  [ " + winner + " ] �������� : " + client.getBettingCoin() + " / ȹ������ : " + client.getBettingCoin()*(6-winnerSize) + "\n";
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
	
	
	// ���� Ŭ���� - Ÿ�̸�
	class StopWatch extends Thread {
		String msg, tag;
		int count;

		StopWatch(String msg, int count) {
			this.msg = msg;
			this.count = count;
			start();
			if (msg.startsWith("//Complete_Dist")) {
				tag = "[�·� Ȯ��] ";
			} else if (msg.startsWith("//ChatStart ")) {
				tag = "[��ȭ �ð�] ";
			}
		}

		public void run() {
			while (true) {
				try {
					Thread.sleep(1000);
					broadCast("//Timer " + tag + count);
					if (count == 0) { // ������ �ð��� ���� ��,
						if (msg.startsWith("//Complete_Dist ")) {
							broadCast("//ChooseEnd"); // �����ڴ� ������ ������ ����
						} else if (msg.startsWith("//ChatStart ")) {
							broadCast("//ChatEnd"); // ��ȭ�� ��Ȱ��ȭ
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
			//if(s.socket != null) s.socket.close();	// ������ ������, ������ Ŭ���̾�Ʈ�� ���� ����  �ڡ�
		}catch(IOException ie){}
	}
	
	// ClientMainUI.textArea â�� �Ѹ��� ���
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
	
	// Ŭ���̾�Ʈ ��� ����
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
