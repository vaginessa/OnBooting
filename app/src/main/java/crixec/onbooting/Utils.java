package crixec.onbooting;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by crixec on 17-3-6.
 */

public class Utils {
    public static void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static StringBuilder readFile(String filePath) {
        return readFile(filePath, "UTF-8");
    }

    public static StringBuilder readFile(File file) {
        return readFile(file.getPath(), "UTF-8");
    }

    public static StringBuilder readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (!file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(reader);
        }
        return fileContent;
    }

    public static void writeFile(File file, String content) {
        writeFile(file.getPath(), content, false);
    }

    public static void writeFile(String filePath, String content, boolean append) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fileWriter);
        }
    }
}
