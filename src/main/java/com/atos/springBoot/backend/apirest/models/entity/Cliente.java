package com.atos.springBoot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name="clientes")
public class Cliente implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable=false)
	@NotEmpty(message="No puede estar vacio")
	@Size(min = 4, max = 12, message="El tamaño tiene que estar entre 4 y 12")
	private String nombre;
	
	@NotEmpty(message="No puede estar vacio")
	private String apellido;
	
	@Column(name="create_at")
	@Temporal(TemporalType.DATE)
	private Date createAt;
	
	@PrePersist
	public void prePersist() {
		createAt = new Date();
	}
	
	@NotEmpty(message="No puede estar vacio")
	@Email(message="No es una dirección de correo bien formada")
	@Column(nullable=false, unique = true)
	private String email;

}
