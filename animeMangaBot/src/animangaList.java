import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bson.Document;

/**
 * This class holds a HashMap containing the animanga 
 * and chapter associated with every userID along with 
 * all the methods associated with editing it.
 */

/**
 * @author Anthony Nguyen
 *
 */
public class animangaList {

	private HashMap<String, Integer> myList;

	public animangaList() {
		myList = new HashMap<String, Integer>();
	}

	public animangaList(Document document) {
		myList = new HashMap<String, Integer>();
		for (Entry<String, Object> entry : document.entrySet()) {
			myList.put(entry.getKey(), (Integer) entry.getValue());
		}
	}

	public boolean validTitle(String title) {
		return myList.containsKey(title);
	}

	public int getChapter(String title) {
		if (validTitle(title))
			return myList.get(title);
		return 0;
	}

	/**
	 * Adds title to list
	 * 
	 * @param title
	 */
	public void add(String title) {
		myList.put(title, 0);
	}

	/**
	 * Adds title and chapter # to list
	 * 
	 * @param title
	 * @param chapter
	 */
	public void add(String title, int chapter) {
		myList.put(title, chapter);
	}

	/**
	 * Adds +1 to given title
	 * 
	 * @param title
	 */
	public void readChapter(String title) {
		if (validTitle(title))
			myList.put(title, myList.get(title) + 1);
	}

	/**
	 * Removes title from list
	 * 
	 * @param title
	 */
	public void remove(String title) {
		myList.remove(title);
	}

	/**
	 * Edits chapter number from given title
	 * 
	 * @param title
	 * @param chapter
	 */
	public void edit(String title, int chapter) {
		if (validTitle(title))
			myList.put(title, chapter);
	}

	/**
	 * Returns list as an organized string
	 * 
	 * @return returned
	 */
	public String list() {
		String returned = "";
		for (Map.Entry<String, Integer> entry : myList.entrySet()) {
			returned += entry.getKey() + " chapter/episode: " + entry.getValue() + "\n";
		}
		return returned;
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return myList.isEmpty();
	}

	public Document toDocument() {
		Document returned = new Document();
		for (Map.Entry<String, Integer> entry : myList.entrySet()) {
			returned.append(entry.getKey(), entry.getValue());
		}
		return returned;
	}

	@Override
	public String toString() {
		return list();
	}

}
