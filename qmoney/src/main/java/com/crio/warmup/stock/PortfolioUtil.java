package com.crio.warmup.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class PortfolioUtil {

    public static final String ALPHAV_KEY = "6HQO6OMDG9M8258I";
    public static final String TIINGO_KEY = "0bb47f878bcc4ab3e021c1a1452f3375eeb53f26";

    private static ObjectMapper mapper;
    public static ObjectMapper getObjectMapper(){
        if(mapper == null){
            mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
        }

        return mapper;
    }
    
}