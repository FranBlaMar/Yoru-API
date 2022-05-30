package main.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Clase usuario
 * @author usuario
 *
 */
@Entity
@Table(name = "Usuario")
@Setter
@ToString
@Getter
@NoArgsConstructor
public class User {
	
	@Id
	private String email;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
	@Column (name="userName", nullable = false)
	private String userName;
	
	@Column (name="fotoPerfil")
	@Lob
	private byte[] fotoPerfil;
	
	@Column (name="descripcion")
	private String aboutMe;

	@Column (name="num_seguidores")
	private Integer numeroSeguidores;
	
	@Column (name="num_seguidos")
	private Integer numeroSeguidos;
	
	@Column (name="num_publicaciones")
	private Integer numeroPublicaciones;
	
	
	@OneToMany(cascade = CascadeType.ALL)
	@JsonBackReference
	private List<Publicacion> publicaciones;
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Publicacion> publicacionesGustadas;
	
	@ManyToMany()
	@JsonIgnore
	private List<User> seguidores;
	
	@ManyToMany()
	@JsonIgnore
	private List<User> seguidos;
	
	
	public User(String userName,String password, String email, String aboutMe, byte[] fotoPerfil){
		this.userName = userName;
		this.password = password;
		this.email = email;
		if(aboutMe.isBlank()) {
			this.aboutMe = "¡Introduce una breve presentación!";
		}
		else {
			this.aboutMe = aboutMe;	
		}
		this.fotoPerfil = fotoPerfil;
		this.numeroPublicaciones = 0;
		this.numeroSeguidores = 0;
		this.numeroSeguidos = 0;
	}

	public void anadirPublicacion(Publicacion publi) {
		this.publicaciones.add(publi);
	}
	
	public void anadirSeguidor(User seguidor) {
		this.seguidores.add(seguidor);
	}
	
	public void anadirSeguido(User seguido) {
		this.seguidos.add(seguido);
	}
	
	public void anadirPublicacionGustada(Publicacion publi) {
		this.publicacionesGustadas.add(publi);
	}
}
