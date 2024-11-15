//package com.media.share.simple;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import javax.imageio.ImageIO;
//import org.bytedeco.javacv.FFmpegFrameGrabber;
//import org.bytedeco.javacv.Java2DFrameConverter;
//
//public class Smoother {
//
//    protected String ffmpegApp;
//
//    public Smoother(String ffmpegApp)
//    {
//        this.ffmpegApp = ffmpegApp;
//    }
//
//    public void getThumb(String videoFilename, String thumbFilename, int width, int height,int hour, int min, float sec)
//            throws IOException, InterruptedException
//    {
//        ProcessBuilder processBuilder = new ProcessBuilder(ffmpegApp, "-y", "-i", videoFilename, "-vframes", "1",
//                "-ss", hour + ":" + min + ":" + sec, "-f", "mjpeg", "-s", width + "*" + height, "-an", thumbFilename);
//        Process process = processBuilder.start();
//        InputStream stderr = process.getErrorStream();
//        InputStreamReader isr = new InputStreamReader(stderr);
//        BufferedReader br = new BufferedReader(isr);
//        String line;
//        while ((line = br.readLine()) != null);
//        process.waitFor();
//    }
//
//    public static void main(String[] args) throws Exception, IOException
//    {
//        FFmpegFrameGrabber g = new FFmpegFrameGrabber("C:\\Users\\qsw233\\Desktop\\project\\share_media\\server\\tmp\\sample");
//        g.setFormat("mp4");
//        g.start();
//        Java2DFrameConverter converter = new Java2DFrameConverter();
//
//        var bufferedImage = converter.convert(g.grab());
//
//        for (int i = 0 ; i < 50 ; i++) {
//            ImageIO.write(bufferedImage, "png", new File("C:\\Users\\qsw233\\Desktop\\project\\share_media\\server\\tmp\\thumbnailvideo-frame-" + System.currentTimeMillis() + ".png"));
//        }
//
//        g.stop();
//    }
//}