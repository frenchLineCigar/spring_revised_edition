package org.springframework.samples.petclinic.owner;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 실제 Aspect(@LogExecutioonTime 애노테이션 달린 곳에 적용)
 * - 스프링이 제공하는 애노테이션 기반의 AOP로 이 내부는 Proxy 패턴으로 동작한다
 */
@Component
@Aspect
public class LogAspect {

    //Slf4j
    Logger logger = LoggerFactory.getLogger(LogAspect.class);

    //Advice
    @Around("@annotation(LogExecutionTime)") //LogExecutionTime 애노테이션 주변에 이 코드를 적용하겠다
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object proceed = joinPoint.proceed(); //실행을 해서 결과가 있다면

        stopWatch.stop();
        logger.info(stopWatch.prettyPrint()); //시간을 재서 로거를 사용해 출력

        return proceed; //그 결과를 그대로 리턴
    }
    /**
     * @Around 를 사용한 메소드 안에서 JoinPoint 인터페이스 타입의 파라미터(joinPoint)를 사용할 수 있다/
     * joinPoint는 @LogExecutioonTime이 붙어 있는 target(Aspect를 적용할 대상) 메서드이다.
     *
     * 같은 코드인데 여러 곳에 들어가는 로직이 있을 경우 스프링 AOP를 적용하는 것을 고려해본다
     *
     * `@Around`, `@After`, `@Before`, 어떤 Exception이 발생했을 때
     * `@Around("표현식"): bean, @annotation, 어떤 메서드가 실행되는 시점
     *
     */

}
