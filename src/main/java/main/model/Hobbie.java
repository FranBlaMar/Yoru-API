package main.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Hobbie")
@Setter
@ToString
@Getter
@NoArgsConstructor
public class Hobbie {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idHobbie;
	
	@Column (name="hobbie")
	private String hobbie;
	
	public Hobbie(String hobbie) {
		this.hobbie = hobbie;
	}
}
