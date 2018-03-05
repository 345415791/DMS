package com.tarena;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import bo.LogData;
import bo.LogRec;

import com.tarena.util.IOUtil;

/**
 * �ͻ���Ӧ�ó���
 * ������UNIXϵͳ��
 * �����Ƕ��ڶ�ȡϵͳ��־�ļ�WTMPX�ļ�
 * �ռ�ÿ���û��ĵ���ǳ���־����ƥ��ɶԵ�
 * ��־��Ϣ�������������ˡ�
 * @author Administrator
 *
 */
public class Client {
	//unixϵͳ��־�ļ� wtmpx�ļ�
	private File logFile;
	
	//����ÿ�ν��������־�ļ�
	private File textLogFile;
	
	//����ÿ�ν�����־�ļ����λ��(��ǩ)���ļ�
	private File lastPositionFile;
	
	//ÿ�δ�wtmpx�ļ��н�����־������
	private int batch;
	
	//����ÿ�������Ϻ�����������־���ļ�
	private File logRecFile;
	
	//����ÿ����Ժ�û����Գɹ��ĵ�����־���ļ�
	private File loginFile;
	
	
	/**
	 * ���췽���г�ʼ��
	 */
	public Client(){
		try{
			this.batch = 10;
			logFile = new File("wtmpx");
			lastPositionFile 
							= new File(
									"last-position.txt");
			textLogFile = new File("log.txt");
			
			logRecFile = new File("logrec.txt");
			
			loginFile = new File("login.txt");
			
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	/**
	 * �÷���Ϊ��һ�󲽵ĵڶ�С�����߼�
	 * ���ڼ��wtmpx�ļ��Ƿ������ݿ��Զ�ȡ
	 * 
	 * @return -1:û�����ݿɶ���
	 *         ��������:������ȡ��λ��
	 */
	public long hasLogs(){
		try{
			//Ĭ�ϴ��ļ���ʼ��ȡ
			long lastposition = 0;
			/*
			 * �������������
			 * 1:û���ҵ�last-position.txt
			 *   �ļ�����˵������û�ж���wtmpx
			 * 2:��last-position.txt�ļ�
			 *   ��ô���ʹӸ��ļ���¼��λ�ÿ�ʼ
			 *   ��ȡ  
			 */
			if(lastPositionFile.exists()){
				lastposition 
					= IOUtil.readLong(
							lastPositionFile);
			}
			/*
			 * ��Ҫ�жϣ�wtmpx�ļ����ܴ�С
			 * ��ȥ���׼����ʼ��ȡ��λ�ã�Ӧ��
			 * ����һ����־��ռ�õ��ֽ���(372)
			 */
			if(logFile.length()-lastposition<LogData.LOG_LENGTH){
				lastposition = -1;
			}
			
			
			return lastposition;
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	/**
	 * �жϵ�ǰRandomAccessFile��ȡ��λ��
	 * �Ƿ���wtmpx�ļ��л������ݿɶ�
	 * @param raf
	 * @return
	 * @throws IOException 
	 */
	public boolean hasLogsByStep(
							RandomAccessFile raf)
							throws IOException{
		if(logFile.length() - 
			 raf.getFilePointer()>=LogData.LOG_LENGTH){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * ��һ��:
	 * ��wtmpx�ļ���һ�ζ�ȡbatch����־��
	 * ������Ϊbatch���ַ�����ÿ���ַ�����ʾ
	 * һ����־��Ȼ��д��log.txt�ļ��С�
	 * @return true�������ɹ�
	 *         false:����ʧ��
	 */
	public boolean readNextLogs(){
		/*
		 * ��������:
		 * 1:�������ж�wtmpx�ļ��Ƿ����
		 * 2:�ж��Ƿ��������ݿɶ�
		 * 3:����һ�ζ�ȡ��λ�ÿ�ʼ������ȡ
		 * 4:ѭ��batch�Σ���ȡbatch��372�ֽ�
		 *   ��ת��Ϊbatch����־
		 * 5:���������batch����־д��log.txt
		 *   �ļ��С�  
		 */
		//1
		if(!logFile.exists()){
			return false;
		}
		//2
		long lastposition = hasLogs();
		if(lastposition<0){
			return false;
		}
		/*
		 * Ϊ�˱����ظ�ִ�е�һ��������ԭ��
		 * ��һ�����Ѿ���������־�ļ���������
		 * ���ǿ������жϣ�����һ��ִ����Ϻ�
		 * ���ɵ�log.txt�ļ����ڣ��Ͳ���ִ��
		 * ��һ���ˡ�
		 * ���ļ����ڵڶ���ִ����Ϻ�ɾ����
		 */
		if(textLogFile.exists()){
			//���ļ����ڣ�˵���������ˡ�
			return true;
		}
		
		try{
			//����RandomAccessFile����ȡ��־�ļ�
			RandomAccessFile raf
				= new RandomAccessFile(
									 logFile,"r");
			//�ƶ��α굽ָ��λ�ã���ʼ������ȡ
			raf.seek(lastposition);
			
			//����һ�����ϣ����ڱ�����������־
			List<LogData> logs
					= new ArrayList<LogData>();
			
			//ѭ��batch�Σ�����batch����־
			for(int i=0;i<batch;i++){
				/*
				 * �����жϻ������־���Զ�
				 */
				if(!hasLogsByStep(raf)){
					break;
				}
				
				//��ȡ�û���
				String user 
					= IOUtil.readString(
							raf, LogData.USER_LENGTH);
				
				//��ȡpid
				raf.seek(
						lastposition+
						LogData.PID_OFFSET);
				int pid = IOUtil.readInt(raf);
				
				//��ȡTYPE
				raf.seek(
						lastposition+
						LogData.TYPE_OFFSET);
				short type 
							= IOUtil.readShort(raf);
				
				//��ȡTIME
				raf.seek(
						lastposition+
						LogData.TIME_OFFSET);
				int time = IOUtil.readInt(raf);
				
				//��ȡHOST
				raf.seek(
						lastposition+
						LogData.HOST_OFFSET);
				String host 
						= IOUtil.readString(
								raf, LogData.HOST_LENGTH);
				
				/*
				 * ��lastposition����Ϊ��ǰraf
				 * ���α�λ��
				 */
				lastposition=raf.getFilePointer();
				/*
				 * ���������������ݴ���һ��LogData
				 * �����У��ٽ��ö�����뼯���С�
				 */
				LogData log
					= new LogData(
							user,pid,type,time,host);
				logs.add(log);		
			}
//			System.out.println(
//					"��������"+logs.size()+"����־");
//			for(LogData log : logs){
//				System.out.println(log);
//			}
			
			/*
			 * �����������־д��log.txt�ļ���
			 */
			IOUtil.saveList(logs, textLogFile);
			/*
			 * ����ν�����RandomAccessFile
			 * ���α�λ�ü�¼���Ա����´ν�����
			 * ʱ�������ȡ��
			 */
			IOUtil.saveLong(
					raf.getFilePointer(), 
					lastPositionFile);
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * �ڶ��󲽵�:
	 * ƥ����־
	 * 
	 * ���岽��:
	 * 1:��ȡlog.txt�ļ�������һ����������
	 *   ���ڶ�ȡ������ת��Ϊ���ɸ�LogData
	 *   �������List�����еȴ���ԡ�
	 * 2:��ȡlogin.txt�ļ�������һ��û�����
	 *   �ɹ��ĵ�����־��ȡ������ת��Ϊ���ɸ�
	 *   LogData����Ҳ����List�����У��ȴ�
	 *   �����ԡ�
	 * 3:ѭ��List,��������ǳ���־�ֱ���뵽
	 *   ����Map�У�value���Ƕ�Ӧ����־����
	 *   key����[user,pid,ip]������ʽ���ַ�
	 *   ��
	 * 4:ѭ���ǳ���map����ͨ��keyѰ�ҵ���map
	 *   �еĵ�����־���Դﵽ��Ե�Ŀ��,��
	 *   ��Ե���־ת��Ϊһ��LogRec�������
	 *   һ��List������
	 * 5:��������Գɹ�����־д���ļ�
	 *   logrec.txt
	 * 6:������û��Գɹ�����־д���ļ�
	 *   login.txt    
	 *         
	 * @return
	 */
	public boolean matchLogs(){
		/*
		 * ��Ҫ���ж�
		 */
		//1 ���log.txt�ļ��Ƿ����
		if(!textLogFile.exists()){
			return false;
		}
		/*
		 * ���ڶ����Ѿ�ִ����Ϻ󣬻���������
		 * �ļ�:logrec.txx,login.txt
		 * ����������ִ��ʱ���ִ�������������
		 * ִ�еڶ������Ὣ�ϴεڶ����Ѿ���Ե�
		 * ��־�����ǣ��Ӷ��������ݶ�ʧ��Ϊ��
		 * ����Ҫ��һ����Ҫ���жϣ�����
		 * logrec.txt�ļ������ڣ���˵���ڶ���
		 * �Ѿ���ɣ����ǵ�����û��˳��ִ�С�
		 * ��Ϊ������ִ����Ϻ󣬻Ὣ���ļ�ɾ����
		 * ���ԣ������ڣ���ڶ�������ִ�С�
		 */
		if(logRecFile.exists()){
			return true;
		}
		
		/*
		 * ҵ���߼�
		 */
		try{
			//1
			List<LogData> list
				= IOUtil.loadLogData(textLogFile);
			
			//2
			if(loginFile.exists()){
				list.addAll(
						IOUtil.loadLogData(loginFile)
				);
			}
			//3
			Map<String,LogData> loginMap
				= new HashMap<String,LogData>();
			Map<String,LogData> logoutMap
				= new HashMap<String,LogData>();
			
			for(LogData log : list){
				if(log.getType()==LogData.TYPE_LOGIN){
					putLogToMap(log,loginMap);
				}else if(log.getType()==LogData.TYPE_LOGOUT){
					putLogToMap(log,logoutMap);
				}
			}
			
			//4
			Set<Entry<String,LogData>> set 
									= logoutMap.entrySet();
			
			//���ڴ��������Գɹ�����־�ļ���
			List<LogRec> logRecList
								= new ArrayList<LogRec>();
			
			for(Entry<String,LogData> entry:set){
				/*
				 * �ӵǳ�MAP�У�ȡ��key
				 */
				String key = entry.getKey();
				/*
				 * ���ݵǳ���key���ӵ���map��
				 * ����ͬ��keyɾ��Ԫ��,ɾ����
				 * ���Ƕ�Ӧ�ĵ�����־
				 */
				LogData login = loginMap.remove(key);
				if(login!=null){
					//ƥ���ת��Ϊһ��LogRec����
					LogRec logrec
						= new LogRec(
								login,entry.getValue());
					//�������־���뼯��
					logRecList.add(logrec);
				}				
			}
			//����forѭ�����൱����Թ����������
			
			//5
			IOUtil.saveList(
								logRecList, logRecFile);
			
			//6
			Collection<LogData> c
									= loginMap.values();
			IOUtil.saveList(
					new ArrayList(c), loginFile);
			
			/*
			 * ���ڶ���ִ����Ϻ�
			 * log.txt�ļ��Ϳ���ɾ����
			 */
			textLogFile.delete();
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			/*
			 * ���ڶ��������쳣����ô�ڶ������ɵ�
			 * ����ļ�logrec.txt�ļ�������Ч�ġ�
			 * Ӧ��ɾ�����Ա�������ִ�еڶ���
			 */
			if(logRecFile.exists()){
				logRecFile.delete();
			}
			return false;
		}
	}
	/**
	 * ����������־���������Map��
	 * @param log
	 * @param map
	 */
	private void putLogToMap(
							LogData log,
							Map<String,LogData> map){
		map.put(
				log.getUser()+","+
				log.getPid()+","+
				log.getHost(), log);
	}
	
	/**
	 * ������:
	 * ����Ե���־�����������
	 * ����:
	 * 	1:����Socket�������ӷ����
	 *  2:ͨ��Socket��ȡ����������𲽰�װΪ
	 *    �����ַ���������ַ�����UTF-8��
	 *  3:���������ַ������������ڶ�ȡ
	 *    logrec.txt����ȡ�����־��
	 *  4:��logrec�ļ��ж�ȡÿһ����־��Ϣ
	 *    �������������
	 *  5:ͨ��Socket��ȡ�����������𲽰�װΪ
	 *    �����ַ������������ڶ�ȡ����˵�
	 *    ��Ӧ��
	 *  6:��ȡ����˵���Ӧ������"OK",��˵��
	 *    ����˳ɹ����������Ƿ��͵������־
	 *    ��ô�ͽ�logrec.txt�ļ�ɾ����
	 *    ������ִ����ϡ�
	 *    �����ص���Ӧ����"OK",���ʾ����û��
	 *    �ɹ�����ô�÷�������false��Ӧ��
	 *    ���³���ִ�е�������        
	 * @return
	 */
	public boolean sendLogToServer(){
		/*
		 * ��Ҫ�ж�
		 */
		if(!logRecFile.exists()){
			return false;
		}
		
		/*
		 * ҵ���߼�
		 */
		Socket socket = null;
		BufferedReader br = null;
		try{
			socket 
				= new	Socket("localhost",8088);
			
			//��ȡlogrec.txt
			FileInputStream fis
				= new FileInputStream(
												logRecFile);
			InputStreamReader isr
				= new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			//���͸���������
			OutputStream out
				= socket.getOutputStream();
			OutputStreamWriter osw
				= new OutputStreamWriter(
											out,"UTF-8");
			PrintWriter pw
				= new PrintWriter(osw);
			
			String line = null;
			/*
			 * ѭ����logrec.txt�ļ��ж�ȡÿһ��
			 * �����־���������������
			 */
			while((line=br.readLine())!=null){
				pw.println(line);
			}
			//�����һ��over,��ʾ���������
			pw.println("over");
			pw.flush();
			//�Ѿ���logrec.txt�ļ��е����ݷ�����
			//�����꣬����ȡ�ļ������ص�
			br.close();
			
			/*
			 * ͨ��Socket���������������ڶ�ȡ
			 * ����˵���Ӧ
			 */
			InputStream in
				=	socket.getInputStream();
			
			BufferedReader brServer
				= new BufferedReader(
						new InputStreamReader(
								in,"UTF-8"
						)
				);
			//��ȡ����˷��ͻ�������Ӧ
			String response
				= brServer.readLine();
			
			if("OK".equals(response)){
				/*
				 * �������ȷ���շ��͵���־��
				 * �Ϳ��Խ��ڶ������ɵ�logrec.txt
				 * �ļ�ɾ���ˡ�
				 */
				logRecFile.delete();
				return true;
			}
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			//��socket�ر�
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
			//��ȡ�ļ���������Ҳ����û�ر�
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		
	}
	
	
	/**
	 * �ͻ��˿�ʼ�����ķ���
	 */
	public void start(){
		/*
		 * ��ʼ�����У�����Ҫѭ��������������
		 * 1:��wtmpx�ļ���һ�ν���batch����־
		 * 2:�����������־�����ϴ�û��ƥ�����־
		 *   һ��ƥ��ɶ�
		 * 3:��ƥ��ɶԵ���־�����������  
		 */
		while(true){
			//1
			readNextLogs();
			
			//2
			matchLogs();
			
			//3
			sendLogToServer();
		}
	}
	
	public static void main(String[] args){
		Client client = new Client();
		client.start();
	}
}







