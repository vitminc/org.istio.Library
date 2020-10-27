package org.istio.library.controller;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.istio.library.generated.Book;
import org.istio.library.generated.LibraryGrpc;
import org.istio.library.generated.SearchReply;
import org.istio.library.generated.SearchRequest;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LibraryAccess {
    private static final Logger logger = Logger.getLogger(LibraryAccess.class.getName());

    private ManagedChannel channel;
    private LibraryGrpc.LibraryBlockingStub blockingStub;


    public LibraryAccess(String host, int port) {

        JWTClientInterceptor clientInterceptor = new JWTClientInterceptor();
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .intercept(clientInterceptor)
                .build();
        this.blockingStub =  LibraryGrpc.newBlockingStub(channel);
    }

    public List<org.istio.library.model.Book> findBooks(String term) {

        SearchRequest.Builder builder = SearchRequest.newBuilder()
                .setTerm(term == null ? "" : term)
                .setType(SearchRequest.SearchType.ALL);

        try {
            SearchReply reply = blockingStub
                    .withDeadlineAfter(5000L, TimeUnit.MILLISECONDS)
                    .findBooks(builder.build());

            List<Book> booksList = reply.getBooksList();
            ArrayList<org.istio.library.model.Book> resultList = new ArrayList<>(booksList.size());
            for (Book book : booksList) {
                org.istio.library.model.Book modelBook = new org.istio.library.model.Book();
                modelBook.setAuthor(book.getAuthor());
                modelBook.setId(book.getIsbn());
                modelBook.setTitle(book.getName());
                resultList.add(modelBook);
            }
            resultList.sort(Comparator.comparing(org.istio.library.model.Book::getTitle));
            return resultList;
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return Collections.emptyList();
        }
    }
}
