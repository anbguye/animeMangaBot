import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the lists of animanga along with all the methods associated with editing it
 */

/**
 * @author Anthony Nguyen
 *
 */
public class animangaList {

	private List<animanga> myList;

	public animangaList() {
		myList = new ArrayList<animanga>();
	}

	/**
	 * @param title
	 * @return animanga
	 */
	private animanga getTitle(String title) {
		for (animanga animanga : myList)
			if (animanga.getAnimanga().equalsIgnoreCase(title))
				return animanga;
		return null;
	}

	/**
	 * Returns whether or not the given title is in the list
	 * 
	 * @param title
	 * @return
	 */
	public boolean hasTitle(String title) {
		if (getTitle(title) != null)
			return true;
		return false;
	}

	public int getChapter(String title) {
		if (getTitle(title) != null)
			return getTitle(title).getChapter();
		return 0;
	}

	/**
	 * Adds title to list
	 * 
	 * @param title
	 */
	public void add(String title) {
		myList.add(new animanga(title));
	}

	/**
	 * Adds title and chapter # to list
	 * 
	 * @param title
	 * @param chapter
	 */
	public void add(String title, int chapter) {
		myList.add(new animanga(title, chapter));
	}

	/**
	 * Adds +1 to given title
	 * 
	 * @param title
	 */
	public void readChapter(String title) {
		if (getTitle(title) != null)
			getTitle(title).readChapter();
	}

	/**
	 * Removes title from list
	 * 
	 * @param title
	 */
	public void remove(String title) {
		myList.removeIf(val -> val.getAnimanga().equalsIgnoreCase(title));
	}

	/**
	 * Edits chapter number from given title
	 * 
	 * @param title
	 * @param chapter
	 */
	public void edit(String title, int chapter) {
		if (getTitle(title) != null)
			getTitle(title).setChapter(chapter);
	}

	/**
	 * Returns list as an organized string
	 * 
	 * @return returned
	 */
	public String list() {
		String returned = "";
		for (animanga animanga : myList)
			returned += animanga.toString() + "\n";
		return returned;
	}

	public boolean isEmpty() {
		if (myList.isEmpty())
			return true;
		return false;
	}

	@Override
	public String toString() {
		return list();
	}

}
