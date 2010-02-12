/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtporganize;

import java.io.*;
import java.util.Arrays;
import org.herac.tuxguitar.io.gtp.*;
import org.herac.tuxguitar.song.models.TGSong;
import java.util.List;
import org.herac.tuxguitar.song.factory.TGFactory;

/**
 *
 * @author Guillermo
 */
public class Main {

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
        String[] array = {"gt", "gpt", "gp3", "gp4", "gp5"};
        List OKexts = Arrays.asList(array);
        try {
            File tab = new File(fullpath);
            if (!(tab.exists() && tab.canRead() && tab.isFile())) {
                throw new FileNotFoundException("No se encontro el archivo.");
            }
            FileInputStream tstream = new FileInputStream(tab);
            switch (OKexts.indexOf(getExtension(tab.getName()))) {

                case 0://gt
                    break;
                case 1://gpt
                    //No sabemos si sera GP1 o GP2!!
                    break;
                case 2://gp3
                    GP5InputStream gp3in = new GP5InputStream(new GTPSettings());
                    gp3in.init(new TGFactory(), tstream);
                    try {
                        ret = gp3in.readSong();
                    } catch (GTPFormatException e) {
                        throw new IOException("Formato no soportado");
                    }
                    break;
                case 3://gp4
                    GP5InputStream gp4in = new GP5InputStream(new GTPSettings());
                    gp4in.init(new TGFactory(), tstream);
                    try {
                        ret = gp4in.readSong();
                    } catch (GTPFormatException e) {
                        throw new IOException("Formato no soportado");
                    }
                    break;
                case 4://gp5
                    GP5InputStream gpin = new GP5InputStream(new GTPSettings());
                    gpin.init(new TGFactory(), tstream);
                    try {
                        ret = gpin.readSong();
                    } catch (GTPFormatException e) {
                        throw new IOException("Formato no soportado");
                    }
                    break;

                case -1:
                    throw new IOException("Extension invalida: " + getExtension(tab.getName()));

            }

            //System.out.println("OK");

        } catch (NullPointerException e) {
            throw new IOException(e.toString());
        }
        return ret;
    }

    public static void main(String[] args) throws IOException {
        String CurLine = ""; // Line read from standard in
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);

        String cwd = (new File(".")).getCanonicalPath();
        System.out.println("Estamos en " + cwd);
        System.out.println("Escribir nombre de tab: ");
        CurLine = in.readLine();



    }
}
