package TouchAutomation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


//modified from https://howtodoinjava.com/json/json-simple-read-write-json-examples/
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
	
	public static void writeInteractionEvents(ArrayList<Coordinate> array) throws Exception{
		/**
		 * "interactions":[
		 * 	{
		 * 		"InteractionEvent": {
		 * 			"id" : "1"
		 * 			"type" : "mouse_leftclick" 
		 * 			"x": "100"
		 * 			"y": "200"
		 * 			"timestamp": "123456789"
		 * 		}
		 * 	},
		 * 	{
		 * 		"InteractionEvent": {
		 * 			"id" : "1"
		 * 			"type" : "mouse_leftclick" 
		 * 			"x": "100"
		 * 			"y": "200"
		 * 			"timestamp": "123456789"
		 * 		}
		 * 	}
		 * ]
		 */		
//		JSONObject interactions = new JSONObject();
		JSONArray interactionEvents = new JSONArray();
		JSONObject interactionObject;
		JSONObject interactionDetails;
		
		for(int i=0; i<array.size(); i++) {
			interactionObject = new JSONObject();
			interactionDetails = new JSONObject();
			interactionDetails.put("id", i);
			interactionDetails.put("type", "mouse_leftclick");
			interactionDetails.put("x", array.get(i).getX()+"");
			interactionDetails.put("y", array.get(i).getY()+"");
			interactionDetails.put("timestamp", "123456789");
			interactionObject.put("interaction", interactionDetails);
			interactionEvents.add(interactionObject);
		}		
		
		Files.write(Paths.get(filename), interactionEvents.toJSONString().getBytes());
	}
	
	public static void testRead() throws Exception {  
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(filename));
		
		JSONArray jsonArray = (JSONArray)obj;
		System.out.println("size: " + jsonArray.size() +" | " +jsonArray);		
		
		
		
		 //Iterate over employee array
//		jsonArray.forEach( interaction -> parseInteractionObject( (JSONObject) interaction ) );
	}
	
	//need to iterate through JSONArray
	public static ArrayList<Coordinate> getCoordinates(){
		ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(new FileReader(filename));
			JSONArray jsonArray = (JSONArray)obj;
			JSONObject jsonObject;
			int x;
			int y;
			
			for(int i=0; i<jsonArray.size(); i++) {
				jsonObject = new JSONObject();
				jsonObject = (JSONObject) jsonArray.get(i);
				x = Integer.parseInt(jsonObject.get("x").toString());
				y = Integer.parseInt(jsonObject.get("y").toString());
				coordinates.add(new Coordinate(x,y));
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return coordinates;
	}
	
//    private static void parseInteractionObject(JSONObject interaction)
//    {
//        //Get employee object within list
//        JSONObject interactionObject = (JSONObject) interaction.get("interaction");
//         
//        //Get employee first name
//        String id = interactionObject.get("id").toString();   
//        System.out.println(id);
//         
//        //Get employee last name
//        String x = interactionObject.get("x").toString(); 
//        System.out.println(x);
//         
//        //Get employee website name
//        String y = interactionObject.get("y").toString();   
//        System.out.println(y);
//    }

}
