package com.groupe5.observerpattern;

import java.util.ArrayList;
import java.util.List;
/**
 * Pattern obersever
 * @author plel
 * @author duhayona
 */
public abstract class Observed {
	private List<Observer> observers = new ArrayList<>();
	
	public void attach(Observer obs) {
		if(!observers.contains(obs)) observers.add(obs);
	}
	
	public void detach(Observer obs) {
		if(observers.contains(obs)) observers.remove(obs);
	}
	
	public void notifyObservers() {
		for(Observer obs : observers) {
			obs.update(this);
		}
	}
	
	public void notifyObservers(Object data) {
		for(Observer obs : observers) {
			obs.update(this, data);
		}
	}
}
