package com.moto.service.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.moto.service.entidades.Moto;

@Repository
public interface MotoRepositorio extends JpaRepository<Moto, Long> {
	List<Moto> findByUsuarioId(Long usuarioId);

}
