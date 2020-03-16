package com.rajiv.jsonrestapi.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BaseService {
	
	private static final Logger LOGGER=LoggerFactory.getLogger(BaseService.class);
	
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	 public String findDateFromDual() {
		// try {
		 
		 LOGGER.info("BaseService  enter....");
			/*try {
				LOGGER.info("BaseService...." + jdbcTemplate);
				LOGGER.info("BaseService...." + jdbcTemplate.getDataSource().getConnection().getMetaData().getURL().toString());
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
		 
	        List<String> result = jdbcTemplate.query(
	                "SELECT sysdate FROM dual",
	                (rs, rowNum) -> new String(rs.getString("sysdate"))
	        );

	        return result.toString();
	    }
	
}
