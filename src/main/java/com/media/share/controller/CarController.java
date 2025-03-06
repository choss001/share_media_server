package com.media.share.controller;

import com.media.share.model.CarModel;
import com.media.share.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/car")
public class CarController {

    private final CarRepository carRepository;

    @PostMapping
    public void save(@RequestBody CarModel car){
        carRepository.save(car);
    }

    @GetMapping("/{id}")
    public CarModel findById(@PathVariable("id") String id){
        return carRepository.findById(id).orElse(null);
    }

    @GetMapping
    public Iterable<CarModel> findAll(){
        return carRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id){
        carRepository.deleteById(id);
    }

    @PutMapping
    public void update(@RequestBody CarModel car){
        carRepository.save(car);
    }
}
