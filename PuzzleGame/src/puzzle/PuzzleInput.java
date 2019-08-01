package puzzle;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

// N 입력 클래스
public class PuzzleInput extends JFrame {

	private JPanel left, right, bottom, back;
	private JLabel l1;
	private JTextField t1;
	private JButton b1, b2, b3;
	private JRadioButton r1, r2, r3;
	public static int level;
	// 리스너 객체 생성
	MyListener listener = new MyListener();

	public PuzzleInput() {
		setTitle("난이도 선택");

		left = new JPanel();
		right = new JPanel();
		back = new JPanel();
		bottom = new JPanel();

		l1 = new JLabel("N 입력값(2~9) : ");
		t1 = new JTextField(5);
		b1 = new JButton("직접풀기");
		b2 = new JButton("자동풀기");
		b3 = new JButton("기록보기");
		r1 = new JRadioButton("초급 난이도");
		r1.setSelected(true);
		level = 1;
		r2 = new JRadioButton("중급 난이도");
		r3 = new JRadioButton("고급 난이도");
		

		left.setLayout(new BorderLayout());
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		back.setLayout(new BorderLayout());
		// 리스너 객체 등록
		b1.addActionListener(listener);
		b2.addActionListener(listener);
		b3.addActionListener(listener);
		;

		left.add(l1, BorderLayout.WEST);
		left.add(t1, BorderLayout.CENTER);
		bottom.add(b1);
		bottom.add(b2);
		bottom.add(b3);

		// 난이도 라디오버튼 그룹으로 묶기
		ButtonGroup r_group = new ButtonGroup();
		r_group.add(r1);
		r_group.add(r2);
		r_group.add(r3);

		right.add(r1);
		right.add(r2);
		right.add(r3);

		back.add(left, BorderLayout.WEST);
		back.add(right, BorderLayout.EAST);
		back.add(bottom, BorderLayout.SOUTH);
		add(back);

		;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		pack();
	}

	private class MyListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			level = (r1.isSelected() ? 1 : (r2.isSelected() ? 2 : 3));
			if (e.getSource() == b1 || e.getSource() == b2) {

				try {
					int value = Integer.parseInt(t1.getText());
					if (!(1 < value && value < 10)){
						JOptionPane.showMessageDialog(null, "2부터 9까지의 숫자를 입력하세요!", "경고", JOptionPane.WARNING_MESSAGE);
					}else{
					int autoFlag = (e.getSource() == b1 ? 0 : 1);
					new PuzzleProcess(value, level, autoFlag);

					dispose();
					}
				} catch (NumberFormatException ne) {
					JOptionPane.showMessageDialog(null, "숫자만 입력하세요!", "경고", JOptionPane.WARNING_MESSAGE);
				}

			} else if (e.getSource() == b3) {
				new PuzzleRanking("", 9999, 0);
				dispose();
			}

		}
	}

}
