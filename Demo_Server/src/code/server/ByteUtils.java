package code.server;

public class ByteUtils {
    //16进制转字节数组
    public static byte[] hexStringToByteArray(String s) {
        s=s.replace(" ","");
        if (!isRightHexStr(s)){
            //XLog.e("不是16进制字符串");
            return null;
        }

        if (s.length() % 2 != 0) {
            StringBuilder stringBuilder = new StringBuilder(s);
            stringBuilder.insert(s.length() - 1, "0");
            s = stringBuilder.toString();
        }


        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    //是否是16进制字符串
    public static boolean isRightHexStr(String str) {
        String reg = "^[0-9a-fA-F]+$";
        return str.matches(reg);
    }
    
    //字节数组转16进制字符串
    public static String ByteArraytoHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String bs = String.format("%02X ", b);
            sb.append(bs);
        }
        return sb.toString();
    }

    //字节数组转16进制字符串
    public static String ByteArraytoHex(byte[] bytes, int len) {
    	StringBuilder sb = new StringBuilder();
    	for(int i=0; i<len; i++) {
    		String bs = String.format("%02X ", bytes[i]);
    		
    		sb.append(bs);
    		
    	}
    	
    	
    	return sb.toString();
    }


    public static String yihuo(String content) {
        String[] b = content.split(" ");
        int a = 0;
        for (int i = 0; i < b.length; i++) {
            a = a ^ Integer.parseInt(b[i], 16);
        }
       /* if(a<10){
            *//*StringBuffer sb = new StringBuffer();
            sb.append("0");
            sb.append(a);
            return sb.toString();*//*
            return "0"+a;
        }*/

        String result=Integer.toHexString(a).toUpperCase();
        if (result.length()==1){
            return "0"+result;
        }else {
            return result;
        }
//        return Integer.toHexString(a).toUpperCase();
    }



    /*10进制转16进制*/
    public static String decimalToHex(int decimal) {
        String hex = "";
        while(decimal != 0) {
            int hexValue = decimal % 16;
            hex = toHexChar(hexValue) + hex;
            decimal = decimal / 16;
        }
        if (hex.length()==1){
            return "0"+hex;
        }
        return  hex;
    }
    //将0~15的十进制数转换成0~F的十六进制数
    public static char toHexChar(int hexValue) {
        if(hexValue <= 9 && hexValue >= 0)
            return (char)(hexValue + '0');
        else
            return (char)(hexValue - 10 + 'A');
    }


    public static byte[] Int2Bytes(int integer)
    {
            byte[] bytes=new byte[4];

            bytes[3]=(byte) ((byte)integer>>24);
            bytes[2]=(byte) ((byte)integer>>16);
            bytes[1]=(byte) ((byte)integer>>8);
            bytes[0]=(byte)integer;

            return bytes;
    }


    /**
     * 将int转为低字节在前，高字节在后的byte数组
     */
    public static byte[] tolh(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }
    /**
     * 将int转为高字节在前，低字节在后的byte数组
     * @param n int
     * @return byte[]
     */
    public static byte[] toHH(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }


    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }



    /**
     * 4位字节数组转换为整型
     * @param b
     * @return
     */
    public static int byte2Int(byte[] b) {
        int intValue = 0;
        for (int i = b.length-1; i >=0; i--) {
            intValue += (b[i] & 0xFF) << (8 * (b.length-i-1));
        }
        return intValue;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {//原数组，开始位置，长度。
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

}
