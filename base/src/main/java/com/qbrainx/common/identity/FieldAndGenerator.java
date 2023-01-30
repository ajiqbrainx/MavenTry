package com.qbrainx.common.identity;

import java.lang.reflect.Field;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldAndGenerator {

  private final Field field;
  private final CustomIdGenerator tableGenerator;
}