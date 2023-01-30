package com.qbrainx.common.lang.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qbrainx.common.exception.CustomException;
import com.qbrainx.common.exception.RequestValidationException;
import com.qbrainx.common.exception.ServiceExecutionException;
import com.qbrainx.common.message.MessageCode;
import com.qbrainx.common.message.MessageConstants;

import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

@Log4j2
@UtilityClass
public final class ObjectConverter {

  private static final ObjectMapper objectMapper;

  private static final ModelMapper modelMapper;
  
  private static final  XmlMapper xmlMapper;

  static {
    modelMapper = new ModelMapper();
    objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    objectMapper.registerModule(new JavaTimeModule());
    xmlMapper = new XmlMapper();
  }

  public static String objToJson(Object obj) throws JsonProcessingException {
    return objectMapper.writeValueAsString(obj);
  }

  public static Map<String, Object> jsonToMap(String jsonString) {

    try {
      return objectMapper.readValue(jsonString,
          new TypeReference<Map<String, Object>>() {
          });
    } catch (JsonProcessingException exception) {
      throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, MessageConstants.JSON_PRCS_ERROR, "Cannot convert input JsonString to Map");
    }
  }

  public static <T> T jsonToObject(final String json, final Class<T> t) {
    try {
      return objectMapper.readValue(json, t);
    } catch (final JsonProcessingException jsonEx) {
      throw new ServiceExecutionException(jsonEx);
    }
  }

  public static <T> T mapToObject(Map<String, Object> map, final Class<T> t) {
    return objectMapper.convertValue(map, t);
  }

  public static <T> T modelmapToDestination(Object src, Class<T> destination) {
    try {
      return modelMapper.map(src, destination);
    } catch (Exception ex) {
      throw new ServiceExecutionException(ex);
    }
  }

  public static <T> T convertToDestination(Object source, Class<T> destination) {
    try {
      return objectMapper.convertValue(source, destination);
    } catch (Exception ex) {
      throw new ServiceExecutionException(ex);
    }
  }

  /**
   * convert Map To String
   *
   * @return
   */
  public static String convertJsonString(final Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (Exception ex) {
      throw new RequestValidationException(MessageCode.error(null,"Exception occurred due to : " + ex));
    }
  }

  /**
   * Method to convert Xml to json Object
   * @param xmlString String DataType
   * @return JsonObject
   */
  public static JSONObject xmlToJson(final String xmlString) {
    try {
      return XML.toJSONObject(xmlString);
    } catch (final JSONException e) {
      log.error(e.getMessage());
      return null;
    }
  }

  /**
   * convertStringToMap
   *
   * @return Map of String as Key and Object as Value
   */
  public static Map<String, Object> convertStringToMap(final String string) {
    try {
      return objectMapper.readValue(string, Map.class);
    } catch (Exception ex) {
      throw new RequestValidationException(MessageCode.error(null, "Exception occurred due to : " + ex));
    }
  }

  /**
   * Convert HashMap to Class object
   *
   * @param response HashMap
   * @param t        Class
   * @param <T>      Class
   * @return Class's Object
   */
  public static <T> T convertMapToEntity(Map<String, Object> response, final Class<T> t) {
    return objectMapper.convertValue(response, t);
  }

  public static <T> List<T> convertJsonToArrayList(String response, final TypeReference<List<T>> t)
          throws JsonProcessingException {
      try {
          return objectMapper.readValue(response, t);
      } catch (final JSONException jsonException) {
          throw new ServiceExecutionException(jsonException);
      }
  }
  
  public static <T> Map<String, Object> convertObjectToMap(T t) {
      if (t == null) {
          throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, null, "Object is null");
      }
      try {
          return objectMapper.convertValue(t, new TypeReference<Map<String, Object>>() {
          });
      } catch (Exception e) {
          throw new RequestValidationException(MessageCode.error(MessageConstants.CONVERT_ERROR, "Exception occurred while converting convertObjectToMap: " + e));
      }
  }
  
  /**
   * Methods which converts from Object to XML
   *
   * @param object : Object class
   * @return : String content of Xml
   */
  public static String convertObjectToXml(final Object object) {
      try {
          return xmlMapper.writeValueAsString(object);
      } catch (final Exception e) {
          throw new RequestValidationException(MessageCode.error(MessageConstants.CONVERT_ERROR, "Exception occurred while converting ObjectToXml: " + e));
      }
  }
  
  /**
   * Methods which converts from XMLString to ClassObject
   *
   * @param xmlString : string content of XML
   * @return : created the Class Object
   */
  public static <T> T convertXmlToEntity(final String xmlString, final Class<T> t) {
      try {
          return xmlMapper.readValue(xmlString, t);
      } catch (final Exception e) {
          throw new RequestValidationException(MessageCode.error(MessageConstants.CONVERT_ERROR, "Exception occurred while converting XmlToEntity: " + e));
      }
  }
  
}
