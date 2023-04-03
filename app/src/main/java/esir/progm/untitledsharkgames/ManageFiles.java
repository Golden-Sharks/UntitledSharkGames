package esir.progm.untitledsharkgames;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;


public class ManageFiles {

    private Context context;

    public ManageFiles(Context context) {
        this.context = context;
    }

    public void createFile(String fileName, String content) {
        if(!fileName.isEmpty()) {
            if (!exists(fileName)) {
                File dir = context.getFilesDir();
                File file = new File(dir, fileName);
                try {
                    FileOutputStream ostream = new FileOutputStream(file);
                    ostream.write(content.getBytes(StandardCharsets.UTF_8));
                    ostream.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("NON");
            }
        } else {
            System.err.println("sérieux?");
        }
    }

    public String readFile(String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        String content = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null){
                content += st;
            }
        } catch (Exception e) {
        }
        return content;
    }

    public boolean exists(String fileName) {
        File dir = context.getFilesDir();
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.getName().equals(fileName))
                return true;
        }
        return false;
    }

    public boolean erase(String fileName) {
        File dir = context.getFilesDir();
        File file = new File(dir, fileName);
        if (exists(fileName)) return file.delete();
        return false;
    }

}
