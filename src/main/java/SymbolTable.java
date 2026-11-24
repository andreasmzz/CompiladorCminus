/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author marco
 */

import java.util.*;

public class SymbolTable {

    // Cada elemento representa um escopo
    private Deque<Map<String, VariableSymbol>> escopos;

    public SymbolTable() {
        escopos = new ArrayDeque<>();

        // Cria o escopo global
        entrarEscopo();
    }

    // Cria um novo escopo
    public void entrarEscopo() {
        escopos.push(new LinkedHashMap<String, VariableSymbol>());
    }

    // Remove o escopo atual
    public void sairEscopo() {
        if (!escopos.isEmpty()) {
            escopos.pop();
        }
    }

    // Declara uma variável no escopo atual
    public boolean declarar(VariableSymbol simbolo) {

        Map<String, VariableSymbol> atual = escopos.peek();

        if (atual.containsKey(simbolo.getNome())) {
            return false;
        }

        atual.put(simbolo.getNome(), simbolo);

        return true;
    }

    // Consulta uma variável, considerando os escopos externos
    public VariableSymbol buscar(String nome) {

        for (Map<String, VariableSymbol> escopo : escopos) {

            if (escopo.containsKey(nome)) {
                return escopo.get(nome);
            }
        }

        return null;
    }

    // Verifica se já existe no escopo atual
    public boolean existeNoEscopoAtual(String nome) {

        if (escopos.isEmpty()) {
            return false;
        }

        return escopos.peek().containsKey(nome);
    }

    // Retorna a quantidade de escopos ativos
    public int getNivelEscopo() {
        return escopos.size() - 1;
    }
    
    public List<VariableSymbol> getSimbolos() {
        List<VariableSymbol> resultado = new ArrayList<>();

        for (Map<String, VariableSymbol> escopo : escopos) {
            resultado.addAll(escopo.values());
        }

        return resultado;
    }
}

