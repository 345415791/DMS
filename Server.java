package com.tarena;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * �����Ӧ�ó���
 * @author Administrator
 * 
 */
public class Server {
	//�����ڷ���˵�Socket
	private ServerSocket server;
	//�̳߳أ����ڹ���ͻ������ӵĽ����߳�
	private ExecutorService threadPool;
	//�������пͻ��˷��͹����������־���ļ�
	private File serverLogFile;
	//����һ��˫������У����ڴ洢�����־��!!!!!!!!!!!!!!
	private BlockingQueue<String> messageQueue; 
	
	/**
	 * ���췽�������ڳ�ʼ�������
	 * @throws IOException 
	 */
	public Server() throws IOException{
		try {
			/*
			 * ����ServerSocketʱ��Ҫָ������˿�
			 */
			System.out.println("��ʼ�������");
			server = new ServerSocket(8088);
			//��ʼ���̳߳�
			threadPool = 
				Executors.newFixedThreadPool(50);
			//��ʼ��������־���ļ�
			serverLogFile 
				= new File("server-log.txt");
			//��ʼ���������
			messageQueue
				= new LinkedBlockingQueue<String>();
			
			System.out.println("����˳�ʼ�����");
			
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * ����˿�ʼ�����ķ���
	 */
	public void start(){
		try{
			/*
			 * ��д��־�ļ����߳���������
			 */
			WriteLogThread thread 
				= new WriteLogThread();
			thread.start();
			/*
			 * ServerSocket��accept����
			 * ���ڼ���8088�˿ڣ��ȴ��ͻ��˵�����
			 * �÷�����һ������������ֱ��һ��
			 * �ͻ������ӣ�����÷���һֱ������
			 * ��һ���ͻ��������ˣ��᷵�ظÿͻ��˵�
			 * Socket
			 */
			while(true){
				System.out.println("�ȴ��ͻ�������...");
				Socket socket = server.accept();
				/*
				 * ��һ���ͻ������Ӻ�����һ���߳�
				 * ClientHandler�����ÿͻ��˵�
				 * Socket���룬ʹ�ø��̴߳������
				 * �ͻ��˵Ľ�����
				 * �������������ٴν���ѭ��������
				 * ��һ���ͻ��˵������ˡ�
				 */
				Runnable handler
					= new ClientHandler(socket);
//				Thread t = new Thread(handler);
//				t.start();
				/*
				 * ʹ���̳߳ط�������߳�������
				 * ��ǰ���ӵĿͻ���
				 */
				threadPool.execute(handler);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args){
		Server server;
		try {
			server = new Server();
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("����˳�ʼ��ʧ��");
		}	
	}
	
	/**
	 * ������е�һ���̣߳�������ĳ���ͻ���
	 * ������
	 * ʹ���̵߳�Ŀ����ʹ�÷���˿��Դ����
	 * �ͻ����ˡ�
	 * @author Administrator
	 *
	 */
	class ClientHandler implements Runnable{
		//��ǰ�̴߳���Ŀͻ��˵�Socket
		private Socket socket;
		
		/**
		 * ���ݸ����Ŀͻ��˵�Socket������
		 * �߳���
		 * @param socket
		 */
		public ClientHandler(Socket socket){
			this.socket = socket;
		}
		/**
		 * ���̻߳Ὣ��ǰSocket�е���������ȡ
		 * ����ѭ����ȡ�ͻ��˷��͹�������Ϣ��
		 */
		public void run() {
			/*
			 * ������try������Ŀ���ǣ�Ϊ����
			 * finally��Ҳ�������õ�
			 */
			PrintWriter pw = null;
			try{
				/*
				 * Ϊ���÷������ͻ��˷�����Ϣ��
				 * ������Ҫͨ��socket��ȡ�������
				 */
				OutputStream out
					= socket.getOutputStream();
				//ת��Ϊ�ַ���������ָ�����뼯
				OutputStreamWriter osw
					= new OutputStreamWriter(
												out,"UTF-8");
				//���������ַ������
				pw = new PrintWriter(osw,true);
				
				
				/*
				 * ͨ���ո����ϵĿͻ��˵�Socket��ȡ
				 * ������������ȡ�ͻ��˷��͹�������Ϣ
				 */
				InputStream in 
					=	socket.getInputStream();
				/*
				 * ���ֽ���������װΪ�ַ������������
				 * ����ָ�����뼯����ȡÿһ���ַ�
				 */
				InputStreamReader isr
					= new InputStreamReader(
												in,"UTF-8");
				/*
				 * ���ַ���ת��Ϊ�����ַ�������
				 * �����Ϳ�������Ϊ��λ��ȡ�ַ����� 
				 */
				BufferedReader br
					= new BufferedReader(isr);		
				
				String message = null;
				/*
				 * ѭ����ȡ�ͻ��˷��͹�����ÿһ��
				 * �����־
				 * ��ȡ��һ�飬�ͽ�����־����
				 * ��Ϣ���У��ȴ���д���ļ���
				 */
				while((message = br.readLine())!=null){
					/*
					 * ����ȡ���ͻ��˷��͵�������"over"
					 * ��ʾ�ͻ��˷������������־��
					 * Ӧ��ֹͣ�ٽ��ܿͻ��˷��͵�������
					 */
					if("over".equals(message)){
						break;
					}	
		/*************˫�������,����*************************/
					messageQueue.offer(message);//˫�������,����
				}
				/*
				 * ���˳�ѭ����˵�����пͻ��˷��͵���־
				 * �����ܳɹ�������������Ϣ�����С�
				 * ��ô���ǻظ��ͻ���"OK"
				 */
				pw.println("OK");
		
			}catch(Exception e){
				//��Windows�еĿͻ��ˣ�
				//����ͨ������Ϊ�ͻ��˶Ͽ�������
				pw.println("ERROR");
			}finally{
				/*
				 * ������linux�û�����windows
				 * �û����������˶Ͽ����Ӻ�
				 * ���Ƕ�Ӧ���ڷ����Ҳ��ͻ���
				 * �Ͽ�����
				 */
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
			
		}		
	}
	
	/**
	 * ���߳���Server�н���һ��ʵ��
	 * ������:
	 *   ѭ������Ϣ������ȡ��һ�������־��
	 *   ��д��server-log.txt�ļ���
	 *   ��������û����־�󣬾�����һ��ʱ��
	 *   �ȴ��ͻ��˷����µ���־������
	 * @author Administrator
	 *
	 */
	class WriteLogThread extends Thread{
		public void run(){
			try{
				PrintWriter pw
					= new PrintWriter(
									serverLogFile);
				
				while(true){
					if(messageQueue.size()>0){
						String log 
							= messageQueue.poll();
						pw.println(log);
					}else{
						pw.flush();
						Thread.sleep(5000);
					}				
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
}
