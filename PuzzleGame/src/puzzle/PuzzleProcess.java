package puzzle;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//��ư�� �̿��� �׸�����
//���� : �ٲٰ� ���� ���� ���� �ΰ��� ���޾� Ŭ���ϸ�, �ΰ��� ������ �¹ٲ��
//   ��, ���� ���������� �Ǽ��� �ѹ� �߸� ������ ��, �߸� ���� ���������� �ѹ� �� Ŭ���ϸ� �ٲ��� �ʴ´�

public class PuzzleProcess extends JFrame implements ActionListener {

	JButton[] btn;	
	JPanel left, right, back;
	int count = 0, game[], N, level=0 , autoFlag=0;
	int oriWidth, oriHeight, width, height,clickCount, oldNum, curNum;
	Image original;
	long start, end;
	int time;

	BufferedImage img[];
	
	public PuzzleProcess(int N, int level, int autoFlag) {
		
		this.N = N;
		this.level = level;		
		game = new int[N * N]; // ���ӹ迭		
		btn = new JButton[N * N]; // ��ư�� �����
		this.autoFlag = autoFlag;
			

		loadImage(); //�̹��� �б�			
	
		divisionImage(); //�̹��� ����
		
		initPannel(); //�г� �ʱ�ȭ		

		createButton(); //��ư�迭 ����.
		
		shakeImage(); //�迭 ����.
		
//		start = System.currentTimeMillis();
		
		if(autoFlag == 1){			
			//�ڵ����� �������
			System.out.println("�ڵ�Ǯ���Դϴ�.��");
			autoPlay();
		}
		
		
		setSize(oriWidth * 2 + 5, right.getHeight() + 30);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);

	}
	/*�̹��� �ҷ�����*/
	private void loadImage(){
		try {
			int r = (int) (Math.random() * 4) + 1;
			
			original = ImageIO.read(new File(r + ".jpg"));
			oriWidth = original.getWidth(this);
			oriHeight = original.getHeight(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
		width = original.getWidth(this) / N;
		height = original.getHeight(this) / N;
	}
	
	/*�̹��� ����*/
	private void divisionImage() {
		img = new BufferedImage[N * N];
		int cnt = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				img[cnt] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = img[cnt].getGraphics();
				// �����̹������� �ʿ��� �κи� �߶� �׸���
				g.drawImage(original, 0, 0, width, height, j * width, i * height, (j + 1) * width, (i + 1) * height,
						this);
				cnt++;
			}
		}
	}
	
	/*�г� �ʱ�ȭ*/
	private void initPannel(){
		back = new JPanel(); //left ,right ���� �г�
		right = new JPanel(); //right �г� : ��ư �迭 ���� �г� 
		left = new JPanel() { // /left : ���� �̹��� ���� �г�

			@Override
			public void paint(Graphics g) { // �г��� paint()�Լ����� �׷���
				g.drawImage(original, 0, 0, this);

			}
		};
		
		right.setLayout(new GridLayout(N, N));
		back.setLayout(new GridLayout(1, 2));
		
		left.setSize(oriWidth, oriHeight);
		right.setSize(oriWidth, oriHeight);
	}
	
	/*��ư ���� �� �гο� ����*/
	private void createButton(){
		for (int i = 0; i < N * N; i++) {
			btn[i] = new JButton();
			//if(autoFlag == 0) 
				btn[i].addActionListener(this);			
			//right.add(btn[i]);	
			addPanel(right,btn[i]);
		}

		
		//back.add(left);
		
		//back.add(right);
		addPanel(back, left);
		addPanel(back, right);
		add(back);		
		
	}
	
	/*�гο� ������Ʈ ����*/
	private void addPanel( JPanel panel, JComponent jcom){
		panel.add(jcom);
	}
	
	/* ���� ���� ��ư�� �̹��� ������ */
	private void shakeImage() {
		//�ð� ���� ����
		start = System.currentTimeMillis();
		
		//T = ����� �����ϴ� ��
		int T,d,M = N * N;
		//���̵� ������ ���� ����
		//Level1(�ʱ�) = 3���, Level2(�߱�) = 2���, Level3(���) = 1���
		T = 4 - level;
		d = M / T; //d�� ��� �б���.
		do {
			for (int i = 0; i < N * N; i++)
				game[i] = 0;

			// �迭�� �ߺ������� �ֱ�
			for (int i = 0; i < N * N; i++) {
				int temp = 0;
				do {
					if (i < d) {
						
						temp = randomRange(0,d-1);
					} else if (d <= i && i < d * 2) {
						
						temp = randomRange(d,d*2-1);
					} else {
						
						temp = randomRange(d*2,M-1);
					}
					
				} while (game[temp] != 0);
				game[temp] = i;
			}
			
		} while (endGame()); // ������ �ʾ����� �ٽ� ����	
		//System.out.println("����");
		buttonToImage(); //��ư�� �̹��� ������.
	}
	
	/*��ư�� �̹��� ������*/
	private void buttonToImage(){
		
		for (int i = 0; i < N * N; i++) {
			btn[i].setBorderPainted(false); // �׵θ� ���ֱ�

			btn[i].setContentAreaFilled(false); // ��ư�� ���� ���ֱ�

			btn[i].setIcon(new ImageIcon(img[game[i]])); //�� ��ư�� �̹��� ����.
			btn[i].setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); //��ư ���� �е� ����.
		}
	}
	
	
	/*�ּڰ�, �ִ� ���� ���� �� ���� ���� .*/
	private int randomRange(int n1, int n2) {
	    return (int) (Math.random() * (n2 - n1 + 1)) + n1;
	  }	
	
	
	/* �ڵ�Ǯ�� */
	private void autoPlay() {

		new Thread(){
			int find = 0; // ã�¼�
			int tmp;
		public void run(){
			try{
				
				do {
					for (int i = 0; i < game.length; i++) {
						if (game[i] == find) {
							tmp = game[find];
							game[find] = find++;
							game[i] = tmp;
							break;
						}				
					}
					sleep(500);
					System.out.println(Arrays.toString(game));
					buttonToImage();
					repaint();

				} while (!endGame());
			}catch(Exception e){
				System.out.println("����");
			}
			complete();
		}
	}.start();		
	}
	
	


	
	
	/* ���� ���Ḧ Ȯ�� �ϴ� �޼ҵ�*/
	private boolean endGame() {
		boolean endGame = true;

		for (int i = 0; i < game.length; i++) {
			if (i != game[i]) {
				endGame = false;
			}
		}

		return endGame;
	}
	

	public void actionPerformed(ActionEvent e) {

		JButton curBtn = (JButton) e.getSource();

		for (int i = 0; i < btn.length; i++) {
			if (curBtn.getIcon().equals(btn[i].getIcon())) {
				// System.out.println(i + ":" + game[i]);
				curNum = i;
				break;
			}
		}

		if (clickCount == 0) { // ù��° Ŭ���̸�

			clickCount++;
			oldNum = curNum;

		} else {

			// �ι�° Ŭ���̸�
			if (oldNum != curNum) { // ���� �Ͱ� ���� �ʴٸ�

				// �̹��� ����
				btn[oldNum].setIcon(new ImageIcon(img[game[curNum]]));
				btn[curNum].setIcon(new ImageIcon(img[game[oldNum]]));

				// �迭�� ����
				int t = game[oldNum];
				game[oldNum] = game[curNum];
				game[curNum] = t;

				complete();
				
			}
			clickCount = 0;
		}
	}
	
	private void complete()
	{
		// ���⼭ ���� ����Ȯ��
		if (endGame()) {
			
			//�ð� ���
			end = System.currentTimeMillis();
			time = (int)((end - start)/1000);
			JOptionPane.showMessageDialog(this, "Success!!");

			// ���� ������� Ȯ��
			int reStart = JOptionPane.showConfirmDialog(this, "����� �Ͻðڽ��ϱ�?(N:�������)  /   " + "���� �ϼ� �ð�  " + time + " ��", "exit?", JOptionPane.YES_NO_OPTION);
			if (reStart == JOptionPane.YES_OPTION) {
				
				shakeImage(); // ����
				
				if(autoFlag == 1) autoPlay(); 
				
				// �迭�� �ٽ� ���� �ٽ� �׸��⸦ �Ѵ�.
				
				repaint(); // �ٽ� �׸���

			} else {
				//System.exit(0);
				new PuzzleRanking("", time,1);
				dispose();
			}
		}
	}


}