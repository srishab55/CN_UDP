import java.awt.SecondaryLoop;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.spec.DSAGenParameterSpec;
import java.sql.Time;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

//5000
public class Application {
	Timer time;
	 packet pack=null;
	static int next=1234,prev=5002;
	int src_id=10;
	int seq_no=0;
	static DatagramSocket ds=null;
	public Application(int port) throws SocketException {
		seq_no++;
		time=new Timer();
		ds=new DatagramSocket(port);			// Initializing at port @port
		time.scheduleAtFixedRate((new TimerTask() {
			
			@Override
			public void run() {
				seq_no++;
				System.out.println("hello from p1");
				int ttl=8;
				try {
					DatagramSocket ds = new DatagramSocket();
				} catch (SocketException e) {
					
					e.printStackTrace();
				} 

				 InetAddress ip = null;
				try {
					ip = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte buf[] = null; 
				packet p=new packet("red", ttl, seq_no,src_id );
				
				buf=p.packet_format("red", ttl, seq_no,src_id );
				
				DatagramPacket DpSend = 
						new DatagramPacket(buf, buf.length, ip, next);  // Red packet generated and forwarded to next application
				try {
					ds.send(DpSend);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}),1000*5,5000);
		
	}
	public static void main(String[] args) throws IOException {
		Application app=new Application(5000);
		InetAddress ip = InetAddress.getLocalHost();
		long time=System.currentTimeMillis();
		byte receive[]=new byte[10000];
		while(true)
		{
			
			DatagramPacket Receive=new DatagramPacket(receive, 10000); 
			ds.receive(Receive);
			String input=data(receive).toString();
			input=app.parseInput(input);
			if(input.equals("red"))  // if received packet is red
			{
			//	System.out.println("green sent");
				app.pack.ttl--;		// decrement ttl
				packet pack1=new packet("green", app.pack.ttl, app.pack.seq_num, app.pack.source_id); // green packet is generated
				
				app.sendData(pack1,next);			// to forward 
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				app.sendData(pack1, prev);			// backward
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if(input.equals("blue"))
				{
				app.printpacket();
				}
			else if(input.equals("green")) 
				{
			//	System.out.println("green sent from green ttl="+app.pack.ttl);
				int t=app.generate_rand();
				if(t==0) app.pack.ttl--;  // decrementing with probability .5
				if(app.pack.ttl>0)
				{
				
				packet pack1=new packet("green", app.pack.ttl, app.pack.seq_num, app.pack.source_id); //since ttl >0 forward the packet
				app.sendData(pack1,next);
				//app.printpacket();
				}
				if(app.pack.ttl==0)
				{
				//app.pack.ttl--;
				packet pack1=new packet("blue", app.pack.ttl, app.pack.seq_num, app.pack.source_id); // ttl=0 gives blue packet in the next direction
				app.sendData(pack1,next);
				//app.printpacket();
				}
				}
			receive=new byte[10000];
		}
		
	}
	public static StringBuilder data(byte[] a) 
	{ 
		if (a == null) 
			return null; 
		StringBuilder ret = new StringBuilder(); 
		int i = 0; 
		while (a[i] != 0) 
		{ 
			ret.append((char) a[i]); 
			i++; 
		} 
		return ret; 
	} 
	private  void printpacket() {
		System.out.println("color :"+pack.color);
		System.out.println("ttl :"+pack.ttl);
		System.out.println("sequence :"+pack.seq_num);
		System.out.println("source id :"+pack.source_id);
		
	}
	private String parseInput(String input) {
		
		StringTokenizer st=new StringTokenizer(input, " ");
		String color;
		int ttl;
		int seq_num;
		int source_id;
		color=st.nextToken();
		ttl=Integer.parseInt(st.nextToken());
		seq_num=Integer.parseInt(st.nextToken());
		source_id=Integer.parseInt(st.nextToken());
		packet p=new packet(color, ttl, seq_num, source_id);
		pack=p;
	//	System.out.println(color);
		return color;
		
	}
	int generate_rand()
	{
		Random rand=new Random();
		return rand.nextInt(2);
	}
void sendData(packet p,int port)
{
	try {
		DatagramSocket ds = new DatagramSocket();
	} catch (SocketException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 

	 InetAddress ip = null;
	try {
		ip = InetAddress.getLocalHost();
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	byte buf[] = null; 
	packet p1=new packet(p.color,p.ttl,p.seq_num,p.source_id);
	
	buf=p1.packet_format(p.color, p.ttl, seq_no,src_id );
	
	DatagramPacket DpSend = 
			new DatagramPacket(buf, buf.length, ip, port); 
	try {
		ds.send(DpSend);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}

class packet
{
	String color;
	int ttl;
	int seq_num;
	int source_id;
	public packet(String color,int ttl,int seq_num,int src) {
		this.color=color;
		this.ttl=ttl;
		this.seq_num=seq_num;
		this.source_id=src;
	}
	public byte[] packet_format(String color,int ttl,int seq_num,int src) {
	
		this.color=color;
		this.ttl=ttl;
		this.seq_num=seq_num;
		this.source_id=src;
		String toReturn=this.color+" "+this.ttl+" "+this.seq_num+" "+source_id;
	//	System.out.println(toReturn);
		byte str[]=new byte [toReturn.length()];
		str=toReturn.getBytes();
		
		return str;
	}
	
}
