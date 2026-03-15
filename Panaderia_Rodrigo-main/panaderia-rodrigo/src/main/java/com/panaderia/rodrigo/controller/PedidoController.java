package com.panaderia.rodrigo.controller;

import com.panaderia.rodrigo.model.Pedido;
import com.panaderia.rodrigo.model.Pedido.EstadoPedido;
import com.panaderia.rodrigo.model.Producto;
import com.panaderia.rodrigo.service.ClienteService;
import com.panaderia.rodrigo.service.PedidoService;
import com.panaderia.rodrigo.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.findAll());
        model.addAttribute("estados", EstadoPedido.values());
        model.addAttribute("pendientes", pedidoService.countByEstado(EstadoPedido.PENDIENTE));
        model.addAttribute("enPreparacion", pedidoService.countByEstado(EstadoPedido.EN_PREPARACION));
        return "pedidos/lista";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public String formularioNuevo(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("productos", productoService.findDisponibles());
        return "pedidos/formulario";
    }

    // Bug fix: el formulario HTML envía los checkboxes de productos como una lista
    // de IDs (Long), no como objetos Producto. @ModelAttribute Pedido no puede
    // convertir esos IDs a List<Producto> automáticamente.
    // Solución: recibir los IDs por separado con @RequestParam y resolverlos.
    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public String guardar(@ModelAttribute Pedido pedido,
                          @RequestParam(value = "productos", required = false) List<Long> productoIds,
                          RedirectAttributes flash) {

        List<Producto> productosSeleccionados = (productoIds != null)
                ? productoIds.stream()
                .map(id -> productoService.findById(id))
                .collect(Collectors.toList())
                : Collections.emptyList();

        pedido.setProductos(productosSeleccionados);
        pedidoService.save(pedido);
        flash.addFlashAttribute("mensaje", "Pedido registrado correctamente ✅");
        return "redirect:/pedidos";
    }

    @GetMapping("/estado/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLEADO')")
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam EstadoPedido estado,
                                RedirectAttributes flash) {
        pedidoService.cambiarEstado(id, estado);
        flash.addFlashAttribute("mensaje", "Estado actualizado: " + estado);
        return "redirect:/pedidos";
    }

    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        pedidoService.delete(id);
        flash.addFlashAttribute("mensaje", "Pedido eliminado 🗑️");
        return "redirect:/pedidos";
    }
}
