package fr.xebia.coherence.bean;

import java.io.Serializable;

public class Commune implements Serializable {

	private static final long serialVersionUID = 7299137017325855682L;

	private int id;
	private int departement;
	private int insee;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDepartement() {
		return departement;
	}

	public void setDepartement(int departement) {
		this.departement = departement;
	}

	public int getInsee() {
		return insee;
	}

	public void setInsee(int insee) {
		this.insee = insee;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Commune [id=" + id + ", departement=" + departement
				+ ", insee=" + insee + ", name=" + name + "]";
	}

}
