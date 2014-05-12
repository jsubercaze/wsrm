package fr.tse.lt2c.satin.matrix.utils;

import java.util.ArrayList;

public class CustomDeque<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3001347267128813799L;

	public CustomDeque(int i) {
		super(i);
	}

	public E peek() {
		if (this.size() == 0)
			return null;
		return this.get(this.size() - 1);
	}

	public E poll() {
		if (this.size() == 0)
			return null;
		return this.remove(this.size() - 1);
	}

	public E peekSecondLast() {
		if (this.size() < 2)
			return null;
		return this.get(this.size() - 2);
	}

}
