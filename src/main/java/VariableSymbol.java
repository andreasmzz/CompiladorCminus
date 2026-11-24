/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author marco
 */

public class VariableSymbol {

    private String nome;
    private String tipo;
    private boolean vetor;
    private int tamanho;
    private int linha;

    public VariableSymbol(
        String nome,
        String tipo,
        boolean vetor,
        int tamanho,
        int linha
    ) {
        this.nome = nome;
        this.tipo = tipo;
        this.vetor = vetor;
        this.tamanho = tamanho;
        this.linha = linha;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean isVetor() {
        return vetor;
    }

    public int getTamanho() {
        return tamanho;
    }

    public int getLinha() {
        return linha;
    }

    @Override
    public String toString() {
        return nome + " | " + tipo +
               " | " + (vetor ? "Vetor[" + tamanho + "]" : "Variável") +
               " | Linha: " + linha;
    }
}
