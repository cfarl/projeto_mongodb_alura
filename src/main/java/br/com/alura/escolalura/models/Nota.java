package br.com.alura.escolalura.models;

public class Nota {
	
	private Integer valor;
	
	public Nota(){}
	
	public Nota(Integer valor) {
		this.valor = valor;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	@Override
	public String toString() {
		return "Nota [valor=" + valor + "]";
	}
	
	
}
