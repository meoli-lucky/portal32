package com.fds.flex.core.portal.plugin.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginReqDTO {
    @JsonProperty("type")
    public String type;
    @JsonProperty("code")
    public String code;
    @JsonProperty("username")
    public String username;
    @JsonProperty("password")
    public String password;
    @JsonProperty("redirect_uri")
    public String redirect_uri;
}
