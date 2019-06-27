/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.servico;

import java.util.List;
import br.edu.utfpr.dto.ClienteDTO;
import br.edu.utfpr.excecao.NomeClienteMenor5CaracteresException;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Jessica
 */
public class ServicoCliente {

    private List<ClienteDTO> clientes;

    public ServicoCliente() {
        clientes = Stream.of(
                ClienteDTO.builder().id(1).nome("Teste").idade(20).telefone("111111111").limiteCredito(1111.11).build(),
                ClienteDTO.builder().id(1).nome("Teste2").idade(22).telefone("222222222").limiteCredito(2222.22).build()
        ).collect(Collectors.toList());
    }

    @GetMapping("/servico/cliente")
    public ResponseEntity<List<ClienteDTO>> ler() {
        return ResponseEntity.ok(clientes);
    }

    @PostMapping("/servico/cliente")
    public ResponseEntity<ClienteDTO> criar(@RequestBody ClienteDTO cliente) {
        cliente.setId(clientes.size() + 1);
        clientes.add(cliente);
        return ResponseEntity.status(201).body(cliente);
    }

    @PutMapping("/servico/cliente/{id}")
    public ResponseEntity<ClienteDTO> alterar(@PathVariable int id, @RequestBody ClienteDTO cliente) {
        Optional<ClienteDTO> clienteExistente = clientes.stream().filter(c -> c.getId() == id).findAny();

        clienteExistente.ifPresent(c -> {
            try {
                c.setNome(cliente.getNome());
            } catch (NomeClienteMenor5CaracteresException ex) {
                Logger.getLogger(ServicoCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            c.setIdade(cliente.getIdade());
            c.setTelefone(cliente.getTelefone());
            c.setLimiteCredito(cliente.getLimiteCredito());
        });

        return ResponseEntity.of(clienteExistente);
    }

    @DeleteMapping("/servico/cliente/{id}")
    public ResponseEntity excluir(@PathVariable int id) {
        if (clientes.removeIf(cliente -> cliente.getId() == id)) 
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.notFound().build();
    }
}
