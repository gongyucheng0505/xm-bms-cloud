package com.xm.bms.carinfo.service;

import com.xm.bms.carinfo.domain.CarSignal;

import java.util.List;

public interface CarSignalService {
    CarSignal getCarSignalById(Long id);
    List<CarSignal> getAllCarSignals();
    int createCarSignal(CarSignal carSignal);
    void updateCarSignal(CarSignal carSignal);
    int deleteCarSignal(Long id) throws InterruptedException;

}
