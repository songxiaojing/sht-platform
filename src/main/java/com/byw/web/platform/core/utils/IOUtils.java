package com.byw.web.platform.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.byw.web.platform.log.PlatformLogger;


/**
 * This is the platform general utility class.
 * <p>
 * This is the platform general utility class.
 *
 * @author baiyanwei
 * @title Utils
 * @package com.topsec.tss.platform.core.utils
 * @date 2014-8-8
 */
public class IOUtils {

    //
    // Logging Object
    //
    final private static PlatformLogger theLogger = PlatformLogger.getLogger(IOUtils.class);
    final private static int BUFFER_SIZE = 1024;
    final private static int EOF = -1;

    /**
     * This will turn an input stream into a String Buffer. The InputStream will always be closed before return.
     *
     * @param inputStream
     * @return
     */
    public static StringBuffer getInputStream2StringBuffer(InputStream inputStream) {

        if (inputStream == null) {
            return null;
        }

        InputStreamReader utf8 = null;
        //
        try {
            utf8 = new InputStreamReader(inputStream, Constants.UTF_8);
            //
            StringBuffer stringBuffer = new StringBuffer();
            //
            char[] buffer = new char[BUFFER_SIZE];
            int n = 0;
            //
            while ((n = utf8.read(buffer)) != EOF) {
                stringBuffer.append(buffer, 0, n);
            }
            //
            return stringBuffer;
        } catch (UnsupportedEncodingException e) {
            theLogger.exception(e);
        } catch (IOException e) {
            theLogger.exception(e);
        } finally {
            if (utf8 != null) {
                try {
                    utf8.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /**
     * 将字节写入到File中.
     *
     * @param bytes
     * @param file
     */
    public static void writeByteContentToFile(byte[] bytes, File file) {

        if (file == null || file.exists() == false) {
            return;
        }
        if (bytes == null || bytes.length <= 0) {
            return;
        }
        ByteArrayInputStream byteInputStream = null;

        OutputStream sOutput = null;
        try {
            byteInputStream = new ByteArrayInputStream(bytes);
            sOutput = new FileOutputStream(file);

            byte[] buffer = new byte[BUFFER_SIZE];
            int size;
            while ((size = byteInputStream.read(buffer, 0, buffer.length)) != EOF) {
                sOutput.write(buffer, 0, size);
            }
            sOutput.flush();
        } catch (Exception e) {
            theLogger.exception(e);
        } finally {
            if (byteInputStream != null) {
                try {
                    byteInputStream.close();
                } catch (IOException e) {
                }
            }
            if (sOutput != null) {
                try {
                    sOutput.close();
                } catch (IOException e) {
                }
            }
        }

    }

    /**
     * This will turn an input stream into a byte[]. The InputStream will always be closed before return.
     *
     * @param inputStream
     * @return
     */
    public static byte[] readInputStream2ByteArray(InputStream inputStream) {

        if (inputStream == null) {
            return null;
        }
        //
        ByteArrayOutputStream byteArrayOutput = null;

        try {
            byteArrayOutput = new ByteArrayOutputStream();
            //
            byte[] buffer = new byte[BUFFER_SIZE];
            int n = 0;
            //
            while ((n = inputStream.read(buffer)) != EOF) {
                byteArrayOutput.write(buffer, 0, n);
            }
            //
            byte[] dataArray = byteArrayOutput.toByteArray();
            //
            return dataArray;
        } catch (IOException e) {
            theLogger.exception(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if (byteArrayOutput != null) {
                try {
                    byteArrayOutput.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /**
     * Converts the input bytes to hex.
     */
    public static String convertToHex(byte[] data) {

        if (data == null) {
            return "";
        }

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Use the standard java GZip functionality.
     * <p>
     * returns a byte array of the compress data.
     *
     * @param inputBytes
     * @return
     */
    public static byte[] compress(byte[] inputBytes) {

        if (inputBytes == null || inputBytes.length == 0) {
            return null;
        }

        ByteArrayOutputStream bytesOutputStream = null;
        GZIPOutputStream gZIPOutputStream = null;
        //
        try {
            bytesOutputStream = new ByteArrayOutputStream();
            gZIPOutputStream = new GZIPOutputStream(bytesOutputStream);
            //compress the data
            gZIPOutputStream.write(inputBytes, 0, inputBytes.length);
            //
            gZIPOutputStream.finish();
            //
            byte[] dataArray = bytesOutputStream.toByteArray();
            //
            return dataArray;
        } catch (IOException e) {
            theLogger.error("", e);
        } finally {
            if (bytesOutputStream != null) {
                try {
                    bytesOutputStream.close();
                } catch (IOException e) {
                }
            }
            if (gZIPOutputStream != null) {
                try {
                    gZIPOutputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    /**
     * Uncompress a byte array of data
     *
     * @param inputBytes
     * @return an uncompressed byte array.
     */
    public static byte[] decompress(byte[] inputBytes) {

        if (inputBytes == null || inputBytes.length == 0) {
            return null;
        }
        GZIPInputStream gzipInputStream = null;
        ByteArrayOutputStream bytesOutputStream = null;
        try {
            gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(inputBytes));
            bytesOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[BUFFER_SIZE];
            int n = 0;

            while ((n = gzipInputStream.read(buffer)) != EOF) {
                bytesOutputStream.write(buffer, 0, n);
            }
            //
            byte[] dataArray = bytesOutputStream.toByteArray();
            //
            return dataArray;
        } catch (IOException e) {
            theLogger.error("", e);
        } finally {
            if (gzipInputStream != null) {
                try {
                    gzipInputStream.close();
                } catch (IOException e) {
                }
            }
            if (bytesOutputStream != null) {
                try {
                    bytesOutputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }


    /**
     * load the properties collection by path.
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static Properties createProperties(String path) throws IOException {

        if (Assert.isEmptyString(path) == true) {
            return null;
        }

        InputStream fileInput = null;
        try {
            // parser the class name to path.
            fileInput = IOUtils.class.getClassLoader().getResourceAsStream(path);
            if (fileInput == null) {
                // Doesn't define the RES.properties file
                fileInput = new FileInputStream(path);
            }
            if (fileInput == null) {
                return null;
            }
            Properties properties = new Properties();
            // loading into cache.
            properties.load(fileInput);
            return properties;
        } catch (IOException e) {
            throw e;
        } finally {
            if (fileInput != null) {
                try {
                    fileInput.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
