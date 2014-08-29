package cc.topicexplorer.plugin.pos.preprocessing.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.csvreader.CsvWriter;

public class csvWriter 
{
	CsvWriter writer = null;
	String[] header =null;
	String path = "";
	boolean append = false;
	OutputStream csvOutputStream=null;
	
	public csvWriter(String[] header, boolean append, String path){
		this.header = header;
		this.append = append;				
		this.path = path;
		try {
			OutputStream  csvOutputStream = new FileOutputStream(path, append);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer = new CsvWriter(this.csvOutputStream, ',',
				  Charset.defaultCharset());
	}
	
	public void writeHeader(){
		try {
			writer.writeRecord(header);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void writeLine(String[] line){
		try {
			writer.writeRecord(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void closeWriter()
	{
		writer.close();
	}
}
