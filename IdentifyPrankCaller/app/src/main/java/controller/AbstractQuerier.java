package controller;

import model.QueryResult;

/**
 * Created by Alex on 07.04.2015.
 */
public abstract class AbstractQuerier
{
    public abstract QueryResult query(String phoneNumber);
}
