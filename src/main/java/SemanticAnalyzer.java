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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticAnalyzer {

    private SymbolTable tabela;
    private List<String> erros;

    public SemanticAnalyzer() {
        tabela = new SymbolTable();
        erros = new ArrayList<>();
    }

    public void analisar(AstNode raiz) {
        tabela = new SymbolTable();
        erros.clear();

        if (raiz == null) {
            erros.add("Árvore sintática não encontrada.");
            return;
        }

        // Primeira etapa: registrar as declarações.
        registrarDeclaracoes(raiz);

        // Segunda etapa: verificar utilizações e atribuições.
        verificarComandos(raiz);
    }

    /*
     * Percorre as listas de declarações.
     * A estrutura da AST pode conter DeclList recursivos.
     */
    private void registrarDeclaracoes(AstNode no) {

        if (no.getNome().equals("DeclList")) {

            AstNode listaAnterior = null;
            AstNode tipo = null;
            AstNode identificadores = null;

            for (AstNode filho : no.getFilhos()) {

                if (filho.getNome().equals("DeclList")) {
                    listaAnterior = filho;
                } else if (filho.getNome().startsWith("Type:")) {
                    tipo = filho;
                } else if (filho.getNome().equals("IdentifierList")) {
                    identificadores = filho;
                }
            }

            // Processa primeiro as declarações anteriores.
            if (listaAnterior != null) {
                registrarDeclaracoes(listaAnterior);
            }

            if (tipo != null && identificadores != null) {
                String tipoVariavel = tipo.getNome()
                        .substring("Type:".length()).trim();

                registrarIdentificadores(
                        identificadores,
                        tipoVariavel
                );
            }

            return;
        }

        for (AstNode filho : no.getFilhos()) {
            registrarDeclaracoes(filho);
        }
    }

    /*
     * Registra variáveis simples e vetores.
     */
    private void registrarIdentificadores(
            AstNode lista,
            String tipo) {

        for (AstNode filho : lista.getFilhos()) {

            String nome = filho.getNome();

            if (nome.startsWith("VariableDeclaration:")) {

                String identificador = nome
                        .substring("VariableDeclaration:".length())
                        .trim();

                declarar(identificador, tipo, false, 0, filho.getLinha());

            } else if (nome.startsWith("ArrayDeclaration:")) {

                String declaracao = nome
                        .substring("ArrayDeclaration:".length())
                        .trim();

                Matcher matcher = Pattern.compile(
                        "([a-zA-Z][a-zA-Z0-9]*)\\[(\\d+)\\]"
                ).matcher(declaracao);

                if (matcher.matches()) {
                    String identificador = matcher.group(1);
                    int tamanho = Integer.parseInt(matcher.group(2));

                    declarar(identificador, tipo, true, tamanho, filho.getLinha());
                } else {
                    erros.add(
                            "Declaração de vetor inválida: " + declaracao
                    );
                }

            } else {
                registrarIdentificadores(filho, tipo);
            }
        }
    }

    private void declarar(
            String nome,
            String tipo,
            boolean vetor,
            int tamanho,
            int linha) {

        VariableSymbol simbolo = new VariableSymbol(
                nome, tipo, vetor, tamanho, linha
        );

        if (!tabela.declarar(simbolo)) {
            erros.add(
                    "Identificador '" + nome +
                    "' já foi declarado na linha " + linha + "."
            );
        }
    }

    /*
     * Percorre os comandos da árvore.
     */
    private void verificarComandos(AstNode no) {

        // Não verifica os nós de declaração como utilizações.
        if (no.getNome().equals("DeclList")
                || no.getNome().equals("IdentifierList")
                || no.getNome().startsWith("VariableDeclaration:")
                || no.getNome().startsWith("ArrayDeclaration:")) {
            return;
        }

        if (no.getNome().equals("Assignment")) {
            verificarAtribuicao(no);
        }

        for (AstNode filho : no.getFilhos()) {
            verificarComandos(filho);
        }

        // Verifica variáveis utilizadas em comandos que não sejam
        // atribuições, como Write.
        if (no.getNome().startsWith("Variable:")) {
            verificarVariavel(no);
        }
    }

    private void verificarAtribuicao(AstNode atribuicao) {

        List<AstNode> filhos = atribuicao.getFilhos();

        if (filhos.size() < 2) {
            return;
        }

        AstNode destino = filhos.get(0);
        AstNode expressao = filhos.get(1);

        String nome = extrairNomeVariavel(destino);

        if (nome == null) {
            return;
        }

        // A verificação de variável não declarada será feita
        // pelo percurso geral da AST.
        VariableSymbol simbolo = tabela.buscar(nome);

        if (simbolo == null) {
            return;
        }

        String tipoExpressao = inferirTipo(expressao);

        if (tipoExpressao == null) {
            return;
        }

        if (!tiposCompativeis(simbolo.getTipo(), tipoExpressao)) {
            erros.add(
                    "Atribuição incompatível: variável '" +
                    simbolo.getNome() + "' é do tipo " +
                    simbolo.getTipo() +
                    ", mas recebeu uma expressão do tipo " +
                    tipoExpressao + "."
            );
        }
    }

    private void verificarVariavel(AstNode no) {
        buscarVariavel(no);
    }

    private VariableSymbol buscarVariavel(AstNode no) {

        String nome = extrairNomeVariavel(no);

        if (nome == null) {
            return null;
        }

        VariableSymbol simbolo = tabela.buscar(nome);

        if (simbolo == null) {
            erros.add(
                    "Variável '" + nome +
                    "' utilizada sem declaração."
            );
            return null;
        }

        return simbolo;
    }

    private String extrairNomeVariavel(AstNode no) {

        String nome = no.getNome();

        if (!nome.startsWith("Variable:")) {
            return null;
        }

        return nome.substring("Variable:".length()).trim();
    }

    /*
     * Determina o tipo de uma expressão.
     * Nesta versão inicial, considera variáveis e constantes.
     */
    private String inferirTipo(AstNode no) {

        String nome = no.getNome();

        if (nome.startsWith("Integer:")) {
            return "int";
        }

        if (nome.startsWith("Float:")) {
            return "float";
        }

        if (nome.startsWith("Variable:")) {
            
            String identificador = extrairNomeVariavel(no);
            
            VariableSymbol simbolo = tabela.buscar(identificador);

            return simbolo == null ? null : simbolo.getTipo();
        }

        // Expressões compostas: verifica os tipos dos operandos.
        if (no.getFilhos().size() >= 2) {

            String tipoEsquerdo = inferirTipo(no.getFilhos().get(0));
            String tipoDireito = inferirTipo(no.getFilhos().get(1));

            if (tipoEsquerdo == null || tipoDireito == null) {
                return null;
            }

            if (tipoEsquerdo.equals("float")
                    || tipoDireito.equals("float")) {
                return "float";
            }

            return "int";
        }

        if (no.getFilhos().size() == 1) {
            return inferirTipo(no.getFilhos().get(0));
        }

        return null;
    }

    private boolean tiposCompativeis(
            String tipoDestino,
            String tipoOrigem) {

        // int pode ser convertido para float.
        // float não pode ser atribuído diretamente a int.
        if (tipoDestino.equals("float")) {
            return tipoOrigem.equals("int")
                    || tipoOrigem.equals("float");
        }

        return tipoDestino.equals(tipoOrigem);
    }

    public SymbolTable getTabela() {
        return tabela;
    }

    public List<String> getErros() {
        return erros;
    }

    public boolean houveErroSemantico() {
        return !erros.isEmpty();
    }
}

