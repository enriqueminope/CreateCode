package util;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class Util {
	static Logger logger = Logger.getLogger(Util.class);
	
	public static Connection getConnection(String host) {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser("frontend");
		dataSource.setPassword("8g6l6T52V73K629");
		dataSource.setServerName(host);
		dataSource.setDatabaseName("efact");
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			logger.error("",e);
		}
		return conn;
	}
	
	public static void createDto(String path,String tableName,List<String[]> list){
		PrintWriter writer;
		String dtoPath = path + "/model/dto/"; 
		createDirectory(dtoPath);
		String className = tableName.substring(tableName.indexOf("_")+1);
		className = className.substring(0,1).toUpperCase() +  className.substring(1);
		String classPath = dtoPath + className + ".java";
		File classFile = new File(classPath);
		if(classFile.exists()) classFile.delete();
		try{
			FileWriter fr = new FileWriter(classPath,true);
			writer = new PrintWriter(fr);
			writer.println("package model.dto;\n");
			writer.println("public class " + className + " {\n");
			
			for(String[] i :list){
				writer.println("\t" +createAttribute(i));
			}
			
			writer.println("\n\tpublic " + className + "() {\n");
			writer.println("\t}\n");
			writer.println("");
			
			for(String[] i :list){
				writer.println(createGetterAndSetter(i));
			}
			writer.println("}");
			writer.close();
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	static void createDirectory(String path){
		String[] pathArray = path.split("[/]");
		String pathTemp=null;
		for(int i =1;i<pathArray.length;i++){
			if(pathTemp==null)
				pathTemp = "/";
			pathTemp += pathArray[i] + "/";
			File f = new File(pathTemp);
			if(!f.exists()){
				f.mkdir();
			}
		}
	}
	
	static String createAttribute(String[] attr){
		String result = null;
		int end = attr[1].indexOf("(")==-1?attr[1].length():attr[1].indexOf("(");
		switch(attr[1].substring(0,end)){
		case "int":
			result = "int \t" + attr[0] + ";";
			break;
		case "varchar":
			result = "String \t" + attr[0] + ";";
			break;
		case "float":
			result = "float \t" + attr[0] + ";";
			break;
		case "double":
			result = "double \t" + attr[0] + ";";
			break;
		case "timestamp":
			result = "Date \t" + attr[0] + ";";
			break;
		}
		return result;
	}
	
	static String createGetterAndSetter(String[] attr){
		String result = null;
		String dataType = null;
		int end = attr[1].indexOf("(")==-1?attr[1].length():attr[1].indexOf("(");
		switch(attr[1].substring(0,end)){
		case "int":
			dataType = "int";
			break;
		case "varchar":
			dataType = "String";
			break;
		case "float":
			dataType = "float";
			break;
		case "double":
			dataType = "double";
			break;
		case "timestamp":
			dataType = "Date";
			break;
		}
		
		String attrNameUpper = attr[0].substring(0, 1).toUpperCase() + attr[0].substring(1); 
		
		result = 	"\tpublic "+ attr[0] + " get" + attrNameUpper + "() {\n" +
						"\t\treturn " + attr[0] + ";\n" +
					"\t}\n\n" +
					"\tpublic void set"+ attrNameUpper + "(" + dataType + " id) { \n" +
					"\t\tthis." + attr[0] + " = " + attr[0] + ";\n" +
					"\t}\n";
		
		return result;
	}

	public static void createMapper(String path, String tableName, List<String[]> list) {
		PrintWriter writer;
		String dtoPath = path + "/data/mapper/"; 
		createDirectory(dtoPath);
		String className = tableName.substring(tableName.indexOf("_")+1);
		className = className.substring(0,1).toUpperCase() +  className.substring(1);
		String classPath = dtoPath + className + ".java";
		File classFile = new File(classPath);
		if(classFile.exists()) classFile.delete();
		try{
			FileWriter fr = new FileWriter(classPath,true);
			writer = new PrintWriter(fr);
			writer.println("package data.mapper;\n");
			writer.println("import org.apache.ibatis.annotations.Select;");
			writer.println("import org.apache.ibatis.annotations.Update;");
			writer.println("import org.apache.ibatis.annotations.Insert;");
			writer.println("import org.apache.ibatis.annotations.Delete;\n");
			writer.println("public interface " + className + "Mapper {\n");
			
			writer.println(createList(tableName,list));
			writer.println(createGet(tableName,list));
			writer.println(createInsert(tableName,list));
			writer.println(createUpdate(tableName,list));
			writer.println(createDelete(tableName,list));
			
			writer.println("}");
			writer.close();
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	public static String createList(String tableName, List<String[]> list) {
		String result = 	"\t@Select(\"select \"\n";
		String className = tableName.substring(tableName.indexOf("_")+1);
		className = className.substring(0,1).toUpperCase() +  className.substring(1);
		for(int i = 0;i<list.size();i++){
			String[] item = list.get(i);
			result += "\t\t\t\"" + item[0];
			if(i<(list.size()-1)) result += ",";
			result +=" \" +\n";
		}
		result += "\t\t\"from " + tableName + "\")\n";
		result += "\tList<" + className + "> list();\n";
		
		return result;
	}
	
	public static String createGet(String tableName, List<String[]> list) {
		String result = 	"\t@Select(\"select \"\n";
		String className = tableName.substring(tableName.indexOf("_")+1);
		className = className.substring(0,1).toUpperCase() +  className.substring(1);
		for(int i = 0;i<list.size();i++){
			String[] item = list.get(i);
			result += "\t\t\t\"" + item[0];
			if(i<(list.size()-1)) result += ",";
			result +=" \" +\n";
		}
		result += 	"\t\t\"from " + tableName + " \"\n" +
					"\t\t\"where " + list.get(0)[0] + " = #{" + list.get(0)[0] + "}\")\n";
		result += "\tList<" + className + "> get(int " + list.get(0)[0] + ");\n";
		
		return result;
	}
	
	public static String createInsert(String tableName, List<String[]> list) {
		String result = 	"\t@Insert(\"insert into " + tableName + " ( \"\n";
		String objectName = tableName.substring(tableName.indexOf("_")+1);
		String className = objectName.substring(0,1).toUpperCase() +  objectName.substring(1);
		for(int i = 1;i<list.size();i++){
			String[] item = list.get(i);
			result += "\t\t\t\"" + item[0];
			if(i<(list.size()-1)) result += ",";
			result +=" \" +\n";
		}
		result += 	"\t\t\")values(\"\n";
		
		for(int i = 1;i<list.size();i++){
			String[] item = list.get(i);
			result += "\t\t\t\"#{" + item[0] + "}";
			if(i<(list.size()-1)) result += ",";
			result +=" \" +\n";
		}
		
		result += 	"\t\t\")\"\n";
		result += "\tint insert(" + className + " " + objectName + ");\n";
		return result;
	}
	
	public static String createUpdate(String tableName, List<String[]> list) {
		String result = 	"\t@Update(\"update " + tableName + " set \"\n";
		String objectName = tableName.substring(tableName.indexOf("_")+1);
		String className = objectName.substring(0,1).toUpperCase() +  objectName.substring(1);
		for(int i = 1;i<list.size();i++){
			String[] item = list.get(i);
			result += "\t\t\t\"" + item[0] + " = #{" + item[0] + "} ";
			if(i<(list.size()-1)) result += ",";
			result +=" \" +\n";
		}
		result += 	"\t\t\"where " + list.get(0)[0] + " = #{" + list.get(0)[0] + "}\")\n";
		
		result += "\tint update(" + className + " " + objectName + ");\n";
		return result;
	}
	
	public static String createDelete(String tableName, List<String[]> list) {
		String result = 	"\t@Delete(\"delete from " + tableName + " where " + list.get(0)[0] + " = #{" + list.get(0)[0] + "}\")\n";
		result += "\tint delete(int " + list.get(0)[0] + ");\n";
		return result;
	}

	public static void createDao(String path,String tableName, List<String[]> list) {
		PrintWriter writer;
		String dtoPath = path + "/model/dao/"; 
		createDirectory(dtoPath);
		String objectName = tableName.substring(tableName.indexOf("_")+1);
		String className = objectName.substring(0,1).toUpperCase() +  objectName.substring(1);
		String classPath = dtoPath + className + "Dao.java";
		File classFile = new File(classPath);
		if(classFile.exists()) classFile.delete();
		try{
			FileWriter fr = new FileWriter(classPath,true);
			writer = new PrintWriter(fr);
			writer.println("package model.dao;\n");
			writer.println("import java.util.List;");
			writer.println("import model.dto." + className + ";\n");
			writer.println("public interface " + className + "Dao {");
			
			writer.println("\tpublic list();");
			writer.println("\tpublic get(int " + list.get(0)[0] + ");");
			writer.println("\tpublic insert(" + className + " " + objectName + ");");
			writer.println("\tpublic update(" + className + " " + objectName + ");");
			writer.println("\tpublic delete(int " + list.get(0)[0] + ");");
			
			writer.println("}");
			writer.close();
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	public static void createDaoImpl(String path,String tableName, List<String[]> list) {
		PrintWriter writer;
		String dtoPath = path + "/model/dao/impl/"; 
		createDirectory(dtoPath);
		String objectName = tableName.substring(tableName.indexOf("_")+1);
		String className = objectName.substring(0,1).toUpperCase() +  objectName.substring(1);
		String classPath = dtoPath + className + "DaoImpl.java";
		File classFile = new File(classPath);
		if(classFile.exists()) classFile.delete();
		try{
			FileWriter fr = new FileWriter(classPath,true);
			writer = new PrintWriter(fr);
			writer.println("package model.dao;\n");			
			writer.println("import data.mapper." + className + "Mapper;");
			writer.println("import java.util.List;");
			writer.println("import model.dao." + className + "Dao;");
			writer.println("import model.dto." + className + ";\n");
			
			writer.println("public class " + className + "DaoImpl implements " + className + "Dao {");
			
			writer.println("\tprivate " + className + "Mapper " + objectName + "Mapper;\n");
			
			writer.println("\tpublic void set" + className + "Mapper(" + className + "Mapper " + objectName + "Mapper) {");
			writer.println("\t\tthis." + objectName + "Mapper = " + " objectName" + "Mapper;");
			writer.println("\t}\n");
			
			writer.println("\t@Override");
			writer.println("\tpublic List<" + className + "> list() {");
			writer.println("\t\treturn " + objectName + "Mapper.list();");
			writer.println("\t}\n");

			writer.println("\t@Override");
			writer.println("\tpublic List<" + className + "> list(int " + list.get(0)[0] + ") {");
			writer.println("\t\treturn " + objectName + "Mapper.get("+ list.get(0)[0] + ");");
			writer.println("\t}\n");
			
			writer.println("\t@Override");
			writer.println("\tpublic String insert(" + className + " " + objectName + ") {");
			writer.println("\t\tString result=\"1\";");
			writer.println("\t\ttry{");
			writer.println("\t\t\t" + objectName + "Mapper.insert(" + objectName + ");");
			writer.println("\t\t}catch(Exception e){");
			writer.println("\t\t\tresult = ex.getMessage();");
			writer.println("\t\t}");
			writer.println("\t\treturn result;");
			writer.println("\t}");
			
			writer.println("\t@Override");
			writer.println("\tpublic String update(" + className + " " + objectName + ") {");
			writer.println("\t\tString result=\"1\";");
			writer.println("\t\ttry{");
			writer.println("\t\t\t" + objectName + "Mapper.update(" + objectName + ");");
			writer.println("\t\t}catch(Exception e){");
			writer.println("\t\t\tresult = ex.getMessage();");
			writer.println("\t\t}");
			writer.println("\t\treturn result;");
			writer.println("\t}");
			
			writer.println("\t@Override");
			writer.println("\tpublic String delete(int " + list.get(0)[0] + ") {");
			writer.println("\t\tString result=\"1\";");
			writer.println("\t\ttry{");
			writer.println("\t\t\t" + objectName + "Mapper.delete(" + list.get(0)[0] + ");");
			writer.println("\t\t}catch(Exception e){");
			writer.println("\t\t\tresult = ex.getMessage();");
			writer.println("\t\t}");
			writer.println("\t\treturn result;");
			writer.println("\t}");
			writer.println("}");
			writer.close();
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	public static void createService(String path,String tableName, List<String[]> list) {
		PrintWriter writer;
		String dtoPath = path + "/service/"; 
		createDirectory(dtoPath);
		String objectName = tableName.substring(tableName.indexOf("_")+1);
		String className = objectName.substring(0,1).toUpperCase() +  objectName.substring(1);
		String classPath = dtoPath + className + "Service.java";
		File classFile = new File(classPath);
		if(classFile.exists()) classFile.delete();
		try{
			FileWriter fr = new FileWriter(classPath,true);
			writer = new PrintWriter(fr);
			writer.println("package service;\n");
			writer.println("import java.util.List;");
			writer.println("import model.dto." + className + ";\n");
			writer.println("public interface " + className + "Service {");
			
			writer.println("\tpublic list();");
			writer.println("\tpublic get(int " + list.get(0)[0] + ");");
			writer.println("\tpublic insert(" + className + " " + objectName + ");");
			writer.println("\tpublic update(" + className + " " + objectName + ");");
			writer.println("\tpublic delete(int " + list.get(0)[0] + ");");
			
			writer.println("}");
			writer.close();
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	public static void createServiceImpl(String path,String tableName, List<String[]> list) {
		PrintWriter writer;
		String dtoPath = path + "/service/impl/"; 
		createDirectory(dtoPath);
		String objectName = tableName.substring(tableName.indexOf("_")+1);
		String className = objectName.substring(0,1).toUpperCase() +  objectName.substring(1);
		String classPath = dtoPath + className + "ServiceImpl.java";
		File classFile = new File(classPath);
		if(classFile.exists()) classFile.delete();
		try{
			FileWriter fr = new FileWriter(classPath,true);
			writer = new PrintWriter(fr);
			writer.println("package model.dao.impl;\n");			
			writer.println("import java.util.List;");
			writer.println("import model.dao.impl." + className + "DaoImpl;");
			writer.println("import model.dto." + className + ";\n");
			
			writer.println("public class " + className + "ServiceImpl implements " + className + "Dao {");
			
			writer.println("\tprivate " + className + "DaoImpl " + objectName + "DaoImpl;\n");
			
			writer.println("\tpublic void set" + className + "DaoImpl(" + className + "DaoImpl " + objectName + "Dao) {");
			writer.println("\t\tthis." + objectName + "DaoImpl = " + " objectName" + "DaoImpl;");
			writer.println("\t}\n");
			
			writer.println("\t@Override");
			writer.println("\tpublic List<" + className + "> list() {");
			writer.println("\t\treturn " + objectName + "DaoImpl.list();");
			writer.println("\t}\n");

			writer.println("\t@Override");
			writer.println("\tpublic List<" + className + "> list(int " + list.get(0)[0] + ") {");
			writer.println("\t\treturn " + objectName + "DaoImpl.get("+ list.get(0)[0] + ");");
			writer.println("\t}\n");
			
			writer.println("\t@Override");
			writer.println("\tpublic String insert(" + className + " " + objectName + ") {");
			writer.println("\t\tString result=\"1\";");
			writer.println("\t\ttry{");
			writer.println("\t\t\t" + objectName + "DaoImpl.insert(" + objectName + ");");
			writer.println("\t\t}catch(Exception e){");
			writer.println("\t\t\tresult = ex.getMessage();");
			writer.println("\t\t}");
			writer.println("\t\treturn result;");
			writer.println("\t}");
			
			writer.println("\t@Override");
			writer.println("\tpublic String update(" + className + " " + objectName + ") {");
			writer.println("\t\tString result=\"1\";");
			writer.println("\t\ttry{");
			writer.println("\t\t\t" + objectName + "DaoImpl.update(" + objectName + ");");
			writer.println("\t\t}catch(Exception e){");
			writer.println("\t\t\tresult = ex.getMessage();");
			writer.println("\t\t}");
			writer.println("\t\treturn result;");
			writer.println("\t}");
			
			writer.println("\t@Override");
			writer.println("\tpublic String delete(int " + list.get(0)[0] + ") {");
			writer.println("\t\tString result=\"1\";");
			writer.println("\t\ttry{");
			writer.println("\t\t\t" + objectName + "DaoImpl.delete(" + list.get(0)[0] + ");");
			writer.println("\t\t}catch(Exception e){");
			writer.println("\t\t\tresult = ex.getMessage();");
			writer.println("\t\t}");
			writer.println("\t\treturn result;");
			writer.println("\t}");
			writer.println("}");
			writer.close();
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	public static void createController(String path,String tableName, List<String[]> list) {
		PrintWriter writer;
		String dtoPath = path + "/controller/"; 
		createDirectory(dtoPath);
		String objectName = tableName.substring(tableName.indexOf("_")+1);
		String className = objectName.substring(0,1).toUpperCase() +  objectName.substring(1);
		String classPath = dtoPath + className + "Controller.java";
		File classFile = new File(classPath);
		if(classFile.exists()) classFile.delete();
		try{
			FileWriter fr = new FileWriter(classPath,true);
			writer = new PrintWriter(fr);
			writer.println("package controller;\n");			
			writer.println("import java.util.List;");
			writer.println("import javax.servlet.http.HttpServletRequest\n");
			writer.println("import model.dto." + className + ";\n");
			writer.println("import org.springframework.stereotype.Controller;");
			writer.println("import org.springframework.web.bind.annotation.RequestMapping;");
			writer.println("import service.impl." + className + "ServiceImpl;\n");
			
			writer.println("@Controller");
			writer.println("public class " + className + "Controller {");
			
			writer.println("\tprivate " + className + "ServiceImpl " + objectName + "ServiceImpl;\n");
			
			writer.println("\tpublic void set" + className + "ServiceImpl(" + className + "ServiceImpl " + objectName + "Dao) {");
			writer.println("\t\tthis." + objectName + "ServiceImpl = " + " objectName" + "ServiceImpl;");
			writer.println("\t}\n");
			
			writer.println("\t@RequestMapping(params = \"action=list\")");
			writer.println("\tpublic ModelAndView list() {");
			writer.println("\t\tModelAndView mav = new ModelAndView(\"" + objectName + "/list\");");
			writer.println("\t\tList<" + className + "> list" + className + " = " + objectName + "ServiceImpl.list();");
			writer.println("\t\tmav.addObject(\"list\", list" + className + ");");
			writer.println("\t\treturn mav;");
			writer.println("\t}\n");

			writer.println("\t@RequestMapping(params = \"action=form_add\")");
			writer.println("\tpublic ModelAndView formAdd() {");
			writer.println("\t\tModelAndView mav = new ModelAndView(\"difficulty/insert\");");
			writer.println("\t\treturn mav;");
			writer.println("\t}\n");
			
			writer.println("\t@RequestMapping(params = \"action=form_upd\")");
			writer.println("\tpublic ModelAndView formAdd(HttpServletRequest request) {");
			writer.println("\t\tModelAndView mav = new ModelAndView(\"" + objectName + "/edit\");");
			writer.println("\t\tString " + objectName + "Id = request.getParameter(\"" + convertSet(list.get(0)) + "\");");
			writer.println("\t\t" + className + " " + objectName +" = " + objectName + "ServiceImpl.get(" + objectName + "Id);");
			writer.println("\t\tmav.addObject(\"objectName\", " + objectName + ");");
			writer.println("\t\treturn mav;");
			writer.println("\t}\n");
			
			writer.println("\t@RequestMapping(params = \"action=insert\")");
			writer.println("\tpublic ModelAndView formAdd(HttpServletRequest request) {");
			
			for(int i = 1;i<list.size();i++){
				String[] item = list.get(i);
				String attrUpper = item[0].substring(0, 1).toUpperCase() + item[0].substring(1);
				writer.println("\t\tString " + item[0] + " = request.getParameter(\"" + convertToParameter(item[0]) + "\");");
			}
			
			writer.println("\t\tModelAndView mav = new ModelAndView(\"result\");");
			writer.println("\t\t" + className + " " + objectName + " = new " + className + "();");
			
			for(int i = 1;i<list.size();i++){
				String[] item = list.get(i);
				String attrUpper = item[0].substring(0, 1).toUpperCase() + item[0].substring(1);
				writer.println("\t\t" + objectName + ".set" + attrUpper + "(" + convertSet(item) + ");");
			}
			writer.println("\t\tString result = " + objectName + "ServiceImpl.insert(" + objectName + ");");
			writer.println("\t\tmav.addObject(\"result\",result);");
			writer.println("\t\treturn mav;");
			writer.println("\t}\n");
			
			writer.println("\t@RequestMapping(params = \"action=upd\")");
			writer.println("\tpublic ModelAndView formAdd(HttpServletRequest request) {");
			
			for(int i = 0;i<list.size();i++){
				String[] item = list.get(i);
				String attrUpper = item[0].substring(0, 1).toUpperCase() + item[0].substring(1);
				writer.println("\t\tString " + item[0] + " = request.getParameter(\"" + convertToParameter(item[0]) + "\");");
			}
			
			writer.println("\t\tModelAndView mav = new ModelAndView(\"result\");");
			writer.println("\t\t" + className + " " + objectName + " = new " + className + "();");
			
			for(int i = 0;i<list.size();i++){
				String[] item = list.get(i);
				String attrUpper = item[0].substring(0, 1).toUpperCase() + item[0].substring(1);
				writer.println("\t\t" + objectName + ".set" + attrUpper + "(" + convertSet(item) + ");");
			}
			writer.println("\t\tString result = " + objectName + "ServiceImpl.update(" + objectName + ");");
			writer.println("\t\tmav.addObject(\"result\",result);");
			writer.println("\t\treturn mav;");
			writer.println("\t}\n");
			
			writer.println("\t@RequestMapping(params = \"action=delete\")");
			writer.println("\tpublic ModelAndView formAdd(HttpServletRequest request) {");
			writer.println("\t\tModelAndView mav = new ModelAndView(\"result\");");
			writer.println("\t\tString " + list.get(0)[0] + " = request.getParameter(\"" + convertToParameter(list.get(0)[0]) + "\");");
			writer.println("\t\tString result = " + objectName + "ServiceImpl.delete(Integer.parseInt(" + list.get(0)[0] + "));");
			writer.println("\t\tmav.addObject(\"result\", result);");
			writer.println("\t\treturn mav;");
			writer.println("\t}\n");
			
			writer.println("}");
			writer.close();
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	static String convertToParameter(String variable){
		String result = "";
		char[] variableArray = variable.toCharArray();
		for(char c:variableArray){
			int ascii = (int)c;
			String tmp = "" + c;
			if(ascii>64 && ascii<91){
				result +="_";
				tmp = tmp.toLowerCase();
			}
			result+=tmp;
		}
		return result;
	}
	
	static String convertSet(String[] attr){
		String result = "";
		int end = attr[1].indexOf("(")==-1?attr[1].length():attr[1].indexOf("(");
		switch(attr[1].substring(0,end)){
		case "int":
			result = "Integer.parseInt(" + attr[0] + ")";
			break;
		case "varchar":
			result = attr[0];
			break;
		case "float":
			result = "Float.parseFloat(" + attr[0] + ")";
			break;
		case "double":
			result = "Double.parseDouble(" + attr[0] + ")";
			break;
		case "timestamp":
			result = "new Date(" + attr[0] + ".split(\"[-]\")[0]," + attr[0] + ".split(\"[-]\")[1]," + attr[0] + ".split(\"[-]\")[2])";
			break;
		}
		return result;
	}

	public static void createJsp(String path, String tableName, List<String[]> list) {
		PrintWriter writer;
		FileWriter fr;
		String objectName = tableName.substring(tableName.indexOf("_")+1);
		String directoryPath = path + "/jsp/" + objectName + "/"; 
		int listField = 3;
		String jspPath = null;
		createDirectory(directoryPath);
		String className = objectName.substring(0,1).toUpperCase() +  objectName.substring(1);
		try{
			
			/*List*/
			jspPath = directoryPath + "list.jsp";
			File classFile = new File(jspPath);
			if(classFile.exists()) classFile.delete();
			fr = new FileWriter(jspPath,true);
			writer = new PrintWriter(fr);
			writer.println("<%@page contentType=\"text/html\" pageEncoding=\"UTF-8\"%>");
			writer.println("<%@ taglib uri=\"http://java.sun.com/jsp/jstl/core\" prefix=\"c\" %>");
			writer.println("<!DOCTYPE html>");
			writer.println("<h3>" + className + "</h3>");
			writer.println("<div class=\"row\">");
			writer.println("\t<div class=\"col-lg-2\"></div>");
			writer.println("\t<div class=\"col-lg-8\">");
			writer.println("\t\t<table class=\"table table-bordered table-striped\">");
			writer.println("\t\t\t<tr>");
			
			for(int i = 1;i<listField;i++){
				String[] item = list.get(i);
				String attrUpper = item[0].substring(0, 1).toUpperCase() + item[0].substring(1);
				writer.println("\t\t\t\t<th>" + attrUpper + "</th>");
			}
			
			writer.println("\t\t\t\t<th style=\"text-align: center\">");
			writer.println("\t\t\t\t\t<button type=\"button\" class=\"btn btn-default edit-difficulty\" id=\"add-" + objectName + "\" title=\"agregar\">");
			writer.println("\t\t\t\t\t\t<span class=\"glyphicon glyphicon-plus\"/>");
			writer.println("\t\t\t\t\t</button>");
			writer.println("\t\t\t\t</th>");
			writer.println("\t\t\t</tr>");
			writer.println("\t\t\t<c:forEach items=\"${list}\" var=\"list\">");
			writer.println("\t\t\t\t<tr id=\"${list." + list.get(0)[0] + "}\">");
			
			for(int i = 1;i<listField;i++){
				String[] item = list.get(i);
				writer.println("\t\t\t\t\t<td>${list." + item[0] + "}</td>");
			}
			writer.println("\t\t\t\t\t<td align=\"center\">");
			writer.println("\t\t\t\t\t\t<button type=\"button\" class=\"btn btn-default edit-" + objectName + "\" title=\"editar\">");
			writer.println("\t\t\t\t\t\t\t<span class=\"glyphicon glyphicon-edit\"/>");
			writer.println("\t\t\t\t\t\t</button>");
			writer.println("\t\t\t\t\t</td>");
			writer.println("\t\t\t\t</tr>");
			writer.println("\t\t\t</c:forEach>");
			writer.println("\t\t</table>");
			writer.println("\t</div>");
			writer.println("\t<div class=\"col-lg-2\"></div>");
			writer.println("</div>");
			writer.println("");
			
			writer.println("<script>");
			writer.println("\tvar myModalId='myModalSm';");
			writer.println("\t$(function(){");
			writer.println("\t\t$('#add-difficulty').click(function(){");
			writer.println("\t\t\t$('#' + myModalId + ' .modal-title').html('Agregar');");
			writer.println("\t\t\t$.post('difficulty.htm',{action:'form_add'},function(data){");
			writer.println("\t\t\t\t$('#' + myModalId + ' .modal-body').html(data);");
			writer.println("\t\t\t\t$('#' + myModalId).modal('show');");
			writer.println("\t\t\t});");
			writer.println("\t\t});");
		        
			writer.println("\t\t$('.edit-difficulty').click(function(){");
			writer.println("\t\t\t$('#' + myModalId + ' .modal-title').html('Editar');");
			writer.println("\t\t\t$.post('difficulty.htm',{action:'form_upd',difficulty_id:$(this).parent().parent().attr('id')},function(data){");
			writer.println("\t\t\t\t$('#' + myModalId + ' .modal-body').html(data);");
			writer.println("\t\t\t\t$('#' + myModalId +).modal('show');");
			writer.println("\t\t\t}); ");
			writer.println("\t\t});");
			writer.println("\t});");
			writer.println("</script>");
			writer.close();
			
			/*Form Insert*/
			jspPath = directoryPath + "insert.jsp";
			classFile = new File(jspPath);
			if(classFile.exists()) classFile.delete();
			fr = new FileWriter(jspPath,true);
			writer = new PrintWriter(fr);
			writer.println("<%@page contentType=\"text/html\" pageEncoding=\"UTF-8\"%>");
			writer.println("<%@ taglib uri=\"http://java.sun.com/jsp/jstl/core\" prefix=\"c\" %>");
			writer.println("<!DOCTYPE html>");
			writer.println("<form class=\"form-horizontal\">");
			writer.println("\t<input type=\"hidden\" name=\"action\" value=\"insert\"/>");
			
			for(int i = 1;i<list.size();i++){
				String[] item = list.get(i);
				String attrUpper = item[0].substring(0, 1).toUpperCase() + item[0].substring(1);
				writer.println("\t<div class=\"form-group\">");
				writer.println("\t\t<label for=\"" + item[0] + "\" class=\"col-sm-4 control-label\">" + attrUpper + "</label>");
				writer.println("\t\t<div class=\"col-sm-8\">");
				writer.println("\t\t\t<input type=\"text\" class=\"form-control\" name=\"" + convertToParameter(item[0]) + "\" id=\"" + item[0] + "\" placeholder=\"" + attrUpper + "\">");
				writer.println("\t\t</div>");
				writer.println("\t</div>");
			}
			
			writer.println("\t<div class=\"alert\"/>");
			writer.println("</form>");
			
			    
			writer.println("<script>");
			writer.println("\tvar controller = '" + objectName + ".htm';");
			writer.println("\tvar modalId = 'myModalSm';");
			writer.println("\t$(function(){");
			writer.println("\t\t$('#' + modalId + ' .btn-primary').unbind('click').click(function(){");
			writer.println("\t\t\t$.post(controller,$('#' + modalId + ' .form-horizontal').serialize(),function(data){");
			writer.println("\t\t\t\tif(data==1){");
			writer.println("\t\t\t\t\t$('#' + modalId + ' .alert').addClass('alert-success').html('Se agrego el nuevo registro');"); 
			writer.println("\t\t\t\t$.post(controller,{action:'list'},function(data){");
			writer.println("\t\t\t\t\t$('#mainContainer').html(data);");
			writer.println("\t\t\t\t\t\t$('#' + modalId).modal('hide');");
			writer.println("\t\t\t\t\t});");
			writer.println("\t\t\t\t}else{");
			writer.println("\t\t\t\t\t$('#myModal .alert').addClass('alert-danger').html(data);");
			writer.println("\t\t\t\t}");
			writer.println("\t\t\t});");
			writer.println("\t\t});");
			writer.println("\t});");
			writer.println("</script>");
			writer.close();
			
			/*Form Update*/
			jspPath = directoryPath + "edit.jsp";
			classFile = new File(jspPath);
			if(classFile.exists()) classFile.delete();
			fr = new FileWriter(jspPath,true);
			writer = new PrintWriter(fr);
			writer.println("<%@page contentType=\"text/html\" pageEncoding=\"UTF-8\"%>");
			writer.println("<%@ taglib uri=\"http://java.sun.com/jsp/jstl/core\" prefix=\"c\" %>");
			writer.println("<!DOCTYPE html>");
			writer.println("<form class=\"form-horizontal\">");
			writer.println("\t<input type=\"hidden\" name=\"action\" value=\"upd\"/>");
			writer.println("\t<input type=\"hidden\" name=\"" + list.get(0)[0] + "\" value=\"${" + objectName + "." + list.get(0)[0] + "}\"/>");
			
			for(int i = 1;i<list.size();i++){
				String[] item = list.get(i);
				String attrUpper = item[0].substring(0, 1).toUpperCase() + item[0].substring(1);
				writer.println("\t<div class=\"form-group\">");
				writer.println("\t\t<label for=\"" + item[0] + "\" class=\"col-sm-4 control-label\">" + attrUpper + "</label>");
				writer.println("\t\t<div class=\"col-sm-8\">");
				writer.println("\t\t\t<input type=\"text\" class=\"form-control\" name=\"" + convertToParameter(item[0]) + "\" id=\"" + item[0] + "\" value=\"${" + objectName + "." + item[0] + "}\" placeholder=\"" + attrUpper + "\">");
				writer.println("\t\t</div>");
				writer.println("\t</div>");
			}
			
			writer.println("\t<div class=\"alert\"/>");
			writer.println("</form>");
			
			    
			writer.println("<script>");
			writer.println("\tvar controller = '" + objectName + ".htm';");
			writer.println("\tvar modalId = 'myModalSm';");
			writer.println("\t$(function(){");
			writer.println("\t\t$('#' + modalId + ' .btn-primary').unbind('click').click(function(){");
			writer.println("\t\t\t$.post(controller,$('#' + modalId + ' .form-horizontal').serialize(),function(data){");
			writer.println("\t\t\t\tif(data==1){");
			writer.println("\t\t\t\t\t$('#' + modalId + ' .alert').addClass('alert-success').html('El registro ha sido actualizado');"); 
			writer.println("\t\t\t\t$.post(controller,{action:'list'},function(data){");
			writer.println("\t\t\t\t\t$('#mainContainer').html(data);");
			writer.println("\t\t\t\t\t\t$('#' + modalId).modal('hide');");
			writer.println("\t\t\t\t\t});");
			writer.println("\t\t\t\t}else{");
			writer.println("\t\t\t\t\t$('#myModal .alert').addClass('alert-danger').html(data);");
			writer.println("\t\t\t\t}");
			writer.println("\t\t\t});");
			writer.println("\t\t});");
			writer.println("\t});");
			writer.println("</script>");
			writer.close();
			
		}catch(Exception e){
			logger.error("",e);
		}
		
	}

	public static void generateBeans(String path, String tableName) {
		PrintWriter writer;
		createDirectory(path);
		String objectName = tableName.substring(tableName.indexOf("_")+1);
		String className = objectName.substring(0,1).toUpperCase() +  objectName.substring(1);
		String classPath = path + "applicationContext.xml";
		try{
			FileWriter fr = new FileWriter(classPath,true);
			writer = new PrintWriter(fr);
			writer.println("\t<!-- " + className + " -->");
			writer.println("\t<bean class=\"org.mybatis.spring.mapper.MapperFactoryBean\" id=\"" + objectName + "Mapper\">");
			writer.println("\t\t<property name=\"mapperInterface\" value=\"data.mapper." + className + "Mapper\"/>");
			writer.println("\t\t<property name=\"sqlSessionFactory\" ref=\"sqlSessionFactory\"/>");
			writer.println("\t</bean>\n");
    
			writer.println("\t<bean class=\"model.dao.impl." + className + "DaoImpl\" id=\"" + objectName + "DaoImpl\">");
			writer.println("\t\t<property name=\"" + objectName + "Mapper\" ref=\"" + objectName + "Mapper\"/>");
			writer.println("\t</bean>\n");
    
			writer.println("\t<bean class=\"service.impl." + className + "ServiceImpl\" id=\"" + objectName + "ServiceImpl\">");
			writer.println("\t\t<property name=\"" + objectName + "DaoImpl\" ref=\"" + objectName + "DaoImpl\"/>");
			writer.println("\t</bean>\n");
			
			writer.println("\t<bean class=\"controller." + className + "StatusController\" id=\"" + objectName + "StatusController\">");
			writer.println("\t\t<property name=\"" + objectName + "ServiceImpl\" ref=\"" + objectName + "ServiceImpl\"/>");
			writer.println("\t</bean>");
			
			writer.close();
		}catch(Exception e){
			logger.error("",e);
		}
	}
}
