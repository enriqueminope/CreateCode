package main;

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
		Statement stmt2 = null;
		ResultSet rs2 = null;
		String pathSrc = "/home/enrique/Documentos/batista/src/";
		String pathJsp = "/home/enrique/Documentos/batista/jsp/";
		
		try{
			stmt = conn.createStatement();
			stmt2 = conn.createStatement();
			rs = stmt.executeQuery("SHOW FULL TABLES FROM efact");
			if(rs.next()){
				logger.info(rs.getString(1));
				rs2 = stmt2.executeQuery("desc " + rs.getString(1));
				List<String[]> list = new ArrayList<>();
				int i = 0;
				while(rs2.next() && i<10){
					i++;
					String[] item = {rs2.getString(1),rs2.getString(2),rs2.getString(4)};
					list.add(item);
				}
				
				Util.createDto(pathSrc,rs.getString(1),list);
				Util.createMapper(pathSrc,rs.getString(1),list);
				Util.createDao(pathSrc,rs.getString(1),list);
				Util.createDaoImpl(pathSrc,rs.getString(1),list);
				Util.createService(pathSrc,rs.getString(1),list);
				Util.createServiceImpl(pathSrc,rs.getString(1),list);
				Util.createController(pathSrc,rs.getString(1),list);
				Util.createJsp(pathJsp,rs.getString(1),list);
			}
		}catch(Exception e){
			logger.error("",e);
		}
	}
}
