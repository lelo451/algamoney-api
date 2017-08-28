package com.algamoney.api.resource;

import com.algamoney.api.event.RecursoCriadoEvent;
import com.algamoney.api.model.Pessoa;
import com.algamoney.api.repository.PessoaRepository;
import com.algamoney.api.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {

    private final PessoaRepository pessoaRepository;
    private final ApplicationEventPublisher publisher;
    private final PessoaService pessoaService;

    @Autowired
    public PessoaResource(PessoaRepository pessoaRepository, ApplicationEventPublisher publisher, PessoaService pessoaService) {
        this.pessoaRepository = pessoaRepository;
        this.publisher = publisher;
        this.pessoaService = pessoaService;
    }

    @GetMapping
    public List<Pessoa> list() {
        return pessoaRepository.findAll();
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Pessoa> listOne(@PathVariable Long codigo) {
        Pessoa pessoa = pessoaRepository.findOne(codigo);
        return pessoa != null ? ResponseEntity.ok(pessoa) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Pessoa> create(@Valid @RequestBody Pessoa categoria, HttpServletResponse response) {
        Pessoa pessoaSalva = pessoaRepository.save(categoria);
        publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getCodigo()));
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Pessoa> update(@PathVariable Long codigo, @Valid @RequestBody Pessoa pessoa) {
        Pessoa pessoaSalva = pessoaService.update(codigo, pessoa);
        return ResponseEntity.ok(pessoaSalva);
    }

    @PutMapping("/{codigo}/ativo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
        pessoaService.updateAtivo(codigo, ativo);
    }

    @DeleteMapping("/{codigo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long codigo) {
        pessoaRepository.delete(codigo);
    }
}