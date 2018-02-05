package com.exchange.model;

/**
 * Used as a key to retrieve executed Orders by RIC and quantity
 * 
 * @author Elliot G
 */
public class RicUserKey {

	private final String ric;
	private final String user;

	public RicUserKey(String ric, String user) {
		super();
		this.ric = ric;
		this.user = user;
	}
	public String getRic() {
		return ric;
	}
	public String getUser() {
		return user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ric == null) ? 0 : ric.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RicUserKey other = (RicUserKey) obj;
		if (ric == null) {
			if (other.ric != null)
				return false;
		} else if (!ric.equals(other.ric))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "RicUserKey [ric=" + ric + ", user=" + user + "]";
	}
}
