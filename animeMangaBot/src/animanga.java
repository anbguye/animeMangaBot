/**
 * This class tracks individual anime & manga along with their current chapter
 */

/**
 * @author Anthony Nguyen
 *
 */
public class animanga {

	private String animanga;
	private int chapter;

	public animanga() {
		this.animanga = animanga;
		this.chapter = chapter;
	}

	public animanga(String animanga, int chapter) {
		this.animanga = animanga;
		this.chapter = chapter;
	}

	public animanga(String animanga) {
		this.animanga = animanga;
		this.chapter = 0;
	}

	/**
	 * This method adds another integer to the chapter variable
	 */
	public void readChapter() {
		chapter++;
	}

	/**
	 * @return the animeManga
	 */
	public String getAnimanga() {
		return animanga;
	}

	/**
	 * @return the chapter
	 */
	public int getChapter() {
		return chapter;
	}

	/**
	 * @param animeManga the animeManga to set
	 */
	public void setAnimanga(String animanga) {
		this.animanga = animanga;
	}

	/**
	 * @param chapter the chapter to set
	 */
	public void setChapter(int chapter) {
		this.chapter = chapter;
	}

	@Override
	public String toString() {
		return animanga + " chepisode: " + getChapter();
	}
}
