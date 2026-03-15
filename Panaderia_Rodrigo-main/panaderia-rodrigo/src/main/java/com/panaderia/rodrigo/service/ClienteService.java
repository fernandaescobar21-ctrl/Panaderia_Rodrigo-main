package com.panaderia.rodrigo.service;

import com.panaderia.rodrigo.model.Cliente;
import com.panaderia.rodrigo.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    public Cliente findByEmail(String email) {
        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con email: " + email));
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Cliente save(Cliente cliente) {
        // Bug fix: solo verificar email duplicado en CREACIÓN (id == null).
        // Al editar, el cliente ya tiene ese email registrado; si no se verifica
        // el id, siempre lanzaría "Ya existe un cliente con ese email".
        if (cliente.getId() == null && clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new RuntimeException("Ya existe un cliente con el email: " + cliente.getEmail());
        }
        return clienteRepository.save(cliente);
    }

    public Cliente update(Long id, Cliente clienteActualizado) {
        Cliente existente = findById(id);
        existente.setNombre(clienteActualizado.getNombre());
        existente.setTelefono(clienteActualizado.getTelefono());
        existente.setDireccion(clienteActualizado.getDireccion());
        return clienteRepository.save(existente);
    }

    public void delete(Long id) {
        Cliente cliente = findById(id);
        clienteRepository.delete(cliente);
    }

    public long count() {
        return clienteRepository.count();
    }
}
