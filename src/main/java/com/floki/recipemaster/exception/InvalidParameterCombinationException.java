package com.floki.recipemaster.exception;
public class InvalidParameterCombinationException extends IllegalArgumentException {
    /**
   * 
   */
  private static final long serialVersionUID = 1L;

    public InvalidParameterCombinationException(String message) {
        super(message);
    }
}