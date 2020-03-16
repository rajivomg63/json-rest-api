package com.rajiv.jsonrestapi.service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;

public class JsonService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonService.class);

	public static boolean isJSONValid(String jsonInString) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(jsonInString);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public void merge(JsonNode toBeMerged, JsonNode mergedInTo) {
		Iterator<Map.Entry<String, JsonNode>> incomingFieldsIterator = toBeMerged.fields();
		Iterator<Map.Entry<String, JsonNode>> mergedIterator = mergedInTo.fields();

		while (incomingFieldsIterator.hasNext()) {
			Map.Entry<String, JsonNode> incomingEntry = incomingFieldsIterator.next();

			JsonNode subNode = incomingEntry.getValue();

			if (subNode.getNodeType().equals(JsonNodeType.OBJECT)) {
				boolean isNewBlock = true;
				mergedIterator = mergedInTo.fields();
				while (mergedIterator.hasNext()) {
					Map.Entry<String, JsonNode> entry = mergedIterator.next();
					if (entry.getKey().equals(incomingEntry.getKey())) {
						merge(incomingEntry.getValue(), entry.getValue());
						isNewBlock = false;
					}
				}
				if (isNewBlock) {
					((ObjectNode) mergedInTo).replace(incomingEntry.getKey(), incomingEntry.getValue());
				}
			} else if (subNode.getNodeType().equals(JsonNodeType.ARRAY)) {
				boolean newEntry = true;
				mergedIterator = mergedInTo.fields();
				while (mergedIterator.hasNext()) {
					Map.Entry<String, JsonNode> entry = mergedIterator.next();
					if (entry.getKey().equals(incomingEntry.getKey())) {
						updateArray(incomingEntry.getValue(), entry);
						newEntry = false;
					}
				}
				if (newEntry) {
					((ObjectNode) mergedInTo).replace(incomingEntry.getKey(), incomingEntry.getValue());
				}
			}
			ValueNode valueNode = null;
			JsonNode incomingValueNode = incomingEntry.getValue();
			switch (subNode.getNodeType()) {
			case STRING:
				valueNode = new TextNode(incomingValueNode.textValue());
				break;
			case NUMBER:
				valueNode = new IntNode(incomingValueNode.intValue());
				break;
			case BOOLEAN:
				valueNode = BooleanNode.valueOf(incomingValueNode.booleanValue());
			}
			if (valueNode != null) {
				updateObject(mergedInTo, valueNode, incomingEntry);
			}
		}
	}

	private void updateArray(JsonNode valueToBePlaced, Map.Entry<String, JsonNode> toBeMerged) {
		toBeMerged.setValue(valueToBePlaced);
	}

	private void updateObject(JsonNode mergeInTo, ValueNode valueToBePlaced, Map.Entry<String, JsonNode> toBeMerged) {
		boolean newEntry = true;
		Iterator<Map.Entry<String, JsonNode>> mergedIterator = mergeInTo.fields();
		while (mergedIterator.hasNext()) {
			Map.Entry<String, JsonNode> entry = mergedIterator.next();
			if (entry.getKey().equals(toBeMerged.getKey())) {
				newEntry = false;
				entry.setValue(valueToBePlaced);
			}
		}
		if (newEntry) {
			((ObjectNode) mergeInTo).replace(toBeMerged.getKey(), toBeMerged.getValue());
		}
	}

	public static Map<String, String> getParameterFromString(String parameter, String token) {

		Map<String, String> param = new HashMap<>();
		String inParam = parameter;// .replaceAll("{", "").replaceAll("}","").trim();

		List<String> tokens = Collections.list(new StringTokenizer(inParam, token)).stream()
				.map(intoken -> (String) intoken).collect(Collectors.toList());
		String[] tokenArray = tokens.stream().toArray(String[]::new);

		String[] oddIndexedNames = IntStream.range(0, tokenArray.length).filter(i -> i % 2 != 0)
				.mapToObj(i -> tokenArray[i]).collect(Collectors.toList()).stream().toArray(String[]::new);

		String[] evenIndexedNames = IntStream.range(0, tokenArray.length).filter(i -> i % 2 == 0)
				.mapToObj(i -> tokenArray[i]).collect(Collectors.toList()).stream().toArray(String[]::new);

		for (int i = 0; i < oddIndexedNames.length; i++) {
			param.put(evenIndexedNames[i], oddIndexedNames[i]);
		}

		return param;
	}

	public static boolean isValuePresent(String key, String value, JsonNode currentNode) {

		boolean isPresent = false;

		if (key.contains("=")) { // This means - operand keyword present
			Map<String, String> operationMap = JsonService.getParameterFromString(key, "=");
			//LOGGER.info("keyValue json.... --- " + operationMap);

			if (operationMap.containsKey("$lte")) {
				
				double nodeValue = currentNode.asDouble();
				int filterValue = Integer.parseInt(operationMap.get("$lte"));
				
				//LOGGER.info("keyValue $lte......... --- " + nodeValue +" ...." + filterValue);

				if ( nodeValue <=filterValue) {
					
					//LOGGER.info("keyValue $lte......... --- " + nodeValue +" . YESSSSSSSSSSSSSSSSSSSSSSSS..." + filterValue);
					return true;
				}

			}
			if (operationMap.containsKey("$lt")) {
				
				double nodeValue = currentNode.asDouble();
				int filterValue = Integer.parseInt(operationMap.get("$lt"));
				
				//LOGGER.info("keyValue $lte......... --- " + nodeValue +" ...." + filterValue);

				if ( nodeValue <filterValue) {
					
					//LOGGER.info("keyValue $lte......... --- " + nodeValue +" . YESSSSSSSSSSSSSSSSSSSSSSSS..." + filterValue);
					return true;
				}

			}
			if (operationMap.containsKey("$gte")) {
				
				double nodeValue = currentNode.asDouble();
				int filterValue = Integer.parseInt(operationMap.get("$gte"));
				
				//LOGGER.info("keyValue $lte......... --- " + nodeValue +" ...." + filterValue);

				if ( nodeValue >=filterValue) {
					
					//LOGGER.info("keyValue $lte......... --- " + nodeValue +" . YESSSSSSSSSSSSSSSSSSSSSSSS..." + filterValue);
					return true;
				}

			}
         if (operationMap.containsKey("$gt")) {
				
        	 double nodeValue = currentNode.asDouble();
				int filterValue = Integer.parseInt(operationMap.get("$gt"));
				
				//LOGGER.info("keyValue $lte......... --- " + nodeValue +" ...." + filterValue);

				if ( nodeValue >filterValue) {
					
					//LOGGER.info("keyValue $lte......... --- " + nodeValue +" . YESSSSSSSSSSSSSSSSSSSSSSSS..." + filterValue);
					return true;
				}

			}

		}
		//LOGGER.info("currentNode.asText()....filter json.... --- " + value +" --------"+currentNode);
		//LOGGER.info("currentNode.asText()....filter json.... --- " + value +" --------"+currentNode.asText());
		
		if (value.equalsIgnoreCase(currentNode.asText())) {

			return true;

		}

		return isPresent;

	}
}
