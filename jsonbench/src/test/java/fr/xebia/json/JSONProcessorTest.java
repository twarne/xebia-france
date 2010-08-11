package fr.xebia.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test la sérialisation et désérialisation JSON en utilisant les {@link JSONProcessor}.
 * 
 */
public class JSONProcessorTest extends TestCase {

    private static final Logger LOG = LoggerFactory.getLogger(JSONProcessorTest.class);

    /**
     * Create the test case
     * 
     * @param testName
     *            name of the test case
     */
    public JSONProcessorTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(JSONProcessorTest.class);
    }

    /**
     * Test 10k itérations de sérialisation/désérialisation d'un {@link DataBean}, sur le JSONProcessor donné en paramétre. Affiche ensuite
     * les statistiques de temps passé sur la sérialisation et la désérialisation.
     * 
     * @param proc
     *            Processeur JSON utilisé pour le test
     * @param type
     *            chaine type positionné sur le DataBean
     * @param wrap
     * @throws UnsupportedEncodingException
     */
    private void testProcessor(JSONProcessor proc, String type, DataBeanConverter wrap) throws UnsupportedEncodingException {

        int cycles = 10000;
        DataBean bean;
        LOG.debug("debut des itérations {}", type);
        StopWatch sw = new StopWatch();
        TimeStat serTime = new TimeStat();
        TimeStat deserTime = new TimeStat();
        ByteArrayOutputStream strm = new ByteArrayOutputStream(200);
        for (int i = 0; i < cycles; i++) {
            strm.reset();
            bean = new DataBean(type);
            bean.setId(i);

            sw.start();
            proc.toJSON(strm, bean);
            serTime.add(sw.stop());
            String res = strm.toString("UTF-8");

            ByteArrayInputStream in = new ByteArrayInputStream(res.getBytes());
            sw.start();
            DataBean db = wrap.getDataBean(proc.fromJSON(in, DataBean.class));
            deserTime.add(sw.stop());
            LOG.debug("Source is {}, Dest is {}", bean, db);
            assertEquals(bean.getType(), db.getType());
            assertEquals(bean.getVersion(), bean.getVersion());
            assertEquals(bean.getDate().toString(), db.getDate().toString());
            assertEquals(bean.isError(), db.isError());
            assertEquals(bean.getId(), db.getId());
        }

        LOG.info("TimeStats serializing with {}\t\t: Min={}, Max={}, Avg={}", new String[]{type, serTime.min + "", serTime.max + "",
                serTime.getAvg(cycles) + ""});
        LOG.info("TimeStats deserializing with {}\t\t: Min={}, Max={}, Avg={}", new String[]{type, deserTime.min + "", deserTime.max + "",
                deserTime.getAvg(cycles) + ""});

    }

    /**
     * Test de l'implémentation {@link SojoProcessor}.
     * 
     * @throws UnsupportedEncodingException
     */
    public void testSojoProcessor() throws UnsupportedEncodingException {

        testProcessor(new SojoProcessor(), "Sojo", DEFAULT_CONVERTER);

    }

    /**
     * Test de l'implémentation {@link JacksonProcessor}.
     * 
     * @throws UnsupportedEncodingException
     */
    public void testJacksonProcessor() throws UnsupportedEncodingException {

        testProcessor(new JacksonProcessor(), "Jackson", DEFAULT_CONVERTER);

    }

    /**
     * Test de l'implémentation {@link GSonProcessor}.
     * 
     * @throws UnsupportedEncodingException
     */
    public void testGSonProcessor() throws UnsupportedEncodingException {

        testProcessor(new GSonProcessor(), "Gson", DEFAULT_CONVERTER);

    }

    /**
     * Test de l'implémentation {@link JSONOrgProcessor} Fournit un {@link DataBeanConverter} pour convertir manuellement les
     * {@link JSONObject} en {@link DataBean}.
     * 
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public void testJSonOrgProcessor() throws UnsupportedEncodingException, JSONException {

        testProcessor(new JSONOrgProcessor(), "JSON", new DataBeanConverter() {

            private SimpleDateFormat sd = new SimpleDateFormat("EEE MMM dd hh:mm:ss zz yyyy", Locale.US);

            public DataBean getDataBean(Object obj) {
                JSONObject db = (JSONObject) obj;
                DataBean res = new DataBean();
                try {
                    res.setError(db.getBoolean("error"));
                    res.setId(db.getInt("id"));
                    res.setType(db.getString("type"));
                    res.setVersion(db.getLong("version"));
                    res.setDate(sd.parse(db.getString("date")));
                } catch (JSONException e) {
                    LOG.error("", e);
                } catch (ParseException e) {
                    LOG.error("", e);
                }
                return res;
            }
        });
    }

    /**
     * Test l'implémentation {@link FlexJsonProcessor}
     * 
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public void testFlexJsonProcessor() throws UnsupportedEncodingException, JSONException {

        testProcessor(new FlexJsonProcessor(), "FlexJson", DEFAULT_CONVERTER);
    }

    /**
     * Test l'implémentation {@link XStreamProcessor}
     * 
     * @throws UnsupportedEncodingException
     * @throws JSONException
     */
    public void testXStreamJsonProcessor() throws UnsupportedEncodingException, JSONException {

        testProcessor(new XStreamProcessor(), "XStreamJson", DEFAULT_CONVERTER);
    }

    /**
     * Convertisseur par défaut. Il se contente de retourner l'objet source en appliquant un cast en {@link DataBean} dessus.
     */
    private static final DataBeanConverter DEFAULT_CONVERTER = new DataBeanConverter() {

        public DataBean getDataBean(Object obj) {
            return (DataBean) obj;
        };
    };

    /**
     * Utilitaire pour convertir un objet sorti d'un JSONProcessor en {@link DataBean}. Principalement utile pour le
     * {@link JSONOrgProcessor} qui retourne toujours un objet {@link JSONObject} et non un {@link DataBean}. Cela permet de réaliser
     * manuellement le mapping du bean si nécéssaire.
     * 
     * @author slm
     * 
     */
    private interface DataBeanConverter {

        /**
         * Converti l'objet obj en DataBean.
         * 
         * @param obj
         *            source
         * @return Le bean résultat de la sérialisation
         */
        DataBean getDataBean(Object obj);

    }

    /**
     * Objet utilisé pour calculer les temps de traitements minimum, maximum et moyen. A chaque itération, il suffit d'ajouter le temps de
     * traitement au TimeStat pour mettre à jour les statistiques de temps.
     * 
     * @author slm
     * 
     */
    class TimeStat {

        long avg = 0;

        long min = 0;

        long max = 0;

        public void add(long time) {
            avg += time;

            if (min == 0) {
                min = time;
                max = time;
            } else if (min > time) {
                min = time;
            } else if (max < time) {
                max = time;
            }
        }

        public long getAvg(int cycles) {
            return avg / cycles;
        }
    }

    /**
     * Compteur de temps en nanosecondes
     * 
     * @author slm
     * 
     */
    class StopWatch {

        long nanos;

        /**
         * Initialisation du compteur
         */
        public void start() {
            nanos = System.nanoTime();
        }

        /**
         * Arret du compteur et retour du temps en nanosecondes écoulé depuis le dernier appel à {@link #start()}
         * 
         * @return
         */
        public long stop() {
            return System.nanoTime() - nanos;
        }

    }
}
