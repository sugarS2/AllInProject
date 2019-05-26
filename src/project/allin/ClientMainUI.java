package project.allin;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

class ClientMainUI extends JFrame {
	Container cp;
	GridBagLayout gb;
	GridBagConstraints gbc;
	
	JPanel mainBackGP, panel, panel1, panel2, panel3, panel4, panel5, panel6, panel7, panel8, panel9, panel10, panel11;
	Image mainBgImg;
	
	//패널1 - 물음표
	JButton questionBtn;
	ImageIcon questionImg, ruleImg;
	// 패널2 - 타이머
	JLabel timerLabel;
	// 패널3 - 유저1
	ImageIcon userImg1;		JLabel userImgLabel1, userInfoLabel1;
	// 패널4 - snails
	JPanel snailPanel1, snailPanel2, snailPanel3, snailPanel4;
	JPanel snailPanel1_1, snailPanel2_1, snailPanel3_1, snailPanel4_1;
	ImageIcon snailImg1, snailImg2, snailImg3, snailImg4;
	JLabel snailLabel1, snailLabel2, snailLabel3, snailLabel4;
	JTextField snailTF1, snailTF2, snailTF3, snailTF4;
	// 패널5 - 유저2 
	ImageIcon userImg2;		JLabel userImgLabel2, userInfoLabel2;
	// 패널6 - 유저3
	ImageIcon userImg3;		JLabel userImgLabel3, userInfoLabel3;
	// 패널7 - 유저4
	ImageIcon userImg4;		JLabel userImgLabel4, userInfoLabel4;	
	// 패널8 - 유저5
	ImageIcon userImg5;		JLabel userImgLabel5, userInfoLabel5;	
	// 패널9 - 채팅창
	JTextArea textArea;		JScrollPane scrollPane;	//채팅화면
	JPanel msgPanel;	JTextField msgTF;	JButton msgBtn;	//메시지부분
	// 패널10 - 승률 조작버튼 
	ImageIcon distImg, dist_hImg, betImg, bet_hImg;      JButton distBtn, bettingBtn;
	// 패널11 - ready, exit 버튼
	JButton readyBtn, exitBtn;
	ImageIcon readyImg, ready_hImg, exitImg, exit_hImg;
	
	ClientLoginUI cLogin;
	ClientHandler clientHandler;
	Socket socket;
	ClientSender clientSender;
	ClientListener clientListener;
	
	ClientMainUI(ClientLoginUI cLogin) {
		this.cLogin = cLogin;

		cp = getContentPane();
		gb = new GridBagLayout();
		gbc = new GridBagConstraints();
		clientHandler = new ClientHandler(this);
		init();
		connectServer();
	}
	
	
	//로그인 성공 시, 서버랑 연결하기
	void connectServer() {
		try {
			socket = new Socket(cLogin.ipTF.getText(), 8282);
			clientSender = new ClientSender(this);//송신
			clientListener = new ClientListener(socket, this);
			new Thread(clientSender).start();
			new Thread(clientListener).start();
			addEvent();
		}catch(UnknownHostException ne) {
		}catch(IOException io) {
		}
	}	
	
	void init() {
		loadImgs();
		setGridBag();
		setUI();
		
	}
	
	void loadImgs() {
		try {
			mainBgImg = ImageIO.read(new File("imgs/bg/bg_02.png"));
			questionImg = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/question.png")));
			ruleImg = new ImageIcon(ImageIO.read(new File("imgs/bg/rule.png")));
			userImg1 = new ImageIcon(ImageIO.read(new File("imgs/character/person_b.png")));
			userImg2 = new ImageIcon(ImageIO.read(new File("imgs/character/person_b.png")));
			userImg3 = new ImageIcon(ImageIO.read(new File("imgs/character/person_b.png")));
			userImg4 = new ImageIcon(ImageIO.read(new File("imgs/character/person_b.png")));
			userImg5 = new ImageIcon(ImageIO.read(new File("imgs/character/person_b.png")));
			snailImg1 = new ImageIcon(ImageIO.read(new File("imgs/snails/miniSnail1.png")));
			snailImg2 = new ImageIcon(ImageIO.read(new File("imgs/snails/miniSnail2.png")));
			snailImg3 = new ImageIcon(ImageIO.read(new File("imgs/snails/miniSnail3.png")));
			snailImg4 = new ImageIcon(ImageIO.read(new File("imgs/snails/miniSnail4.png")));
			readyImg = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/ready.png")));
			ready_hImg = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/ready_hover.png")));
			exitImg = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/exit.png")));
			exit_hImg = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/exit_hover.png")));
			distImg = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/dist.png")));
			dist_hImg = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/dist_hover.png")));
			betImg = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/bet.png")));
			bet_hImg = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/bet_hover.png")));
		} catch (IOException io) {
		}
	}

	void make(JComponent jc, int x, int y, int w, int h) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;

		gb.setConstraints(jc, gbc);
	}
	
	void setGridBag() {
		panel = new JPanel(gb);
		panel.setOpaque(false);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.5;
		gbc.weighty = 0.4;

		// 패널1 - 물음표(게임 룰)
		panel1 = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panel1.setOpaque(false);
		questionBtn = new JButton(questionImg);
		questionBtn.setBorderPainted(false);
		questionBtn.setContentAreaFilled(false);
		questionBtn.setFocusPainted(false);
		panel1.add(questionBtn);
		make(panel1, 0, 0, 2, 1);
		panel.add(panel1);

		// 패널2 - 타이머
		panel2 = new JPanel(new BorderLayout());
		panel2.setOpaque(false);
		timerLabel = new JLabel("타이머 부분");
		timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timerLabel.setFont(new Font("a전자시계", Font.PLAIN, 20));
		timerLabel.setForeground(Color.WHITE);
		
		panel2.add(timerLabel);
		make(panel2, 2, 0, 1, 1);
		panel.add(panel2);

		// 패널3 - 유저1
		gbc.weighty = 0.8;
		panel3 = new JPanel(new BorderLayout());
		panel3.setOpaque(false);
		userImgLabel1 = new JLabel(userImg1);
		userInfoLabel1 = new JLabel("유저1 정보");
		userInfoLabel1.setForeground(Color.WHITE);
		userInfoLabel1.setFont(new Font("a전자시계", Font.PLAIN, 15));
		userInfoLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		panel3.add(userImgLabel1, BorderLayout.CENTER);
		panel3.add(userInfoLabel1, BorderLayout.SOUTH);
		make(panel3, 0, 1, 1, 1);
		panel.add(panel3);

		// 패널4 - snails
		panel4 = new JPanel(gb);
		panel4.setOpaque(false);
		snailPanel1 = new JPanel();
		snailPanel1_1 = new JPanel();
		snailPanel2 = new JPanel();
		snailPanel2_1 = new JPanel();
		snailPanel3 = new JPanel();
		snailPanel3_1 = new JPanel();
		snailPanel4 = new JPanel();
		snailPanel4_1 = new JPanel();
		snailPanel1.setOpaque(false);
		snailPanel1_1.setOpaque(false);
		snailPanel2.setOpaque(false);
		snailPanel2_1.setOpaque(false);
		snailPanel3.setOpaque(false);
		snailPanel3_1.setOpaque(false);
		snailPanel4.setOpaque(false);
		snailPanel4_1.setOpaque(false);
		snailLabel1 = new JLabel(snailImg1);
		snailLabel2 = new JLabel(snailImg2);
		snailLabel3 = new JLabel(snailImg3);
		snailLabel4 = new JLabel(snailImg4);
		snailTF1 = new JTextField(5);
		snailTF2 = new JTextField(5);
		snailTF3 = new JTextField(5);
		snailTF4 = new JTextField(5);
		snailTF1.setBorder(null);	snailTF2.setBorder(null);	snailTF3.setBorder(null);	snailTF4.setBorder(null);
		snailTF1.setForeground(Color.BLUE);	snailTF2.setForeground(Color.BLUE);	snailTF3.setForeground(Color.BLUE);	snailTF4.setForeground(Color.BLUE);
		snailTF1.setFont(new Font("a전자시계", Font.BOLD, 15));	snailTF2.setFont(new Font("a전자시계", Font.BOLD, 15));
		snailTF3.setFont(new Font("a전자시계", Font.BOLD, 15));	snailTF4.setFont(new Font("a전자시계", Font.BOLD, 15));
		snailTF1.setHorizontalAlignment(JTextField.CENTER);	snailTF2.setHorizontalAlignment(JTextField.CENTER);
		snailTF3.setHorizontalAlignment(JTextField.CENTER);	snailTF4.setHorizontalAlignment(JTextField.CENTER);
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		make(snailPanel1, 0, 0, 1, 1);
		make(snailPanel2, 1, 0, 1, 1);
		make(snailPanel3, 2, 0, 1, 1);
		make(snailPanel4, 3, 0, 1, 1);
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		make(snailPanel1_1, 0, 1, 1, 1);
		make(snailPanel2_1, 1, 1, 1, 1);
		make(snailPanel3_1, 2, 1, 1, 1);
		make(snailPanel4_1, 3, 1, 1, 1);
		snailPanel1.add(snailLabel1);
		snailPanel1_1.add(snailTF1);
		snailPanel1_1.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
		snailPanel2.add(snailLabel2);
		snailPanel2_1.add(snailTF2);
		snailPanel3.add(snailLabel3);
		snailPanel3_1.add(snailTF3);
		snailPanel4.add(snailLabel4);
		snailPanel4_1.add(snailTF4);
		panel4.add(snailPanel1);
		panel4.add(snailPanel1_1);
		panel4.add(snailPanel2);
		panel4.add(snailPanel2_1);
		panel4.add(snailPanel3);
		panel4.add(snailPanel3_1);
		panel4.add(snailPanel4);
		panel4.add(snailPanel4_1);
		gbc.weightx = 0.5;
		gbc.weighty = 0.8;
		make(panel4, 1, 1, 1, 2);
		panel.add(panel4);

		// 패널5 - 유저2
		gbc.weightx = 0.5;
		panel5 = new JPanel(new BorderLayout());
		panel5.setOpaque(false);
		userImgLabel2 = new JLabel(userImg2);
		userInfoLabel2 = new JLabel("유저2 정보");
		userInfoLabel2.setForeground(Color.WHITE);
		userInfoLabel2.setFont(new Font("a전자시계", Font.PLAIN, 15));
		userInfoLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		panel5.add(userImgLabel2, BorderLayout.CENTER);
		panel5.add(userInfoLabel2, BorderLayout.SOUTH);
		make(panel5, 2, 1, 1, 1);
		panel.add(panel5);

		// 패널6 - 유저3
		panel6 = new JPanel(new BorderLayout());
		panel6.setOpaque(false);
		userImgLabel3 = new JLabel(userImg3);
		userInfoLabel3 = new JLabel("유저3 정보");
		userInfoLabel3.setForeground(Color.WHITE);
		userInfoLabel3.setFont(new Font("a전자시계", Font.PLAIN, 15));
		userInfoLabel3.setHorizontalAlignment(SwingConstants.CENTER);
		panel6.add(userImgLabel3, BorderLayout.CENTER);
		panel6.add(userInfoLabel3, BorderLayout.SOUTH);
		make(panel6, 0, 2, 1, 1);
		panel.add(panel6);

		// 패널7 - 유저4
		panel7 = new JPanel(new BorderLayout());
		panel7.setOpaque(false);
		userImgLabel4 = new JLabel(userImg4);
		userInfoLabel4 = new JLabel("유저4 정보");
		userInfoLabel4.setForeground(Color.WHITE);
		userInfoLabel4.setFont(new Font("a전자시계", Font.PLAIN, 15));
		userInfoLabel4.setHorizontalAlignment(SwingConstants.CENTER);
		panel7.add(userImgLabel4, BorderLayout.CENTER);
		panel7.add(userInfoLabel4, BorderLayout.SOUTH);
		make(panel7, 2, 2, 1, 1);
		panel.add(panel7);

		// 패널8 - 유저5
		gbc.weighty = 0.6;
		panel8 = new JPanel(new BorderLayout());
		panel8.setOpaque(false);
		panel8.setBorder(BorderFactory.createEmptyBorder(0, 0, 70, 0));
		userImgLabel5 = new JLabel(userImg5);
		userInfoLabel5 = new JLabel("유저5 정보");
		userInfoLabel5.setForeground(Color.WHITE);
		userInfoLabel5.setFont(new Font("a전자시계", Font.PLAIN, 15));
		userInfoLabel5.setHorizontalAlignment(SwingConstants.CENTER);
		panel8.add(userImgLabel5, BorderLayout.CENTER);
		panel8.add(userInfoLabel5, BorderLayout.SOUTH);
		make(panel8, 0, 3, 1, 2);
		panel.add(panel8);

		// 패널9 - 채팅창
		panel9 = new JPanel(new BorderLayout());
		panel9.setOpaque(false);
		textArea = new JTextArea(10, 15);
		textArea.setLineWrap(true); // 자동 줄바꿈 기능(창 끝으로 가면 밑으로)
		scrollPane = new JScrollPane(textArea);
		panel9.add(scrollPane, BorderLayout.CENTER);
		msgPanel = new JPanel(new BorderLayout());
		msgTF = new JTextField(15);
		msgBtn = new JButton("전송");
		msgPanel.add(msgTF, BorderLayout.CENTER);
		msgPanel.add(msgBtn, BorderLayout.EAST);
		panel9.add(msgPanel, BorderLayout.SOUTH);
		make(panel9, 1, 3, 1, 2);
		panel.add(panel9);

		// 패널10 - 승률 조작완료 버튼
		/*
		 * panel10 = new JPanel(); bettingBtn = new JButton("승률 조작완료");
		 * panel10.add(bettingBtn); make(panel10, 2, 3, 1, 1); panel.add(panel10);
		 */
		panel10 = new JPanel();
		panel10.setOpaque(false);
		gbc.fill = GridBagConstraints.NONE;
		distBtn = new JButton(dist_hImg);
		bettingBtn = new JButton(bet_hImg);
		distBtn.setPreferredSize(new Dimension(100, 40));
		distBtn.setBorderPainted(false);
		distBtn.setFocusPainted(false);
		distBtn.setContentAreaFilled(false);
		bettingBtn.setPreferredSize(new Dimension(100, 40));
		bettingBtn.setBorderPainted(false);
		bettingBtn.setFocusPainted(false);
		bettingBtn.setContentAreaFilled(false);
		panel10.add(distBtn);
		panel10.add(bettingBtn);
		make(panel10, 2, 3, 1, 1);
		panel.add(panel10);

		// 패널11 - ready, exit버튼
		panel11 = new JPanel();
		panel11.setOpaque(false);
		// gbc.fill = GridBagConstraints.NONE;
		readyBtn = new JButton(ready_hImg);
		exitBtn = new JButton(exit_hImg);
		readyBtn.setPreferredSize(new Dimension(100, 40));
		readyBtn.setBorderPainted(false);
		readyBtn.setFocusPainted(false);
		readyBtn.setContentAreaFilled(false);
		exitBtn.setPreferredSize(new Dimension(100, 40));
		exitBtn.setBorderPainted(false);
		exitBtn.setFocusPainted(false);
		exitBtn.setContentAreaFilled(false);
		panel11.add(readyBtn);
		panel11.add(exitBtn);
		make(panel11, 2, 4, 1, 1);
		panel.add(panel11);

		mainBackGP = new JPanel(new BorderLayout()) {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(mainBgImg, 0, 0, null);
			}
		};
		mainBackGP.add(panel);
		mainBackGP.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
		cp.add(mainBackGP);
	}
	
	void setUI() {
		setTitle("ClientMainUI");
		setSize(1200, 800);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setVisible(true);
		
		textArea.setEnabled(false);
		readyBtn.setEnabled(false);
		
		hideRateManipulation();  // 처음에는 달팽이 승률 조작하는 부분을 안보이게 한다.
	}
	
	void hideRateManipulation() { // 처음에는 달팽이 승률 조작하는 부분을 안보이게 한다.
		snailTF1.setVisible(false);		snailTF1.setEnabled(false);
		snailTF2.setVisible(false);		snailTF2.setEnabled(false);
		snailTF3.setVisible(false);		snailTF3.setEnabled(false);
		snailTF4.setVisible(false);		snailTF4.setEnabled(false);
		distBtn.setVisible(false);      bettingBtn.setVisible(false);
	}
	void addEvent() {
		questionBtn.addActionListener(clientHandler);
		msgTF.addKeyListener(clientSender);
		msgBtn.addActionListener(clientSender);
		exitBtn.addActionListener(clientHandler);
		distBtn.addActionListener(clientHandler);	// 조작완료 버튼
		bettingBtn.addActionListener(clientHandler);	// 배팅하기 버튼
	}
}