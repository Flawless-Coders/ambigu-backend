package com.flawlesscoders.ambigu.modules.user.waiter;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WaiterService {

    private final WaiterRepository waiterRepository;

    public List<Waiter> getAllWaiters() {
        return waiterRepository.findAllWaiters();
    }

    public List<Waiter> getAllActiveWaiters() {
        return waiterRepository.findAllByStatusTrue();
    }

    public Waiter getWaiterById(String id) {
        return waiterRepository.findById(id).orElseThrow(() -> new RuntimeException("Waiter not found"));
    }

    public Waiter createWaiter(Waiter waiter) {
        return waiterRepository.save(waiter);
    }

    public Waiter updateWaiter(Waiter waiter) {
        Waiter existingWaiter = getWaiterById(waiter.getId());
        existingWaiter.setName(waiter.getName());
        existingWaiter.setLastname_p(waiter.getEmail());
        existingWaiter.setLastname_m(waiter.getPassword());
        existingWaiter.setShift(waiter.getShift());
        existingWaiter.setEmail(waiter.getEmail());
        return waiterRepository.save(existingWaiter);
    }

    public Waiter changeWaiterStatus(String id) {
        Waiter existingWaiter = getWaiterById(id);
        existingWaiter.setStatus(!existingWaiter.isStatus());
        return waiterRepository.save(existingWaiter);
    }
    
}
