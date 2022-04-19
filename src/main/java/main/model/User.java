package main.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
	
	@Column (name="descripcion")
	private String aboutMe;

	@Column (name="num_seguidores")
	private Integer numeroSeguidores;
	
	@Column (name="num_seguidos")
	private Integer numeroSeguidos;
	
	@Column (name="num_publicaciones")
	private Integer numeroPublicaciones;
	
	
	
	public User(String userName,String password, String email, String aboutMe){
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.aboutMe = aboutMe;
		this.numeroPublicaciones = 0;
		this.numeroSeguidores = 0;
		this.numeroSeguidos = 0;
	}


}
