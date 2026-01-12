package com.moayad.stocktracker.exception;

public class FavoriteAlreadyExistsException extends RuntimeException {
        public FavoriteAlreadyExistsException(String symbol) {
            super("Stock already saved as a favorite "+ symbol);
        }
}
