package com.exchange.model;



public class RicDirKey {

	private final String ric;
	private final Direction direction;


	public RicDirKey(String ric, Direction direction) {
		super();
		this.ric = ric;
		this.direction = direction;
	}

	public String getRic() {
		return ric;
	}

	public Direction getDirection() {
		return direction;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((ric == null) ? 0 : ric.hashCode());
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
		RicDirKey other = (RicDirKey) obj;
		if (direction != other.direction)
			return false;
		if (ric == null) {
			if (other.ric != null)
				return false;
		} else if (!ric.equals(other.ric))
			return false;
		return true;
	}
	
	

}
