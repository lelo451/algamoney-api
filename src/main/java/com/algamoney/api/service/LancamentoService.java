package com.algamoney.api.service;

import com.algamoney.api.model.Lancamento;
import com.algamoney.api.model.Pessoa;
import com.algamoney.api.repository.LancamentoRepository;
import com.algamoney.api.repository.PessoaRepository;
import com.algamoney.api.service.exception.PessoaInexistenteOuInativaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LancamentoService {

    private final PessoaRepository pessoaRepository;
    private final LancamentoRepository lancamentoRepository;

    @Autowired
    public LancamentoService(PessoaRepository pessoaRepository, LancamentoRepository lancamentoRepository) {
        this.pessoaRepository = pessoaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    public Lancamento save(Lancamento lancamento) {
        Pessoa pessoa = pessoaRepository.findOne(lancamento.getPessoa().getCodigo());
        if(pessoa == null || pessoa.isIntivo()) {
            throw new PessoaInexistenteOuInativaException();
        }
        return lancamentoRepository.save(lancamento);
    }
}