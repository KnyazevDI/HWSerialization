package com.company;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.lang.System.out;
import static java.nio.file.Files.delete;

public class Main {

    public static void main(String[] args) throws IOException {
        StringBuilder log = new StringBuilder();

        mkdir("D://Games/src", log);
        mkdir("D://Games/res", log);
        mkdir("D://Games/savegames", log);
        mkdir("D://Games/temp", log);
        mkdir("D://Games/src/main", log);
        mkdir("D://Games/src/test", log);
        mkdir("D://Games/res/drawables", log);
        mkdir("D://Games/res/vectors", log);
        mkdir("D://Games/res/icons", log);

        try {
            createNewFile("D://Games/src/main", "Main.java", log);
            createNewFile("D://Games/src/main", "Utils.java", log);
            createNewFile("D://Games/temp", "temp.txt", log);
        } catch (IOException e) {
            out.println(e.getMessage());
        }

        String text = log.toString();
        try (FileOutputStream fos = new FileOutputStream("D://Games/temp/temp.txt")) {
            byte[] bytes = text.getBytes();
            fos.write(bytes, 0, bytes.length);
        } catch (IOException ex) {
            out.println(ex.getMessage());
        }

        GameProgress s1 = new GameProgress("save1", 90, 3, 88, 308.8);
        GameProgress s2 = new GameProgress("save2", 80, 2, 78, 402.0);
        GameProgress s3 = new GameProgress("save3", 78, 3, 50, 600.3);

        saveGame(s1);
        saveGame(s2);
        saveGame(s3);

        List<String> savesPath = new ArrayList<>();
        savesPath.add("save1.dat");
        savesPath.add("save2.dat");
        savesPath.add("save3.dat");

        zipFiles(savesPath, "D://Games/savegames/");

        delete(Path.of("D://Games/savegames/save1.dat"));
        delete(Path.of("D://Games/savegames/save2.dat"));
        delete(Path.of("D://Games/savegames/save3.dat"));

        openZip("D://Games/savegames/", "D://Games/savegames/zip_saves.zip");

        GameProgress save2 = openProgress("D://Games/savegames/save2.dat");
        System.out.println(save2);
    }

    public static void mkdir(String dirName, StringBuilder log) {
        File dir = new File(dirName);
        Date date = new Date();
        if (dir.mkdir())
           log.append("Каталог ").append(dirName).append(" создан в ").append(date.toString()).append("\n");
    }

    public static void createNewFile(String path, String fileName, StringBuilder log) throws IOException {
        File file = new File(path, fileName);
        Date date = new Date();
        if (file.createNewFile())
            log.append("файл ").append(fileName).append(" создан в ").append(date.toString()).append("\n");
    }

    public static void saveGame(GameProgress save) {
        try (FileOutputStream fos = new FileOutputStream("D://Games/savegames/" + save.getSave() + ".dat");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(save);
        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
    }

    public static void zipFiles(List<String> savesPath, String path) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(path + "zip_saves.zip"))) {

            for (String save: savesPath) {
                try (FileInputStream fis = new FileInputStream(path + save)) {
                    ZipEntry entry = new ZipEntry(save);
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void openZip(String path, String nameZip) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(nameZip))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fout = new FileOutputStream(path + name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static GameProgress openProgress(String name) {
        GameProgress gameProgress = null;
        try (FileInputStream fis = new FileInputStream(name);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }

}
