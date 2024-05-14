

/**
 * @author Anthony Baoloc Nguyen
 *
 */
public class animeManga {

	String animanga;
	int chapter;

	public animeManga(int chapter, String animanga) {
		this.animanga = animanga;
		this.chapter = chapter;
	}

	/**
	 * This method adds another integer to the chapter variable
	 */
	public void addChapter() {
		chapter++;
	}

	/**
	 * @return the animeManga
	 */
	public String getAnimeManga() {
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
	public void setAnimeManga(String animanga) {
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
		return animanga;
	}
}
