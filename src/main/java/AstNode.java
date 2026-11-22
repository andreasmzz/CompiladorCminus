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
     private String nome;
    private List<AstNode> filhos;

    public AstNode(String nome) {
        this.nome = nome;
        this.filhos = new ArrayList<>();
    }

    public AstNode(String nome, AstNode... filhos) {
        this(nome);

        for (AstNode filho : filhos) {
            if (filho != null) {
                this.filhos.add(filho);
            }
        }
    }

    public void adicionarFilho(AstNode filho) {
        if (filho != null) {
            filhos.add(filho);
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
        sb.append(nome);
        sb.append("\n");

        for (int i = 0; i < filhos.size(); i++) {
            boolean ultimoFilho = (i == filhos.size() - 1);

            filhos.get(i).imprimir(
                sb,
                prefixo + (ultimo ? "    " : "│   "),
                ultimoFilho
            );
        }
    }
    
}
