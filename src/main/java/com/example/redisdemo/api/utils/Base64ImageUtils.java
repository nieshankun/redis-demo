package com.example.redisdemo.api.utils;

import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;

/**
 * @author nsk
 * 2018/12/19 8:19
 */
public class Base64ImageUtils {

    private Base64ImageUtils() {
        // for sonar
    }

    /**
     * 将图片内容编码为字符串
     * @param file
     * @return
     */
    public static String encodeImageToBase64(MultipartFile file) {
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes).trim();
    }

    /**
     * 将图片内容解码为输入流
     * @param base
     * @return
     */
    public static InputStream decodeBase64ToImage(String base) {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decodeBytes = null;
        try {
            decodeBytes = decoder.decodeBuffer(base);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(decodeBytes);
    }

    public static String encodeImageToBase64(File file) {
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(outputStream.toByteArray()).trim();
    }

    public static void decodeBase64ToImage(String base,String path,String imageName){
        BASE64Decoder decoder = new BASE64Decoder();
        FileOutputStream write = null;
        try{
            write = new FileOutputStream(new File(path+imageName));
            byte[] decodeBytes = decoder.decodeBuffer(base);
            write.write(decodeBytes);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if (write != null){
                    write.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }


}
