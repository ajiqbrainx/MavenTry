package com.qbrainx.common.service.validation;

import com.qbrainx.common.auditing.BaseAuditingDTO;
import com.qbrainx.common.rest.MappedParentRef;
import com.qbrainx.common.validation.CustomCompany;
import com.qbrainx.common.validation.CustomEmail;
import com.qbrainx.common.validation.CustomEngineChassis;
import com.qbrainx.common.validation.CustomGst;
import com.qbrainx.common.validation.CustomLength;
import com.qbrainx.common.validation.CustomMobileNumber;
import com.qbrainx.common.validation.CustomName;
import com.qbrainx.common.validation.CustomNonNull;
import com.qbrainx.common.validation.CustomNotBlank;
import com.qbrainx.common.validation.CustomPanCard;
import com.qbrainx.common.validation.CustomPincode;
import com.qbrainx.common.validation.CustomRegNumber;
import com.qbrainx.common.validation.CustomStreet;

import java.util.List;
import javax.validation.Valid;
//import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public class EmployeeDTO extends BaseAuditingDTO {

    private static final long serialVersionUID = -5586369469964433822L;

    @CustomNonNull(fieldCode = "EMP_NAME", fieldDefaultValue = "Name")
    //@NotNull
    private String name;
    
    @CustomName(fieldCode = "EMP_NAME", fieldDefaultValue = "NameValidator")
    private String firstName;
    
    @CustomStreet(fieldCode = "STREET", fieldDefaultValue = "street")
    private String street;
    
    @CustomCompany(fieldCode = "CMPNY", fieldDefaultValue = "companyName")
    private String companyName;
    
    @CustomEngineChassis(fieldCode = "ENGN", fieldDefaultValue = "engineNumber")
    private String engineNumber;
    
    @CustomEngineChassis(fieldCode = "CHSN", fieldDefaultValue = "chassisNumber")
    private String chassisNumber;
    
    @CustomPincode(fieldCode = "PCD", fieldDefaultValue = "pincode")
    private String pincode;
    
    @CustomRegNumber(fieldCode = "REGN", fieldDefaultValue = "resitrationNumber")
    private String regNumber;

    @CustomNotBlank(fieldCode = "EMP_OTHER_NAME", fieldDefaultValue = "Other Name")
    @CustomLength(min = 3, fieldCode = "EMP_OTHER_NAME_LENGTH", fieldDefaultValue = "Other Name")
    @NotNull
    //@Min(value = 3)
    private String otherName;

    @CustomLength(max = 10, fieldCode = "EMP_OPTIONAL_NAME", fieldDefaultValue =  "Optional Name")
    //@Max(value = 10)
    private String optionalName;

    @CustomLength(min = 3, max = 5, fieldCode = "EMP_NUMBER", fieldDefaultValue =  "Emp Number")
    private Long empNumber;

    @CustomGst(fieldCode = "gst", fieldDefaultValue = "Name")
    private String gst;

    @CustomEmail(fieldCode = "email", fieldDefaultValue = "Name")
    private String email;

    @CustomMobileNumber(fieldCode = "mobile", fieldDefaultValue = "Name")
    private String mobile;

    @CustomPanCard(fieldCode = "panCard", fieldDefaultValue = "Name")
    private String panCard;

    @MappedParentRef(mappedBy = "employee")
    @Valid
    private List<AddressDto> address;

    public Long getEmpNumber() {
        return empNumber;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }
    
    public void setEmpNumber(Long empNumber) {
        this.empNumber = empNumber;
    }


    public List<AddressDto> getAddress() {
        return address;
    }

    public void setAddress(List<AddressDto> address) {
        this.address = address;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPanCard() {
        return panCard;
    }

    public void setPanCard(String panCard) {
        this.panCard = panCard;
    }

    public String getOptionalName() {
        return optionalName;
    }

    public void setOptionalName(final String optionalName) {
        this.optionalName = optionalName;
    }

    public String getName() {
        return name;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setName(final @NotNull String name) {
        this.name = name;
    }

    public void setOtherName(final @Min(value = 3) String otherName) {
        this.otherName = otherName;
    }
}
