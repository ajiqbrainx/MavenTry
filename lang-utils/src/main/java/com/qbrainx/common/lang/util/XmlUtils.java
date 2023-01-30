package com.qbrainx.common.lang.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.util.ResourceUtils;

import com.qbrainx.common.exception.CustomException;

@UtilityClass
public class XmlUtils {

    public String getDynamicXmlStr(String xmlData, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            xmlData = xmlData.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return xmlData;
    }

    public String getXmlString(String fileLocation) {
        try {
            return new String(Files.readAllBytes(Paths.get(ResourceUtils.getFile(fileLocation).getAbsolutePath())));
        } catch (Exception e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, null, "Error Occurred while transforming the XML Data");
        }
    }
}
