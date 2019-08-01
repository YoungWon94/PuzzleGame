package puzzle;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PuzzleRanking extends JFrame implements ActionListener{


	private JPanel head,body,bottom,back;
	private JLabel title, rank, name, time;
	private JTextField[] nameAry;
	private JLabel[] rankAry, timeAry;
	private JButton submit;
	private String[] rankerName = new String[5];
	private int[] rankerTime = new int[5];
	private String userName;
	private int userTime;
	private int index;
	 
	
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	private String driver = "net.ucanaccess.jdbc.UcanaccessDriver";
	private String url = "jdbc:ucanaccess://D:\\CHOI\\eclipse_EE\\bit-academy\\PuzzleGame\\RankingDB.accdb";
	
	public PuzzleRanking(String userName, int userTime, int watching){
		
		this.userName = userName;
		this.userTime = userTime;
		
		head = new JPanel();
		body = new JPanel(new GridLayout(6,3));
		bottom = new JPanel();
		back = new JPanel(new BorderLayout());
		
		String grade = PuzzleInput.level == 1 ? ("�ʱ�") : (PuzzleInput.level == 2 ? "�߱�" : "���");
		
		title = new JLabel("���� ��ŷ"+"("+grade+")");
		rank = new JLabel("����");
		name = new JLabel("�̸�");
		time = new JLabel("�ð�(��)");
			
		rankAry = new JLabel[5];
		nameAry = new JTextField[5];
		timeAry = new JLabel[5];

		
		submit = new JButton("Ȯ��");
		submit.addActionListener(this);
						
		selectAll();
		
		head.add(title);
		bottom.add(submit);
		
		body.add(rank);
		body.add(name);
		body.add(time);
		for(int i=0; i<5; i++){
			body.add(rankAry[i]);
			body.add(nameAry[i]);
			body.add(timeAry[i]);
		}
		
		back.add(head, BorderLayout.NORTH);
		back.add(body, BorderLayout.CENTER);
		back.add(bottom, BorderLayout.SOUTH);
		
		add(back);
		
		if(watching != 0) newRanker();
		//updateAll();
		//selectAll();
		
		pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		
		
	}
	
	public void selectAll(){
		
		String sql=""; // SQL ��
		int index=0;
		
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url);
			
			sql = "Select * From level"+PuzzleInput.level;
			
			pstmt = con.prepareStatement(sql);
			
			System.out.println("DB ���� ����");
			rs = pstmt.executeQuery();			
			
			while(rs.next()){
				rankAry[index] = new JLabel("   "+rs.getString("����"));
				nameAry[index] = new JTextField(rs.getString(2));
				rankerName[index] = rs.getString(2);
				nameAry[index].setEnabled(false);
				timeAry[index] = new JLabel("   "+rs.getString(3));
				timeAry[index].setHorizontalAlignment(JLabel.CENTER);
				rankerTime[index++] = Integer.parseInt(rs.getString(3));
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs = null;
			}
			if(pstmt != null){
				pstmt = null;
			}
			if(con != null){
				con = null;
			}
		}
	}
	
	public void updateAll(){
		
		String sql="";
		
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url);
			
			for(int i=0; i<5; i++){
				sql = "UPDATE level"+PuzzleInput.level+" SET �̸�=?,�ð�=? WHERE ����=?";
				pstmt = con.prepareStatement(sql);				
				pstmt.setString(1, rankerName[i]);
				pstmt.setString(2, rankerTime[i]+"");
				pstmt.setString(3, (i+1)+"");
				int result = pstmt.executeUpdate();
				if(result < 0 ) throw new Exception();
			}			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			if(pstmt != null){
				pstmt = null;
			}
			if(con != null){
				con = null;
			}
		}
		
	}
	
	
	public void newRanker(){
		int oldTime;
		String oldName;
		
		for(int i=0; i<rankerTime.length; i++){
			if(userTime < rankerTime[i] ){
				oldTime = rankerTime[i];
				oldName = rankerName[i];
				rankerTime[i] = userTime;
				rankerName[i] = userName;
				userTime = oldTime;
				userName = oldName;
			}
		}
		
		System.out.println(Arrays.toString(rankerTime));
		System.out.println(Arrays.toString(rankerName));
		
		for(int i=0; i<5; i++){
			nameAry[i].setText(rankerName[i]);
			timeAry[i].setText(rankerTime[i]+"");
			if(nameAry[i].getText().equals("")){
				nameAry[i].setEnabled(true);
				index = i;
			}
		}
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == submit){
			
			
			if(nameAry[index].getText().equals("")){
				JOptionPane.showMessageDialog(null, "�̸��� �Է��ϼ���!", "���", JOptionPane.WARNING_MESSAGE);
			}else{
				String name = nameAry[index].getText();
				rankerName[index] = name;
				nameAry[index].setEnabled(false);
				updateAll();
				new PuzzleInput();
				dispose();
				
			}
					
					
		}
	}
	
}
