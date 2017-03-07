package crixec.onbooting.download;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import crixec.onbooting.util.Utils;

/**
 * Created by crixec on 17-3-6.
 */

public class DownloadTask extends AsyncTask<String, Void, File> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
    }

    @Override
    protected File doInBackground(String... params) {
        File saveFile = new File(params[2]);
        FileWriter writer = null;
        URL url;
        BufferedReader reader = null;
        try {
            url = new URL(params[0]);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            writer = new FileWriter(saveFile, false);
            String line = reader.readLine();
            while (line != null) {
                writer.write(line);
                writer.write("\n");
                writer.flush();
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.close(reader, writer);
        }
        return saveFile;
    }
}
