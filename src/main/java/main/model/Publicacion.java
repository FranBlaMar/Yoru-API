package main.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Clase publicación
 * @author fblanco
 *
 */
@Entity
@Table(name = "Publicacion")
@Setter
@ToString
@Getter
@NoArgsConstructor
public class Publicacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idPublicacion;
	
	@Column (name="titulo")
	private String titulo;
	
	@Column (name="imagen")
	@Lob
	private byte[] imagen;
	
	@ManyToOne
	private User autor;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Comentario> comentarios;
	
	@Column (name="likes")
	private int likes;

	@Column (name="fechaPublicacion")
	private LocalDate fechaPublicacion;
	
	public Publicacion(String titulo, byte[] imagen, User autor) {
		super();
		this.titulo = titulo;
		this.imagen = imagen;
		this.autor = autor;
		this.likes = 0;
		this.fechaPublicacion = LocalDate.now();
		this.comentarios = new ArrayList<>();
	}
	
	
	public void addComentario(Comentario comentario) {
		this.comentarios.add(comentario);
	}
}
