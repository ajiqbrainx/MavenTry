package com.qbrainx.common.message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageConstants {

    public static final String FORBIDDEN = "DJCOMM-FRBDN";
    public static final String TECHNICAL_PROBLEM = "DJCOMM-TCPRB";

    public static final String NON_NULL = "DJCOMM-NONNULL";
    public static final String NOT_EMPTY = "DJCOMM-NOTEMPTY";
    public static final String LENGTH = "DJCOMM-LENGTH";
    public static final String INVALIDGSTFORMAT = "DJCOMM-INVALIDGSTFORMAT";
    public static final String INVALIDEMAILFORMAT = "DJCOMM-INVALIDEMAILFORMAT";
    public static final String INVALIDPANFORMAT = "DJCOMM-INVALIDPANFORMAT";
    public static final String INVALIDMOBILENUMBERFORMAT = "DJCOMM-INVALIDMOBILENUMBERFORMAT";
    public static final String INVALIDNAMEFORMAT = "DJCOMM-INVALIDENAMEFORMAT";
    public static final String INVALIDPINCODEFORMAT = "DJCOMM-INVALIDPINCODEFORMAT";
    public static final String INVALIDSTREETFORMAT = "DJCOMM-INVALIDSTREETFORMAT";
    public static final String INVALIDCOMPANYNAMEFORMAT = "DJCOMM-INVALIDCOMPANYNAMEFORMAT";
    public static final String INVALIDENGINENUMBERFORMAT = "DJCOMM-INVALIDENGINENUMBERFORMAT";
    public static final String INVALIDREGISTRATIONNUMBERFORMAT = "DJCOMM-INVALIDREGISTRATIONNUMBERFORMAT";
    public static final String INVALIDFORMAT = "INVALIDFORMAT";

    public static final String ID_NOT_FOUND = "ID_NOT_FOUND"; // "Requested Id Not Found"
    public static final String FORBIDDEN_ACTION = "DJCOMM-FRBDN-ACTN"; // "Forbidden Action {}"
    public static final String NOT_FOUND = "NOT_FOUND"; // "Not Found: {}"
    public static final String JSON_PRCS_ERROR = "JSON_PRCS_ERROR"; // "Cannot convert input JsonString to Map"
    public static final String CONVERT_ERROR = "CONVERT_ERROR"; // "Cannot convert input JsonString to Map"

}
