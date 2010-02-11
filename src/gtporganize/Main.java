/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gtporganize;

import java.io.*;
import java.util.Arrays;
import org.herac.tuxguitar.io.gtp.*;
import org.herac.tuxguitar.io.base.TGFileFormat;
import org.herac.tuxguitar.song.models.TGSong;
import java.util.List;

/**
 *
 * @author Guillermo
 */
public class Main {

    private static String getExtension(String filename) {
        String ext = filename.substring(filename.lastIndexOf(".") + 1);
        return ext;
    }

    public static void main(String[] args) throws IOException {
        String CurLine = ""; // Line read from standard in
        GTPInputStream gtptab;
        String[] array = {"gt", "gpt", "gp3", "gp4", "gp5"};
        List OKexts = Arrays.asList(array);
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);

        String cwd = (new File(".")).getCanonicalPath();
        System.out.println("Estamos en " + cwd);
        System.out.println("Escribir nombre de tab: ");
        CurLine = in.readLine();
        try {
            System.out.println("Abriendo \"" + cwd + "\\" + CurLine + "\" ...");
            File tab = new File(cwd + "\\" + CurLine);
            if (!(tab.exists() && tab.canRead() && tab.isFile())) {
                throw new FileNotFoundException("No se encontro el archivo.");
            }

            switch (OKexts.indexOf(getExtension(tab.getName()))) {

                case 0://gt
                    break;
                case 1://gpt
                    break;
                case 2://gp3
                    break;
                case 3://gp4
                    break;
                case 4://gp5
                    break;

                case -1:
                    throw new IOException("Extension invalida: " + getExtension(tab.getName()));

            }

            System.out.println("OK");

        } catch (NullPointerException e) {
            System.out.println("Error");
        } catch (IOException e) {
            System.out.println(e);
        }


    }
}
