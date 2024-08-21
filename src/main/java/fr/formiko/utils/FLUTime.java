package fr.formiko.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * {@summary Time functions.}<br>
 * 
 * @author Hydrolien
 */
public class FLUTime {
    /**
     * {@summary return time with as specify number of unit.}<br>
     * If language file is not initialize, it will use french letter: 3j 23h 59min 10,1267s
     * 
     * @param ms        times in ms.
     * @param nbrOfUnit number of units to include in the return string.
     * @param dayOn     enable or disable day as a unit.
     * @since 0.0.4
     */
    public static String msToTime(long ms, int nbrOfUnit, boolean dayOn) {
        if (nbrOfUnit < 1) {
            return "";
        }
        String[] ts = {"t.d", "t.h", "t.min", "t.s", "t.ms"};
        long[] tl = msToTimeLongArray(ms, dayOn);
        int k = 0;
        int i = 0;
        StringBuilder r = new StringBuilder();
        boolean first = true;
        while (k < nbrOfUnit && i < 5) {
            if (tl[i] > 0) {
                if (first) {
                    first = false;
                } else {
                    r.append(" ");
                }
                if (i == 3 && k + 1 < nbrOfUnit && tl[i + 1] > 0) { // si on doit traiter les s et les ms ensembles.
                    String s = "" + tl[i + 1];
                    while (s.length() < 3) {
                        s = "0" + s;
                    }
                    while (s.length() > 1 && s.charAt(s.length() - 1) == '0') {
                        s = s.substring(0, s.length() - 1);
                    }
                    r.append(i).append(",").append(s).append(ts[i].substring(2));
                    k++;
                    i++;
                } else {
                    r.append(tl[i]).append(ts[i].substring(2));
                }
                k++;
            }
            i++;// pour ne pas sortir du tableau.
        }
        if (first) {
            r.append(tl[4]).append(ts[4].substring(2));
        }
        return r.toString();
    }
    public static String msToTime(long ms) { return msToTime(ms, 2, true); }

    /**
     * {@summary return time on a long [].}
     * 
     * @param ms    times in ms.
     * @param dayOn enable or disable day as a unit.
     */
    public static long[] msToTimeLongArray(long ms, boolean dayOn) {
        long[] tr = new long[5];
        if (ms < 0) {
            tr[4] = -1;
            return tr;
        }
        int nbrMsD = 86400000;
        int nbrMsH = 3600000;
        int nbrMsM = 60000;
        int nbrMsS = 1000;
        long d, h, m, s;
        if (dayOn) {
            d = ms / nbrMsD;
            h = (ms % nbrMsD) / nbrMsH;
        } else {
            d = 0;
            h = ms / nbrMsH;
        }
        m = (ms % nbrMsH) / nbrMsM;
        s = (ms % nbrMsM) / nbrMsS;
        ms = ms % nbrMsS;
        tr[0] = d;
        tr[1] = h;
        tr[2] = m;
        tr[3] = s;
        tr[4] = ms;
        return tr;
    }

    /**
     * {@summary Try to stop execution of the programme during some ms.}
     * 
     * @param ms number of ms to wait before continue.
     */
    public static void sleep(int ms) {
        if (ms < 1) {
            return;
        }
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            // return;
        }
    }

    /**
     * {@summary return the current time as a String 2019-12-31 23-59-59.999}
     */
    public static String currentTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss.SSS");
        return df.format(System.currentTimeMillis());
    }
}
