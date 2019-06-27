/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.servico;

import java.util.List;
import br.edu.utfpr.dto.ClienteDTO;
import br.edu.utfpr.dto.PaisDTO;
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
    private List<PaisDTO> paises;

    public ServicoCliente() {
        paises = Stream.of(
                PaisDTO.builder().id(1).nome("Brasil").sigla("BR").codigoTelefone(55).build(),
                PaisDTO.builder().id(2).nome("Japão").sigla("JP").codigoTelefone(81).build()
        ).collect(Collectors.toList());
        clientes = Stream.of(
                ClienteDTO.builder().id(1).nome("Teste").idade(20).telefone("111111111").limiteCredito(1111.11).pais(paises.get(1)).build(),
                ClienteDTO.builder().id(2).nome("Teste2").idade(22).telefone("222222222").limiteCredito(2222.22).pais(paises.get(1)).build()
        ).collect(Collectors.toList());
    }

    @GetMapping("/servico/cliente")
    public ResponseEntity<List<ClienteDTO>> ler() {
        return ResponseEntity.ok(clientes);
    }

    @PostMapping("/servico/cliente")
    public ResponseEntity<ClienteDTO> criar(@RequestBody ClienteDTO cliente) {
        Optional<PaisDTO> pais = paises.stream().filter(p -> p.getId() == cliente.getPais().getId()).findAny();
        
        cliente.setId(clientes.size() + 1);
        cliente.setPais(pais.get());
        clientes.add(cliente);
        return ResponseEntity.status(201).body(cliente);
    }

    @PutMapping("/servico/cliente/{id}")
    public ResponseEntity<ClienteDTO> alterar(@PathVariable int id, @RequestBody ClienteDTO cliente) {
        Optional<ClienteDTO> clienteExistente = clientes.stream().filter(c -> c.getId() == id).findAny();
        Optional<PaisDTO> pais = paises.stream().filter( p -> p.getId() == cliente.getPais().getId() ).findAny();
        
        clienteExistente.ifPresent(c -> {
            try {
                c.setNome(cliente.getNome());
            } catch (NomeClienteMenor5CaracteresException ex) {
                Logger.getLogger(ServicoCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            c.setIdade(cliente.getIdade());
            c.setTelefone(cliente.getTelefone());
            c.setLimiteCredito(cliente.getLimiteCredito());
            c.setPais(pais.get());
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
