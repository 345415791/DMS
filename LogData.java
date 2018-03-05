package bo;
/**
 * LogData��ÿһ��ʵ�����ڱ�ʾwtmpx�ļ��е�
 * һ����־��Ϣ
 * @author Administrator
 */
public class LogData {
	/**
	 * ��־��wtmpx�ļ��еĳ���
	 * ÿһ����־�ĳ��ȶ���372�ֽ�
	 */
	public static final int LOG_LENGTH=372;
	
	/**
	 * user�ڵ�����־����ʼ�ֽ�
	 */
	public static final int USER_OFFSET=0;
	
	/**
	 * user����־��ռ�õ��ֽ���
	 */
	public static final int USER_LENGTH=32;
	
	/**
	 * PID����־�е���ʼλ��
	 */
	public static final int PID_OFFSET=68;
	
	/**
	 * TYPE����־�е���ʼλ��
	 */
	public static final int TYPE_OFFSET=72;
	
	/**
	 * TIME����־�е���ʼλ��
	 */
	public static final int TIME_OFFSET=80;
	
	/**
	 * HOST����־�е���ʼλ��
	 */
	public static final int HOST_OFFSET=114;
	/**
	 * HOST����־�ļ��еĳ���
	 */
	public static final int HOST_LENGTH=258;	
	/**
	 * ��־���ͣ��������
	 */
	public static final short TYPE_LOGIN=7;
	/**
	 * ��־���ͣ��ǳ�����
	 */
	public static final short TYPE_LOGOUT=8;	
	
	//��¼�û����û���
	private String user;
	
	//����ID
	private int pid;
	
	//��־����(�����ǳ�)
	private short type;
	
	//��־���ɵ�ʱ��(����͵ǳ���ʱ��)
	private int time;
	
	//��¼�û���IP��ַ
	private String host;

	public LogData(String user, int pid, short type, int time, String host) {
		super();
		this.user = user;
		this.pid = pid;
		this.type = type;
		this.time = time;
		this.host = host;
	}
	/**
	 * ����һ���ַ���
	 * (��ʽӦ���ǵ�ǰ��toString��������)
	 * �����ַ���ת��Ϊһ��LogData����
	 * @param line
	 */
	public LogData(String line) {
		//1:����","����ַ���
		String[] array = line.split(",");
		//2:�������е�ÿһ�����õ������ϼ���
		this.user = array[0];
		this.pid = Integer.parseInt(
														array[1]);
		this.type = Short.parseShort(
														array[2]);
		this.time = Integer.parseInt(
														array[3]);
		this.host = array[4];
		
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * ������д
	 */
	public String toString(){
		return user+","+pid+","+
		       type+","+time+","+host;
	}
}	



