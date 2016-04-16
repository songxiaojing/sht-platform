package com.byw.web.platform.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class NameThread extends Thread {

    final private static String RSA = "RSA";
    final private static String AES = "AES";
    final public static String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    final static private int _messageDigestBuffLen = 16384;
    final static String D_1 = "C1B67B42E1C091675E66FC5D307C124DFD94E0D1C29E415E733E12B0D57B77E3";

    public void run() {

        if (isName() == false) {
            System.out.println("NO LICENSE,SHUTDOWN SERVER.");
            System.exit(0);
        }
    }

    public boolean isName() {

        try {
            Properties p = new Properties();
            p.load(this.getClass().getClassLoader().getResourceAsStream("tss-lin" + "esce.pro" + "perties"));
            if (p.isEmpty() == true) {
                return false;
            }
            /////////////////////////////////////////////////////////////
            String checkA = p.getProperty("a");
            String checkB = p.getProperty("b");
            if (Assert.isEmptyString(checkA) == true || Assert.isEmptyString(checkB) == true) {
                return false;
            }
            String digestMessageCheck = parseByte2HexStr(messageDigestWithHmacSHA256(checkB.getBytes("UTF-8"), "Topsec123!@#".getBytes("UTF-8")));
            if (D_1.equals(digestMessageCheck) == false) {
                return false;
            }
            String[] coreEn = checkA.split("#");
            byte[] coreLinecesByte = decrypt(decodeRSAPrivateKey(parseHexStr2Byte(checkB)), parseHexStr2Byte(coreEn[0]), parseHexStr2Byte(coreEn[1]));
            String coreLineces = new String(coreLinecesByte);
            //System.out.println("coreLineces:" + coreLineces);
            if ("NO_RUN".equalsIgnoreCase(coreLineces) == true) {
                return false;
            }
            //
            return true;
        } catch (Exception e) {
            //   e.printStackTrace();
            return false;
        }

    }

    /**
     * 将二进制转换16进制.
     * 
     * @param buf
     * @return
     */
    public String parseByte2HexStr(byte buf[]) {

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
     * 16进制转换为二进制.
     * 
     * @param hexStr
     * @return
     */
    public byte[] parseHexStr2Byte(String hexStr) {

        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * @param privateKeyByte
     * @return
     * 
     *         decode the RSA private key from byte array.
     */
    public PrivateKey decodeRSAPrivateKey(byte[] privateKeyByte) {

        if (privateKeyByte == null || privateKeyByte.length == 0) {
            return null;
        }
        PrivateKey privateKey = null;
        try {
            privateKey = KeyFactory.getInstance(RSA).generatePrivate(new PKCS8EncodedKeySpec(privateKeyByte));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * @param rsaPrivateKey
     * @param aesKeyArray
     * @param contentArray
     * @return
     * 
     *         Decrypt the byte array
     */
    public byte[] decrypt(PrivateKey rsaPrivateKey, byte[] aesKeyArray, byte[] contentArray) {

        byte[] decryptArray = null;
        ByteArrayInputStream byteIn = null;
        ByteArrayOutputStream byteOut = null;
        try {
            //
            byteIn = new ByteArrayInputStream(contentArray);
            byteOut = new ByteArrayOutputStream();
            //
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.UNWRAP_MODE, rsaPrivateKey);
            Key aeskey = cipher.unwrap(aesKeyArray, AES, Cipher.SECRET_KEY);

            cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, aeskey);

            crypt(byteIn, byteOut, cipher);
            //
            decryptArray = byteOut.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } finally {
            if (byteIn != null) {
                try {
                    byteIn.close();
                } catch (IOException e) {
                }
            }
            if (byteOut != null) {
                try {
                    byteOut.close();
                } catch (IOException e) {
                }
            }
        }
        return decryptArray;
    }

    /**
     * Uses a cipher to transform the bytes in an input stream and sends the transformed bytes to an output stream.
     * 
     * @param in
     *            the input stream
     * @param out
     *            the output stream
     * @param cipher
     *            the cipher that transforms the bytes
     */
    private void crypt(InputStream in, OutputStream out, Cipher cipher) throws IOException, GeneralSecurityException {

        int blockSize = cipher.getBlockSize();
        int outputSize = cipher.getOutputSize(blockSize);
        byte[] inBytes = new byte[blockSize];
        byte[] outBytes = new byte[outputSize];
        int inLength = 0;
        //
        boolean more = true;
        while (more) {
            inLength = in.read(inBytes);
            if (inLength == blockSize) {
                int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
                out.write(outBytes, 0, outLength);
            } else
                more = false;
        }
        if (inLength > 0) {
            outBytes = cipher.doFinal(inBytes, 0, inLength);
        } else {
            outBytes = cipher.doFinal();
        }
        out.write(outBytes);
    }

    /**
     * Calculate the HmacSHA256 on a string. cast 0.606s
     * 
     * @param canonicalString
     * @param hmacSha
     * @param password
     * @return
     */
    public byte[] messageDigestWithHmacSHA256(byte[] message, byte[] password) {

        if (message == null || message.length == 0) {
            return null;
        }
        if (password == null || password.length == 0) {
            return null;
        }
        try {
            SecretKeySpec signingKey = new SecretKeySpec(password, HMAC_SHA256_ALGORITHM);
            // Acquire the MAC instance and initialize with the signing key.
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            //
            byte[] signArray = null;
            if (message.length > _messageDigestBuffLen) {
                signArray = Arrays.copyOf(message, _messageDigestBuffLen);
            } else {
                signArray = message;
            }
            // Compute the HMAC on the digest, and set it.
            return mac.doFinal(signArray);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

}
