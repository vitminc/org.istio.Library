package org.istio.server;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.istio.library.generated.Book;
import org.istio.library.generated.LibraryGrpc;
import org.istio.library.generated.SearchReply;
import org.istio.library.generated.SearchRequest;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

public class LibraryDataServer {
    private static final Logger logger = Logger.getLogger(LibraryDataServer.class.getName());
    private final ArrayList<Book> books = new ArrayList<>();
    private Server server;

    public LibraryDataServer() {
        books.add(Book.newBuilder().setAuthor("J.K. Rowling")
                .setName("Harry Potter and the Deathly Hallows").setIsbn("B00FQ2LNR6").build());
        books.add(Book.newBuilder().setAuthor("J.R.R. Tolkien")
                .setName("The Lord of the Rings").setIsbn("B002RI9176").build());
        books.add(Book.newBuilder().setAuthor("Leo Tolstoy")
                .setName("War and Peace").setIsbn("978-1400079988").build());
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        final LibraryDataServer server = new LibraryDataServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new LibraryImpl())
                .intercept(new JWTServerInterceptor())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                LibraryDataServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {

        if (server != null) {
            server.awaitTermination();
        }
    }

    class LibraryImpl extends LibraryGrpc.LibraryImplBase {
        @Override
        public void findBooks(SearchRequest request, StreamObserver<SearchReply> responseObserver) {
            String term = request.getTerm().toLowerCase();
            DecodedJWT jwt = JWTServerInterceptor.CLIENT_ID_CONTEXT_KEY.get();
            ArrayList<Book> books = new ArrayList<>(LibraryDataServer.this.books);
            if (jwt!=null) {
                books.add(Book.newBuilder()
                        .setAuthor(jwt.getClaim("preferred_username").asString())
                        .setName("Hand written book")
                        .setIsbn("N/A")
                        .build());
            }
            SearchReply.Builder builder = SearchReply.newBuilder();
            if (term.isEmpty() || request.getType() == SearchRequest.SearchType.UNRECOGNIZED) {
                builder.addAllBooks(books);
            } else {
                for (Book book : books) {
                    switch (request.getType()) {
                        case ALL:
                            if (book.getAuthor().toLowerCase().contains(term) ||
                                    book.getName().toLowerCase().contains(term) ||
                                    book.getIsbn().toLowerCase().contains(term)
                            ) {
                                builder.addBooks(book);
                            }
                            break;
                        case ISBN:
                            if (book.getIsbn().toLowerCase().contains(term)) {
                                builder.addBooks(book);
                            }
                            break;
                        case NAME:
                            if (book.getName().toLowerCase().contains(term)) {
                                builder.addBooks(book);
                            }
                            break;
                        case AUTHOR:
                            if (book.getAuthor().toLowerCase().contains(term)) {
                                builder.addBooks(book);
                            }
                            break;
                        case UNRECOGNIZED:
                            break;
                    }
                }
            }

            SearchReply reply = builder.build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }

}
