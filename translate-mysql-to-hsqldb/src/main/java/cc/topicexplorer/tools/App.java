package cc.topicexplorer.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    Connection conn;
	static FileWriter outp;
	static int count=0;
	static int print = 0;
	static String output;
    
    public App(String db_file_name_prefix) throws Exception {
        Class.forName("org.hsqldb.jdbc.JDBCDriver");
        conn = DriverManager.getConnection("jdbc:hsqldb:"+ db_file_name_prefix,"SA","");                      // password
    }

    public void shutdown() throws SQLException {
        Statement st = conn.createStatement();
        st.execute("SHUTDOWN");
        conn.close();
    }

    public synchronized void query(String expression) throws SQLException {

    	try {
			outp = new FileWriter(output/*"/data/vishal/db_r1/current_k.txt"*/,true);
			outp.write(expression+"\n");
			outp.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        Statement st = null;
        ResultSet rs = null;

        st = conn.createStatement();
        rs = st.executeQuery(expression);
        if(rs==null) System.out.println("rs is null");
        //else System.out.println(rs.last());
        dump(rs);
        st.close();
    }

    public synchronized void update(String expression) throws SQLException {

    	try {
			outp = new FileWriter(output/*"/data/vishal/db_r1/current_k.txt"*/,true);
			outp.write(expression+"\n");
			outp.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
        Statement st = null;
        st = conn.createStatement();    // statements
        int i = st.executeUpdate(expression);    // run the query
        if (i == -1) {
            System.out.println("db error : " + expression);
        }
        st.close();
    }    // void update()

    public static void dump(ResultSet rs) throws SQLException {
        ResultSetMetaData meta   = rs.getMetaData();
        int               colmax = meta.getColumnCount();
        int               i;
        Object            o = null;

        for (; rs.next(); ) {
            for (i = 0; i < colmax; ++i) {
                o = rs.getObject(i + 1);    // Is SQL the first column is indexed

                // with 1 not 0
                if(o!=null) {
                	byte[] array = null;
					try {
						array = o.toString().getBytes("UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	String s = new String(array, Charset.forName("UTF-8"));
                	System.out.println(s);
                	//System.out.print(o.toString() + " ");
                }
            }

            System.out.println(" ");
        }
    }                                       //void dump( ResultSet rs )

    public static void main(String[] args) {
    	//args: database input output logfile
    	
    	output = args[2]; //output file
        App db = null;
        try {
            db = new App(args[0]/*"/data/vishal/db_runtime/time"*/);
        } catch (Exception ex1) {
            ex1.printStackTrace();    // could not start db

            return;                   // bye bye
        }
        
        InputStream inputfile;
        InputStreamReader isr;
        BufferedReader inputBufferedReader=null;
        FileWriter logFile = null;
        String line;
        int k=0;
       
        try {
			inputfile = new FileInputStream(args[1]/*"/data/vishal/Desktop/jpqueries.txt"*/);
			isr = new InputStreamReader(inputfile, Charset.forName("UTF-8"));
	        inputBufferedReader = new BufferedReader(isr);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        line="";
        k=0;
        long time = System.nanoTime(); 
        //if(1==2)
        try{
        	while((line=inputBufferedReader.readLine())!=null){
        		double timenow = ( System.nanoTime() - time ) / 1e6;
        		try {
        			logFile = new FileWriter(args[3]/*"/data/vishal/db_r1/current_k.txt"*/,true);
        		} catch (IOException e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
        		logFile.write("time: "+timenow+"\n"+"error count: "+count+"\n"+k+"\n"+line.substring(0,(200<line.length()?60:line.length()))+"\n");
        		logFile.close();
        		/*Pattern pat = Pattern.compile("^\\s*SELECT");
        		Matcher mat = pat.matcher(line);
        		if(mat.find()) {k++;continue;};*/
        		
        		//System.out.println(line);
        		System.out.println(k+" "+line);
        		int flag = 0;
        		if(line.contains("TABLES")){
        			flag=101;
        			System.out.println("flag="+flag);
        			line = line.replace("TABLES", "TABLE");
        		}
        		if(line.contains("UNLOCK")){
        			Pattern pattern = Pattern.compile("^\\s*UNLOCK\\s*TABLE\\s*");
        			Matcher m = pattern.matcher(line);
        			line=m.replaceAll("COMMIT");
        		}
        		while(line.contains("ENGINE")){
        			flag=1;
        			System.out.println("flag="+flag);
        			//System.out.println("CONTAINS ENGINE");
        			int en = line.indexOf("ENGINE");
        			//System.out.println("en: "+en);
        			line = line.substring(0, en)+";";
        			//System.out.println("New query: "+line);
        		}
        		if(line.contains("\'")){
        			line = line.replaceAll("\\\\'", "''");
        			flag=101;
        			System.out.println("flag="+flag);
        		}
        		while(line.contains("INTEGER(")){
        			//System.out.println("Yes");
        			Pattern pattern = Pattern.compile("INTEGER[(]\\d+[)]");
        			Matcher m = pattern.matcher(line);
        			line = m.replaceAll("INTEGER");
        			flag=2;
        			System.out.println("flag="+flag);
        			//System.out.println(line);
        		}
        		if(line.contains("ADD")){
        			//System.out.println("Yes");
        			flag=201;
        			System.out.println("flag="+flag);
        			Pattern pattern = Pattern.compile("ADD\\s+KEY");
        			Matcher m = pattern.matcher(line);
        			if(m.find()) line=m.replaceAll("ADD INDEX");
        			Pattern pattern1 = Pattern.compile("ADD\\s+UNIQUE\\s+KEY");
        			Matcher m1 = pattern1.matcher(line);
        			if(m1.find()) line=m1.replaceAll("ADD UNIQUE INDEX");
        			Pattern pattern2 = Pattern.compile("ADD\\s+FULLTEXT\\s+KEY");
        			Matcher m2 = pattern2.matcher(line);
        			if(m2.find()) line=m2.replaceAll("ADD INDEX");
        		}
        		while(line.contains("int(")){
        			//System.out.println("Yes");
        			Pattern pattern = Pattern.compile("(int)[(](\\d+)[)]");
        			Matcher m = pattern.matcher(line);
        			//if(m.find()) System.out.println(m.group(0)+" "+m.group(1)+" "+m.group(2)+"\n");
        			line = m.replaceAll("int");
        			flag=4;
        			System.out.println("flag="+flag);
        			//System.out.println(line);
        		}
        		while(line.contains("INT(")){
        			//System.out.println("Yes");
        			Pattern pattern = Pattern.compile("(INT)[(](\\d+)[)]");
        			Matcher m = pattern.matcher(line);
        			//if(m.find()) System.out.println(m.group(0)+" "+m.group(1)+" "+m.group(2)+"\n");
        			line = m.replaceAll("INT");
        			flag=5;
        			System.out.println("flag="+flag);
        			//System.out.println(line);
        		}
        		while(line.contains("`")){
        			flag=6;
        			System.out.println("flag="+flag);
        			int b = line.indexOf("`");
        			line = line.substring(0, b)+line.substring(b+1, line.length());
        			//System.out.println(line);
        		}
        		while(line.contains(" text,") || line.contains(" Text,")){
        			Pattern pattern = Pattern.compile(" text,| Text,");
        			Matcher m = pattern.matcher(line);
        			line = m.replaceAll(" VARCHAR(65536),");
        			flag=7;
        			System.out.println("flag="+flag);
        			System.out.println(line);
        			/*flag=1;
        			int b = line.indexOf(" text");
        			line = line.substring(0, b)+ " VARCHAR(65536) " +line.substring(b+5, line.length());*/
        		}
        		while(line.contains(" text)") || line.contains(" Text)")){
        			Pattern pattern = Pattern.compile(" text[)]| Text[)]");
        			Matcher m = pattern.matcher(line);
        			line = m.replaceAll(" VARCHAR(65536))");
        			flag=8;
        			System.out.println("flag="+flag);
        			System.out.println(line);
        			/*flag=1;
        			int b = line.indexOf(" text");
        			line = line.substring(0, b)+ " VARCHAR(65536) " +line.substring(b+5, line.length());*/
        		}
        		if(line.contains("TEXT") || line.contains("text") || line.contains("Text")){
        			Pattern pattern = Pattern.compile(" text$| Text$ | TEXT$");
        			Matcher m = pattern.matcher(line);
        			line = m.replaceAll(" VARCHAR(65536)");
        			flag=9;
        			System.out.println("flag="+flag);
        		}
        		while(line.contains(" mediumtext,") || line.contains(" Mediumtext,") || line.contains(" MEDIUMTEXT,")){
        			Pattern pattern = Pattern.compile(" mediumtext,| Mediumtext,|MEDIUMTEXT,");
        			Matcher m = pattern.matcher(line);
        			line = m.replaceAll(" VARCHAR(65536),");
        			flag=71;
        			System.out.println("flag="+flag);
        			System.out.println(line);
        			/*flag=1;
        			int b = line.indexOf(" text");
        			line = line.substring(0, b)+ " VARCHAR(65536) " +line.substring(b+5, line.length());*/
        		}
        		while(line.contains(" mediumtext)") || line.contains(" Mediumtext)")){
        			Pattern pattern = Pattern.compile(" mediumtext[)]| Mediumtext[)]");
        			Matcher m = pattern.matcher(line);
        			line = m.replaceAll(" VARCHAR(65536))");
        			flag=81;
        			System.out.println("flag="+flag);
        			System.out.println(line);
        			/*flag=1;
        			int b = line.indexOf(" text");
        			line = line.substring(0, b)+ " VARCHAR(65536) " +line.substring(b+5, line.length());*/
        		}
        		if(line.contains("MEDIUMTEXT") || line.contains("mediumtext") || line.contains("Mediumtext")){
        			Pattern pattern = Pattern.compile(" mediumtext$| Mediumtext$ | MEDIUMTEXT$");
        			Matcher m = pattern.matcher(line);
        			line = m.replaceAll(" VARCHAR(65536)");
        			flag=91;
        			System.out.println("flag="+flag);
        		}
        		while(line.contains("KEY") && (!line.contains("PRIMARY KEY") && !line.contains("FOREIGN KEY") )){
        			Pattern pattern = Pattern.compile("(,\\s*KEY\\s+(\\w+\\s*)[(]\\s*(\\w+)\\s*[)])");
        			Matcher m = pattern.matcher(line);
        			String qs = "" ;
        			String table = "";
        			Pattern p1 = Pattern.compile("ALTER\\s+TABLE\\s+(\\s*\\w+\\s*)");
        			Matcher m1 = p1.matcher(line);
        			Pattern p2 = Pattern.compile("ALTER\\s+IGNORE\\s+TABLE\\s+(\\s*\\w+\\s*)");
        			Matcher m2 = p2.matcher(line);
        			if(m1.find()) table = m1.group(1);
        			else if(m2.find()) table = m2.group(1);
        			while(m.find()){
        				line=m.replaceFirst("");
        				for(int i=0;i<=m.groupCount();i++) System.out.println(i+" "+m.group(i));
        				
        				qs= "CREATE INDEX "+m.group(2)+" ON "+table+" ("+m.group(3)+")";
        				System.out.println("Executing query: "+qs);
        				db.update(qs);
        			}
        			System.out.println(line);
        			int b = line.indexOf(" KEY ");
        			line = line.substring(0, b) + " PRIMARY KEY " + line.substring(b+5, line.length());
        			flag=10;
        			System.out.println("flag="+flag);
        		}
        		while(line.contains(" auto_increment")){
        			flag=11;
        			System.out.println("flag="+flag);
        			int b = line.indexOf(" auto_increment");
        			line = line.substring(0, b)+ " IDENTITY " +line.substring(b+15, line.length());
        		}
        		while(line.contains(" NOT NULL DEFAULT '0'")){
        			//System.out.println("nnd\n");
        			Pattern pattern = Pattern.compile(" NOT NULL DEFAULT '0'");
        			Matcher m = pattern.matcher(line);
        			//if(m.find())System.out.println(m.group(0) +"**"+m.group(1));
        			line = m.replaceAll(" DEFAULT '0' NOT NULL");
        			flag=12;
        			System.out.println("flag="+flag);
        			System.out.println(line);
        		}
        		if(line.contains("$")){
        			flag=13;
        			System.out.println("flag="+flag);
        			Pattern pattern = Pattern.compile("\\s(\\w+\\$\\w+)");
        			Matcher m = pattern.matcher(line);
        			while(m.find()){
        				//System.out.println(m.group(1));
        				line=line.replace(m.group(1), " \""+m.group(1)+"\" ");
        			}
        		}
        		if(line.contains("$")){
        			flag=14;
        			System.out.println("flag="+flag);
        			Pattern pattern = Pattern.compile("(\\w+)(\\.\\w+\\$\\w+)");
        			Matcher m = pattern.matcher(line);
        			while(m.find()){
        				//System.out.println(m.group(1));
        				if(line.contains("ADD") || line.contains("DROP")){
        					line=line.replace(m.group(1)+".", "");
        					System.out.println("Now: "+line);
        					if(line.contains("$")){
        	        			flag=1;
        	        			Pattern pattern1 = Pattern.compile("\\s(\\w+\\$\\w+)");
        	        			Matcher m1 = pattern1.matcher(line);
        	        			while(m1.find()){
        	        				//System.out.println(m.group(1));
        	        				line=line.replace(m1.group(1), " \""+m1.group(1)+"\" ");
        	        			}
        	        		}
        				}
        			}
        		}
        		if(line.contains(".")){
        			//System.out.println("DOT");
        			Pattern pattern = Pattern.compile("\\s+(\\w+)\\s*[(]\\s*(\\w+)[.](\\w+)");
        			Matcher m = pattern.matcher(line);
        			/*Pattern pattern1 = Pattern.compile("\\s+(\\w+[.]\\w+\\s)");
        			Matcher m1 = pattern1.matcher(line);*/
        			while(m.find() /*&& m.group(1)==m.group(2)*/){
        				if(m.group(1).equals(m.group(2))){
                			flag=15;
                			System.out.println("flag="+flag);
        				System.out.println(m.group(1)+" "+m.group(2));
        				System.out.println(line);
        				if(line.contains("("+m.group(1)+".")) line=line.replace("("+m.group(1)+".", "(");
        				System.out.println(line);
        				if(line.contains("( "+m.group(1)+".")) line=line.replace("( "+m.group(1)+".", "( ");
        				System.out.println(line);
        				if(line.contains(","+m.group(1)+".")) line=line.replace(","+m.group(1)+".", ",");
        				System.out.println(line);
        				if(line.contains(", "+m.group(1)+".")) line=line.replace(", "+m.group(1)+".", ", ");
        				System.out.println(line);
        				if(line.contains(",  "+m.group(1)+".")) line=line.replace(",  "+m.group(1)+".", ", ");
        				System.out.println(line);
        				}
        			}
        		}
        		if(line.contains("IGNORE")){
        			flag=16;
        			System.out.println("flag="+flag);
        			Pattern pattern = Pattern.compile("\\s+IGNORE\\s*");
        			Matcher m = pattern.matcher(line);
        			line=m.replaceAll(" ");
        			System.out.println(line);
        		}
        		if(line.contains("log")){
        			flag=1601;
        			System.out.println("flag="+flag);
        			Pattern pattern = Pattern.compile("log\\s*[(]\\s*10\\s*(,)");
        			Matcher m = pattern.matcher(line);
        			if(m.find())
        				line=m.replaceAll("log(");
        		}
        		if(line.contains("ADD") || line.contains("DROP")){
        			//System.out.println("YES");
        			Pattern pattern = Pattern.compile("ADD(.*),(\\s*)ADD");
        			Matcher m = pattern.matcher(line);
        			Pattern pattern4 = Pattern.compile("ADD(.*),(\\s*)DROP");
        			Matcher m4 = pattern4.matcher(line);
        			Pattern pattern2 = Pattern.compile("DROP(.*),(\\s*)ADD");
        			Matcher m2 = pattern2.matcher(line);
        			Pattern pattern3 = Pattern.compile("DROP(.*),(\\s*)DROP");
        			Matcher m3 = pattern3.matcher(line);
        			if(m.find() || m2.find() || m3.find() || m4.find()){
        				Pattern pattern1 = Pattern.compile("ALTER TABLE(\\s+)(\\w+)(\\s+)");
            			Matcher m1 = pattern1.matcher(line);
        				String table="";
            			if(m1.find()) table=m1.group(2);
            			//System.out.println("table: "+table);
        				//System.out.println("Found");
        				String[] commands = null ;
        				int f=0;
        				if(line.contains(" INDEX ")) {f=1;commands = line.split("\\)");}
        				else {f=2;commands = line.split(",");}
        				//System.out.println(commands.length);
        				if(f==2){
        				for(int i=0;i<commands.length;i++){
        					if(i==0){	
        						System.out.println("Executing Sub-command " +k+"."+(i+1)+": "+commands[i]);
        						try{
        	        				db.update(commands[i]);
        	        			}catch(SQLException e){
        	        				e.printStackTrace();
        	        			}
        					}
        					else{
        						System.out.println("Executing Sub-command " +k+"."+(i+1)+": "+"ALTER TABLE "+table+commands[i]);
        						try{
        	        				db.update("ALTER TABLE "+table+commands[i]);
        	        			}catch(SQLException e){
        	        				e.printStackTrace();
        	        			}
        					}
        				}
        			}
        				else{
        					System.out.println("Command till now: "+line);
        					for(int i=0;i<commands.length;i++)
        						System.out.println(i+" "+commands[i]);
        					System.out.println();
        					for(int i=0;i<commands.length;i++){
        						if(commands[i].length()<6) continue;
        						if(i==0){
        							commands[i] += ")";
        							Pattern patterns = Pattern.compile("\\s*ADD\\s+INDEX\\s(.+)[(](.+)[)]");
        							Matcher ms = patterns.matcher(commands[i]);
        							Pattern patterns1 = Pattern.compile("\\s*ADD\\s+UNIQUE\\s+INDEX\\s(.+)[(](.+)[)]");
        							Matcher ms1 = patterns1.matcher(commands[i]);
        							if(ms.find()) {commands[i] = "CREATE INDEX " + ms.group(1) + " ON " + table + "(" + ms.group(2) + ")";}
        							else if(ms1.find()) {
        								commands[i] = "CREATE UNIQUE INDEX " + ms1.group(1) + " ON " + table + "(" + ms1.group(2) + ")";
        							}
        							System.out.println("Executing Sub-command " +k+"."+(i+1)+": "+commands[i]);
            						try{
            	        				db.update(commands[i]);
            	        			}catch(SQLException e){
            	        				e.printStackTrace();
            	        			}
            					}
            					else{
            						System.out.println("Executing Sub-command " +k+"."+(i+1)+": "+"ALTER TABLE "+table+commands[i]);
            						commands[i] = commands[i] + ")";
            						commands[i] = commands[i].substring(1, commands[i].length());
            						Pattern patterns = Pattern.compile("\\s*ADD\\s+INDEX\\s+(.+)[(](.+)[)]");
            						Matcher ms = patterns.matcher(commands[i]);
        							Pattern patterns1 = Pattern.compile("\\s*ADD\\s+UNIQUE\\s+INDEX\\s(.+)[(](.+)[)]");
        							Matcher ms1 = patterns1.matcher(commands[i]);
        							if(ms.find()) {commands[i] = "CREATE INDEX " + ms.group(1) + " ON " + table + "(" + ms.group(2) + ")";}
            						else if(ms1.find()) {commands[i] = "CREATE INDEX " + ms1.group(1) + " ON " + table + "(" + ms1.group(2) + ")";}
        							try{
            	        				db.update(commands[i]);
            	        			}catch(SQLException e){
            	        				e.printStackTrace();
            	        			}
            					}
            				}
        				}
        				k++;
        				continue;
        			}
        		}
        		if(line.contains("LOAD")){
        			flag=18;
        			System.out.println("LOAD");
        			//Pattern pattern1 = Pattern.compile("LOAD DATA LOCAL INFILE(.*)$");
        			Pattern pattern = Pattern.compile("LOAD\\s+DATA\\s+\\w+\\s+\\w+\\s+'(.+)'\\s+INTO\\s+TABLE\\s+(\\w+)\\s+(.*)\\s+([(].*[)])(.*)");
        			Matcher m = pattern.matcher(line);
        			if(m.find()){
        				//System.out.println("ALL 3: "+m.group(0)+"\n"+m.group(1)+"\n"+m.group(2)+"\n"+m.group(3));
        				//String command1
        				String command0 = "CREATE TEXT TABLE BUFFER AS (SELECT * FROM " + m.group(2) + " WHERE 1=2) WITH NO DATA;";
        				//String command1 = "SET TABLE BUFFER TYPE TEXT";
        				String command2 = "SET TABLE BUFFER SOURCE \""+m.group(1)+";fs=\\t\";";
        				System.out.println("Executing sub-command: "+command0);
        				try{
        					db.update(command0);
        				}catch(SQLException s){
        					s.printStackTrace();
        				}
        				System.out.println("Executing sub-command: "+command2);
        				try{
        					db.update(command2);
        				}catch(SQLException s){
        					s.printStackTrace();
        				}
        				String command3 = "DROP TABLE IF EXISTS "+m.group(2);
        				System.out.println("Executing sub-command: "+command3);
        				try{
        					db.update(command3);
        				}catch(SQLException s){
        					s.printStackTrace();
        				}
        				String command4 = "ALTER TABLE BUFFER RENAME TO "+m.group(2);
        				System.out.println("Executing sub-command: "+command4);
        				try{
        					db.update(command4);
        				}catch(SQLException s){
        					s.printStackTrace();
        				}
        				continue;
        				//String command3 = "CREATE TABLE " + m.group(1) + " " ( DOCUMENT_ID , POSITION_OF_TOKEN_IN_DOCUMENT , TERM , TOPIC_ID ) AS (SELECT DOCUMENT_ID , POSITION_OF_TOKEN_IN_DOCUMENT , TERM , TOPIC_ID FROM DOCUMENT_TERM_TOPIC_CSV) WITH DATA;;
        			}	
        		}
        		if(line.contains("UPDATE")){
        			flag=20;
        			Pattern pattern = Pattern.compile("^\\s*UPDATE\\s+(\\s*\\w+\\s+\\w+\\s*)(([,]\\s*(\\w+)\\s+(\\w+)\\s*)*)(set|SET|Set)");
        			Matcher m = pattern.matcher(line);
        			Pattern pattern1 = Pattern.compile("(SET|set|Set)\\s*(\\w*\\.\\w*\\$\\w*|\\w*\\.\\w*|\\w*)(\\s*[=]\\s*)(\\w*\\.\\w*\\$\\w*|\\w*\\.\\w*|\\w*)");
        			Matcher m1 = pattern1.matcher(line);
        			Pattern pattern2 = Pattern.compile("WHERE\\s+(.*)$");
        			Matcher m2 = pattern2.matcher(line);
        			if(m.find()){
        				for(int i=0;i<=m.groupCount();i++)
        					System.out.print(/*i+" "+m.group(i)*/"");
        				if(m1.find())
        					for(int i=0;i<=m1.groupCount();i++)
            					System.out.print(/*i+" "+m1.group(i)*/"");
        				if(m2.find()){
        				String q = "UPDATE "+m.group(1) + " SET "+m1.group(2)+" = ( SELECT " + m1.group(4) + " FROM " + m.group(2).substring(1) + " WHERE "+m2.group(1)+");";
        				//System.out.println(q);
        				line=q;
        				}
        			}
        			
        		}
        		    		
        		if(flag>=1){
        			//System.out.println("Query "+k+" changed with last flag: " +flag);
        			System.out.println("New Query: "+line+"\n");
        		}
        		
        		if(k==-1) db.update(line);
        		else{
        			try{
        				
        				if(line.contains("INSERT") || line.contains("UPDATE") || line.contains("ALTER") || (line.contains("CREATE"))){ System.out.println(k+" "+line);db.update(line);}
        				else
        					db.query(line);
        			}catch(SQLException e){
        				System.out.println(e.getMessage());
        				try {
                			logFile = new FileWriter(args[3]/*"/data/vishal/db_r1/current_k.txt"*/,true);
                		} catch (IOException e1) {
                			// TODO Auto-generated catch block
                			e1.printStackTrace();
                		}
                		logFile.write("ERROR IS: "+e.getMessage()+"\n");
                		logFile.close();
        				e.printStackTrace();
        				count++;
        				System.out.println("Exception here: line "+(k+1) + " count:" + count);
        			}
        		}
        		
        		//System.out.println(k);
        		k++;
        			//db.update(line);
        	}	
            db.shutdown();
        }catch(Exception ex2){
        			ex2.printStackTrace();
        			System.out.println("Error at query");
        }
        System.out.println("count: "+count);

        //System.out.println(error);
    }    // main()
}    // class Testdb
