package fr.xebia.json;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Les processeurs JSON sont des objets chargé de convertir un bean source en flux de charactères JSON, et de charger un flux JSON dans un
 * bean dont la classe est fournie.
 * 
 * @author slm
 * 
 */
public interface JSONProcessor {

    /**
     * convertit l'objet src en JSON écrit sur le flux out
     * 
     * @param out
     * @param src
     */
    void toJSON(OutputStream out, Object src);

    /**
     * lit un objet JSON le flux in et le mappe sur un objet de la classe dst
     * 
     * @param in
     * @param dst
     * @return
     */
    Object fromJSON(InputStream in, Class dst);

}
