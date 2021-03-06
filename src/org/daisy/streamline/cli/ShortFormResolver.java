package org.daisy.streamline.cli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides shorter names for factory identifiers, to be used in command line user interfaces.
 * The short forms are guaranteed to be consistent between executions as long as
 * the identifiers in the collection remains the same.  
 * @author Joel Håkansson
 */
public class ShortFormResolver {
	private final HashMap<String, String> idents;
	private final HashMap<String, String> shorts;

	/**
	 * Creates a new short form resolver with the specified
	 * list of identifiers.
	 * @param s the identifiers
	 */
	public ShortFormResolver(String ... s) {
		this(toCollection(s));
	}

	private static Collection<String> toCollection(String ... s) {
		Collection<String> ret = new ArrayList<>();
		Collections.addAll(ret, s);
		return ret;
	}

	/**
	 * Creates a new ShortFormResolver for the supplied collection of identifiers.
	 * @param obj the collection to create short forms for
	 */
	public ShortFormResolver(Collection<String> obj) {
		this.idents = new HashMap<>();
		this.shorts = new HashMap<>();
		//analyze uniqueness short forms
		HashMap<String, Integer> uniqueIndex = new HashMap<>();
		for (String f : obj) {
			String identifier = f.toLowerCase();
			for (String p : identifier.split("\\.")) {
				Integer i = uniqueIndex.get(p);
				if (i!=null) {
					uniqueIndex.put(p, i+1);
				} else {
					uniqueIndex.put(p, 1);
				}
			}
		}
		//add short forms
		for (String f : obj) {
			String identifier = f.toLowerCase();
			String[] s = identifier.split("\\.");
			Integer x = uniqueIndex.get(s[s.length-1]);
			assert x!=null;
			if (x==1) {
				idents.put(s[s.length-1], f);
				shorts.put(f, s[s.length-1]);
			} else {
				//TODO: expand on this
				// Don't do anything
				idents.put(identifier, f);
				shorts.put(f, identifier);
			}
		}
	}

	/**
	 * Gets all short forms.
	 * @return returns a list of short forms
	 */
	public List<String> getShortForms() {
		ArrayList<String> ret = new ArrayList<>(idents.keySet());
		Collections.sort(ret);
		return ret;
	}

	/**
	 * Get the short form for the specified identifier.
	 * @param id the identifier to get the short form for
	 * @return returns the short form for the identifier, or null if the identifier 
	 * does not have a short form
	 */
	public String getShortForm(String id) {
		return shorts.get(id);
	}

	/**
	 * Resolves a short form.
	 * @param shortForm the short form to resolve
	 * @return returns the full id for the supplied short form, or null if the short
	 * form does not have an identifier
	 */
	public String resolve(String shortForm) {
		return idents.get(shortForm.toLowerCase());
	}
	
	/**
	 * Expands the short form value found at the specified key in the provided map and 
	 * replaces it with the full id using {@link #resolve(String)}.
	 * @param map the map with keys
	 * @param key the key to whose value to expand
	 * @throws IllegalArgumentException if the value for the key cannot be resolved
	 */
	public void expandShortForm(Map<String, String> map, String key) {
		String value = map.get(key);
		if (value!=null) {
			String id = resolve(value);
			if (id!=null) {
				map.put(key, id);
			} else {
				throw new IllegalArgumentException("Unknown value for "+key+": '" + value + "'");
			}
		}
	}
}
