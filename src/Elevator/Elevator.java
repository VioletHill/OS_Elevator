package Elevator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Require.RequireButton;



class ElevatorInsideView extends JPanel
{
	Elevator elevator;
	JButton buttonFloor[];
	JButton openDoor=new JButton("◁▷");
	JButton closeDoor=new JButton("▷◁");
	ElevatorInsideView(Elevator ele)
	{
		setLayout(null);
		elevator=ele;
		buttonFloor=new JButton[ElevatorConst.totalFloor];
		for (int i=0,j=19; i<ElevatorConst.totalFloor; i++,j--)
		{
			buttonFloor[j]=new JButton(""+(ElevatorConst.totalFloor-i));
			buttonFloor[j].setMargin(new Insets(1,1,1,1));
			buttonFloor[j].setFont(new Font(buttonFloor[j].getFont().getFontName(),buttonFloor[j].getFont().getStyle(),20));
			buttonFloor[j].setBounds(((i+1)%2)*ElevatorConst.elevatorButtonWide, (i>>1)*ElevatorConst.elevatorButtonHigh, 
					ElevatorConst.elevatorButtonWide, ElevatorConst.elevatorButtonHigh);
			buttonFloor[j].setBackground(Color.white);
			buttonFloor[j].addActionListener(buttonFloorListener);
			add(buttonFloor[j]);
		}

		openDoor.setBounds(0, (ElevatorConst.totalFloor>>1)*ElevatorConst.elevatorButtonHigh,
				ElevatorConst.elevatorButtonWide, ElevatorConst.elevatorButtonHigh);
		openDoor.setMargin(new Insets(1,1,1,1));
		openDoor.setFont(new Font(openDoor.getFont().getFontName(),openDoor.getFont().getStyle(),20));
		openDoor.addActionListener(openFloorListener);
		openDoor.setBackground(Color.white);
		
		closeDoor.setBounds(ElevatorConst.elevatorButtonWide, ((ElevatorConst.totalFloor+1)>>1)*ElevatorConst.elevatorButtonHigh,
				ElevatorConst.elevatorButtonWide, ElevatorConst.elevatorButtonHigh);
		closeDoor.setMargin(new Insets(1,1,1,1));
		closeDoor.setFont(new Font(closeDoor.getFont().getFontName(),closeDoor.getFont().getStyle(),20));
		closeDoor.addActionListener(closeFloorListener);
		closeDoor.setBackground(Color.white);
		
		add(openDoor);
		add(closeDoor);
	}
	
	ActionListener buttonFloorListener=new ActionListener()
	{
		public void actionPerformed(ActionEvent	e)
		{
			int floor=Integer.parseInt(( (JButton)e.getSource() ).getText());

			if (elevator.getFloor()==floor && elevator.doorOpen) 
			{
				elevator.reopenDoor() ;
				return ;
			}
			if (elevator.getElevatorState()==0 && elevator.getFloor()==floor)
			{
				elevator.openDoor();
				return ;
			}

			buttonFloor[floor-1].setBackground(Color.red);
			if (floor==ElevatorConst.totalFloor)
			{
				elevator.setArriveFloor(2*ElevatorConst.totalFloor-floor);
				return ;
			}
			if (floor==1)
			{
				elevator.setArriveFloor(0);
				return ;
			}
			if (elevator.getFloor()<floor) 
			{
				elevator.setArriveFloor(floor-1);
			}
			else if (elevator.getFloor()>floor) 
			{
				elevator.setArriveFloor(2*ElevatorConst.totalFloor-floor);
			}
			else if (elevator.getFloor()==floor)
			{
				if (elevator.getElevatorState()==1 || elevator.getElevatorState()==2)
					 elevator.setArriveFloor(2*ElevatorConst.totalFloor-floor);
				else if (elevator.getElevatorState()==-1 || elevator.getElevatorState()==-2)
					elevator.setArriveFloor(floor-1);
			}
		}
	};
	
	ActionListener closeFloorListener=new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			if (!elevator.doorOpen) return ;
			elevator.closeDoor();
		}
	};
	
	ActionListener openFloorListener=new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			if (elevator.doorOpen)	elevator.reopenDoor();
			else if (elevator.getElevatorState()==0) elevator.setArriveFloor(elevator.getFloor()-1);
			else return ;
		}
	};
}

class FloorView extends JPanel
{
	JTextField floor[];
	Elevator elevator;
	FloorView(Elevator ele)
	{
		elevator=ele;
		setLayout(null);
		floor=new JTextField[ElevatorConst.totalFloor];
		
		ele.elevator.setEditable(false);
		ele.elevator.setBounds(0, 
				(ElevatorConst.totalFloor-1)*(ElevatorConst.floorHigh+ElevatorConst.floorSpace)+ElevatorConst.floorSpace, 
				ElevatorConst.floorWide, ElevatorConst.floorHigh);
		ele.elevator.setBackground(Color.green);
		add(ele.elevator);
		
		for (int i=0,j=19; i<ElevatorConst.totalFloor; i++,j--)
		{
			floor[j]=new JTextField(""+(ElevatorConst.totalFloor-i));
			floor[j].setEditable(false);
			floor[j].setHorizontalAlignment(JTextField.CENTER);
			floor[j].setBackground(Color.black);
			floor[j].setForeground(Color.red);
			floor[j].setBounds(0, 
					i*(ElevatorConst.floorHigh+ElevatorConst.floorSpace)+ElevatorConst.floorSpace, 
					ElevatorConst.floorWide, ElevatorConst.floorHigh);
			add(floor[j]);
		}

	}
}


public class Elevator extends Thread
{
	TextField elevator=new TextField();
	private ElevatorInsideView elevatorInsideView=new ElevatorInsideView(this);
	private FloorView floorView=new FloorView(this);
	private int state;	
	//1 正在向上运行 并且接向上的
	//10 表示向上运行的过程中在某些楼层停止
	//2表示向上运行，接向下的
	//0 表示没有运行
	private boolean restart;
	private int floor;
	public boolean arrive[];
	private int willArrive;
	boolean doorOpen;
	
	public Elevator()
	{
		doorOpen=false;
		willArrive=1;
		state=0;
		floor=1;
		arrive=new boolean[ElevatorConst.totalFloor*2];
		for (int i=0; i<ElevatorConst.totalFloor*2; i++)
			arrive[i]=false;
	}
	
	public int getElevatorState()
	{
		return state;
	}
	

	public void run()
	{
		while (true)
		{
			state=upOrDown();
			if (state==1 || state==2)	upFloor();
			else if (state==-1 || state==-2) 	downFloor();
			else if (state==100)
			{
				arrive[floor-1]=RequireButton.require[floor-1]=false;
				elevatorInsideView.buttonFloor[floor-1].setBackground(Color.white);
				RequireButton.requireButton[floor-1].setBackground(Color.white);
				openDoor();
				state=1;
			}
			else if (state==-100)
			{
				arrive[2*ElevatorConst.totalFloor-floor]=RequireButton.require[2*ElevatorConst.totalFloor-floor]=false;
				elevatorInsideView.buttonFloor[floor].setBackground(Color.white);
				RequireButton.requireButton[2*ElevatorConst.totalFloor-floor].setBackground(Color.white);
				openDoor();
				state=-1;
			}
		}
	}
	public void upFloor()
	{
		for (int i=0; i<ElevatorConst.floorHigh+ElevatorConst.floorSpace; i++)
		{
			try	
			{
				Thread.sleep(40);
			}
			catch (InterruptedException e)
			{		
			}
			elevator.setLocation(elevator.getLocation().x, 
					elevator.getLocation().y-1);
		}
		floor++;
		if (willArrive==floor &&  state==2 && !arrive[floor-1] && !RequireButton.require[floor-1])
		{
			arrive[2*ElevatorConst.totalFloor-floor]=false;
			elevatorInsideView.buttonFloor[floor-1].setBackground(Color.white);
			
			RequireButton.require[2*ElevatorConst.totalFloor-floor]=false;
			RequireButton.requireButton[2*ElevatorConst.totalFloor-floor].setBackground(Color.white);
			openDoor();			
		}
		else if (arrive[floor-1] || RequireButton.require[floor-1] )
		{
			elevatorInsideView.buttonFloor[floor-1].setBackground(Color.white);
			arrive[floor-1]=false;
			
			RequireButton.require[floor-1]=false;
			RequireButton.requireButton[floor-1].setBackground(Color.white);
			openDoor();			
		}
	}
	
	public void downFloor()
	{
		for (int i=0; i<ElevatorConst.floorHigh+ElevatorConst.floorSpace; i++)
		{
			try	
			{
				this.sleep(40);
			}
			catch (InterruptedException e)
			{
			}
			elevator.setLocation(elevator.getLocation().x, 
					elevator.getLocation().y+1);
		}
		floor--;
		if (willArrive==floor && state==-2 && !arrive[2*ElevatorConst.totalFloor-floor] && !RequireButton.require[2*ElevatorConst.totalFloor-floor])
		{
			arrive[floor-1]=false;
			elevatorInsideView.buttonFloor[floor-1].setBackground(Color.white);
			
			RequireButton.require[floor-1]=false;
			RequireButton.requireButton[floor-1].setBackground(Color.white);
			openDoor();
		}
		else if (arrive[2*ElevatorConst.totalFloor-floor] || RequireButton.require[2*ElevatorConst.totalFloor-floor])
		{
				arrive[2*ElevatorConst.totalFloor-floor]=false;
				elevatorInsideView.buttonFloor[floor-1].setBackground(Color.white);
				
				RequireButton.require[2*ElevatorConst.totalFloor-floor]=false;
				RequireButton.requireButton[2*ElevatorConst.totalFloor-floor].setBackground(Color.white);
				openDoor();
		}

	}
	

	
	private int upOrDown()
	{
		if ((state==0) && (arrive[floor-1]))	return 100;
		if ((state==0) && (arrive[2*ElevatorConst.totalFloor-floor])) return -100;
				

		
		if (state==0)
		{
			for (int i=0; i<ElevatorConst.totalFloor; i++)
			{
				if (arrive[i])
				{
					willArrive=i+1;	
					if (i+1>floor)		return 1;
					else 	return -2;	

				}
				if (arrive[2*ElevatorConst.totalFloor-i-1])
				{
					willArrive=i+1;
					if (i+1>floor)	return 2;
					else return -1;
				}
			}
			search(this);
		}
		if (state==1 || state==2)	//电梯状态向上
		{
			for (int i=ElevatorConst.totalFloor-1; i>floor-1; i--)
			{
				if (arrive[i])									//需求向上 
				{
					willArrive=Math.max(i+1,willArrive);	
					return 1;
				}
				if (arrive[2*ElevatorConst.totalFloor-i-1])
				{
					willArrive=Math.max(i+1,willArrive);	
					return 2;
				}
			}
		}
		
		if (state==-1 || state==-2)
		{
			for (int i=0; i<floor-1; i++)
			{
				if (arrive[2*ElevatorConst.totalFloor-i-1])
				{
					willArrive=Math.min(i+1,willArrive);
					return -1;
				}
				if (arrive[i])
				{
					willArrive=Math.min(i+1,willArrive);
					return -2;
				}
			}
			
		}
		return 0;
	}
	
	static public synchronized void search(Elevator ele)	//寻找电梯的目标楼层
	{
		int i=RequireButton.getRequire();
		if (i==-1) return ;
		int floor=i+1;
		if (floor>ElevatorConst.totalFloor) floor=2*ElevatorConst.totalFloor-floor;
		if (RequireButton.require[i] && RequireButton.isShortest(ele, floor))
		{
			ele.arrive[i]=true;
			RequireButton.require[i]=false;
			RequireButton.requireList.removeFirst();
			return ;
		}
	}
	
	public void setFloor(int i)
	{
		floor=i;
	}
	
	public void setArriveFloor(int i)
	{
		arrive[i]=true;
	}
	
	public int getFloor()
	{
		return floor;
	}
	
	public void openDoor()
	{
		elevator.setBackground(Color.pink);
		doorOpen=true;

		restart=false;
		try	
		{
			this.sleep(2000);
		}
		catch (InterruptedException e)
		{
			if (restart)	openDoor();
		}
		closeDoor();
	}
	
	public void closeDoor()
	{
		if (doorOpen && state!=-100 && state!=100) this.interrupt();
		elevator.setBackground(Color.green);
		doorOpen=false;
	}
	
	public void reopenDoor()
	{
		restart=true;
		this.interrupt();
	}
	
	public void add(JFrame frame,int i)
	{	
		frame.setLayout(null);
				
		elevatorInsideView.setBounds(i*250+30, 80,
				ElevatorConst.elevatorButtonWide*2, ElevatorConst.elevatorButtonHigh*((ElevatorConst.totalFloor>>1)+2));
		floorView.setBounds(250*(i+1)-ElevatorConst.elevatorButtonWide*2,0, 
				ElevatorConst.elevatorButtonWide*2,
				(ElevatorConst.floorHigh+ElevatorConst.floorSpace)*ElevatorConst.totalFloor);
		frame.add(floorView);
		frame.add(elevatorInsideView);
		
	}
}
