package com.zhitengda.util;

import org.apache.shiro.crypto.hash.SimpleHash;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/**
 * 
 * <p>Description: 京东敏感信息加密和解密工具类<／p>
 *
 * @author wy
 *
 * @date 2020年1月13日
 */
public class ZtdAESUtils {

    private static final String AES_ALG = "AES";

    /**
     * 使用AES加密原始字符串.
     * @param input 原始输入字符数组
     * @param key 符合AES要求的密钥
     */
    public static byte[] aesEncrypt(byte[] input, byte[] key) {
        return aes(input, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * 加密字符串（加密失败返回原字符串）
     * @param input
     * @return
     */
    public static String aesEncryptString(String input){
        byte[] encode = new byte[0];
        try {
            encode = aesEncrypt(input.getBytes("utf-8"),"ztd123!@ztd123!@".getBytes());
        } catch (UnsupportedEncodingException e) {
            return input;
        }
        String code = parseByte2HexStr(encode);
        return code;
    }

    /**
     * 解密字符串 如果解密失败就返回原有字符串
     * @param code
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String aesDecryptString(String code){
        String decryptResult = null;
        try {
            byte[] decode =parseHexStr2Byte(code);
            decryptResult = aesDecrypt(decode, "ztd123!@ztd123!@".getBytes());
        } catch (Exception e) {
            return code;
        }
        return decryptResult;
    }

    /**
     * oms密码加密
     * @param rawPass
     * @return
     */
    public static String encodeOmsPwd(String rawPass) {
        return (new SimpleHash("md5", rawPass.toCharArray(), (Object)null, 2)).toString();
    }

    /**
     * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
     * @param input 原始字节数组
     * @param key 符合AES要求的密钥
     * @param mode Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
     */
    private static byte[] aes(byte[] input, byte[] key, int mode) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, AES_ALG);
            Cipher cipher = Cipher.getInstance(AES_ALG);
            cipher.init(mode, secretKey);
            return cipher.doFinal(input);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 将二进制转换成16进制
     * @param buf
     * @return
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     * @param hexStr
     * @return
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


    /**
     * 使用AES解密字符串.
     * @param input Hex编码的加密字符串
     * @param key 符合AES要求的密钥
     * @throws UnsupportedEncodingException
     */
    private static String aesDecrypt(byte[] input, byte[] key) throws UnsupportedEncodingException {
        byte[] decryptResult = aes(input, key, Cipher.DECRYPT_MODE);
        return new String(decryptResult,"UTF-8");
    }

    public static void main(String[] args) {

        //加密：9AB98F7A5A9800B07FB393BA1EEF4923
        String code = aesEncryptString("15200991579");
        System.out.println(code);
        //解密
        String decryptResult = aesDecryptString("1B245BE07DD727DEF0297E21DCE27E02");
        System.out.println(decryptResult);

        System.out.println(encodeOmsPwd("888888"));
	}
}