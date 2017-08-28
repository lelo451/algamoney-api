package com.algamoney.api.service;

import com.algamoney.api.model.Pessoa;
import com.algamoney.api.repository.PessoaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    @Autowired
    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public Pessoa update(Long codigo, Pessoa pessoa) {
        Pessoa pessoaSalva = getPessoaByCodigo(codigo);
        BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
        return pessoaRepository.save(pessoaSalva);
    }

    public void updateAtivo(Long codigo, Boolean ativo) {
        Pessoa pessoaSalva = getPessoaByCodigo(codigo);
        pessoaSalva.setAtivo(ativo);
        pessoaRepository.save(pessoaSalva);
    }

    private Pessoa getPessoaByCodigo(Long codigo) {
        Pessoa pessoaSalva = pessoaRepository.findOne(codigo);
        if (pessoaSalva == null) {
            throw  new EmptyResultDataAccessException(1);
        }
        return pessoaSalva;
    }
}
