package com.panaderia.rodrigo;

import com.panaderia.rodrigo.model.Producto;
import com.panaderia.rodrigo.repository.ProductoRepository;
import com.panaderia.rodrigo.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RodrigoApplicationTests {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void contextLoads() {
        // Verifica que el contexto Spring cargue correctamente
    }

    @Test
    void testProductosDataLoader() {
        List<Producto> productos = productoService.findAll();
        assertThat(productos).isNotEmpty();
        assertThat(productos.size()).isGreaterThan(0);
    }

    @Test
    void testBuscarProductoPorNombre() {
        List<Producto> resultados = productoService.buscarPorNombre("Medialuna");
        assertThat(resultados).isNotEmpty();
    }

    @Test
    void testGuardarProducto() {
        Producto nuevo = Producto.builder()
                .nombre("Pan de Prueba")
                .categoria("Pan")
                .precio(100.0)
                .stock(10)
                .disponible(true)
                .build();

        Producto guardado = productoService.save(nuevo);
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Pan de Prueba");

        // Limpiar
        productoService.delete(guardado.getId());
    }

    @Test
    void testFindStockBajo() {
        List<Producto> stockBajo = productoService.findStockBajo();
        assertThat(stockBajo).isNotNull();
    }
}
