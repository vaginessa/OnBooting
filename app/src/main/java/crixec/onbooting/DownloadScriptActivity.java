package crixec.onbooting;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class DownloadScriptActivity extends AppCompatActivity {

    private AppCompatTextView textView;
    private File FILE_SCRIPT_LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_script);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        FILE_SCRIPT_LIST = new File(getFilesDir(), "scripts.json");
        textView = (AppCompatTextView) findViewById(R.id.text1);
        textView.setTypeface(Typeface.MONOSPACE);
        new DownloadScriptListTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (getSupportActionBar() != null) {
            if (item.getItemId() == android.R.id.home) {
                this.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class DownloadScriptListTask extends AsyncTask<Void, Void, File> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(DownloadScriptActivity.this);
            dialog.setProgressStyle(R.style.ProgressBar);
            dialog.setMessage(getString(R.string.loading_script_list));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            dialog.dismiss();
            textView.setText(Utils.readFile(FILE_SCRIPT_LIST));
        }

        @Override
        protected File doInBackground(Void... params) {
            FileWriter writer = null;
            URL url;
            BufferedReader reader = null;
            try {
                url = new URL(Constant.SCRIPT_LIST_URL);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(8000);
                connection.setRequestProperty("accept", "*/*");
                connection.setRequestProperty("connection", "Keep-Alive");
                connection.setRequestProperty("user-agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                writer = new FileWriter(FILE_SCRIPT_LIST, false);
                String line = reader.readLine();
                while (line != null) {
                    Log.i(getClass().getSimpleName(), line);
                    writer.write(line);
                    writer.flush();
                    line = reader.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Utils.close(reader, writer);
            }
            return null;
        }
    }
}