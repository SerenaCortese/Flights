package it.polito.tdp.flight.model;

import java.util.HashMap;
import java.util.Map;

public class AirportIdMap {
	
	private Map<Integer, Airport> map;

	public AirportIdMap() {
		this.map = new HashMap<>();
	}
	
	public Airport get(Airport airport) {
		Airport old = map.get(airport.getAirportId());
		if(old==null) {
			//nella mappa non c'è questo corso => LO AGGIUNGO
			map.put(airport.getAirportId(), airport);
			return airport;
		}
		
		//avevo già inserito quell'oggetto
		return old;
	
	}
	
	public Airport get(int airportId) {
		return map.get(airportId);
	}
	
	public void put(Airport airport, int airportId) {
		map.put(airportId, airport);
	}

}
