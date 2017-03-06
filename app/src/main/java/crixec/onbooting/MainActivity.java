package crixec.onbooting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Collections;

import crixec.commom.widget.AlterEditor;

public class MainActivity extends AppCompatActivity implements OnScriptItemClickListener, ScriptDialog.IOnScriptChanged {

    private ScriptAdapter recycleAdapter;
    private ScriptBean currentScript;
    private AlterEditor editor;
    private StringBuilder content = new StringBuilder();
    private int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        initViews();
        loadScripts();
        refreshAdapter();
    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        recycleAdapter = new ScriptAdapter(ScriptManager.getBeans());
        recycleAdapter.setOnScriptItemClickListener(this);
        recyclerView.setAdapter(recycleAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        editor = (AlterEditor) findViewById(R.id.editor);
        findViewById(R.id.newScriptLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScriptBean bean = new ScriptBean("Untitled", false, ScriptManager.randomLocalShell(), false);
                new ScriptDialog(bean, MainActivity.this, ScriptDialog.ACTION_NEW).show(getSupportFragmentManager(), "");
            }
        });
    }

    private void loadScripts() {
        ScriptManager.readAll();
        sortScripts();
        if (currentScript == null)
            switchScript(ScriptManager.getBeans().get(0));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScriptManager.saveAll();
    }

    private void switchScript(final ScriptBean scriptBean) {
        if (scriptBean != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    content = ScriptManager.readScriptContent(scriptBean);
                    Log.i("aa", content.toString());
                    editor.post(new Runnable() {
                        @Override
                        public void run() {
                            editor.setText(content);
                            setTitle(scriptBean.getScriptName());
                        }
                    });
                }
            }).start();
            currentScript = scriptBean;
            current = ScriptManager.getBeans().indexOf(scriptBean);
        }
    }

    private void sortScripts() {
        Collections.sort(ScriptManager.getBeans(), new ScriptSortComparator());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!isSaved()) {
                showSaveDialog(current);
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            new ScriptDialog(currentScript, this, ScriptDialog.ACTION_CHANGE).show(getSupportFragmentManager(), "");
            return true;
        } else if (id == R.id.action_run) {
            saveScriptContent();
            new RunScriptTask(this, currentScript).execute();
            return true;
        } else if (id == R.id.action_download_scripts) {
            Intent intent = new Intent(this, DownloadScriptActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void Tip(int res) {
        Tip(getText(res));
    }

    private void Tip(CharSequence text) {
        Snackbar.make(editor, text, Snackbar.LENGTH_SHORT).show();
    }

    private void saveScriptContent() {
        ScriptManager.writeScriptContent(currentScript, editor.content());
        content = editor.content();
        Tip(R.string.save_successful);
    }

    @Override
    public void onScriptItemClick(int position) {
        if (!isSaved()) {
            showSaveDialog(position);
        } else {
            switchScript(ScriptManager.getBeans().get(position));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void showSaveDialog(final int position) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_save)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveScriptContent();
                        switchScript(ScriptManager.getBeans().get(position));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switchScript(ScriptManager.getBeans().get(position));
                    }
                })
                .show();
    }

    @Override
    public void onScriptItemLongClick(int position) {
        new ScriptDialog(ScriptManager.getBeans().get(position), this, ScriptDialog.ACTION_CHANGE).show(getSupportFragmentManager(), "");
    }

    private boolean isSaved() {
        return editor.content().toString().equals(content.toString());
    }

    @Override
    public void onChanged(ScriptBean newBean, int action) {
        if (action == ScriptDialog.ACTION_NEW) {
            ScriptManager.addScript(newBean);
        } else if (action == ScriptDialog.ACTION_CHANGE) {
            saveScriptContent();
            ScriptManager.changeBean(currentScript, newBean);
            switchScript(newBean);
        }
        refreshAdapter();
    }

    private void refreshAdapter() {
        ScriptManager.saveAll();
        recycleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleted(ScriptBean bean) {
        ScriptManager.removeScript(bean);
        Tip(R.string.delete_successful);
        switchScript(ScriptManager.getBeans().get(0));
        refreshAdapter();
    }
}
