package crixec.onbooting.download;

import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import crixec.onbooting.Constant;
import crixec.onbooting.script.OnScriptItemClickListener;
import crixec.onbooting.R;
import crixec.onbooting.script.ScriptBean;
import crixec.onbooting.script.ScriptManager;
import crixec.onbooting.util.Utils;
import crixec.onbooting.download.infodialog.Info;
import crixec.onbooting.download.infodialog.InfoAdapter;
import crixec.onbooting.download.webscript.WebScript;
import crixec.onbooting.download.webscript.WebScriptAdapter;

public class DownloadScriptActivity extends AppCompatActivity implements OnScriptItemClickListener, DialogInterface.OnClickListener {

    private File FILE_SCRIPT_LIST;
    private RecyclerView recyclerView;
    private WebScriptAdapter recyclerAdapter;
    private ArrayList<WebScript> scripts = new ArrayList<WebScript>();
    private ArrayList<Info> infos = new ArrayList<>();
    private WebScript webScript;

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
        recyclerView = (RecyclerView) findViewById(R.id.script_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recyclerAdapter = new WebScriptAdapter(scripts);
        recyclerAdapter.setOnScriptItemClickListener(this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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

    private void parseJson(String jsonContent) {
        try {
            JSONArray jsonArray = new JSONArray(jsonContent);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                WebScript webScript = new WebScript();
                webScript.setScriptName(object.getString("name"));
                webScript.setScriptBean(new ScriptBean(object.getString("name"), object.getBoolean("isBootable"), ScriptManager.randomLocalShell(), object.getBoolean("asRoot")));
                webScript.setAuthor(object.getString("author"));
                webScript.setDescription(object.getString("description"));
                webScript.setDownloadUrl(object.getString("downloadUrl"));
                webScript.setLength(object.getInt("length"));
                webScript.setVersionName(object.getString("versionName"));
                webScript.setVersionCode(object.getInt("versionCode"));
                scripts.add(webScript);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showScriptInfoDialog(WebScript webScript) {
        this.webScript = webScript;
        final View view = getLayoutInflater().inflate(R.layout.dialog_script_info, null, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.script_info_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        InfoAdapter infoAdapter = new InfoAdapter(infos);
        infoAdapter.setOnScriptItemClickListener(new OnScriptItemClickListener() {
            @Override
            public void onScriptItemClick(int position) {
                ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText(infos.get(position).getValue());
                Tip(view, R.string.copied);
            }

            @Override
            public void onScriptItemLongClick(int position) {

            }
        });
        recyclerView.setAdapter(infoAdapter);
        infos.clear();
        infos.add(new Info(getString(R.string.script_name), webScript.getScriptName()));
        infos.add(new Info(getString(R.string.description), webScript.getDescription()));
        infos.add(new Info(getString(R.string.author), webScript.getAuthor()));
        infos.add(new Info(getString(R.string.version_name), webScript.getVersionName()));
        infos.add(new Info(getString(R.string.version_code), webScript.getVersionCode() + ""));
        infos.add(new Info(getString(R.string.bootable), webScript.getScriptBean().isBootable() + ""));
        infos.add(new Info(getString(R.string.rootable), webScript.getScriptBean().isAsRoot() + ""));
        infos.add(new Info(getString(R.string.download_url), webScript.getDownloadUrl()));
        infoAdapter.notifyDataSetChanged();
        new AlertDialog.Builder(this)
                .setView(view)
                .setNeutralButton(R.string.download, this)
                .setPositiveButton(android.R.string.cancel, this)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onScriptItemClick(int position) {
        showScriptInfoDialog(scripts.get(position));
    }

    @Override
    public void onScriptItemLongClick(int position) {
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            // download
            new DownloadScriptTask(webScript.getDownloadUrl()).execute();
        }
    }

    private void Tip(int res) {
        Tip(getString(res));
    }

    private void Tip(View view, int res) {
        Tip(view, getString(res));
    }

    private void Tip(View view, CharSequence text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show();
    }

    private void Tip(CharSequence text) {
        Tip(recyclerView, text);
    }

    class DownloadScriptTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String downloadUrl;
        private String savePath;

        public DownloadScriptTask(String url) {
            this.downloadUrl = url;
            this.savePath = webScript.getScriptBean().getRealPath();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(DownloadScriptActivity.this);
            dialog.setProgressStyle(R.style.ProgressBar);
            dialog.setTitle(webScript.getScriptName());
            dialog.setMessage(getString(R.string.downloading));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            ScriptManager.addScript(webScript.getScriptBean());
            Tip(R.string.download_successful);
        }

        @Override
        protected Void doInBackground(Void... params) {
            FileWriter writer = null;
            URL url;
            BufferedReader reader = null;
            try {
                url = new URL(downloadUrl);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(6000);
                connection.setRequestProperty("accept", "*/*");
                connection.setRequestProperty("connection", "Keep-Alive");
                connection.setRequestProperty("user-agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                writer = new FileWriter(savePath, false);
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
            return null;
        }
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
            scripts.clear();
            if (file != null) {
                parseJson(Utils.readFile(file).toString());
                recyclerAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected File doInBackground(Void... params) {
            FileWriter writer = null;
            URL url;
            BufferedReader reader = null;
            try {
                url = new URL(Constant.SCRIPT_LIST_URL);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(6000);
                connection.setRequestProperty("accept", "*/*");
                connection.setRequestProperty("connection", "Keep-Alive");
                connection.setRequestProperty("user-agent",
                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                writer = new FileWriter(FILE_SCRIPT_LIST, false);
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
            return FILE_SCRIPT_LIST;
        }
    }
}