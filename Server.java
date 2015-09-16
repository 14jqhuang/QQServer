package mywrittenqq;

import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


public class Server {
	
	//List标错往往是引包错误
	private List<ServerThread> clients=null;
	public void startup()
	{
		ServerSocket ss=null;
		Socket s=null;
		
		try {
			//多线程实现多个客户端聊天
			ss= new ServerSocket(9898);
			clients= new ArrayList<ServerThread>();
			while (true)
			{
				s=ss.accept();
				ServerThread st= new ServerThread(s);
				new Thread(st).start();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try 
			{
				if (ss!=null) ss.close();
			}
			
		catch (IOException e2) {
		}
	}
	}
	
	public static void main(String[] args) 
	{
		//静态方法不能调用动态 (实现接口)
		Server server = new Server();
		server.startup();
		
	}
	
	private class ServerThread implements Runnable{
		Socket s=null;
		BufferedReader br;
		PrintWriter pr;
		String st=null;
		String name=null;
		Boolean flag=true;
		public ServerThread(Socket s) throws IOException
		{
			this.s=s;
			br= new BufferedReader(new InputStreamReader(s.getInputStream()));
			pr= new PrintWriter(s.getOutputStream(),true);//此处的“true”是自动刷新
			name=br.readLine();
			System.out.println(name+"上线了:"+s.getInetAddress()+s.getLocalAddress()+s.getPort());
			clients.add(this);
			send(name+"上线了");
		}
		
		public void send(String msg)
		{
			for (ServerThread st:clients)
			{
				st.pr.println(msg);
			}
		}
		public void receieve() throws IOException
		{
			while ((st=br.readLine())!=null)
			{
				if (st.equalsIgnoreCase("quit"))
				{
					stop();
					//服务器接受到quit命令，然后传送给client，通知它关闭
					pr.println("disconnect");
					break;
				}
				System.out.println(name+":"+st);
//				pr.println("Recieved: "+st);
				send(name+"str");
			}
		}
		
		public void stop()
		{
			System.out.println(name+"已经离开了");
			flag=false;
			clients.remove(this);
			send(name+"已经下线了");
		}
		public void run()
		{
			while (true)
			{
				if (!flag)
				break;
				try {
					receieve();
				} catch (SocketException se){
					stop();//防止输入quit时，flag还未变为false
				System.out.println(name+"已经非正常离开了");
				}catch (IOException e) {
					e.printStackTrace();
				}
			finally {
				try{
					if (s!=null)
					s.close();
				}catch (IOException e){e.printStackTrace();}
			}
			
		}
	  }
	}

}
