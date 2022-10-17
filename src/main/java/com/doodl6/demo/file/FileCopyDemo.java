package com.doodl6.demo.file;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Random;

/**
 * 文件复制，多种方式实现
 */
public class FileCopyDemo {

    public static void main(String[] args) throws IOException {
        //创建一个大的临时文件
        String filePath = "/tmp/TestFile.tmp";
        File file = getFile(filePath);

        long start = System.currentTimeMillis();
        //300MB的内容
        byte[] data = generateData(300 * 1024 * 1024);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.flush();
        System.out.println("创建临时文件耗时：" + (System.currentTimeMillis() - start));

        //测试性能的时候需要分别运行，因为会受缓存页数据的影响
        //使用FileStream复制文件
        File destFile1 = getFile("/tmp/TestFile.fs");
        copyFileUseFileStream(file, destFile1);

        //使用FileChannel复制文件
        File destFile2 = getFile("/tmp/TestFile.fc");
//        copyFileUsingFileChannel(file, destFile2);

        //使用Files复制文件
        File destFile3 = getFile("/tmp/TestFile.f");
//        copyFileUsingFiles(file, destFile3);
    }

    private static void copyFileUseFileStream(File source, File dest) throws IOException {
        long start = System.currentTimeMillis();
        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buf)) > 0) {
                os.write(buf, 0, bytesRead);
            }
        }
        System.out.println("copyFileUseFileStream cost:" + (System.currentTimeMillis() - start));
    }

    private static void copyFileUsingFileChannel(File source, File dest) throws IOException {
        long start = System.currentTimeMillis();
        try (FileChannel inputChannel = new FileInputStream(source).getChannel(); FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        }
        System.out.println("copyFileUsingFileChannel cost:" + (System.currentTimeMillis() - start));
    }

    private static void copyFileUsingFiles(File source, File dest) throws IOException {
        long start = System.currentTimeMillis();
        Files.copy(source.toPath(), dest.toPath());
        System.out.println("copyFileUsingFiles cost:" + (System.currentTimeMillis() - start));
    }

    private static byte[] generateData(int size) {
        byte[] data = new byte[size];
        //随机填充内容
        Random random = new Random();
        random.nextBytes(data);

        return data;
    }

    private static File getFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        return file;
    }
}
