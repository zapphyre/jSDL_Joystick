package org.asmus.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@EqualsAndHashCode
public class SourceState {
    boolean connected;
    String devicePath;
}
