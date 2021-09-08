package com.bolsadeideas.springboot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="clientes")
public class Cliente implements Serializable { 

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty(message=("No puede estar vacío"))
	@Size(min=4, max=12, message="el tamaño debe estar entre 4 y 12 caracteres")
	@Column(nullable=false)
	private String nombre;
	
	@NotEmpty(message=("No puede estar vacío"))
	@Size(min=4, max=12, message="el tamaño debe estar entre 4 y 12 caracteres")
	@Column(nullable=false)
	private String apellido;
	
	@NotEmpty(message=("No puede estar vacío"))
	@Email(message="No es una dirección de correo válida")
	@Column(nullable=false, unique=true)
	private String email;
	
	@NotNull(message="No puede estar vacío")
	@Column(name="create_at")
	@Temporal(TemporalType.DATE) //Transforma la fecha java a la fecha date de SQL
	private Date createAt;
	
	private String foto;
	
	@NotNull(message="No puede estar vacío")
	@ManyToOne(fetch=FetchType.LAZY) //Fetch indica la forma en que se obtienen los datos en la relación
	@JoinColumn(name="region_id") //Llave foránea
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) //Ignoramos unos atributos
	private Region region;
	
	/*@PrePersist //Metodo que se ejecuta antes de persistir en la BBDD
	public void prePersist() {
		this.createAt = new Date();
	}*/
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}
	
	

}
