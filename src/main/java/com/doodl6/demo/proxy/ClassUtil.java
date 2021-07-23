package com.doodl6.demo.proxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClassUtil {

    private static final String CLASS_DIR = "target/classes";

    public static void saveClass(byte[] bytes, String className) {
        String filePath = CLASS_DIR + File.separatorChar + className.replace('.', File.separatorChar) + ".class";
        int pos = filePath.lastIndexOf(File.separatorChar);
        if (pos > 0) {
            String dir = filePath.substring(0, pos);
            if (!dir.equals(".")) {
                (new File(dir)).mkdirs();
            }
        }

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
