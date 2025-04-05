package com.fds.flex.core.portal.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class GatewayModel {
	public String gatewayContext;
	public String endpoint;
	public List<String> resources;
}
