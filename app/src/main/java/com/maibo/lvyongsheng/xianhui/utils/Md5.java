package com.maibo.lvyongsheng.xianhui.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LYS on 2016/12/20.
 */

public class Md5 {
    //md5加密
    public String getSign1(String userName,String type,String publicKey){
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
        String dateTime = sf.format(new Date());
        String plainText=userName+"-"+type+"-"+dateTime+"-"+publicKey;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSign2(String userName,String type,String agent_id,String publicKey){
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMdd");
        String dateTime = sf.format(new Date());
        String plainText=userName+"-"+type+"-"+agent_id+"-"+dateTime+"-"+publicKey;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            //32位加密
            return buf.toString();
            // 16位的加密
            //return buf.toString().substring(8, 24);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
