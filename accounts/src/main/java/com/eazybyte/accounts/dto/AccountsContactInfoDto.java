package com.eazybyte.accounts.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * YML에 있는 값을 가져와서 맵핑
 * @param message
 * @param contactDetails
 * @param onCallSupport
 */
@ConfigurationProperties(prefix = "accounts")
public record AccountsContactInfoDto(String message, Map<String, String> contactDetails, List<String> onCallSupport) {

}
