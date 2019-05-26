package project.allin;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ServerUI extends JFrame{
	
	Container cp;
	ImageIcon titleImg, outImg, optionImg1, optionImg2, sendImg;
	JPanel topPanel, bottomPanel;
	JLabel titleLabel;
	JButton outBtn, msgBtn;
	JScrollPane sPane;
	JTextArea textArea;
	JComboBox<String> options;
	JTextField msgField;
	
	ServerSocket serverSocket;
	Socket socket;
	OneClientModule ocm;
	int readyCount=0;
	int bettingCount=0;
	boolean gameStart;
	int round=0;
	
	public static final int MAX_CLIENT = 5;	// 입장 제한 수 : 5명
	LinkedHashMap<String, DisDos> clientList = new LinkedHashMap<String, DisDos>();  // 클라이언트 아이디, 소켓 정보
	
	LinkedHashMap<String, Integer> clientInfo = new LinkedHashMap<String, Integer>();	// 클라이언트 아이디, 코인관리
	LinkedHashMap<String, String> clientCharactor = new LinkedHashMap<String, String>(); // 클라이언트 아이디, 캐릭터
	LinkedHashMap<String, Integer> snailsRate = new LinkedHashMap<String, Integer>();	// 조작자가 정한 각 달팽이 승률을 저장하는 공간 (달팽이명, 승률)
	
	Vector<ClientInfo> clientInfoVector = new Vector<ClientInfo>();	// 클라이언트 정보 모두 저장	
	Vector<String> winner = new Vector<String>();	// 승자 저장소
	
	ServerUI(){
		cp = getContentPane();
	}
	
	//Server 세팅하기
	void setServer() {
		try {
			serverSocket = new ServerSocket(8282);
			textArea.setText(textArea.getText() + "클라이언트 기다리는 중..\n");
			while(true) {
				socket = serverSocket.accept();
				if((clientList.size() + 1) > MAX_CLIENT) {	// 정원이 초과된 경우
					socket.close();
				}else {
					ocm = new OneClientModule(this);
					ocm.start();
				}
			}
		}catch(IOException io) {
			JOptionPane.showMessageDialog(null, "서버가 이미 열려있습니다.\n종료합니다.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	
	void init() {
		loadImages();
		setComponent();
		setUI();
		setServer();
	}
	
	//필요한 이미지 
	void loadImages() {
		try {
			titleImg = new ImageIcon(ImageIO.read(new File("imgs/serverBtn/s_title.png")));
			outImg = new ImageIcon(ImageIO.read(new File("imgs/serverBtn/s_outBtn.png")));
			optionImg1 = new ImageIcon(ImageIO.read(new File("imgs/serverBtn/s_option1.png")));
			optionImg2 = new ImageIcon(ImageIO.read(new File("imgs/serverBtn/s_option2.png")));
			sendImg = new ImageIcon(ImageIO.read(new File("imgs/serverBtn/s_send.png")));
		} catch (IOException ie) {
		}
	}
	
	void setComponent() {
		// Top Part 
		topPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(new EmptyBorder(5,5,5,5));	//위,왼,아래,오른쪽
		titleLabel = new JLabel(titleImg);
		outBtn = new JButton(outImg);
		outBtn.setBorderPainted(false);	//버튼 테두리 설정
		outBtn.setContentAreaFilled(false); //버튼 영역배경 표시 설정
		//outBtn.setFocusPainted(false);	//포커스 표시 설정 
		topPanel.add(titleLabel, BorderLayout.WEST);
		topPanel.add(outBtn, BorderLayout.EAST);
		cp.add(topPanel, BorderLayout.NORTH);
		
		// Center Part
		textArea = new JTextArea();
		sPane = new JScrollPane(textArea);
		sPane.setBorder(new EmptyBorder(5,10,5,10));
		cp.add(sPane, BorderLayout.CENTER);
		
		// Bottom Part
		bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(new EmptyBorder(5,5,5,5));
		Object[] items = {optionImg1, optionImg2};
		options = new JComboBox(items);
		options.setBorder(new EmptyBorder(0,5,0,10));
		//((JComponent)options.getComponent(0)).setBorder(new EmptyBorder(0,0,0,0));	//버튼 테두리 설정
		//((AbstractButton)options.getComponent(0)).setBorderPainted(false);
		msgField = new JTextField();
		msgBtn = new JButton(sendImg);
		
		msgBtn.setContentAreaFilled(false); //버튼 영역배경 표시 설정
		msgBtn.setBorderPainted(false);	//버튼 테두리 설정
		
		bottomPanel.add(options, BorderLayout.WEST);
		bottomPanel.add(msgField, BorderLayout.CENTER);
		bottomPanel.add(msgBtn, BorderLayout.EAST);
		cp.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	void setUI() {
		setTitle("ServerUI");
		setSize(500, 600);
		setLocation(100, 100);
		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		
		textArea.setEnabled(false);
	}
	
	public static void main(String[] args) {
		new ServerUI().init();
	}
}
