package org.giglab.live.application.dto.action;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ActionResponseTest {

  @Test
  @DisplayName("sealed permits 목록과 @JsonSubTypes 타입이 일치해야 합니다")
  void permitsAndJsonSubTypesShouldBeInSync() {
    Set<Class<?>> permittedTypes =
        Arrays.stream(ActionResponse.class.getPermittedSubclasses()).collect(Collectors.toSet());

    JsonSubTypes jsonSubTypes = ActionResponse.class.getAnnotation(JsonSubTypes.class);
    Set<Class<?>> jsonSubTypesClasses =
        Arrays.stream(jsonSubTypes.value())
            .map(JsonSubTypes.Type::value)
            .collect(Collectors.toSet());

    assertThat(permittedTypes)
        .as("sealed permits 목록과 @JsonSubTypes 고유 타입 집합이 일치해야 합니다")
        .containsExactlyInAnyOrderElementsOf(jsonSubTypesClasses);
  }
}
