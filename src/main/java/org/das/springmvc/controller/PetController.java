package org.das.springmvc.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.das.springmvc.model.Pet;
import org.das.springmvc.service.PetService;
import org.das.springmvc.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//todo export in main branch
@RequestMapping("/pets")
@RestController
public class PetController {
    private final static Logger LOGGER = LoggerFactory.getLogger(PetController.class);
    private final PetService petService;
    private final UserService userService;

    @Autowired
    public PetController(PetService petService, UserService userService) {
        this.petService = petService;
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<Pet> create(@RequestBody @Valid Pet petToCreate) {
        LOGGER.info("Get request in PetController for created pet: pet={}", petToCreate);
        Pet pet = petService.create(petToCreate);
        if (!petToCreate.isUserIdEmpty()) {
            userService.findById(petToCreate.userId()).addPet(pet);
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> updateById(
           @PathVariable("id") @NotNull Long id,
           @RequestBody @Valid Pet petToUpdate) {
        LOGGER.info("Get request in PetController update for pet with id: id={}, pet: pet={}", id, petToUpdate);
        Pet updatedPet = petService.updateById(id, petToUpdate);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedPet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") @NotNull Long id) {
        LOGGER.info("Get request in PetController delete for pet with id: id={}", id);
        Pet pet = petService.deleteById(id);
        if (!pet.isUserIdEmpty()) {
            LOGGER.info("execute method findById and removePet in UserService, userId: id={}, pet: pet={}"
                    ,id ,pet);
            userService.findById(pet.userId()).removePet(pet);
        }
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping()
    public ResponseEntity<List<Pet>> findAll(
           @RequestParam(value = "name", required = false) String name,
           @RequestParam(value = "userId", required = false) Long userId
    ) {
        LOGGER.info("Get request in PetController findAll for pet with name: name={}, userId: userId={}"
                , name, userId);
        List<Pet> pets = petService.findAll(name, userId);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(pets);
    }

    @GetMapping("/pets/{id}")
    public ResponseEntity<Pet> findById(@PathVariable("id") @NotNull Long id) {
        LOGGER.info("Get request in PetController find for pet with id: id={}", id);
        Pet pet = petService.findById(id);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(pet);
    }
}
