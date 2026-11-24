/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author marco
 */
import java.util.ArrayList;
import java.util.List;

public class AstNode {

    /**
     * @return the linha
     */
    public int getLinha() {
        return linha;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @return the filhos
     */
    public List<AstNode> getFilhos() {
        return filhos;
    }
    private String nome;
    private List<AstNode> filhos;
    private int linha;

    public AstNode(String nome) {
        this.nome = nome;
        this.filhos = new ArrayList<>();
        this.linha = 0;
    }

    public AstNode(String nome, int linha, AstNode... filhos) {
        this(nome);
        this.linha = linha;

        for (AstNode filho : filhos) {
            if (filho != null) {
                this.filhos.add(filho);
            }
        }
    }

    public void adicionarFilho(AstNode filho) {
        if (filho != null) {
            getFilhos().add(filho);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        imprimir(sb, "", true);
        return sb.toString();
    }

    private void imprimir(
        StringBuilder sb,
        String prefixo,
        boolean ultimo
    ) {
        sb.append(prefixo);

        sb.append(ultimo ? "└── " : "├── ");
        sb.append(getNome());
        sb.append("\n");

        for (int i = 0; i < getFilhos().size(); i++) {
            boolean ultimoFilho = (i == getFilhos().size() - 1);

            getFilhos().get(i).imprimir(
                sb,
                prefixo + (ultimo ? "    " : "│   "),
                ultimoFilho
            );
        }
    }
    
}
