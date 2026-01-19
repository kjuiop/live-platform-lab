package org.giglab.live.application.dto.action;

import java.util.Map;

public record ActionRequest(
    String roomId, String action, Actor actor, Map<String, Object> payload) {}
