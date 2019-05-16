package org.daisy.dotify.tasks.impl.input.xml;

final class XMLKey {
	private final String key;
	private final String formatName;
	
	private XMLKey(String key, String formatName) {
		this.key = key;
		this.formatName = formatName;
	}

	static XMLKey from(String key, String formatName) {
		return new XMLKey(key, formatName);
	}
	
	static XMLKey from(String key) {
		return new XMLKey(key, null);
	}

	String getKey() {
		return key;
	}

	String getFormatName() {
		return formatName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	// Only include "key" in hashCode and equals. This is because the format name
	// is considered meta data in this case.
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMLKey other = (XMLKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

}
