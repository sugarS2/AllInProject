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
	
	public static final int MAX_CLIENT = 5;	// ���� ���� �� : 5��
	LinkedHashMap<String, DisDos> clientList = new LinkedHashMap<String, DisDos>();  // Ŭ���̾�Ʈ ���̵�, ���� ����
	
	LinkedHashMap<String, Integer> clientInfo = new LinkedHashMap<String, Integer>();	// Ŭ���̾�Ʈ ���̵�, ���ΰ���
	LinkedHashMap<String, String> clientCharactor = new LinkedHashMap<String, String>(); // Ŭ���̾�Ʈ ���̵�, ĳ����
	LinkedHashMap<String, Integer> snailsRate = new LinkedHashMap<String, Integer>();	// �����ڰ� ���� �� ������ �·��� �����ϴ� ���� (�����̸�, �·�)
	
	Vector<ClientInfo> clientInfoVector = new Vector<ClientInfo>();	// Ŭ���̾�Ʈ ���� ��� ����	
	Vector<String> winner = new Vector<String>();	// ���� �����
	
	ServerUI(){
		cp = getContentPane();
	}
	
	//Server �����ϱ�
	void setServer() {
		try {
			serverSocket = new ServerSocket(8282);
			textArea.setText(textArea.getText() + "Ŭ���̾�Ʈ ��ٸ��� ��..\n");
			while(true) {
				socket = serverSocket.accept();
				if((clientList.size() + 1) > MAX_CLIENT) {	// ������ �ʰ��� ���
					socket.close();
				}else {
					ocm = new OneClientModule(this);
					ocm.start();
				}
			}
		}catch(IOException io) {
			JOptionPane.showMessageDialog(null, "������ �̹� �����ֽ��ϴ�.\n�����մϴ�.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	
	void init() {
		loadImages();
		setComponent();
		setUI();
		setServer();
	}
	
	//�ʿ��� �̹��� 
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
		topPanel.setBorder(new EmptyBorder(5,5,5,5));	//��,��,�Ʒ�,������
		titleLabel = new JLabel(titleImg);
		outBtn = new JButton(outImg);
		outBtn.setBorderPainted(false);	//��ư �׵θ� ����
		outBtn.setContentAreaFilled(false); //��ư ������� ǥ�� ����
		//outBtn.setFocusPainted(false);	//��Ŀ�� ǥ�� ���� 
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
		//((JComponent)options.getComponent(0)).setBorder(new EmptyBorder(0,0,0,0));	//��ư �׵θ� ����
		//((AbstractButton)options.getComponent(0)).setBorderPainted(false);
		msgField = new JTextField();
		msgBtn = new JButton(sendImg);
		
		msgBtn.setContentAreaFilled(false); //��ư ������� ǥ�� ����
		msgBtn.setBorderPainted(false);	//��ư �׵θ� ����
		
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
