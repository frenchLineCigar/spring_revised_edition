package org.springframework.samples.petclinic.owner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 자바 기본 애노테이션
 * @Target 이 애노테이션을 어디에 적용할 수 있는지
 * @Retention 이 애노테이션 정보를 언제까지 유지할 것인가
 * @LogExecutionTime 간단한 마커용 애노테이션(어디에 적용할지 표시 해두는 용도)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
}
