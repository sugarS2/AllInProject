package project.allin;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

class RuleWindow extends JFrame implements MouseListener {
   JPanel rulePanel;
   RuleWindow(ClientHandler clientHandler) {
      Container ct = getContentPane();
      
      JLabel ruleLabel = new JLabel(clientHandler.clientMainUI.ruleImg);
      ruleLabel.setHorizontalAlignment(SwingConstants.CENTER);
      
      JPanel rulePanel = new JPanel(new BorderLayout());
      rulePanel.add(ruleLabel);
      //rulePanel.setBackground(Color.WHITE);
      rulePanel.setBackground(new Color(255, 248, 237));
      ct.addMouseListener(this);
      
      ct.add(rulePanel);

      setTitle("Game Rule");
      setSize(720, 810);
      setUndecorated(true);
      setResizable(true);
      setLocationRelativeTo(null);
      setVisible(true);
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
       dispose();
   }
    @Override
    public void mousePressed(MouseEvent e) {
    }
    @Override
    public void mouseReleased(MouseEvent e) {
    }
}