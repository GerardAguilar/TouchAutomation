package TouchAutomation;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONWrapper {	
	private static String filename = "MouseEvents.json";
	public static void writeMouseEventsToJSON(ArrayList<Coordinate> array) throws Exception {  
//		JSONObject sampleObject = new JSONObject();	
		JSONArray touchEvents = new JSONArray();
		for(int i=0; i<array.size(); i=i+2) {	
			JSONObject newEvent = new JSONObject();
			JSONArray coordinates = new JSONArray();
			coordinates.add(array.get(i).getX()+"");
			coordinates.add(array.get(i).getY()+"");
			newEvent.put("touchId", i);
			newEvent.put("coordinates", coordinates);
			touchEvents.add(newEvent);	
		}   
//		sampleObject.put("touchEvents", touchEvents);
		
		Files.write(Paths.get(filename), touchEvents.toJSONString().getBytes());
	}
//	public static Object readJsonSimpleDemo(String filename) throws Exception {  
//	    FileReader reader = new FileReader(filename);
//	    JSONParser jsonParser = new JSONParser();
//	    return jsonParser.parse(reader);
//	}

}
