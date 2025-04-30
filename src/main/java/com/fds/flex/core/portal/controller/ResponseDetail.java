package com.fds.flex.core.portal.controller;

import java.time.Instant;

public record ResponseDetail(
        int status,
        String message,
        String result,
        Instant timestamp) {
}
