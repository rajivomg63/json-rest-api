package com.rajiv.jsonrestapi.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rajiv.jsonrestapi.model.FieldData;
import com.rajiv.jsonrestapi.service.BaseService;
import com.rajiv.jsonrestapi.service.ConverterService;
import com.rajiv.jsonrestapi.service.JsonService;

@RestController
public class TestController {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(TestController.class);
	
	@Autowired
	BaseService bs;
	
	@GetMapping("/testMe")
	public Object TestMe(@RequestParam Map<String, String> searchParams) {
		LOGGER.info("test me enter....");
		LOGGER.info("searchParams...." + searchParams);


		// return "{\"id\":1,\"name\":\"Rajiv Bhalani\",\"role\":\"ITS Contrator\"}";
		// Resource resource = new ClassPathResource("/json/testjson.json");
		Resource resource = new ClassPathResource("/json/delaware_RIBS_data.json");

		try {
			ObjectMapper mapper = new ObjectMapper();
			LOGGER.debug("test me before return....");
			JsonNode rootNode = mapper.readTree(resource.getInputStream());
			
				
			//BaseService bs = new BaseService();
			LOGGER.info("get date from database...." +bs.findDateFromDual());
			LOGGER.debug("get date from database...." +bs.findDateFromDual());
			
			System.out.println("get date from database...." +bs.findDateFromDual());

			// return rootNode;
			return rootNode;
		} catch (Exception e) {

			throw new ResponseStatusException(
			           HttpStatus.NOT_FOUND, "Error occurred while making request...", e);
		}
		
	}
	
	
	@GetMapping("/api/fieldData/{year}")
	public Object fieldData(@PathVariable("year") String year,@RequestParam Map<String, String> searchParams) {
		LOGGER.info("test me enter....");
		LOGGER.info("searchParams...." + searchParams);

		for (Map.Entry<String, String> entry : searchParams.entrySet())
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

		// return "{\"id\":1,\"name\":\"Rajiv Bhalani\",\"role\":\"ITS Contrator\"}";
		// Resource resource = new ClassPathResource("/json/testjson.json");
		Resource resource = new ClassPathResource("/json/delaware_RIBS_data.json");

		try {
			ObjectMapper mapper = new ObjectMapper();
			LOGGER.info("test me before return....");
			JsonNode rootNode = mapper.readTree(resource.getInputStream());
			JsonNode resultNode = null;
			ArrayNode outerArray = mapper.createArrayNode();
			List<JsonNode> lJ = new ArrayList<>();
			if (rootNode.has("features")) {
				resultNode = rootNode.get("features");
				// resultNode.get("x")

				if (resultNode.isArray()) {
					JsonNode n = resultNode.get(0);

					Iterator<JsonNode> iter = resultNode.iterator();

					if (!searchParams.entrySet().isEmpty()) {
						while (iter.hasNext()) {
							JsonNode currentNode = iter.next();
							if (currentNode.has("attributes")) {
								LOGGER.info("test me iterator");
								JsonNode siteNode = currentNode.get("attributes").findValue("SITE_ID");
								String siteId = siteNode.asText().trim();
								LOGGER.info("test me for site id --- " + siteId);
								
								//List<JsonNode> labList = getLabDataFromSiteId(siteId);
								//LOGGER.info("test me for labList --- " + labList);
								
								
								
								currentNode.get("attributes").fields().forEachRemaining(e -> {
									// LOGGER.info(e.getKey()+"---"+ e.getValue());
									
									for (Map.Entry<String, String> entry : searchParams.entrySet()) {
										//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

										if (e.getValue().isValueNode() && entry.getKey().equalsIgnoreCase(e.getKey())
												&& entry.getValue().equalsIgnoreCase(e.getValue().asText())) {
											lJ.add(currentNode.get("attributes"));
											/*LOGGER.info("test me for site id --- " + siteId);
											ArrayNode an = getLabDataFromSiteId(siteId);
											LOGGER.info("test me for getLabDataFromSiteId site id --- " + an);
											JsonNode filterNode = currentNode.get("attributes");
											//JsonNode newNode = filterNode.deepCopy();
											
											ObjectNode root = JsonNodeFactory.instance.objectNode();
											  root.put("fieldData", filterNode);
											  root.put("labData", an);
											 LOGGER.info("test me for getLabDataFromSiteId filterNode --- " + filterNode);
											 lJ.add(root);*/
											//outerArray.add();
											
										}
									}

								});
							}

						}

					} else {
						return resultNode;
					}

				}

			}

			// return rootNode;
			return lJ;
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOGGER.info("test me before return null....");
		return null;
	}
	
	
	@GetMapping("/api/v2/fieldData/{year}")
	public Object fieldDatav2(@PathVariable("year") String year,@RequestParam(value = "field" , required = false) String field) {
		LOGGER.info("test me enter....");
		LOGGER.info("field...." + field);

		

		// return "{\"id\":1,\"name\":\"Rajiv Bhalani\",\"role\":\"ITS Contrator\"}";
		// Resource resource = new ClassPathResource("/json/testjson.json");
		Resource resource = new ClassPathResource("/json/delaware_RIBS_data.json");

		try {
			ObjectMapper mapper = new ObjectMapper();
			LOGGER.info("test me before return....");
			
			FieldData fieldFilterLocal = null;
			FieldData labFilterLocal =null;
			
			if(field!=null) {
				fieldFilterLocal= mapper.readValue(field, FieldData.class);
				
				LOGGER.info("fieldFilter...." + fieldFilterLocal);
			}
			final FieldData fieldFilter = fieldFilterLocal;
			
			if(field!=null) {
				for (Map.Entry<String, Object> entry : fieldFilter.getDetail().entrySet()) {
					System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

				}
			}
			
			JsonNode rootNode = mapper.readTree(resource.getInputStream());
			JsonNode resultNode = null;
			ArrayNode searchArray = mapper.createArrayNode();
			ArrayNode outerArray = mapper.createArrayNode();
			List<JsonNode> lJ = new ArrayList<>();
			if (rootNode.has("features")) {
				resultNode = rootNode.get("features");
				// resultNode.get("x")

				if (resultNode.isArray()) {
					JsonNode n = resultNode.get(0);

					Iterator<JsonNode> iter = resultNode.iterator();

					
						while (iter.hasNext()) {
							JsonNode currentNode = iter.next();
							if (currentNode.has("attributes")) {
								LOGGER.info("test me iterator");
								JsonNode siteNode = currentNode.get("attributes").findValue("SITE_ID");
								String siteId = siteNode.asText().trim();
								
								if(field!=null) {
								int fieldFiltersize = fieldFilter.getDetail().entrySet().size();
								
								 for (Map.Entry<String, Object> entry : fieldFilter.getDetail().entrySet()) {
										System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
										String key = entry.getKey();
										String keyValue = entry.getValue().toString().replaceAll("\\{", "").replaceAll("\\}", "");
										
										
										
										//String nodeKeyValue = currentNode.get("attributes").get(key);
										String filterValue = entry.getValue().toString();
										
										
										if(JsonService.isValuePresent(keyValue, filterValue, currentNode.get("attributes").get(key))){
											fieldFiltersize--;
										}
										
									}
								 
								 
								 if(fieldFiltersize==0) {
									 searchArray.add(currentNode.get("attributes")); 
								 }
								 
								}else {
									
									outerArray.add(currentNode.get("attributes")); 
								}
								 
								 
							}

						}

						

				}

			}
          
			if(field!=null) {
				return searchArray;
			}else {
				return outerArray;
			}
			// return rootNode;
			
		} catch (IOException e) {
			e.printStackTrace();
			

			throw new ResponseStatusException(
			           HttpStatus.NOT_FOUND, "Error occurred while making request...", e);
		}
	
	}
	
	
	@GetMapping("/api/v2/labData/{year}")
	public Object fieldLab2(@PathVariable("year") String year,@RequestParam(value = "lab" , required = false) String field) {
		LOGGER.info("test me enter....");
		LOGGER.info("field...." + field);

		

		// return "{\"id\":1,\"name\":\"Rajiv Bhalani\",\"role\":\"ITS Contrator\"}";
		// Resource resource = new ClassPathResource("/json/testjson.json");
		Resource resource = new ClassPathResource("/json/labdata_2019.json");

		try {
			ObjectMapper mapper = new ObjectMapper();
			LOGGER.info("test me before return....");
			
			FieldData fieldFilterLocal = null;
			FieldData labFilterLocal =null;
			
			if(field!=null) {
				fieldFilterLocal= mapper.readValue(field, FieldData.class);
				
				LOGGER.info("fieldFilter...." + fieldFilterLocal);
			}
			final FieldData fieldFilter = fieldFilterLocal;
			
			if(field!=null) {
				for (Map.Entry<String, Object> entry : fieldFilter.getDetail().entrySet()) {
					System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

				}
			}
			
			JsonNode rootNode = mapper.readTree(resource.getInputStream());
			JsonNode resultNode = rootNode;
			LOGGER.info("fieldFilter...." + rootNode);
			ArrayNode searchArray = mapper.createArrayNode();
			ArrayNode outerArray = mapper.createArrayNode();
			List<JsonNode> lJ = new ArrayList<>();
			//if (rootNode.has("features")) {
				//resultNode = rootNode.get("features");
				// resultNode.get("x")

				if (resultNode.isArray()) {
					

					Iterator<JsonNode> iter = resultNode.iterator();

					
						while (iter.hasNext()) {
							JsonNode currentNode = iter.next();
							//if (currentNode.has("attributes")) {
								LOGGER.info("test me iterator");
								JsonNode siteNode = currentNode.findValue("SiteID");
								String siteId = siteNode.asText().trim();
								
								
								if(field!=null) {
								int fieldFiltersize = fieldFilter.getDetail().entrySet().size();
								
								 for (Map.Entry<String, Object> entry : fieldFilter.getDetail().entrySet()) {
										System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
										String key = entry.getKey();
										String keyValue = entry.getValue().toString().replaceAll("\\{", "").replaceAll("\\}", "");
										
										
										
										//String nodeKeyValue = currentNode.get("attributes").get(key);
										String filterValue = entry.getValue().toString();
										
										
										if(JsonService.isValuePresent(keyValue, filterValue, currentNode.get(key))){
											fieldFiltersize--;
										}
										
									}
								 
								 
								 if(fieldFiltersize==0) {
									 searchArray.add(currentNode); 
								 }
								 
								}else {
									
									outerArray.add(currentNode); 
								}
								 
								 
							//}

						}

						

				}

			//}
          
			if(field!=null) {
				return searchArray;
			}else {
				return outerArray;
			}
			// return rootNode;
			
		} catch (IOException e) {

			throw new ResponseStatusException(
			           HttpStatus.NOT_FOUND, "Error occurred while making request...", e);
		}
		
	}
	
	@GetMapping("/api/fieldLabData/{year}")
	public Object fieldLabData(@PathVariable("year") String year,@RequestParam Map<String, String> searchParams) {
		LOGGER.info("test me enter....");
		LOGGER.info("searchParams...." + searchParams);

		for (Map.Entry<String, String> entry : searchParams.entrySet())
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

		// return "{\"id\":1,\"name\":\"Rajiv Bhalani\",\"role\":\"ITS Contrator\"}";
		// Resource resource = new ClassPathResource("/json/testjson.json");
		Resource resource = new ClassPathResource("/json/delaware_RIBS_data.json");

		try {
			ObjectMapper mapper = new ObjectMapper();
			LOGGER.info("test me before return....");
			JsonNode rootNode = mapper.readTree(resource.getInputStream());
			JsonNode resultNode = null;
			ArrayNode outerArray = mapper.createArrayNode();
			ArrayNode searchArray = mapper.createArrayNode();
			ArrayNode resultArray = mapper.createArrayNode();
			List<JsonNode> lJ = new ArrayList<>();
			if (rootNode.has("features")) {
				resultNode = rootNode.get("features");
				// resultNode.get("x")

				if (resultNode.isArray()) {
					JsonNode n = resultNode.get(0);

					Iterator<JsonNode> iter = resultNode.iterator();

					//if (!searchParams.entrySet().isEmpty()) {
						while (iter.hasNext()) {
							JsonNode currentNode = iter.next();
							if (currentNode.has("attributes")) {
								//LOGGER.info("test me iterator");
								JsonNode siteNode = currentNode.get("attributes").findValue("SITE_ID");
								String siteId = siteNode.asText().trim();
								//LOGGER.info("test me for site id --- " + siteId);
								
								//List<JsonNode> labList = getLabDataFromSiteId(siteId);
								//LOGGER.info("test me for labList --- " + labList);
								//LOGGER.info("test me for site id --- " + siteId);
								ArrayNode an = getLabDataFromSiteId(siteId);
								//LOGGER.info("test me for getLabDataFromSiteId site id --- " + an);
								JsonNode filterNode = currentNode.get("attributes");
								//JsonNode newNode = filterNode.deepCopy();
								
								ObjectNode root = JsonNodeFactory.instance.objectNode();
								  root.put("fieldData", filterNode);
								  root.put("labData", an);
								// LOGGER.info("test me for getLabDataFromSiteId filterNode --- " + filterNode);
								 outerArray.add(root);
							}
							
						}
								 boolean added =false;
								 for (JsonNode jsonNode : outerArray) {
						            
									 
									 
									 jsonNode.get("fieldData").fields().forEachRemaining(e -> {
											// LOGGER.info(e.getKey()+"---"+ e.getValue());
											int i=0;
											for (Map.Entry<String, String> entry : searchParams.entrySet()) {
												//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

												if (i==0 && e.getValue().isValueNode() && entry.getKey().equalsIgnoreCase(e.getKey())
														&& entry.getValue().equalsIgnoreCase(e.getValue().asText())) {
													//lJ.add(currentNode.get("attributes"));
												   
													searchArray.add(jsonNode);
													i=1;
												}
											}

										});
									
									 
						          }
								 
								 // only include the lab data that is matching search parameter
								 
								/* for (JsonNode jsonNode : searchArray) {
						             
									// LOGGER.info("-------jsonNode---------"+ jsonNode);
									 
									 
									 //LOGGER.info("-------jsonNode---------"+  jsonNode.get("labData"));
									 //added=false;
									
									 
									 
									 for (JsonNode jsonLabNode : jsonNode.get("labData")) {		  
										 jsonLabNode.fields().forEachRemaining(e -> {
											 LOGGER.info("----------------------------------------------------"+e.getKey()+"---"+ e.getValue());
											
											for (Map.Entry<String, String> entry : searchParams.entrySet()) {
												//System.out.println("Key = ....................... " + entry.getKey() + ", Value = " + entry.getValue());

												if (e.getValue().isValueNode() && entry.getKey().equalsIgnoreCase(e.getKey())
														&& entry.getValue().equalsIgnoreCase(e.getValue().asText())) {
													//lJ.add(currentNode.get("attributes"));
													//System.out.println("Match................................................... Value = " + entry.getValue());

													resultArray.add(jsonNode);
													
												}
											}

										});
									   }
									
									 
						          }*/
								 
								// int i=0;
								/*  for (JsonNode jsonNode : searchArray) {
									  
									  for (JsonNode jsonLabNode : jsonNode.get("labData")) {		  
								 jsonLabNode.fields().forEachRemaining(e -> {
									 LOGGER.info("----------------------------------------------------"+e.getKey()+"---"+ e.getValue());
									
									for (Map.Entry<String, String> entry : searchParams.entrySet()) {
										//System.out.println("Key = ....................... " + entry.getKey() + ", Value = " + entry.getValue());

										if (e.getValue().isValueNode() && entry.getKey().equalsIgnoreCase(e.getKey())
												&& entry.getValue().equalsIgnoreCase(e.getValue().asText())) {
											//lJ.add(currentNode.get("attributes"));
											//System.out.println("Match................................................... Value = " + entry.getValue());

											resultArray.add(jsonNode);
											
										}
									}

								});
							   }
							 }
								*/
								/* outerArray.get("fieldData").fields().forEachRemaining(e -> {
									// LOGGER.info(e.getKey()+"---"+ e.getValue());
									
									for (Map.Entry<String, String> entry : searchParams.entrySet()) {
										//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

										if (e.getValue().isValueNode() && entry.getKey().equalsIgnoreCase(e.getKey())
												&& entry.getValue().equalsIgnoreCase(e.getValue().asText())) {
											//lJ.add(currentNode.get("attributes"));
										   
											searchArray.add(e);
											
										}
									}

								});*/
							

						
                    
						if (!searchParams.entrySet().isEmpty()) {
							if(!resultArray.isEmpty()) return resultArray;
							else return searchArray;
							
						}
					//} else {
					//	return resultNode;
					//}

				}

			}

			// return rootNode;
			return outerArray;
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOGGER.info("test me before return null....");
		return null;
	}
	
	
	@RequestMapping("/api/v2/fieldLabData/{year}")
	public Object fieldLabDataV2(@PathVariable("year") String year,@RequestParam(value = "field" , required = false) String field,@RequestParam(value = "lab" , required = false) String lab) {
		LOGGER.info("test me enter....");
		LOGGER.info("field...." + field);
		LOGGER.info("lab...." + lab);

		

		// return "{\"id\":1,\"name\":\"Rajiv Bhalani\",\"role\":\"ITS Contrator\"}";
		// Resource resource = new ClassPathResource("/json/testjson.json");
		Resource resource = new ClassPathResource("/json/delaware_RIBS_data.json");

		try {
			
			
			
			ObjectMapper mapper = new ObjectMapper();
			LOGGER.info("test me before return....");
			
			FieldData fieldFilterLocal = null;
			FieldData labFilterLocal =null;
			
			if(field!=null) {
				fieldFilterLocal= mapper.readValue(field, FieldData.class);
				
				LOGGER.info("fieldFilter...." + fieldFilterLocal);
			}
			
			if(lab!=null) {
				labFilterLocal= mapper.readValue(lab, FieldData.class);
				
				LOGGER.info("labFilter...." + labFilterLocal);
			}
			
			final FieldData fieldFilter = fieldFilterLocal;
			final FieldData labFilter=labFilterLocal;
			
			if(field!=null) {
			for (Map.Entry<String, Object> entry : fieldFilter.getDetail().entrySet()) {
				System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

			}
			}
			
			JsonNode rootNode = mapper.readTree(resource.getInputStream());
			JsonNode resultNode = null;
			ArrayNode outerArray = mapper.createArrayNode();
			ArrayNode searchArray = mapper.createArrayNode();
			ArrayNode resultArray = mapper.createArrayNode();
			List<JsonNode> lJ = new ArrayList<>();
			if (rootNode.has("features")) {
				resultNode = rootNode.get("features");
				// resultNode.get("x")

				if (resultNode.isArray()) {
					JsonNode n = resultNode.get(0);

					Iterator<JsonNode> iter = resultNode.iterator();

					//if (!searchParams.entrySet().isEmpty()) {
						while (iter.hasNext()) {
							JsonNode currentNode = iter.next();
							if (currentNode.has("attributes")) {
								//LOGGER.info("test me iterator");
								JsonNode siteNode = currentNode.get("attributes").findValue("SITE_ID");
								String siteId = siteNode.asText().trim();
								//LOGGER.info("test me for site id --- " + siteId);
								
								//List<JsonNode> labList = getLabDataFromSiteId(siteId);
								//LOGGER.info("test me for labList --- " + labList);
								//LOGGER.info("test me for site id --- " + siteId);
								ArrayNode an = getLabDataFromSiteId(siteId,labFilter);
								//LOGGER.info("test me for getLabDataFromSiteId site id --- " + an);
								JsonNode filterNode = currentNode.get("attributes");
								//JsonNode newNode = filterNode.deepCopy();
								
								ObjectNode root = JsonNodeFactory.instance.objectNode();
								  root.put("fieldData", filterNode);
								  root.put("labData", an);
								// LOGGER.info("test me for getLabDataFromSiteId filterNode --- " + filterNode);
								 outerArray.add(root);
							}
							
						}
								 boolean added =false;
						   
								 if(field!=null) {	 
								 for (JsonNode jsonNode : outerArray) {
						            
									// LOGGER.info("-------jsonNode---------"+ jsonNode);
									 
									 
									 //LOGGER.info("-------jsonNode---------"+  jsonNode.get("labData"));
									 //added=false;
									
									 
									 
									 /*jsonNode.get("fieldData").fields().forEachRemaining(e -> {
											// LOGGER.info(e.getKey()+"---"+ e.getValue());
											
											for (Map.Entry<String, Object> entry : fieldFilter.getDetail().entrySet()) {
												//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

												if (e.getValue().isValueNode() && entry.getKey().equalsIgnoreCase(e.getKey())
														&& entry.getValue().toString().equalsIgnoreCase(e.getValue().asText())) {
													//lJ.add(currentNode.get("attributes"));
												   
													searchArray.add(jsonNode);
													
												}
											}

										});*/
									 /*int fieldFiltersize = fieldFilter.getDetail().entrySet().size();
									 
									 for (Map.Entry<String, Object> entry : fieldFilter.getDetail().entrySet()) {
											//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

											if (jsonNode.get("fieldData").get(entry.getKey()).asText().equalsIgnoreCase(entry.getValue().toString())){
													
												
												//lJ.add(currentNode.get("attributes"));
												fieldFiltersize--;
												
												
											}
										}
									 if(fieldFiltersize==0) {
										 searchArray.add(jsonNode); 
									 }*/
									 
									 int fieldFiltersize = fieldFilter.getDetail().entrySet().size();
										
									 for (Map.Entry<String, Object> entry : fieldFilter.getDetail().entrySet()) {
											System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
											String key = entry.getKey();
											String keyValue = entry.getValue().toString().replaceAll("\\{", "").replaceAll("\\}", "");
											
											
											
											//String nodeKeyValue = currentNode.get("attributes").get(key);
											String filterValue = entry.getValue().toString();
											
											
											if(JsonService.isValuePresent(keyValue, filterValue, jsonNode.get("fieldData").get(key))){
												fieldFiltersize--;
											}
											
										}
									 
									 
									 if(fieldFiltersize==0) {
										 searchArray.add(jsonNode); 
									 }
									
									 
						          }
								 
								 return searchArray;
								 
								 }
								 
								
								 // only include the lab data that is matching search parameter
								 
								/* for (JsonNode jsonNode : searchArray) {
						             
									// LOGGER.info("-------jsonNode---------"+ jsonNode);
									 
									 
									 //LOGGER.info("-------jsonNode---------"+  jsonNode.get("labData"));
									 //added=false;
									
									 
									 
									 for (JsonNode jsonLabNode : jsonNode.get("labData")) {		  
										 jsonLabNode.fields().forEachRemaining(e -> {
											 LOGGER.info("----------------------------------------------------"+e.getKey()+"---"+ e.getValue());
											
											for (Map.Entry<String, String> entry : searchParams.entrySet()) {
												//System.out.println("Key = ....................... " + entry.getKey() + ", Value = " + entry.getValue());

												if (e.getValue().isValueNode() && entry.getKey().equalsIgnoreCase(e.getKey())
														&& entry.getValue().equalsIgnoreCase(e.getValue().asText())) {
													//lJ.add(currentNode.get("attributes"));
													//System.out.println("Match................................................... Value = " + entry.getValue());

													resultArray.add(jsonNode);
													
												}
											}

										});
									   }
									
									 
						          }*/
								 
								// int i=0;
								/*  for (JsonNode jsonNode : searchArray) {
									  
									  for (JsonNode jsonLabNode : jsonNode.get("labData")) {		  
								 jsonLabNode.fields().forEachRemaining(e -> {
									 LOGGER.info("----------------------------------------------------"+e.getKey()+"---"+ e.getValue());
									
									for (Map.Entry<String, String> entry : searchParams.entrySet()) {
										//System.out.println("Key = ....................... " + entry.getKey() + ", Value = " + entry.getValue());

										if (e.getValue().isValueNode() && entry.getKey().equalsIgnoreCase(e.getKey())
												&& entry.getValue().equalsIgnoreCase(e.getValue().asText())) {
											//lJ.add(currentNode.get("attributes"));
											//System.out.println("Match................................................... Value = " + entry.getValue());

											resultArray.add(jsonNode);
											
										}
									}

								});
							   }
							 }
								*/
								/* outerArray.get("fieldData").fields().forEachRemaining(e -> {
									// LOGGER.info(e.getKey()+"---"+ e.getValue());
									
									for (Map.Entry<String, String> entry : searchParams.entrySet()) {
										//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

										if (e.getValue().isValueNode() && entry.getKey().equalsIgnoreCase(e.getKey())
												&& entry.getValue().equalsIgnoreCase(e.getValue().asText())) {
											//lJ.add(currentNode.get("attributes"));
										   
											searchArray.add(e);
											
										}
									}

								});*/
							

						
                    
						
					//} else {
					//	return resultNode;
					//}

				}

			}

			// return rootNode;
			return outerArray;
		} catch (IOException e) {
			e.printStackTrace();
			
			throw new ResponseStatusException(
			           HttpStatus.NOT_FOUND, "Error occurred while making request...", e);
		}
		
	}
	
	
	
	
	@GetMapping("/api/labData/{year}")
	public Object labData(@PathVariable("year") String year,@RequestParam Map<String, String> searchParams) {
		LOGGER.info("test me enter....");
		LOGGER.info("searchParams...." + searchParams);

		for (Map.Entry<String, String> entry : searchParams.entrySet())
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

		// return "{\"id\":1,\"name\":\"Rajiv Bhalani\",\"role\":\"ITS Contrator\"}";
		// Resource resource = new ClassPathResource("/json/testjson.json");
		Resource resource = new ClassPathResource("/json/labdata_2019.json");

		try {
			ObjectMapper mapper = new ObjectMapper();
			LOGGER.info("test me before return....");
			JsonNode rootNode = mapper.readTree(resource.getInputStream());
			JsonNode resultNode = null;
			List<JsonNode> lJ = new ArrayList<>();
			

				if (rootNode.isArray()) {					

					Iterator<JsonNode> iter = rootNode.iterator();

					if (!searchParams.entrySet().isEmpty()) {
						while (iter.hasNext()) {
							JsonNode currentNode = iter.next();
							//if (currentNode.has("attributes")) {
								LOGGER.info("test me iterator");
								currentNode.fields().forEachRemaining(e -> {
									// LOGGER.info(e.getKey()+"---"+ e.getValue());

									for (Map.Entry<String, String> entry : searchParams.entrySet()) {
										System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

										if (e.getValue().isValueNode() && entry.getKey().equalsIgnoreCase(e.getKey())
												&& entry.getValue().equalsIgnoreCase(e.getValue().asText())) {
											lJ.add(currentNode);
										}
									}

								});
							//}

						}

					} else {
						return rootNode;
					}

				}

			

			// return rootNode;
			return lJ;
		} catch (IOException e) {
			e.printStackTrace();
		}
		LOGGER.info("test me before return null....");
		return null;
	}
	
	@GetMapping("/api/labData/site/{siteId}")
	private ArrayNode getLabDataFromSiteId(@PathVariable("siteId") String siteId) {
		
		Resource resource = new ClassPathResource("/json/labdata_2019.json");
		List<JsonNode> lJ = new ArrayList<>();
		
		try {
		ObjectMapper mapper = new ObjectMapper();
		//LOGGER.info("test me getLabDataFromSiteId before return....");
		
		JsonNode rootNode = mapper.readTree(resource.getInputStream());
		
		ArrayNode outerArray = mapper.createArrayNode(); //your outer array
		//ObjectNode outerObject = mapper.createObjectNode(); //the object with the "data" array
		
		
		if (rootNode.isArray()) {					

			Iterator<JsonNode> iter = rootNode.iterator();

			
				while (iter.hasNext()) {
					JsonNode currentNode = iter.next();
					//if (currentNode.has("attributes")) {
						//LOGGER.info("test me getLabDataFromSiteId iterator");
						currentNode.fields().forEachRemaining(e -> {
							// LOGGER.info(e.getKey()+"---"+ e.getValue());

							

								if (e.getValue().isValueNode() && e.getKey().equalsIgnoreCase("SiteID")
										&& e.getValue().asText().equalsIgnoreCase(siteId)) {
									//lJ.add(currentNode);
									
									//outerObject.putPOJO("data",parsedJson); 
									outerArray.add(currentNode);
								}
							

						});
					//}

				}

			

		}

		return outerArray;
		
	
	} catch (IOException e) {
		e.printStackTrace();
	}
	LOGGER.info("test me before getLabDataFromSiteId return null....");
	return null;
	}
	
	
	
private ArrayNode getLabDataFromSiteId(String siteId,FieldData labFilter) {
		
		Resource resource = new ClassPathResource("/json/labdata_2019.json");
		List<JsonNode> lJ = new ArrayList<>();
		
		try {
		ObjectMapper mapper = new ObjectMapper();
		//LOGGER.info("test me getLabDataFromSiteId before return....");
		
		JsonNode rootNode = mapper.readTree(resource.getInputStream());
		
		ArrayNode outerArray = mapper.createArrayNode(); //your outer array
		//ObjectNode outerObject = mapper.createObjectNode(); //the object with the "data" array
		
		
		if (rootNode.isArray()) {					

			Iterator<JsonNode> iter = rootNode.iterator();

			
				while (iter.hasNext()) {
					JsonNode currentNode = iter.next();
					//if (currentNode.has("attributes")) {
						//LOGGER.info("test me getLabDataFromSiteId iterator");
						/*currentNode.fields().forEachRemaining(e -> {
							// LOGGER.info(e.getKey()+"---"+ e.getValue());

							

								if (e.getValue().isValueNode() && e.getKey().equalsIgnoreCase("SiteID")
										&& e.getValue().asText().equalsIgnoreCase(siteId)) {
									//lJ.add(currentNode);
									
									//outerObject.putPOJO("data",parsedJson); 
									outerArray.add(currentNode);
								}
							

						});*/
						if(currentNode.get("SiteID").asText().equalsIgnoreCase(siteId)) {
						
					    if(labFilter !=null && labFilter.getDetail().size()>0) {
						 /*int labFiltersize = labFilter.getDetail().entrySet().size();
						 
						 for (Map.Entry<String, Object> entry : labFilter.getDetail().entrySet()) {
								//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

								if (currentNode.get(entry.getKey()).asText().equalsIgnoreCase(entry.getValue().toString())){
										
									
									//lJ.add(currentNode.get("attributes"));
									labFiltersize--;
									
									
								}
							}
						 if(labFiltersize==0) {
							 outerArray.add(currentNode); 
						 }*/
					    	
					    	int fieldFiltersize = labFilter.getDetail().entrySet().size();
							
							 for (Map.Entry<String, Object> entry : labFilter.getDetail().entrySet()) {
									//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
									String key = entry.getKey();
									String keyValue = entry.getValue().toString().replaceAll("\\{", "").replaceAll("\\}", "");
									
									
									
									//String nodeKeyValue = currentNode.get("attributes").get(key);
									String filterValue = entry.getValue().toString();
									
									
									if(JsonService.isValuePresent(keyValue, filterValue, currentNode.get(key))){
										fieldFiltersize--;
									}
									
								}
							 
							 
							 if(fieldFiltersize==0) {
								 outerArray.add(currentNode); 
							 }
							 
					    	
					    }else {
					    	outerArray.add(currentNode); 
					    }
						 
						}
					//}

				}

			

		}

		return outerArray;
		
	
	} catch (IOException e) {
		e.printStackTrace();
	}
	LOGGER.info("test me before getLabDataFromSiteId return null....");
	return null;
	}
	
	@GetMapping("/api/csvtojson")
	private Object convertCsvtoJson() {
		
		
		 Resource resourceInput = new ClassPathResource("/json/2019_screening_delaware_chem_pre-qaqc_2020-03-02.csv");
	     //Resource resourceOutput = new ClassPathResource("/json/2019_screening_delaware_chem_pre-qaqc_2020-03-02.json");
	     
	    
		
		try {
			
			 //ClassLoader classLoader = getClass().getClassLoader();
		     //File resourceOutput = new File(classLoader.getResource(".").getFile() + "/json/2019_screening_delaware_chem_pre-qaqc_2020-03-02.json");
			File resourceOutput = new File("C://temp" + "/2019_screening_delaware_chem_pre-qaqc_2020-03-02.json");
			
		     if (resourceOutput.createNewFile()) {
		         System.out.println("File is created.!");
		     } else {
		         System.out.println("File already exists.");
		     }
			ConverterService cs = new ConverterService();

				cs.csvFiletoJsonFile(resourceInput.getFile(), resourceOutput);
	
	   } catch (Exception e) {
		e.printStackTrace();
	}
	LOGGER.info("test me before getLabDataFromSiteId return null....");
	return "conversion Completed";
	}
	
	
	
	
}
