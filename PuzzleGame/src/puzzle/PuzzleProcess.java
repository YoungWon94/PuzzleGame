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

//버튼을 이용한 그림퍼즐
//사용법 : 바꾸고 싶은 퍼즐 조각 두개를 연달아 클릭하면, 두개의 퍼즐이 맞바뀐다
//   단, 같은 퍼즐조각을 실수로 한번 잘못 눌렀을 때, 잘못 누른 퍼즐조각을 한번 더 클릭하면 바뀌지 않는다

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
		game = new int[N * N]; // 게임배열		
		btn = new JButton[N * N]; // 버튼을 만들기
		this.autoFlag = autoFlag;
			

		loadImage(); //이미지 읽기			
	
		divisionImage(); //이미지 분할
		
		initPannel(); //패널 초기화		

		createButton(); //버튼배열 생성.
		
		shakeImage(); //배열 섞기.
		
//		start = System.currentTimeMillis();
		
		if(autoFlag == 1){			
			//자동으로 퍼즐맞춤
			System.out.println("자동풀기입니다.ㅣ");
			autoPlay();
		}
		
		
		setSize(oriWidth * 2 + 5, right.getHeight() + 30);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);

	}
	/*이미지 불러오기*/
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
	
	/*이미지 분할*/
	private void divisionImage() {
		img = new BufferedImage[N * N];
		int cnt = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				img[cnt] = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = img[cnt].getGraphics();
				// 원본이미지에서 필요한 부분만 잘라서 그리기
				g.drawImage(original, 0, 0, width, height, j * width, i * height, (j + 1) * width, (i + 1) * height,
						this);
				cnt++;
			}
		}
	}
	
	/*패널 초기화*/
	private void initPannel(){
		back = new JPanel(); //left ,right 붙일 패널
		right = new JPanel(); //right 패널 : 버튼 배열 붙일 패널 
		left = new JPanel() { // /left : 원본 이미지 붙일 패널

			@Override
			public void paint(Graphics g) { // 패널의 paint()함수에서 그려줌
				g.drawImage(original, 0, 0, this);

			}
		};
		
		right.setLayout(new GridLayout(N, N));
		back.setLayout(new GridLayout(1, 2));
		
		left.setSize(oriWidth, oriHeight);
		right.setSize(oriWidth, oriHeight);
	}
	
	/*버튼 생성 후 패널에 붙임*/
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
	
	/*패널에 컴포넌트 붙임*/
	private void addPanel( JPanel panel, JComponent jcom){
		panel.add(jcom);
	}
	
	/* 숫자 섞고 버튼에 이미지 입히기 */
	private void shakeImage() {
		//시간 측정 시작
		start = System.currentTimeMillis();
		
		//T = 등분을 결정하는 수
		int T,d,M = N * N;
		//난이도 조절을 위한 수식
		//Level1(초급) = 3등분, Level2(중급) = 2등분, Level3(고급) = 1등분
		T = 4 - level;
		d = M / T; //d는 등분 분기점.
		do {
			for (int i = 0; i < N * N; i++)
				game[i] = 0;

			// 배열의 중복값없이 넣기
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
			
		} while (endGame()); // 섞이지 않았을떄 다시 섞기	
		//System.out.println("섞임");
		buttonToImage(); //버튼에 이미지 입히기.
	}
	
	/*버튼에 이미지 입히기*/
	private void buttonToImage(){
		
		for (int i = 0; i < N * N; i++) {
			btn[i].setBorderPainted(false); // 테두리 없애기

			btn[i].setContentAreaFilled(false); // 버튼의 바탕 없애기

			btn[i].setIcon(new ImageIcon(img[game[i]])); //각 버튼에 이미지 입힘.
			btn[i].setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); //버튼 내부 패딩 제거.
		}
	}
	
	
	/*최솟값, 최댓값 포함 범위 내 난수 생성 .*/
	private int randomRange(int n1, int n2) {
	    return (int) (Math.random() * (n2 - n1 + 1)) + n1;
	  }	
	
	
	/* 자동풀기 */
	private void autoPlay() {

		new Thread(){
			int find = 0; // 찾는수
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
				System.out.println("예외");
			}
			complete();
		}
	}.start();		
	}
	
	


	
	
	/* 게임 종료를 확인 하는 메소드*/
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

		if (clickCount == 0) { // 첫번째 클릭이면

			clickCount++;
			oldNum = curNum;

		} else {

			// 두번째 클릭이면
			if (oldNum != curNum) { // 이전 것과 같지 않다면

				// 이미지 변경
				btn[oldNum].setIcon(new ImageIcon(img[game[curNum]]));
				btn[curNum].setIcon(new ImageIcon(img[game[oldNum]]));

				// 배열값 변경
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
		// 여기서 게임 종료확인
		if (endGame()) {
			
			//시간 계산
			end = System.currentTimeMillis();
			time = (int)((end - start)/1000);
			JOptionPane.showMessageDialog(this, "Success!!");

			// 게임 재시작을 확인
			int reStart = JOptionPane.showConfirmDialog(this, "재시작 하시겠습니까?(N:기록측정)  /   " + "퍼즐 완성 시간  " + time + " 초", "exit?", JOptionPane.YES_NO_OPTION);
			if (reStart == JOptionPane.YES_OPTION) {
				
				shakeImage(); // 섞기
				
				if(autoFlag == 1) autoPlay(); 
				
				// 배열을 다시 섞고 다시 그리기를 한다.
				
				repaint(); // 다시 그리기

			} else {
				//System.exit(0);
				new PuzzleRanking("", time,1);
				dispose();
			}
		}
	}


}