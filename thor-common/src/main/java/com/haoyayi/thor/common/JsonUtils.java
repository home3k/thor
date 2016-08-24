package com.haoyayi.thor.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static String writeValueAsString(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T>T readValue(String content, Class<T> clz) {
		try {
			return mapper.readValue(content, clz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static List<JsonNode> readValue(String value) {
		List<JsonNode> list = new ArrayList<JsonNode>();
		if (value == null || value.trim().equals("")) {
			return list;
		}
		try {
			JsonNode json = mapper.readTree(value);
			Iterator<JsonNode> its = json.elements();
			while (its.hasNext()) {
				list.add(its.next());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return list;
	}
	
	public static void main(String[] args) {
		String s = "[{\"id\":2,\"name\":\"牙疼\",\"price\":13.5,\"num\":4}]";
		List<JsonNode> list = readValue(s);
		for(JsonNode jsonNode : list) {
			String name = jsonNode.path("names").asText();
			long id = jsonNode.path("ids").longValue();
			System.out.println(jsonNode.has("id"));
			
			System.out.println(name+"d"+id);
		}
	}

}
