package gptorganize.base;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.herac.tuxguitar.io.gtp.*;
import org.herac.tuxguitar.song.models.TGSong;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.herac.tuxguitar.song.factory.TGFactory;

/**
 *
 * @author Guillermo
 */
public class GPTOrganize {

    private static String[] array = {"gpt", "gp3", "gp4", "gp5"};
    public static List OKexts = Arrays.asList(array);
    private static String unkartist = "Unknown Artist";
    private static String unkalbum = "Unknown Album";

    public static void test() throws IOException {
        String CurLine = ""; // Line read from standard in
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);

        String cwd = (new File(".")).getCanonicalPath();
        System.out.println("Estamos en " + cwd);
        LinkedList<File> allfiles = new LinkedList();

        for (int i = 0; i < OKexts.size(); i++) {
            allfiles.addAll(getAllFilesThatMatchFilenameExtension(cwd, "*." + OKexts.get(i)));
        }

        System.out.println("Partituras encontradas:" + allfiles.size());
        System.out.println("----------------------------");

        Iterator<File> ite = allfiles.iterator();

        while (ite.hasNext()) {
            File f = ite.next();
            TGSong s = readSong(f);
            String newfilename;
            System.out.println(showInfo(s));
            if (!s.getName().isEmpty()) {
                newfilename = s.getName() + f.getName().substring(f.getName().lastIndexOf("."));
            } else {
                newfilename = f.getName();
            }
            newfilename = sanitizeFilename(newfilename);
            File newname = new File(newfilename);
            if (!f.renameTo(newname)) {
                System.out.println("Error renamig " + f.getName() + " to " + newname.getName());
            } else {
                System.out.println("New filename:" + newname);
                String art = s.getArtist(), alb = s.getAlbum();
                art = sanitizeFilename(art);
                alb = sanitizeFilename(alb);
                if (alb.isEmpty()) {
                    alb = unkalbum;
                }
                if (art.isEmpty()) {
                    art = unkartist;
                }
                String newdest = art + File.separator + alb + File.separator;
                newdest = cwd + File.separator + sanitizeFilename(newdest);
                File dest = new File(newdest);
                System.out.println("New dest:" + dest);
                try {
                    FileUtils.moveFileToDirectory(newname, dest, true);
                } catch (IOException e) {
                    System.out.println("Error!!:" + e);
                }
            }

            System.out.println("----------------------------");
        }

        System.out.println("No hay más archivos");

    }

    private static Collection getAllFilesThatMatchFilenameExtension(String directoryName, String extension) {
        File directory = new File(directoryName);
        return FileUtils.listFiles(directory, new WildcardFileFilter(extension), null);
    }

    private static String getExtension(String filename) {
        String ext = filename.substring(filename.lastIndexOf(".") + 1);
        return ext;
    }

    public static void visitAllFiles(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                visitAllFiles(new File(dir, children[i]));
            }
        } else {
            //process(dir);
        }
    }

    public static TGSong readSong(String fullpath) throws IOException {
        TGSong ret = null;
        try {
            File tab = new File(fullpath);
            ret = readSong(tab);
        } catch (NullPointerException e) {
            throw new IOException(e.toString());
        }
        return ret;
    }

    public static TGSong readSong(File tab) throws IOException {
        TGSong ret = null;
        try {
            if (!(tab.exists() && tab.canRead() && tab.isFile())) {
                throw new FileNotFoundException("No se encontro el archivo.");
            }

            FileInputStream tstream;
            GTPSettings settings = new GTPSettings();
            TGFactory fact = new TGFactory();

            GP1InputStream gp1in = new GP1InputStream(settings);
            GP2InputStream gp2in = new GP2InputStream(settings);
            GP3InputStream gp3in = new GP3InputStream(settings);
            GP4InputStream gp4in = new GP4InputStream(settings);
            GP5InputStream gp5in = new GP5InputStream(settings);

            try {
                tstream = new FileInputStream(tab);
                gp1in.init(fact, tstream);
                ret = gp1in.readSong();
            } catch (GTPFormatException e) {
                try {
                    tstream = new FileInputStream(tab);
                    gp2in.init(fact, tstream);
                    ret = gp2in.readSong();
                } catch (GTPFormatException e2) {
                    try {
                        tstream = new FileInputStream(tab);
                        gp3in.init(new TGFactory(), tstream);
                        ret = gp3in.readSong();
                    } catch (GTPFormatException e3) {
                        try {
                            tstream = new FileInputStream(tab);
                            gp4in.init(new TGFactory(), tstream);
                            ret = gp4in.readSong();
                        } catch (GTPFormatException e4) {
                            try {
                                tstream = new FileInputStream(tab);
                                gp5in.init(new TGFactory(), tstream);
                                ret = gp5in.readSong();
                            } catch (GTPFormatException e5) {
                                throw new IOException("Formato no soportado");
                            }
                        }
                    }

                }
            }

        } catch (NullPointerException e) {
            throw new IOException(e.toString());
        }

        return ret;
    }

    public static String showInfo(TGSong song) {
        String ret = "";
        ret += "Song: " + song.getName() + "\n";
        ret += "Album: " + song.getAlbum() + "\n";
        ret += "Artist: " + song.getArtist() + "\n";
        ret += "Author: " + song.getAuthor();
        return ret;
    }

    private static String sanitizeDirname(String s) {
        String ret = s;
        while (s.endsWith(" ")) {
            s = s.substring(0, s.length());
        }
        s.replaceAll("s/[^\\w\\&%'`\\-\\@{}~!#\\(\\)&_\\^\\+,\\.=\\[\\]]//g", "_");
        return ret;
    }

    private static String sanitizeFilename(String s) {
        String ret = s;
        while (s.startsWith(" ")) {
            s = s.substring(1);
        }
        s.replaceAll("s/[^\\w\\&%'`\\-\\@{}~!#\\(\\)&_\\^\\+,\\.=\\[\\]]//g", "_");
        return ret;
    }
}
