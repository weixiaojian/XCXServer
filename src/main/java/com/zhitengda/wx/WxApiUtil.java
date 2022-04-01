package com.zhitengda.wx;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhitengda.web.exception.GlobalException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.Security;
import java.util.Arrays;

/**
 * @author langao_q
 * @since 2021-02-01 11:42
 */
public class WxApiUtil {

    private static final String WATERMARK = "watermark";
    private static final String APPID = "appid";
    public static boolean initialized = false;

    /**
     * 解密数据
     *
     * @return
     * @throws Exception
     */
    public static String decrypt(String appId, String encryptedData, String sessionKey, String iv) {
        String result = "";
        try {
            byte[] resultByte = decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey),
                    Base64.decodeBase64(iv));
            if (null != resultByte && resultByte.length > 0) {
                result = new String(decode(resultByte));
                JSONObject jsonObject = JSONUtil.parseObj(result);
                String decryptAppid = jsonObject.getJSONObject(WATERMARK).getStr(APPID);
                if (!appId.equals(decryptAppid)) {
                    result = "";
                }
            }
        } catch (Exception e) {
            result = "";
            throw new GlobalException(e.getMessage());
        }
        return result;
    }

    /**
     * AES解密
     * @param content 密文
     * @return
     * @throws InvalidAlgorithmParameterException
     */
    private static byte[] decrypt(byte[] content, byte[] keyByte, byte[] ivByte) throws InvalidAlgorithmParameterException {
        initialize();
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            Key sKeySpec = new SecretKeySpec(keyByte, "AES");
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(ivByte));
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            throw new GlobalException("解密用户数据失败：请退出登陆或清除缓存后重试！");
        }
    }

    /**
     * 初始化
     */
    private static void initialize() {
        if (initialized){
            return;
        }
        Security.addProvider(new BouncyCastleProvider());
        initialized = true;
    }

    /**
     * 生成iv
     * @param iv
     * @return
     * @throws Exception
     */
    private static AlgorithmParameters generateIV(byte[] iv) throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
        params.init(new IvParameterSpec(iv));
        return params;
    }

    /**
     * 删除解密后明文的补位字符
     * @param decrypted 解密后的明文
     * @return 删除补位字符后的明文
     */
    private static byte[] decode(byte[] decrypted) {
        int pad = decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 32) {
            pad = 0;
        }
        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
    }
}
