package cc.topicexplorer.utils;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

public class MySQLEncoder {
	private Codec mysqlCodec;
	
	public MySQLEncoder() {
		mysqlCodec = new MySQLCodec(org.owasp.esapi.codecs.MySQLCodec.Mode.STANDARD);
	}
	
	public String encode(String encodeString) {  
		return ESAPI.encoder().encodeForSQL(mysqlCodec, encodeString);
	}
	
	public String encodeAsANSI(String encodeString) {  
		return ESAPI.encoder().encodeForSQL(new MySQLCodec(org.owasp.esapi.codecs.MySQLCodec.Mode.ANSI), encodeString);
	}
}
