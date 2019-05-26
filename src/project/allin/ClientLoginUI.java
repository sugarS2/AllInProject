package project.allin;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;


public class ClientLoginUI extends JFrame implements ActionListener, MouseListener, KeyListener {
	JLabel ipL, idL, charL1, charL2, charL3, charL4, charL5, charL6, charL7, charL8, charL9;
	JTextField ipTF, idTF;
	JPanel cP, cNP, cSP, cCP, charP, backGP;
	Container cp;
	JButton joinB, exitB, rightB, leftB;
	Image loginBgImg;
	ImageIcon char1, char2, char3, char4,char5, char6, char7, char8, char9, 
			right1, right2, left1, left2, join1, join2, exit1, exit2;
	String ip, id, pickC;
	ClientMainUI clientMainUI;
	int x = 0;
	
	ClientLoginUI(){
		loadImg();
	}
	
	void init() {
		// ip, id 부분
		ipL = new JLabel("IP Address");
		ipL.setHorizontalAlignment(SwingConstants.CENTER);
		idL = new JLabel("ID (닉네임)");
		idL.setHorizontalAlignment(SwingConstants.CENTER);
		ipTF = new JTextField("127.0.0.1", 10);
		idTF = new JTextField(5);
		ipTF.setBorder(new EmptyBorder(0,0,0,0));
		idTF.setBorder(new EmptyBorder(0,0,0,0));
		
		cNP = new JPanel(new GridLayout(2,2,10,10));
		cNP.setOpaque(false);
		cNP.add(ipL);
		cNP.add(ipTF);
		cNP.add(idL);
		cNP.add(idTF);
		cNP.setBorder(BorderFactory.createEmptyBorder(40, 100, 10, 100)); // top, left, bottom, right 내부 공백 띄우기
		// 입장, 종료 부분
		joinB = new JButton(join1);
	    joinB.setRolloverIcon(join2);
		joinB.addActionListener(this);
		joinB.addMouseListener(this);
		joinB.setBorderPainted(false);
		joinB.setPreferredSize(new Dimension(70,30));
		//join.setRolloverEnabled(true);
		exitB = new JButton(exit1);
	    exitB.setRolloverIcon(exit2);
		exitB.addActionListener(this);
		exitB.addMouseListener(this);
		exitB.setBorderPainted(false);	
		exitB.setPreferredSize(new Dimension(70,30));
		//cS = new JPanel(new GridLayout(2,1));
		cSP = new JPanel();
		cSP.setOpaque(false);
		cSP.add(joinB);
		cSP.add(exitB);
		cSP.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // top, left, bottom, right
		
		// 캐릭터 선택 부분
		rightB = new JButton(right1);
		rightB.addActionListener(this);
		rightB.addMouseListener(this);
		rightB.setBorderPainted(false);
		rightB.setContentAreaFilled(false);
		rightB.setPreferredSize(new Dimension(60,100)); // 크기조절

		
		leftB = new JButton(left1);
		leftB.addActionListener(this);
		leftB.addMouseListener(this);
		leftB.setBorderPainted(false);
		leftB.setContentAreaFilled(false);
		leftB.setPreferredSize(new Dimension(60,100));
		
		charL1 = new JLabel(char1);
		charL1.setName("캐릭1");
		charL2 = new JLabel(char2);
		charL2.setName("캐릭2");
		charL3 = new JLabel(char3);
		charL3.setName("캐릭3");
		charL4 = new JLabel(char4);
		charL4.setName("캐릭4");
		charL5 = new JLabel(char5);
		charL5.setName("캐릭5");
		charL6 = new JLabel(char6);
		charL6.setName("캐릭6");
		charL7 = new JLabel(char7);
		charL7.setName("캐릭7");
		charL8 = new JLabel(char8);
		charL8.setName("캐릭8");
		charL9 = new JLabel(char9);
		charL9.setName("캐릭9");
		
		charP = new JPanel();
		charP.setOpaque(false);
		charP.add(charL1); // 캐릭 초기값
		
		cCP = new JPanel();
		cCP.setOpaque(false);
		cCP.add(leftB);
		cCP.add(charP);
		cCP.add(rightB);
		cCP.setBorder(BorderFactory.createEmptyBorder(10, 20, 50, 20)); // top, left, bottom, right
		
		cP = new JPanel(new BorderLayout());
		cP.setOpaque(false);
		cP.add(cNP, BorderLayout.NORTH);
		cP.add(cCP, BorderLayout.CENTER);
		cP.add(cSP, BorderLayout.SOUTH);

		backGP = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(loginBgImg, 0, 0, null);
			}
		};
		backGP.add(cP);
		cp = getContentPane();
		cp.add(backGP);
		
		setUI();
	}
	
	void setUI(){
		setTitle("Client Login");
		setSize(400, 400);
		setUndecorated(true); // 타이틀바 없애기 setVisible 위에 넣어줘야한다.
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		idTF.requestFocus();
	}
	
	void loadImg() {
		try {
			loginBgImg = ImageIO.read(new File("imgs/bg/bg_01.png"));

			char1 = new ImageIcon(ImageIO.read(new File("imgs/character/char1.png")));
			char2 = new ImageIcon(ImageIO.read(new File("imgs/character/char2.png")));
			char3 = new ImageIcon(ImageIO.read(new File("imgs/character/char3.png")));
			char4 = new ImageIcon(ImageIO.read(new File("imgs/character/char4.png")));
			char5 = new ImageIcon(ImageIO.read(new File("imgs/character/char5.png")));
			char6 = new ImageIcon(ImageIO.read(new File("imgs/character/char6.png")));
			char7 = new ImageIcon(ImageIO.read(new File("imgs/character/char7.png")));
			char8 = new ImageIcon(ImageIO.read(new File("imgs/character/char8.png")));
			char9 = new ImageIcon(ImageIO.read(new File("imgs/character/char9.png")));

			right1 = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/next_G.png")));
			right2 = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/next_B.png")));
			left1 = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/pre_G.png")));
			left2 = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/pre_B.png")));

			join2 = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/enter_hover.png")));
			join1 = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/enter.png")));
			exit2 = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/exit_hover.png")));
			exit1 = new ImageIcon(ImageIO.read(new File("imgs/clientBtn/exit.png")));

		} catch (IOException ie) {
		}
	}
	
	
	// ActionListener
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		// 캐릭터 선택
		if (obj == rightB) {
			if (charL1.getName() == "캐릭1") {
				charL1.setIcon(char2);
				charL1.setName("캐릭2");
			} else if (charL1.getName() == "캐릭2") {
				charL1.setIcon(char3);
				charL1.setName("캐릭3");
			} else if (charL1.getName() == "캐릭3") {
				charL1.setIcon(char4);
				charL1.setName("캐릭4");
			} else if (charL1.getName() == "캐릭4") {
				charL1.setIcon(char5);
				charL1.setName("캐릭5");
			} else if (charL1.getName() == "캐릭5") {
				charL1.setIcon(char6);
				charL1.setName("캐릭6");
			} else if (charL1.getName() == "캐릭6") {
				charL1.setIcon(char7);
				charL1.setName("캐릭7");
			} else if (charL1.getName() == "캐릭7") {
				charL1.setIcon(char8);
				charL1.setName("캐릭8");
			} else if (charL1.getName() == "캐릭8") {
				charL1.setIcon(char9);
				charL1.setName("캐릭9");
			} else if (charL1.getName() == "캐릭9") {
				charL1.setIcon(char1);
				charL1.setName("캐릭1");
			}
		}
		if (obj == leftB) {
			if (charL1.getName() == "캐릭2") {
				charL1.setIcon(char1);
				charL1.setName("캐릭1");
			} else if (charL1.getName() == "캐릭3") {
				charL1.setIcon(char2);
				charL1.setName("캐릭2");
			} else if (charL1.getName() == "캐릭4") {
				charL1.setIcon(char3);
				charL1.setName("캐릭3");
			} else if (charL1.getName() == "캐릭5") {
				charL1.setIcon(char4);
				charL1.setName("캐릭4");
			} else if (charL1.getName() == "캐릭6") {
				charL1.setIcon(char5);
				charL1.setName("캐릭5");
			} else if (charL1.getName() == "캐릭7") {
				charL1.setIcon(char6);
				charL1.setName("캐릭6");
			} else if (charL1.getName() == "캐릭8") {
				charL1.setIcon(char7);
				charL1.setName("캐릭7");
			} else if (charL1.getName() == "캐릭9") {
				charL1.setIcon(char8);
				charL1.setName("캐릭8");
			} else if (charL1.getName() == "캐릭1") {
				charL1.setIcon(char9);
				charL1.setName("캐릭9");
			}
		}
		if (obj == joinB) {	// 입장, 종료 버튼 작동
			ip = ipTF.getText();
			id = idTF.getText();
			checklogin();
		} else if (obj == exitB) {
			System.exit(0);
		}
	}
	
	// MouseListener
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
    }
    @Override
	public void mouseClicked(MouseEvent e) {
	}
    @Override
    public void mousePressed(MouseEvent e) {
    	JButton mEvent = (JButton)e.getSource();
	    if(mEvent == rightB) {
	    	rightB.setIcon(right2);
	    	//repaint();
	    }else if (mEvent == leftB) {
	    	leftB.setIcon(left2);
	    }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    	JButton mEvent = (JButton)e.getSource();
    	if(mEvent == rightB) {
	    	rightB.setIcon(right1);
	    	//repaint();
	    }else if (mEvent == leftB) {
	    	leftB.setIcon(left1);
	    }
    }
    
    // KeyListener
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_ENTER) {
			idTF.requestFocus();
			}
		}
	public void keyReleased(KeyEvent e) {
	}
	public void keyTyped(KeyEvent e) {
	}
	
	void checklogin() {
		String id = idTF.getText();
		pickC = charL1.getName();
		if(id.length()==0) {
			JOptionPane.showMessageDialog(null, "아이디를 입력하세요.");
		}else { 
			clientMainUI = new ClientMainUI(this);
			dispose();	//원하는 Frame만 종료시킬 때는 dispose() 이용
		}
	}
	
	public static void main(String[] args) {
		new ClientLoginUI().init();
	}
}
