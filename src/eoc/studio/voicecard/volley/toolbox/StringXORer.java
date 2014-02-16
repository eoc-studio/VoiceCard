package eoc.studio.voicecard.volley.toolbox;

//import sun.misc.BASE64Encoder;
//import org.apache.commons.codec.binary.Base64;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

//import android.util.Base64;


public class StringXORer {

    public static String encode(String s, String key) {
        return base64Encode(xorWithKey(s.getBytes(), key.getBytes()));
    }

    public static String decode(String s, String key) {
        return new String(xorWithKey(base64Decode(s), key.getBytes()));
    }

    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }

    private static byte[] base64Decode(String s) {
        return Base64.encodeBase64(s.getBytes());
//            BASE64Decoder d = new BASE64Decoder();
//            return d.decodeBuffer(s);
    }

    private static String base64Encode(byte[] bytes) {
//        BASE64Encoder enc = new BASE64Encoder();
//        return enc.encode(bytes).replaceAll("\\s", "");
    	
    	String outputString = new String(Base64.encodeBase64(bytes));
    	
    	  return outputString;
    }
}