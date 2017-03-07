package crixec.onbooting.script;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crixec on 17-3-3.
 */

public class ScriptManager {
    private static String STORE_FILE;
    private static Context sContext;
    private static List<ScriptBean> beans = new ArrayList<>();

    public static void init(Context context) {
        if (STORE_FILE == null) {
            STORE_FILE = new File(context.getFilesDir(), "scripts.xml").getPath();
            sContext = context;
        }
    }

    public static List<ScriptBean> readAll() {
        beans.clear();
        try {
            FileReader reader = new FileReader(STORE_FILE);
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(reader);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("script")) {
                        ScriptBean bean = new ScriptBean();
                        bean.setScriptName(parser.getAttributeValue(null, "script-name"));
                        bean.setRealPath(parser.getAttributeValue(null, "real-path"));
                        bean.setBootable(Boolean.valueOf(parser.getAttributeValue(null, "bootable")));
                        bean.setAsRoot(Boolean.valueOf(parser.getAttributeValue(null, "as-root")));
                        beans.add(bean);
                    }
                }
                eventType = parser.next();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (beans.size() == 0) {
            beans.add(new ScriptBean("Untitled Script", false, randomLocalShell(), false));
        }
        return beans;
    }

    public static void changeBean(ScriptBean oldBean, ScriptBean newBean) {
        if (oldBean != null && newBean != null && oldBean != newBean && beans.contains(oldBean)) {
            int index = beans.indexOf(oldBean);
            beans.remove(index);
            beans.add(index, newBean);
        }
    }

    public static String randomLocalShell() {
        File file = new File(sContext.getFilesDir(), System.currentTimeMillis() + ".sh");
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write("#!/system/bin/sh\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {

        }
        return file.getPath();
    }

    public static void addScript(ScriptBean scriptBean) {
        if (scriptBean != null && !beans.contains(scriptBean)) {
            beans.add(scriptBean);
        }
    }

    public static boolean removeScript(int index) {
        if (index < 0) return false;
        beans.remove(index);
        return true;
    }

    public static boolean removeScript(ScriptBean scriptBean) {
        if (scriptBean != null && beans.contains(scriptBean)) {
            beans.remove(scriptBean);
            return new File(scriptBean.getRealPath()).delete();
        }
        return false;
    }

    public static StringBuilder readScriptContent(ScriptBean bean) {
        StringBuilder content = new StringBuilder();
        File file = new File(bean.getRealPath());
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line = reader.readLine();
                while (line != null) {
                    content.append(line).append("\n");
                    line = reader.readLine();
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    public static List<ScriptBean> getBootableScripts() {
        List<ScriptBean> bootables = new ArrayList<>();
        if (beans.size() != 0) {
            for (ScriptBean bean : beans) {
                if (bean.isBootable()) bootables.add(bean);
            }
        }
        return bootables;
    }

    public static void writeScriptContent(ScriptBean bean, StringBuilder content) {
        try {
            File file = new File(bean.getRealPath());
            FileWriter fw = new FileWriter(file, false);
            fw.write(content.toString());
            fw.flush();
            fw.close();
            file.setExecutable(true);
            file.setReadable(true);
            file.setWritable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ScriptBean> getBeans() {
        if (beans.size() == 0) {
            beans.add(new ScriptBean("Untitled Script", false, randomLocalShell(), false));
        }
        return beans;
    }

    public static void saveAll() {
        XmlSerializer serializer = Xml.newSerializer();
        try {
            FileWriter writer = new FileWriter(STORE_FILE, false);
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "scripts");
            for (ScriptBean bean : beans) {
                serializer.startTag(null, "script");
                serializer.attribute(null, "script-name", bean.getScriptName());
                serializer.attribute(null, "bootable", String.valueOf(bean.isBootable()));
                serializer.attribute(null, "as-root", String.valueOf(bean.isAsRoot()));
                serializer.attribute(null, "real-path", bean.getRealPath());
                serializer.endTag(null, "script");
            }
            serializer.endTag(null, "scripts");
            serializer.flush();
            serializer.endDocument();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
