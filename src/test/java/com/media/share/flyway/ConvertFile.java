//package com.media.share.flyway;
//
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//public class ConvertFile {
//
//    String filePathBase = "E:/project/file";
//    @Test
//    public void test() throws IOException, InterruptedException {
//        Path fileDirectoryPath = Paths.get(filePathBase,"public","media","test1");
//        Path videoPath = Paths.get(filePathBase,"public","media","test1.mp4");
//        Path outputPath = Paths.get(filePathBase,"public","media","test1");
//        File file = new File(videoPath.toString());
//        System.out.println(file);
//        Files.createDirectories(fileDirectoryPath);
//
//        String ffmpegCmd = String.format(
//                "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%4d.ts\"  \"%s/master.m3u8\" ",
//                videoPath, outputPath, outputPath
//        );
//        System.out.println(ffmpegCmd);
//        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", ffmpegCmd);
//        processBuilder.inheritIO();
//        Process process = processBuilder.start();
//        int exit = process.waitFor();
//        if (exit != 0) {
//            throw new RuntimeException("video processing failed!!");
//        }
//
//    }
//}
