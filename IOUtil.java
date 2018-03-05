package com.tarena.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import bo.LogData;

/**
 * ������һ�������࣬�����д����
 * �Ѷ�д�߼����������ڸ����е�Ŀ����
 * Ϊ��������Щ�߼���
 * @author Administrator
 *
 */
public class IOUtil {
	/**
	 * �Ӹ������ļ��ж�ȡ��һ���ַ�����������
	 * ת��Ϊһ��longֵ����
	 * @param file
	 * @return
	 */
	public static long readLong(File file){
		BufferedReader br = null;
		try{
			FileInputStream fis
				= new FileInputStream(file);
			InputStreamReader isr
				= new InputStreamReader(fis);
			br = new BufferedReader(isr);
			String line = br.readLine();
			long l = Long.parseLong(line);
			return l;
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			try {
				if(br != null){
					br.close();
				}
			}catch (IOException e) {
			}
		}
	}
	/**
	 * �Ӹ�����RandomAccessFile�ĵ�ǰλ�ô�
	 * ������ȡ�����ֽ�������ת��Ϊ�ַ�����
	 * @param raf
	 * @param len
	 * @return
	 */
	public static String readString(
					RandomAccessFile raf,int len)
										throws IOException{
		byte[] buf = new byte[len];
		raf.read(buf);
		String str 
			= new String(buf,"ISO8859-1");
		return str.trim();
	}
	
	/**
	 * �Ӹ�����RandomAccessFile��ǰλ�ô�
	 * ��ȡһ��intֵ������
	 * @param raf
	 * @return
	 */
	public static int readInt(
								RandomAccessFile raf)
								throws IOException{
		return raf.readInt();
	}
	
	/**
	 * �Ӹ�����RandomAccessFile��ǰλ�ô�
	 * ��ȡһ��shortֵ������
	 * @param raf
	 * @return
	 * @throws IOException
	 */
	public static short readShort(
								RandomAccessFile raf)
								throws IOException{
		return raf.readShort();
	}
	
	/**
	 * �������ļ����е�ÿ��Ԫ�ص�toString
	 * �������ص��ַ�������Ϊһ������д��
	 * �������ļ��С�
	 * @param list
	 * @param file
	 * @throws IOException
	 */
	public static void saveList(
								List list,File file)
								throws IOException{
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(file);
			for(Object o : list){
				pw.println(o);
			}
		}finally{
			if(pw!=null){
				pw.close();
			}
		}
	}
	/**
	 * ��������longֵ��Ϊһ���ַ���д��
	 * �������ļ���
	 * @param l
	 * @param file
	 */
	public static void saveLong(
									long l,File file)
									throws IOException{
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(file);
			pw.println(l);
		}finally{
			if(pw!=null){
				pw.close();
			}
		}									
	}
	
	/**
	 * ��ָ�����ļ��а��ж�ȡÿһ����־����
	 * ת��Ϊһ��LogData�������ս�������־
	 * �������һ��List�����в�����
	 * @param file
	 * @return
	 */
	public static List<LogData>
								loadLogData(File file)
								throws IOException{
		BufferedReader br = null;
		try{
			FileInputStream fis
				= new FileInputStream(file);
			InputStreamReader isr
				= new InputStreamReader(fis);
			br = new BufferedReader(isr);
			List<LogData> list
				= new ArrayList<LogData>();
			String line = null;
			while((line=br.readLine())!=null){
				/*
				 * ��������Ӧ������LogData
				 * ԭ�����ڸ��ַ����ĸ�ʽ����
				 * LogData�����toString������
				 * ���Խ�����ȻҲӦ�ý�������
				 */
				LogData log = new LogData(line);
				list.add(log);
			}
			return list;
		}finally{
			if(br != null){
				br.close();
			}
		}
	}
	
}



