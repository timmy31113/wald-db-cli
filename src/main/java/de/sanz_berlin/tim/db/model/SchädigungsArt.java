package de.sanz_berlin.tim.db.model;

/**
 * The type of damage done to a tree.
 * 
 * @author tim
 *
 */
public enum SchädigungsArt {
	/**
	 * Insects
	 */
	INSEKT("insekt"),
	/**
	 * Wild animals
	 */
	WILD("wild"),
	/**
	 * Fungus
	 */
	PILZ("pilz"),
	/**
	 * Storm
	 */
	STURM("sturm"),
	/**
	 * Drought
	 */
	DÜRRE("dürre"),
	/**
	 * Fire
	 */
	BRAND("brand");

	private final String value;

	private SchädigungsArt(String value) {
		this.value = value;
	}

	/**
	 * Get the value of this damage type for the database.
	 * 
	 * @return value
	 */
	public String getValue() {
		return value;
	}
}
