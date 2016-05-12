package main;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.Util;

public class Main {
	static Logger logger = Logger.getLogger(Main.class);
	
	public static void main(String[] args) {
		Connection conn = Util.getConnection("67.207.157.112");
		Statement stmt = null;
		ResultSet rs = null;
		String pathSrc = "/home/enrique/Documentos/batista/src/";
		String pathWeb = "/home/enrique/Documentos/batista/WEB-INF/";
		
		try{
			File file = new File(pathWeb + "/applicationContext.xml");
			if(file.exists()) file.delete();
			stmt = conn.createStatement();
			
			String[] tables = {"","",""};
			for(String table:tables){
				logger.info(rs.getString(1));
				rs = stmt.executeQuery("desc " + table);
				List<String[]> list = new ArrayList<>();
				int i = 0;
				while(rs.next() && i<10){
					i++;
					String[] item = {rs.getString(1),rs.getString(2),rs.getString(4)};
					list.add(item);
				}
				
				Util.createDto(pathSrc,rs.getString(1),list);
				Util.createMapper(pathSrc,rs.getString(1),list);
				Util.createDao(pathSrc,rs.getString(1),list);
				Util.createDaoImpl(pathSrc,rs.getString(1),list);
				Util.createService(pathSrc,rs.getString(1),list);
				Util.createServiceImpl(pathSrc,rs.getString(1),list);
				Util.createController(pathSrc,rs.getString(1),list);
				Util.createJsp(pathWeb,rs.getString(1),list);
				Util.generateBeans(pathWeb,rs.getString(1));
			}
		}catch(Exception e){
			logger.error("",e);
		}
	}
}
