package com.atos.springBoot.backend.apirest.models.dao;

import org.springframework.data.repository.CrudRepository;
import com.atos.springBoot.backend.apirest.models.entity.Cliente;

public interface IClienteDao extends CrudRepository<Cliente, Long>{

}
