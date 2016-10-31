package com.softranger.bayshopmfr.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.softranger.bayshopmfr.model.address.Country;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Eduard Albu on 9/30/16, 09, 2016
 * for project bayshop-mf
 * email eduard.albu@gmail.com
 */

public class CountriesDeserializer extends JsonDeserializer<ArrayList<Country>> {

    @Override
    public ArrayList<Country> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
//        int id = node.get
        return null;
    }
}
