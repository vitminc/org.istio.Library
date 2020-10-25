package org.istio.library.controller;

import io.grpc.*;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.security.Principal;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class JWTClientInterceptor implements ClientInterceptor {

    public static final Metadata.Key<String> JWT_KEY = Metadata.Key.of("jwt", ASCII_STRING_MARSHALLER);

    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public String accessToken() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (request != null) {
            Principal userPrincipal = request.getUserPrincipal();
            if (userPrincipal instanceof KeycloakAuthenticationToken) {
                Object credentials = ((KeycloakAuthenticationToken) userPrincipal).getCredentials();
                if (credentials instanceof KeycloakSecurityContext) {
                    return ((KeycloakSecurityContext) credentials).getTokenString();
                }
            }
        }
        return null;
    }

    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                String accessToken = accessToken();
                //System.out.println(accessToken);
                if (accessToken!=null) {
                    headers.put(JWT_KEY, accessToken);
                }
                super.start(responseListener, headers);
            }
        };
    }
}
