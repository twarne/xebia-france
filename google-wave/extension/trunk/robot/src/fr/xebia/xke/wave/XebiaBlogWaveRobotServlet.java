package fr.xebia.xke.wave;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.google.wave.api.AbstractRobotServlet;
import com.google.wave.api.Blip;
import com.google.wave.api.Event;
import com.google.wave.api.Range;
import com.google.wave.api.RobotMessageBundle;
import com.google.wave.api.StyleType;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;

@SuppressWarnings("serial")
public class XebiaBlogWaveRobotServlet extends AbstractRobotServlet {

	private static final Logger LOG = Logger
			.getLogger(XebiaBlogWaveRobotServlet.class.getName());

	private final static Set<String> TAGS = new TreeSet<String>();

	private final static String URL_PREFIX_TAG = "http://blog.xebia.fr/tag/";

	private final static String URL_PREFIX_AUTHORS = "http://blog.xebia.fr/author/";

	private final static Map<String, String> GOOGLE_ACCOUNTS = new HashMap<String, String>();

	private final static String COMMAND_PREFIX_AUTHOR = "whois_blog?";

	@Override
	public void init() throws ServletException {
		super.init();
		initializeTagSet();
	}

	@SuppressWarnings("unchecked")
	private void initializeTagSet() {
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc;
		try {
			doc = saxBuilder.build("http://blog.xebia.fr/sitemap.xml");

			List<Element> list = doc.getRootElement().getChildren();
			for (Element url : list) {
				Element location = url.getChild("loc", url.getNamespace());
				if (location != null) {
					if (location.getValue().startsWith(URL_PREFIX_TAG)) {
						String tag = location.getValue().replace(
								URL_PREFIX_TAG, "").replace("/", "");
						TAGS.add(tag);
					}
				}
			}

			// link author (using xebia mail) and wave accounts.
			GOOGLE_ACCOUNTS.put("erwan.alliaume@googlewave.com", "ealliaume");
			GOOGLE_ACCOUNTS.put("pabs.agro@googlewave.com", "plopez");
			GOOGLE_ACCOUNTS.put("christophe.heubes@googlewave.com", "cheubes");
			GOOGLE_ACCOUNTS.put("nicolas.griso@googlewave.com", "ngriso");
			GOOGLE_ACCOUNTS.put("fathallah.amin@googlewave.com", "afathallah");
			GOOGLE_ACCOUNTS.put("bmoussaud@googlewave.com", "bmoussaud");
			GOOGLE_ACCOUNTS.put("david.galichet@googlewave.com", "dgalichet");
			GOOGLE_ACCOUNTS.put("ellene.dijoux@googlewave.com", "edijoux");
			GOOGLE_ACCOUNTS.put("garnaud25@googlewave.com", "garnaud");
			GOOGLE_ACCOUNTS.put("gbodet@googlewave.com", "gbodet");
			GOOGLE_ACCOUNTS.put("cyrille.leclerc@googlewave.com",
					"cleclerc@googlewave.com");
			GOOGLE_ACCOUNTS.put("aurelien.masse.pro@googlewave.com", "amasse");
			GOOGLE_ACCOUNTS.put("aurelien.maury@googlewave.com", "amaury");
			GOOGLE_ACCOUNTS.put("emmanuel.servent@googlewave.com", "eservent");
			GOOGLE_ACCOUNTS.put("francois.marot@googlewave.com", "fmarot");
			GOOGLE_ACCOUNTS.put("guillaume.carre@googlewave.com", "gcarre");
			GOOGLE_ACCOUNTS.put("guillaume.mathias@googlewave.com", "gmathias");
			GOOGLE_ACCOUNTS.put("julien.buret@googlewave.com", "jburet");
			GOOGLE_ACCOUNTS.put("sylvain.barbot@googlewave.com", "sbarbot");
			GOOGLE_ACCOUNTS.put("sevenlemesle@googlewave.com", "slemesle");
			GOOGLE_ACCOUNTS.put("michael.figuiere@googlewave.com", "mfiguiere");
			GOOGLE_ACCOUNTS.put("polyte.p@googlewave.com", "ppolyte");
			GOOGLE_ACCOUNTS.put("romain.maton@googlewave.com", "rmaton");
			GOOGLE_ACCOUNTS.put("romain.schlick@googlewave.com", "rschlick");
			GOOGLE_ACCOUNTS.put("nrichand@googlewave.com", "nrichand");

		} catch (JDOMException e) {
			LOG.info(e.getMessage());
		} catch (IOException e) {
			LOG.info(e.getMessage());
		}
	}

	@Override
	public void processEvents(RobotMessageBundle robotMessageBundle) {
		// Retrieves the wavelet
		Wavelet wavelet = robotMessageBundle.getWavelet();

		// If the event is "Wavelet self added"
		if (robotMessageBundle.wasSelfAdded()) {
			processWaveletSelfAddedEvent(wavelet);
		} else {
			// If the event is "Blip submitted"
			for (Event event : robotMessageBundle.getBlipSubmittedEvents()) {
				processBlipSubmittedEvent(event.getBlip());
			}
			for (Event event : robotMessageBundle
					.getParticipantsChangedEvents()) {
				LOG.info("getParticipantsChangedEvents");

				processBlipAddedParticipants(event.getAddedParticipants(),
						event.getBlip());
			}
		}
	}

	private void processBlipAddedParticipants(
			Collection<String> addedParticipants, Blip blip) {
		for (String name : addedParticipants) {
			LOG.info("Ajout de : " + name);
			if (GOOGLE_ACCOUNTS.keySet().contains(name)) {
				linkAuthor(blip.createChild(), name, 0);
			}

		}
	}

	private void linkAuthor(Blip blip, String name, int startIdx) {
		String appendText = name + " est l'auteur des articles suivants\n";
		TextView textView = blip.getDocument();
		LOG.info("Start index : " + startIdx);
		textView.insert(startIdx, appendText);
		Range range = new Range(startIdx + name.length() + 5, startIdx
				+ appendText.length());
		textView.setAnnotation(range, "link/manual", URL_PREFIX_AUTHORS
				+ GOOGLE_ACCOUNTS.get(name));
	}

	/**
	 * Processes the event: Wavelet self added.
	 * 
	 * @param wavelet
	 *            the wavelet
	 */
	private void processWaveletSelfAddedEvent(Wavelet wavelet) {
		// Displays instructions
		Blip blip = wavelet.appendBlip();
		TextView textView = blip.getDocument();
		textView.append("Je suis le robot du blog de Xebia France\n");
		textView
				.append("Je vous propose de suivre d'ajouter des liens sur notre blog aux termes techniques de votre Wave");
		Range range = new Range(21, 41);
		textView.setAnnotation(range, "link/manual", "http://blog.xebia.fr");
	}

	/**
	 * Processes the event: Blip submitted.
	 * 
	 * @param blip
	 *            the blip
	 */
	private void processBlipSubmittedEvent(Blip blip) {
		// StringBuffer response = new StringBuffer();

		// Retrieves the content of the blip
		String blipDocumentText = blip.getDocument().getText();

		LOG.info(blipDocumentText);

		for (String tag : TAGS) {
			Pattern p = Pattern.compile("\\b" + tag + "\\b",
					Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
			Matcher m = p.matcher(blipDocumentText);
			while (m.find()) {
				Range range = new Range(m.start(), m.end());
				LOG.info("Range set : " + range);
				blip.getDocument().setAnnotation(range, "link/manual",
						URL_PREFIX_TAG + tag);
			}
		}

		// If this content is a robot command
		Pattern p = Pattern.compile("\\b" + COMMAND_PREFIX_AUTHOR + "\\b",
				Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
		Matcher m = p.matcher(blipDocumentText);
		if (m.find()) {
			LOG.info("Command author found");
			String authorName = blip.getCreator();
			LOG.info("Creator of parent blip is : " + authorName);
			Range range = new Range(m.start(), m.end() + 1);
			blip.getDocument().delete(range);
			String message = "\n*** Demande des informations sur le créateur de ce blip ***\n";
			blip.getDocument().insert(m.start(), message);
			
			linkAuthor(blip, authorName, m.start() + message.length());
		}

		// Extracts the address in the command
		// String keyWord = blipDocumentText.substring(
		// COMMAND_PREFIX_AUTHOR.length()).trim();
		//
		// if (keyWord.length() > 0) {
		// log.info("Keyword : " + keyWord);
		// String url = "";
		// if (keyWord.equals("xebia")) {
		// url = "http://blog.xebia.fr/feed/?dualfeed=2";
		// } else if (keyWord.equals("cnn")) {
		// url = "http://rss.cnn.com/rss/cnn_world.rss";
		// }
		// RssParser parser;
		// try {
		// parser = RssParserFactory.createDefault();
		// Rss rss = parser.parse(new URL(url));
		//
		// // Get all XML elements in the feed
		// Collection items = rss.getChannel().getItems();
		// if (items != null && !items.isEmpty()) {
		// // Iterate over our main elements. Should have one for
		// // each
		// // article
		// for (Iterator i = items.iterator(); i.hasNext(); response
		// .append("\n")) {
		// Item item = (Item) i.next();
		// response.append("Title: " + item.getTitle() + "\n");
		// response
		// .append("   Link: " + item.getLink() + "\n");
		// }
		//
		// }
		// } catch (RssParserException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (MalformedURLException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// } else if (blipDocumentText.startsWith(COMMAND_PREFIX_ISBN)) {
		// // Extracts the address in the command
		// String keyWord = blipDocumentText.substring(
		// COMMAND_PREFIX_ISBN.length()).trim();
		// log.info("Keyword isbn : " + keyWord);
		//
		// if (keyWord.length() > 0) {
		// TextView textView = blip.getDocument();
		// replaceIsbns(textView, keyWord);
		// }
		// } else if (blipDocumentText.startsWith(COMMAND_PREFIX_MY_GADGET)) {
		// // Extracts the address in the command
		// String keyWord = blipDocumentText.substring(
		// COMMAND_PREFIX_MY_GADGET.length()).trim();
		// log.info("Keyword gadget : " + keyWord);
		//
		// if (keyWord.length() > 0) {
		// TextView textView = blip.getDocument();
		// textView.replace("");
		// Gadget gadget = new Gadget(MY_GADGET_XML);
		// gadget.setField("isbn", keyWord);
		// textView.insertElement(0, gadget);
		// }
		//
		// }
		//
		// // Renders response
		// if (response.toString().length() > 0) {
		// blip.getDocument().append("\n" + response.toString());
		// }
		//
		// if (blip.getCreator().equals("erwan.alliaume@googlewave.com")) {
		// String replacement = blipDocumentText.replaceAll("r", "");
		// blip.getDocument().append("\n Ewan translato : " + replacement);
		// }

	}

	void replaceIsbns(TextView textView, String keyword) {

		// Pattern isbn = Pattern.compile("\\d{13}");
		// Matcher matcher = isbn.matcher(keyword);
		// log.info("Keyword : " + keyword);
		//
		// while (matcher.find()) {
		// log.info(" Found a match");
		// textView.replace("");
		// String isbnNum = keyword;
		// Gadget gadget = new Gadget(ISBN_GADGET_XML);
		// gadget.setField("isbn", isbnNum);
		// textView.insertElement(0, gadget);
		// }
	}

}
