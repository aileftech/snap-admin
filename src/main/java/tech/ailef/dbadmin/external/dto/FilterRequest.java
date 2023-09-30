package tech.ailef.dbadmin.external.dto;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public interface FilterRequest {
	public MultiValueMap<String, String> computeParams();
	
	public static MultiValueMap<String, String> empty() {
		return new LinkedMultiValueMap<>();
	}
}
