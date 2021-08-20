package cn.safekeeper.common.utils;

import cn.safekeeper.common.exception.SafeKeeperException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 统一工具类
 * @author skylark
 */
public class SafeKeeperUtils {

    private static Base64.Encoder encoder = Base64.getEncoder();
    private static Base64.Decoder decoder = Base64.getDecoder();

    /**
     * 判断字符串是否为空
     * @param str 字符串
     * @return 结果
     */
    public static boolean isEmpty(String str){
        if(str==null||"".equals(str)){
            return true;
        }
        return false;
    }


    /**
     * md5加密
     * @param str 指定字符串
     * @return 加密后的字符串
     */
    public static String md5(String str) {
        str = (str == null ? "" : str);
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] btInput = str.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] strA = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                strA[k++] = hexDigits[byte0 >>> 4 & 0xf];
                strA[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(strA);
        } catch (Exception e) {
            throw new SafeKeeperException(e);
        }
    }
    /**
     * sha1加密
     *
     * @param str 指定字符串
     * @return 加密后的字符串
     */
    public static String sha1(String str) {
        try {
            str = (str == null ? "" : str);
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] b = str.getBytes();
            md.update(b);
            byte[] b2 = md.digest();
            int len = b2.length;
            String strA = "0123456789abcdef";
            char[] ch = strA.toCharArray();
            char[] chs = new char[len * 2];
            for (int i = 0, k = 0; i < len; i++) {
                byte b3 = b2[i];
                chs[k++] = ch[b3 >>> 4 & 0xf];
                chs[k++] = ch[b3 & 0xf];
            }
            return new String(chs);
        } catch (Exception e) {
            throw new SafeKeeperException(e);
        }
    }

    /**
     * sha256加密
     *
     * @param str 指定字符串
     * @return 加密后的字符串
     */
    public static String sha256(String str) {
        try {
            str = (str == null ? "" : str);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));

            byte[] bytes = messageDigest.digest();
            StringBuilder builder = new StringBuilder();
            String temp;
            for (int i = 0; i < bytes.length; i++) {
                temp = Integer.toHexString(bytes[i] & 0xFF);
                if (temp.length() == 1) {
                    builder.append("0");
                }
                builder.append(temp);
            }

            return builder.toString();
        } catch (Exception e) {
            throw new SafeKeeperException(e);
        }
    }

    /**
     * md5加盐加密: md5(md5(str) + md5(salt))
     * @param str 字符串
     * @param salt 盐
     * @return 加密后的字符串
     */
    public static String md5BySalt(String str, String salt) {
        return md5(md5(str) + md5(salt));
    }

    /**
     * 默认密码算法
     */
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * AES加密
     *
     * @param key 加密的密钥
     * @param text 需要加密的字符串
     * @return 返回Base64转码后的加密数据
     */
    public static String aesEncrypt(String key, String text) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] byteContent = text.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));
            byte[] result = cipher.doFinal(byteContent);
            return encoder.encodeToString(result);
        } catch (Exception e) {
            throw new SafeKeeperException(e);
        }
    }

    /**
     * AES解密
     * @param key 加密的密钥
     * @param text 已加密的密文
     * @return 返回解密后的数据
     */
    public static String aesDecrypt(String key, String text) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));
            byte[] result = cipher.doFinal(decoder.decode(text));
            return new String(result, "utf-8");
        } catch (Exception e) {
            throw new SafeKeeperException(e);
        }
    }

    /**
     * 生成加密秘钥
     * @param password 秘钥
     * @return SecretKeySpec
     * @throws NoSuchAlgorithmException
     */
    private static SecretKeySpec getSecretKey(final String password) throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        kg.init(128, random);
        SecretKey secretKey = kg.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

    private static final String ALGORITHM = "RSA";
    private static final int KEY_SIZE = 1024;

    /**
     * 生成密钥对
     * @return Map对象 (private=私钥, public=公钥)
     * @throws Exception 异常
     */
    public static HashMap<String, String> rsaGenerateKeyPair() throws Exception {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        KeyPair keyPair;

        try {
            keyPairGenerator.initialize(KEY_SIZE,
                    new SecureRandom(UUID.randomUUID().toString().replaceAll("-", "").getBytes()));
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (InvalidParameterException e) {
            throw e;
        } catch (NullPointerException e) {
            throw e;
        }

        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

        HashMap<String, String> map = new HashMap<String, String>(16);
        map.put("private", encoder.encodeToString(rsaPrivateKey.getEncoded()));
        map.put("public", encoder.encodeToString(rsaPublicKey.getEncoded()));
        return map;
    }

    /**
     * RSA公钥加密
     * @param publicKeyString 公钥
     * @param content 内容
     * @return 加密后内容
     */
    public static String rsaEncryptByPublic(String publicKeyString, String content) {
        try {
            // 获得公钥对象
            PublicKey publicKey = getPublicKeyFromString(publicKeyString);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 该密钥能够加密的最大字节长度
            int splitLength = ((RSAPublicKey) publicKey).getModulus().bitLength() / 8 - 11;
            byte[][] arrays = splitBytes(content.getBytes(), splitLength);
            StringBuffer stringBuffer = new StringBuffer();
            for (byte[] array : arrays) {
                stringBuffer.append(bytesToHexString(cipher.doFinal(array)));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            throw new SafeKeeperException(e);
        }
    }

    /**
     * RSA私钥加密
     * @param privateKeyString 私钥
     * @param content 内容
     * @return 加密后内容
     */
    public static String rsaEncryptByPrivate(String privateKeyString, String content) {
        try {
            PrivateKey privateKey = getPrivateKeyFromString(privateKeyString);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            // 该密钥能够加密的最大字节长度
            int splitLength = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8 - 11;
            byte[][] arrays = splitBytes(content.getBytes(), splitLength);
            StringBuffer stringBuffer = new StringBuffer();
            for (byte[] array : arrays) {
                stringBuffer.append(bytesToHexString(cipher.doFinal(array)));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            throw new SafeKeeperException(e);
        }
    }

    /**
     * RSA公钥解密
     * @param publicKeyString 公钥
     * @param content 已加密内容
     * @return 解密后内容
     */
    public static String rsaDecryptByPublic(String publicKeyString, String content) {

        try {
            PublicKey publicKey = getPublicKeyFromString(publicKeyString);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            // 该密钥能够加密的最大字节长度
            int splitLength = ((RSAPublicKey) publicKey).getModulus().bitLength() / 8;
            byte[] contentBytes = hexStringToBytes(content);
            byte[][] arrays = splitBytes(contentBytes, splitLength);
            StringBuffer stringBuffer = new StringBuffer();
            for (byte[] array : arrays) {
                stringBuffer.append(new String(cipher.doFinal(array)));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            throw new SafeKeeperException(e);
        }
    }

    /**
     * RSA私钥解密
     * @param privateKeyString 公钥
     * @param content 已加密内容
     * @return 解密后内容
     */
    public static String rsaDecryptByPrivate(String privateKeyString, String content) {
        try {
            PrivateKey privateKey = getPrivateKeyFromString(privateKeyString);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            // 该密钥能够加密的最大字节长度
            int splitLength = ((RSAPrivateKey) privateKey).getModulus().bitLength() / 8;
            byte[] contentBytes = hexStringToBytes(content);
            byte[][] arrays = splitBytes(contentBytes, splitLength);
            StringBuffer stringBuffer = new StringBuffer();
            for (byte[] array : arrays) {
                stringBuffer.append(new String(cipher.doFinal(array)));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            throw new SafeKeeperException(e);
        }
    }

    /** 根据公钥字符串获取 公钥对象 */
    private static PublicKey getPublicKeyFromString(String key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        // 过滤掉\r\n
        key = key.replace("\r\n", "");

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(decoder.decode(key));

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        return publicKey;
    }

    /** 根据私钥字符串获取 私钥对象 */
    private static PrivateKey getPrivateKeyFromString(String key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        // 过滤掉\r\n
        key = key.replace("\r\n", "");

        // 取得私钥
        PKCS8EncodedKeySpec x509KeySpec = new PKCS8EncodedKeySpec(decoder.decode(key));

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        PrivateKey privateKey = keyFactory.generatePrivate(x509KeySpec);

        return privateKey;
    }

    private static byte[][] splitBytes(byte[] bytes, int splitLength) {
        // bytes与splitLength的余数
        int remainder = bytes.length % splitLength;
        // 数据拆分后的组数，余数不为0时加1
        int quotient = remainder != 0 ? bytes.length / splitLength + 1 : bytes.length / splitLength;
        byte[][] arrays = new byte[quotient][];
        byte[] array = null;
        for (int i = 0; i < quotient; i++) {
            // 如果是最后一组（quotient-1）,同时余数不等于0，就将最后一组设置为remainder的长度
            if (i == quotient - 1 && remainder != 0) {
                array = new byte[remainder];
                System.arraycopy(bytes, i * splitLength, array, 0, remainder);
            } else {
                array = new byte[splitLength];
                System.arraycopy(bytes, i * splitLength, array, 0, splitLength);
            }
            arrays[i] = array;
        }
        return arrays;
    }

    /**
     * 将字节数组转换成16进制字符串
     * @param bytes 字节数组
     * @return 字符串
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() < 2) {
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    /**
     *
     * 16进制字符串转换成将字节数组
     * @param hex 字符串
     * @return 字节数组
     *
     */
    private static byte[] hexStringToBytes(String hex) {
        int len = (hex.length() / 2);
        hex = hex.toUpperCase();
        byte[] result = new byte[len];
        char[] chars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(chars[pos]) << 4 | toByte(chars[pos + 1]));
        }
        return result;
    }

    /** 将char转换为byte */
    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    /**
     * 打印版本字符画
     */
    public static void printSaToken() {
    }

    /**
     *
     * 生成指定长度的随机字符串
     * @param length 字符串的长度
     * @return 一个随机字符串
     *
     */
    public static String getRandomString(int length) {
        String str=  "!@#$%^&*abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 以当前时间戳和随机int数字拼接一个随机字符串
     * @return 随机字符串
     */
    public static String getMarking28() {
        return System.currentTimeMillis() + "" + new Random().nextInt(Integer.MAX_VALUE);
    }

    /**
     *
     * 将日期格式化
     * @param date 日期
     * @return 格式化后的时间
     *
     */
    public static String formatDate(Date date){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    /**
     * 从集合里查询数据
     *
     * @param dataList 数据集合
     * @param prefix   前缀
     * @param keyword  关键字
     * @param start    起始位置 (-1代表查询所有)
     * @param size     获取条数
     * @return 符合条件的新数据集合
     */
    public static List<String> searchList(Collection<String> dataList, String prefix, String keyword, int start,
                                          int size) {
        if (prefix == null) {
            prefix = "";
        }
        if (keyword == null) {
            keyword = "";
        }
        // 挑选出所有符合条件的
        List<String> list = new ArrayList<String>();
        Iterator<String> keys = dataList.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.startsWith(prefix) && key.indexOf(keyword) > -1) {
                list.add(key);
            }
        }
        // 取指定段数据
        return searchList(list, start, size);
    }

    /**
     * 从集合里查询数据
     * @param list  数据集合
     * @param start 起始位置
     * @param size  获取条数
     * @return 符合条件的新数据集合
     *
     */
    public static List<String> searchList(List<String> list, int start, int size) {
        if (start < 0||size<0) {
            return list;
        }
        int end = start + size;
        List<String> tmp = new ArrayList<String>();
        for (int i = start; i < end; i++) {
            if (i >= list.size()) {
                return tmp;
            }
            tmp.add(list.get(i));
        }
        return tmp;
    }

    /**
     * 字符串模糊匹配
     * @param patter 表达式
     * @param str 待匹配的字符串
     * @return 是否可以匹配
     */
    public static boolean vagueMatch(String patter, String str) {
        if(!patter.contains("*")) {
            return patter.equals(str);
        }
        return Pattern.matches(patter.replaceAll("\\*", ".*"), str);
    }


    /**
     *
     * 将指定值转化为指定类型
     * @param <T> 泛型
     * @param obj 值
     * @param cs 类型
     * @return 转换后的值
     *
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValueByType(Object obj, Class<T> cs) {
        // 如果 obj 为 null 或者本来就是 cs 类型
        if(obj == null || obj.getClass().equals(cs)) {
            return (T)obj;
        }
        // 开始转换
        String obj2 = String.valueOf(obj);
        Object obj3 = null;
        if (cs.equals(String.class)) {
            obj3 = obj2;
        } else if (cs.equals(int.class) || cs.equals(Integer.class)) {
            obj3 = Integer.valueOf(obj2);
        } else if (cs.equals(long.class) || cs.equals(Long.class)) {
            obj3 = Long.valueOf(obj2);
        } else if (cs.equals(short.class) || cs.equals(Short.class)) {
            obj3 = Short.valueOf(obj2);
        } else if (cs.equals(byte.class) || cs.equals(Byte.class)) {
            obj3 = Byte.valueOf(obj2);
        } else if (cs.equals(float.class) || cs.equals(Float.class)) {
            obj3 = Float.valueOf(obj2);
        } else if (cs.equals(double.class) || cs.equals(Double.class)) {
            obj3 = Double.valueOf(obj2);
        } else if (cs.equals(boolean.class) || cs.equals(Boolean.class)) {
            obj3 = Boolean.valueOf(obj2);
        } else {
            obj3 = (T)obj;
        }
        return (T)obj3;
    }
}
