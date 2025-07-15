package com.myaws.controller;
import java.util.Iterator;
import java.util.List;

import com.myaws.entity.Expense;
import com.myaws.repo.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("")
public class ExpenseController {

    @Autowired
    private ExpenseRepository repo;



    @PostMapping("/save")
    public String save(@RequestBody Expense expense)
    {
        System.out.println("incoming:"+expense);
        repo.save(expense);
        return "Success";
    }


    @GetMapping("/findall")
    public Iterable<Expense> findAll()
    {
        Iterable<Expense> i=repo.findAll();
        for(Expense e:i)
        {
            System.out.println(e);
        }

        return repo.findAll();
    }
}