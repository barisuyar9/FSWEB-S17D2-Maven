package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    private final Taxable taxable;

    public Map<Integer, Developer> developers;

    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @GetMapping
    public List<Developer> getAllDevelopers() {
        return new ArrayList<>(developers.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Developer> getDeveloperById(@PathVariable int id) {
        Developer dev = developers.get(id);
        if (dev == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dev);
    }

    @PostMapping
    public ResponseEntity<Developer> createDeveloper(@RequestBody Developer developer) {
        Experience exp = developer.getExperience();
        double baseSalary = developer.getSalary();
        Developer newDev = null;

        switch (exp) {
            case JUNIOR:
                baseSalary -= (baseSalary * taxable.getSimpleTaxRate()) / 100;
                newDev = new JuniorDeveloper(developer.getId(), developer.getName(), baseSalary);
                break;
            case MID:
                baseSalary -= (baseSalary * taxable.getMiddleTaxRate()) / 100;
                newDev = new MidDeveloper(developer.getId(), developer.getName(), baseSalary);
                break;
            case SENIOR:
                baseSalary -= (baseSalary * taxable.getUpperTaxRate()) / 100;
                newDev = new SeniorDeveloper(developer.getId(), developer.getName(), baseSalary);
                break;
        }

        developers.put(newDev.getId(), newDev);
        return new ResponseEntity<>(newDev, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Developer> updateDeveloper(@PathVariable int id, @RequestBody Developer updatedDev) {
        if (!developers.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        developers.put(id, updatedDev);
        return ResponseEntity.ok(updatedDev);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeveloper(@PathVariable int id) {
        developers.remove(id);
        return ResponseEntity.ok().build();
    }
}
