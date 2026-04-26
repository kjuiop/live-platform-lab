package org.giglab.live.commerce.api.dto.product;

import java.util.List;

public record GetSimulationMessagesResponse(
    Long productId, List<String> chatMessages, List<String> faqQuestions) {}
