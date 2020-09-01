package org.example.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
public class LogAspect {
    /* ********************************* POINTCUTs ******************************************** */
    @Pointcut("execution(* org.example.core..*(..)) && ! execution(* *.toBuilder(..))")
    public void pointcutExecutionFramework() {
        // it is a pointcut
    }

    @Pointcut("execution(* *.toString(..)) || execution(* *.lambda$*(..))   || execution(* *.hashCode(..)) ")
    public void noNeedMethods() {
        // it is a pointcut
    }

    @Pointcut("@annotation(org.example.aspects.NoAopLog)")
    public void noLog() {
        // it is a pointcut
    }

    @Pointcut("@annotation(org.example.aspects.ReplaceAop)")
    public void replaceAop() {
        // it is a pointcut
    }


    /* ********************************* ADVICE ******************************************** */
    @Before("pointcutExecutionFramework() && ! noLog()")
    public void beforeLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        Object[] arguments = joinPoint.getArgs();
        System.out.println("[>>] " + methodName + "(" + Arrays.toString(arguments) + ")");

    }

    @Around(value = "pointcutExecutionFramework()  && noLog() && !noNeedMethods() ")
    public Object aroundNoLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }

    @Around(value = "replaceAop()")
    public Object aroundReplace(ProceedingJoinPoint proceedingJoinPoint) {
        System.out.println("We start working instead of " + proceedingJoinPoint.getSignature().getName());
        System.out.println("We have finished working instead of " + proceedingJoinPoint.getSignature().getName());
        return null;
    }

    @Around(value = "replaceAop()")
    public Object aroundReplace2(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("We start working instead of " + proceedingJoinPoint.getSignature().getName());
        Object result = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        System.out.println("We have finished working instead of " + proceedingJoinPoint.getSignature().getName());
        return result;
    }

    @AfterReturning(value = "pointcutExecutionFramework()   && ! noLog() && !noNeedMethods()"
            , returning = "result")
    public void afterReturningLog(JoinPoint joinPoint, Object result) {
        Signature signature = joinPoint.getSignature();
        Method method = ((MethodSignature) signature).getMethod();
        if (!method.getGenericReturnType().toString().equals("void")) {
            String methodReturnedResultAsString = result.toString();
            System.out.println("[o<] [" + methodReturnedResultAsString + "] <== " + signature.getDeclaringType().getSimpleName()
                    + "::" + signature.getName() + "()");
        } else {
            System.out.println("[<<] "
                    + signature.getDeclaringType().getSimpleName()
                    + "::"
                    + signature.getName()
                    + "()");

        }
    }

    @AfterThrowing(value = "pointcutExecutionFramework()", throwing = "e")
    public void afterThrowingFramework(JoinPoint joinPoint, Throwable e) {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName()
                + "." + signature.getName();
        String signatureString = signature.toString();
        Object[] arguments = joinPoint.getArgs();
        System.out.println("Exception in method: ["
                + methodName + "] with arguments "
                + "("
                + (Arrays.toString(arguments))
                + ")\n"
                + " Signature [ " + signatureString + " ]"
                + "\n Exception [ "
                + e.getMessage() + " ]");
    }
}
