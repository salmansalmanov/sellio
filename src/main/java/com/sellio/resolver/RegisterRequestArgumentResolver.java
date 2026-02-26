package com.sellio.resolver;

import com.sellio.exception.custom.ResourceNotFoundException;
import com.sellio.factory.concrete.RegisterRequestFactory;
import com.sellio.model.dto.request.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RegisterRequestArgumentResolver implements HandlerMethodArgumentResolver {
    private final RegisterRequestFactory registerRequestFactory;
    private final ObjectMapper objectMapper;


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return RegisterRequest.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public @Nullable Object resolveArgument(
            @NonNull MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (!(request instanceof MultipartHttpServletRequest multipartRequest)) {
            throw new IllegalArgumentException("Request is not multipart");
        }

        String role = request.getParameter("role");
        if (role == null) {
            throw new ResourceNotFoundException("Request parameter 'role' is missing");
        }

        String jsonData = multipartRequest.getParameter("data");
        if (jsonData == null) {
            throw new IllegalArgumentException("Multipart request must contain 'data' parameter");
        }

        Class<? extends RegisterRequest> targetClass = registerRequestFactory.getRequestClassByRole(role);
        RegisterRequest registerRequest = objectMapper.readValue(jsonData, targetClass);

        if (parameter.hasParameterAnnotation(Valid.class)) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, registerRequest, Objects.requireNonNull(parameter.getParameterName()));
            binder.validate();
            if (binder.getBindingResult().hasErrors()) {
                throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
            }
        }

        return registerRequest;
    }
}
