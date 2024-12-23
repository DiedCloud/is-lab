package org.example.islab.configuration.Interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.islab.annotation.RequireRole;
import org.example.islab.entity.User;
import org.example.islab.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequireRoleInterceptor implements HandlerInterceptor {
    private final UserService userService;

    @Autowired
    public RequireRoleInterceptor(UserService userService){
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  {
        if (handler instanceof HandlerMethod handlerMethod) {
            RequireRole methodAnnotation = handlerMethod.getMethodAnnotation(RequireRole.class);
            if (methodAnnotation == null) {
                methodAnnotation = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
            }

            if (methodAnnotation != null) {
                User currentUser = userService.getCurrentUser();

                if (currentUser.getUserType().lessThan(methodAnnotation.value())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }
}
