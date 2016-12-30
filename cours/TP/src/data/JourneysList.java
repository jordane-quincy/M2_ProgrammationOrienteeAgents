package data;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class JourneysList
  implements Serializable
{
  Hashtable<String, ArrayList<Journey>> catalog;
  
  public JourneysList()
  {
    this.catalog = new Hashtable();
  }
  
  public void addJourney(String _start, String _stop, String _means, int _departureDate, int _duration)
  {
    ArrayList<Journey> list = (ArrayList)this.catalog.get(_start.toUpperCase());
    if (list == null)
    {
      list = new ArrayList();
      list.add(new Journey(_start.toUpperCase(), _stop.toUpperCase(), _means.toUpperCase(), _departureDate, 
        _duration));
      this.catalog.put(_start.toUpperCase(), list);
    }
    else
    {
      list.add(new Journey(_start.toUpperCase(), _stop.toUpperCase(), _means.toUpperCase(), _departureDate, 
        _duration));
    }
  }
  
  public void addJourney(String _start, String _stop, String _means, int _departureDate, int _duration, double _cost, int _co2, int _confort)
  {
    ArrayList<Journey> list = (ArrayList)this.catalog.get(_start.toUpperCase());
    if (list == null)
    {
      list = new ArrayList();
      list.add(new Journey(_start.toUpperCase(), _stop.toUpperCase(), _means.toUpperCase(), _departureDate, 
        _duration, _cost, _co2, _confort));
      this.catalog.put(_start.toUpperCase(), list);
    }
    else
    {
      list.add(new Journey(_start.toUpperCase(), _stop.toUpperCase(), _means.toUpperCase(), _departureDate, 
        _duration, _cost, _co2, _confort));
    }
  }
  
  public void addJourney(Journey j)
  {
    addJourney(j.start, j.stop, j.means, j.departureDate, j.duration, j.cost, j.co2, j.confort);
  }
  
  public void addJourneys(JourneysList _list)
  {
    Iterator localIterator2;
    for (Iterator localIterator1 = _list.catalog.values().iterator(); localIterator1.hasNext(); localIterator2.hasNext())
    {
      ArrayList<Journey> l = (ArrayList)localIterator1.next();
      localIterator2 = l.iterator(); //continue;
      Journey j = (Journey)localIterator2.next();
      addJourney(j.start, j.stop, j.means, j.departureDate, j.duration, j.cost, j.co2, j.confort);
    }
  }
  
  ArrayList<Journey> findDirectJourneys(String start, String stop)
  {
    ArrayList<Journey> result = new ArrayList();
    ArrayList<Journey> list = (ArrayList)this.catalog.get(start.toUpperCase());
    if (list != null) {
      for (Journey j : list) {
        if ((j.start.equalsIgnoreCase(start)) && (j.stop.equalsIgnoreCase(stop))) {
          result.add(j);
        }
      }
    }
    if (result.isEmpty()) {
      result = null;
    }
    return result;
  }
  
  public boolean findIndirectJourney(String start, String stop, int date, int late, ArrayList<Journey> currentJourney, List<String> via, List<ComposedJourney> results)
  {
    via.add(start);
    ArrayList<Journey> list = (ArrayList)this.catalog.get(start.toUpperCase());
    if (list == null) {
      return false;
    }
    for (Journey j : list) {
      if ((j.start.equalsIgnoreCase(start)) && (j.departureDate >= date) && 
        (j.departureDate <= Journey.addTime(date, late))) {
        if (j.stop.equalsIgnoreCase(stop))
        {
          currentJourney.add(j);
          ComposedJourney compo = new ComposedJourney();
          compo.addJourneys((ArrayList)currentJourney.clone());
          results.add(compo);
          currentJourney.remove(currentJourney.size() - 1);
        }
        else if (!via.contains(j.stop))
        {
          currentJourney.add(j);
          findIndirectJourney(j.stop, stop, j.arrivalDate, late, currentJourney, via, results);
          via.remove(j.stop);
          currentJourney.remove(j);
        }
      }
    }
    boolean result = !results.isEmpty();
    return result;
  }
  
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    Collection<ArrayList<Journey>> lists = this.catalog.values();
    ArrayList<Journey> list = new ArrayList();
    for (ArrayList<Journey> l : lists) {
      list.addAll(l);
    }
    for (Journey j : list) {
      sb.append(j).append("\n");
    }
    sb.append("---end---");
    return "list of journeys:\n" + sb.toString();
  }
  
  public static void main(String[] args)
  {
    JourneysList journeysList = new JourneysList();
    journeysList.addJourney("Val", "Lille", "car", 1440, 30);
    journeysList.addJourney("Val", "Lille", "train", 1440, 40);
    journeysList.addJourney("Val", "Lille", "car", 1510, 30);
    journeysList.addJourney("Lille", "Dunkerque", "car", 1500, 40);
    journeysList.addJourney("Lille", "Dunkerque", "car", 1600, 40);
    journeysList.addJourney("Lille", "Dunkerque", "car", 1630, 40);
    journeysList.addJourney("Dunkerque", "Bray-Dunes", "car", 1700, 10);
    journeysList.addJourney("Dunkerque", "Bray-Dunes", "car", 1710, 20);
    System.out.println(journeysList);
    ArrayList<Journey> search = journeysList.findDirectJourneys("val", "lille");
    System.out.println(search);
    System.out.println("----");
    
    System.out.println("----");
    ArrayList<ComposedJourney> journeys = new ArrayList();
    journeysList.findIndirectJourney("val", "dunkerque", 1400, 90, new ArrayList(), 
      new ArrayList(), journeys);
    System.out.println(journeys);
  }

	public boolean removeJourney(String _start, String _stop) {
		boolean hasRemovedJourney = false;
		if (_start != null && !_start.isEmpty() && _stop != null && !_stop.isEmpty()) {
			//on recupere la liste des trajets au depart de _start
			ArrayList<Journey> list = (ArrayList) this.catalog.get(_start.toUpperCase());
			if (list != null) {
				for (Iterator<Journey> iterator = list.iterator(); iterator.hasNext(); ) {
					Journey journey = iterator.next();
					//si la destination est celle recherchee
					if(journey.getStop().equalsIgnoreCase(_stop)){
				        iterator.remove();
				        hasRemovedJourney = true;
				    }
				}
			}
		}
		return hasRemovedJourney;
	}
	
	public Hashtable<String, ArrayList<Journey>> getCatalog(){
		return this.catalog;
	}
}
