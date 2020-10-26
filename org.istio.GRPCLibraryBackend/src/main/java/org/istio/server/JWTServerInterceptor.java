package org.istio.server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.grpc.*;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class JWTServerInterceptor implements ServerInterceptor {
    public static final Context.Key<DecodedJWT> CLIENT_ID_CONTEXT_KEY = Context.key("clientId");

    private static final Metadata.Key<String> JWT_KEY = Metadata.Key.of("jwt", ASCII_STRING_MARSHALLER);
    private static final ServerCall.Listener NOOP_LISTENER = new ServerCall.Listener() {};

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {
        // Get token from Metadata
        // Capture the JWT token and just print it out.
        String token = metadata.get(JWT_KEY);
        System.out.println("Received Token: " + token);

        DecodedJWT jwt = validateJWT(token);
        if (jwt==null) {
            // In case of deny
            // serverCall.close(Status.UNAUTHENTICATED.withDescription("JWT Token is missing from Metadata"), metadata);
            // return NOOP_LISTENER;
        } else {
            Context context = Context.current().withValue(CLIENT_ID_CONTEXT_KEY, jwt);
            return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
        }
        return serverCallHandler.startCall(serverCall, metadata);
    }

    private DecodedJWT validateJWT(String token){
        if (token!=null && !token.isEmpty()) {
            try {
                return JWT.decode(token);
            } catch (JWTDecodeException exception){
                exception.printStackTrace();
            }
        }
        return null;
    }

}
