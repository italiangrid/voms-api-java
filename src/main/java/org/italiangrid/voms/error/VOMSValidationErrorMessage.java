package org.italiangrid.voms.error;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A VOMS validation error message class (freely inspired by the CANL validation error message class).
 * 
 * @author cecco
 *
 */
public class VOMSValidationErrorMessage {
	
	static final String ERROR_BUNDLE = VOMSValidationErrorMessage.class.getPackage().getName()+"."+"errors";
	
	private VOMSValidationErrorCode errorCode;
	private String message;
	private Object[] parameters;
	
	public static VOMSValidationErrorMessage newErrorMessage(VOMSValidationErrorCode errorCode){
		return new VOMSValidationErrorMessage(errorCode);
	}
	
	public static VOMSValidationErrorMessage newErrorMessage(VOMSValidationErrorCode errorCode, Object...params){
		return new VOMSValidationErrorMessage(errorCode, params);
	}
	
	private VOMSValidationErrorMessage(VOMSValidationErrorCode errorCode) {
		this(errorCode,(Object[])null);
		
	}
	
	private VOMSValidationErrorMessage(VOMSValidationErrorCode errorCode, Object... params) {
		this.errorCode = errorCode;
		this.parameters = params;
		
		ResourceBundle bundle = ResourceBundle.getBundle(ERROR_BUNDLE);
		String template = null;
		try{
			
			 template = bundle.getString(errorCode.name());
		
		}catch(MissingResourceException e){
			template = "Other error";
		}
		
		message = MessageFormat.format(template, parameters);
		
	}
	
	public VOMSValidationErrorCode getErrorCode() {
		return errorCode;
	}
	public String getMessage() {
		return message;
	}
	public Object[] getParameters() {
		return parameters;
	}
	
	@Override
	public String toString() {
		return String.format("[%s]:%s",errorCode.name(), message);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((errorCode == null) ? 0 : errorCode.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VOMSValidationErrorMessage other = (VOMSValidationErrorMessage) obj;
		if (errorCode != other.errorCode)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}
}
