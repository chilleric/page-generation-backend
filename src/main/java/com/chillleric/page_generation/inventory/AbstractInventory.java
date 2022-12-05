package com.chillleric.page_generation.inventory;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractInventory<r> {
    @Autowired
    protected r repository;
}