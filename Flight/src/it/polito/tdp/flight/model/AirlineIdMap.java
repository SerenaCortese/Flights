package it.polito.tdp.flight.model;

import java.util.HashMap;
import java.util.Map;


public class AirlineIdMap {
	
	private Map<Integer, Airline> map;

	public AirlineIdMap() {
		this.map = new HashMap<>();
	}
	
	public Airline get(Airline airline) {
		Airline old = map.get(airline.getAirlineId());
		if(old==null) {
			//nella mappa non c'è questo corso => LO AGGIUNGO
			map.put(airline.getAirlineId(), airline);
			return airline;
		}
		
		//avevo già inserito quell'oggetto
		return old;
	
	}
	
	public Airline get(int airlineId) {
		return map.get(airlineId);
	}
	
	public void put(Airline airline, int airlineId) {
		map.put(airlineId, airline);
	}
	

}
