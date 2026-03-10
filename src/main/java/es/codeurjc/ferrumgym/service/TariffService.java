package es.codeurjc.ferrumgym.service;

import es.codeurjc.ferrumgym.model.Tariff;
import es.codeurjc.ferrumgym.repository.TariffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TariffService {

    @Autowired
    private TariffRepository tariffRepository;

    // Método para devolver TODAS las tarifas (para mostrarlas en prices.html)
    public List<Tariff> findAll() {
        return tariffRepository.findAll();
    }

    // Método para buscar una tarifa concreta por su ID
    public Optional<Tariff> findById(Long id) {
        return tariffRepository.findById(id);
    }

    // Método para guardar una tarifa nueva o actualizarla
    public void save(Tariff tariff) {
        tariffRepository.save(tariff);
    }

    // Método para borrar una tarifa
    public void deleteById(Long id) {
        tariffRepository.deleteById(id);
    }
}