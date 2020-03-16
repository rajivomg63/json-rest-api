package com.rajiv.jsonrestapi.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class ConverterService {
	
	public static void csvFiletoJsonFile(File resourceInput, File resourceOutput) throws Exception {
       
        
     
        
 
        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
        CsvMapper csvMapper = new CsvMapper();
 
        // Read data from CSV file
        List<Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(resourceInput).readAll();
 
        ObjectMapper mapper = new ObjectMapper();
 
        // Write JSON formated data to output.json file
        mapper.writerWithDefaultPrettyPrinter().writeValue(resourceOutput, readAll);
 
        // Write JSON formated data to stdout
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll));
    }
}
